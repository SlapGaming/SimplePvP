package me.naithantu.SimplePVP.listeners;

import java.util.List;

import me.naithantu.SimplePVP.Settings;
import me.naithantu.SimplePVP.SimplePVP;
import me.naithantu.SimplePVP.commands.AdminCommands;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RespawnListener implements Listener{

	SimplePVP plugin;
	Settings settings;
	AdminCommands adminCommands;
	
	public RespawnListener(SimplePVP instance){
		plugin = instance;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		settings = plugin.getSettings();
		Player player = event.getPlayer();
		if (plugin.getDeadAfterGame().contains(player.getName())) {
			event.setRespawnLocation(plugin.getServer().getWorld("world").getSpawnLocation());
		}
		List<String> blue = plugin.getBlue();
		List<String> red = plugin.getRed();
		if (red.contains(player.getName()) || blue.contains(player.getName())) {
			Integer id = plugin.respawnTimer(player, event);
			if (id != null)
				plugin.getRespawnTimers().put(player.getName(), id);
		}
	}
}
