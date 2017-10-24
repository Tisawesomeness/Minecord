package com.tisawesomeness.minecord.item;

public enum Recipe {
	
	//0
	STONE(RecipeType.SMELTING),
	GRANITE(RecipeType.SHAPELESS, Version.V1_8),
	POLISHED_GRANITE(Version.V1_8),
	DIORITE(RecipeType.CRAFTING, Version.V1_8),
	POLISHED_DIORITE(Version.V1_8),
	ANDESITE(RecipeType.SHAPELESS, Version.V1_8),
	POLISHED_ANDESITE(Version.V1_8),
	OAK_PLANKS(),
	SPRUCE_PLANKS(),
	BIRCH_PLANKS(),
	JUNGLE_PLANKS(),
	ACACIA_PLANKS(),
	DARK_OAK_PLANKS(),
	
	//10
	SPONGE(RecipeType.SMELTING),
	
	//20
	GLASS(RecipeType.SMELTING),
	LAPIS_LAZULI_BLOCK(),
	DISPENSER("The bow must have full durability."),
	SANDSTONE(),
	CHISELED_SANDSTONE(),
	SMOOTH_SANDSTONE(),
	NOTE_BLOCK(),
	//If you can get the white colored bed icon, please send me it
	BED(RecipeType.CRAFTING_SHAPELESS, "From 1.12 onwards, use any matching glass or dye to color the bed.", true),
	POWERED_RAIL(),
	DETECTOR_RAIL(),
	STICKY_PISTON(),
	
	//30
	PISTON(),
	WOOL(RecipeType.CRAFTING_SHAPELESS, "Combine any wool with any dye to change its color.", true),
	ORANGE_WOOL(RecipeType.SHAPELESS, "Combine any wool with any dye to change its color."),
	MAGENTA_WOOL(RecipeType.SHAPELESS, "Combine any wool with any dye to change its color."),
	LIGHT_BLUE_WOOL(RecipeType.SHAPELESS, "Combine any wool with any dye to change its color."),
	YELLOW_WOOL(RecipeType.SHAPELESS, "Combine any wool with any dye to change its color."),
	LIME_WOOL(RecipeType.SHAPELESS, "Combine any wool with any dye to change its color."),
	PINK_WOOL(RecipeType.SHAPELESS, "Combine any wool with any dye to change its color."),
	GRAY_WOOL(RecipeType.SHAPELESS, "Combine any wool with any dye to change its color."),
	LIGHT_GRAY_WOOL(RecipeType.SHAPELESS, "Combine any wool with any dye to change its color."),
	CYAN_WOOL(RecipeType.SHAPELESS, "Combine any wool with any dye to change its color."),
	PURPLE_WOOL(RecipeType.SHAPELESS, "Combine any wool with any dye to change its color."),
	BLUE_WOOL(RecipeType.SHAPELESS, "Combine any wool with any dye to change its color."),
	BROWN_WOOL(RecipeType.SHAPELESS, "Combine any wool with any dye to change its color."),
	GREEN_WOOL(RecipeType.SHAPELESS, "Combine any wool with any dye to change its color."),
	RED_WOOL(RecipeType.SHAPELESS, "Combine any wool with any dye to change its color."),
	BLACK_WOOL(RecipeType.SHAPELESS, "Combine any wool with any dye to change its color."),
	
	//40
	GOLD_BLOCK(),
	IRON_BLOCK(),
	STONE_SLAB(),
	SANDSTONE_SLAB(),
	WOODEN_SLAB(),
	COBBLESTONE_SLAB(),
	BRICK_SLAB(),
	STONE_BRICK_SLAB("Any stone brick variant is allowed."),
	NETHER_BRICK_SLAB(),
	QUARTZ_SLAB(),
	BRICKS(),
	TNT(),
	BOOKSHELF(),
	
	//50
	TORCH(true),
	OAK_WOOD_STAIRS(),
	CHEST(),
	DIAMOND_BLOCK(),
	CRAFTING_TABLE(),
	
	//60
	FURNACE(),
	LADDER(),
	RAIL(),
	COBBLESTONE_STAIRS(),
	LEVER(),
	
	//70
	STONE_PRESSURE_PLATE(),
	WOODEN_PRESSURE_PLATE(),
	REDSTONE_TORCH(),
	STONE_BUTTON(),
	
	//80
	SNOW_BLOCK(),
	CLAY_BLOCK(),
	JUKEBOX(),
	OAK_FENCE(),
	GLOWSTONE(),
	
	//90
	JACK_O_LANTERN(),
	STAINED_GLASS("Combine any glass with any dye to change its color."),
	ORANGE_STAINED_GLASS("Combine any glass with any dye to change its color."),
	MAGENTA_STAINED_GLASS("Combine any glass with any dye to change its color."),
	LIGHT_BLUE_STAINED_GLASS("Combine any glass with any dye to change its color."),
	YELLOW_STAINED_GLASS("Combine any glass with any dye to change its color."),
	LIME_STAINED_GLASS("Combine any glass with any dye to change its color."),
	PINK_STAINED_GLASS("Combine any glass with any dye to change its color."),
	GRAY_STAINED_GLASS("Combine any glass with any dye to change its color."),
	LIGHT_GRAY_STAINED_GLASS("Combine any glass with any dye to change its color."),
	CYAN_STAINED_GLASS("Combine any glass with any dye to change its color."),
	PURPLE_STAINED_GLASS("Combine any glass with any dye to change its color."),
	BLUE_STAINED_GLASS("Combine any glass with any dye to change its color."),
	BROWN_STAINED_GLASS("Combine any glass with any dye to change its color."),
	GREEN_STAINED_GLASS("Combine any glass with any dye to change its color."),
	RED_STAINED_GLASS("Combine any glass with any dye to change its color."),
	BLACK_STAINED_GLASS("Combine any glass with any dye to change its color."),
	WOODEN_TRAPDOOR(),
	STONE_BRICKS(),
	MOSSY_STONE_BRICKS(),
	CRACKED_STONE_BRICKS(),
	CHISELED_STONE_BRICKS();
	
	public final Item item;
	public final RecipeType type;
	public final Version version;
	public final String notes;
	public final boolean gif;
	
	private Recipe() {
		this(RecipeType.CRAFTING, null, null, false);
	}
	private Recipe(RecipeType type) {
		this(type, null, null, false);
	}
	private Recipe(Version version) {
		this(RecipeType.CRAFTING, version, null, false);
	}
	private Recipe(String notes) {
		this(RecipeType.CRAFTING, null, notes, false);
	}
	private Recipe(boolean gif) {
		this(RecipeType.CRAFTING, null, null, true);
	}
	private Recipe(RecipeType type, Version version) {
		this(type, version, null, false);
	}
	private Recipe(RecipeType type, String notes) {
		this(type, null, notes, false);
	}
	private Recipe(RecipeType type, boolean gif) {
		this(type, null, null, gif);
	}
	private Recipe(RecipeType type, String notes, boolean gif) {
		this(type, null, notes, gif);
	}
	
	private Recipe(RecipeType type, Version version, String notes, boolean gif) {
		this.item = Item.valueOf(this.name());
		this.type = type;
		this.version = version;
		this.notes = notes;
		this.gif = gif;
	}

}
