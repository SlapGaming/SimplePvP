package me.naithantu.SimplePVP.listeners;

import java.util.HashMap;
import java.util.List;

import me.naithantu.SimplePVP.Settings;
import me.naithantu.SimplePVP.SimplePVP;
import me.naithantu.SimplePVP.commands.AdminCommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class MoveListener implements Listener {

	SimplePVP plugin;
	Settings settings;
	AdminCommands adminCommands;

	HashMap<String, Integer> outOfBoundsTimer = new HashMap<String, Integer>();
	HashMap<String, Integer> outOfBounds = new HashMap<String, Integer>();

	public MoveListener(SimplePVP instance) {
		plugin = instance;
	}

	String header = ChatColor.DARK_RED + "[PvP] " + ChatColor.WHITE + "";

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		adminCommands = plugin.getAdminCommands();
		settings = plugin.getSettings();
		List<String> blue = plugin.getBlue();
		List<String> red = plugin.getRed();
		final Player player = event.getPlayer();
		if (!red.contains(player.getName()) && !blue.contains(player.getName())) {
			return;
		}
		//Cancel out of bounds area if player is dead/match not playing/player respawning/between rounds.
		if (settings.getBooleanSetting("outOfBoundsArea") == true && settings.getBooleanSetting("isPlaying") == true && !player.isDead()
				&& !plugin.getPlayersRespawningBlue().contains(player.getName()) && !plugin.getPlayersRespawningRed().contains(player.getName()) && plugin.getBetweenRounds() == false) {
			Location outOfBoundsLocation1 = adminCommands.getOutOfBoundsLocation(1);
			Location outOfBoundsLocation2 = adminCommands.getOutOfBoundsLocation(2);
			Double x = player.getLocation().getX();
			Double y = player.getLocation().getY();
			Double z = player.getLocation().getZ();

			if (x < outOfBoundsLocation1.getX() || x > outOfBoundsLocation2.getX() || y < outOfBoundsLocation1.getY() || y > outOfBoundsLocation2.getY() || z < outOfBoundsLocation1.getZ()
					|| z > outOfBoundsLocation2.getZ()) {
				if (!outOfBoundsTimer.containsKey(player.getName())) {
					if (settings.getIntSetting("outOfBoundsTime") == 0) {
						player.damage(20);
						player.sendMessage(header + ChatColor.RED + "You left the combat area, you have been killed!");
					} else {
						int id = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
							Integer timeLeft = null;

							@Override
							public void run() {
								if (!outOfBounds.containsKey(player.getName())) {
									timeLeft = settings.getIntSetting("outOfBoundsTime") + 1;
								} else {
									timeLeft = outOfBounds.get(player.getName());
								}
								timeLeft--;
								if (timeLeft == 0) {
									player.damage(20);
									plugin.getServer().getScheduler().cancelTask(outOfBoundsTimer.get(player.getName()));
									outOfBounds.remove(player.getName());
									outOfBoundsTimer.remove(player.getName());
								} else {
									outOfBounds.put(player.getName(), timeLeft);
									player.sendMessage(header + ChatColor.RED + "You have " + timeLeft + " seconds to return to the combat area!");
								}
							}
						}, 0, 20);
						outOfBoundsTimer.put(player.getName(), id);
					}
				}
			} else {
				if (outOfBoundsTimer.containsKey(player.getName())) {
					plugin.getServer().getScheduler().cancelTask(outOfBoundsTimer.get(player.getName()));
					outOfBounds.remove(player.getName());
					outOfBoundsTimer.remove(player.getName());
					player.sendMessage(header + "You have returned to the combat area!");
				}
			}
		}
		/*
		 * if(!outOfBounds.containsKey(player.getName())){ Integer timeLeft =
		 * plugin.getOutOfBoundsTime(); } public void run() {
		 * 
		 * outOfBounds.put(player.getName(), timeLeft--); if (timeLeft == 0) { }
		 * }
		 */

		String gameMode = settings.getStringSetting("gameMode");
		if (gameMode == null)
			return;
		if (gameMode.equals("ctf")) {
			if (red.contains(player.getName())) {
				if (plugin.getBlueFlagTaken() == null) {
					if (!player.getLocation().getWorld().getName().equals(plugin.getConfigLocation(settings.getStringSetting("selectedMap"), "gamemodevars.redflaglocation").getWorld().getName())) {
						return;
					}
					if (player.getLocation().distance(plugin.getConfigLocation(settings.getStringSetting("selectedMap"), "gamemodevars.blueflaglocation")) < 1) {
						if (player.getHealth() <= 0) {
							return;
						}
						plugin.setBlueFlagTaken(player.getName());
						player.getInventory().setHelmet(new ItemStack(Material.WOOL, 1, (byte) 11));
						plugin.sendMessageAll(ChatColor.DARK_RED + "[PvP] " + ChatColor.WHITE + "The blue flag has been taken by " + ChatColor.RED + player.getName() + "!");
					}
				} else {
					if (plugin.getBlueFlagTaken().equals(player.getName())
							&& player.getLocation().distance(plugin.getConfigLocation(settings.getStringSetting("selectedMap"), "gamemodevars.redflaglocation")) < 1
							&& plugin.getRedFlagTaken() == null) {
						plugin.setBlueFlagTaken(null);
						player.getInventory().setHelmet(plugin.getHelmet("red"));
						plugin.sendMessageAll(ChatColor.DARK_RED + "[PvP] " + ChatColor.WHITE + "The blue flag has been captured!");
						plugin.setRedScore(plugin.getRedScore() + 1);
						plugin.sendCTFScore();
						if (plugin.getRedScore() >= settings.getIntSetting("scoreLimit")) {
							plugin.stop();
						}
					}
				}
			} else if (blue.contains(player.getName())) {
				if (plugin.getRedFlagTaken() == null) {
					if (!player.getLocation().getWorld().getName().equals(plugin.getConfigLocation(settings.getStringSetting("selectedMap"), "gamemodevars.redflaglocation").getWorld().getName())) {
						return;
					}
					if (player.getLocation().distance(plugin.getConfigLocation(settings.getStringSetting("selectedMap"), "gamemodevars.redflaglocation")) < 1) {
						if (player.getHealth() <= 0) {
							return;
						}
						plugin.setRedFlagTaken(player.getName());
						player.getInventory().setHelmet(new ItemStack(Material.WOOL, 1, (byte) 14));
						plugin.sendMessageAll(ChatColor.DARK_RED + "[PvP] " + ChatColor.WHITE + "The red flag has been taken by " + ChatColor.BLUE + player.getName() + "!");
					}
				} else {
					if (plugin.getRedFlagTaken().equals(player.getName())
							&& player.getLocation().distance(plugin.getConfigLocation(settings.getStringSetting("selectedMap"), "gamemodevars.blueflaglocation")) < 1
							&& plugin.getBlueFlagTaken() == null) {
						plugin.setRedFlagTaken(null);
						player.getInventory().setHelmet(plugin.getHelmet("blue"));
						plugin.sendMessageAll(ChatColor.DARK_RED + "[PvP] " + ChatColor.WHITE + "The red flag has been captured!");
						plugin.setBlueScore(plugin.getBlueScore() + 1);
						plugin.sendCTFScore();
						if (plugin.getBlueScore() >= settings.getIntSetting("scoreLimit")) {
							plugin.stop();
						}
					}
				}
			}
		} else if (gameMode.equals("dth")) {
			if (!player.getLocation().getWorld().getName().equals(plugin.getConfigLocation(settings.getStringSetting("selectedMap"), "gamemodevars.dthlocation").getWorld().getName()))
				return;
			if (red.contains(player.getName()) && plugin.getAttacking().equals("red")) {
				if (player.getLocation().distance(plugin.getConfigLocation(settings.getStringSetting("selectedMap"), "gamemodevars.dthlocation")) < 1) {
					plugin.sendMessageAll(ChatColor.DARK_RED + "[PvP] " + ChatColor.WHITE + "The red team has taken the hill!");
					plugin.setRedScore(plugin.getRedScore() + 1);
					if (plugin.getRedScore() >= settings.getIntSetting("scoreLimit")) {
						plugin.stop();
					} else {
						plugin.sendMessageAll(header + "Switching sides!");
						plugin.switchTeams();
					}
				}
			}
			if (blue.contains(player.getName()) && plugin.getAttacking().equals("blue")) {
				if (player.getLocation().distance(plugin.getConfigLocation(settings.getStringSetting("selectedMap"), "gamemodevars.dthlocation")) < 1) {
					plugin.sendMessageAll(ChatColor.DARK_RED + "[PvP] " + ChatColor.WHITE + "The blue team has taken the hill!");
					plugin.setBlueScore(plugin.getBlueScore() + 1);
					if (plugin.getRedScore() >= settings.getIntSetting("scoreLimit")) {
						plugin.stop();
					} else {
						plugin.switchTeams();
					}
				}
			}
		}
	}
}
