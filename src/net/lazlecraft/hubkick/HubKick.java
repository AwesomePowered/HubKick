package net.lazlecraft.hubkick;

import java.io.IOException;
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
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

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
		try {
                    Metrics metrics = new Metrics(this);
                    metrics.start();
                } 
                catch (IOException e) {}
	}
	
	  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		    if (((commandLabel.equalsIgnoreCase("hub")) || (commandLabel.equalsIgnoreCase("lobby"))) && (sender.hasPermission("hubkick.command"))) {
			    Player p = (Player)sender;
		    	sendPlayer(p);
		    }
		    else if (commandLabel.equalsIgnoreCase("alltolobby") || (commandLabel.equalsIgnoreCase("lobbyall")) || (commandLabel.equalsIgnoreCase("allto")) && (sender.hasPermission("hubkick.kickall"))) {
		    	kickAll();
		    }
		    else if (commandLabel.equalsIgnoreCase("shutdown") && (sender.hasPermission("hubkick.shutdown"))) {
		    	kickShutdown();
		    }
		    else if (commandLabel.equalsIgnoreCase("forcekick") || (commandLabel.equalsIgnoreCase("fkick") && (sender.hasPermission("hubkick.forcekick")))) {
		    	if (args.length == 0) {
		    		sender.sendMessage(prefix + ChatColor.RED + "/fkick <player>");
		    	} else if (args.length == 1) {
		    		if (sender.getServer().getPlayer(args[0]) != null) {
            			Player pp = sender.getServer().getPlayer(args[0]);
            			//original code from https://github.com/yawkat/EnhancedKick
            			//Untested
            			((org.bukkit.craftbukkit.v1_5_R3.entity.CraftPlayer) pp).getHandle().playerConnection.sendPacket(new net.minecraft.server.v1_5_R3.Packet61WorldEvent(2001, 0, 0, 0, 0x105, false));
		    		} 
		    		else sender.sendMessage(prefix + ChatColor.RED + "Player does not exist!");
		    	}
		    }
		    return false;
		  }
	  
	  //Send the player to the specified server. Expected [Player] and [Server]
	  public void sendTo(Player p, String server) {
	   	   ByteArrayDataOutput out = ByteStreams.newDataOutput();
		      out.writeUTF("Connect");
		      out.writeUTF(server);
		    p.sendPluginMessage(this, "BungeeCord", out.toByteArray());
	  }
	  
	  //Sends the player to the hub.
	  public void sendPlayer(Player p) {
   	   ByteArrayDataOutput out = ByteStreams.newDataOutput();
		      out.writeUTF("Connect");
		      out.writeUTF(getConfig().getString("HubServer"));
		    p.sendPluginMessage(this, "BungeeCord", out.toByteArray());
   }
	  	  
	  //This will send all playerss to the hub with the message configured in the config.
	  public void kickAll() {
	       for (Player p : Bukkit.getServer().getOnlinePlayers()) {
	    	   p.sendMessage(ChatColor.translateAlternateColorCodes('&', kickallMessage));
	    	   ByteArrayDataOutput out = ByteStreams.newDataOutput();
			      out.writeUTF("Connect");
			      out.writeUTF(getConfig().getString("HubServer"));
			    p.sendPluginMessage(this, "BungeeCord", out.toByteArray());
	       }
	  }
	  
	  //This will kick all the players to the hub and shut the server down;
	  //#BlameRoblabla2013
	  public void kickShutdown() {
			  kickAll();
			  						new BukkitRunnable() {
		      public void run() {
		            				Bukkit.shutdown();
		      }
			  						}.runTaskLater(this, 40L);
	  		  }
	  
		  @EventHandler
		  public void onKick(PlayerKickEvent ev) {
			  if (getConfig().getBoolean("HubOnKick")) {
		    Player p = ev.getPlayer();
		    p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix) + ChatColor.RED + " You have been kicked from the server!");
		    p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix) + ChatColor.RED + ChatColor.RED + " Reason: " + ChatColor.GOLD + ev.getReason());
		    ev.setCancelled(true);
		    sendPlayer(p);
		} 
	}
}
//LaxWasHere