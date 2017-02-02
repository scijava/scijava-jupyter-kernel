# scijava-jupyter-kernel

`scijava-jupyter-kernel` is a JSR223 Jupyter kernel implementation in Java. It uses [SciJava Scripting Languages](https://github.com/scijava?utf8=%E2%9C%93&q=scripting&type=&language=) to execute the source code.

Languages currently available are :

- Jython
- Groovy
- Clojure
- Beanshell (not tested)
- Java (not tested)
- Javascript (not tested)
- JRuby (not tested)

## Install

Add the scijava-jupyter-kernel artifact somewhere in your classpath :

```xml
<dependency>
    <groupId>org.scijava</groupId>
    <artifactId>scijava-jupyter-kernel</artifactId>
</dependency>
```

Be sure to also add its dependencies in your classpath :

```xml
<dependency>
    <groupId>org.scijava</groupId>
    <artifactId>scijava-common</artifactId>
</dependency>

<dependency>
    <groupId>commons-cli</groupId>
    <artifactId>commons-cli</artifactId>
</dependency>

<dependency>
    <groupId>org.zeromq</groupId>
    <artifactId>jeromq</artifactId>
</dependency>

<dependency>
    <groupId>org.json</groupId>
    <artifactId>json</artifactId>
</dependency>
```

Then execute the `install-kernel.py` script to install the Jupyter kernel :

```bash
wget -qO- https://raw.githubusercontent.com/hadim/scijava-jupyter-kernel/master/install-kernel.py | python - --java-path=YOUR_JAVA_PATH --classpath=YOUR_CLASSPATH
```

By default `install-kernel.py` will install the Jython Jupyter kernel. You can install another kernel with `--language` :

```bash
wget -qO- https://raw.githubusercontent.com/hadim/scijava-jupyter-kernel/master/install-kernel.py | python - --java-path=YOUR_JAVA_PATH  --classpath=YOUR_CLASSPATH --language groovy
```

A typical [kernelspec file](https://jupyter-client.readthedocs.io/en/latest/kernels.html#kernel-specs) looks like this :

```json
{
"argv": [
    "/your/system/jdk1.8.0_66/jre/bin/java",
    "-classpath",
    "/path/to/kernel/scijava-jupyter-kernel-0.1.0-SNAPSHOT.jar:/your/java/jars/files/*",
    "org.scijava.jupyterkernel.kernel.Session",
    "-k",
    "python",
    "-f",
    "{connection_file}"
],
"display_name": "Jython",
"language": "python"
}
```

## Development

During development it's convenient to use the `scijava-jupyter-kernel` artifact created by Maven in `target/`.

To use it you can just add the `--dev` :

```bash
python install-kernel.py --java-path=YOUR_JAVA_PATH --classpath=YOUR_CLASSPATH --dev
```

## License

Under Apache 2.0 license. See [LICENSE](LICENSE).

## Authors

- Hadrien Mary <hadrien.mary@gmail.com>

`scijava-jupyter-kernel` is a friendly fork of https://github.com/fiber-space/jupyter-kernel-jsr223.
