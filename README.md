
## Run server

C:\Users\Lucas\.p2\pool\plugins\org.eclipse.justj.openjdk.hotspot.jre.full.win32.x86_64_14.0.2.v20200815-0932\jre\bin\java.exe -Xmx1024M -Xmx1024M -jar .\spigot-1.16.3.jar

Debug mode: (does snot work)

C:\Users\Lucas\.p2\pool\plugins\org.eclipse.justj.openjdk.hotspot.jre.full.win32.x86_64_14.0.2.v20200815-0932\jre\bin\java.exe -Xmx1024M -Xmx1024M -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 -jar .\spigot-1.16.3.jar


## Eclipse config

- JRE path:

Window > preferences > java > installed JRE

- Add spigot.jar

Project > properties > java build path > libraries > add external JARs

- Add a plugin API

Project > properties > java build path > libraries > add external JARs

then

Project > properties > java build path > module dependencies > all modules > scroll down > copy

then 

module-info.java > "requires <paste>"
