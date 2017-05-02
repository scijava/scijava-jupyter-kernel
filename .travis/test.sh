#!/bin/bash
set -ex


# Install python

cd $HOME/
wget https://repo.continuum.io/miniconda/Miniconda3-latest-Linux-x86_64.sh -O miniconda.sh
bash miniconda.sh -b -p "$HOME/conda"
export PATH="$HOME/conda/bin:$PATH"

conda config --set always_yes yes --set changeps1 no
conda config --add channels conda-forge
conda update --yes conda
conda update --yes --all
conda info -a

conda create --yes -n scijava python jupyter nbconvert

source activate scijava


# Install a kernel with Java in the Fiji folder

export IJ_PATH="$HOME/Fiji.app"
export IJ_LAUNCHER="$IJ_PATH/ImageJ-linux64"

JAVA_COMMAND_CLASS="org.scijava.jupyter.commands.InstallScijavaKernel"
$IJ_LAUNCHER --ij2 --headless --run $JAVA_COMMAND_CLASS "logLevel=\"info\",pythonBinaryPath=\"$(which python)\",installAllKernels=true"

jupyter kernelspec list


# Now run some notebooks

"jupyter nbconvert --execute --ExecutePreprocessor.timeout=60 --ExecutePreprocessor.kernel_name=scijava-python $TRAVIS_BUILD_DIR/notebooks/Welcome.ipynb"
"jupyter nbconvert --execute --ExecutePreprocessor.timeout=60 --ExecutePreprocessor.kernel_name=scijava-groovy $TRAVIS_BUILD_DIR/notebooks/General.ipynb"
"jupyter nbconvert --execute --ExecutePreprocessor.timeout=60 --ExecutePreprocessor.kernel_name=scijava-python $TRAVIS_BUILD_DIR/notebooks/Rich Output.ipynb"
"jupyter nbconvert --execute --ExecutePreprocessor.timeout=60 --ExecutePreprocessor.kernel_name=scijava-python $TRAVIS_BUILD_DIR/notebooks/On-The-Fly Grabbing.ipynb"
"jupyter nbconvert --execute --ExecutePreprocessor.timeout=60 --ExecutePreprocessor.kernel_name=scijava-groovy $TRAVIS_BUILD_DIR/notebooks/ImageJ.ipynb"
"jupyter nbconvert --execute --ExecutePreprocessor.timeout=60 --ExecutePreprocessor.kernel_name=scijava-groovy $TRAVIS_BUILD_DIR/notebooks/Scijava.ipynb"
"jupyter nbconvert --execute --ExecutePreprocessor.timeout=60 --ExecutePreprocessor.kernel_name=scijava-python $TRAVIS_BUILD_DIR/notebooks/languages/Python.ipynb"
"jupyter nbconvert --execute --ExecutePreprocessor.timeout=60 --ExecutePreprocessor.kernel_name=scijava-groovy $TRAVIS_BUILD_DIR/notebooks/languages/Groovy.ipynb"
"jupyter nbconvert --execute --ExecutePreprocessor.timeout=60 --ExecutePreprocessor.kernel_name=scijava-scala $TRAVIS_BUILD_DIR/notebooks/languages/Scala.ipynb"
"jupyter nbconvert --execute --ExecutePreprocessor.timeout=60 --ExecutePreprocessor.kernel_name=scijava-clojure $TRAVIS_BUILD_DIR/notebooks/languages/Clojure.ipynb"
"jupyter nbconvert --execute --ExecutePreprocessor.timeout=60 --ExecutePreprocessor.kernel_name=scijava-beanshell $TRAVIS_BUILD_DIR/notebooks/languages/Beanshell.ipynb"
"jupyter nbconvert --execute --ExecutePreprocessor.timeout=60 --ExecutePreprocessor.kernel_name=scijava-javascript $TRAVIS_BUILD_DIR/notebooks/languages/Javascript.ipynb"

