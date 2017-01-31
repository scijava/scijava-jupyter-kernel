import argparse
import os
import json
import re

try:
    from jupyter_client.kernelspec import KernelSpecManager
except:
    raise ImportError("Jupyter is not installed. Please install it.")

try:
    from IPython.utils.tempdir import TemporaryDirectory
except:
    raise ImportError("IPython is not installed. Please install it.")


kernel_json_model = {"argv": ["{java_bin_path}",
                              "-classpath", "{classpath}",
                               "org.jupyterkernel.kernel.Session",
                               "-k", "python",
                               "-f", "{connection_file}"],
                     "display_name": "Jython",
                     "language": "python"
                    }


def find_file(filenames, directory):
    """Return the first ocurence of a filename list in a folder tree. Filename can be regex expression.
    """

    if type(filenames) == str:
        filenames = [filenames]

    for root, sub_dir, files in os.walk(directory):
        for file in files:
            for candidate in filenames:
                if re.match(candidate, file):
                    return os.path.join(root, file)

    return None


def find_jar(name, filenames, directory):
    """
    """
    java_bin_path = find_file(filenames, directory)
    if not java_bin_path:
        raise Exception("Unable to find a {} in the specified Java path : {}".format(name, directory))
    else:
        print("\t- {} found : {}".format(name, java_bin_path))

    return java_bin_path


def install_kernel_spec(kernel_json, user=True, prefix=None):
    """Install a new IPython kernel from a temporary directory
    """
    with TemporaryDirectory() as td:
        os.chmod(td, 0o755)
        with open(os.path.join(td, 'kernel.json'), 'w') as f:
            json.dump(kernel_json, f, sort_keys=True)


        kernel_manager = KernelSpecManager()
        destination = kernel_manager.install_kernel_spec(td, kernel_json_model["display_name"],
                                                         user=True, replace=True, prefix=prefix)

        print('The kernel spec has been installed to {}'.format(destination))


def main(java_path):
    """
    """

    # Find Java bin and JAR files
    print("Find Java binary and JAR files.")
    java_bin_path = find_jar("Java binary", "^java$", java_path)
    jars = []
    jars.append(find_jar("jupyter-kernel-jsr223", "^jupyter-kernel-jsr223.*\.jar$", java_path))
    jars.append(find_jar("Jython", "^jython.*\.jar$", java_path))
    jars.append(find_jar("jeromq", "^jeromq.*\.jar$", java_path))
    jars.append(find_jar("JSON", "^json.*\.jar$", java_path))
    jars.append(find_jar("Commons CLI", "^commons-cli-.*\.jar$", java_path))

    # Define the new kernel spec
    kernel_json = kernel_json_model.copy()
    kernel_json['argv'][0] = kernel_json['argv'][0].format(java_bin_path=java_bin_path)
    kernel_json['argv'][2] = kernel_json['argv'][2].format(classpath=":".join(jars))

    print("The new kernel spec has been defined : \n{}".format(json.dumps(kernel_json,
                                                                         sort_keys=True,
                                                                         indent=4)))

    install_kernel_spec(kernel_json)


if __name__ == '__main__':

    parser = argparse.ArgumentParser(description="Find Java binaries and needed JAR files for "
                                                 "jupyter-kernel-jsr223.")

    parser.add_argument('--java-path', type=str, required=True,
                        help="Path to your Java installation where Java binary and needed JAR "
                             "files will be searched.")

    args = parser.parse_args()
    main(java_path=args.java_path)
