#!/usr/bin/sh
sjjk=~/anaconda3/envs/java_env/opt/scijava-jupyter-kernel

mvn clean package
mvn dependency:copy-dependencies
rm $sjjk/*.jar
cp target/dependency/*.jar $sjjk/
cp target/*.jar $sjjk/

