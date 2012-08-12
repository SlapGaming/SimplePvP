package me.naithantu.SimplePVP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.base.Joiner;

public class SimplePVP extends JavaPlugin {

	Configuration config;
	static Economy econ = null;
	PluginManager pm;
	public final PlayerListener playerListener = new PlayerListener(this);

	String header = ChatColor.DARK_RED + "[PvP] " + ChatColor.WHITE + "";

	// /leave Variables
	public HashSet<String> pvpWorld = new HashSet<String>();
	public HashSet<String> pvpTimer = new HashSet<String>();

	// Team Variables
	List<String> blue = new ArrayList<String>();
	List<String> red = new ArrayList<String>();
	List<String> allChat = new ArrayList<String>();
	List<String> teamChat = new ArrayList<String>();
	//List<String> blueOut = new ArrayList<String>();
	//List<String> redOut = new ArrayList<String>();
	List<String> spectate = new ArrayList<String>();
	List<String> offlineRedInGame = new ArrayList<String>();
	List<String> offlineRedStopped = new ArrayList<String>();
	List<String> offlineBlueInGame = new ArrayList<String>();
	List<String> offlineBlueStopped = new ArrayList<String>();
	List<String> playersRespawningRed = new ArrayList<String>();
	List<String> playersRespawningBlue = new ArrayList<String>();
	List<String> deadAfterGame = new ArrayList<String>();

	// Map info
	//Set<String> mapNames = new HashSet<String>();
	HashMap<String, String> maps = new HashMap<String, String>();
	String selectedMap;
	Boolean allowJoin = false;
	List<Location> redLocation = new ArrayList<Location>();
	List<Location> blueLocation = new ArrayList<Location>();
	Location specLocation;

	// Temporary Variables
	boolean betweenRounds = false;
	int round;
	Location tempLocation1;
	Location tempLocation2;
	String mapName;
	String mapNick;
	int creation = 0;
	HashSet<String> invites = new HashSet<String>();
	ArrayList<String> tempArrayList = new ArrayList<String>();
	Boolean isPlaying = false;
	int redSpawnLocations = 0;
	int blueSpawnLocations = 0;
	int roundTimer;
	boolean updatedTimer;
	HashMap<String, Integer> respawnTimers = new HashMap<String, Integer>();
	// HashMap<String, ItemStack[]> deathContents = new HashMap<String,
	// ItemStack[]>();
	// HashMap<String, ItemStack[]> deathArmor = new HashMap<String,
	// ItemStack[]>();

	// Types
	String joinType = "freejoin";
	String gameMode = "none";
	String friendlyFire = "disabled";
	String deathTypeDrop = "disabled";
	int respawnTimer = 0;
	int deathTypePay = 0;
	ItemStack[] redInv;
	ItemStack[] blueInv;
	int scoreLimit = 10;
	int maxPlayers = 0;
	int roundTime = 0;
	String randomSpawnPoints = "enabled";
	String joinMidGame = "disabled";
	String forcePvpChat = "enabled";
	Location outOfBoundsLocation1;
	Location outOfBoundsLocation2;
	String outOfBoundsArea = "disabled";
	int outOfBoundsTime = 5;
	String autoBalance = "enabled";
	int roundLimit = 1;
	int betweenRoundTime = 10;

	// Gamemode Variables
	int redScore = 0;
	int blueScore = 0;
	// CTF
	Location redFlagLocation;
	Location blueFlagLocation;
	String redFlagTaken;
	String blueFlagTaken;
	String attacking = "red";
	// DTH
	Location dthLocation;

