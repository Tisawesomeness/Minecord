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
	TNT(true),
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
	MOSSY_STONE_BRICKS(RecipeType.SHAPELESS),
	CRACKED_STONE_BRICKS(RecipeType.SMELTING),
	CHISELED_STONE_BRICKS(),
	
	
	
	//100
	IRON_BARS(),
	GLASS_PANE(),
	OAK_FENCE_GATE(),
	BRICK_STAIRS(),
	STONE_BRICK_STAIRS(),
	
	//110
	NETHER_BRICK(),
	NETHER_BRICK_FENCE(),
	NETHER_BRICK_STAIRS(),
	ENCHANTMENT_TABLE(),
	
	//120
	REDSTONE_LAMP(),
	OAK_WOOD_SLAB(),
	BIRCH_WOOD_SLAB(),
	SPRUCE_WOOD_SLAB(),
	JUNGLE_WOOD_SLAB(),
	ACACIA_WOOD_SLAB(),
	DARK_OAK_WOOD_SLAB(),
	SANDSTONE_STAIRS(),
	
	//130
	ENDER_CHEST(),
	TRIPWIRE_HOOK(),
	EMERALD_BLOCK(),
	SPRUCE_STAIRS(),
	BIRCH_STAIRS(),
	JUNGLE_STAIRS(),
	BEACON(),
	COBBLESTONE_WALL(),
	MOSSY_COBBLESTONE_WALL(),
	
	//140
	WOODEN_BUTTON(),
	ANVIL(),
	TRAPPED_CHEST(RecipeType.SHAPELESS),
	LIGHT_WEIGHTED_PRESSURE_PLATE(),
	HEAVY_WEIGHTED_PRESSURE_PLATE(),
	
	//150
	DAYLIGHT_DETECTOR(),
	REDSTONE_BLOCK(),
	HOPPER(),
	QUARTZ_BLOCK(),
	CHISELED_QUARTZ_BLOCK(),
	PILLAR_QUARTZ_BLOCK(),
	QUARTZ_STAIRS(),
	ACTIVATOR_RAIL(),
	DROPPER(),
	STAINED_HARDENED_CLAY(),
	ORANGE_STAINED_HARDENED_CLAY(),
	MAGENTA_STAINED_HARDENED_CLAY(),
	LIGHT_BLUE_STAINED_HARDENED_CLAY(),
	YELLOW_STAINED_HARDENED_CLAY(),
	LIME_STAINED_HARDENED_CLAY(),
	PINK_STAINED_HARDENED_CLAY(),
	GRAY_STAINED_HARDENED_CLAY(),
	LIGHT_GRAY_STAINED_HARDENED_CLAY(),
	CYAN_STAINED_HARDENED_CLAY(),
	PURPLE_STAINED_HARDENED_CLAY(),
	BLUE_STAINED_HARDENED_CLAY(),
	BROWN_STAINED_HARDENED_CLAY(),
	GREEN_STAINED_HARDENED_CLAY(),
	RED_STAINED_HARDENED_CLAY(),
	BLACK_STAINED_HARDENED_CLAY(),
	
	//160
	STAINED_GLASS_PANE(),
	ORANGE_STAINED_GLASS_PANE(),
	MAGENTA_STAINED_GLASS_PANE(),
	LIGHT_BLUE_STAINED_GLASS_PANE(),
	YELLOW_STAINED_GLASS_PANE(),
	LIME_STAINED_GLASS_PANE(),
	PINK_STAINED_GLASS_PANE(),
	GRAY_STAINED_GLASS_PANE(),
	LIGHT_GRAY_STAINED_GLASS_PANE(),
	CYAN_STAINED_GLASS_PANE(),
	PURPLE_STAINED_GLASS_PANE(),
	BLUE_STAINED_GLASS_PANE(),
	BROWN_STAINED_GLASS_PANE(),
	GREEN_STAINED_GLASS_PANE(),
	RED_STAINED_GLASS_PANE(),
	BLACK_STAINED_GLASS_PANE(),
	ACACIA_STAIRS(),
	DARK_OAK_STAIRS(),
	SLIME_BLOCK(Version.V1_8),
	IRON_TRAPDOOR(Version.V1_8),
	PRISMARINE(Version.V1_8),
	PRISMARINE_BRICKS(Version.V1_8),
	DARK_PRISMARINE(Version.V1_8),
	SEA_LANTERN(Version.V1_8),
	
	//170
	HAY_BLOCK(),
	CARPET(),
	ORANGE_CARPET(),
	MAGENTA_CARPET(),
	LIGHT_BLUE_CARPET(),
	YELLOW_CARPET(),
	LIME_CARPET(),
	PINK_CARPET(),
	GRAY_CARPET(),
	LIGHT_GRAY_CARPET(),
	CYAN_CARPET(),
	PURPLE_CARPET(),
	BLUE_CARPET(),
	BROWN_CARPET(),
	GREEN_CARPET(),
	RED_CARPET(),
	BLACK_CARPET(),
	HARDENED_CLAY(RecipeType.SMELTING),
	COAL_BLOCK(),
	RED_SANDSTONE(Version.V1_8),
	CHISELED_RED_SANDSTONE(Version.V1_8),
	SMOOTH_RED_SANDSTONE(Version.V1_8),
	
	//180
	RED_SANDSTONE_STAIRS(Version.V1_8),
	STONE_SLAB2(Version.V1_8),
	SPRUCE_FENCE_GATE(Version.V1_8),
	BIRCH_FENCE_GATE(Version.V1_8),
	JUNGLE_FENCE_GATE(Version.V1_8),
	DARK_OAK_FENCE_GATE(Version.V1_8),
	ACACIA_FENCE_GATE(Version.V1_8),
	SPRUCE_FENCE(Version.V1_8),
	BIRCH_FENCE(Version.V1_8),
	
	//190
	JUNGLE_FENCE(Version.V1_8),
	DARK_OAK_FENCE(Version.V1_8),
	ACACIA_FENCE(Version.V1_8),
	END_ROD(Version.V1_9),
	
	
	
	//200
	PURPUR_BLOCK(Version.V1_9),
	PURPUR_PILLAR(Version.V1_9),
	PURPUR_STAIRS(Version.V1_9),
	PURPUR_SLAB(Version.V1_9),
	END_BRICKS(Version.V1_9),
	
	//210
	MAGMA(Version.V1_10),
	NETHER_WART_BLOCK(Version.V1_10),
	RED_NETHER_BRICK(Version.V1_10),
	BONE_BLOCK(Version.V1_10),
	OBSERVER(Version.V1_11),
	WHITE_SHULKER_BOX(Version.V1_9, "Combine any shulker box with any dye to change its color."),
	
	//220
	ORANGE_SHULKER_BOX(Version.V1_9, "Combine any shulker box with any dye to change its color."),
	MAGENTA_SHULKER_BOX(Version.V1_9, "Combine any shulker box with any dye to change its color."),
	LIGHT_BLUE_SHULKER_BOX(Version.V1_9, "Combine any shulker box with any dye to change its color."),
	YELLOW_SHULKER_BOX(Version.V1_9, "Combine any shulker box with any dye to change its color."),
	LIME_SHULKER_BOX(Version.V1_9, "Combine any shulker box with any dye to change its color."),
	PINK_SHULKER_BOX(Version.V1_9, "Combine any shulker box with any dye to change its color."),
	GRAY_SHULKER_BOX(Version.V1_9, "Combine any shulker box with any dye to change its color."),
	SILVER_SHULKER_BOX(Version.V1_9, "Combine any shulker box with any dye to change its color."),
	CYAN_SHULKER_BOX(Version.V1_9, "Combine any shulker box with any dye to change its color."),
	PURPLE_SHULKER_BOX(Version.V1_9, "Combine any shulker box with any dye to change its color.", true),
	
	//230
	BLUE_SHULKER_BOX(Version.V1_9, "Combine any shulker box with any dye to change its color."),
	BROWN_SHULKER_BOX(Version.V1_9, "Combine any shulker box with any dye to change its color."),
	GREEN_SHULKER_BOX(Version.V1_9, "Combine any shulker box with any dye to change its color."),
	RED_SHULKER_BOX(Version.V1_9, "Combine any shulker box with any dye to change its color."),
	BLACK_SHULKER_BOX(Version.V1_9, "Combine any shulker box with any dye to change its color."),
	WHITE_GLAZED_TERRACOTTA(Version.V1_12),
	ORANGE_GLAZED_TERRACOTTA(Version.V1_12),
	MAGENTA_GLAZED_TERRACOTTA(Version.V1_12),
	LIGHT_BLUE_GLAZED_TERRACOTTA(Version.V1_12),
	YELLOW_GLAZED_TERRACOTTA(Version.V1_12),
	
	//240
	LIME_GLAZED_TERRACOTTA(Version.V1_12),
	PINK_GLAZED_TERRACOTTA(Version.V1_12),
	GRAY_GLAZED_TERRACOTTA(Version.V1_12),
	LIGHT_GRAY_GLAZED_TERRACOTTA(Version.V1_12),
	CYAN_GLAZED_TERRACOTTA(Version.V1_12),
	PURPLE_GLAZED_TERRACOTTA(Version.V1_12),
	BLUE_GLAZED_TERRACOTTA(Version.V1_12),
	BROWN_GLAZED_TERRACOTTA(Version.V1_12),
	GREEN_GLAZED_TERRACOTTA(Version.V1_12),
	RED_GLAZED_TERRACOTTA(Version.V1_12),
	
	//250
	BLACK_GLAZED_TERRACOTTA(Version.V1_12),
	CONCRETE_POWDER(RecipeType.SHAPELESS, Version.V1_12),
	ORANGE_CONCRETE_POWDER(RecipeType.SHAPELESS, Version.V1_12),
	MAGENTA_CONCRETE_POWDER(RecipeType.SHAPELESS, Version.V1_12),
	LIGHT_BLUE_CONCRETE_POWDER(RecipeType.SHAPELESS, Version.V1_12),
	YELLOW_CONCRETE_POWDER(RecipeType.SHAPELESS, Version.V1_12),
	LIME_CONCRETE_POWDER(RecipeType.SHAPELESS, Version.V1_12),
	PINK_CONCRETE_POWDER(RecipeType.SHAPELESS, Version.V1_12),
	GRAY_CONCRETE_POWDER(RecipeType.SHAPELESS, Version.V1_12),
	LIGHT_GRAY_CONCRETE_POWDER(RecipeType.SHAPELESS, Version.V1_12),
	CYAN_CONCRETE_POWDER(RecipeType.SHAPELESS, Version.V1_12),
	PURPLE_CONCRETE_POWDER(RecipeType.SHAPELESS, Version.V1_12),
	BLUE_CONCRETE_POWDER(RecipeType.SHAPELESS, Version.V1_12),
	BROWN_CONCRETE_POWDER(RecipeType.SHAPELESS, Version.V1_12),
	GREEN_CONCRETE_POWDER(RecipeType.SHAPELESS, Version.V1_12),
	RED_CONCRETE_POWDER(RecipeType.SHAPELESS, Version.V1_12),
	BLACK_CONCRETE_POWDER(RecipeType.SHAPELESS, Version.V1_12);
	
	
	
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
		this(RecipeType.CRAFTING, null, null, gif);
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
	private Recipe(Version version, String notes) {
		this(RecipeType.CRAFTING, version, notes, false);
	}
	private Recipe(Version version, String notes, boolean gif) {
		this(RecipeType.CRAFTING, version, notes, gif);
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
