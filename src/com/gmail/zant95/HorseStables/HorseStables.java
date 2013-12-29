package com.gmail.zant95.HorseStables;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.zant95.HorseStables.Storage.DiskStorage;

public class HorseStables extends JavaPlugin {
	@Override
	public void onEnable() {
		//Setup plugin folder
		if (!this.getDataFolder().exists()) {
			this.getLogger().info("Creating plugin folder...");
			new File(this.getDataFolder().toString()).mkdir();	
		}
		
		//Load storage
		DiskStorage.asyncLoadHorses();
		
		//Implement commands
		this.getCommand("horsespawn").setExecutor(new CommandHandler(this));
		this.getCommand("horsetoggle").setExecutor(new CommandHandler(this));
		this.getCommand("horselist").setExecutor(new CommandHandler(this));
		
		//Enable message
		this.getLogger().info("HorseStables enabled!");
	}
	
	@Override
	public void onDisable() {
		this.getLogger().info("Goodbye HorseStables!");
	}
}