	//Getters and setters
	public HashSet<String> getPvpWorld() {
		return pvpWorld;
	}
	public HashSet<String> getPvpTimer() {
		return pvpTimer;
	}
	public String getAttacking() {
		return attacking;
	}
	public String getRedFlagTaken() {
		return redFlagTaken;
	}
	public String getBlueFlagTaken() {
		return blueFlagTaken;
	}
	public void setRedFlagTaken(String redFlagTaken) {
		this.redFlagTaken = redFlagTaken;
	}
	public void setBlueFlagTaken(String blueFlagTaken) {
		this.blueFlagTaken = blueFlagTaken;
	}
	public int getScoreLimit() {
		return scoreLimit;
	}
	public int getRedScore() {
		return redScore;
	}
	public int getBlueScore() {
		return blueScore;
	}
	public void setRedScore(int redScore) {
		this.redScore = redScore;
	}
	public void setBlueScore(int blueScore) {
		this.blueScore = blueScore;
	}
	public String getDeathTypeDrop() {
		return deathTypeDrop;
	}
	public int getDeathTypePay() {
		return deathTypePay;
	}
	public int getRespawnTimer() {
		return respawnTimer;
	}
	public String getFriendlyFire() {
		return friendlyFire;
	}
	public Boolean getAllowJoin() {
		return allowJoin;
	}
	public String getGameMode() {
		return gameMode;
	}
	public String getSelectedMap() {
		return selectedMap;
	}
	public List<String> getBlue() {
		return blue;
	}
	public List<String> getRed() {
		return red;
	}
	public int getRound() {
		return round;
	}
	public String getOutOfBoundsArea() {
		return outOfBoundsArea;
	}
	/*
	 * public List<ItemStack> getRedArmour() { return redArmour; } public
	 * List<ItemStack> getBlueArmour() { return blueArmour; }
	 */
	public List<String> getOfflineRedInGame() {
		return offlineRedInGame;
	}
	public List<String> getOfflineRedStopped() {
		return offlineRedStopped;
	}
	public List<String> getOfflineBlueInGame() {
		return offlineBlueInGame;
	}
	public List<String> getOfflineBlueStopped() {
		return offlineBlueStopped;
	}
	public List<String> getPlayersRespawningRed() {
		return playersRespawningRed;
	}
	public List<String> getPlayersRespawningBlue() {
		return playersRespawningBlue;
	}
	public void setPlayersRespawningRed(List<String> playersRespawningRed) {
		this.playersRespawningRed = playersRespawningRed;
	}
	public void setPlayersRespawningBlue(List<String> playersRespawningBlue) {
		this.playersRespawningBlue = playersRespawningBlue;
	}
	public boolean getIsPlaying() {
		return isPlaying;
	}
	public List<String> getSpectate() {
		return spectate;
	}
	public List<String> getDeadAfterGame() {
		return deadAfterGame;
	}
	public List<String> getAllChat() {
		return allChat;
	}
	public List<String> getTeamChat() {
		return teamChat;
	}
	public void setIsPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}
	public void setRed(List<String> red) {
		this.red = red;
	}
	public void setBlue(List<String> blue) {
		this.blue = blue;
	}
	public Location getOutOfBoundsLocation1() {
		return outOfBoundsLocation1;
	}
	public Location getOutOfBoundsLocation2() {
		return outOfBoundsLocation2;
	}
	public int getOutOfBoundsTime() {
		return outOfBoundsTime;
	}
	public boolean getBetweenRounds(){
		return betweenRounds;
	}
	public HashMap<String, Integer> getRespawnTimers(){
		return respawnTimers;
	}
	public void setRespawnTimers(HashMap<String, Integer> respawnTimers){
		this.respawnTimers = respawnTimers;
	}
	public void onEnable() {
		config = this.getConfig();
		loadMapNames();
		setupEconomy();
		loadOfflinePlayers();
		pm = getServer().getPluginManager();
		pm.registerEvents(this.playerListener, this);
	}

	public void onDisable() {
		saveMapNames();
		if (!offlineRedInGame.isEmpty()) {
			offlineRedStopped.addAll(offlineRedInGame);
			offlineRedInGame.clear();
		}
		if (!offlineBlueInGame.isEmpty()) {
			offlineBlueStopped.addAll(offlineBlueInGame);
			offlineBlueInGame.clear();
		}
		saveOfflinePlayers();
		saveConfig();
	}

	public void removeDeadAfterGame() {
		List<String> tempList = new ArrayList<String>();
		for (String playerName : red) {
			if (getServer().getPlayer(playerName).isDead()) {
				tempList.add(playerName);
				deadAfterGame.add(playerName);
			}
		}
		red.removeAll(tempList);
		tempList.clear();
		for (String playerName : blue) {
			if (getServer().getPlayer(playerName).isDead()) {
				tempList.add(playerName);
				deadAfterGame.add(playerName);
			}
		}
		blue.removeAll(tempList);
		tempList.clear();
		for (String playerName : spectate) {
			if (getServer().getPlayer(playerName).isDead()) {
				tempList.add(playerName);
				deadAfterGame.add(playerName);
			}
		}
		spectate.removeAll(tempList);
		tempList.clear();
	}

	public void loadOfflinePlayers() {
		if (!config.getStringList("offlineplayersred.stopped").isEmpty()) {
			offlineRedStopped = config.getStringList("offlineplayersred.stopped");
		}
		if (!config.getStringList("offlineplayersblue.stopped").isEmpty()) {
			offlineBlueStopped = config.getStringList("offlineplayersblue.stopped");
		}
	}

	public void saveOfflinePlayers() {
		config.set("offlineplayersred.stopped", null);
		config.set("offlineplayersred.stopped", offlineRedStopped);
		config.set("offlineplayersblue.stopped", null);
		config.set("offlineplayersblue.stopped", offlineBlueStopped);
		saveConfig();
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}
	//Round, stop and score methods.
	public void roundTimer() {
		updatedTimer = false;
		if (roundTime == 0) {
			return;
		}
		roundTimer = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			Integer timeLeft = roundTime;

			public void run() {
				timeLeft--;
				if (timeLeft == 0) {
					if (gameMode.equals("dth")) {
						sendMessageAll(header + "Time's up! The blue team has defended the hill!");
						blueScore++;
						if (blueScore >= scoreLimit) {
							stop();
						} else {
							sendMessageAll(header + "Switching sides!");
							switchTeams();
						}
					} else {
						//TODO Add other gamemodes here.
						nextRound();
					}
				} else if (timeLeft == 1) {
					sendMessageAll(header + "1 second remaining!");
				} else if (timeLeft <= 5 && timeLeft % 1 == 0) {
					sendMessageAll(header + timeLeft + " seconds remaining!");
				} else if (timeLeft <= 30 & timeLeft % 10 == 0) {
					sendMessageAll(header + timeLeft + " seconds remaining!");
				} else if (timeLeft % 60 == 0) {
					sendMessageAll(header + timeLeft / 60 + " minute(s) remaining!");
				}
			}
		}, 0, 20);

	}
	public void stop() {
		getServer().getScheduler().cancelTasks(this);
		removeDeadAfterGame();
		teleportAll("spawn");
		if (redScore > blueScore) {
			sendMessageAll(header + "You have been teleported to spawn, the game has been won by team red!");
		} else if (blueScore > redScore) {
			sendMessageAll(header + "You have been teleported to spawn, the game has been won by team blue!");
		} else {
			sendMessageAll(header + "You have been teleported to spawn, the game ended in a tie!");
		}
		emptyAllInventories();
		if (!offlineRedInGame.isEmpty()) {
			offlineRedStopped.addAll(offlineRedInGame);
			offlineRedInGame.clear();
		}
		if (!offlineBlueInGame.isEmpty()) {
			offlineBlueStopped.addAll(offlineBlueInGame);
			offlineBlueInGame.clear();
		}
		spectate.clear();
		red.clear();
		blue.clear();
		resetTypes();
		selectedMap = null;
		isPlaying = false;
	}
	public void nextRound() {
		betweenRounds = true;
		round++;
		teleportRed("speclocation");
		teleportBlue("speclocation");
		emptyAllInventories();
		sendMessageAll(header + "The next round will start in " + betweenRoundTime + " seconds!");
		getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			public void run() {
				betweenRounds = false;
				//Re-add players to teams if they have respawned during the waiting time in LMS.
				tempArrayList = (ArrayList<String>) red;
				for(String playerName: tempArrayList){
					if(getServer().getPlayer(playerName).isDead()){
						red.remove(playerName);
						playersRespawningRed.add(playerName);
						getServer().getPlayer(playerName).sendMessage(header + "You have not respawned in time! You will join the game next round!");
					}
				}
				tempArrayList = (ArrayList<String>) playersRespawningBlue;
				for(String playerName: tempArrayList){
					if(getServer().getPlayer(playerName).isDead()){
						blue.remove(playerName);
						playersRespawningBlue.add(playerName);
						getServer().getPlayer(playerName).sendMessage(header + "You have not respawned in time! You will join the game next round!");
					}
				}
				giveItemsAll();
				redScore = 0;
				blueScore = 0;
				sendMessageAll(header + "The next round has started!");
				teleportRed("redlocation");
				teleportBlue("bluelocation");
			}
		}, betweenRoundTime * 20);
	}
	public void givePoint(int team) {
		if (gameMode.equals("redstone")) {
			sendMessageAll(ChatColor.DARK_RED + "[PvP] " + ChatColor.WHITE + "Team blue activated the redstone!");
			sendRedstoneScore();
		}
		if (team == 1) {
			redScore++;
			if (redScore >= scoreLimit && scoreLimit != 0) {
				stop();
			}
		} else if (team == 2) {
			blueScore++;
			if (blueScore >= scoreLimit && scoreLimit != 0) {
				stop();
			}
		}
	}
	//Inventory methods

	public void loadItemsRed() {
		List<String> configItems = config.getStringList("maps." + selectedMap + ".redinv");
		// ItemStack[] tempInv = null;
		List<ItemStack> tempInv = new ArrayList<ItemStack>();
		for (String itemString : configItems) {
			String[] configItemsSplit = itemString.split(":");
			int type = Integer.parseInt(configItemsSplit[0]);
			int amount = Integer.parseInt(configItemsSplit[1]);
			byte data = (byte) Integer.parseInt(configItemsSplit[2]);
			ItemStack item = new ItemStack(type, amount, data);
			// int enchantmentsAmount = configItemsSplit.length - 2;
			// if(enchantmentsAmount > 0){
			// for(int i = 0; i < enchantmentsAmount; i++){
			// item.addUnsafeEnchantment()
			// }
			// }
			tempInv.add(item);
		}
		redInv = tempInv.toArray(new ItemStack[0]);
	}
	@SuppressWarnings("unchecked")
	public void saveItemsRed(ItemStack[] itemstack) {
		if (itemstack == null) {
			return;
		}
		ArrayList<String> configItemList = new ArrayList<String>();
		for (ItemStack item : itemstack) {
			config.set("maps." + selectedMap + ".redinv", null);
			String type = Integer.toString(item.getTypeId());
			String amount = Integer.toString(item.getAmount());
			byte data = item.getData().getData();
			Map<Enchantment, Integer> itemEnchantments = item.getEnchantments();
			ArrayList<String> tempArrayList = new ArrayList<String>();
			Set<?> set = itemEnchantments.entrySet();
			Iterator<?> itemItr = set.iterator();
			tempArrayList.clear();
			while (itemItr.hasNext()) {
				Map.Entry<Enchantment, Integer> me = (Map.Entry<Enchantment, Integer>) itemItr.next();
				tempArrayList.add(me.getKey().toString() + ":" + me.getValue().toString());
			}
			configItemList.add(type + ":" + amount + ":" + data + ":" + tempArrayList);
		}
		config.set("maps." + selectedMap + ".redinv", configItemList);
		saveConfig();
	}
	public void loadItemsBlue() {
		List<String> configItems = config.getStringList("maps." + selectedMap + ".blueinv");
		// ItemStack[] tempInv = null;
		List<ItemStack> tempInv = new ArrayList<ItemStack>();
		for (String itemString : configItems) {
			String[] configItemsSplit = itemString.split(":");
			int type = Integer.parseInt(configItemsSplit[0]);
			int amount = Integer.parseInt(configItemsSplit[1]);
			byte data = (byte) Integer.parseInt(configItemsSplit[2]);
			ItemStack item = new ItemStack(type, amount, data);
			// int enchantmentsAmount = configItemsSplit.length - 2;
			// if(enchantmentsAmount > 0){
			// for(int i = 0; i < enchantmentsAmount; i++){
			// item.addUnsafeEnchantment()
			// }
			// }
			tempInv.add(item);
		}
		blueInv = tempInv.toArray(new ItemStack[0]);
		//Load armour.
	}
	@SuppressWarnings("unchecked")
	public void saveItemsBlue(ItemStack[] itemstack) {
		if (itemstack == null) {
			return;
		}
		ArrayList<String> configItemList = new ArrayList<String>();
		for (ItemStack item : itemstack) {
			config.set("maps." + selectedMap + ".blueinv", null);
			String type = Integer.toString(item.getTypeId());
			String amount = Integer.toString(item.getAmount());
			byte data = item.getData().getData();
			Map<Enchantment, Integer> itemEnchantments = item.getEnchantments();
			ArrayList<String> tempArrayList = new ArrayList<String>();
			Set<?> set = itemEnchantments.entrySet();
			Iterator<?> itemItr = set.iterator();
			tempArrayList.clear();
			while (itemItr.hasNext()) {
				Map.Entry<Enchantment, Integer> me = (Map.Entry<Enchantment, Integer>) itemItr.next();
				tempArrayList.add(me.getKey().toString() + ":" + me.getValue().toString());
			}
			configItemList.add(type + ":" + amount + ":" + data + ":" + tempArrayList);
		}
		config.set("maps." + selectedMap + ".blueinv", configItemList);
		saveConfig();
	}
	public void removeEmptyInvSlotsRed(ItemStack[] itemStack) {
		List<ItemStack> itemList = new ArrayList<ItemStack>();
		for (ItemStack item : itemStack) {
			if (item != null) {
				itemList.add(item);
			}
		}
		redInv = itemList.toArray(new ItemStack[itemList.size()]);
	}
	public void removeEmptyInvSlotsBlue(ItemStack[] itemStack) {
		List<ItemStack> itemList = new ArrayList<ItemStack>();
		for (ItemStack item : itemStack) {
			if (item != null) {
				itemList.add(item);
			}
		}
		blueInv = itemList.toArray(new ItemStack[itemList.size()]);
	}
	public void giveItems(Player player) {
		if (red.contains(player.getName())) { // || redOut.contains(player.getName())
			try {
				player.getInventory().setContents(redInv);
			} catch (NullPointerException e) {
			}
			List<String> tempRedArmour = config.getStringList("maps." + selectedMap + ".redarmour");
			for (String itemString : tempRedArmour) {
				String[] configArmourSplit = itemString.split(":");
				String armourType = configArmourSplit[0];
				int type = Integer.parseInt(configArmourSplit[1]);
				int amount = Integer.parseInt(configArmourSplit[2]);
				byte data = (byte) Integer.parseInt(configArmourSplit[3]);
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
		} else if (blue.contains(player.getName())) { // || blueOut.contains(player.getName())
			try {
				player.getInventory().setContents(blueInv);
			} catch (NullPointerException e) {
			}
			List<String> tempBlueArmour = config.getStringList("maps." + selectedMap + ".bluearmour");
			for (String itemString : tempBlueArmour) {
				String[] configArmourSplit = itemString.split(":");
				String armourType = configArmourSplit[0];
				int type = Integer.parseInt(configArmourSplit[1]);
				int amount = Integer.parseInt(configArmourSplit[2]);
				byte data = (byte) Integer.parseInt(configArmourSplit[3]);
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
	public void getArmourRed(Player player) {
		config.set("maps." + selectedMap + ".redarmour", null);
		saveArmour(player.getInventory().getItem(0), "helmet", "red");
		saveArmour(player.getInventory().getItem(1), "chest", "red");
		saveArmour(player.getInventory().getItem(2), "leggings", "red");
		saveArmour(player.getInventory().getItem(3), "boots", "red");
		saveConfig();
	}
	public void getArmourBlue(Player player) {
		config.set("maps." + selectedMap + ".bluearmour", null);
		saveArmour(player.getInventory().getItem(0), "helmet", "blue");
		saveArmour(player.getInventory().getItem(1), "chest", "blue");
		saveArmour(player.getInventory().getItem(2), "leggings", "blue");
		saveArmour(player.getInventory().getItem(3), "boots", "blue");
		saveConfig();
	}

	@SuppressWarnings("unchecked")
	public void saveArmour(ItemStack item, String armourType, String team) {
		if (item != null) {
			String type = Integer.toString(item.getTypeId());
			String amount = Integer.toString(item.getAmount());
			byte data = item.getData().getData();
			Map<Enchantment, Integer> itemEnchantments = item.getEnchantments();
			ArrayList<String> tempArrayList = new ArrayList<String>();
			Set<?> set = itemEnchantments.entrySet();
			Iterator<?> itemItr = set.iterator();
			tempArrayList.clear();
			while (itemItr.hasNext()) {
				Map.Entry<Enchantment, Integer> me = (Map.Entry<Enchantment, Integer>) itemItr.next();
				tempArrayList.add(me.getKey().toString() + ":" + me.getValue().toString());
			}
			List<String> tempArmour = config.getStringList("maps." + selectedMap + "." + team + "armour");
			tempArmour.add(armourType + ":" + type + ":" + amount + ":" + data + ":" + tempArrayList);
			config.set("maps." + selectedMap + "." + team + "armour", tempArmour);
		}
	}

	public void emptyAllInventories() {
		Player player;
		Iterator<String> blueItr = blue.iterator();
		while (blueItr.hasNext()) {
			player = getServer().getPlayer(blueItr.next());
			emptyInventory(player);
		}

		Iterator<String> redItr = red.iterator();
		while (redItr.hasNext()) {
			player = getServer().getPlayer(redItr.next());
			emptyInventory(player);
		}

		Iterator<String> specItr = spectate.iterator();
		while (specItr.hasNext()) {
			player = getServer().getPlayer(specItr.next());
			emptyInventory(player);
		}
	}
	public void giveItemsAll() {
		Player player;
		Iterator<String> blueItr = blue.iterator();
		ItemStack[] tempBlueInv = null;
		ItemStack[] tempRedInv = null;
		if (gameMode.equals("dth") && attacking.equals("blue")) {
			try {
				tempBlueInv = redInv;
			} catch (NullPointerException e) {
			}
			try {
				tempRedInv = blueInv;
			} catch (NullPointerException e) {
			}
		} else {
			try {
				tempBlueInv = blueInv;
			} catch (NullPointerException e) {
			}
			try {
				tempRedInv = redInv;
			} catch (NullPointerException e) {
			}
		}
		while (blueItr.hasNext()) {
			player = getServer().getPlayer(blueItr.next());
			try {
				player.getInventory().setContents(tempBlueInv);
			} catch (NullPointerException e) {
			}
			List<String> tempBlueArmour = config.getStringList("maps." + selectedMap + ".bluearmour");
			for (String itemString : tempBlueArmour) {
				String[] configArmourSplit = itemString.split(":");
				String armourType = configArmourSplit[0];
				int type = Integer.parseInt(configArmourSplit[1]);
				int amount = Integer.parseInt(configArmourSplit[2]);
				byte data = (byte) Integer.parseInt(configArmourSplit[3]);
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
		Iterator<String> redItr = red.iterator();
		while (redItr.hasNext()) {
			player = getServer().getPlayer(redItr.next());
			try {
				player.getInventory().setContents(tempRedInv);
			} catch (NullPointerException e) {
			}
			List<String> tempRedArmour = config.getStringList("maps." + selectedMap + ".redarmour");
			for (String itemString : tempRedArmour) {
				String[] configArmourSplit = itemString.split(":");
				String armourType = configArmourSplit[0];
				int type = Integer.parseInt(configArmourSplit[1]);
				int amount = Integer.parseInt(configArmourSplit[2]);
				byte data = (byte) Integer.parseInt(configArmourSplit[3]);
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

	@SuppressWarnings("unchecked")
	public void saveArmourBlue(ItemStack[] itemstack) {
		if (itemstack == null) {
			return;
		}
		ArrayList<String> configItemList = new ArrayList<String>();
		for (ItemStack item : itemstack) {
			if (item != null) {
				config.set("maps." + selectedMap + ".bluearmour", null);
				String type = Integer.toString(item.getTypeId());
				String amount = Integer.toString(item.getAmount());
				byte data = item.getData().getData();
				Map<Enchantment, Integer> itemEnchantments = item.getEnchantments();
				ArrayList<String> tempArrayList = new ArrayList<String>();
				Set<?> set = itemEnchantments.entrySet();
				Iterator<?> itemItr = set.iterator();
				tempArrayList.clear();
				while (itemItr.hasNext()) {
					Map.Entry<Enchantment, Integer> me = (Map.Entry<Enchantment, Integer>) itemItr.next();
					tempArrayList.add(me.getKey().toString() + ":" + me.getValue().toString());
				}
				configItemList.add(type + ":" + amount + ":" + data + ":" + tempArrayList);
			}
		}
		config.set("maps." + selectedMap + ".bluearmour", configItemList);
		saveConfig();
	}
	//Map methods
	@SuppressWarnings("rawtypes")
	public void saveMapNames() {
		if (maps.isEmpty()) {
			return;
		}
		config.set("mapnames", null);
		Set<?> set = maps.entrySet();
		Iterator<?> mapsItr = set.iterator();
		tempArrayList.clear();
		while (mapsItr.hasNext()) {
			Map.Entry me = (Map.Entry) mapsItr.next();
			tempArrayList.add(me.getKey().toString() + ":" + me.getValue().toString());
		}
		config.set("mapnames", tempArrayList);
		saveConfig();
	}
	@SuppressWarnings("unchecked")
	public void loadMapNames() {
		maps.clear();
		if (config.get("mapnames") != null) {
			String tempString = null;
			tempArrayList = (ArrayList<String>) this.getConfig().get("mapnames");
			Integer i = 0;
			Iterator<String> itr = tempArrayList.iterator();
			while (itr.hasNext()) {
				itr.next();
				try {
					tempString = tempArrayList.get(i);
				} catch (IndexOutOfBoundsException e) {
					e.printStackTrace();
				}
				i++;
				String[] mapsplit = tempString.split(":");
				if (mapsplit.length == 1) {
					maps.put(mapsplit[0], mapsplit[0]);
				} else {
					maps.put(mapsplit[0], mapsplit[1]);
				}
			}
		} else {
			System.out.println("No maps found in the config file!");
		}
	}
	public void saveMap() {
		if (selectedMap == null) {
			if (getServer().getPlayer("naithantu") != null) {
				getServer().getPlayer("naithantu").sendMessage("Error occured while saving map! (759)");
			}
			return;
		}
		if (mapNick != null) {
			maps.put(selectedMap, mapNick);
		} else {
			maps.put(selectedMap, selectedMap);
		}
		redSpawnLocations = 0;
		Boolean hasNext = true;
		while (hasNext == true) {
			if (config.contains("maps." + selectedMap + ".redlocation" + redSpawnLocations)) {
				redSpawnLocations++;
			} else {
				hasNext = false;
			}
		}
		blueSpawnLocations = 0;
		hasNext = true;
		while (hasNext == true) {
			if (config.contains("maps." + selectedMap + ".bluelocation" + blueSpawnLocations)) {
				blueSpawnLocations++;
			} else {
				hasNext = false;
			}
		}

		int locationNumber = redSpawnLocations;
		for (Location tempLocation : redLocation) {
			setConfigLocation("maps." + selectedMap + ".redlocation" + locationNumber, tempLocation);
			locationNumber++;
			redSpawnLocations++;
		}
		redLocation.clear();
		locationNumber = blueSpawnLocations;
		for (Location tempLocation : blueLocation) {
			setConfigLocation("maps." + selectedMap + ".bluelocation" + locationNumber, tempLocation);
			locationNumber++;
			blueSpawnLocations++;
		}
		blueLocation.clear();
		if (specLocation != null) {
			setConfigLocation("maps." + selectedMap + ".speclocation", specLocation);
		}
		saveTypes();
		saveConfig();
	}
	//Type methods
	public void loadTypes() {
		joinType = config.getString("maps." + selectedMap + ".jointype");
		gameMode = config.getString("maps." + selectedMap + ".gamemode");
		friendlyFire = config.getString("maps." + selectedMap + ".friendlyfire");
		deathTypePay = config.getInt("maps." + selectedMap + ".deathtypepay");
		deathTypeDrop = config.getString("maps." + selectedMap + ".deathtypedrop");
		respawnTimer = config.getInt("maps." + selectedMap + ".respawntimer");
		scoreLimit = config.getInt("maps." + selectedMap + ".scorelimit");
		maxPlayers = config.getInt("maps." + selectedMap + ".maxplayers");
		randomSpawnPoints = config.getString("maps." + selectedMap + ".randomspawnpoints");
		joinMidGame = config.getString("maps." + selectedMap + ".joinmidgame");
		roundTime = config.getInt("maps." + selectedMap + ".roundtime");
		forcePvpChat = config.getString("maps." + selectedMap + ".forcepvpchat");
		outOfBoundsArea = config.getString("maps." + selectedMap + ".outofboundsarea");
		autoBalance = "enabled";
		if (config.contains("maps." + selectedMap + ".outofboundslocation1") && config.contains("maps." + selectedMap + ".outofboundslocation2")) {
			outOfBoundsLocation1 = getConfigLocation(selectedMap, "outofboundslocation1");
			outOfBoundsLocation2 = getConfigLocation(selectedMap, "outofboundslocation2");
		}
		redSpawnLocations = 0;
		Boolean hasNext = true;
		while (hasNext == true) {
			if (config.contains("maps." + selectedMap + ".redlocation" + redSpawnLocations)) {
				redSpawnLocations++;
			} else {
				hasNext = false;
			}
		}
		blueSpawnLocations = 0;
		hasNext = true;
		while (hasNext == true) {
			if (config.contains("maps." + selectedMap + ".bluelocation" + blueSpawnLocations)) {
				blueSpawnLocations++;
			} else {
				hasNext = false;
			}
		}
	}
	public void saveTypes() {
		config.set("maps." + selectedMap + ".jointype", joinType);
		config.set("maps." + selectedMap + ".gamemode", gameMode);
		config.set("maps." + selectedMap + ".friendlyfire", friendlyFire);
		config.set("maps." + selectedMap + ".deathtypepay", deathTypePay);
		config.set("maps." + selectedMap + ".deathtypedrop", deathTypeDrop);
		config.set("maps." + selectedMap + ".respawntimer", respawnTimer);
		config.set("maps." + selectedMap + ".scorelimit", scoreLimit);
		config.set("maps." + selectedMap + ".maxplayers", maxPlayers);
		config.set("maps." + selectedMap + ".randomspawnpoints", randomSpawnPoints);
		config.set("maps." + selectedMap + ".joinmidgame", joinMidGame);
		config.set("maps." + selectedMap + ".roundtime", roundTime);
		config.set("maps." + selectedMap + ".forcepvpchat", forcePvpChat);
		config.set("maps." + selectedMap + ".outofboundsarea", outOfBoundsArea);
		config.set("maps." + selectedMap + ".outofboundstime", outOfBoundsTime);
		config.set("maps." + selectedMap + ".autobalance", autoBalance);
		if (outOfBoundsLocation1 != null && outOfBoundsLocation2 != null) {
			setConfigLocation("maps." + selectedMap + ".outofboundslocation1", outOfBoundsLocation1);
			setConfigLocation("maps." + selectedMap + ".outofboundslocation2", outOfBoundsLocation2);
		}
		saveMapNames();
		saveItemsRed(redInv);
		saveItemsBlue(blueInv);
		saveConfig();
	}
	public void sendTypes(Player player) {
		player.sendMessage(header + "Error: Allowed types:");
		player.sendMessage(header + "Join: freejoin/invite");
		player.sendMessage(header + "Gamemode: none/tdm");
		player.sendMessage(header + "Friendlyfire: enabled/disabled");
		player.sendMessage(header + "Death Type Pay: [amount] (0 to disable it)");
		player.sendMessage(header + "Death Type Drop: enabled/disabled");
		player.sendMessage(header + "Respawn Timer: [time in seconds] (0 to disable it)");
		player.sendMessage(header + "Score Limit: [score]");
		player.sendMessage(header + "Players: [amount] (0 to disable maximum amount)");
		player.sendMessage(header + "Nick: [nickname] (changes the name which is shown to the players.)");
		player.sendMessage(header + "Random spawn points: enabled/disabled");
		player.sendMessage(header + "Join mid game: enabled/disabled");
		player.sendMessage(header + "Round Time: [time in seconds] (0 to disable it)");
		player.sendMessage(header + "Force pvp chat: enabled/disabled");
		player.sendMessage(header + "Out of bounds area: enabled/disabled");
		player.sendMessage(header + "Out of bounds time: [time in seconds]");
		player.sendMessage(header + "Auto balance: [enabled/disabled]");
	}
	public void sendCurrentTypes(Player player) {
		player.sendMessage(header + "Join type: " + joinType);
		player.sendMessage(header + "Gamemode: " + gameMode);
		player.sendMessage(header + "Friendly fire: " + friendlyFire);
		player.sendMessage(header + "Death Type Pay: " + deathTypePay);
		player.sendMessage(header + "Death Type Drop: " + deathTypeDrop);
		player.sendMessage(header + "Respawn Timer: " + respawnTimer / 20);
		player.sendMessage(header + "Score limit: " + scoreLimit);
		player.sendMessage(header + "Players: " + maxPlayers);
		player.sendMessage(header + "Nick: " + maps.get(selectedMap));
		player.sendMessage(header + "Random spawn points: " + randomSpawnPoints);
		player.sendMessage(header + "Join mid game: " + joinMidGame);
		player.sendMessage(header + "Round time: " + roundTime);
		player.sendMessage(header + "Force pvp chat: " + forcePvpChat);
		player.sendMessage(header + "Out of bounds area: " + outOfBoundsArea);
		player.sendMessage(header + "Out of bounds time: " + outOfBoundsTime);
		player.sendMessage(header + "Auto balance: " + autoBalance);
		player.sendMessage(header + "Red spawn locations: " + redSpawnLocations);
		player.sendMessage(header + "Blue spawn locations: " + blueSpawnLocations);
	}

	public void resetTypes() {
		joinType = "freejoin";
		gameMode = "none";
		friendlyFire = "disabled";
		deathTypeDrop = "disabled";
		respawnTimer = 0;
		deathTypePay = 0;
		scoreLimit = 10;
		redScore = 0;
		blueScore = 0;
		playersRespawningRed.clear();
		playersRespawningBlue.clear();
		redSpawnLocations = 0;
		blueSpawnLocations = 0;
		randomSpawnPoints = "enabled";
		joinMidGame = "disabled";
		forcePvpChat = "enabled";
		outOfBoundsArea = "disabled";
		outOfBoundsTime = 5;
		autoBalance = "enabled";
		redInv = null;
		blueInv = null;
		roundTime = 0;
	}

	//Config locations
	public void setConfigLocation(String configString, Location location) {
		String world = location.getWorld().getName();
		Double x = location.getX();
		Double y = location.getY();
		Double z = location.getZ();
		float yaw = location.getYaw();
		float pitch = location.getPitch();
		config.set(configString, world + ":" + x + ":" + y + ":" + z + ":" + yaw + ":" + pitch);
	}
	public Location getConfigLocation(String map, String spawnLocation) {
		if (map == null || spawnLocation == null) {
			stop();
			return getServer().getWorld("world").getSpawnLocation();
		}
		String[] tempString = config.getString("maps." + map + "." + spawnLocation).split(":");
		World world = getServer().getWorld(tempString[0]);
		Double x = Double.valueOf(tempString[1]);
		Double y = Double.valueOf(tempString[2]);
		Double z = Double.valueOf(tempString[3]);
		Float yaw = Float.valueOf(tempString[4]);
		Float pitch = Float.valueOf(tempString[5]);
		Location location = new Location(world, x, y, z, yaw, pitch);
		return location;
	}

	public Boolean checkInventory(Player player) {
		Boolean emptyInv = true;
		PlayerInventory inv = player.getInventory();
		for (ItemStack stack : inv.getContents()) {
			try {
				if (stack.getType() != (Material.AIR)) {
					emptyInv = false;
				}
			} catch (NullPointerException e) {
			}
		}
		for (ItemStack stack : inv.getArmorContents()) {
			try {
				if (stack.getType() != (Material.AIR)) {
					emptyInv = false;
				}
			} catch (NullPointerException e) {
			}
		}
		return emptyInv;
	}

	//Message methods
	public void sendMessageRed(String message) {
		Player player;
		Iterator<String> redItr = red.iterator();
		while (redItr.hasNext()) {
			player = getServer().getPlayer(redItr.next());
			player.sendMessage(message);
		}
		/*
		 * Iterator<String> redOutItr = redOut.iterator(); while
		 * (redOutItr.hasNext()) { player =
		 * getServer().getPlayer(redOutItr.next()); player.sendMessage(message);
		 * }
		 */
	}
	public void sendMessageBlue(String message) {
		Player player;
		Iterator<String> blueItr = blue.iterator();
		while (blueItr.hasNext()) {
			player = getServer().getPlayer(blueItr.next());
			player.sendMessage(message);
		}
		/*
		 * Iterator<String> blueOutItr = blueOut.iterator(); while
		 * (blueOutItr.hasNext()) { player =
		 * getServer().getPlayer(blueOutItr.next());
		 * player.sendMessage(message); }
		 */
	}
	public void sendMessageSpectate(String message) {
		Player player;
		Iterator<String> spectateItr = spectate.iterator();
		while (spectateItr.hasNext()) {
			player = getServer().getPlayer(spectateItr.next());
			player.sendMessage(message);
		}
	}
	public void sendMessageAll(String message) {
		sendMessageRed(message);
		sendMessageBlue(message);
		sendMessageSpectate(message);

	}

	//Score methods
	public void sendCTFScore() {
		sendMessageAll(ChatColor.DARK_RED + "CTF Score:");
		sendMessageAll(ChatColor.RED + "Team Red: " + Integer.toString(redScore));
		sendMessageAll(ChatColor.BLUE + "Team Blue: " + Integer.toString(blueScore));
	}
	public void sendTDMScore() {
		sendMessageAll(ChatColor.DARK_RED + "TDM Score:");
		sendMessageAll(ChatColor.RED + "Team Red: " + Integer.toString(redScore));
		sendMessageAll(ChatColor.BLUE + "Team Blue: " + Integer.toString(blueScore));
	}
	public void sendRedstoneScore() {
		sendMessageAll(ChatColor.DARK_RED + "Redstone Score:");
		sendMessageAll(ChatColor.RED + "Team Red: " + Integer.toString(redScore));
		sendMessageAll(ChatColor.BLUE + "Team Blue: " + Integer.toString(blueScore));
	}

	//Teleport methods
	public Location respawn(Player player) {
		Location location = null;

		if (blue.contains(player.getName())) {
			if (randomSpawnPoints.equals("enabled")) {
				int randLocation = 0;
				if (blueSpawnLocations > 1) {
					Random random = new Random();
					randLocation = random.nextInt(blueSpawnLocations);
				}
				location = getConfigLocation(selectedMap, "bluelocation" + randLocation);
			} else {
				location = getConfigLocation(selectedMap, "bluelocation0");
			}
		} else if (red.contains(player.getName())) {
			if (randomSpawnPoints.equals("enabled")) {
				int randLocation = 0;
				if (redSpawnLocations > 1) {
					Random random = new Random();
					randLocation = random.nextInt(redSpawnLocations);
				}
				location = getConfigLocation(selectedMap, "redlocation" + randLocation);
			} else {
				location = getConfigLocation(selectedMap, "redlocation0");
			}
		}
		if (location == null) {
			location = getServer().getWorld("world").getSpawnLocation();
			player.sendMessage(header + "An error occured! You have been teleported to spawn.");
		}
		return location;
	}

	public void teleportRed(String locationName) {
		if (red.size() == 0) {
			return;
		}
		Player player;
		Iterator<String> redItr = red.iterator();
		while (redItr.hasNext()) {
			player = getServer().getPlayer(redItr.next());
			Location location;
			if (locationName.equals("spawn")) {
				World world = getServer().getWorld("world");
				location = world.getSpawnLocation();
			} else if (locationName.equals("redlocation") && randomSpawnPoints.equals("enabled")) {
				int randLocation = 0;
				if (redSpawnLocations > 1) {
					Random random = new Random();
					randLocation = random.nextInt(redSpawnLocations);
				}
				location = getConfigLocation(selectedMap, locationName + randLocation);
			} else {
				location = getConfigLocation(selectedMap, locationName);
			}
			try {
				player.teleport(location);
			} catch (NullPointerException e) {
			}
		}
	}
	public void teleportBlue(String locationName) {
		if (blue.size() == 0) {
			return;
		}
		Player player;
		Iterator<String> blueItr = blue.iterator();
		while (blueItr.hasNext()) {
			player = getServer().getPlayer(blueItr.next());
			Location location;
			if (locationName.equals("spawn")) {
				World world = getServer().getWorld("world");
				location = world.getSpawnLocation();
			} else if (locationName.equals("bluelocation")) {
				int randLocation = 0;
				if (blueSpawnLocations > 1) {
					Random random = new Random();
					randLocation = random.nextInt(blueSpawnLocations);
				}
				location = getConfigLocation(selectedMap, locationName + randLocation);
			} else {
				location = getConfigLocation(selectedMap, locationName);
			}
			try {
				player.teleport(location);
			} catch (NullPointerException e) {
			}
		}
	}
	public void teleportSpectate(String locationName) {
		if (spectate.size() == 0) {
			return;
		}
		Location location;
		if (locationName.equals("spawn")) {
			World world = getServer().getWorld("world");
			location = world.getSpawnLocation();
		} else {
			location = getConfigLocation(selectedMap, locationName);
		}
		Player player;
		Iterator<String> specItr = spectate.iterator();
		while (specItr.hasNext()) {
			player = getServer().getPlayer(specItr.next());
			try {
				player.teleport(location);
			} catch (NullPointerException e) {
			}
		}
	}
	public void teleportAll(String locationName) {
		teleportRed(locationName);
		teleportBlue(locationName);
		teleportSpectate(locationName);
		/*
		 * Player player; Iterator<String> redOutItr = redOut.iterator(); while
		 * (redOutItr.hasNext()) { player =
		 * getServer().getPlayer(redOutItr.next()); try {
		 * player.teleport(location); } catch (NullPointerException e) { } }
		 * Iterator<String> blueOutItr = blueOut.iterator(); while
		 * (blueOutItr.hasNext()) { player =
		 * getServer().getPlayer(blueOutItr.next()); try {
		 * player.teleport(location); } catch (NullPointerException e) { } }
		 */
	}
	public void balanceTeams() {
		if (blue.size() - red.size() > 1) {
			Player removedPlayer = getServer().getPlayer(blue.get(blue.size() - 1));
			red.add(removedPlayer.getName());
			blue.remove(removedPlayer.getName());
			removedPlayer.damage(Short.MAX_VALUE);
			removedPlayer.sendMessage(header + "You have been team balanced!");
		} else if (blue.size() - red.size() < -1) {
			Player removedPlayer = getServer().getPlayer(red.get(red.size() - 1));
			blue.add(removedPlayer.getName());
			red.remove(removedPlayer.getName());
			removedPlayer.damage(Short.MAX_VALUE);
			removedPlayer.sendMessage(header + "You have been team balanced!");
		}
	}
	public void joinGame(Player player, String team) {
		if (team != null) {
			if (team.equalsIgnoreCase("blue")) {
				player.teleport(getConfigLocation(selectedMap, "speclocation"));
				sendMessageAll(header + ChatColor.BLUE + player.getName() + ChatColor.WHITE + " joined the game!");
				blue.add(player.getName());
				player.sendMessage(header + "You joined team " + ChatColor.BLUE + "blue" + "!");
				player.sendMessage(header + "You will be teleported to your teams spawn point as soon as the match starts!");
			} else if (team.equalsIgnoreCase("red")) {
				player.teleport(getConfigLocation(selectedMap, "speclocation"));
				sendMessageAll(header + ChatColor.RED + player.getName() + ChatColor.WHITE + " joined the game!");
				red.add(player.getName());
				player.sendMessage(header + "You joined team " + ChatColor.RED + "red" + "!");
				player.sendMessage(header + "You will be teleported to your teams spawn point as soon as the match starts!");
			} else {
				player.sendMessage(header + "Error: That team does not exist. Use /pvp join to auto-join a team!");
			}
		} else {
			if (blue.size() >= red.size()) {
				player.teleport(getConfigLocation(selectedMap, "speclocation"));
				sendMessageAll(header + ChatColor.RED + player.getName() + ChatColor.WHITE + " joined the game!");
				red.add(player.getName());
				player.sendMessage(header + "You joined team " + ChatColor.RED + "red" + "!");
				player.sendMessage(header + "You will be teleported to your teams spawn point as soon as the match starts!");
			} else {
				player.teleport(getConfigLocation(selectedMap, "speclocation"));
				sendMessageAll(header + ChatColor.BLUE + player.getName() + ChatColor.WHITE + " joined the game!");
				blue.add(player.getName());
				player.sendMessage(header + "You joined team " + ChatColor.BLUE + "blue" + "!");
				player.sendMessage(header + "You will be teleported to your teams spawn point as soon as the match starts!");
			}
		}

		//Force pvp join if enabled
		if (forcePvpChat.equals("enabled")) {
			if (red.contains(player.getName()) || blue.contains(player.getName())) {
				teamChat.add(player.getName());
				player.sendMessage(header + "You joined the team chat channel!");
			} else if (spectate.contains(player.getName())) {
				allChat.add(player.getName());
				player.sendMessage(header + "You joined the all chat channel!");
			}
		}
	}
	public boolean leaveGame(String playerName) {
		Player player = getServer().getPlayer(playerName);
		if (player != null) {
			if (gameMode.equals("ctf")) {
				if (redFlagTaken != null) {
					if (redFlagTaken.equals(player.getName())) {
						setRedFlagTaken(null);
						sendMessageAll(header + "The red flag has been returned!");
					}
				} else if (blueFlagTaken != null) {
					if (blueFlagTaken.equals(player.getName())) {
						setBlueFlagTaken(null);
						sendMessageAll(header + "The blue flag has been returned!");
					}
				}
			}
			if (red.contains(player.getName())) {
				red.remove(player.getName());
			} else if (blue.contains(player.getName())) {
				blue.remove(player.getName());
			} else if (spectate.contains(player.getName())) {
				spectate.remove(player.getName());
			} else {
				return false;
			}
			sendMessageAll(header + ChatColor.BLUE + player.getName() + ChatColor.WHITE + " left the game!");
			emptyInventory(player);
			World world = getServer().getWorld("world");
			player.teleport(world.getSpawnLocation());
		} else if (offlineRedInGame.contains(playerName)) {
			offlineRedInGame.remove(playerName);
			offlineRedStopped.add(playerName);
		} else if (offlineBlueInGame.contains(playerName)) {
			offlineBlueInGame.remove(playerName);
			offlineBlueStopped.add(playerName);
		} else {
			return false;
		}
		if (autoBalance.equals("enabled")) {
			balanceTeams();
		}
		return true;
		/*
		 * else if (redOut.contains(player.getName())) {
		 * redOut.remove(player.getName()); sendMessageAll(header +
		 * ChatColor.RED + player.getName() + ChatColor.WHITE +
		 * " left the game!"); } else if (blueOut.contains(player.getName())) {
		 * blueOut.remove(player.getName()); sendMessageAll(header +
		 * ChatColor.BLUE + player.getName() + ChatColor.WHITE +
		 * " left the game!"); }
		 */
	}

	public void emptyInventory(Player player) {
		player.getInventory().clear();
		player.getInventory().setBoots(new ItemStack(Material.AIR));
		player.getInventory().setLeggings(new ItemStack(Material.AIR));
		player.getInventory().setChestplate(new ItemStack(Material.AIR));
		player.getInventory().setHelmet(new ItemStack(Material.AIR));
	}

	public void switchTeams() {
		getServer().getScheduler().cancelTask(roundTimer);
		List<String> tempList = red;
		red = blue;
		blue = tempList;
		int tempInt = redScore;
		redScore = blueScore;
		blueScore = tempInt;
		emptyAllInventories();
		giveItemsAll();
		teleportRed("redlocation");
		teleportBlue("bluelocation");
		roundTimer();
	}

	public String checkInGame(Player player) {
		String playerName = player.getName();
		if (blue.contains(playerName)) {
			return "blue";
		} else if (red.contains(playerName)) {
			return "red";
		} else if (spectate.contains(playerName)) {
			return "spectate";
		}
		return null;
	}

	//Chat methods
	public void pvpChat(Player player, String team, String message) {
		if (team.equals("team")) {
			if (red.contains(player.getName())) { //|| redOut.contains(player.getName())
				if (player.hasPermission("simplepvp.mod")) {
					message = ChatColor.translateAlternateColorCodes('&', message);
				}
				sendMessageRed(ChatColor.GRAY + "[TEAM] " + ChatColor.RED + player.getName() + ChatColor.WHITE + ": " + message);
			} else if (blue.contains(player.getName())) { //|| blueOut.contains(player.getName())
				if (player.hasPermission("simplepvp.mod")) {
					message = ChatColor.translateAlternateColorCodes('&', message);
				}
				sendMessageBlue(ChatColor.GRAY + "[TEAM] " + ChatColor.BLUE + player.getName() + ChatColor.WHITE + ": " + message);
			} else {
				player.sendMessage(header + "Error: You aren't in a game!");
			}
		} else if (team.equals("all")) {
			if (red.contains(player.getName())) {
				sendMessageAll(ChatColor.GRAY + "[ALL] " + ChatColor.RED + player.getName() + ChatColor.WHITE + ": " + message);
			} else if (blue.contains(player.getName())) {
				sendMessageAll(ChatColor.GRAY + "[ALL] " + ChatColor.BLUE + player.getName() + ChatColor.WHITE + ": " + message);
			}
		}
	}

	/*
	 * public void switchDth() { if (attacking.equals("red")) { attacking =
	 * "blue"; } else { attacking = "red"; } emptyAllInventories();
	 * giveItemsAll(); teleportBlue("bluelocation"); teleportRed("redlocation");
	 * }
	 */
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (commandLabel.equalsIgnoreCase("leave")) {
			final Player player = (Player) sender;
			if (player.hasPermission("simplepvp.leave")) {
				if (player.getWorld().getName().equalsIgnoreCase("world_pvp")) {
					if (red.contains(player.getName()) || blue.contains(player.getName()) || spectate.contains(player.getName())) {
						player.sendMessage(header + "Error: You are in a game. Type /pvp leave to leave pvp!");
						return true;
					}
					if (!pvpTimer.contains(player.getName())) {
						pvpWorld.add(player.getName());
						pvpTimer.add(player.getName());
						player.sendMessage(header + "Leaving the pvp world in 10 seconds!");
						getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
							public void run() {
								if (pvpWorld.contains(player.getName())) {
									pvpWorld.remove(player.getName());
									pvpTimer.remove(player.getName());
									World world = getServer().getWorld("world");
									player.teleport(new Location(world, -5.5, 80, -795.5));
									player.sendMessage(header + "You have been teleported to spawn!");
								} else {
									pvpTimer.remove(player.getName());
								}
							}
						}, 200);
					} else {
						player.sendMessage(ChatColor.RED + "You are not allowed to use this more then once per 10 seconds!");
					}

				} else {
					player.sendMessage(header + "Error: You are not in the pvp world!");
				}
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
			return true;
		}

		if (commandLabel.equalsIgnoreCase("pvp")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (args.length > 0) {
					String arg = args[0];
					if (arg.equalsIgnoreCase("create")) {
						if (player.hasPermission("simplepvp.create") || player.hasPermission("simplepvp.admin")) {
							if (isPlaying == true) {
								player.sendMessage(header + "Error: There is game playing. Stop this game with /pvp stop first!");
								return true;
							}
							if (creation == 0) {
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
										selectedMap = arg.toLowerCase();
										creation = 1;
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
							stop();
							creation = 0;
							return true;
						}
					}

					if (arg.equalsIgnoreCase("setpos")) {
						if (player.hasPermission("simplepvp.create") || player.hasPermission("simplepvp.admin")) {
							if (creation == 1) {
								redLocation.add(player.getLocation());
								player.sendMessage(header + "Next: Make a spawnlocation for team blue. Type /pvp setpos when there.");
								creation = 2;
							} else if (creation == 2) {
								blueLocation.add(player.getLocation());
								player.sendMessage(header + "Next: Make a spectator location (this is also used as location before the match starts). Type /pvp setpos when there.");
								creation = 3;
							} else if (creation == 3) {
								specLocation = player.getLocation();
								player.sendMessage(header + "Saving arena under name: " + mapName);
								resetTypes();
								saveMap();
								creation = 0;
								// End of map creation. Next: Extra spawnpoint creation.
							} else if (creation == 4) {
								redLocation.add(player.getLocation());
								player.sendMessage(header + "Red spawn location added. Type /pvp setpos to add another spawn location or /pvp done to stop adding spawn locations.");
							} else if (creation == 5) {
								blueLocation.add(player.getLocation());
								player.sendMessage(header + "Blue spawn location added. Type /pvp setpos to add another spawn location or /pvp done to stop adding spawn locations.");
								// End of extra spawnpoint creation. Next: ctf flag creation.
							} else if (creation == 6) {
								redFlagLocation = player.getLocation();
								player.sendMessage(header + "Next: Make a blue flag location. Type /pvp setpos when there.");
								creation = 7;
							} else if (creation == 7) {
								blueFlagLocation = player.getLocation();
								creation = 0;
								setConfigLocation("maps." + selectedMap + ".gamemodevars.redflaglocation", redFlagLocation);
								setConfigLocation("maps." + selectedMap + ".gamemodevars.blueflaglocation", blueFlagLocation);
								gameMode = "ctf";
								player.sendMessage(header + "Red and blue flag locations made. Gamemode changed to CTF!");
								saveTypes();
								// End of CTF creation. Next: dth location.
							} else if (creation == 8) {
								dthLocation = player.getLocation();
								creation = 0;
								setConfigLocation("maps." + selectedMap + ".gamemodevars.dthlocation", dthLocation);
								gameMode = "dth";
								player.sendMessage(header + "Dth location made. Gamemode changed to DTH!");
								saveTypes();
								//End of dth creation. Next: out of bounds creation.
							} else if (creation == 9) {
								outOfBoundsLocation1 = player.getLocation();
								creation = 10;
								player.sendMessage(header + "Type /pvp setpos to add a second out of bounds location!");
							} else if (creation == 10) {
								outOfBoundsLocation2 = player.getLocation();
								creation = 0;
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
								setConfigLocation("maps." + selectedMap + ".outofboundslocation1", outOfBoundsLocation1);
								setConfigLocation("maps." + selectedMap + ".outofboundslocation2", outOfBoundsLocation2);
								outOfBoundsArea = "enabled";
								player.sendMessage(header + "Out of bounds area enabled!");
								creation = 0;
								saveTypes();
								//End of out of bounds creation. Next: redstone creation.
							} else if (creation == 11) {
								tempLocation1 = player.getLocation();
								player.sendMessage(header + "Redstone location set for team red. Type /pvp setpos to add a blue redstone location");
								creation = 12;
							} else if (creation == 12) {
								tempLocation2 = player.getLocation();
								player.sendMessage(header + "Redstone location set for team blue, gamemode changed to redstone!");
								setConfigLocation("maps." + selectedMap + ".gamemodevars.redstonelocation1", tempLocation1);
								setConfigLocation("maps." + selectedMap + ".gamemodevars.redstonelocation2", tempLocation2);
								creation = 0;
								gameMode = "redstone";
								saveTypes();
								//End of redstone creation.
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
							if (creation == 4 || creation == 5) {
								player.sendMessage(header + "Saving extra spawn locations...");
								saveMap();
								creation = 0;
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
									config.set("maps." + arg, null);
									maps.remove(arg);
									player.sendMessage(header + "Removed map " + arg + " succesfully.");
									saveConfig();
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
										player.teleport(getConfigLocation(arg, args[2]));
									} else {
										player.teleport(getConfigLocation(arg, "speclocation"));
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
					if (arg.equalsIgnoreCase("list")) {
						if (player.hasPermission("simplepvp.list") || player.hasPermission("simplepvp.mod")) {
							Iterator<String> mapNamesItr = maps.keySet().iterator();
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
							if (maps.containsKey(arg)) {
								selectedMap = arg;
								mapNick = maps.get(arg);
								loadTypes();
								try {
									loadItemsRed();
								} catch (NullPointerException e) {
								}
								try {
									loadItemsBlue();
								} catch (NullPointerException e) {
								}
								player.sendMessage(header + "Selected map " + selectedMap + ", map info:");
								sendCurrentTypes(player);
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
								sendTypes(player);
								return true;
							}
							if (selectedMap != null) {
								arg = args[1].toLowerCase();
								if (args.length > 2) {
									String arg2 = args[2].toLowerCase();
									if (arg.equals("join") || arg.equals("jointype")) {
										if (arg2.equals("freejoin")) {
											joinType = "freejoin";
										} else if (arg2.equals("invite")) {
											joinType = "invite";
										} else {
											sendTypes(player);
											return true;
										}
									} else if (arg.equals("gamemode")) {
										if (arg2.equals("none")) {
											gameMode = "none";
										} else if (arg2.equals("tdm")) {
											gameMode = "tdm";
										} else if (arg2.equals("ctf")) {
											if (!config.contains("maps." + selectedMap + ".gamemodevars.redflaglocation") || !config.contains("maps." + selectedMap + ".gamemodevars.blueflaglocation")) {
												player.sendMessage(header + "You need to make red and blue flag locations first!");
												player.sendMessage(header + "Next: Make a red flag location. Type /pvp setpos when there.");
												creation = 6;
												return true;
											}
											gameMode = "ctf";
										} else if (arg2.equals("dth")) {
											if (!config.contains("maps." + selectedMap + ".gamemodevars.dthlocation")) {
												creation = 8;
												player.sendMessage(header + "You need to make a DTH location first!");
												player.sendMessage(header + "Next: Make a dth location. Type /pvp setpos when there.");
												return true;
											}
											gameMode = "dth";
										} else if (arg2.equals("lms")) {
											gameMode = "lms";
										} else if (arg2.equals("redstone")) {
											if (!config.contains("maps." + selectedMap + ".gamemodevars.redredstonelocation") || !config.contains("maps." + selectedMap + ".gamemodevars.blueredstonelocation")) {
												player.sendMessage(header + "You need to make red and blue redstone locations first!");
												player.sendMessage(header + "Next: Make a red redstone location (team red will win if this activates). Type /pvp setpos when there.");
												creation = 11;
												return true;
											}
											gameMode = "redstone";
										} else {
											sendTypes(player);
											return true;
										}
									} else if (arg.equals("friendlyfire")) {
										if (arg2.equals("enabled")) {
											friendlyFire = "enabled";
										} else if (arg2.equals("disabled")) {
											friendlyFire = "disabled";
										} else {
											sendTypes(player);
											return true;
										}
									} else if (arg.equals("deathtypepay")) {
										try {
											deathTypePay = Integer.parseInt(arg2);
										} catch (NumberFormatException e) {
											sendTypes(player);
											return true;
										}
									} else if (arg.equals("deathtypedrop")) {
										if (arg2.equals("enabled")) {
											deathTypeDrop = "enabled";
										} else if (arg2.equals("disabled")) {
											deathTypeDrop = "disabled";
										} else {
											sendTypes(player);
											return true;
										}
									} else if (arg.equals("respawntimer")) {
										try {
											respawnTimer = Integer.parseInt(arg2) * 20;
										} catch (NumberFormatException e) {
											sendTypes(player);
											return true;
										}
									} else if (arg.equals("scorelimit") || arg.equals("score")) {
										try {
											scoreLimit = Integer.parseInt(arg2);
										} catch (NumberFormatException e) {
											sendTypes(player);
											return true;
										}
									} else if (arg.equals("roundlimit") || arg.equals("rounds")) {
										try {
											roundLimit = Integer.parseInt(arg2);
										} catch (NumberFormatException e) {
											sendTypes(player);
											return true;
										}
									} else if (arg.equals("players")) {
										try {
											maxPlayers = Integer.parseInt(arg2);
										} catch (NumberFormatException e) {
											sendTypes(player);
											return true;
										}
									} else if (arg.equals("nick")) {
										maps.put(selectedMap, args[2]);
										mapNick = args[2];
										saveConfig();
									} else if (arg.equals("randomspawnpoints")) {
										if (arg2.equals("enabled")) {
											randomSpawnPoints = "enabled";
										} else if (arg2.equals("disabled")) {
											randomSpawnPoints = "disabled";
										} else {
											sendTypes(player);
											return true;
										}
									} else if (arg.equals("joinmidgame")) {
										if (arg2.equals("enabled")) {
											joinMidGame = "enabled";
										} else if (arg2.equals("disabled")) {
											joinMidGame = "disabled";
										} else {
											sendTypes(player);
											return true;
										}
									} else if (arg.equals("roundtime")) {
										try {
											roundTime = Integer.parseInt(arg2);
										} catch (NumberFormatException e) {
											sendTypes(player);
											return true;
										}
									} else if (arg.equals("outofbounds") || arg.equals("outofboundsarea")) {
										if (arg2.equals("enabled")) {
											if (config.contains("maps." + selectedMap + ".outofboundslocation1") && config.contains("maps." + selectedMap + ".outofboundslocation2")) {
												outOfBoundsArea = "enabled";
											} else {
												player.sendMessage(header + "There are no out of bounds locations yet! Type /pvp change outofbounds to add these!");
												return true;
											}
										} else if (arg2.equals("disabled")) {
											outOfBoundsArea = "disabled";
										} else {
											sendTypes(player);
											return true;
										}
									} else if (arg.equals("outofboundstime")) {
										try {
											outOfBoundsTime = Integer.parseInt(arg2);
										} catch (NumberFormatException e) {
											sendTypes(player);
											return true;
										}
									} else if (arg.equals("autobalance")) {
										if (arg2.equals("enabled")) {
											autoBalance = "enabled";
										} else if (arg2.equals("disabled")) {
											autoBalance = "disabled";
										} else {
											sendTypes(player);
											return true;
										}
									} else {
										sendTypes(player);
										return true;
									}
								} else if (args.length > 1) {
									if (arg.equals("redinventory")) {
										redInv = player.getInventory().getContents();
										removeEmptyInvSlotsRed(redInv);
									} else if (arg.equals("blueinventory")) {
										blueInv = player.getInventory().getContents();
										removeEmptyInvSlotsBlue(blueInv);
									} else if (arg.equals("redarmour")) {
										getArmourRed(player);
									} else if (arg.equals("bluearmour")) {
										getArmourBlue(player);
									} else if (arg.equals("redspawns") | arg.equals("redlocation")) {
										creation = 4;
										player.sendMessage(header + "Type /pvp setpos to add a red spawn location.");
										return true;
									} else if (arg.equals("bluespawns") || arg.equals("bluelocation")) {
										creation = 5;
										player.sendMessage(header + "Type /pvp setpos to add a blue spawn location");
										return true;
									} else if (arg.equals("outofbounds")) {
										creation = 9;
										player.sendMessage(header + "Type /pvp setpos to add a first out of bounds position");
										return true;
									} else {
										sendTypes(player);
										return true;
									}
								}
								sendCurrentTypes(player);
								saveTypes();
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
								if (maps.containsKey(arg)) {
									selectedMap = arg;
									mapNick = maps.get(arg);
									loadTypes();
									try {
										loadItemsRed();
									} catch (NullPointerException e) {
									}
									try {
										loadItemsBlue();
									} catch (NullPointerException e) {
									}
								} else {
									player.sendMessage(header + "Error: That map does not exist. Type /pvp list for all maps.");
									return true;
								}
							}
							if (selectedMap != null) {
								allowJoin = true;
								if (joinType.equals("freejoin")) {
									getServer().broadcastMessage(header + "Type " + ChatColor.AQUA + "/pvp join " + ChatColor.WHITE + "to join map " + mapNick + " (Gamemode: " + gameMode + ")!");
								} else if (joinType.equals("invite")) {
									player.sendMessage(header + "Type /pvp invite [player] to invite players to join " + selectedMap + "!");
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
							if (selectedMap != null) {
								invites.clear();
								isPlaying = true;
								redScore = 0;
								blueScore = 0;
								redFlagTaken = null;
								blueFlagTaken = null;
								teleportBlue("bluelocation");
								sendMessageBlue(header + "You have been teleported to your teams spawn point, let the games begin!");
								teleportRed("redlocation");
								sendMessageRed(header + "You have been teleported to your teams spawn point, let the games begin!");
								player.sendMessage(header + "Players have been teleported to their spawn points, let the games begin!");
								allowJoin = false;
								if (gameMode.equals("dth")) {
									roundTimer();
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
							stop();
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
								player.sendMessage(ChatColor.RED + "Team Red: " + red.toString());
								player.sendMessage(ChatColor.BLUE + "Team Blue: " + blue.toString());
								player.sendMessage(ChatColor.DARK_RED + "Offline In-Game Players:");
								player.sendMessage(ChatColor.RED + "Offline Red: " + offlineRedInGame.toString());
								player.sendMessage(ChatColor.BLUE + "Offline Blue: " + offlineBlueInGame.toString());
								player.sendMessage(ChatColor.DARK_RED + "Offline Stopped Players:");
								player.sendMessage(ChatColor.RED + "Offline Stopped Red: " + offlineRedStopped.toString());
								player.sendMessage(ChatColor.BLUE + "Offline Stopped Blue: " + offlineBlueStopped.toString());
								player.sendMessage(ChatColor.DARK_RED + "Players Out:");
								//player.sendMessage(ChatColor.RED + "Red Out: " + redOut.toString());
								//player.sendMessage(ChatColor.BLUE + "Blue Out: " + blueOut.toString());
								player.sendMessage(ChatColor.DARK_RED + "Spectating Players:");
								player.sendMessage(ChatColor.GRAY + spectate.toString());
								return true;
							} else {
								player.sendMessage(ChatColor.RED + "You do not have access to that command.");
								return true;
							}
						}
					}

					if (arg.equalsIgnoreCase("balance")) {
						if (player.hasPermission("simplepvp.balance") || player.hasPermission("simplepvp.mod")) {
							balanceTeams();
							player.sendMessage(header + "The teams have been balanced!");
							return true;
						}
					}

					if (arg.equalsIgnoreCase("kick")) {
						if (player.hasPermission("simplepvp.kick") || player.hasPermission("simplepvp.mod")) {
							if (args.length > 1) {
								if (leaveGame(args[1]) == true) {
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
								Player invitedPlayer = getServer().getPlayer(args[1]);
								if (!(invitedPlayer == null)) {
									invites.add(invitedPlayer.getName());
									invitedPlayer.sendMessage(header + "You have been invited! Type /pvp join to join map " + selectedMap + " (Gamemode: " + gameMode + ")!");
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
									if (blueScore >= redScore) {
										redScore = blueScore + 1;
									}
									stop();
								} else if (args[1].equalsIgnoreCase("blue")) {
									if (redScore >= blueScore) {
										blueScore = redScore + 1;
									}
									stop();
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

					if (arg.equalsIgnoreCase("join")) {
						if (player.hasPermission("simplepvp.join") || player.hasPermission("simplepvp.player")) {
							if (selectedMap != null) {
								if (!red.contains(player.getName()) && !blue.contains(player.getName()) && !spectate.contains(player.getName())) {
									if (maxPlayers != 0 && red.size() + blue.size() >= maxPlayers) {
										player.sendMessage(header + "Error: This game is full (Player limit: " + maxPlayers + ")!");
										return true;
									}
									if (args.length > 1) {
										arg = args[1];
									} else {
										arg = null;
									}
									if (joinType.equals("freejoin")) {
										player.sendMessage(isPlaying + joinMidGame);
										if (isPlaying == true && joinMidGame.equals("disabled")) {
											player.sendMessage(header + "Error: The game has already started, you'll have to wait for the next game!");
											return true;
										}
										if (checkInventory(player) == true) {
											joinGame(player, arg);
											giveItems(player);
										} else {
											player.sendMessage(header + "Error: Empty your inventory first, make sure you also don't have armor on!");
										}
										if(isPlaying == true){
											respawn(player);
										}
										return true;
									} else if (joinType.equals("invite")) {
										if (invites.contains(player.getName())) {
											if (checkInventory(player) == true) {
												joinGame(player, arg);
												giveItems(player);
												invites.remove(player.getName());
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
							if (leaveGame(player.getName()) == true) {
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
							if (gameMode.equals("tdm")) {
								player.sendMessage(ChatColor.DARK_RED + "TDM Score:");
								player.sendMessage(ChatColor.RED + "Team Red: " + Integer.toString(redScore));
								player.sendMessage(ChatColor.BLUE + "Team Blue: " + Integer.toString(blueScore));
							} else if (gameMode.equals("ctf")) {
								player.sendMessage(ChatColor.DARK_RED + "CTF Score:");
								player.sendMessage(ChatColor.RED + "Team Red: " + Integer.toString(redScore));
								player.sendMessage(ChatColor.BLUE + "Team Blue: " + Integer.toString(blueScore));
							} else if (gameMode.equals("dth")) {
								player.sendMessage(ChatColor.DARK_RED + "DTH Score:");
								player.sendMessage(ChatColor.RED + "Team Red: " + Integer.toString(redScore));
								player.sendMessage(ChatColor.BLUE + "Team Blue: " + Integer.toString(blueScore));
							} else if (gameMode.equals("lms")) {
								player.sendMessage(ChatColor.DARK_RED + "LMS Score:");
								player.sendMessage(ChatColor.RED + "Team Red: " + Integer.toString(redScore));
								player.sendMessage(ChatColor.BLUE + "Team Blue: " + Integer.toString(blueScore));
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
							if (checkInGame(player) != "red" && checkInGame(player) != "blue")
								return false;
							if (args.length > 1) {
								String fullMessage = Joiner.on(" ").join(args);
								fullMessage = fullMessage.replaceFirst("team ", "");
								pvpChat(player, "team", fullMessage);
							} else if (!teamChat.contains(player.getName())) {
								teamChat.add(player.getName());
								allChat.remove(player.getName());
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

					if (arg.equalsIgnoreCase("all")) {//TODO
						if (player.hasPermission("simplepvp.all") || player.hasPermission("simplepvp.player")) {
							if (checkInGame(player) == null)
								return false;
							if (args.length > 1) {
								String fullMessage = Joiner.on(" ").join(args);
								fullMessage = fullMessage.replaceFirst("all ", "");
								pvpChat(player, "all", fullMessage);
							} else if (!allChat.contains(player.getName())) {
								allChat.add(player.getName());
								teamChat.remove(player.getName());
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
							if (allChat.contains(player.getName())) {
								allChat.remove(player.getName());
								player.sendMessage(header + "You left pvp chat and are now speaking in normal chat!");
							} else if (teamChat.contains(player.getName())) {
								teamChat.remove(player.getName());
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
							if (selectedMap == null) {
								player.sendMessage(header + "Error: There is no game to join!");
								return true;
							}
							if (!red.contains(player.getName()) && !blue.contains(player.getName()) && !spectate.contains(player.getName())) {
								if (checkInventory(player) == true) {
									spectate.add(player.getName());
									player.teleport(getConfigLocation(selectedMap, "speclocation"));
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
							sendCurrentTypes(player);
							return true;
						}
					}
					if (arg.equalsIgnoreCase("flag")) {
						if (player.hasPermission("simplepvp.flag") || player.hasPermission("simplepvp.player")) {
							if (!gameMode.equals("ctf")) {
								player.sendMessage(header + "Error: The gamemode is not ctf!");
								return true;
							}
							if (blueFlagTaken != null) {
								player.sendMessage(header + "Blue flag: " + blueFlagTaken);
							} else {
								player.sendMessage(header + "Blue flag not taken");
							}
							if (redFlagTaken != null) {
								player.sendMessage(header + "Red flag: " + redFlagTaken);
							} else {
								player.sendMessage(header + "Red flag not taken");
							}
							return true;
						} else {
							player.sendMessage(ChatColor.RED + "You do not have access to that command.");
							return true;
						}
					}
					if (arg.equalsIgnoreCase("debug")) {
						if (player.hasPermission("simplepvp.admin")) {
							if (blueFlagTaken != null) {
								player.sendMessage(header + "Blue flag: " + blueFlagTaken);
							} else {
								player.sendMessage(header + "Blue flag not taken");
							}
							if (redFlagTaken != null) {
								player.sendMessage(header + "Red flag: " + redFlagTaken);
							} else {
								player.sendMessage(header + "Red flag not taken");
							}
							player.sendMessage(maps.toString());
							player.sendMessage("Is playing: " + isPlaying);
							return true;
						}
					}
					if (arg.equalsIgnoreCase("hide")) {
						if (player.hasPermission("simplepvp.admin")) {
							player.hidePlayer(player);
						}
					}
					if (arg.equalsIgnoreCase("updatemaxplayers")) {
						if (player.hasPermission("simplepvp.admin")) {
							player.sendMessage(header + "Changing config for maxplayers...");
							Set<String> mapNamesFromConfig = ((ConfigurationSection) config.get("maps")).getKeys(false);
							player.sendMessage(header + mapNamesFromConfig.toString());
							Iterator<String> mapNamesItr = mapNamesFromConfig.iterator();
							while (mapNamesItr.hasNext()) {
								String tempMapName = mapNamesItr.next().toString();
								if (config.contains("maps." + tempMapName + ".maxPlayers")) {
									config.set("maps." + tempMapName + ".maxplayers", config.get("maps." + tempMapName + ".maxPlayers"));
									config.set("maps." + tempMapName + ".maxPlayers", null);
								}
							}
							saveConfig();
							return true;
						}
					}

					if (arg.equalsIgnoreCase("updatetypes")) {
						if (player.hasPermission("simplepvp.admin")) {
							player.sendMessage(header + "Nothing to update...");
							Set<String> mapNamesFromConfig = ((ConfigurationSection) config.get("maps")).getKeys(false);
							player.sendMessage(header + mapNamesFromConfig.toString());
							Iterator<String> mapNamesItr = mapNamesFromConfig.iterator();
							while (mapNamesItr.hasNext()) {
								String tempMapName = mapNamesItr.next().toString();
								if (!config.contains("maps." + tempMapName + ".forcepvpchat")) {
									config.set("maps." + tempMapName + ".forcepvpchat", "enabled");
								}
								if (!config.contains("maps." + tempMapName + ".outofboundsarea")) {
									config.set("maps." + tempMapName + ".outofboundsarea", "disabled");
								}
							}
							saveConfig();
							return true;
						}
					}

					if (arg.equalsIgnoreCase("reload")) {
						if (player.hasPermission("simplepvp.admin")) {
							Bukkit.getPluginManager().disablePlugin(this);
							Bukkit.getPluginManager().enablePlugin(this);
							player.sendMessage(ChatColor.GRAY + "SimplePvP has been reloaded...");
							return true;
						}
					}
				}
			} else {
				System.out.println("[SimplePvP] This may only be used by in-game players!");
			}
		}
		return false;
	}
}
