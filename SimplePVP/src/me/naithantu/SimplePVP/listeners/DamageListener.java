package me.naithantu.SimplePVP.listeners;

import java.util.HashSet;
import java.util.List;

import me.naithantu.SimplePVP.Settings;
import me.naithantu.SimplePVP.SimplePVP;
import me.naithantu.SimplePVP.commands.AdminCommands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class DamageListener  implements Listener{

	SimplePVP plugin;
	Settings settings;
	AdminCommands adminCommands;
	
	public DamageListener(SimplePVP instance){
		plugin = instance;
	}
	
	String header = ChatColor.DARK_RED + "[PvP] " + ChatColor.WHITE + "";

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		settings = plugin.getSettings();
		HashSet<String> pvpWorld = plugin.getPvpWorld();
		//Cancel for /leave if you get hit.
		if (event.getEntity() instanceof Player) {
			String world = event.getEntity().getWorld().getName();
			if (world.equals("world_pvp") && event.getCause().equals(DamageCause.ENTITY_ATTACK) && event.getDamager() instanceof Player) {
				if (pvpWorld.contains(((Player) event.getEntity()).getName())) {
					((Player) event.getEntity()).sendMessage(header + "You have been attacked! Teleport cancelled!");
					pvpWorld.remove(((Player) event.getEntity()).getName());
				}
			}
			if (world.equals("world_pvp") && event.getCause().equals(DamageCause.PROJECTILE) && ((Projectile) event.getDamager()).getShooter() instanceof Player) {
				if (pvpWorld.contains(((Player) event.getEntity()).getName())) {
					((Player) event.getEntity()).sendMessage(header + "You have been attacked! Teleport cancelled!");
					pvpWorld.remove(((Player) event.getEntity()).getName());
				}
			}
		}

		//If it isnt player v. player: return.
		if (event.getCause().equals(DamageCause.PROJECTILE)) {
			if (!(event.getEntity() instanceof Player) || !(((Projectile) event.getDamager()).getShooter() instanceof Player)) {
				return;
			}
		} else {
			if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
				return;
			}
		}
		Player player = (Player) event.getEntity();
		String playerName = player.getName();
		List<String> blue = plugin.getBlue();
		List<String> red = plugin.getRed();
		List<String> spectate = plugin.getSpectate();

		//Cancel pvp for arrows.
		if (event.getCause().equals(DamageCause.PROJECTILE)) {
			String shooterName = ((Player) ((Projectile) event.getDamager()).getShooter()).getName();

			//No pvp for spectators.
			if (spectate.contains(shooterName) || spectate.contains(playerName)) {
				event.setCancelled(true);
			}

			//No arrow damage when not playing.
			if (red.contains(shooterName) || blue.contains(shooterName) || red.contains(playerName) || blue.contains(playerName)) {
				if (settings.getBooleanSetting("isPlaying") == false) {
					event.setCancelled(true);
				}
			}

			//Friendly fire for arrows.
			if (red.contains(playerName) && red.contains(shooterName) && settings.getBooleanSetting("friendlyFire") == false && !settings.getStringSetting("gameMode").equals("lms")) {
				event.setCancelled(true);
			} else if (blue.contains(playerName) && blue.contains(shooterName) && settings.getBooleanSetting("friendlyFire") == false && !settings.getStringSetting("gameMode").equals("lms")) {
				event.setCancelled(true);
			}
		} else {
			//Cancel pvp for normal attacks.
			String damagerName = ((Player) event.getDamager()).getName();

			//No pvp for spectators.
			if (spectate.contains(damagerName) || spectate.contains(playerName)) {
				event.setCancelled(true);
			}

			//No pvp when not playing.
			if (red.contains(playerName) || blue.contains(playerName)) {
				if (red.contains(damagerName) || blue.contains(damagerName)) {
					if (settings.getBooleanSetting("isPlaying") == false) {
						event.setCancelled(true);
					}
				}
			}

			//Friendly fire for normal damage
			Player damager = (Player) event.getDamager();
			if (red.contains(playerName) && red.contains(damager.getName()) && settings.getBooleanSetting("friendlyFire") == false && !settings.getStringSetting("gameMode").equals("lms")) {
				event.setCancelled(true);
			} else if (blue.contains(playerName) && blue.contains(damager.getName()) && settings.getBooleanSetting("friendlyFire") == false && !settings.getStringSetting("gameMode").equals("lms")) {
				event.setCancelled(true);
			}
		}
	}
}
