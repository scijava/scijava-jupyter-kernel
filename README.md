# jupyter-kernel-jsr223

*Note : this is a work in progress that is currently not working. Any help is welcome !*

jupyter-kernel-jsr223 is a JSR223 Jupyter kernel implementation in Java. It's a friendly fork of https://github.com/fiber-space/jupyter-kernel-jsr223.

## Install

Add the jupyter-kernel-jsr223 artifact to your Java installation :

```xml
<dependency>
    <groupId>org.scijava</groupId>
    <artifactId>jupyter-kernel-jsr223</artifactId>
</dependency>
```

Then execute the `install-kernel.py` script to install the Jython kernel to your Python installation :

```bash
wget -qO- https://raw.githubusercontent.com/hadim/jupyter-kernel-jsr223/master/install-kernel.py | python - --java-path=YOUR_JAVA_PATH
```

**Note** : The `install-kernel.py` script will only support Python 3. *The reason is that Python 3 has been released 8 years ago now and it's time to move on.*

## TODO

- Implement Jupyter messaging protocol v5.0
- Encrypt messaging

## License

Under Apache 2.0 license. See [LICENSE](LICENSE).

## Authors

- Hadrien Mary <hadrien.mary@gmail.com>
