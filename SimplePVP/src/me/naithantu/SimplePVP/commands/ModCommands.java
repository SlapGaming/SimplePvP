package me.naithantu.SimplePVP.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.naithantu.SimplePVP.SimplePVP;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;

public class ModCommands implements CommandExecutor{

	SimplePVP plugin;
	ModCommands(SimplePVP instance) {
		plugin = instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		//TODO
		String arg = args[0];
		Player player = (Player) sender;
		String header = plugin.getHeader();

		if (arg.equalsIgnoreCase("list")) {
			if (player.hasPermission("simplepvp.list") || player.hasPermission("simplepvp.mod")) {
				Iterator<String> mapNamesItr = plugin.getMaps().keySet().iterator();
				List<String> mapList = new ArrayList<String>();
				while (mapNamesItr.hasNext()) {
					mapList.add(mapNamesItr.next());
				}
				player.sendMessage(header + "Maps: " + mapList.toString());
				return true;
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
				return true;
			}
		}

		if (arg.equalsIgnoreCase("select")) {
			if (player.hasPermission("simplepvp.select") || player.hasPermission("simplepvp.mod")) {
				if (args.length != 2)
					return false;
				arg = args[1];
				if (plugin.getMaps().containsKey(arg)) {
					plugin.setSelectedMap(arg);
					plugin.loadTypes();
					try {
						plugin.loadItemsRed();
					} catch (NullPointerException e) {
					}
					try {
						plugin.loadItemsBlue();
					} catch (NullPointerException e) {
					}
					player.sendMessage(header + "Selected map " + plugin.selectedMap + ", map info:");
					plugin.sendCurrentTypes(player);
					return true;
				} else {
					player.sendMessage(header + "Error: That map does not exist. Type /pvp list for all maps.");
					return true;
				}
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
				return true;
			}
		}

		if (arg.equalsIgnoreCase("change")) {
			if (player.hasPermission("simplepvp.change") || player.hasPermission("simplepvp.mod")) {
				if (args.length == 1) {
					plugin.sendTypes(player);
					return true;
				}
				if (plugin.getSelectedMap() != null) {
					arg = args[1].toLowerCase();
					if (args.length > 2) {
						String arg2 = args[2].toLowerCase();
						if (arg.equals("join") || arg.equals("jointype")) {
							if (arg2.equals("freejoin")) {
								plugin.joinType = "freejoin";
							} else if (arg2.equals("invite")) {
								plugin.joinType = "invite";
							} else {
								plugin.sendTypes(player);
								return true;
							}
						} else if (arg.equals("gamemode")) {
							if (arg2.equals("none")) {
								plugin.setGameMode("none");
							} else if (arg2.equals("tdm")) {
								plugin.setGameMode("tdm");
							} else if (arg2.equals("ctf")) {
								if (!plugin.config.contains("maps." + plugin.getSelectedMap() + ".gamemodevars.redflaglocation") || !plugin.config.contains("maps." + plugin.selectedMap + ".gamemodevars.blueflaglocation")) {
									player.sendMessage(header + "You need to make red and blue flag locations first!");
									player.sendMessage(header + "Next: Make a red flag location. Type /pvp setpos when there.");
									plugin.creation = 6;
									return true;
								}
								plugin.setGameMode("ctf");
							} else if (arg2.equals("dth")) {
								if (!plugin.config.contains("maps." + plugin.getSelectedMap() + ".gamemodevars.dthlocation")) {
									plugin.creation = 8;
									player.sendMessage(header + "You need to make a DTH location first!");
									player.sendMessage(header + "Next: Make a dth location. Type /pvp setpos when there.");
									return true;
								}
								plugin.setGameMode("dth");
							} else if (arg2.equals("lms")) {
								plugin.gameMode = "lms";
							} else if (arg2.equals("redstone")) {
								if (!plugin.config.contains("maps." + plugin.getSelectedMap() + ".gamemodevars.redredstonelocation") || !plugin.config.contains("maps." + plugin.selectedMap + ".gamemodevars.blueredstonelocation")) {
									player.sendMessage(header + "You need to make red and blue redstone locations first!");
									player.sendMessage(header + "Next: Make a red redstone location (team red will win if this activates). Type /pvp setpos when there.");
									plugin.creation = 11;
									return true;
								}
								plugin.gameMode = "redstone";
							} else {
								plugin.sendTypes(player);
								return true;
							}
						} else if (arg.equals("friendlyfire")) {
							if (arg2.equals("enabled")) {
								plugin.friendlyFire = "enabled";
							} else if (arg2.equals("disabled")) {
								plugin.friendlyFire = "disabled";
							} else {
								plugin.sendTypes(player);
								return true;
							}
						} else if (arg.equals("deathtypepay")) {
							try {
								plugin.deathTypePay = Integer.parseInt(arg2);
							} catch (NumberFormatException e) {
								plugin.sendTypes(player);
								return true;
							}
						} else if (arg.equals("deathtypedrop")) {
							if (arg2.equals("enabled")) {
								plugin.deathTypeDrop = "enabled";
							} else if (arg2.equals("disabled")) {
								plugin.deathTypeDrop = "disabled";
							} else {
								plugin.sendTypes(player);
								return true;
							}
						} else if (arg.equals("respawntimer")) {
							try {
								plugin.respawnTimer = Integer.parseInt(arg2) * 20;
							} catch (NumberFormatException e) {
								plugin.sendTypes(player);
								return true;
							}
						} else if (arg.equals("scorelimit") || arg.equals("score")) {
							try {
								plugin.scoreLimit = Integer.parseInt(arg2);
							} catch (NumberFormatException e) {
								plugin.sendTypes(player);
								return true;
							}
						} else if (arg.equals("roundlimit") || arg.equals("rounds")) {
							try {
								plugin.roundLimit = Integer.parseInt(arg2);
							} catch (NumberFormatException e) {
								plugin.sendTypes(player);
								return true;
							}
						} else if (arg.equals("players")) {
							try {
								plugin.maxPlayers = Integer.parseInt(arg2);
							} catch (NumberFormatException e) {
								plugin.sendTypes(player);
								return true;
							}
						} else if (arg.equals("nick")) {
							List<String> nickName = new ArrayList<String>();
							for (int i = 2; i < args.length; i++) {
								nickName.add(args[i]);
							}
							String fullMessage = Joiner.on(" ").join(nickName);
							plugin.getMaps().put(plugin.getSelectedMap(), fullMessage);
							plugin.saveConfig();
						} else if (arg.equals("randomspawnpoints")) {
							if (arg2.equals("enabled")) {
								plugin.randomSpawnPoints = "enabled";
							} else if (arg2.equals("disabled")) {
								plugin.randomSpawnPoints = "disabled";
							} else {
								plugin.sendTypes(player);
								return true;
							}
						} else if (arg.equals("joinmidgame")) {
							if (arg2.equals("enabled")) {
								plugin.joinMidGame = "enabled";
							} else if (arg2.equals("disabled")) {
								plugin.joinMidGame = "disabled";
							} else {
								plugin.sendTypes(player);
								return true;
							}
						} else if (arg.equals("roundtime")) {
							try {
								plugin.roundTime = Integer.parseInt(arg2);
							} catch (NumberFormatException e) {
								plugin.sendTypes(player);
								return true;
							}
						} else if (arg.equals("outofbounds") || arg.equals("outofboundsarea")) {
							if (arg2.equals("enabled")) {
								if (plugin.config.contains("maps." + plugin.getSelectedMap() + ".outofboundslocation1") && plugin.config.contains("maps." + plugin.getSelectedMap() + ".outofboundslocation2")) {
									plugin.outOfBoundsArea = "enabled";
								} else {
									player.sendMessage(header + "There are no out of bounds locations yet! Type /pvp change outofbounds to add these!");
									return true;
								}
							} else if (arg2.equals("disabled")) {
								plugin.outOfBoundsArea = "disabled";
							} else {
								plugin.sendTypes(player);
								return true;
							}
						} else if (arg.equals("outofboundstime")) {
							try {
								plugin.outOfBoundsTime = Integer.parseInt(arg2);
							} catch (NumberFormatException e) {
								plugin.sendTypes(player);
								return true;
							}
						} else if (arg.equals("autobalance")) {
							if (arg2.equals("enabled")) {
								plugin.autoBalance = "enabled";
							} else if (arg2.equals("disabled")) {
								plugin.autoBalance = "disabled";
							} else {
								plugin.sendTypes(player);
								return true;
							}
						} else if (arg.equals("forcepvpchat")) {
							if (arg2.equals("enabled")) {
								plugin.forcePvpChat = "enabled";
							} else if (arg2.equals("disabled")) {
								plugin.forcePvpChat = "disabled";
							} else {
								plugin.sendTypes(player);
								return true;
							}
						}

						else {
							plugin.sendTypes(player);
							return true;
						}
					} else if (args.length > 1) {
						if (arg.equals("redinventory")) {
							plugin.redInv = player.getInventory().getContents();
							plugin.removeEmptyInvSlotsRed(plugin.redInv);
						} else if (arg.equals("blueinventory")) {
							plugin.blueInv = player.getInventory().getContents();
							plugin.removeEmptyInvSlotsBlue(plugin.blueInv);
						} else if (arg.equals("redarmour")) {
							plugin.getArmourRed(player);
						} else if (arg.equals("bluearmour")) {
							plugin.getArmourBlue(player);
						} else if (arg.equals("redspawns") | arg.equals("redlocation")) {
							plugin.creation = 4;
							player.sendMessage(header + "Type /pvp setpos to add a red spawn location.");
							return true;
						} else if (arg.equals("bluespawns") || arg.equals("bluelocation")) {
							plugin.creation = 5;
							player.sendMessage(header + "Type /pvp setpos to add a blue spawn location");
							return true;
						} else if (arg.equals("outofbounds")) {
							plugin.creation = 9;
							player.sendMessage(header + "Type /pvp setpos to add a first out of bounds position");
							return true;
						} else {
							plugin.sendTypes(player);
							return true;
						}
					}
					plugin.sendCurrentTypes(player);
					plugin.saveTypes();
					return true;
				} else {
					player.sendMessage(header + "Error: You haven't selected a map yet! Type /pvp select [mapname] first!");
					return true;
				}
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
				return true;
			}
		}

		if (arg.equalsIgnoreCase("allowjoin")) {
			if (player.hasPermission("simplepvp.allowjoin") || player.hasPermission("simplepvp.mod")) {
				if (args.length == 2) {
					arg = args[1];
					if (plugin.getMaps().containsKey(arg)) {
						plugin.selectedMap = arg;
						plugin.loadTypes();
						try {
							plugin.loadItemsRed();
						} catch (NullPointerException e) {
						}
						try {
							plugin.loadItemsBlue();
						} catch (NullPointerException e) {
						}
					} else {
						player.sendMessage(header + "Error: That map does not exist. Type /pvp list for all maps.");
						return true;
					}
				}
				if (plugin.getSelectedMap() != null) {
					plugin.allowJoin = true;
					if (plugin.joinType.equals("freejoin")) {
						plugin.getServer().broadcastMessage(header + "Type " + ChatColor.AQUA + "/pvp join " + ChatColor.WHITE + "to join map " + ChatColor.translateAlternateColorCodes('&', plugin.getMaps().get(arg)) + " (Gamemode: " + plugin.gameMode + ")!"); //TODO

					} else if (plugin.joinType.equals("invite")) {
						player.sendMessage(header + "Type /pvp invite [player] to invite players to join " + plugin.selectedMap + "!");
					}
					return true;
				} else {
					player.sendMessage(header + "Error: You haven't selected a map yet! Type /pvp select [mapname] first!");
				}

			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
				return true;
			}
		}

		if (arg.equalsIgnoreCase("start")) {
			if (player.hasPermission("simplepvp.start") || player.hasPermission("simplepvp.mod")) {
				if (plugin.getSelectedMap() != null) {
					plugin.invites.clear();
					plugin.isPlaying = true;
					plugin.redScore = 0;
					plugin.blueScore = 0;
					plugin.redFlagTaken = null;
					plugin.blueFlagTaken = null;
					plugin.teleportBlue("bluelocation");
					plugin.sendMessageBlue(header + "You have been teleported to your teams spawn point, let the games begin!");
					plugin.teleportRed("redlocation");
					plugin.sendMessageRed(header + "You have been teleported to your teams spawn point, let the games begin!");
					player.sendMessage(header + "Players have been teleported to their spawn points, let the games begin!");
					plugin.allowJoin = false;
					if (plugin.gameMode.equals("dth")) {
						plugin.roundTimer();
					}
					return true;
				} else {
					player.sendMessage(header + "Error: You haven't selected a map yet! Type /pvp select [mapname] first!");
					return true;
				}
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
				return true;
			}
		}

		if (arg.equalsIgnoreCase("stop")) {
			if (player.hasPermission("simplepvp.stop") || player.hasPermission("simplepvp.mod")) {
				//removeDeadAfterGame();
				plugin.stop();
				player.sendMessage(header + "Stopped the game, all players have been teleported back to spawn.");

				return true;
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
				return true;
			}
		}

		if (arg.equalsIgnoreCase("allplayers")) {
			if (player.hasPermission("simplepvp.allplayers") || player.hasPermission("simplepvp.mod")) {
				if (player.hasPermission("simplepvp.teams") || player.hasPermission("simplepvp.player")) {
					player.sendMessage(ChatColor.DARK_RED + "PvP Teams:");
					player.sendMessage(ChatColor.RED + "Team Red: " + plugin.getRed().toString());
					player.sendMessage(ChatColor.BLUE + "Team Blue: " + plugin.getBlue().toString());
					player.sendMessage(ChatColor.DARK_RED + "Offline In-Game Players:");
					player.sendMessage(ChatColor.RED + "Offline Red: " + plugin.getOfflineRed().toString());
					player.sendMessage(ChatColor.BLUE + "Offline Blue: " + plugin.getOfflineBlue().toString());
					player.sendMessage(ChatColor.DARK_RED + "Offline Stopped Players:");
					player.sendMessage(ChatColor.DARK_RED + "Players Respawning:");
					player.sendMessage(ChatColor.RED + "Red Respawning: " + plugin.getPlayersRespawningRed().toString());
					player.sendMessage(ChatColor.BLUE + "Blue Respawning: " + plugin.getPlayersRespawningBlue().toString());
					player.sendMessage(ChatColor.DARK_RED + "Spectating Players:");
					player.sendMessage(ChatColor.GRAY + plugin.getSpectate().toString());
					return true;
				} else {
					player.sendMessage(ChatColor.RED + "You do not have access to that command.");
					return true;
				}
			}
		}

		if (arg.equalsIgnoreCase("balance")) {
			if (player.hasPermission("simplepvp.balance") || player.hasPermission("simplepvp.mod")) {
				plugin.balanceTeams();
				player.sendMessage(header + "The teams have been balanced!");
				return true;
			}
		}

		if (arg.equalsIgnoreCase("kick")) {
			if (player.hasPermission("simplepvp.kick") || player.hasPermission("simplepvp.mod")) {
				if (args.length > 1) {
					Player kickPlayer = plugin.getServer().getPlayer(args[1]);
					if (kickPlayer == null) {
						player.sendMessage(header + "Error: Player not found.");
						return true;
					}
					if (plugin.leaveGame(args[1]) == true) {
						player.sendMessage(header + "Kicked player " + arg + "!");
					} else {
						player.sendMessage(header + "Error: That player is not in a team!");
					}
					return true;
				}
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
				return true;
			}
		}

		if (arg.equalsIgnoreCase("invite")) {
			if (player.hasPermission("simplepvp.invite") || player.hasPermission("simplepvp.mod")) {
				if (args.length > 1) {
					Player invitedPlayer = plugin.getServer().getPlayer(args[1]);
					if (!(invitedPlayer == null)) {
						plugin.invites.add(invitedPlayer.getName());
						invitedPlayer.sendMessage(header + "You have been invited! Type /pvp join to join map " + plugin.selectedMap + " (Gamemode: " + plugin.gameMode + ")!");
						player.sendMessage(header + "Invited player " + invitedPlayer.getName() + "!");
					} else {
						player.sendMessage(header + "Error: That player is not online!");
					}
					return true;
				}
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
				return true;
			}
		}

		if (arg.equalsIgnoreCase("win")) {
			if (player.hasPermission("simplepvp.win") || player.hasPermission("simplepvp.mod")) {
				if (args.length > 1) {
					if (args[1].equalsIgnoreCase("red")) {
						if (plugin.blueScore >= plugin.redScore) {
							plugin.redScore = plugin.blueScore + 1;
						}
						plugin.stop();
					} else if (args[1].equalsIgnoreCase("blue")) {
						if (plugin.redScore >= plugin.blueScore) {
							plugin.blueScore = plugin.redScore + 1;
						}
						plugin.stop();
					} else {
						player.sendMessage(header + "Error: You can only make red or blue win.");
					}
					return true;
				}
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
				return true;
			}
		}
		return false;
	}
}
