package com.gmail.zant95.HorseStables.Storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bukkit.Bukkit;

import com.gmail.zant95.HorseStables.Storage.MemStorage;
import com.gmail.zant95.HorseStables.Utils.CIMap;

public class DiskStorage {
	public final static void asyncLoadHorses() {
		if (!MemStorage.isOccupiedStorage && MemStorage.storageFolder.exists()) {
			MemStorage.isOccupiedStorage = true;
			Bukkit.getScheduler().runTaskAsynchronously(MemStorage.plugin, new Runnable() {
				@SuppressWarnings("unchecked")
				@Override
				public void run() {
					File[] storageFiles = MemStorage.storageFolder.listFiles(new FilenameFilter() {
						public boolean accept(File dir, String filename)
							{return filename.endsWith(".gz");}
						});
					
					for (File storageFile : storageFiles) {
						try {
							FileInputStream fis = new FileInputStream(storageFile);
							GZIPInputStream gis = new GZIPInputStream(fis);
							ObjectInputStream ois = new ObjectInputStream(gis);
							MemStorage.horses.put(
								storageFile.getName().substring(0, storageFile.getName().length() - 3),
								(CIMap<String, CIMap<String, Object[]>>)ois.readObject()
							);
							ois.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
					MemStorage.isOccupiedStorage = false;
				}
			});
		}
	}
	
	public final static void asyncSaveHorses() {
		if (!MemStorage.isOccupiedStorage) {
			MemStorage.isOccupiedStorage = true;
			Bukkit.getScheduler().runTaskAsynchronously(MemStorage.plugin, new Runnable() {
				@Override
				public void run() {
					if (!MemStorage.storageFolder.exists()) MemStorage.storageFolder.mkdir();
					Iterator<Entry<String, CIMap<String, CIMap<String, Object[]>>>> horsesStorageIterator = MemStorage.horses.entrySet().iterator();
					while (horsesStorageIterator.hasNext()) {
						Entry<String, CIMap<String, CIMap<String, Object[]>>> horsesStorage = horsesStorageIterator.next();
						try {
							FileOutputStream fos = new FileOutputStream(new File(MemStorage.storageFolder + File.separator + horsesStorage.getKey() + ".gz"));
							GZIPOutputStream gos = new GZIPOutputStream(fos);
							ObjectOutputStream oos = new ObjectOutputStream(gos);
							oos.writeObject(horsesStorage.getValue());
							oos.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					MemStorage.isOccupiedStorage = false;
				}
			});
		}
	}
}