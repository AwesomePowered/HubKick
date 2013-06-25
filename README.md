HubKick by LaxWasHere
=====================

A bukkit plugin for bungeecord. This will automatically send all the player to the lobby whenever they get kicked or on command.


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
