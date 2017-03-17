package com.tisawesomeness.minecord.item;

public enum Enchantment {
	
	SHARPNESS_I("Sharpness I", 1, 21, 10, Version.V1_8, new Enchantment[]{});
	
	private final String name;
	private final int min;
	private final int max;
	private final int weight;
	private final Version version;
	private final Enchantment[] conflicts;
	
	private Enchantment(String name, int min, int max, int weight, Version version, Enchantment[] conflicts) {
		this.name = name;
		this.min = min;
		this.max = max;
		this.weight = weight;
		this.version = version;
		this.conflicts = conflicts;
	}
	
	public String toString() {return name;}
	public int getMin() {return min;}
	public int getMax() {return max;}
	public int getWeight() {return weight;}
	public Version getVersion() {return version;}
	public Enchantment[] getConflicts() {return conflicts;}

}
