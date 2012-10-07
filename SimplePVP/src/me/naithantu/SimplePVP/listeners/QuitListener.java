package me.naithantu.SimplePVP.listeners;

import java.util.List;

import me.naithantu.SimplePVP.Settings;
import me.naithantu.SimplePVP.SimplePVP;
import me.naithantu.SimplePVP.commands.AdminCommands;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener  implements Listener{

	SimplePVP plugin;
	Settings settings;
	AdminCommands adminCommands;
	
	public QuitListener(SimplePVP instance){
		plugin = instance;
	}
	
	@EventHandler
	public void onPlayerLogOff(PlayerQuitEvent event) {
		settings = plugin.getSettings();
		Player player = event.getPlayer();
		String playerName = event.getPlayer().getName();
		List<String> blue = plugin.getBlue();
		List<String> red = plugin.getRed();
		List<String> spectate = plugin.getSpectate();
		List<String> playersRespawningRed = plugin.getPlayersRespawningRed();
		List<String> playersRespawningBlue = plugin.getPlayersRespawningBlue();
		World world = plugin.getServer().getWorld("world");
		//Add the player to the offline group (in red/blue team). Or completely remove from group and teleport to spawn.
		if (red.contains(playerName) || blue.contains(playerName) || playersRespawningRed.contains(playerName) || playersRespawningBlue.contains(playerName)) {
			plugin.logOffMidGame(player);
			player.teleport(world.getSpawnLocation());
		} else if (spectate.contains(playerName)) {
			plugin.leaveGame(playerName);
			player.teleport(world.getSpawnLocation());
		}
		//Return flag if player disconnects with the flag.
		if (settings.getStringSetting("gameMode").equals("ctf")) {
			if (plugin.getRedFlagTaken() != null) {
				if (plugin.getRedFlagTaken().equals(playerName)) {
					plugin.setRedFlagTaken(null);
					player.getInventory().setHelmet(plugin.getHelmet("blue"));
					plugin.sendMessageAll(ChatColor.DARK_RED + "[PvP] " + ChatColor.WHITE + "The red flag has been returned!");
				}
			} else if (plugin.getBlueFlagTaken() != null) {
				if (plugin.getBlueFlagTaken().equals(playerName)) {
					plugin.setBlueFlagTaken(null);
					player.getInventory().setHelmet(plugin.getHelmet("red"));
					plugin.sendMessageAll(ChatColor.DARK_RED + "[PvP] " + ChatColor.WHITE + "The blue flag has been returned!");
				}
			}
		}
	}
}
