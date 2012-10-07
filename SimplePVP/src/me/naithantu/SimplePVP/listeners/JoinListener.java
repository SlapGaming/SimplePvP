package me.naithantu.SimplePVP.listeners;

import java.util.List;

import me.naithantu.SimplePVP.Settings;
import me.naithantu.SimplePVP.SimplePVP;
import me.naithantu.SimplePVP.commands.AdminCommands;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener  implements Listener{

	SimplePVP plugin;
	Settings settings;
	AdminCommands adminCommands;
	
	public JoinListener(SimplePVP instance){
		plugin = instance;
	}
	
	@EventHandler
	public void onPlayerLogIn(PlayerJoinEvent event) {
		settings = plugin.getSettings();
		final Player player = event.getPlayer();
		List<String> offlineRed = plugin.getOfflineRed();
		List<String> offlineBlue = plugin.getOfflineBlue();
		List<String> red = plugin.getRed();
		List<String> blue = plugin.getBlue();
		//Teleport players which were in the offline list to their teams spawn. or to spawn if the game has ended.
		if (offlineRed.contains(player.getName())) {
			red.add(player.getName());
			offlineRed.remove(player.getName());
		} else if (offlineBlue.contains(player.getName())) {
			blue.add(player.getName());
			offlineBlue.remove(player.getName());
		}
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				if (plugin.checkInGame(player) == true) {
					player.teleport(plugin.respawn(player));
					plugin.respawnTimer(player, null);
				}
			}
		}, 1);
	}

}
