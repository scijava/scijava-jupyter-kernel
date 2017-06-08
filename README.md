# scijava-jupyter-kernel
[![Travis branch](https://img.shields.io/travis/scijava/scijava-jupyter-kernel/master.svg?style=flat-square)](https://travis-ci.org/scijava/scijava-jupyter-kernel)
[![License](https://img.shields.io/github/license/scijava/scijava-jupyter-kernel.svg?style=flat-square)](https://github.com/scijava/scijava-jupyter-kernel/blob/master/LICENSE)
[![Anaconda-Server Badge](https://anaconda.org/conda-forge/scijava-jupyter-kernel/badges/version.svg)](https://anaconda.org/conda-forge/scijava-jupyter-kernel)
[![Anaconda-Server Badge](https://anaconda.org/conda-forge/scijava-jupyter-kernel/badges/downloads.svg)](https://anaconda.org/conda-forge/scijava-jupyter-kernel)

---

`scijava-jupyter-kernel` aims to be a polyglot [Jupyter](http://jupyter.org/) kernel. It uses the [Scijava scripting languages](https://imagej.net/Scripting#Supported_languages) to execute the code in Jupyter client and it's possible to use different languages in the same notebook.

Some of the supported languages are Groovy (default), Python, Beanshell, Clojure, Java, Javascript, Ruby and Scala.

The kernel has been originally created to work with ImageJ. See [here](https://imagej.net/Scijava_Jupyter_Kernel) for more details.

*Under the hood `scijava-jupyter-kernel` uses the [Beaker base kernel](https://github.com/twosigma/beakerx/tree/master/kernel/base).*

## Documentation

A documentation is available as a series of notebooks [here](./notebooks/Welcome.ipynb).

## Installation - Standalone

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

*Note : It is suggested to install the kernel in an isolated Conda environment (not in the root environment).*

## Installation - With Fiji integration

- [Download](https://imagej.net/Fiji/Downloads) the last Fiji release.
- Add the *Scijava Jupyter Kernel* update site in Fiji:
    - Click [Help ▶ Update...](https://imagej.net/Update_Sites).
    - Click `Manage update sites`.
    - Select the *Scijava Jupyter Kernel* checkbox (see also [List of update sites](https://imagej.net/List_of_update_sites)).
    - Click `Apply changes` and restart Fiji.

- Launch `Analyze > Jupyter Kernel > Install Scijava Kernel`.
- Set the path to your Python binary.
- Choose a log level.

- Check the kernel has been installed with : `jupyter kernelspec list`.
- Launch `jupyter notebook` or `jupyter lab` and **select the kernel you want in the kernel list**.

![Scijava Jupyter Kernel Installation](teaser.gif)

## Development

- [CI with Travis](https://travis-ci.org/scijava/scijava-jupyter-kernel) makes sure the project builds without errors for each new commit.
- A [test notebook](./notebooks/Test.ipynb) is executed during CI with [nbconvert](http://nbconvert.readthedocs.io/en/latest/execute_api.html).
- A [Conda package](https://github.com/conda-forge/scijava-jupyter-kernel-feedstock) is built for each new release.

## License

Under Apache 2.0 license. See [LICENSE](LICENSE).

## Authors

- Hadrien Mary <hadrien.mary@gmail.com>
