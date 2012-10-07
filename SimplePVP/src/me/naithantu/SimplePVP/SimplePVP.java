package me.naithantu.SimplePVP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import me.naithantu.SimplePVP.commands.AdminCommands;
import me.naithantu.SimplePVP.commands.ModCommands;
import me.naithantu.SimplePVP.commands.PlayerCommands;
import me.naithantu.SimplePVP.listeners.ChatListener;
import me.naithantu.SimplePVP.listeners.DamageListener;
import me.naithantu.SimplePVP.listeners.DeathListener;
import me.naithantu.SimplePVP.listeners.JoinListener;
import me.naithantu.SimplePVP.listeners.MoveListener;
import me.naithantu.SimplePVP.listeners.QuitListener;
import me.naithantu.SimplePVP.listeners.RedstoneListener;
import me.naithantu.SimplePVP.listeners.RespawnListener;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class SimplePVP extends JavaPlugin {

	PlayerCommands playerCommandHandler = new PlayerCommands(this);
	ModCommands modCommandHandler = new ModCommands(this);
	AdminCommands adminCommandHandler = new AdminCommands(this);

	HashMap<String, PlayerScore> playerScores = new HashMap<String, PlayerScore>();

	Settings settings;
	public Configuration config;
	public static Economy econ = null;
	PluginManager pm;
	public final ChatListener chatListener = new ChatListener(this);
	public final DamageListener damageListener = new DamageListener(this);
	public final DeathListener deathListener = new DeathListener(this);
	public final JoinListener joinListener = new JoinListener(this);
	public final MoveListener moveListener = new MoveListener(this);
	public final QuitListener quitListener = new QuitListener(this);
	public final RedstoneListener redstoneListener = new RedstoneListener(this);
	public final RespawnListener respawnListener = new RespawnListener(this);

	String header = ChatColor.DARK_RED + "[PvP] " + ChatColor.WHITE + "";

	// /leave Variables
	public HashSet<String> pvpWorld = new HashSet<String>();
	public HashSet<String> pvpTimer = new HashSet<String>();

	// Team Variables
	List<String> blue = new ArrayList<String>();
	List<String> red = new ArrayList<String>();
	public List<String> allChat = new ArrayList<String>();
	public List<String> teamChat = new ArrayList<String>();
	List<String> spectate = new ArrayList<String>();
	List<String> offlineRed = new ArrayList<String>();
	List<String> offlineBlue = new ArrayList<String>();
	List<String> playersRespawningRed = new ArrayList<String>();
	List<String> playersRespawningBlue = new ArrayList<String>();
	List<String> deadAfterGame = new ArrayList<String>();

	// Map info
	//Set<String> mapNames = new HashSet<String>();
	HashMap<String, String> maps = new HashMap<String, String>();

	// Temporary Variables
	boolean betweenRounds = false;
	int round;
	String mapName;
	String mapNick;
	public HashSet<String> invites = new HashSet<String>();
	ArrayList<String> tempArrayList = new ArrayList<String>();
	int redSpawnLocations = 0;
	int blueSpawnLocations = 0;
	int roundTimer;
	boolean updatedTimer;
	HashMap<String, Integer> respawnTimers = new HashMap<String, Integer>();

	public ItemStack[] redInv;
	public ItemStack[] blueInv;

	// Gamemode Variables
	public int redScore = 0;
	public int blueScore = 0;
	// CTF
	public String redFlagTaken;
	public String blueFlagTaken;
	String attacking = "red";
	public int creation = 0;

	public Settings getSettings() {
		return settings;
	}

	//Getters and setters
	public String getHeader() {
		return header;
	}

	public HashMap<String, PlayerScore> getPlayerScores() {
		return playerScores;
	}

	public HashMap<String, String> getMaps() {
		return maps;
	}

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
	public List<String> getBlue() {
		return blue;
	}
	public List<String> getRed() {
		return red;
	}
	public int getRound() {
		return round;
	}
	/*
	 * public List<ItemStack> getRedArmour() { return redArmour; } public
	 * List<ItemStack> getBlueArmour() { return blueArmour; }
	 */
	public List<String> getOfflineRed() {
		return offlineRed;
	}
	public List<String> getOfflineBlue() {
		return offlineBlue;
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

	public void setRed(List<String> red) {
		this.red = red;
	}
	public void setBlue(List<String> blue) {
		this.blue = blue;
	}
	public boolean getBetweenRounds() {
		return betweenRounds;
	}
	public HashMap<String, Integer> getRespawnTimers() {
		return respawnTimers;
	}
	public void setRespawnTimers(HashMap<String, Integer> respawnTimers) {
		this.respawnTimers = respawnTimers;
	}

	public void onEnable() {
		config = this.getConfig();
		settings = new Settings(this);
		loadMapNames();
		setupEconomy();
		pm = getServer().getPluginManager();
		pm.registerEvents(this.chatListener, this);
		pm.registerEvents(this.damageListener, this);
		pm.registerEvents(this.deathListener, this);
		pm.registerEvents(this.joinListener, this);
		pm.registerEvents(this.moveListener, this);
		pm.registerEvents(this.quitListener, this);
		pm.registerEvents(this.redstoneListener, this);
		pm.registerEvents(this.respawnListener, this);
		settings.resetSettings();
	}

	public void onDisable() {
		saveMapNames();
		saveConfig();
	}

	public Integer respawnTimer(final Player player, PlayerRespawnEvent event) {
		String selectedMap = settings.getStringSetting("selectedMap");
		Integer id = null;
		// Give items back.
		if (settings.getBooleanSetting("deathTypeDrop") == false) {
			giveItems(player);
		}

		//Respawn to speclocation if the game is not playing.
		if (settings.getBooleanSetting("isPlaying") == false || getBetweenRounds() == true) {
			if (event == null) {
				player.teleport(getConfigLocation(selectedMap, "speclocation"));
			} else {
				event.setRespawnLocation(getConfigLocation(selectedMap, "speclocation"));
			}
			return null;
		}

		//Respawn to speclocation if the gamemode is lms.
		if (settings.getStringSetting("gameMode").equals("lms")) {
			if (red.contains(player.getName())) {
				getPlayersRespawningRed().add(player.getName());
				player.sendMessage(header + "You will respawn next round!");
			} else if (blue.contains(player.getName())) {
				getPlayersRespawningBlue().add(player.getName());
				player.sendMessage(header + "You will respawn next round!");
			}
			if (event == null) {
				player.teleport(getConfigLocation(selectedMap, "speclocation"));
			} else {
				event.setRespawnLocation(getConfigLocation(selectedMap, "speclocation"));
			}
			return null;
		}

		if (settings.getIntSetting("respawnTimer") == 0) {
			if (red.contains(player.getName()) || blue.contains(player.getName())) {
				if (event == null) {
					player.teleport(respawn(player));
				} else {
					event.setRespawnLocation(respawn(player));
				}
			}
		} else {
			// Respawn with respawntimer.
			if (event == null) {
				player.teleport(getConfigLocation(selectedMap, "speclocation"));
			} else {
				event.setRespawnLocation(getConfigLocation(selectedMap, "speclocation"));
			}
			id = respawnScheduler(player);
		}
		return id;
	}

	public Integer respawnScheduler(final Player player) {
		if (red.contains(player.getName())) {
			playersRespawningRed.add(player.getName());
		} else if (blue.contains(player.getName())) {
			playersRespawningBlue.add(player.getName());
		}
		player.sendMessage(ChatColor.DARK_RED + "[PvP] " + ChatColor.WHITE + "You will respawn in " + settings.getIntSetting("respawnTimer") / 20 + " seconds!");
		Integer id = getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			public void run() {
				if (red.contains(player.getName())) {
					Player respawnedPlayer = getServer().getPlayer(getPlayersRespawningRed().get(0));
					playersRespawningRed.remove(0);
					setPlayersRespawningRed(playersRespawningRed);
					respawnedPlayer.teleport(respawn(respawnedPlayer));
				} else if (blue.contains(player.getName())) {
					Player respawnedPlayer = getServer().getPlayer(getPlayersRespawningBlue().get(0));
					respawnedPlayer.teleport(respawn(respawnedPlayer));
					playersRespawningBlue.remove(0);
					setPlayersRespawningBlue(playersRespawningBlue);
				}
				HashMap<String, Integer> respawnTimers = getRespawnTimers();
				respawnTimers.remove(player.getName());
				setRespawnTimers(respawnTimers);
			}
		}, settings.getIntSetting("respawnTimer"));
		return id;
	}

	public ItemStack getHelmet(String team) {
		ItemStack helmet = null;
		if (team.equals("blue")) {
			List<String> tempBlueArmour = config.getStringList("maps." + settings.getStringSetting("selectedMap") + ".bluearmour");
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
			List<String> tempRedArmour = config.getStringList("maps." + settings.getStringSetting("selectedMap") + ".redarmour");
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
		if (settings.getIntSetting("roundTime") == 0) {
			return;
		}
		roundTimer = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			Integer timeLeft = settings.getIntSetting("roundtime");

			public void run() {
				timeLeft--;
				if (timeLeft == 0) {
					if (settings.getStringSetting("gameMode").equals("dth")) {
						sendMessageAll(header + "Time's up! The blue team has defended the hill!");
						blueScore++;
						if (blueScore >= settings.getIntSetting("scoreLimit")) {
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
		teamChat.clear();
		allChat.clear();
		emptyAllInventories();
		offlineRed.clear();
		offlineBlue.clear();
		spectate.clear();
		red.clear();
		blue.clear();
		resetTypes();
		settings.setBooleanSetting("isPlaying", false);
	}
	public void nextRound() {
		betweenRounds = true;
		round++;
		teleportRed("speclocation");
		teleportBlue("speclocation");
		emptyAllInventories();
		sendMessageAll(header + "The next round will start in " + settings.getIntSetting("betweenRoundTime") + " seconds!");
		getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			public void run() {
				betweenRounds = false;
				ArrayList<String> tempList = new ArrayList<String>();
				//Re-add players to teams if they have respawned during the waiting time in LMS.
				tempList = (ArrayList<String>) red;
				for (String playerName : tempList) {
					getServer().broadcastMessage(playerName);
					if (getServer().getPlayer(playerName).isDead()) {
						red.remove(playerName);
						playersRespawningRed.add(playerName);
						getServer().getPlayer(playerName).sendMessage(header + "You have not respawned in time! You will join the game next round!");
					}
				}
				tempList = (ArrayList<String>) playersRespawningBlue;
				for (String playerName : tempList) {
					getServer().broadcastMessage(playerName);
					if (getServer().getPlayer(playerName).isDead()) {
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
		}, settings.getIntSetting("betweenRoundTime") * 20);
	}
	public void givePoint(int team) {
		if (settings.getStringSetting("gameMode").equals("redstone")) {
			sendMessageAll(ChatColor.DARK_RED + "[PvP] " + ChatColor.WHITE + "Team blue activated the redstone!");
			sendRedstoneScore();
		}
		if (team == 1) {
			redScore++;
			if (redScore >= settings.getIntSetting("scoreLimit") && settings.getIntSetting("scoreLimit") != 0) {
				stop();
			}
		} else if (team == 2) {
			blueScore++;
			if (blueScore >= settings.getIntSetting("scoreLimit") && settings.getIntSetting("scoreLimit") != 0) {
				stop();
			}
		}
	}
	//Inventory methods

	public void loadItemsRed() {
		List<String> configItems = config.getStringList("maps." + settings.getStringSetting("selectedMap") + ".redinv");
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
			config.set("maps." + settings.getStringSetting("selectedMap") + ".redinv", null);
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
		config.set("maps." + settings.getStringSetting("selectedMap") + ".redinv", configItemList);
		saveConfig();
	}
	public void loadItemsBlue() {
		List<String> configItems = config.getStringList("maps." + settings.getStringSetting("selectedMap") + ".blueinv");
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
			config.set("maps." + settings.getStringSetting("selectedMap") + ".blueinv", null);
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
		config.set("maps." + settings.getStringSetting("selectedMap") + ".blueinv", configItemList);
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
			List<String> tempRedArmour = config.getStringList("maps." + settings.getStringSetting("selectedMap") + ".redarmour");
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
			List<String> tempBlueArmour = config.getStringList("maps." + settings.getStringSetting("selectedMap") + ".bluearmour");
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
		config.set("maps." + settings.getStringSetting("selectedMap") + ".redarmour", null);
		saveArmour(player.getInventory().getItem(0), "helmet", "red");
		saveArmour(player.getInventory().getItem(1), "chest", "red");
		saveArmour(player.getInventory().getItem(2), "leggings", "red");
		saveArmour(player.getInventory().getItem(3), "boots", "red");
		saveConfig();
	}
	public void getArmourBlue(Player player) {
		config.set("maps." + settings.getStringSetting("selectedMap") + ".bluearmour", null);
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
			List<String> tempArmour = config.getStringList("maps." + settings.getStringSetting("selectedMap") + "." + team + "armour");
			tempArmour.add(armourType + ":" + type + ":" + amount + ":" + data + ":" + tempArrayList);
			config.set("maps." + settings.getStringSetting("selectedMap") + "." + team + "armour", tempArmour);
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
		if (settings.getStringSetting("gameMode").equals("dth") && attacking.equals("blue")) {
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
			List<String> tempBlueArmour = config.getStringList("maps." + settings.getStringSetting("selectedMap") + ".bluearmour");
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
			List<String> tempRedArmour = config.getStringList("maps." + settings.getStringSetting("selectedMap") + ".redarmour");
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
				config.set("maps." + settings.getStringSetting("selectedMap") + ".bluearmour", null);
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
		config.set("maps." + settings.getStringSetting("selectedMap") + ".bluearmour", configItemList);
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

	//Type methods
	public void loadTypes() {
		String selectedMap = settings.getStringSetting("selectedMap");
		settings.loadSettings();
		if (config.contains("maps." + selectedMap + ".outofboundslocation1") && config.contains("maps." + selectedMap + ".outofboundslocation2")) {
			adminCommandHandler.setOutOfBoundsLocation(getConfigLocation(selectedMap, "outofboundslocation1"), 1);
			adminCommandHandler.setOutOfBoundsLocation(getConfigLocation(selectedMap, "outofboundslocation2"), 2);
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
		String selectedMap = settings.getStringSetting("selectedMap");
		settings.saveSettings();
		if (adminCommandHandler.getOutOfBoundsLocation(1) != null && adminCommandHandler.getOutOfBoundsLocation(2) != null) {
			setConfigLocation("maps." + selectedMap + ".outofboundslocation1", adminCommandHandler.getOutOfBoundsLocation(1));
			setConfigLocation("maps." + selectedMap + ".outofboundslocation2", adminCommandHandler.getOutOfBoundsLocation(2));
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
		player.sendMessage(header + "Red spawn locations: " + redSpawnLocations);
		player.sendMessage(header + "Blue spawn locations: " + blueSpawnLocations);
		settings.sendTypes(player);
	}

	public void resetTypes() {
		settings.resetSettings();
		redScore = 0;
		blueScore = 0;
		playersRespawningRed.clear();
		playersRespawningBlue.clear();
		redSpawnLocations = 0;
		blueSpawnLocations = 0;
		redInv = null;
		blueInv = null;
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
		String selectedMap = settings.getStringSetting("selectedMap");
		Location location = null;

		if (blue.contains(player.getName())) {
			if (settings.getBooleanSetting("randomSpawnPoints") == true) {
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
			if (settings.getBooleanSetting("randomSpawnPoints") == true) {
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
		String selectedMap = settings.getStringSetting("selectedMap");
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
			} else if (locationName.equals("redlocation") && settings.getBooleanSetting("randomSpawnPoints") == true) {
				int randLocation = 0;
				if (redSpawnLocations > 1) {
					Random random = new Random();
					randLocation = random.nextInt(redSpawnLocations);
				}
				location = getConfigLocation(selectedMap, locationName + randLocation);
			} else {
				location = getConfigLocation(selectedMap, locationName + "0");
			}
			try {
				player.teleport(location);
			} catch (NullPointerException e) {
			}
		}
	}
	public void teleportBlue(String locationName) {
		String selectedMap = settings.getStringSetting("selectedMap");
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
			} else if (locationName.equals("bluelocation") && settings.getBooleanSetting("randomSpawnPoints") == true) {
				int randLocation = 0;
				if (blueSpawnLocations > 1) {
					Random random = new Random();
					randLocation = random.nextInt(blueSpawnLocations);
				}
				location = getConfigLocation(selectedMap, locationName + randLocation);
			} else {
				location = getConfigLocation(selectedMap, locationName + "0");
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
			location = getConfigLocation(settings.getStringSetting("selectedMap"), locationName);
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
				player.teleport(getConfigLocation(settings.getStringSetting("selectedMap"), "speclocation"));
				sendMessageAll(header + ChatColor.BLUE + player.getName() + ChatColor.WHITE + " joined the game!");
				blue.add(player.getName());
				player.sendMessage(header + "You joined team " + ChatColor.BLUE + "blue" + "!");
				player.sendMessage(header + "You will be teleported to your teams spawn point as soon as the match starts!");
			} else if (team.equalsIgnoreCase("red")) {
				player.teleport(getConfigLocation(settings.getStringSetting("selectedMap"), "speclocation"));
				sendMessageAll(header + ChatColor.RED + player.getName() + ChatColor.WHITE + " joined the game!");
				red.add(player.getName());
				player.sendMessage(header + "You joined team " + ChatColor.RED + "red" + "!");
				player.sendMessage(header + "You will be teleported to your teams spawn point as soon as the match starts!");
			} else {
				player.sendMessage(header + "Error: That team does not exist. Use /pvp join to auto-join a team!");
			}
		} else {
			if (blue.size() >= red.size()) {
				player.teleport(getConfigLocation(settings.getStringSetting("selectedMap"), "speclocation"));
				sendMessageAll(header + ChatColor.RED + player.getName() + ChatColor.WHITE + " joined the game!");
				red.add(player.getName());
				player.sendMessage(header + "You joined team " + ChatColor.RED + "red" + "!");
				player.sendMessage(header + "You will be teleported to your teams spawn point as soon as the match starts!");
			} else {
				player.teleport(getConfigLocation(settings.getStringSetting("selectedMap"), "speclocation"));
				sendMessageAll(header + ChatColor.BLUE + player.getName() + ChatColor.WHITE + " joined the game!");
				blue.add(player.getName());
				player.sendMessage(header + "You joined team " + ChatColor.BLUE + "blue" + "!");
				player.sendMessage(header + "You will be teleported to your teams spawn point as soon as the match starts!");
			}
		}

		//Force pvp join if enabled
		if (settings.getBooleanSetting("forcePvpChat") == true) {
			allChat.add(player.getName());
			player.sendMessage(header + "You joined the all chat channel!");
		}

		//Add to PlayerScore.
		playerScores.put(player.getName(), new PlayerScore(player.getName()));
	}
	public void logOffMidGame(Player player) {
		String playerName = player.getName();
		World world = getServer().getWorld("world");
		player.teleport(world.getSpawnLocation());
		emptyInventory(player);
		if (red.contains(playerName)) {
			offlineRed.add(playerName);
			red.remove(playerName);
		} else if (blue.contains(playerName)) {
			offlineBlue.add(playerName);
			blue.remove(playerName);
		} else if (playersRespawningRed.contains(playerName)) {
			playersRespawningRed.remove(playerName);
		} else if (playersRespawningBlue.contains(playerName)) {
			playersRespawningBlue.remove(playerName);
		}
	}

	public boolean leaveGame(String playerName) {
		Player player = getServer().getPlayer(playerName);
		if (player != null) {
			if (settings.getStringSetting("gameMode").equals("ctf")) {
				if (redFlagTaken != null) {
					if (redFlagTaken.equals(playerName)) {
						setRedFlagTaken(null);
						sendMessageAll(header + "The red flag has been returned!");
					}
				} else if (blueFlagTaken != null) {
					if (blueFlagTaken.equals(playerName)) {
						setBlueFlagTaken(null);
						sendMessageAll(header + "The blue flag has been returned!");
					}
				}
			}
			if (red.contains(playerName)) {
				red.remove(playerName);
				sendMessageAll(header + ChatColor.RED + playerName + ChatColor.WHITE + " left the game!");
			} else if (blue.contains(playerName)) {
				blue.remove(playerName);
				sendMessageAll(header + ChatColor.BLUE + playerName + ChatColor.WHITE + " left the game!");
			} else if (spectate.contains(playerName)) {
				spectate.remove(playerName);
			} else {
				return false;
			}
			emptyInventory(player);
			World world = getServer().getWorld("world");
			player.teleport(world.getSpawnLocation());
		} else if (offlineRed.contains(playerName)) {
			offlineRed.remove(playerName);
		} else if (offlineBlue.contains(playerName)) {
			offlineBlue.remove(playerName);
		} else {
			return false;
		}
		if (settings.getBooleanSetting("autoBalance") == true) {
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

	public Boolean checkInGame(Player player) {
		String playerName = player.getName();
		if (blue.contains(playerName)) {
			return true;
		} else if (red.contains(playerName)) {
			return true;
		} else if (spectate.contains(playerName)) {
			return true;
		} else if (playersRespawningRed.contains(playerName)) {
			return true;
		} else if (playersRespawningBlue.contains(playerName)) {
			return true;
		}
		return false;
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
				if (args.length > 0) {
					String arg = args[0];
					String[] playerCommands = { "join", "leave", "teams", "score", "team", "all", "chat", "spectate", "info", "flag" };
					String[] modCommands = { "list", "select", "change", "allowjoin", "start", "stop", "allplayers", "balance", "kick", "invite", "win" };
					String[] adminCommands = { "create", "abort", "setpos", "done", "remove", "tp" };
					for (String command : playerCommands) {
						if (arg.equalsIgnoreCase(command))
							return playerCommandHandler.playerCommandHandler(sender, cmd, commandLabel, args);
					}
					for (String command : modCommands) {
						if (arg.equalsIgnoreCase(command))
							return modCommandHandler.modCommandHandler(sender, cmd, commandLabel, args);
					}
					for (String command : adminCommands) {
						if (arg.equalsIgnoreCase(command))
							return adminCommandHandler.adminCommandHandler(sender, cmd, commandLabel, args);
					}

					Player player = (Player) sender;
					if (arg.equalsIgnoreCase("getscore")) {
						if (!player.hasPermission("simplepvp.admin")) {
							return false;
						}
						player.sendMessage(header + "Current kills & deaths:");
						for (String playerName : red) {
							player.sendMessage(header + ChatColor.RED + playerName + ChatColor.WHITE + "| Kills: " + playerScores.get(playerName).getKills() + " | Deaths: "
									+ playerScores.get(playerName).getDeaths());
						}
						for (String playerName : blue) {
							player.sendMessage(header + ChatColor.BLUE + playerName + ChatColor.WHITE + "| Kills: " + playerScores.get(playerName).getKills() + " | Deaths: "
									+ playerScores.get(playerName).getDeaths());
						}
						return true;
					}
					/*
					 * if (arg.equalsIgnoreCase("team")) { if
					 * (player.hasPermission("simplepvp.team") ||
					 * player.hasPermission("simplepvp.player")) { if
					 * (!red.contains(player.getName()) &&
					 * !blue.contains(player.getName())) return false; if
					 * (args.length > 1) { String fullMessage =
					 * Joiner.on(" ").join(args); fullMessage =
					 * fullMessage.replaceFirst("team ", ""); pvpChat(player,
					 * "team", fullMessage); } else if
					 * (!teamChat.contains(player.getName())) {
					 * teamChat.add(player.getName());
					 * allChat.remove(player.getName());
					 * player.sendMessage(header +
					 * "You joined the team chat channel!"); } else {
					 * player.sendMessage(header +
					 * "Error: You are already in the team chat channel. Just type in chat to say something in this channel!"
					 * ); } return true; } else {
					 * player.sendMessage(ChatColor.RED +
					 * "You do not have access to that command."); return true;
					 * } }
					 * 
					 * if (arg.equalsIgnoreCase("all")) { if
					 * (player.hasPermission("simplepvp.all") ||
					 * player.hasPermission("simplepvp.player")) { if
					 * (checkInGame(player) == null) return false; if
					 * (args.length > 1) { String fullMessage =
					 * Joiner.on(" ").join(args); fullMessage =
					 * fullMessage.replaceFirst("all ", ""); pvpChat(player,
					 * "all", fullMessage); } else if
					 * (!allChat.contains(player.getName())) {
					 * allChat.add(player.getName());
					 * teamChat.remove(player.getName());
					 * player.sendMessage(header +
					 * "You joined the all chat channel!"); } else {
					 * player.sendMessage(header +
					 * "Error: You are already in the all chat channel. Just type in chat to say something in this channel!"
					 * ); } return true; } else {
					 * player.sendMessage(ChatColor.RED +
					 * "You do not have access to that command."); return true;
					 * } }
					 * 
					 * if (arg.equalsIgnoreCase("chat")) { if
					 * (player.hasPermission("simplepvp.player") ||
					 * player.hasPermission("simplepvp.chat")) { if
					 * (allChat.contains(player.getName())) {
					 * allChat.remove(player.getName());
					 * player.sendMessage(header +
					 * "You left pvp chat and are now speaking in normal chat!"
					 * ); } else if (teamChat.contains(player.getName())) {
					 * teamChat.remove(player.getName());
					 * player.sendMessage(header +
					 * "You left pvp chat and are now speaking in normal chat!"
					 * ); } else { player.sendMessage(header +
					 * "Error: You were not in pvp chat."); } return true; }
					 * else { player.sendMessage(ChatColor.RED +
					 * "You do not have access to that command."); } }
					 * 
					 * if (arg.equalsIgnoreCase("spectate")) { if
					 * (player.hasPermission("simplepvp.spectate") ||
					 * player.hasPermission("simplepvp.player")) { if
					 * (selectedMap == null) { player.sendMessage(header +
					 * "Error: There is no game to join!"); return true; } if
					 * (!red.contains(player.getName()) &&
					 * !blue.contains(player.getName()) &&
					 * !spectate.contains(player.getName())) { if
					 * (checkInventory(player) == true) {
					 * spectate.add(player.getName());
					 * player.teleport(getConfigLocation(selectedMap,
					 * "speclocation")); player.sendMessage(header +
					 * "You are now spectating! Enjoy!"); } else {
					 * player.sendMessage(header +
					 * "Error: Emtpy your inventory first, make sure you also don't have armor on!"
					 * ); } } else { player.sendMessage(header +
					 * "Error: You are already in a team! Type /pvp leave to leave if you want to join a different team!"
					 * ); } } else { player.sendMessage(ChatColor.RED +
					 * "You do not have access to that command."); } return
					 * true; } if (arg.equalsIgnoreCase("info")) { if
					 * (player.hasPermission("simplepvp.player")) {
					 * sendCurrentTypes(player); return true; } } if
					 * (arg.equalsIgnoreCase("flag")) { if
					 * (player.hasPermission("simplepvp.flag") ||
					 * player.hasPermission("simplepvp.player")) { if
					 * (!settings.getStringSetting("gameMode").equals("ctf")) {
					 * player.sendMessage(header +
					 * "Error: The gamemode is not ctf!"); return true; } if
					 * (blueFlagTaken != null) { player.sendMessage(header +
					 * "Blue flag: " + blueFlagTaken); } else {
					 * player.sendMessage(header + "Blue flag not taken"); } if
					 * (redFlagTaken != null) { player.sendMessage(header +
					 * "Red flag: " + redFlagTaken); } else {
					 * player.sendMessage(header + "Red flag not taken"); }
					 * return true; } else { player.sendMessage(ChatColor.RED +
					 * "You do not have access to that command."); return true;
					 * } } if (arg.equalsIgnoreCase("debug")) { if
					 * (player.hasPermission("simplepvp.admin")) {
					 * player.sendMessage("Red respawning:");
					 * player.sendMessage(playersRespawningRed.toString());
					 * player.sendMessage("Blue respawning:");
					 * player.sendMessage(playersRespawningBlue.toString());
					 * player.sendMessage("Is playing: " + isPlaying); return
					 * true; } }
					 * 
					 * if (arg.equalsIgnoreCase("updatemaxplayers")) { if
					 * (player.hasPermission("simplepvp.admin")) {
					 * player.sendMessage(header +
					 * "Changing config for maxplayers..."); Set<String>
					 * mapNamesFromConfig = ((ConfigurationSection)
					 * config.get("maps")).getKeys(false);
					 * player.sendMessage(header +
					 * mapNamesFromConfig.toString()); Iterator<String>
					 * mapNamesItr = mapNamesFromConfig.iterator(); while
					 * (mapNamesItr.hasNext()) { String tempMapName =
					 * mapNamesItr.next().toString(); if
					 * (config.contains("maps." + tempMapName + ".maxPlayers"))
					 * { config.set("maps." + tempMapName + ".maxplayers",
					 * config.get("maps." + tempMapName + ".maxPlayers"));
					 * config.set("maps." + tempMapName + ".maxPlayers", null);
					 * } } saveConfig(); return true; } }
					 * 
					 * if (arg.equalsIgnoreCase("updatetypes")) { if
					 * (player.hasPermission("simplepvp.admin")) {
					 * player.sendMessage(header + "Nothing to update...");
					 * Set<String> mapNamesFromConfig = ((ConfigurationSection)
					 * config.get("maps")).getKeys(false);
					 * player.sendMessage(header +
					 * mapNamesFromConfig.toString()); Iterator<String>
					 * mapNamesItr = mapNamesFromConfig.iterator(); while
					 * (mapNamesItr.hasNext()) { String tempMapName =
					 * mapNamesItr.next().toString(); if
					 * (!config.contains("maps." + tempMapName +
					 * ".forcepvpchat")) { config.set("maps." + tempMapName +
					 * ".forcepvpchat", "enabled"); } if
					 * (!config.contains("maps." + tempMapName +
					 * ".outofboundsarea")) { config.set("maps." + tempMapName +
					 * ".outofboundsarea", "disabled"); } } saveConfig(); return
					 * true; } }
					 * 
					 * if (arg.equalsIgnoreCase("reload")) { if
					 * (player.hasPermission("simplepvp.admin")) {
					 * Bukkit.getPluginManager().disablePlugin(this);
					 * Bukkit.getPluginManager().enablePlugin(this);
					 * player.sendMessage(ChatColor.GRAY +
					 * "SimplePvP has been reloaded..."); return true; } }
					 * if(arg.equalsIgnoreCase("purgeplayers")){
					 * if(player.hasPermission("simplepvp.admin")){
					 * ArrayList<String> purgeList = new ArrayList<String>();
					 * for(String playerName: red){
					 * if(getServer().getPlayer(playerName) == null){
					 * purgeList.add(playerName); player.sendMessage("Removing "
					 * + playerName + " from team red!"); } }
					 * red.removeAll(purgeList); purgeList.clear(); for(String
					 * playerName: blue){ if(getServer().getPlayer(playerName)
					 * == null){ purgeList.add(playerName);
					 * player.sendMessage("Removing " + playerName +
					 * " from team blue!"); } } blue.removeAll(purgeList);
					 * purgeList.clear(); for(String playerName: spectate){
					 * if(getServer().getPlayer(playerName) == null){
					 * purgeList.add(playerName); player.sendMessage("Removing "
					 * + playerName + " from team spectate!"); } }
					 * spectate.removeAll(purgeList); purgeList.clear();
					 * for(String playerName: playersRespawningRed){
					 * if(getServer().getPlayer(playerName) == null){
					 * purgeList.add(playerName); player.sendMessage("Removing "
					 * + playerName + " from respawning red!"); } }
					 * playersRespawningRed.removeAll(purgeList);
					 * purgeList.clear(); for(String playerName:
					 * playersRespawningBlue){
					 * if(getServer().getPlayer(playerName) == null){
					 * purgeList.add(playerName); player.sendMessage("Removing "
					 * + playerName + " from respawning blue!"); } }
					 * playersRespawningBlue.removeAll(purgeList);
					 * purgeList.clear();
					 * player.sendMessage("Purge complete..."); return true; } }
					 */
				}

			} else {
				System.out.println("[SimplePvP] This may only be used by in-game players!");
			}
		}
		return false;
	}
}
