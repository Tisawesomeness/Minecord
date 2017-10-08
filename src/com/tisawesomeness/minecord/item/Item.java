package com.tisawesomeness.minecord.item;

import java.util.regex.Pattern;

public enum Item {
	
	AIR(0, 0, "Air"),
	STONE(1, 0, "Stone"),
	GRANITE(1, 1, "Granite", Version.V1_8),
	POLISHED_GRANITE(1, 2, "Polished Granite", "polished[^0-9A-Z]?granite", Version.V1_8),
	DIORITE(1, 3, "Diorite", Version.V1_8),
	POLISHED_DIORITE(1, 4, "Polished Diorite", "polished[^0-9A-Z]?diorite", Version.V1_8),
	ANDESITE(1, 5, "Andesite", Version.V1_8),
	POLISHED_ANDESITE(1, 6, "Polished Andesite", "polished[^0-9A-Z]?andesite", Version.V1_8),
	GRASS(2, 0, "Grass"),
	DIRT(3, 0, "Dirt"),
	COARSE_DIRT(3, 1, "Coarse Dirt", "coarse[^0-9A-Z]?dirt"),
	PODZOL(3, 2, "Podzol"),
	COBBLESTONE(4, 0, "Cobblestone", "cobble"),
	OAK_PLANKS(5, 0, "Oak Wood Planks", "^[^0-9A-Z]?(oak[^0-9A-Z]?)?(plank)"),
	SPRUCE_PLANKS(5, 1, "Spruce Wood Planks", "^[^0-9A-Z]?spruce[^0-9A-Z]?(plank)"),
	BIRCH_PLANKS(5, 2, "Birch Wood Planks", "^[^0-9A-Z]?birch[^0-9A-Z]?(plank)"),
	JUNGLE_PLANKS(5, 3, "Jungle Wood Planks", "^[^0-9A-Z]?jungle[^0-9A-Z]?(plank)"),
	ACACIA_PLANKS(5, 4, "Acacia Wood Planks", "^[^0-9A-Z]?acacia[^0-9A-Z]?(plank)"),
	DARK_OAK_PLANKS(5, 5, "Dark Oak Wood Planks", "^[^0-9A-Z]?dark[^0-9A-Z]?(oak[^0-9A-Z]?)?(plank)");
	
	public final int id;
	public final int data;
	public final String name;
	public final String aliases;
	public final Version version;
	public final Tool tool;
	public final int enchantability;
	
	private Item(int id, int data, String name) {
		this(id, data, name, null, null, null, 0);
	}
	private Item(int id, int data, String name, String aliases) {
		this(id, data, name, aliases, null, null, 0);
	}
	private Item(int id, int data, String name, Version version) {
		this(id, data, name, null, version, null, 0);
	}
	private Item(int id, int data, String name, String aliases, Version version) {
		this(id, data, name, aliases, version, null, 0);
	}
	private Item(int id, int data, String name, String aliases, Tool tool, int enchantability) {
		this(id, data, name, aliases, null, tool, 0);
	}
	
	private Item(int id, int data, String name, String aliases, Version version, Tool tool, int enchantability) {
		this.id = id;
		this.data = data;
		this.name = name;
		this.aliases = aliases;
		this.version = version;
		this.tool = tool;
		this.enchantability = enchantability;
	}
	
	public boolean matches(String str) {
		//Item id and data
		String pattern = "^" + id;
		if (data != 0) {
			pattern += ":" + data;
		}
		if (Pattern.compile(pattern + "([^0-9:]|$)").matcher(str).find()) {
			return true;
		}
		//Display name
		if (name.equalsIgnoreCase(str)) {
			System.out.println("true");
			return true;
		}
		//Regex
		return aliases != null && Pattern.compile(aliases, Pattern.CASE_INSENSITIVE).matcher(str).find();
	}
	
}
