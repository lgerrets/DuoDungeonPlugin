
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

## Debug

- NullPointerException on a static variable: could have meant interdependecy between class static variables, but actually I forgot to initialize a ```EnumMap<...> x **= new EnumMap<>(...)**```

## Bugs / Todos

Bugs:
- call SetTeam in Player constructor when Player joins server
- adventure mode causes builder left click to trigger nothing when clicking on barrier block
- builder right click triggers place
- deal with player exit (currently exceptions occur)

Todos:
+ piece rotation
- builder slow effect / give him several speed boots
- checkpoints
- life loss
- default structures on map
- mobs:
  - without helmet + fire resistance, to give runners fire on melee hit
- fix builder actions

Ideas:
- enchantments
  + lifesteal when low
  - damage reduction when low
  + gatherer (find more supplies when offered in chests)
  - speed
  - jump boost
  - strength
