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

conda create --yes -n scijava python jupyter runipy

source activate scijava


# Install a kernel with Java in the Fiji folder

export IJ_PATH="$HOME/Fiji.app"
export IJ_LAUNCHER="$IJ_PATH/ImageJ-linux64"

JAVA_COMMAND_CLASS="org.scijava.jupyter.commands.InstallScijavaKernel"

LANGUAGE="jython"
$IJ_LAUNCHER --ij2 --headless --run $JAVA_COMMAND_CLASS "logLevel=\"info\",scriptLanguage=\"$LANGUAGE\",pythonBinaryPath=\"$(which python)\""

LANGUAGE="groovy"
$IJ_LAUNCHER --ij2 --headless --run $JAVA_COMMAND_CLASS "logLevel=\"info\",scriptLanguage=\"$LANGUAGE\",pythonBinaryPath=\"$(which python)\""

LANGUAGE="clojure"
$IJ_LAUNCHER --ij2 --headless --run $JAVA_COMMAND_CLASS "logLevel=\"info\",scriptLanguage=\"$LANGUAGE\",pythonBinaryPath=\"$(which python)\""

jupyter kernelspec list
