import argparse
import sys

from contextlib import contextmanager

from jupyter_client import KernelManager
from jupyter_client import run_kernel

import nbformat


@contextmanager
def run_kernel(kernel_name=None):
    
    if(kernel_name):
        km = KernelManager(kernel_name=kernel_name)
    else:
        km = KernelManager()
        
    print("* Start the kernel")
    km.start_kernel()

    kc = km.client(klass='jupyter_client.blocking.BlockingKernelClient')
    
    print("* Start the channels")
    kc.start_channels()

    try:
        kc.wait_for_ready(timeout=60)
    except RuntimeError:
        kc.stop_channels()
        km.shutdown_kernel()
        raise
        
    print("* Kernel is ready")
    try:
        yield kc
    finally:
        kc.stop_channels()
        km.shutdown_kernel(now=True)


def execute_notebook(notebook_path, kernel_name=None):

    nb = nbformat.read(notebook_path, nbformat.current_nbformat)

    with run_kernel(kernel_name=kernel_name) as kc:

        print("* Start notebook execution.")
        print("---------------------------")
        
        error = 0
        success = 0
        
        # Execute code cells one by one
        for cell in nb["cells"]:
            if cell["cell_type"] == "code":
                
                code = cell["source"]
                
                msg_id = kc.execute(code, silent=False, store_history=True, user_expressions=None,
                                    allow_stdin=None, stop_on_error=True)
                
                reply = kc.get_shell_msg()
                status = reply['content']['status']
                
                if status == 'error':
                    print('\n'.join(reply['content']['traceback']))
                    error += 1
                else:
                    success += 1

                outs = list()
                while True:
                    try:
                        msg = kc.get_iopub_msg(timeout=1)
                        if msg['msg_type'] == 'status':
                            if msg['content']['execution_state'] == 'idle':
                                break
                    except Empty:
                        # execution state should return to idle
                        # before the queue becomes empty,
                        # if it doesn't, something bad has happened
                        raise

                    content = msg['content']
                    msg_type = msg['msg_type']
                    
                    if msg_type == 'stream':
                        print(content['text'], end='')

    print("-----------------------------")
    print("* Notebook execution is done.")
    print("{} cells executed. {} with success and {} with error.".format(error + success, success, error))

    return error == 0


if __name__ == "__main__":

    parser = argparse.ArgumentParser()

    parser.add_argument("notebook_path", help="Path to the notebook file to execute.")
    parser.add_argument("-k", "--kernel_name", help="Path to the notebook file to execute.")

    args = parser.parse_args()

    success = execute_notebook(args.notebook_path, kernel_name=args.kernel_name)

    if success:
        sys.exit(0)
    else:
        sys.exit(1)