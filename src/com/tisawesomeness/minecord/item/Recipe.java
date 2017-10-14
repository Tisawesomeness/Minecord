package com.tisawesomeness.minecord.item;

public enum Recipe {
	
	STONE(RecipeType.SMELTING),
	GRANITE(RecipeType.SHAPELESS, Version.V1_8),
	POLISHED_GRANITE(RecipeType.CRAFTING, Version.V1_8),
	DIORITE(RecipeType.CRAFTING, Version.V1_8),
	POLISHED_DIORITE(RecipeType.CRAFTING, Version.V1_8),
	ANDESITE(RecipeType.SHAPELESS, Version.V1_8),
	POLISHED_ANDESITE(RecipeType.CRAFTING, Version.V1_8),
	OAK_PLANKS(RecipeType.CRAFTING),
	SPRUCE_PLANKS(RecipeType.CRAFTING),
	BIRCH_PLANKS(RecipeType.CRAFTING),
	JUNGLE_PLANKS(RecipeType.CRAFTING),
	ACACIA_PLANKS(RecipeType.CRAFTING),
	DARK_OAK_PLANKS(RecipeType.CRAFTING),
	
	SPONGE(RecipeType.SMELTING),
	
	GLASS(RecipeType.SMELTING),
	LAPIS_LAZULI_BLOCK(RecipeType.CRAFTING),
	DISPENSER(RecipeType.CRAFTING, "The bow must have full durability."),
	SANDSTONE(RecipeType.CRAFTING),
	CHISELED_SANDSTONE(RecipeType.CRAFTING),
	SMOOTH_SANDSTONE(RecipeType.CRAFTING),
	NOTE_BLOCK(RecipeType.CRAFTING),
	//If you can get the white colored bed icon, please send me it
	BED(RecipeType.CRAFTING_SHAPELESS, "From 1.12 onwards, use any matching wool or dye to color the bed.", true),
	POWERED_RAIL(RecipeType.CRAFTING),
	DETECTOR_RAIL(RecipeType.CRAFTING),
	STICKY_PISTON(RecipeType.CRAFTING),
	
	PISTON(RecipeType.CRAFTING),
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
	
	GOLD_BLOCK(RecipeType.CRAFTING),
	IRON_BLOCK(RecipeType.CRAFTING),
	STONE_SLAB(RecipeType.CRAFTING),
	SANDSTONE_SLAB(RecipeType.CRAFTING),
	WOODEN_SLAB(RecipeType.CRAFTING),
	COBBLESTONE_SLAB(RecipeType.CRAFTING),
	BRICK_SLAB(RecipeType.CRAFTING),
	STONE_BRICK_SLAB(RecipeType.CRAFTING, "Any stone brick variant is allowed."),
	NETHER_BRICK_SLAB(RecipeType.CRAFTING),
	QUARTZ_SLAB(RecipeType.CRAFTING),
	BRICKS(RecipeType.CRAFTING),
	TNT(RecipeType.CRAFTING),
	BOOKSHELF(RecipeType.CRAFTING),
	
	TORCH(RecipeType.CRAFTING, true);
	
	public final Item item;
	public final RecipeType type;
	public final Version version;
	public final String notes;
	public final boolean gif;
	
	private Recipe(RecipeType type) {
		this(type, null, null, false);
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
