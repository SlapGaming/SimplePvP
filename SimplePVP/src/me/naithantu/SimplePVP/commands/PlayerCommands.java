package me.naithantu.SimplePVP.commands;

import java.util.List;

import me.naithantu.SimplePVP.Settings;
import me.naithantu.SimplePVP.SimplePVP;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;

public class PlayerCommands {

	SimplePVP plugin;

	public PlayerCommands(SimplePVP instance) {
		plugin = instance;
	}

	Settings settings;

	public boolean playerCommandHandler(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		settings = plugin.getSettings();
		String header = plugin.getHeader();
		if (args.length > 0) {
			String arg = args[0];
			Player player = (Player) sender;
			List<String> red = plugin.getRed();
			List<String> blue = plugin.getBlue();
			List<String> spectate = plugin.getSpectate();
			if (arg.equalsIgnoreCase("join")) {
				if (player.hasPermission("simplepvp.join") || player.hasPermission("simplepvp.player")) {
					if (settings.getStringSetting("selectedMap") != null) {
						if (!red.contains(player.getName()) && !blue.contains(player.getName()) && !spectate.contains(player.getName())) {
							if (settings.getIntSetting("maxPlayers") != 0 && red.size() + blue.size() >= settings.getIntSetting("maxPlayers")) {
								player.sendMessage(header + "Error: This game is full (Player limit: " + settings.getIntSetting("maxPlayers") + ")!");
								return true;
							}
							if (args.length > 1) {
								arg = args[1];
							} else {
								arg = null;
							}
							if (settings.getStringSetting("joinType").equals("freejoin")) {
								if (settings.getBooleanSetting("isPlaying") == true && settings.getBooleanSetting("joinMidGame") == false) {
									player.sendMessage(header + "Error: The game has already started, you'll have to wait for the next game!");
									return true;
								}
								if (plugin.checkInventory(player) == true) {
									plugin.joinGame(player, arg);
									plugin.giveItems(player);
								} else {
									player.sendMessage(header + "Error: Empty your inventory first, make sure you also don't have armor on!");
								}
								if (settings.getBooleanSetting("isPlaying") == true) {
									plugin.respawn(player);
								}
								return true;
							} else if (settings.getStringSetting("joinType").equals("invite")) {
								if (plugin.invites.contains(player.getName())) {
									if (plugin.checkInventory(player) == true) {
										plugin.joinGame(player, arg);
										plugin.giveItems(player);
										plugin.invites.remove(player.getName());
									} else {
										player.sendMessage(header + "Error: Emtpy your inventory first, make sure you also don't have armor on!");
									}
									return true;
								}
								player.sendMessage(header + "Error: You are not allowed to join the game, this game does not have free join enabled!");
							}

						} else {
							player.sendMessage(header + "Error: You are already in a team! Type /pvp leave to leave if you want to join a different team!");
						}
					} else {
						player.sendMessage(header + "Error: There is no game to join!");
					}
				} else {
					player.sendMessage(ChatColor.RED + "You do not have access to that command.");
					return true;
				}
				return true;
			}

			if (arg.equalsIgnoreCase("leave")) {
				if (player.hasPermission("simplepvp.join") || player.hasPermission("simplepvp.player")) {
					if (plugin.leaveGame(player.getName()) == true) {
						player.sendMessage(header + "You left the game!");
					} else {
						player.sendMessage(header + "Error: You aren't in a game!");
					}
					return true;
				} else {
					player.sendMessage(ChatColor.RED + "You do not have access to that command.");
					return true;
				}
			}

			if (arg.equalsIgnoreCase("teams")) {
				if (player.hasPermission("simplepvp.teams") || player.hasPermission("simplepvp.player")) {
					player.sendMessage(ChatColor.DARK_RED + "PvP Teams:");
					player.sendMessage(ChatColor.RED + "Team Red " + "(" + red.size() + "): " + red.toString());
					player.sendMessage(ChatColor.BLUE + "Team Blue " + "(" + blue.size() + "): " + blue.toString());
					return true;
				} else {
					player.sendMessage(ChatColor.RED + "You do not have access to that command.");
					return true;
				}
			}

			if (arg.equalsIgnoreCase("score")) {
				if (player.hasPermission("simplepvp.score") || player.hasPermission("simplepvp.player")) {
					if (settings.getStringSetting("gameMode").equals("tdm")) {
						player.sendMessage(ChatColor.DARK_RED + "TDM Score:");
						player.sendMessage(ChatColor.RED + "Team Red: " + Integer.toString(plugin.redScore));
						player.sendMessage(ChatColor.BLUE + "Team Blue: " + Integer.toString(plugin.blueScore));
					} else if (settings.getStringSetting("gameMode").equals("ctf")) {
						player.sendMessage(ChatColor.DARK_RED + "CTF Score:");
						player.sendMessage(ChatColor.RED + "Team Red: " + Integer.toString(plugin.redScore));
						player.sendMessage(ChatColor.BLUE + "Team Blue: " + Integer.toString(plugin.blueScore));
					} else if (settings.getStringSetting("gameMode").equals("dth")) {
						player.sendMessage(ChatColor.DARK_RED + "DTH Score:");
						player.sendMessage(ChatColor.RED + "Team Red: " + Integer.toString(plugin.redScore));
						player.sendMessage(ChatColor.BLUE + "Team Blue: " + Integer.toString(plugin.blueScore));
					} else if (settings.getStringSetting("gameMode").equals("lms")) {
						player.sendMessage(ChatColor.DARK_RED + "LMS Score:");
						player.sendMessage(ChatColor.RED + "Team Red: " + Integer.toString(plugin.redScore));
						player.sendMessage(ChatColor.BLUE + "Team Blue: " + Integer.toString(plugin.blueScore));
					} else {
						player.sendMessage(header + "Error: No gamemode selected!");
					}
					return true;
				} else {
					player.sendMessage(ChatColor.RED + "You do not have access to that command.");
					return true;
				}
			}

			if (arg.equalsIgnoreCase("team")) {
				if (player.hasPermission("simplepvp.team") || player.hasPermission("simplepvp.player")) {
					if (!red.contains(player.getName()) && !blue.contains(player.getName()))
						return false;
					if (args.length > 1) {
						String fullMessage = Joiner.on(" ").join(args);
						fullMessage = fullMessage.replaceFirst("team ", "");
						plugin.pvpChat(player, "team", fullMessage);
					} else if (!plugin.teamChat.contains(player.getName())) {
						plugin.teamChat.add(player.getName());
						plugin.allChat.remove(player.getName());
						player.sendMessage(header + "You joined the team chat channel!");
					} else {
						player.sendMessage(header + "Error: You are already in the team chat channel. Just type in chat to say something in this channel!");
					}
					return true;
				} else {
					player.sendMessage(ChatColor.RED + "You do not have access to that command.");
					return true;
				}
			}

			if (arg.equalsIgnoreCase("all")) {
				if (player.hasPermission("simplepvp.all") || player.hasPermission("simplepvp.player")) {
					if (plugin.checkInGame(player) == null && !plugin.getSpectate().contains(player.getName()))
						return false;
					if (args.length > 1) {
						String fullMessage = Joiner.on(" ").join(args);
						fullMessage = fullMessage.replaceFirst("all ", "");
						plugin.pvpChat(player, "all", fullMessage);
					} else if (!plugin.allChat.contains(player.getName())) {
						plugin.allChat.add(player.getName());
						plugin.teamChat.remove(player.getName());
						player.sendMessage(header + "You joined the all chat channel!");
					} else {
						player.sendMessage(header + "Error: You are already in the all chat channel. Just type in chat to say something in this channel!");
					}
					return true;
				} else {
					player.sendMessage(ChatColor.RED + "You do not have access to that command.");
					return true;
				}
			}

			if (arg.equalsIgnoreCase("chat")) {
				if (player.hasPermission("simplepvp.player") || player.hasPermission("simplepvp.chat")) {
					if (plugin.allChat.contains(player.getName())) {
						plugin.allChat.remove(player.getName());
						player.sendMessage(header + "You left pvp chat and are now speaking in normal chat!");
					} else if (plugin.teamChat.contains(player.getName())) {
						plugin.teamChat.remove(player.getName());
						player.sendMessage(header + "You left pvp chat and are now speaking in normal chat!");
					} else {
						player.sendMessage(header + "Error: You were not in pvp chat.");
					}
					return true;
				} else {
					player.sendMessage(ChatColor.RED + "You do not have access to that command.");
				}
			}

			if (arg.equalsIgnoreCase("spectate")) {
				if (player.hasPermission("simplepvp.spectate") || player.hasPermission("simplepvp.player")) {
					if (settings.getStringSetting("selectedMap") == null) {
						player.sendMessage(header + "Error: There is no game to join!");
						return true;
					}
					if (!red.contains(player.getName()) && !blue.contains(player.getName()) && !spectate.contains(player.getName())) {
						if (plugin.checkInventory(player) == true) {
							spectate.add(player.getName());
							//Force pvp join if enabled
							if (settings.getBooleanSetting("forcePvpChat") == true) {
								plugin.getAllChat().add(player.getName());
								player.sendMessage(header + "You joined the all chat channel!");
							}
							player.teleport(plugin.getConfigLocation(settings.getStringSetting("selectedMap"), "speclocation"));
							player.sendMessage(header + "You are now spectating! Enjoy!");
						} else {
							player.sendMessage(header + "Error: Emtpy your inventory first, make sure you also don't have armor on!");
						}
					} else {
						player.sendMessage(header + "Error: You are already in a team! Type /pvp leave to leave if you want to join a different team!");
					}
				} else {
					player.sendMessage(ChatColor.RED + "You do not have access to that command.");
				}
				return true;
			}
			if (arg.equalsIgnoreCase("info")) {
				if (player.hasPermission("simplepvp.player")) {
					plugin.sendCurrentTypes(player);
					return true;
				}
			}
			if (arg.equalsIgnoreCase("flag")) {
				if (player.hasPermission("simplepvp.flag") || player.hasPermission("simplepvp.player")) {
					if (!settings.getStringSetting("gameMode").equals("ctf")) {
						player.sendMessage(header + "Error: The gamemode is not ctf!");
						return true;
					}
					if (plugin.blueFlagTaken != null) {
						player.sendMessage(header + "Blue flag: " + plugin.blueFlagTaken);
					} else {
						player.sendMessage(header + "Blue flag not taken");
					}
					if (plugin.redFlagTaken != null) {
						player.sendMessage(header + "Red flag: " + plugin.redFlagTaken);
					} else {
						player.sendMessage(header + "Red flag not taken");
					}
					return true;
				} else {
					player.sendMessage(ChatColor.RED + "You do not have access to that command.");
					return true;
				}
			}
		}
		return false;
	}

}
