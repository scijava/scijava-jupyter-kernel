#!/bin/bash
set -ex


# Install python

cd $HOME/
wget https://repo.continuum.io/miniconda/Miniconda3-latest-Linux-x86_64.sh -O miniconda.sh > /dev/null 2>&1
bash miniconda.sh -b -p "$HOME/conda" > /dev/null 2>&1
export PATH="$HOME/conda/bin:$PATH"

conda config --set always_yes yes --set changeps1 no
conda config --add channels conda-forge
conda update --yes conda > /dev/null 2>&1
conda update --yes --all > /dev/null 2>&1
conda info -a

conda create --yes -n scijava python jupyter nbconvert > /dev/null 2>&1

source activate scijava


# Install a kernel with Java in the Fiji folder

export IJ_PATH="$HOME/Fiji.app"
export IJ_LAUNCHER="$IJ_PATH/ImageJ-linux64"

JAVA_COMMAND_CLASS="org.scijava.jupyter.commands.InstallScijavaKernel"
$IJ_LAUNCHER --ij2 --headless --run $JAVA_COMMAND_CLASS "logLevel=\"info\",pythonBinaryPath=\"$(which python)\""

jupyter kernelspec list

# Now run the test notebook
#jupyter nbconvert --execute --ExecutePreprocessor.timeout="60" --ExecutePreprocessor.kernel_name="scijava" "$TRAVIS_BUILD_DIR/notebooks/Test.ipynb"


