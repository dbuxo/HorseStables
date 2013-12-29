package com.gmail.zant95.HorseStables.Storage;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.gmail.zant95.HorseStables.Utils.CIMap;

public final class MemStorage {
	public static boolean isOccupiedStorage = false;
	public final static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("HorseStables");
	public final static File storageFolder = new File(plugin.getDataFolder() + File.separator + "storage");
	public final static
		CIMap <					/* Stables HashMap              */
			String,				/*  |--Stable name              */
			CIMap <				/*  |--Players HashMap          */
				String,			/*      |--Player name          */
				CIMap <			/*      |--Slots TreeMap        */
					String,		/*          |--Slot number      */
					Object[]	/*          |--Serialized horse */
				>
			>
		> horses = new CIMap<String, CIMap<String, CIMap<String, Object[]>>>();
}