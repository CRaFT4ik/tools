# All To One Printer

CLI-утилита, которая собирает содержимое всех файлов по заданной маске в указанной директории (включая подпапки) в один выходной файл.

```
java -jar "alltooneprinter.jar" [args...]
 -d,--directory <arg>   директория для сканирования
 -m,--mask <arg>        маска файлов, glob-паттерн
 -o,--output <arg>      путь к выходному файлу
```

Java 8+. Сборка через Gradle (`./gradlew shadowJar`).
