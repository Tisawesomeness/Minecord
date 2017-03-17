package com.tisawesomeness.minecord.item;

public enum Item {
	
	DIAMOND_SWORD("",
			RecipeType.CRAFTING, Tool.SWORD, 0, 276, "Diamond Sword",
			"diamond[^a-zA-Z0-9]?sword");
	
	private final String recipe;
	private final RecipeType type;
	private final Tool tool;
	private final int enchantability;
	private final Version version;
	private final int id;
	private final int data;
	private final String name;
	private final String[] aliases;
	
	private Item(String recipe, RecipeType type, int id, String name, String alias) {
		this(recipe, type, null, -1, Version.V1_8, id, 0, name, new String[]{alias});
	}
	private Item(String recipe, RecipeType type, Tool tool, int enchantability, int id, String name, String alias) {
		this(recipe, type, tool, enchantability, Version.V1_8, id, 0, name, new String[]{alias});
	}
	private Item(String recipe, RecipeType type, Version version, int id, String name, String alias) {
		this(recipe, type, null, -1, version, id, 0, name, new String[]{alias});
	}
	private Item(String recipe, RecipeType type, int id, int data, String name, String alias) {
		this(recipe, type, null, -1, Version.V1_8, id, data, name, new String[]{alias});
	}
	private Item(String recipe, RecipeType type, Tool tool, int enchantability,
			Version version, int id, String name, String alias) {
		this(recipe, type, tool, enchantability, version, id, 0, name, new String[]{alias});
	}
	private Item(String recipe, RecipeType type, Tool tool, int enchantability,
			int id, int data, String name, String alias) {
		this(recipe, type, tool, enchantability, Version.V1_8, id, data, name, new String[]{alias});
	}
	private Item(String recipe, RecipeType type, Version version, int id, int data, String name, String alias) {
		this(recipe, type, null, -1, version, id, data, name, new String[]{alias});
	}
	private Item(String recipe, RecipeType type, Tool tool, int enchantability,
			Version version, int id, int data, String name, String alias) {
		this(recipe, type, tool, enchantability, version, id, data, name, new String[]{alias});
	}
	private Item(String recipe, RecipeType type, int id, String name, String[] aliases) {
		this(recipe, type, null, -1, Version.V1_8, id, 0, name, aliases);
	}
	private Item(String recipe, RecipeType type, Tool tool, int enchantability, int id, String name, String[] aliases) {
		this(recipe, type, tool, enchantability, Version.V1_8, id, 0, name, aliases);
	}
	private Item(String recipe, RecipeType type, Version version, int id, String name, String[] aliases) {
		this(recipe, type, null, -1, version, id, 0, name, aliases);
	}
	private Item(String recipe, RecipeType type, int id, int data, String name, String[] aliases) {
		this(recipe, type, null, -1, Version.V1_8, id, data, name, aliases);
	}
	private Item(String recipe, RecipeType type, Tool tool, int enchantability,
			Version version, int id, String name, String[] aliases) {
		this(recipe, type, tool, enchantability, version, id, 0, name, aliases);
	}
	private Item(String recipe, RecipeType type, Tool tool, int enchantability,
			int id, int data, String name, String[] aliases) {
		this(recipe, type, tool, enchantability, Version.V1_8, id, data, name, aliases);
	}
	private Item(String recipe, RecipeType type, Version version, int id, int data, String name, String[] aliases) {
		this(recipe, type, null, -1, version, id, data, name, aliases);
	}
	
	private Item(String recipe, RecipeType type, Tool tool, int enchantability,
			Version version, int id, int data, String name, String[] aliases) {
		this.recipe = recipe;
		this.type = type;
		this.tool = tool;
		this.enchantability = enchantability;
		this.version = version;
		this.id = id;
		this.data = data;
		this.name = name;
		this.aliases = aliases;
	}
	
	public String getRecipe() {return recipe;}
	public RecipeType getType() {return type;}
	public Tool getTool() {return tool;}
	public int getEnchantability() {return enchantability;}
	public Version getVersion() {return version;}
	public int getId() {return id;}
	public int getData() {return data;}
	public String toString() {return name;}
	public String[] getAliases() {return aliases;}

}
