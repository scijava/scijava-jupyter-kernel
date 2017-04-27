# This script is used to test notebooks in `./notebooks/`
# when commits are pushed on the remote repo.
# CI service used is Travis.

import argparse
import os

import nbformat
from nbconvert.preprocessors import ExecutePreprocessor


def execute_notebook(notebook_path, kernel_name=None):

    print("* Start notebook execution.")
    print("---------------------------")

    nb = nbformat.read(notebook_path, nbformat.current_nbformat)
    ep = ExecutePreprocessor(timeout=600, kernel_name=kernel_name)

    try:
        ep.preprocess(nb, {'metadata': {'path': os.path.dirname(notebook_path)}})
    except CellExecutionError:
        msg = 'Error executing the notebook "{}".\n\n'.format(notebook_path)
        print(msg)
        raise

    print("-----------------------------")
    print("* Notebook execution is done.")


if __name__ == "__main__":

    parser = argparse.ArgumentParser()

    parser.add_argument("notebook_path", help="Path to the notebook file to execute.")
    parser.add_argument("-k", "--kernel_name", help="Path to the notebook file to execute.")

    args = parser.parse_args()

    success = execute_notebook(args.notebook_path, kernel_name=args.kernel_name)
