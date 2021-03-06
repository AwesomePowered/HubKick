package net.lazlecraft.hubkick;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HubKick extends JavaPlugin implements Listener {

    public String prefix;
    public String kickallMessage;

    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getConfig().options().copyDefaults(true);
        this.saveConfig();
        prefix = getConfig().getString("prefix");
        kickallMessage = getConfig().getString("KickallMessage");
        Metrics metrics = new Metrics(this);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (((commandLabel.equalsIgnoreCase("hub")) || (commandLabel.equalsIgnoreCase("lobby"))) && (sender.hasPermission("hubkick.command"))) {
            Player p = (Player) sender;
            if (args.length == 0) {
                sendPlayer(p);
            } else if (args.length >= 1 && sender.hasPermission("hubkick.others")) {
                if (sender.getServer().getPlayer(args[0]) != null) {
                    sendPlayer(p);
                } else sender.sendMessage(prefix + ChatColor.RED + "Player does not exist!");
            }
        } else if (commandLabel.equalsIgnoreCase("sendplayer") || (commandLabel.equalsIgnoreCase("sendp") && (sender.hasPermission("hubkick.send")))) {
            if (args.length <= 1) {
                sender.sendMessage(prefix + ChatColor.RED + "/sendplayer <player> <server>");
            } else if (args.length >= 2) {
                if (sender.getServer().getPlayer(args[0]) != null) {
                    Player p = sender.getServer().getPlayer(args[0]);
                    String server = args[1];
                    p.sendMessage(prefix + ChatColor.GREEN + "You were sent to " + server + " by " + sender.getName());
                    sendPlayer(p, server);
                } else sender.sendMessage(prefix + ChatColor.RED + "The player you're trying to send is offline!");
            }
        } else if (commandLabel.equalsIgnoreCase("alltolobby") || (commandLabel.equalsIgnoreCase("lobbyall")) || (commandLabel.equalsIgnoreCase("allto")) && (sender.hasPermission("hubkick.kickall"))) {
            kickAll();
        } else if (commandLabel.equalsIgnoreCase("shutdown") && (sender.hasPermission("hubkick.shutdown"))) {
            kickShutdown();
        }
            else if (commandLabel.equalsIgnoreCase("forcekick") || (commandLabel.equalsIgnoreCase("fkick") && (sender.hasPermission("hubkick.forcekick")))) {
		    	if (args.length == 0) {
		    		sender.sendMessage(prefix + ChatColor.RED + "/fkick <player>");
		    	} else if (args.length == 1) {
		    		if (sender.getServer().getPlayer(args[0]) != null) {
						Player p = sender.getServer().getPlayer(args[0]);
                        p.kickPlayer("You have been kicked -f");
            			sender.sendMessage(ChatColor.RED + "Player has been force kicked");
		    		}
		    		else sender.sendMessage(prefix + ChatColor.RED + "Player does not exist!");
		    	}
		    }
        return false;
    }

    //Send the player to the specified server. Expected [Player] and [Server]
    public void sendPlayer(Player p, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        p.sendPluginMessage(this, "BungeeCord", out.toByteArray());
    }

    //Sends the player to the hub.
    public void sendPlayer(Player p) {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("toLobbyMsg")));
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(getHubServer());
        p.sendPluginMessage(this, "BungeeCord", out.toByteArray());
    }

    //This will send all players to the hub with the message configured in the config.
    public void kickAll() {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', kickallMessage));
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(getHubServer());
            p.sendPluginMessage(this, "BungeeCord", out.toByteArray());
        }
    }

    public void kickShutdown() {
        kickAll();
        new BukkitRunnable() {
            public void run() {
                Bukkit.shutdown();
            }
        }.runTaskLater(this, 40L);
    }

    public String getPattern() {
        if (getConfig().getString("ignorePattern") != null) {
            return getConfig().getString("ignorePattern");
        }
        return "(\\bafk|AFK|-f\\b)";
    }

    @EventHandler
    public void onKick(PlayerKickEvent ev) {
        Matcher m = Pattern.compile(getPattern()).matcher(ev.getReason());
        if (m.find()) {
            return;
        }
        if (getConfig().getBoolean("HubOnKick")) {
            Player p = ev.getPlayer();
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix) + ChatColor.RED + " You have been kicked from the server!");
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix) + ChatColor.RED + ChatColor.RED + " Reason: " + ChatColor.GOLD + ev.getReason());
            ev.setCancelled(true);
            sendPlayer(p);
        }
    }


    public String getHubServer() {
        if (getConfig().getBoolean("randomHubs")) {
            Random r = new Random();
            return getConfig().getStringList("Hubs").get(r.nextInt(getConfig().getStringList("Hubs").size()));
        } else {
            return getConfig().getString("HubServer");
        }
    }
}