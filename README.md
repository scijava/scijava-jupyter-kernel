# scijava-jupyter-kernel

`scijava-jupyter-kernel` aims to be a Jupyter kernel that integrate well with ImageJ. See https://imagej.net/Jupyter for more details.

Under the hood `scijava-jupyter-kernel` uses the [Beaker base kernel](https://github.com/twosigma/beakerx/tree/master/kernel/base).

Languages supported are :

- Jython
- Groovy
- Clojure
- Beanshell
- Java
- Javascript
- JRuby

See here for more details : https://imagej.net/Scripting#Supported_languages

## Installation

When mature enough, `scijava-jupyter-kernel` will be shipped within ImageJ/Fiji and an easy way will be provided to install the kernel specification file to your Python distribution.

## Development

If you want to test or contribute to the kernel, you can do the following :

- Clone this repo and compile it.
- A `scijava-jupyter-kernel-*-SNAPSHOT.jar` package should be created into the `target` folder.
- Create a Jupyter kernel specification file :

```json
{
"argv": [
    "/your/system/jdk1.8.0_66/jre/bin/java",
    "-classpath",
    "/path/to/kernel/scijava-jupyter-kernel-0.1.0-SNAPSHOT.jar:/your/java/jars/files/*",
    "org.scijava.jupyter.kernel.DefaultKernel",
    "-language", "jython",
    "-verbose", "debug",
    "-configFile", "{connection_file}"
],
"display_name": "Scijava Kernel - Jython",
"language": "python"
}
```

- Replace the appropriate fields in the above JSON file.
- Install the kernel using the `jupyter kernelspec install` command.

Now you can try the kernel using `jupyter notebook` or `jupyter lab` but also with this super useful command to do quick test : `jupyter console --kernel="Scijava Kernel - Jython"`.

**Note** : For now Beakerx artifact is not available so you have to compile it yourself and install it in your local Maven repository. Use the following commands :

```bash
git clone https://github.com/twosigma/beakerx.git
cd beakerx/kernel/base/
gradle publishToMavenLocal
```

## License

Under Apache 2.0 license. See [LICENSE](LICENSE).

## Authors

- Hadrien Mary <hadrien.mary@gmail.com>
