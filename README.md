HubKick by LaxWasHere
=====================

A bukkit plugin for bungeecord. This will automatically send the player to the lobby whenever s/he get kicked or on command. It also has some functions that other plugin can use such as sending a player to another server, sending everyone to the hub server, sending everyone to the hub and shutting the server down.


Commands
========
- /hub
- /lobby
- /alltolobby
- /lobbyall
- /allto
- /shutdown
- /forcekick
- /fkick


Developers
==========

Hook into HubKick
```java
HubKick hubkick = (HubKick) Bukkit.getPluginManager().getPlugin("HubKick");
```

Sends the specified player to the specified server.
```java
hubkick.sendTo(player, server);
```

Sends the player to the lobby server.
```java
hubkick.sendPlayer(player);
```

Kick all players to the lobby server.
```java
hubkick.KickShutdown();
```

Kick all players to the lobby server and shutdown the server.
```java
hubkick.KickShutdown();
```
