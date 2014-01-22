package com.gmail.zant95.HorseStables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.Player;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;

/* 
 * I will need to change these values ​​with each version of Minecraft
 * because of the Bukkit API is incomplete :(
 */
import net.minecraft.server.v1_7_R1.EntityInsentient;
import net.minecraft.server.v1_7_R1.GenericAttributes;

import org.bukkit.craftbukkit.v1_7_R1.entity.CraftLivingEntity;
/* --------------------------------------------------------------- */

import com.gmail.zant95.HorseStables.Locale.Messages;
import com.gmail.zant95.HorseStables.Storage.DiskStorage;
import com.gmail.zant95.HorseStables.Storage.MemStorage;
import com.gmail.zant95.HorseStables.Utils.CIMap;

public final class CommandHandler implements CommandExecutor {
	HorseStables plugin;
	public CommandHandler(HorseStables instance) {
		plugin = instance;
	}
		
	public final boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		
		/* 
		 * #############################################################
		 * # Syntax: /horsespawn                                       #
		 * #                        <stable> --> [Alphanumeric string] #
		 * #                        <player> --> [Valid player name]   #
		 * #                        <slot>   --> [Valid 0-15 integer]  #
		 * #############################################################
		 */
		
		if (command.getName().equalsIgnoreCase("horsespawn")) {
			if (!sender.hasPermission("horsestables.spawn")) {
				sender.sendMessage(Messages.NO_PERMISSION);
			} else if (!(sender instanceof Player)) {
				sender.sendMessage(Messages.ONLY_A_PLAYER);
			} else if (args.length == 3) {
				if (HorseSearch.horseExists(args[0], args[1], args[2])) {
					HorseManage.loadCustomHorse((Player)sender, args[0], args[1], args[2]);
				} else {
					sender.sendMessage(Messages.NOT_FOUND);
				}
			} else {
				sender.sendMessage(Messages.BAD_SYNTAX);
			}
		}
		
		/* 
		 * #############################################################
		 * # Syntax: /horsedelete                                      #
		 * #                        <stable> --> [Alphanumeric string] #
		 * #                        <player> --> [Valid player name]   #
		 * #                        <slot>   --> [Valid 0-15 integer]  #
		 * #############################################################
		 */
		
		if (command.getName().equalsIgnoreCase("horsedelete")) {
			if (!sender.hasPermission("horsestables.delete")) {
				sender.sendMessage(Messages.NO_PERMISSION);
			} else if (args.length == 3) {
				if (HorseSearch.horseExists(args[0], args[1], args[2])) {
					HorseManage.deleteCustomHorse(sender, args[0], args[1], args[2]);
				} else {
					sender.sendMessage(Messages.NOT_FOUND);
				}
			} else {
				sender.sendMessage(Messages.BAD_SYNTAX);
			}
		}
		
		/* 
		 * #############################################################
		 * # Syntax: /horsetoggle                                      #
		 * #                        <stable> --> [Alphanumeric string] #
		 * #                        <player> --> [Valid player name]   #
		 * #                        <slot>   --> [Valid 0-15 integer]  #
		 * #############################################################
		 */
		
		else if (command.getName().equalsIgnoreCase("horsetoggle")) {
			if (!sender.hasPermission("horsestables.toggle")) {
				sender.sendMessage(Messages.NO_PERMISSION);
			} else if (args.length == 3) {
				
				/* Check if the stable name is an alphanumeric string */
				if (!Pattern.compile("[-\\p{Alnum}]+").matcher(args[0]).matches()) {
					sender.sendMessage(Messages.ONLY_ALPHANUMERIC);
					return true;
				}
				/* -------------------------------------------------- */
				
				/* Check if the player is online */
				if (!Bukkit.getServer().getOfflinePlayer(args[1]).isOnline()) {
					sender.sendMessage(Messages.DISCONNECTED_PLAYER);
					return true;
				}
				/* ----------------------------- */
				
				/* Check if the slot number is and integer between 0-15 */
				if (!Pattern.compile("1[0-5]|[0-9]").matcher(args[2]).matches()) {
					sender.sendMessage(Messages.SLOT_NUMBER_BETWEEN);
					return true;
				}
				/* ---------------------------------------------------- */
				
				Player player = Bukkit.getServer().getPlayer(args[1]);
				
				/* 
				 * ###############
				 * # Load horse: #
				 * ###############
				 */
				
				if (HorseSearch.horseExists(args[0], args[1], args[2])) {
					if (player.isInsideVehicle()) {
						if (player.getVehicle() instanceof Horse) player.sendMessage(Messages.OCCUPIED_SLOT);
						else player.sendMessage(Messages.LEAVE_VEHICLE);
						return true;
					}
					
					HorseManage.loadCustomHorse(player, args[0], args[1], args[2]);
				}
				
				/* 
				 * ###############
				 * # Save horse: #
				 * ###############
				 */
				
				else if (player.isInsideVehicle() && player.getVehicle() instanceof Horse) {
					HorseManage.saveCustomHorse(player, args[0], args[1], args[2]);
				}
				
				/* 
				 * ###############
				 * # Do nothing: #
				 * ###############
				 */
				
				else {
					player.sendMessage(Messages.NOT_HORSE);
				}
			} else {
				sender.sendMessage(Messages.BAD_SYNTAX);
			}
		}
		
