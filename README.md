# scijava-jupyter-kernel
[![Travis branch](https://img.shields.io/travis/scijava/scijava-jupyter-kernel/master.svg?style=flat-square)](https://travis-ci.org/scijava/scijava-jupyter-kernel)
[![License](https://img.shields.io/github/license/scijava/scijava-jupyter-kernel.svg?style=flat-square)](https://github.com/scijava/scijava-jupyter-kernel/blob/master/LICENSE)
[![Anaconda-Server Badge](https://anaconda.org/conda-forge/scijava-jupyter-kernel/badges/version.svg)](https://anaconda.org/conda-forge/scijava-jupyter-kernel)
[![Anaconda-Server Badge](https://anaconda.org/conda-forge/scijava-jupyter-kernel/badges/downloads.svg)](https://anaconda.org/conda-forge/scijava-jupyter-kernel)
[![Binder](https://mybinder.org/badge.svg)](https://mybinder.org/v2/gh/scijava/scijava-jupyter-kernel/master)
---

`scijava-jupyter-kernel` aims to be a polyglot [Jupyter](http://jupyter.org/) kernel. It uses the [Scijava scripting languages](https://imagej.net/Scripting#Supported_languages) to execute the code in Jupyter client and it's possible to use different languages in the same notebook.

Some of the supported languages are Groovy (default), Python, Beanshell, Clojure, Java, Javascript, Ruby and Scala.

The kernel has been originally created to work with ImageJ. See [here](https://imagej.net/Scijava_Jupyter_Kernel) for more details.

*Under the hood `scijava-jupyter-kernel` uses the [Beaker base kernel](https://github.com/twosigma/beakerx/tree/master/kernel/base).*

## Binder Usage

- [ImageJ notebook](https://mybinder.org/v2/gh/4QuantOSS/scijava-jupyter-kernel/master?filepath=notebooks%2FImageJ.ipynb)
- [JupyterLab](https://mybinder.org/v2/gh/4QuantOSS/scijava-jupyter-kernel/master?urlpath=lab)

## Documentation

A documentation is available as a series of notebooks [here](./notebooks/Welcome.ipynb).

## Installation

- Install [Anaconda](https://www.continuum.io/downloads)
- Install `scijava-jupyter-kernel` with :

```bash
# Add the conda-forge channel
conda config --add channels conda-forge

# Create an isolated environment called `java_env` and install the kernel
conda create --name java_env scijava-jupyter-kernel
```

- Usage :

```bash
# Activate the `java_env` environment
source activate java_env

# Check the kernel has been installed
jupyter kernelspec list

# Launch your favorite Jupyter client
jupyter notebook

# or
jupyter lab
```

*Note : It is strongly suggested to install the kernel in an isolated Conda environment (not in the root environment).*

## Development

- [CI with Travis](https://travis-ci.org/scijava/scijava-jupyter-kernel) makes sure the project builds without errors for each new commit.
- A [test notebook](./notebooks/Test.ipynb) is executed during CI with [nbconvert](http://nbconvert.readthedocs.io/en/latest/execute_api.html).
- A [Conda package](https://github.com/conda-forge/scijava-jupyter-kernel-feedstock) is built for each new release.

## License

Under Apache 2.0 license. See [LICENSE](LICENSE).

## Authors

- Hadrien Mary <hadrien.mary@gmail.com>
