package com.gmail.zant95.HorseStables.Utils;

import java.util.TreeMap;

public final class CIMap<K, V> extends TreeMap<String, V> {
	private static final long serialVersionUID = 3588405358786015678L;

	@Override
	public final V put(String key, V value) {
		return super.put(key.toLowerCase(), value);
	}
	
	public final V get(String key) {
		return super.get(key.toLowerCase());
	}
	
	public final V remove(String key) {
		return super.remove(key.toLowerCase());
	}
	
	public final boolean containsKey(String key) {
		return super.containsKey(key.toLowerCase());
	}
}