		/* 
		 * ############################################################################
		 * # Syntax: /horselist                                                       #
		 * #                        <[stable]|[player]|[tamer]|[tamed]]> --> [String] #
		 * #                        <stable|player>                      --> [String] #
		 * ############################################################################
		 */
		
		else if (command.getName().equalsIgnoreCase("horselist")) {
			if (!sender.hasPermission("horsestables.list")) {
				sender.sendMessage(Messages.NO_PERMISSION);
			} else if (args.length == 2) {
				if (!sender.hasPermission("horsestables.list.other")) {
					sender.sendMessage(Messages.NO_PERMISSION);
				} else if (args[0].equalsIgnoreCase("stable")) {
					
					/* Print stable horses */
					HorseSearch.asyncSearchAndPrintStable(sender, args[1]);
					/* ------------------- */
					
				} else if (args[0].equalsIgnoreCase("player")) {
					
					/* Print player horses */
					HorseSearch.asyncSearchAndPrintPlayer(sender, args[1]);
					/* ------------------- */
					
				} else if (args[0].equalsIgnoreCase("tamer")) {
					
					/* Print tamer horses */
					HorseSearch.asyncSearchAndPrintTamer(sender, args[1]);
					/* ------------------ */
					
				} else {
					sender.sendMessage(Messages.BAD_SYNTAX);
				}
			} else if (args.length == 0 && sender instanceof Player) {
				
				/* Print own horses */
				HorseSearch.asyncSearchAndPrintPlayer(sender, sender.getName());
				/* ---------------- */
				
			} else if (args.length == 1 && args[0].equalsIgnoreCase("tamed")) {
				
				/* Print own tamed horses */
				HorseSearch.asyncSearchAndPrintTamer(sender, sender.getName());
				/* ---------------------- */
				
			} else {
				sender.sendMessage(Messages.BAD_SYNTAX);
			}
		}
		
		return true;
	}
}

