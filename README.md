# scijava-jupyter-kernel
[![Travis branch](https://img.shields.io/travis/hadim/scijava-jupyter-kernel/master.svg?style=flat-square)](https://travis-ci.org/hadim/scijava-jupyter-kernel)
[![License](https://img.shields.io/github/license/hadim/scijava-jupyter-kernel.svg?style=flat-square)](https://github.com/hadim/scijava-jupyter-kernel/blob/master/LICENSE)
[![Anaconda-Server Badge](https://anaconda.org/conda-forge/scijava-jupyter-kernel/badges/version.svg)](https://anaconda.org/conda-forge/scijava-jupyter-kernel)
[![Anaconda-Server Badge](https://anaconda.org/conda-forge/scijava-jupyter-kernel/badges/downloads.svg)](https://anaconda.org/conda-forge/scijava-jupyter-kernel)

---

`scijava-jupyter-kernel` aims to be a [Jupyter](http://jupyter.org/) kernel that integrate well with [ImageJ](http://imagej.net). See [here](https://imagej.net/Scijava_Jupyter_Kernel) for more details.

*Under the hood `scijava-jupyter-kernel` uses the [Beaker base kernel](https://github.com/twosigma/beakerx/tree/master/kernel/base).*

Scripting languages supported are :

- Groovy
- Python
- BeanShell
- Clojure
- Java
- JavaScript
- R
- Ruby
- Scala

See here for more details : https://imagej.net/Scripting#Supported_languages

## Installation - Standalone

- Install [Anaconda](https://www.continuum.io/downloads)
- Install `scijava-jupyter-kernel` with :

```bash
# Add the conda-forge channel
conda config --add channels conda-forge

# Create an isolated environment
conda create --name scijava openjdk

# Activate the scijava environment
source install scijava

# Install the kernel
conda install scijava-jupyter-kernel
```

- Usage :

```bash
# Check the kernels have been installed
jupyter kernelspec list

# Launch your favorite Jupyter client
jupyter notebook

# or
jupyter lab
```

*Note : the kernel does not install properly from the root Conda environment. PRs are welcome.*

## Installation - With Fiji integration

- Clone and compile `scijava-jupyter-kernel` :

```bash
git clone https://github.com/hadim/scijava-jupyter-kernel.git
cd scijava-jupyter-kernel
mvn -Dimagej.app.directory="PATH-TO-YOUR-IMAGEJ-REPO" install
```

- Start Fiji and launch `Analyze > Jupyter Kernel > Install Scijava Kernel`.
- Set the path to your Python binary.
- Choose a language (for example `jython` or `groovy`) or you can choose to install all the available languages.
- Choose a log level.

- Check the kernels have been installed with : `jupyter kernelspec list`.
- Launch `jupyter notebook` or `jupyter lab` and **select the kernel you want in the kernel list**.

## Screenshot

![Scijava Jupyter Kernel Installation](teaser.gif)

## About Python

We strongly suggest the use of the [Anaconda Python distribution](https://www.continuum.io/downloads) + the use of the [conda-forge channel](https://conda-forge.github.io/). That way, your Python and all your libs will be kept synced and updated.

## License

Under Apache 2.0 license. See [LICENSE](LICENSE).

## Authors

- Hadrien Mary <hadrien.mary@gmail.com>
