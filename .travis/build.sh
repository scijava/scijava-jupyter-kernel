#!/bin/bash
set -ex


# Define some variables

export USER="Scijava-jupyter-kernel"
export UPDATE_SITE="Scijava-jupyter-kernel"

export IJ_PATH="$HOME/Fiji.app"
export URL="http://sites.imagej.net/$UPDATE_SITE/"
export IJ_LAUNCHER="$IJ_PATH/ImageJ-linux64"
export PATH="$IJ_PATH:$PATH"


# Install Fiji

mkdir -p $IJ_PATH/
cd $HOME/
wget --no-check-certificate https://downloads.imagej.net/fiji/latest/fiji-linux64.zip
unzip fiji-linux64.zip


# Install the package

cd $TRAVIS_BUILD_DIR/
mvn install -Pimagej --settings ".travis/settings.xml"


# Deploy the package

# $IJ_LAUNCHER --update edit-update-site $UPDATE_SITE $URL "webdav:$USER:$WIKI_UPLOAD_PASS" .
# $IJ_LAUNCHER --update upload --update-site $UPDATE_SITE --force-shadow jars/beaker-kernel-base.jar
# $IJ_LAUNCHER --update upload --update-site $UPDATE_SITE --force-shadow jars/scijava-jupyter-kernel.jar