final class HorseManage {
	final static void loadCustomHorse(final Player player, final String stableName, final String playerName, final String slotNumber) {
		/* Spawn default horse */
		Horse horse = (Horse)player.getWorld().spawnEntity(player.getLocation(), EntityType.HORSE);
		/* ------------------- */
		
		/* Try to apply attributes to the spawned horse */
		try {
			Object[] serializedHorse = MemStorage.horses.get(stableName).get(playerName).get(slotNumber);
			
			horse.setAge						((int)				serializedHorse[0]);
			horse.setBreed						((boolean)			serializedHorse[1]);
			horse.setCarryingChest				((boolean)			serializedHorse[2]);
			horse.setColor						((Color)			serializedHorse[3]);
			horse.setCustomName					((String)			serializedHorse[4]);
			horse.setCustomNameVisible			((boolean)			serializedHorse[5]);
			horse.setDomestication				((int)				serializedHorse[6]);
			horse.setFireTicks					((int)				serializedHorse[7]);
			horse.setHealth						(
					(double)serializedHorse[8] > 53 ? 53 : (double)serializedHorse[8]
			);
			horse.setJumpStrength				((double)			serializedHorse[9]);
			horse.setMaxDomestication			((int)				serializedHorse[10]);
			horse.setMaxHealth					((double)			serializedHorse[11]);
			horse.setMaximumAir					((int)				serializedHorse[12]);
			horse.setMaximumNoDamageTicks		((int)				serializedHorse[13]);
			horse.setNoDamageTicks				((int)				serializedHorse[14]);
			
			/* AnimalTamer cannot be serialized */
			if (serializedHorse[15] != null) {
				horse.setOwner					((AnimalTamer)		Bukkit.getServer().getPlayer((String)serializedHorse[15]));
			}
			/* -------------------------------- */
			
			horse.setRemainingAir				((int)				serializedHorse[16]);
			horse.setRemoveWhenFarAway			((boolean)			serializedHorse[17]);
			horse.setStyle						((Style)			serializedHorse[18]);
			horse.setTamed						((boolean)			serializedHorse[19]);
			horse.setTicksLived					((int)				serializedHorse[20]);
			horse.setVariant					((Variant)			serializedHorse[21]);
						
			/*  Incomplete Bukkit API */
			((EntityInsentient)((CraftLivingEntity)horse).getHandle()).getAttributeInstance(GenericAttributes.d).setValue((double)serializedHorse[22]);
			/* ---------------------- */
			
			HorseInventory horseInventory = horse.getInventory();
			
			@SuppressWarnings("unchecked")
			ItemStack[] deserializedHorseInventory = SerializeItemStackList.deserializeItemStackList((List<HashMap<Map<String, Object>, Map<String, Object>>>) serializedHorse[23]);
			
			for (int i = 0; i < deserializedHorseInventory.length; i++) {
				try {
					horseInventory.setItem(i, deserializedHorseInventory[i]);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			/* Delete horse from HashMap and mount player on the spawned horse */
			horse.setPassenger(player);
			
			deleteCustomHorse(stableName, playerName, slotNumber);
			/* --------------------------------------------------------------- */
			
			/* Save HashMap async */
			DiskStorage.asyncSaveHorses();
			/* ------------------ */
			
			player.sendMessage(Messages.LOADED_HORSE);
		}
		
		
		/* If anything fails remove the spawned entity */
		catch (Exception e) {
			e.printStackTrace();
			horse.remove();
			
			player.sendMessage(Messages.UNEXPECTED);
		}
		/* ------------------------------------------- */
	}
	
	final static void saveCustomHorse(final Player player, final String stableName, final String playerName, final String slotNumber) {
		try {
			
			/* Serialize horse to object */
			Horse horse = (Horse)player.getVehicle();
			
			Object[] serializedHorse = new Object[24];
				serializedHorse[0] = horse.getAge();
				serializedHorse[1] = horse.canBreed();
				serializedHorse[2] = horse.isCarryingChest();
				serializedHorse[3] = horse.getColor();
				serializedHorse[4] = horse.getCustomName();
				serializedHorse[5] = horse.isCustomNameVisible();
				serializedHorse[6] = horse.getDomestication();
				serializedHorse[7] = horse.getFireTicks();
				serializedHorse[8] = horse.getHealth();
				serializedHorse[9] = horse.getJumpStrength();
				serializedHorse[10] = horse.getMaxDomestication();
				serializedHorse[11] = horse.getMaxHealth();
				serializedHorse[12] = horse.getMaximumAir();
				serializedHorse[13] = horse.getMaximumNoDamageTicks();
				serializedHorse[14] = horse.getNoDamageTicks();
				serializedHorse[15] = (horse.getOwner() != null) ? horse.getOwner().getName() : null;
				serializedHorse[16] = horse.getRemainingAir();
				serializedHorse[17] = horse.getRemoveWhenFarAway();
				serializedHorse[18] = horse.getStyle();
				serializedHorse[19] = horse.isTamed();
				serializedHorse[20] = horse.getTicksLived();
				serializedHorse[21] = horse.getVariant();
				
				/*  Incomplete Bukkit API */
				serializedHorse[22] = ((EntityInsentient)((CraftLivingEntity)horse).getHandle()).getAttributeInstance(GenericAttributes.d).getValue();
				/* ---------------------- */
				
				serializedHorse[23] = SerializeItemStackList.serializeItemStackList(horse.getInventory().getContents());
			/* --------------------------- */
			
			/* Put serialized horse in HashMap */
			CIMap<String, Object[]> slotStorage = new CIMap<String, Object[]>();
			slotStorage.put(slotNumber, serializedHorse);
			
			CIMap<String, CIMap<String, Object[]>> playerStorage = new CIMap<String, CIMap<String, Object[]>>();
			playerStorage.put(playerName, slotStorage);
			
			if (MemStorage.horses.containsKey(stableName)) {
				if (MemStorage.horses.get(stableName).containsKey(playerName)) {
					MemStorage.horses.get(stableName).get(playerName).putAll(slotStorage);
				} else { /* Player not found */
					MemStorage.horses.get(stableName).putAll(playerStorage);
				}
			} else { /* Stable not found */
				MemStorage.horses.put(stableName, playerStorage);
			}
			/* ------------------------------- */
			
			/* Remove horse entity */
			horse.remove();
			/* ------------------- */
			
			/* Load HashMap async */
			DiskStorage.asyncSaveHorses();
			/* ------------------ */
			
			player.sendMessage(Messages.SAVED_HORSE);
		} catch (Exception e) {
			e.printStackTrace();
			
			player.sendMessage(Messages.UNEXPECTED);
		}
	}
	
	final static void deleteCustomHorse(final CommandSender sender, final String stableName, final String playerName, final String slotNumber) {
		try {
			deleteCustomHorse(stableName, playerName, slotNumber);
			
			sender.sendMessage(Messages.DELETED_HORSE);
		} catch (Exception e) {
			e.printStackTrace();
			
			sender.sendMessage(Messages.UNEXPECTED);
		}
	}
	
	final static void deleteCustomHorse(final String stableName, final String playerName, final String slotNumber) throws Exception {
		MemStorage.horses.get(stableName).get(playerName).remove(slotNumber);
		if (MemStorage.horses.get(stableName).get(playerName).isEmpty()) {
			MemStorage.horses.get(stableName).remove(playerName);
			if (MemStorage.horses.get(stableName).isEmpty()) {
				MemStorage.horses.remove(stableName);
			}
		}
		
		/* Save HashMap async */
		DiskStorage.asyncSaveHorses();
		/* ------------------ */
	}	
}

final class HorseSearch {
	final static boolean horseExists(final String stableName, final String playerName, final String slotNumber) {
		return (
			MemStorage.horses.containsKey(stableName) &&
			MemStorage.horses.get(stableName).containsKey(playerName) &&
			MemStorage.horses.get(stableName).get(playerName).containsKey(slotNumber)
		);
	}
	
	final static void asyncSearchAndPrintStable(final CommandSender sender, final String stableName) {
		sender.sendMessage(Messages.SEPARATOR);
		sender.sendMessage(Messages.STABLE_LIST_TITLE(stableName));
		
		Bukkit.getScheduler().runTaskAsynchronously(MemStorage.plugin, new Runnable() {
			@Override
			public void run() {
				CIMap<String, CIMap<String, Object[]>> stableEntry = MemStorage.horses.get(stableName);
				
				ArrayList<String> chatMessageBuffer = new ArrayList<String>();
				
				if (stableEntry != null) {
					
					/* Iterate over stable players */
					Iterator<Entry<String, CIMap<String, Object[]>>> playerIterator = stableEntry.entrySet().iterator();
					while (playerIterator.hasNext()) {
						Entry<String, CIMap<String, Object[]>> playerEntry = playerIterator.next();
						
						chatMessageBuffer.add(Messages.PLAYER_ENTRY(playerEntry.getKey()));
						
						/* Iterate over player slots */
						Iterator<Entry<String, Object[]>> slotIterator = playerEntry.getValue().entrySet().iterator();
						while (slotIterator.hasNext()) {
							Entry<String, Object[]> slotEntry = slotIterator.next();
							
							Object[] serializedHorse = slotEntry.getValue();
							String horseType = Messages.HORSE_TYPE(serializedHorse[21]);
							String horseName = Messages.HORSE_NAME(serializedHorse[4]);
							
							chatMessageBuffer.add(Messages.HORSE_ENTRY(slotEntry.getKey(), horseType, horseName));
							
						}
						/* ------------------------- */
						
					}
					/* --------------------------- */
					
				}
				
				if (!chatMessageBuffer.isEmpty()) {
					for (String chatMessage : chatMessageBuffer) {
						sender.sendMessage(chatMessage);
					}
				} else {
					sender.sendMessage(Messages.NOT_FOUND);
				}
				
				sender.sendMessage(Messages.SEPARATOR);
			}
		});
	}
	
	final static void asyncSearchAndPrintPlayer(final CommandSender sender, final String playerName) {
		sender.sendMessage(Messages.SEPARATOR);
		sender.sendMessage(Messages.PLAYER_LIST_TITLE(playerName));
		
		Bukkit.getScheduler().runTaskAsynchronously(MemStorage.plugin, new Runnable() {
			@Override
			public void run() {
				ArrayList<String> chatMessageBuffer = new ArrayList<String>();
				
				/* Iterate over stables */
				Iterator<Entry<String, CIMap<String, CIMap<String, Object[]>>>> stableIterator = MemStorage.horses.entrySet().iterator();
				while (stableIterator.hasNext()) {
					Entry<String, CIMap<String, CIMap<String, Object[]>>> stableEntry = stableIterator.next();
					
					if (stableEntry.getValue().containsKey(playerName)) {
						chatMessageBuffer.add(Messages.STABLE_ENTRY(stableEntry.getKey()));
						
						/* Iterate over player slots */
						Iterator<Entry<String, Object[]>> slotIterator = stableEntry.getValue().get(playerName).entrySet().iterator();
						while (slotIterator.hasNext()) {
							Entry<String, Object[]> slotEntry = slotIterator.next();
							
							Object[] serializedHorse = slotEntry.getValue();
							String horseType = Messages.HORSE_TYPE(serializedHorse[21]);
							String horseName = Messages.HORSE_NAME(serializedHorse[4]);
							
							chatMessageBuffer.add(Messages.HORSE_ENTRY(slotEntry.getKey(), horseType, horseName));
						}
						/* ------------------------- */
						
					}
					
				}
				/* ------------------------ */
				
				if (!chatMessageBuffer.isEmpty()) {
					for (String chatMessage : chatMessageBuffer) {
						sender.sendMessage(chatMessage);
					}
				} else {
					sender.sendMessage(Messages.NOT_FOUND);
				}
				
				sender.sendMessage(Messages.SEPARATOR);
			}
		});
	}
	
	final static void asyncSearchAndPrintTamer(final CommandSender sender, final String tamerName) {
		sender.sendMessage(Messages.SEPARATOR);
		sender.sendMessage(Messages.TAMER_LIST_TITLE(tamerName));
		
		Bukkit.getScheduler().runTaskAsynchronously(MemStorage.plugin, new Runnable() {
			@Override
			public void run() {
				ArrayList<String> chatMessageBufferStable = new ArrayList<String>();
				
				/* Iterate over stables */
				Iterator<Entry<String, CIMap<String, CIMap<String, Object[]>>>> stableIterator = MemStorage.horses.entrySet().iterator();
				while (stableIterator.hasNext()) {
					Entry<String, CIMap<String, CIMap<String, Object[]>>> stableEntry = stableIterator.next();
					
					ArrayList<String> chatMessageBufferPlayer = new ArrayList<String>();
					
					/* Iterate over stable players */
					Iterator<Entry<String, CIMap<String, Object[]>>> playerIterator = stableEntry.getValue().entrySet().iterator();
					while (playerIterator.hasNext()) {
						Entry<String, CIMap<String, Object[]>> playerEntry = playerIterator.next();
						
						ArrayList<String> chatMessageBufferSlot = new ArrayList<String>();
						
						/* Iterate over player slots */
						Iterator<Entry<String, Object[]>> slotIterator = playerEntry.getValue().entrySet().iterator();
						while (slotIterator.hasNext()) {
							Entry<String, Object[]> slotEntry = slotIterator.next();
							
							Object[] serializedHorse = slotEntry.getValue();
							String horseType = Messages.HORSE_TYPE(serializedHorse[21]);
							String horseName = Messages.HORSE_NAME(serializedHorse[4]);
							
							if (serializedHorse[15] != null && serializedHorse[15].toString().equalsIgnoreCase(tamerName)) {
								chatMessageBufferSlot.add("    " + Messages.HORSE_ENTRY(slotEntry.getKey(), horseType, horseName));
							}
						}
						
						if (!chatMessageBufferSlot.isEmpty()) {
							chatMessageBufferPlayer.add("    " + Messages.PLAYER_ENTRY(playerEntry.getKey()));
							chatMessageBufferPlayer.addAll(chatMessageBufferSlot);
						}
						
						/* ------------------------- */
						
					}
					/* --------------------------- */
					
					if (!chatMessageBufferPlayer.isEmpty()) {
						chatMessageBufferStable.add(Messages.STABLE_ENTRY(stableEntry.getKey()));
						chatMessageBufferStable.addAll(chatMessageBufferPlayer);
					}
					
				}
				/* ------------------------ */
				
				if (!chatMessageBufferStable.isEmpty()) {
					for (String chatMessage : chatMessageBufferStable) {
						sender.sendMessage(chatMessage);
					}
				} else {
					sender.sendMessage(Messages.NOT_FOUND);
				}
				
				sender.sendMessage(Messages.SEPARATOR);
			}
		});
	}
}