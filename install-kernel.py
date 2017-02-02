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
                               "org.scijava.jupyterkernel.kernel.Session",
                               "-k", "{language}",
                               "-f", "{connection_file}"],
                     "display_name": "{name}",
                     "language": "{language}"
                    }


def find_file(name, filenames, directory):
    """Return the first ocurence of a filename list in a folder tree. Filename can be regex expression.
    """

    if type(filenames) == str:
        filenames = [filenames]

    file_path = None

    for root, sub_dir, files in os.walk(directory):
        for file in files:
            for candidate in filenames:
                if re.match(candidate, file):
                    file_path = os.path.join(root, file)

    if not file_path:
        raise Exception("Unable to find a {} in the specified Java path : {}".format(name, directory))
    else:
        print("\t- {} found : {}".format(name, file_path))

    return file_path


def install_kernel_spec(kernel_json, user=True, prefix=None):
    """Install a new IPython kernel from a temporary directory
    """
    with TemporaryDirectory() as td:
        os.chmod(td, 0o755)
        with open(os.path.join(td, 'kernel.json'), 'w') as f:
            json.dump(kernel_json, f, sort_keys=True)


        kernel_manager = KernelSpecManager()
        destination = kernel_manager.install_kernel_spec(td, kernel_json["display_name"],
                                                         user=True, replace=True, prefix=prefix)

        print('The kernel spec has been installed to {}'.format(destination))


def main(java_path, dev=False, additional_classpath="", name="Jython", language="python"):
    """
    """

    # Find Java bin and JAR files
    print("Find Java binary and JAR files.")
    java_bin_path = find_file("Java binary", "^java$", java_path)

    jars = []
    if dev:
         jars.append(find_file("scijava-jupyter-kernel", "^scijava-jupyter-kernel.*\-SNAPSHOT.jar$",
                              os.path.join(os.path.dirname(os.path.realpath(__file__)), "target")))

    # Define the new kernel spec
    kernel_json = kernel_json_model.copy()
    kernel_json['argv'][0] = kernel_json['argv'][0].format(java_bin_path=java_bin_path)

    classpath = ":".join(jars)
    classpath += ":{}".format(additional_classpath)
    kernel_json['argv'][2] = kernel_json['argv'][2].format(classpath=classpath)

    kernel_json['argv'][5] = kernel_json['argv'][5].format(language=language)

    kernel_json['display_name'] = kernel_json['display_name'].format(name=name)
    kernel_json['language'] = kernel_json['language'].format(language=language)

    print("The new kernel spec has been defined : \n{}".format(json.dumps(kernel_json,
                                                                         sort_keys=True,
                                                                         indent=4)))

    install_kernel_spec(kernel_json)


if __name__ == '__main__':


    parser = argparse.ArgumentParser(description="Java Jupyter Kernel Installer.")

    parser.add_argument('--java-path', type=str, required=True,
                        help="Path to your Java installation where Java binary will be searched.")

    parser.add_argument('--dev', action='store_true', required=False, default=False,
                        help="Use the `jupyter-kernel-jsr223` artifact located in the `target/` directory.")

    parser.add_argument('--classpath', type=str, required=False, default="",
                        help="Additional classpath.")

    parser.add_argument('--name', type=str, required=False, default="Jython",
                        help="By default the name of the kernel will be Jython. You can rename it.")

    parser.add_argument('--language', type=str, required=False, default="python",
                        help="Choose between : ['python', 'groovy', 'clojure']")

    args = parser.parse_args()
    main(java_path=args.java_path, dev=args.dev, additional_classpath=args.classpath,
         name=args.name, language=args.language)
