package com.gmail.zant95.HorseStables.Locale;

import org.bukkit.ChatColor;

public final class Messages {
	private final static String SUCESSPREFIX = "&6 - &2", FAILPREFIX = "&6 - &c";
	
	public final static String
		NO_PERMISSION = FORMAT(FAILPREFIX + "You don't have permissions to use this command."),
		ONLY_A_PLAYER = FORMAT(FAILPREFIX + "This command must be used by a player."),
		NOT_FOUND = FORMAT(FAILPREFIX + "Not found."),
		BAD_SYNTAX = FORMAT(FAILPREFIX + "Error, bad syntax."),
		ONLY_ALPHANUMERIC = FORMAT(FAILPREFIX + "The stable name can only contain alphanumeric characters."),
		DISCONNECTED_PLAYER = FORMAT(FAILPREFIX + "Error, disconnected player."),
		SLOT_NUMBER_BETWEEN = FORMAT(FAILPREFIX + "The slot number must be between 0 and 15."),
		OCCUPIED_SLOT = FORMAT(FAILPREFIX + "You already have a horse in this slot."),
		LEAVE_VEHICLE = FORMAT(FAILPREFIX + "You must leave this vehicle to do this."),
		NOT_HORSE = FORMAT(FAILPREFIX + "You don't have a horse in this slot."),
		UNEXPECTED = FORMAT(FAILPREFIX + "Unexpected error."),
		LOADED_HORSE = FORMAT(SUCESSPREFIX + "Your horse has been successfully &6loaded&2."),
		SAVED_HORSE = FORMAT(SUCESSPREFIX + "Your horse has been successfully &6saved&2."),
		DELETED_HORSE = FORMAT(SUCESSPREFIX + "The selected horse has been successfully &6deleted&2."),
		SEPARATOR = FORMAT("&l=============================================");
	
	public final static String STABLE_LIST_TITLE(String stableName) {
		return FORMAT("&6 Stable &f" + stableName + " &6horses:");
	}
	
	public final static String PLAYER_LIST_TITLE(String playerName) {
		return FORMAT("&f " + playerName + "&6's horses:");
	}
	
	public final static String TAMER_LIST_TITLE(String tamerName) {
		return FORMAT("&f " + tamerName + "&6's tamed horses:");
	}
	
	public final static String HORSE_TYPE(Object horseType) {
		return FORMAT("[&f" + horseType + "&6]");
	}
	
	public final static String HORSE_NAME(Object horseName) {
		return FORMAT((horseName != null) ? "[&f" + horseName + "&6]" : "");
	}
	
	public final static String HORSE_ENTRY(String slotNumber, String horseType, String horseName) {
		return FORMAT("&2    - &6Slot &f" + slotNumber + "&6 " + horseType + horseName + ".");
	}
	
	public final static String STABLE_ENTRY(String stableName) {
		return FORMAT(SUCESSPREFIX + stableName + "&6:");
	}
	
	public final static String PLAYER_ENTRY(String playerName) {
		return FORMAT(SUCESSPREFIX + playerName + "&6:");
	}
	
	private final static String FORMAT(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}
}
