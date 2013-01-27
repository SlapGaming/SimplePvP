package me.naithantu.SimplePVP;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Team {
	SimplePVP plugin;
	Settings settings;

	String name;
	String colour;
	int spawnLocations;
	private int score = 0;
	private boolean flagTaken = false;
	private List<String> players = new ArrayList<String>();
	ItemStack[] inventory;
	List<String> armour;

	public Team(SimplePVP instance, String name, String colour, ItemStack[] inventory, List<String> armour) {
		this.name = name;
		this.colour = colour;
		this.inventory = inventory;
		this.armour = armour;
		plugin = instance;
		settings = plugin.getSettings();
	}

	public List<String> getPlayers() {
		return players;
	}

	public int getScore() {
		return score;
	}

	public boolean getFlagTaken() {
		return flagTaken;
	}

	public void addPoint() {
		score++;
	}

	public void loadSettings() {
		//Everything team related from loadTypes goes in here.
		calculateSpawnLocations();
	}

	public void updateSettings() {
		settings = plugin.getSettings();
	}

	public void sendMessage(String message) {

	}

	//Inventory and armour methods.
	public void giveItems(String playerName) {
		giveInventory(playerName);
		giveArmour(playerName);
	}
	
	public void giveItemsTeam() {
		for (String playerName : players){
			giveInventory(playerName);
			giveArmour(playerName);
		}
	}

	public void giveInventory(String playerName) {
		Player player = Bukkit.getServer().getPlayer(playerName);
		if (player != null)
			player.getInventory().setContents(inventory);
	}
	
	public void giveArmour(String playerName){
		Player player = Bukkit.getServer().getPlayer(playerName);
		if (player != null){
			for (String itemString : armour) {
				String[] armourSplit = itemString.split(":");
				String armourType = armourSplit[0];
				int type = Integer.parseInt(armourSplit[1]);
				int amount = Integer.parseInt(armourSplit[2]);
				byte data = (byte) Integer.parseInt(armourSplit[3]);
				ItemStack item = new ItemStack(type, amount, data);
				if (armourType.equals("helmet")) {
					player.getInventory().setHelmet(item);
				} else if (armourType.equals("chest")) {
					player.getInventory().setChestplate(item);
				} else if (armourType.equals("leggings")) {
					player.getInventory().setLeggings(item);
				} else if (armourType.equals("boots")) {
					player.getInventory().setBoots(item);
				}
			}
		}
	}

	//Methods for teleportation.
	public void teleportTeam(String locationName) {
		for (String playerName : players)
			teleportPlayer(locationName, playerName);
	}

	public boolean teleportPlayer(String locationName, String playerName) {
		String selectedMap = settings.getStringSetting("selectedMap");
		Location location;
		if (locationName.equals("spawn")) {
			World world = Bukkit.getServer().getWorld("world");
			location = world.getSpawnLocation();
		} else if (locationName.equals("redlocation") && settings.getBooleanSetting("randomSpawnPoints") == true) {
			int randLocation = 0;
			if (spawnLocations > 1) {
				Random random = new Random();
				randLocation = random.nextInt(spawnLocations);
			}
			location = plugin.getConfigLocation(selectedMap, locationName + randLocation);
		} else {
			location = plugin.getConfigLocation(selectedMap, locationName + "0");
		}
		Player player = Bukkit.getServer().getPlayer(playerName);
		if (location != null && player != null) {
			player.teleport(plugin.getConfigLocation(settings.getStringSetting("selectedMap"), locationName));
			return true;
		} else {
			return false;
		}
	}

	private void calculateSpawnLocations() {
		String selectedMap = settings.getStringSetting("selectedMap");
		FileConfiguration config = plugin.getConfig();
		spawnLocations = 0;
		Boolean hasNext = true;
		while (hasNext == true) {
			if (config.contains("maps." + selectedMap + ".redlocation" + spawnLocations)) {
				spawnLocations++;
			} else {
				hasNext = false;
			}
		}
	}
}
