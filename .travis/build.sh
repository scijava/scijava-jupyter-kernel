#!/bin/sh
set -e

dir="$(dirname "$0")"
test "$TRAVIS_PULL_REQUEST" = false \
     -a "$TRAVIS_BRANCH" = master

# Build and install Beakerx base kernel (no artifact available for now)
git clone --depth 1 https://github.com/twosigma/beakerx.git
cd beakerx/kernel/base
gradle publishToMavenLocal
cd ../../../

mvn install
