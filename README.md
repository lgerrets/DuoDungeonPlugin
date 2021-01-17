
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
- "can't send chat message" occurs if the entity of the player was accidentally removed with a "entity.remove()"

## Bugs / Todos

Bugs:
- as runner, I can double click on gold ingot in my inventory to sneak pick gold in chest
- parfois, une nouvelle piece ne spawn pas => loggé

Todos:
- life loss
- mobs:
  - without helmet + fire resistance, to give runners fire on melee hit
  - no natural mob spawn
  - mobs do not move until their piece is active ()
  - stop natural despawn?
- have only one runnable per class, not per player runner!
- araignées inoffensive
- overlay grille
- + d'armes, loot trop bien d'un coup
- boots levitation use la durabilité
- shop de départ / augmenter la rareté sur les premiers checkpoints (slay the spyre style)

- cp dans le mauvais sens
- clear runner inv
- descendre buider
- on mob creation mob.setRemoveWhenFarAway(false);

+ beacon blanc pour les communs
+ disparaitre beacons coffres ouverts ou carrément disparaitre le coffre
+ empecher la piece/bombe de sortir du champ de vision du builder
+ despawn pieces après 10 pieces
+ accéler le countdown de piece despawn (5 ... 4 ... 3 .. 2 .. 1 . 0)
+ mob interdits dans les cp
+ fix clear all entities in dungeon area but player

+ augmenter la hauteur max pour cp
+ marchands aux checkpoints
+ spawn mobs and merchants at center
+ play sound on piece placed
+ son quand la piece bouge
+ config params
+ sert hunger and saturation (huge for builder)
+ builder invisible

+ checkpoint obstacle des deux côtés
+ empecher de poser dans un coffre
+ 3 items dans coffres
+ checkpoint collision peacefuls
+ cp ne fait pas 8x3
+ désactiver lianes poussent / grass propage
+ fleches ??
+ lifesteal 5 ?!
+ log dd start clear area
+ knockback mobs

+ premier niveau + facile
+ checkpoit y +3
+ augmenter timers
+ builder speed 2
+ bombes spawn pos??
+ réduire piece distance +10
+ poser à côté d'un cp atteint
+ plafond barriers pour builder
+ tiers
+ pas de skeleton au début

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
- bouffe ne regen pas, mais important pour pouvoir courrir
- boss fight every eg. 5 checkpoints
  - builder gameplay changes: place walls
  - minions
  - builder aims and activates TNTs placed by runner
- durabilité des equipements comme facteur de gameplay


