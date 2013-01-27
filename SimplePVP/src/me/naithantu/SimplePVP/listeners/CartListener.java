package me.naithantu.SimplePVP.listeners;

import me.naithantu.SimplePVP.Settings;
import me.naithantu.SimplePVP.SimplePVP;

import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;

public class CartListener implements Listener {

	SimplePVP plugin;
	Settings settings;
	
	public CartListener(SimplePVP instance){
		plugin = instance;
	}
	@EventHandler
	public void onCartCollision(VehicleEntityCollisionEvent event) {
		if(event.getVehicle() instanceof Minecart && event.getVehicle().getLocation().getWorld().getName().equals("world")){
			event.setCancelled(true);
		}
	}
	
	//TODO
}
