import argparse


def main(java_path):
    print(java_path)


if __name__ == '__main__':

    parser = argparse.ArgumentParser(description="Find Java binaries and needed JAR files for "
                                                 "jupyter-kernel-jsr223.")

    parser.add_argument('--java-path', type=str, required=True,
                        help="Path to your Java installation where Java binary and needed JAR "
                             "files will be searched.")

    args = parser.parse_args()
    main(java_path=args.java_path)
