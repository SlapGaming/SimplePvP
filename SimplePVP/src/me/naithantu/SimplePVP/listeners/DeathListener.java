package me.naithantu.SimplePVP.listeners;

import java.util.List;

import me.naithantu.SimplePVP.Settings;
import me.naithantu.SimplePVP.SimplePVP;
import me.naithantu.SimplePVP.commands.AdminCommands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {

	SimplePVP plugin;
	Settings settings;
	AdminCommands adminCommands;

	public DeathListener(SimplePVP instance) {
		plugin = instance;
	}

	String header = ChatColor.DARK_RED + "[PvP] " + ChatColor.WHITE + "";

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		settings = plugin.getSettings();
		Player player = event.getEntity();
		// Get variables out of main class.
		List<String> blue = plugin.getBlue();
		List<String> red = plugin.getRed();
		if (red.contains(player.getName()) || blue.contains(player.getName())) {
			// Pay for death && give for kill.
			if (settings.getIntSetting("deathTypePay") != 0 && player.getKiller() != null && !player.getKiller().getName().equalsIgnoreCase(player.getName())) {
				player.getKiller()
						.sendMessage(ChatColor.DARK_RED + "[PvP] " + ChatColor.WHITE + "You killed " + player.getName() + ". You got " + settings.getIntSetting("deathTypePay") + " Dollars!");
				SimplePVP.econ.depositPlayer(player.getKiller().getName(), settings.getIntSetting("deathTypePay"));
				player.sendMessage(ChatColor.DARK_RED + "[PvP] " + ChatColor.WHITE + "You were killed by " + player.getKiller().getName() + ". You lost " + settings.getIntSetting("deathTypePay")
						+ " Dollars!");
				SimplePVP.econ.withdrawPlayer(player.getName(), settings.getIntSetting("deathTypePay"));
			}
			// Drop the items.
			if (settings.getBooleanSetting("deathTypeDrop") == false) {
				event.setDroppedExp(0);
				event.setKeepLevel(true);
				event.getDrops().clear();
			}
			// Remove from respawntimer if they died during respawn. //TODO does this work properly...?
			if (settings.getIntSetting("respawnTimer") != 0) {
				List<String> playersRespawningRed = plugin.getPlayersRespawningRed();
				List<String> playersRespawningBlue = plugin.getPlayersRespawningBlue();
				if (playersRespawningRed.contains(player.getName())) {
					playersRespawningRed.remove(player.getName());
					plugin.setPlayersRespawningRed(playersRespawningRed);
					plugin.getServer().getScheduler().cancelTask(plugin.getRespawnTimers().get(player.getName()));
					plugin.getRespawnTimers().remove(player.getName());
				} else if (playersRespawningBlue.contains(player.getName())) {
					playersRespawningBlue.remove(player.getName());
					plugin.setPlayersRespawningBlue(playersRespawningBlue);
					plugin.getServer().getScheduler().cancelTask(plugin.getRespawnTimers().get(player.getName()));
					plugin.getRespawnTimers().remove(player.getName());
				}
			}
			// if (!(econ.getBalance(player.getName()) < 25))
			// {
			// The thing for open world pvp, re-implement this later!
			// } else {
			// player.getKiller().sendMessage(ChatColor.DARK_RED +
			// "[PvP] " + ChatColor.WHITE + "You killed " +
			// player.getName() +
			// " but he did not have enough money. You can take his items and experience!");
			// player.sendMessage(ChatColor.DARK_RED + "[PvP] " +
			// ChatColor.WHITE +
			// "You did not have enough money, you lost your items and experience!");
			// }

			// Gamemodes (tdm. ctf. lms)
			if (settings.getStringSetting("gameMode").equals("tdm")) {
				if (player.getKiller() == null) {
					return;
				}
				if (red.contains(player.getKiller().getName())) {
					plugin.setRedScore(plugin.getRedScore() + 1);
					if (plugin.getRedScore() >= settings.getIntSetting("scoreLimit")) {
						plugin.stop();
					}
					if (plugin.getRedScore() % 10 == 0) {
						plugin.sendMessageAll(ChatColor.DARK_RED + "TDM Score:");
						plugin.sendMessageAll(ChatColor.RED + "Team Red: " + Integer.toString(plugin.getRedScore()));
						plugin.sendMessageAll(ChatColor.BLUE + "Team Blue: " + Integer.toString(plugin.getBlueScore()));
					}
				} else if (blue.contains(player.getKiller().getName())) {
					plugin.setBlueScore(plugin.getBlueScore() + 1);
					if (plugin.getBlueScore() >= settings.getIntSetting("scoreLimit")) {
						plugin.stop();
					}
					if (plugin.getBlueScore() % 10 == 0) {
						plugin.sendMessageAll(ChatColor.DARK_RED + "TDM Score:");
						plugin.sendMessageAll(ChatColor.RED + "Team Red: " + Integer.toString(plugin.getRedScore()));
						plugin.sendMessageAll(ChatColor.BLUE + "Team Blue: " + Integer.toString(plugin.getBlueScore()));
					}
				}
			} else if (settings.getStringSetting("gameMode").equals("ctf")) {
				if (plugin.getRedFlagTaken() != null) {
					if (plugin.getRedFlagTaken().equals(player.getName())) {
						plugin.setRedFlagTaken(null);
						player.getInventory().setHelmet(plugin.getHelmet("blue"));
						plugin.sendMessageAll(ChatColor.DARK_RED + "[PvP] " + ChatColor.WHITE + "The red flag has been returned!");
					}
				}
				if (plugin.getBlueFlagTaken() != null) {
					if (plugin.getBlueFlagTaken().equals(player.getName())) {
						plugin.setBlueFlagTaken(null);
						player.getInventory().setHelmet(plugin.getHelmet("red"));
						plugin.sendMessageAll(ChatColor.DARK_RED + "[PvP] " + ChatColor.WHITE + "The blue flag has been returned!");
					}
				}
			} else if (settings.getStringSetting("gameMode").equals("lms")) {
				int totalAlive = 0;
				String winnerName = null;
				for (String playerName : red) {
					if (!plugin.getServer().getPlayer(playerName).isDead() && !plugin.getPlayersRespawningRed().contains(playerName)) {
						winnerName = playerName;
						totalAlive++;
					}
				}
				for (String playerName : blue) {
					if (!plugin.getServer().getPlayer(playerName).isDead() && !plugin.getPlayersRespawningBlue().contains(playerName)) {
						winnerName = playerName;
						totalAlive++;
					}
				}
				if (totalAlive == 1) {
					if (red.contains(winnerName)) {
						plugin.sendMessageAll(header + "The round is over, " + red.get(0) + " won the round!");
					} else if (blue.contains(winnerName)) {
						plugin.sendMessageAll(header + "The round is over, " + blue.get(0) + " won the round!");
					}
					plugin.getPlayersRespawningRed().clear();
					plugin.getPlayersRespawningBlue().clear();
					plugin.nextRound();
				}
			}
			//Death Messages
			plugin.sendMessageAll(event.getDeathMessage());
			event.setDeathMessage(null);

			//Add death and kill.
			plugin.getPlayerScores().get(player.getName()).addDeath();
			if(player.getKiller() != null)
				plugin.getPlayerScores().get(player.getKiller().getName()).addKill();

			plugin.setRed(red);
			plugin.setBlue(blue);
		}
	}

}
