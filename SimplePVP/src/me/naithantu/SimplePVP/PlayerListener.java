package me.naithantu.SimplePVP;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {

	SimplePVP plugin;
	HashMap<String, Integer> outOfBoundsTimer = new HashMap<String, Integer>();
	HashMap<String, Integer> outOfBounds = new HashMap<String, Integer>();
	Boolean team1Activated = false;
	Boolean team2Activated = false;

	PlayerListener(SimplePVP instance) {
		plugin = instance;
	}

	String header = ChatColor.DARK_RED + "[PvP] " + ChatColor.WHITE + "";

	public Integer respawnTimer(final Player player, PlayerRespawnEvent event) {
		final List<String> red = plugin.getRed();
		final List<String> blue = plugin.getBlue();
		String selectedMap = plugin.getSelectedMap();
		Integer id = null;
		if (plugin.getRespawnTimer() == 0) {
			if (red.contains(player.getName())) {
				event.setRespawnLocation(plugin.respawn(player));
			} else if (blue.contains(player.getName())) {
				event.setRespawnLocation(plugin.respawn(player));
			}
		} else {
			// Respawn with respawntimer.
			final List<String> playersRespawningRed = plugin.getPlayersRespawningRed();
			final List<String> playersRespawningBlue = plugin.getPlayersRespawningBlue();
			event.setRespawnLocation(plugin.getConfigLocation(selectedMap, "speclocation"));
			playersRespawningRed.add(player.getName());
			player.sendMessage(ChatColor.DARK_RED + "[PvP] " + ChatColor.WHITE + "You will respawn in " + plugin.getRespawnTimer() / 20 + " seconds!");
			id = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					if (red.contains(player.getName())) {
						Player respawnedPlayer = plugin.getServer().getPlayer(plugin.getPlayersRespawningRed().get(0));
						playersRespawningRed.remove(0);
						plugin.setPlayersRespawningRed(playersRespawningRed);
						respawnedPlayer.teleport(plugin.respawn(respawnedPlayer));
					} else if (blue.contains(player.getName())) {
						Player respawnedPlayer = plugin.getServer().getPlayer(plugin.getPlayersRespawningBlue().get(0));
						respawnedPlayer.teleport(plugin.respawn(respawnedPlayer));
						playersRespawningBlue.remove(0);
						plugin.setPlayersRespawningBlue(playersRespawningBlue);
					}
					HashMap<String, Integer> respawnTimers = plugin.getRespawnTimers();
					respawnTimers.remove(player.getName());
					plugin.setRespawnTimers(respawnTimers);
				}
			}, plugin.getRespawnTimer());
		}
		return id;

	}

	public ItemStack getHelmet(String team) {
		ItemStack helmet = null;
		if (team.equals("blue")) {
			List<String> tempBlueArmour = plugin.getConfig().getStringList("maps." + plugin.getSelectedMap() + ".bluearmour");
			for (String itemString : tempBlueArmour) {
				String[] configArmourSplit = itemString.split(":");
				String armourType = configArmourSplit[0];
				int type = Integer.parseInt(configArmourSplit[1]);
				int amount = Integer.parseInt(configArmourSplit[2]);
				byte data = (byte) Integer.parseInt(configArmourSplit[3]);
				ItemStack item = new ItemStack(type, amount, data);
				if (armourType.equals("helmet")) {
					helmet = item;
				}
			}
		} else if (team.equals("red")) {
			List<String> tempRedArmour = plugin.getConfig().getStringList("maps." + plugin.getSelectedMap() + ".redarmour");
			for (String itemString : tempRedArmour) {
				String[] configArmourSplit = itemString.split(":");
				String armourType = configArmourSplit[0];
				int type = Integer.parseInt(configArmourSplit[1]);
				int amount = Integer.parseInt(configArmourSplit[2]);
				byte data = (byte) Integer.parseInt(configArmourSplit[3]);
				ItemStack item = new ItemStack(type, amount, data);
				if (armourType.equals("helmet")) {
					helmet = item;
				}
			}
		}
		return helmet;

	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		// Get variables out of main class.
		List<String> blue = plugin.getBlue();
		List<String> red = plugin.getRed();
		if (red.contains(player.getName()) || blue.contains(player.getName())) {
			// Pay for death && give for kill.
			if (plugin.getDeathTypePay() != 0 && player.getKiller() != null && !player.getKiller().getName().equalsIgnoreCase(player.getName())) {
				player.getKiller().sendMessage(ChatColor.DARK_RED + "[PvP] " + ChatColor.WHITE + "You killed " + player.getName() + ". You got " + plugin.getDeathTypePay() + " Dollars!");
				SimplePVP.econ.depositPlayer(player.getKiller().getName(), plugin.getDeathTypePay());
				player.sendMessage(ChatColor.DARK_RED + "[PvP] " + ChatColor.WHITE + "You were killed by " + player.getKiller().getName() + ". You lost " + plugin.getDeathTypePay() + " Dollars!");
				SimplePVP.econ.withdrawPlayer(player.getName(), plugin.getDeathTypePay());
			}
			// Drop the items.
			if (plugin.getDeathTypeDrop().equals("disabled")) {
				event.setDroppedExp(0);
				event.setKeepLevel(true);
				event.getDrops().clear();
			}
			// Remove from respawntimer if they died during respawn. //TODO does this work properly...?
			if (plugin.getRespawnTimer() != 0) {
				List<String> playersRespawningRed = plugin.getPlayersRespawningRed();
				List<String> playersRespawningBlue = plugin.getPlayersRespawningBlue();
				if (playersRespawningRed.contains(player.getName())) {
					playersRespawningRed.remove(0);
					plugin.setPlayersRespawningRed(playersRespawningRed);
				} else if (playersRespawningBlue.contains(player.getName())) {
					playersRespawningBlue.remove(0);
					plugin.setPlayersRespawningBlue(playersRespawningBlue);
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
			if (plugin.getGameMode().equals("tdm")) {
				if (player.getKiller() == null) {
					return;
				}
				if (red.contains(player.getKiller().getName())) {
					plugin.setRedScore(plugin.getRedScore() + 1);
					if (plugin.getRedScore() >= plugin.getScoreLimit()) {
						plugin.stop();
					}
					if (plugin.getRedScore() % 10 == 0) {
						plugin.sendMessageAll(ChatColor.DARK_RED + "TDM Score:");
						plugin.sendMessageAll(ChatColor.RED + "Team Red: " + Integer.toString(plugin.getRedScore()));
						plugin.sendMessageAll(ChatColor.BLUE + "Team Blue: " + Integer.toString(plugin.getBlueScore()));
					}
				} else if (blue.contains(player.getKiller().getName())) {
					plugin.setBlueScore(plugin.getBlueScore() + 1);
					if (plugin.getBlueScore() >= plugin.getScoreLimit()) {
						plugin.stop();
					}
					if (plugin.getBlueScore() % 10 == 0) {
						plugin.sendMessageAll(ChatColor.DARK_RED + "TDM Score:");
						plugin.sendMessageAll(ChatColor.RED + "Team Red: " + Integer.toString(plugin.getRedScore()));
						plugin.sendMessageAll(ChatColor.BLUE + "Team Blue: " + Integer.toString(plugin.getBlueScore()));
					}
				}
			} else if (plugin.getGameMode().equals("ctf")) {
				if (plugin.getRedFlagTaken() != null) {
					if (plugin.getRedFlagTaken().equals(player.getName())) {
						plugin.setRedFlagTaken(null);
						player.getInventory().setHelmet(getHelmet("blue"));
						plugin.sendMessageAll(ChatColor.DARK_RED + "[PvP] " + ChatColor.WHITE + "The red flag has been returned!");
					}
				}
				if (plugin.getBlueFlagTaken() != null) {
					if (plugin.getBlueFlagTaken().equals(player.getName())) {
						plugin.setBlueFlagTaken(null);
						player.getInventory().setHelmet(getHelmet("red"));
						plugin.sendMessageAll(ChatColor.DARK_RED + "[PvP] " + ChatColor.WHITE + "The blue flag has been returned!");
					}
				}
			} else if (plugin.getGameMode().equals("lms")) {
				if (red.size() - plugin.getPlayersRespawningRed().size() + blue.size() + plugin.getPlayersRespawningBlue().size() == 1) {
					if (red.size() == 1) {
						plugin.sendMessageAll(header + "The round is over, " + red.get(0) + " won the round!");
					} else if (blue.size() == 1) {
						plugin.sendMessageAll(header + "The round is over, " + blue.get(0) + " won the round!");
					}
					plugin.nextRound();
				}
			}
			//Death Messages
			if (red.contains(player.getName()) || blue.contains(player.getName())) {
				plugin.sendMessageAll(event.getDeathMessage());
				event.setDeathMessage(null);
			}
			plugin.setRed(red);
			plugin.setBlue(blue);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		if (plugin.getDeadAfterGame().contains(player.getName())) {
			event.setRespawnLocation(plugin.getServer().getWorld("world").getSpawnLocation());
		}
		List<String> blue = plugin.getBlue();
		List<String> red = plugin.getRed();
		if (red.contains(player.getName()) || blue.contains(player.getName())) { // || redOut.contains(player.getName()) || blueOut.contains(player.getName())
			final String selectedMap = plugin.getSelectedMap();

			// Give items back.
			if (plugin.getDeathTypeDrop().equals("disabled")) {
				plugin.giveItems(player);
			}

			//Respawn to speclocation if the game is not playing.
			if (plugin.getIsPlaying() == false || plugin.getBetweenRounds() == true) {
				event.setRespawnLocation(plugin.getConfigLocation(selectedMap, "speclocation"));
				return;
			}

			//Respawn to speclocation if the gamemode is lms.
			if (plugin.getGameMode().equals("lms")) {
				if (red.contains(player.getName())) {
					plugin.getPlayersRespawningRed().add(player.getName());
					player.sendMessage(header + "You will respawn next round!");
				} else if (blue.contains(player.getName())) {
					plugin.getPlayersRespawningBlue().add(player.getName());
					player.sendMessage(header + "You will respawn next round!");
				}
			}

			//Respawn for non-lms gamemodes.
			int id = respawnTimer(player, event);
			plugin.getRespawnTimers().put(player.getName(), id);
			plugin.setRed(red);
			plugin.setBlue(blue);
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
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
				if (plugin.getIsPlaying() == false) {
					event.setCancelled(true);
				}
			}

			//Friendly fire for arrows.
			if (red.contains(playerName) && red.contains(shooterName) && plugin.getFriendlyFire().equals("disabled") && !plugin.getGameMode().equals("lms")) {
				event.setCancelled(true);
			} else if (blue.contains(playerName) && blue.contains(shooterName) && plugin.getFriendlyFire().equals("disabled") && !plugin.getGameMode().equals("lms")) {
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
					if (plugin.getIsPlaying() == false) {
						event.setCancelled(true);
					}
				}
			}

			//Friendly fire for normal damage
			Player damager = (Player) event.getDamager();
			if (red.contains(playerName) && red.contains(damager.getName()) && plugin.getFriendlyFire().equals("disabled") && !plugin.getGameMode().equals("lms")) {
				event.setCancelled(true);
			} else if (blue.contains(playerName) && blue.contains(damager.getName()) && plugin.getFriendlyFire().equals("disabled") && !plugin.getGameMode().equals("lms")) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerLogOff(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		String playerName = event.getPlayer().getName();
		List<String> blue = plugin.getBlue();
		List<String> red = plugin.getRed();
		List<String> offlineRedInGame = plugin.getOfflineRedInGame();
		List<String> offlineBlueInGame = plugin.getOfflineBlueInGame();
		List<String> spectate = plugin.getSpectate();
		List<String> playersRespawningRed = plugin.getPlayersRespawningRed();
		List<String> playersRespawningBlue = plugin.getPlayersRespawningBlue();
		World world = plugin.getServer().getWorld("world");
		//Add the player to the offline group (in red/blue team). Or completely remove from group and teleport to spawn.
		if (red.contains(playerName)) {
			plugin.emptyInventory(player);
			player.teleport(world.getSpawnLocation());
			offlineRedInGame.add(playerName);
			red.remove(playerName);
		} else if (blue.contains(playerName)) {
			plugin.emptyInventory(player);
			player.teleport(world.getSpawnLocation());
			offlineBlueInGame.add(playerName);
			blue.remove(playerName);
		} else if (spectate.contains(playerName)) {
			player.teleport(plugin.getServer().getWorld("world").getSpawnLocation());
			plugin.emptyInventory(player);
			spectate.remove(playerName);
		} else if (playersRespawningRed.contains(playerName)) {

		} else if (playersRespawningBlue.contains(playerName)) {

		}

		/*
		 * else if (redOut.contains(playerName)) {
		 * player.teleport(plugin.getServer
		 * ().getWorld("world").getSpawnLocation());
		 * plugin.emptyInventory(player); redOut.remove(playerName); } else if
		 * (blueOut.contains(playerName)) {
		 * player.teleport(plugin.getServer().getWorld
		 * ("world").getSpawnLocation()); plugin.emptyInventory(player);
		 * blueOut.remove(playerName); }
		 */
		//Return flag if player disconnects with the flag.
		if (plugin.getGameMode().equals("ctf")) {
			if (plugin.getRedFlagTaken() != null) {
				if (plugin.getRedFlagTaken().equals(playerName)) {
					plugin.setRedFlagTaken(null);
					player.getInventory().setHelmet(getHelmet("blue"));
					plugin.sendMessageAll(ChatColor.DARK_RED + "[PvP] " + ChatColor.WHITE + "The red flag has been returned!");
				}
			} else if (plugin.getBlueFlagTaken() != null) {
				if (plugin.getBlueFlagTaken().equals(playerName)) {
					plugin.setBlueFlagTaken(null);
					player.getInventory().setHelmet(getHelmet("red"));
					plugin.sendMessageAll(ChatColor.DARK_RED + "[PvP] " + ChatColor.WHITE + "The blue flag has been returned!");
				}
			}
		}
	}

	@EventHandler
	public void onPlayerLogIn(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		String playerName = event.getPlayer().getName();
		List<String> blue = plugin.getBlue();
		List<String> red = plugin.getRed();
		List<String> offlineRedInGame = plugin.getOfflineRedInGame();
		List<String> offlineBlueInGame = plugin.getOfflineBlueInGame();
		//Teleport players which were in the offline list to their teams spawn. or to spawn if the game has ended.
		if (offlineRedInGame.contains(playerName)) {
			player.teleport(plugin.respawn(player));
			red.add(playerName);
			offlineRedInGame.remove(playerName);
		} else if (offlineBlueInGame.contains(playerName)) {
			player.teleport(plugin.respawn(player));
			blue.add(playerName);
			offlineBlueInGame.remove(playerName);
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		List<String> blue = plugin.getBlue();
		List<String> red = plugin.getRed();
		final Player player = event.getPlayer();
		if (!red.contains(player.getName()) && !blue.contains(player.getName())) {
			return;
		}
		//Cancel out of bounds area if player is dead/match not playing/player respawning/between rounds.
		if (plugin.getOutOfBoundsArea().equals("enabled") && plugin.getIsPlaying() == true && !player.isDead() && !plugin.getRespawnTimers().containsKey(player.getName()) && plugin.getBetweenRounds() == false) {
			Location outOfBoundsLocation1 = plugin.getOutOfBoundsLocation1();
			Location outOfBoundsLocation2 = plugin.getOutOfBoundsLocation2();
			Double x = player.getLocation().getX();
			Double y = player.getLocation().getY();
			Double z = player.getLocation().getZ();

			if (x < outOfBoundsLocation1.getX() || x > outOfBoundsLocation2.getX() || y < outOfBoundsLocation1.getY() || y > outOfBoundsLocation2.getY() || z < outOfBoundsLocation1.getZ() || z > outOfBoundsLocation2.getZ()) {
				if (!outOfBoundsTimer.containsKey(player.getName())) {
					int id = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
						Integer timeLeft = null;

						@Override
						public void run() {
							if (!outOfBounds.containsKey(player.getName())) {
								timeLeft = plugin.getOutOfBoundsTime() + 1;
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

		if (plugin.getGameMode() == null)
			return;
		if (plugin.getGameMode().equals("ctf")) {
			if (red.contains(player.getName())) {
				if (plugin.getBlueFlagTaken() == null) {
					if (!player.getLocation().getWorld().getName().equals(plugin.getConfigLocation(plugin.getSelectedMap(), "gamemodevars.redflaglocation").getWorld().getName())) {
						return;
					}
					if (player.getLocation().distance(plugin.getConfigLocation(plugin.getSelectedMap(), "gamemodevars.blueflaglocation")) < 1) {
						if (player.getHealth() <= 0) {
							return;
						}
						plugin.setBlueFlagTaken(player.getName());
						player.getInventory().setHelmet(new ItemStack(Material.WOOL, 1, (byte) 11));
						plugin.sendMessageAll(ChatColor.DARK_RED + "[PvP] " + ChatColor.WHITE + "The blue flag has been taken by " + ChatColor.RED + player.getName() + "!");
					}
				} else {
					if (plugin.getBlueFlagTaken().equals(player.getName()) && player.getLocation().distance(plugin.getConfigLocation(plugin.getSelectedMap(), "gamemodevars.redflaglocation")) < 1 && plugin.getRedFlagTaken() == null) {
						plugin.setBlueFlagTaken(null);
						player.getInventory().setHelmet(getHelmet("red"));
						plugin.sendMessageAll(ChatColor.DARK_RED + "[PvP] " + ChatColor.WHITE + "The blue flag has been captured!");
						plugin.setRedScore(plugin.getRedScore() + 1);
						plugin.sendCTFScore();
						if (plugin.getRedScore() >= plugin.getScoreLimit()) {
							plugin.stop();
						}
					}
				}
			} else if (blue.contains(player.getName())) {
				if (plugin.getRedFlagTaken() == null) {
					if (!player.getLocation().getWorld().getName().equals(plugin.getConfigLocation(plugin.getSelectedMap(), "gamemodevars.redflaglocation").getWorld().getName())) {
						return;
					}
					if (player.getLocation().distance(plugin.getConfigLocation(plugin.getSelectedMap(), "gamemodevars.redflaglocation")) < 1) {
						if (player.getHealth() <= 0) {
							return;
						}
						plugin.setRedFlagTaken(player.getName());
						player.getInventory().setHelmet(new ItemStack(Material.WOOL, 1, (byte) 14));
						plugin.sendMessageAll(ChatColor.DARK_RED + "[PvP] " + ChatColor.WHITE + "The red flag has been taken by " + ChatColor.BLUE + player.getName() + "!");
					}
				} else {
					if (plugin.getRedFlagTaken().equals(player.getName()) && player.getLocation().distance(plugin.getConfigLocation(plugin.getSelectedMap(), "gamemodevars.blueflaglocation")) < 1 && plugin.getBlueFlagTaken() == null) {
						plugin.setRedFlagTaken(null);
						player.getInventory().setHelmet(getHelmet("blue"));
						plugin.sendMessageAll(ChatColor.DARK_RED + "[PvP] " + ChatColor.WHITE + "The red flag has been captured!");
						plugin.setBlueScore(plugin.getBlueScore() + 1);
						plugin.sendCTFScore();
						if (plugin.getBlueScore() >= plugin.getScoreLimit()) {
							plugin.stop();
						}
					}
				}
			}
		} else if (plugin.getGameMode().equals("dth")) {
			if (!player.getLocation().getWorld().getName().equals(plugin.getConfigLocation(plugin.getSelectedMap(), "gamemodevars.dthlocation").getWorld().getName()))
				return;
			if (red.contains(player.getName()) && plugin.getAttacking().equals("red")) {
				if (player.getLocation().distance(plugin.getConfigLocation(plugin.getSelectedMap(), "gamemodevars.dthlocation")) < 1) {
					plugin.sendMessageAll(ChatColor.DARK_RED + "[PvP] " + ChatColor.WHITE + "The red team has taken the hill!");
					plugin.setRedScore(plugin.getRedScore() + 1);
					if (plugin.getRedScore() >= plugin.getScoreLimit()) {
						plugin.stop();
					} else {
						plugin.sendMessageAll(header + "Switching sides!");
						plugin.switchTeams();
					}
				}
			}
			if (blue.contains(player.getName()) && plugin.getAttacking().equals("blue")) {
				if (player.getLocation().distance(plugin.getConfigLocation(plugin.getSelectedMap(), "gamemodevars.dthlocation")) < 1) {
					plugin.sendMessageAll(ChatColor.DARK_RED + "[PvP] " + ChatColor.WHITE + "The blue team has taken the hill!");
					plugin.setBlueScore(plugin.getBlueScore() + 1);
					if (plugin.getRedScore() >= plugin.getScoreLimit()) {
						plugin.stop();
					} else {
						plugin.switchTeams();
					}
				}
			}
		}
	}
	@EventHandler
	public void onPlayerChat(PlayerChatEvent event) {
		Player player = event.getPlayer();
		List<String> teamChat = plugin.getTeamChat();
		List<String> allChat = plugin.getAllChat();
		if (teamChat.contains(player.getName())) {
			plugin.pvpChat(player, "team", event.getMessage());
			event.setCancelled(true);
		} else if (allChat.contains(player.getName())) {
			plugin.pvpChat(player, "all", event.getMessage());
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onRedstoneEvent(BlockRedstoneEvent event) {
		if (plugin.getIsPlaying() == true && plugin.getGameMode().equals("redstone")) {
			if (event.getBlock().getBlockPower() == 0)
				return;
			if (event.getBlock().getLocation().getWorld().getName().equals(plugin.getConfigLocation(plugin.getSelectedMap(), "gamemodevars.redstonelocation1").getWorld().getName())) {
				if (event.getBlock().getLocation().distance(plugin.getConfigLocation(plugin.getSelectedMap(), "gamemodevars.redstonelocation1")) < 1) {
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
			if (event.getBlock().getLocation().getWorld().getName().equals(plugin.getConfigLocation(plugin.getSelectedMap(), "gamemodevars.redstonelocation2").getWorld().getName())) {
				if (event.getBlock().getLocation().distance(plugin.getConfigLocation(plugin.getSelectedMap(), "gamemodevars.redstonelocation2")) < 1) {
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