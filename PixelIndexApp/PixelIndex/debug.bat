chcp 65001
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar .\target\PixelIndex-1.0-SNAPSHOT.jar