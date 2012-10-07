package me.naithantu.SimplePVP.listeners;

import me.naithantu.SimplePVP.Settings;
import me.naithantu.SimplePVP.SimplePVP;
import me.naithantu.SimplePVP.commands.AdminCommands;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

public class RedstoneListener  implements Listener{

	SimplePVP plugin;
	Settings settings;
	AdminCommands adminCommands;
	
	boolean team1Activated = false;
	boolean team2Activated = false;
	
	public RedstoneListener(SimplePVP instance){
		plugin = instance;
	}
	
	@EventHandler
	public void onRedstoneEvent(BlockRedstoneEvent event) {
		settings = plugin.getSettings();
		if (settings.getBooleanSetting("isPlaying") == true && settings.getStringSetting("gameMode").equals("redstone")) {
			if (event.getBlock().getBlockPower() == 0)
				return;
			if (event.getBlock().getLocation().getWorld().getName().equals(plugin.getConfigLocation(settings.getStringSetting("selectedMap"), "gamemodevars.redstonelocation1").getWorld().getName())) {
				if (event.getBlock().getLocation().distance(plugin.getConfigLocation(settings.getStringSetting("selectedMap"), "gamemodevars.redstonelocation1")) < 1) {
					if (!team1Activated == true) {
						plugin.givePoint(1);
						team1Activated = true;
						plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							public void run() {
								team1Activated = false;
							}
						}, 30);
					}
				}
			}
			if (event.getBlock().getLocation().getWorld().getName().equals(plugin.getConfigLocation(settings.getStringSetting("selectedMap"), "gamemodevars.redstonelocation2").getWorld().getName())) {
				if (event.getBlock().getLocation().distance(plugin.getConfigLocation(settings.getStringSetting("selectedMap"), "gamemodevars.redstonelocation2")) < 1) {
					if (!team2Activated == true) {
						plugin.givePoint(2);
						team2Activated = true;
						plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							public void run() {
								team2Activated = false;
							}
						}, 30);
					}
				}
			}
		}
	}
}
