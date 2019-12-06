# All To One Printer

Prints the contents of all files by the specified mask in the specified directory and subdirectories to a new destination file.

```
usage: java -jar "alltooneprinter.jar" [args...]
 -d,--directory <arg>   scanning directory
 -m,--mask <arg>        file mask, glob pattern (see for details:
                        https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob)
 -o,--output <arg>      output path with filename
```