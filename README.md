# jupyter-kernel-jsr223

*Note : this is a work in progress that is currently not working. Any help is welcome !*

jupyter-kernel-jsr223 is a JSR223 Jupyter kernel implementation in Java. It's a friendly fork of https://github.com/fiber-space/jupyter-kernel-jsr223.


## Install

Add the jupyter-kernel-jsr223 artifact somewhere in your classpath :

```xml
<dependency>
    <groupId>org.scijava</groupId>
    <artifactId>jupyter-kernel-jsr223</artifactId>
</dependency>
```

Be sure to also its dependencies in your classpath : 

```xml
<dependency>
    <groupId>commons-cli</groupId>
    <artifactId>commons-cli</artifactId>
</dependency>

<dependency>
    <groupId>org.scijava</groupId>
    <artifactId>jython-shaded</artifactId>
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

Then execute the `install-kernel.py` script to install the Jython kernel to your Python installation :

```bash
wget -qO- https://raw.githubusercontent.com/hadim/jupyter-kernel-jsr223/master/install-kernel.py | python - --java-path=YOUR_JAVA_PATH
```

**Note** : The `install-kernel.py` script will only support Python 3. *The reason is that Python 3 has been released 8 years ago now and it's time to move on.*


## Advanced Installation

### Development

During development it's convenient to use the `jupyter-kernel-jsr223` artifact created by Maven in `target/`.

To use it you can just add the `--dev` :

```bash
python install-kernel.py --java-path=YOUR_JAVA_PATH --dev
```

### Classpath

You can add JAVA classpaths to the Java Machine running the kernel with `--classpath`. For example :

```bash
python install-kernel.py --java-path=YOUR_JAVA_PATH --classpath="YOUR_CLASS_PATH"
```

### Test

When doing development I often use this command line to quickly test the kernel :

```bash
jupyter console --kernel jython-dev
```

## License

Under Apache 2.0 license. See [LICENSE](LICENSE).

## Authors

- Hadrien Mary <hadrien.mary@gmail.com>
