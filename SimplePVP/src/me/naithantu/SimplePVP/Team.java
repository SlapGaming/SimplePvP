package me.naithantu.SimplePVP;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

public class Team {
	SimplePVP plugin;
	Settings settings;
	private int score = 0;
	private boolean flagTaken = false;
	private List<String> players = new ArrayList<String>();
	
	public Team(SimplePVP instance){
		plugin = instance;
		settings = plugin.getSettings();
	}
	
	public List<String> getPlayers(){
		return players;
	}
	
	public int getScore(){
		return score;
	}
	
	public boolean getFlagTaken(){
		return flagTaken;
	}
	
	public void addPoint(){
		score++;
	}
	
	public void teleport(String locationName){
		for(String playerName: players){
			Bukkit.getServer().getPlayer(playerName).teleport(plugin.getConfigLocation(settings.getStringSetting("selectedMap") , locationName));
		}
	}
}
