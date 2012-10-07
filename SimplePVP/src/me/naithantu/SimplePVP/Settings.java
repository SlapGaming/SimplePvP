package me.naithantu.SimplePVP;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Settings {
	Plugin plugin;
	private HashMap<String, Integer> intSettings = new HashMap<String, Integer>();
	private HashMap<String, Boolean> booleanSettings = new HashMap<String, Boolean>();
	private HashMap<String, String> stringSettings = new HashMap<String, String>();

	public Settings(SimplePVP instance) {
		plugin = instance;
		resetSettings();
	}

	private void resetIntSettings() {
		//Changeable settings.
		intSettings.put("respawnTimer", 0);
		intSettings.put("deathTypePay", 0);
		intSettings.put("scoreLimit", 0);
		intSettings.put("maxPlayers", 0);
		intSettings.put("roundTime", 0);
		intSettings.put("outOfBoundsTime", 5);
		intSettings.put("roundLimit", 1);
		intSettings.put("betweenRoundTime", 10);
	}

	private void resetBooleanSettings() {
		//Changeable settings.
		booleanSettings.put("friendlyFire", false);
		booleanSettings.put("deathTypeDrop", false);
		booleanSettings.put("randomSpawnPoints", true);
		booleanSettings.put("joinMidGame", false);
		booleanSettings.put("forcePvpChat", true);
		booleanSettings.put("outOfBoundsArea", false);
		booleanSettings.put("autoBalance", true);
		//Extra map info.
		//booleanSettings.put("allowJoin", false);
		booleanSettings.put("betweenRounds", false);
		booleanSettings.put("isPlaying", false);
	}

	private void resetStringSettings() {
		//Changeable settings.
		stringSettings.put("joinType", "freejoin");
		stringSettings.put("gameMode", "none");
		//Extra map info.
		stringSettings.put("selectedMap", null);
	}

	public int getIntSetting(String settingName) {
		//Bukkit.getServer().broadcastMessage("Requested: " + settingName + "   Returned: " + intSettings.get(settingName));
		return intSettings.get(settingName);
	}

	public boolean getBooleanSetting(String settingName) {
		//Bukkit.getServer().broadcastMessage("Requested: " + settingName + "   Returned: " + booleanSettings.get(settingName));
		return booleanSettings.get(settingName);
	}

	public String getStringSetting(String settingName) {
		//Bukkit.getServer().broadcastMessage("Requested: " + settingName + "   Returned: " + stringSettings.get(settingName));
		return stringSettings.get(settingName);
	}

	public void setIntSetting(String settingName, int setting) {
		intSettings.put(settingName, setting);
	}

	public void setStringSetting(String settingName, String setting) {
		stringSettings.put(settingName, setting);
	}
	public void setBooleanSetting(String settingName, Boolean setting) {
		booleanSettings.put(settingName, setting);
	}

	public void resetSettings() {
		resetIntSettings();
		resetBooleanSettings();
		resetStringSettings();
	}

	public void saveSettings() {
		FileConfiguration config = plugin.getConfig();
		String selectedMap = stringSettings.get("selectedMap");

		//Save int settings.
		config.set("maps." + selectedMap + ".respawntimer", intSettings.get("respawnTimer"));
		config.set("maps." + selectedMap + ".deathtypepay", intSettings.get("deathTypePay"));
		config.set("maps." + selectedMap + ".scorelimit", intSettings.get("scoreLimit"));
		config.set("maps." + selectedMap + ".maxplayers", intSettings.get("maxPlayers"));
		config.set("maps." + selectedMap + ".roundtime", intSettings.get("roundTime"));
		config.set("maps." + selectedMap + ".outofboundstime", intSettings.get("outOfBoundsTime"));
		config.set("maps." + selectedMap + ".roundlimit", intSettings.get("roundLimit"));
		config.set("maps." + selectedMap + ".betweenroundtime", intSettings.get("betweenRoundTime"));

		//Save boolean settings.
		config.set("maps." + selectedMap + ".friendlyfire", booleanSettings.get("friendlyFire"));
		config.set("maps." + selectedMap + ".deathtypedrop", booleanSettings.get("deathTypeDrop"));
		config.set("maps." + selectedMap + ".randomspawnpoints", booleanSettings.get("randomSpawnPoints"));
		config.set("maps." + selectedMap + ".joinmidgame", booleanSettings.get("joinMidGame"));
		config.set("maps." + selectedMap + ".forcepvpchat", booleanSettings.get("forcePvpChat"));
		config.set("maps." + selectedMap + ".outofboundsarea", booleanSettings.get("outOfBoundsArea"));
		config.set("maps." + selectedMap + ".autobalance", booleanSettings.get("autoBalance"));

		//Save string settings.
		config.set("maps." + selectedMap + ".jointype", stringSettings.get("joinType"));
		config.set("maps." + selectedMap + ".gamemode", stringSettings.get("gameMode"));
	}

	public void loadSettings() {
		FileConfiguration config = plugin.getConfig();
		String selectedMap = stringSettings.get("selectedMap");

		//Load int 
		intSettings.put("respawnTimer", config.getInt("maps." + selectedMap + ".respawntimer"));
		intSettings.put("deathTypePay", config.getInt("maps." + selectedMap + ".deathtypepay"));
		intSettings.put("scoreLimit", config.getInt("maps." + selectedMap + ".scorelimit"));
		intSettings.put("maxPlayers", config.getInt("maps." + selectedMap + ".maxplayers"));
		intSettings.put("roundTime", config.getInt("maps." + selectedMap + ".roundtime"));
		intSettings.put("outOfBoundsTime", config.getInt("maps." + selectedMap + ".outofboundstime"));
		intSettings.put("roundLimit", config.getInt("maps." + selectedMap + ".roundlimit"));
		intSettings.put("betweenRoundTime", config.getInt("maps." + selectedMap + ".betweenroundtime"));

		//Load boolean settings.
		booleanSettings.put("friendlyFire", config.getBoolean("maps." + selectedMap + ".friendlyfire"));
		booleanSettings.put("deathTypeDrop", config.getBoolean("maps." + selectedMap + ".deathtypedrop"));
		booleanSettings.put("randomSpawnPoints", config.getBoolean("maps." + selectedMap + ".randomspawnpoints"));
		booleanSettings.put("joinMidGame", config.getBoolean("maps." + selectedMap + ".joinmidgame"));
		booleanSettings.put("forcePvpChat", config.getBoolean("maps." + selectedMap + ".forcepvpchat"));
		booleanSettings.put("outOfBoundsArea", config.getBoolean("maps." + selectedMap + ".outofboundsarea"));
		booleanSettings.put("autoBalance", config.getBoolean("maps." + selectedMap + ".autobalance"));

		//Load string settings.
		stringSettings.put("joinType", config.getString("maps." + selectedMap + ".jointype"));
		stringSettings.put("gameMode", config.getString("maps." + selectedMap + ".gamemode"));
	}
	public void sendTypes(Player player) {
		for (Map.Entry<String, Integer> entry : intSettings.entrySet()) {
			player.sendMessage(ChatColor.DARK_RED + "[PvP] " + ChatColor.WHITE + entry.getKey() + ": " + entry.getValue());
		}
		for (Map.Entry<String, Boolean> entry : booleanSettings.entrySet()) {
			player.sendMessage(ChatColor.DARK_RED + "[PvP] " + ChatColor.WHITE + entry.getKey() + ": " + entry.getValue());
		}
		for (Map.Entry<String, String> entry : stringSettings.entrySet()) {
			player.sendMessage(ChatColor.DARK_RED + "[PvP] " + ChatColor.WHITE + entry.getKey() + ": " + entry.getValue());
		}
	}
}
