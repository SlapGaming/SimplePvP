package me.naithantu.SimplePVP.listeners;

import java.util.List;

import me.naithantu.SimplePVP.Settings;
import me.naithantu.SimplePVP.SimplePVP;
import me.naithantu.SimplePVP.commands.AdminCommands;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener{

	SimplePVP plugin;
	Settings settings;
	AdminCommands adminCommands;
	
	public ChatListener(SimplePVP instance){
		plugin = instance;
	}
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		settings = plugin.getSettings();
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
}
