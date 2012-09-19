package me.naithantu.SimplePVP.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.naithantu.SimplePVP.SimplePVP;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminCommands implements CommandExecutor{

	SimplePVP plugin;
	public AdminCommands(SimplePVP instance) {
		plugin = instance;
	}
	
	String mapName;
	String mapNick;

	Location outOfBoundsLocation1;
	Location outOfBoundsLocation2;
	Location tempLocation1;
	Location tempLocation2;
	Location dthLocation;
	Location redFlagLocation;
	Location blueFlagLocation;
	int redSpawnLocations = 0;
	int blueSpawnLocations = 0;
	List<Location> redLocation = new ArrayList<Location>();
	List<Location> blueLocation = new ArrayList<Location>();
	Location specLocation;
	
	public Location getOutOfBoundsLocation(int number){
		if(number == 1){
			return outOfBoundsLocation1;
		}else if(number == 2){
			return outOfBoundsLocation2;
		}
		return null;
	}
	
	public void setOutOfBoundsLocation(Location location, int number){
		if(number == 1){
			outOfBoundsLocation1 = location;
		}else if(number == 2){
			outOfBoundsLocation2 = location;
		}
	}
	public void saveMap() {
		String selectedMap = plugin.getSelectedMap();
		if (selectedMap == null) {
			if (plugin.getServer().getPlayer("naithantu") != null) {
				plugin.getServer().getPlayer("naithantu").sendMessage("Error occured while saving map! (794)");
			}
			return;
		}
		if (mapNick != null) {
			plugin.getMaps().put(selectedMap, mapNick);
		} else {
			plugin.getMaps().put(selectedMap, selectedMap);
		}
		redSpawnLocations = 0;
		Boolean hasNext = true;
		while (hasNext == true) {
			if (plugin.config.contains("maps." + selectedMap + ".redlocation" + redSpawnLocations)) {
				redSpawnLocations++;
			} else {
				hasNext = false;
			}
		}
		blueSpawnLocations = 0;
		hasNext = true;
		while (hasNext == true) {
			if (plugin.config.contains("maps." + selectedMap + ".bluelocation" + blueSpawnLocations)) {
				blueSpawnLocations++;
			} else {
				hasNext = false;
			}
		}

		int locationNumber = redSpawnLocations;
		for (Location tempLocation : redLocation) {
			plugin.setConfigLocation("maps." + selectedMap + ".redlocation" + locationNumber, tempLocation);
			locationNumber++;
			redSpawnLocations++;
		}
		redLocation.clear();
		locationNumber = blueSpawnLocations;
		for (Location tempLocation : blueLocation) {
			plugin.setConfigLocation("maps." + selectedMap + ".bluelocation" + locationNumber, tempLocation);
			locationNumber++;
			blueSpawnLocations++;
		}
		blueLocation.clear();
		if (specLocation != null) {
			plugin.setConfigLocation("maps." + selectedMap + ".speclocation", specLocation);
		}
		plugin.saveTypes();
		plugin.saveConfig();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		Player player = (Player) sender;
		String arg = args[0];
		String header = plugin.getHeader();

		//Get some often used variables from main class.
		HashMap<String, String> maps = plugin.getMaps();
		
		
		if (arg.equalsIgnoreCase("create")) {
			if (player.hasPermission("simplepvp.create") || player.hasPermission("simplepvp.admin")) {
				if (plugin.getIsPlaying() == true) {
					player.sendMessage(header + "Error: There is game playing. Stop this game with /pvp stop first!");
					return true;
				}
				if (plugin.creation == 0) {
					if (args.length > 1) {
						arg = args[1];
						if (!maps.containsKey(arg)) {
							if (args.length == 3) {
								mapName = arg.toLowerCase();
								mapNick = args[2];
							} else {
								mapName = arg;
								mapNick = null;
							}
							plugin.setSelectedMap(arg.toLowerCase());
							plugin.creation = 1;
							player.sendMessage(header + "Next: Make a spawnlocation for team red. Type /pvp setpos when there.");
							return true;
						} else {
							player.sendMessage(header + "Error: That map already exists. Type /pvp remove [mapname] to remove it");
						}
					} else {
						return false;
					}
				} else {
					player.sendMessage(header + "Error: Someone is making an arena, type /pvp abort to cancel this or wait till they are done.");
				}
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
				return true;
			}
		}

		if (arg.equalsIgnoreCase("abort")) {
			if (player.hasPermission("simplepvp.admin")) {
				plugin.stop();
				plugin.creation = 0;
				return true;
			}
		}

		if (arg.equalsIgnoreCase("setpos")) {
			if (player.hasPermission("simplepvp.create") || player.hasPermission("simplepvp.admin")) {
				String selectedMap = plugin.getSelectedMap();
				if (plugin.creation == 1) {
					redLocation.add(player.getLocation());
					player.sendMessage(header + "Next: Make a spawnlocation for team blue. Type /pvp setpos when there.");
					plugin.creation = 2;
				} else if (plugin.creation == 2) {
					blueLocation.add(player.getLocation());
					player.sendMessage(header + "Next: Make a spectator location (this is also used as location before the match starts). Type /pvp setpos when there.");
					plugin.creation = 3;
				} else if (plugin.creation == 3) {
					specLocation = player.getLocation();
					player.sendMessage(header + "Saving arena under name: " + mapName);
					plugin.resetTypes();
					saveMap();
					plugin.creation = 0;
					// End of map plugin.creation. Next: Extra spawnpoint plugin.creation.
				} else if (plugin.creation == 4) {
					redLocation.add(player.getLocation());
					player.sendMessage(header + "Red spawn location added. Type /pvp setpos to add another spawn location or /pvp done to stop adding spawn locations.");
				} else if (plugin.creation == 5) {
					blueLocation.add(player.getLocation());
					player.sendMessage(header + "Blue spawn location added. Type /pvp setpos to add another spawn location or /pvp done to stop adding spawn locations.");
					// End of extra spawnpoint plugin.creation. Next: ctf flag plugin.creation.
				} else if (plugin.creation == 6) {
					redFlagLocation = player.getLocation();
					player.sendMessage(header + "Next: Make a blue flag location. Type /pvp setpos when there.");
					plugin.creation = 7;
				} else if (plugin.creation == 7) {
					blueFlagLocation = player.getLocation();
					plugin.creation = 0;
					plugin.setConfigLocation("maps." + selectedMap + ".gamemodevars.redflaglocation", redFlagLocation);
					plugin.setConfigLocation("maps." + selectedMap + ".gamemodevars.blueflaglocation", blueFlagLocation);
					plugin.setGameMode("ctf");
					player.sendMessage(header + "Red and blue flag locations made. Gamemode changed to CTF!");
					plugin.saveTypes();
					// End of CTF plugin.creation. Next: dth location.
				} else if (plugin.creation == 8) {
					dthLocation = player.getLocation();
					plugin.creation = 0;
					plugin.setConfigLocation("maps." + selectedMap + ".gamemodevars.dthlocation", dthLocation);
					plugin.setGameMode("dth");
					player.sendMessage(header + "Dth location made. Gamemode changed to DTH!");
					plugin.saveTypes();
					//End of dth plugin.creation. Next: out of bounds plugin.creation.
				} else if (plugin.creation == 9) {
					outOfBoundsLocation1 = player.getLocation();
					plugin.creation = 10;
					player.sendMessage(header + "Type /pvp setpos to add a second out of bounds location!");
				} else if (plugin.creation == 10) {
					outOfBoundsLocation2 = player.getLocation();
					plugin.creation = 0;
					double x1 = outOfBoundsLocation1.getX();
					double y1 = outOfBoundsLocation1.getY();
					double z1 = outOfBoundsLocation1.getZ();
					double x2 = outOfBoundsLocation2.getX();
					double y2 = outOfBoundsLocation2.getY();
					double z2 = outOfBoundsLocation2.getZ();
					if (x1 > x2) {
						outOfBoundsLocation1.setX(x2);
						outOfBoundsLocation2.setX(x1);
					}
					if (y1 > y2) {
						outOfBoundsLocation1.setY(y2);
						outOfBoundsLocation2.setY(y1);
					}
					if (z1 > z2) {
						outOfBoundsLocation1.setZ(z2);
						outOfBoundsLocation2.setZ(z1);
					}
					plugin.setConfigLocation("maps." + selectedMap + ".outofboundslocation1", outOfBoundsLocation1);
					plugin.setConfigLocation("maps." + selectedMap + ".outofboundslocation2", outOfBoundsLocation2);
					plugin.setOutOfBoundsArea("enabled");
					player.sendMessage(header + "Out of bounds area enabled!");
					plugin.creation = 0;
					plugin.saveTypes();
					//End of out of bounds plugin.creation. Next: redstone plugin.creation.
				} else if (plugin.creation == 11) {
					tempLocation1 = player.getLocation();
					player.sendMessage(header + "Redstone location set for team red. Type /pvp setpos to add a blue redstone location");
					plugin.creation = 12;
				} else if (plugin.creation == 12) {
					tempLocation2 = player.getLocation();
					player.sendMessage(header + "Redstone location set for team blue, gamemode changed to redstone!");
					plugin.setConfigLocation("maps." + selectedMap + ".gamemodevars.redstonelocation1", tempLocation1);
					plugin.setConfigLocation("maps." + selectedMap + ".gamemodevars.redstonelocation2", tempLocation2);
					plugin.creation = 0;
					plugin.setGameMode("redstone");
					plugin.saveTypes();
					//End of redstone plugin.creation.
				} else {
					player.sendMessage(header + "Error: Not creating a map. Type /pvp create [mapname] first.");
				}
				return true;
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
				return true;
			}
		}

		if (arg.equalsIgnoreCase("done")) {
			if (player.hasPermission("simplepvp.create") || player.hasPermission("simplepvp.admin")) {
				if (plugin.creation == 4 || plugin.creation == 5) {
					player.sendMessage(header + "Saving extra spawn locations...");
					saveMap();
					plugin.creation = 0;
					return true;
				} else {
					player.sendMessage(header + "Error: You did not set all of the required positions yet!");
					return true;
				}
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
				return true;
			}
		}

		if (arg.equalsIgnoreCase("remove")) {
			if (player.hasPermission("simplepvp.remove") || player.hasPermission("simplepvp.admin")) {
				if (args.length == 2) {
					arg = args[1];
					if (maps.containsKey(arg)) {
						plugin.config.set("maps." + arg, null);
						maps.remove(arg);
						player.sendMessage(header + "Removed map " + arg + " succesfully.");
						plugin.saveConfig();
						return true;
					} else {
						player.sendMessage(header + "Error: That map does not exist. Type /pvp list for all maps.");
						return true;
					}
				}
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
				return true;
			}
		}
		if (arg.equalsIgnoreCase("tp")) {
			if (player.hasPermission("simplepvp.tp") || player.hasPermission("simplepvp.admin")) {
				if (args.length > 1) {
					arg = args[1];
					if (maps.containsKey(arg)) {
						if (args.length > 2) {
							player.teleport(plugin.getConfigLocation(arg, args[2]));
						} else {
							player.teleport(plugin.getConfigLocation(arg, "speclocation"));
						}
					} else {
						player.sendMessage(header + "Error: That map does not exist. Type /pvp list for all maps.");
					}
					return true;
				} else {
					return false;
				}
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
				return true;
			}
		}
		return false;
	}

}
