
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

Todos:
- checkpoints
- life loss
+ default structures on map
+ missile
+ show mobs' health on top of their head (=> in action bar with ActionHealth plugin)
+ put beacon at bottom of piece => allow chests to be put in heights
- mobs:
  - without helmet + fire resistance, to give runners fire on melee hit
  - no natural mob spawn
  - random #mobs / piece
  - customized health
  - mobs do not move until their piece is active ()
  - stop natural despawn?
- have only one runnable per class, not per player runner!

Ideas:
- enchantments
  - damage reduction when low
  - damage dodge chance when sprinting
  - speed
  - jump boost
  - strength
  - walk on water/lava
  - walk in void (close to platforms)
(- builder can move around, give him several speed boots)
- builder supply:
  - reroll pièce courrante
  - onde de choc knockback mobs
  - météorite
  - spawner un dongeon avec loot pété
- quêtes
  - sauver des villageois