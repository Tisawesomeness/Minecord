package com.tisawesomeness.minecord.item;

import java.util.regex.Pattern;

import net.dv8tion.jda.core.EmbedBuilder;

public enum Item {
	
	AIR(0, "Air"),
	STONE(1, "Stone"),
	GRANITE(1, 1, "Granite", Version.V1_8),
	POLISHED_GRANITE(1, 2, "Polished Granite", "polished[^0-9A-Z]*granite", Version.V1_8),
	DIORITE(1, 3, "Diorite", Version.V1_8),
	POLISHED_DIORITE(1, 4, "Polished Diorite", "polished[^0-9A-Z]*diorite", Version.V1_8),
	ANDESITE(1, 5, "Andesite", Version.V1_8),
	POLISHED_ANDESITE(1, 6, "Polished Andesite", "polished[^0-9A-Z]*andesite", Version.V1_8),
	GRASS(2, "Grass"),
	DIRT(3, "Dirt"),
	COARSE_DIRT(3, 1, "Coarse Dirt", "coarse[^0-9A-Z]*dirt"),
	PODZOL(3, 2, "Podzol"),
	COBBLESTONE(4, "Cobblestone", "^[^0-9A-Z]*cobble[^0-9A-Z]*$"),
	OAK_PLANKS(5, "Oak Wood Planks", "^[^0-9A-Z]*(oak[^0-9A-Z]*(wood[^0-9A-Z]*)?)?plank"),
	SPRUCE_PLANKS(5, 1, "Spruce Wood Planks", "spruce[^0-9A-Z]*(wood[^0-9A-Z]*)?plank"),
	BIRCH_PLANKS(5, 2, "Birch Wood Planks", "birch[^0-9A-Z]*(wood[^0-9A-Z]*)?plank"),
	JUNGLE_PLANKS(5, 3, "Jungle Wood Planks", "jungle[^0-9A-Z]*(wood[^0-9A-Z]*)?plank"),
	ACACIA_PLANKS(5, 4, "Acacia Wood Planks", "acacia[^0-9A-Z]*(wood[^0-9A-Z]*)?plank"),
	DARK_OAK_PLANKS(5, 5, "Dark Oak Wood Planks", "dark[^0-9A-Z]*(oak[^0-9A-Z]*(wood[^0-9A-Z]*)?)?plank"),
	OAK_SAPLING(6, "Oak Sapling", "^[^0-9A-Z]*sapling"),
	SPRUCE_SAPLING(6, 1, "Spruce Sapling"),
	BIRCH_SAPLING(6, 2, "Birch Sapling"),
	JUNGLE_SAPLING(6, 3, "Jungle Sapling"),
	ACACIA_SAPLING(6, 4, "Acacia Sapling"),
	DARK_OAK_SAPLING(6, 5, "Dark Oak Sapling"),
	BEDROCK(7, "Bedrock"),
	FLOWING_WATER(8, "Flowing Water"),
	STILL_WATER(9, "Still Water"),
	
	FLOWING_LAVA(10, "Flowing Lava"),
	STILL_LAVA(11, "Still Lava"),
	SAND(12, "Sand"),
	RED_SAND(12, 1, "Red Sand"),
	GRAVEL(13, "Gravel"),
	GOLD_ORE(14, "Gold Ore"),
	IRON_ORE(15, "Iron Ore"),
	COAL_ORE(16, "Coal Ore"),
	OAK_WOOD(17, "Oak Wood", "^[^0-9A-Z]*wood[^0-9A-Z]*$"), //Punch wood
	SPRUCE_WOOD(17, 1, "Spruce Wood"),
	BIRCH_WOOD(17, 2, "Birch Wood"),
	JUNGLE_WOOD(17, 3, "Jungle Wood"),
	ACACIA_WOOD(17, 4, "Acacia Wood"),
	DARK_OAK_WOOD(17, 5, "Dark Oak Wood"),
	OAK_LEAVES(18, "Oak Leaves", "^[^0-9A-Z]*leaves"),
	SPRUCE_LEAVES(18, 1, "Spruce Leaves"),
	BIRCH_LEAVES(18, 2, "Birch Leaves"),
	JUNGLE_LEAVES(18, 3, "Jungle Leaves"),
	SPONGE(19, "Sponge"),
	WET_SPONGE(19, 1, "Wet Sponge"),
	
	GLASS(20, "Glass"),
	LAPIS_LAZULI_ORE(21, "Lapis Lazuli Ore", "lapis[^0-9A-Z]*ore"),
	LAPIS_LAZULI_BLOCK(22, "Lapis Lazuli Block", "lapis[^0-9A-Z]*block"),
	DISPENSER(23, "Dispenser"),
	SANDSTONE(24, "Sandstone"),
	CHISELED_SANDSTONE(24, 1, "Chiseled Sandstone"),
	SMOOTH_SANDSTONE(24, 2, "Smooth Sandstone"),
	NOTE_BLOCK(25, "Note Block"), //Play that noteblock nicely
	BED(26, "Bed"),
	POWERED_RAIL(27, "Powered Rail"),
	DETECTOR_RAIL(28, "Detector Rail"),
	STICKY_PISTON(29, "Sticky Piston"),
	
	COBWEB(30, "Cobweb"),
	DEAD_SHRUB(31, "Dead Shrub"),
	TALL_GRASS(31, 1, "Tall Grass"),
	FERN(31, 2, "Fern"),
	DEAD_BUSH(32, "Dead Bush"), //[chat filter intensifies]
	PISTON(33, "Piston"),
	PISTON_HEAD(34, "Piston Head"),
	WOOL(35, "Wool"),
	ORANGE_WOOL(35, 1, "Orange Wool", "(35)|(wool):orange"),
	MAGENTA_WOOL(35, 2, "Magenta Wool", "(35)|(wool):magenta"),
	LIGHT_BLUE_WOOL(35, 3, "Light Blue Wool", "(aqua[^0-9A-Z]*wool)|((35)|(wool):(aqua)|(light[^0-9A-Z]*blue))"),
	YELLOW_WOOL(35, 4, "Yellow Wool", "(35)|(wool):yellow"),
	LIME_WOOL(35, 5, "Lime Wool", "((35)|(wool):(lime)|(light[^0-9A-Z]*green))|(light[^0-9A-Z]*green[^0-9A-Z]*wool)"),
	PINK_WOOL(35, 6, "Pink Wool", "(35)|(wool):pink"),
	GRAY_WOOL(35, 7, "Gray Wool", "(35)|(wool):gray"),
	LIGHT_GRAY_WOOL(35, 8, "Light Gray Wool", "(35)|(wool):light[^0-9A-Z]*gray"),
	CYAN_WOOL(35, 9, "Cyan Wool", "(35)|(wool):cyan"),
	PURPLE_WOOL(35, 10, "Purple Wool", "(35)|(wool):purple"),
	BLUE_WOOL(35, 11, "Blue Wool", "(35)|(wool):blue"),
	BROWN_WOOL(35, 12, "Brown Wool", "(35)|(wool):brown"),
	GREEN_WOOL(35, 13, "Green Wool", "(35)|(wool):green"),
	RED_WOOL(35, 14, "Red Wool", "(35)|(wool):red"),
	BLACK_WOOL(35, 15, "Black Wool", "(35)|(wool):black"),
	//Block 36 is a technical block for the tile entity being moved by a piston
	DANDELION(37, "Dandelion", "yellow[^0-9A-Z]*flower"),
	POPPY(38, "Poppy", "(rose)|(red[^0-9A-Z]*flower)"),
	BLUE_ORCHID(38, 1, "Blue Orchid"),
	ALLIUM(38, 2, "Allium"),
	AZURE_BLUET(38, 3, "Azure Bluet"),
	RED_TULIP(38, 4, "Red Tulip"),
	ORANGE_TULIP(38, 5, "Orange Tulip"),
	WHITE_TULIP(38, 6, "White Tulip"),
	PINK_TULIP(38, 7, "Pink Tulip"),
	OXEYE_DAISY(38, 8, "Oxeye Daisy"),
	BROWN_MUSHROOM(39, "Brown Mushroom"),
	
	RED_MUSHROOM(40, "Red Mushroom"),
	GOLD_BLOCK(41, "Gold Block"),
	IRON_BLOCK(42, "Iron Block"),
	DOUBLE_STONE_SLAB(43, "Double Stone Slab"),
	DOUBLE_SANDSTONE_SLAB(43, 1, "Double Sandstone Slab"),
	DOUBLE_WOODEN_SLAB(43, 2, "Double Wooden Slab"),
	DOUBLE_COBBLESTONE_SLAB(43, 3, "Double Cobblestone Slab"),
	DOUBLE_BRICK_SLAB(43, 4, "Double Brick Slab"),
	DOUBLE_STONE_BRICK_SLAB(43, 5, "Double Stone Brick Slab"),
	DOUBLE_NETHER_BRICK_SLAB(43, 6, "Double Nether Brick Slab"),
	DOUBLE_QUARTZ_SLAB(43, 7, "Double Quartz Slab"),
	STONE_SLAB(44, "Stone Slab"),
	SANDSTONE_SLAB(44, 1, "Sandstone Slab"),
	WOODEN_SLAB(44, 2, "Wooden Slab"),
	COBBLESTONE_SLAB(44, 3, "Cobblestone Slab"),
	BRICK_SLAB(44, 4, "Brick Slab"),
	STONE_BRICK_SLAB(44, 5, "Stone Brick Slab"),
	NETHER_BRICK_SLAB(44, 6, "Nether Brick Slab"),
	QUARTZ_SLAB(44, 7, "Quartz Slab"),
	BRICKS(45, "Bricks"), //Brown bricks
	TNT(46, "TNT"), //The first thing we all used when we got Minecraft
	BOOKSHELF(47, "Bookshelf"), //You need 15 to get full enchants. You're welcome.
	MOSSY_COBBLESTONE(48, "Mossy Cobblestone"),
	OBSIDIAN(49, "Obsidian"),
	
	TORCH(50, "Torch"),
	FIRE(51, "Fire"),
	MONSTER_SPAWNER(52, "Monster Spawner"),
	OAK_WOOD_STAIRS(53, "Oak Wood Stairs", "^[^0-9A-Z]*(oak[^0-9A-Z]*)?stair"),
	CHEST(54, "Chest"),
	REDSTONE_WIRE(55, "Redstone Wire"),
	DIAMOND_ORE(56, "Diamond Ore"), //Please mine around
	DIAMOND_BLOCK(57, "Diamond Block"),
	CRAFTING_TABLE(58, "Crafting Table"),
	WHEAT_CROPS(59, "Wheat Crops"),
	
	FARMLAND(60, "Farmland"),
	FURNACE(61, "Furnace"),
	BURNING_FURNACE(62, "Burning Furnace"),
	STANDING_SIGN(63, "Standing Sign"),
	OAK_DOOR_BLOCK(64, "Oak Door Block"),
	LADDER(65, "Ladder"),
	RAIL(66, "Rail"),
	COBBLESTONE_STAIRS(67, "Cobblestone Stairs", "cobble(stone)?[^0-9A-Z]stairs?"),
	WALL_SIGN(68, "Wall-mounted Sign Block"),
	LEVER(69, "Lever"),
	
	STONE_PRESSURE_PLATE(70, "Stone Pressure Plate"),
	IRON_DOOR_BLOCK(71, "Iron Door Block"),
	WOODEN_PRESSURE_PLATE(72, "Wooden Pressure Plate"),
	REDSTONE_ORE(73, "Redstone Ore"),
	GLOWING_REDSTONE_ORE(74, "Glowing Redstone Ore"),
	UNLIT_REDSTONE_TORCH(75, "Redstone Torch (off)"),
	REDSTONE_TORCH(76, "Redstone Torch"),
	STONE_BUTTON(77, "Stone Button"),
	SNOW(78, "Snow Layer"),
	ICE(79, "Ice"),
	
	SNOW_BLOCK(80, "Snow Block"),
	CACTUS(81, "Cactus"),
	CLAY(82, "Clay Block"),
	SUGAR_CANE_BLOCK(83, "Sugar Cane Block"),
	JUKEBOX(84, "Jukebox"),
	OAK_FENCE(85, "Oak Fence", "^[^0-9A-Z]*(oak[^0-9A-Z]*(wood[^0-9A-Z]*)?)?fence"),
	PUMPKIN(86, "Pumpkin"),
	NETHERRACK(87, "Netherrack"),
	SOUL_SAND(88, "Soul Sand"),
	GLOWSTONE(89, "Glowstone"),
	
	NETHER_PORTAL(90, "Nether Portal", "^[^0-9A-Z]*portal"),
	JACK_O_LANTERN(91, "Jack o'Lantern"),
	CAKE_BLOCK(92, "Cake Block"),
	UNPOWERED_REDSTONE_REPEATER_BLOCK(93, "Redstone Repeater Block (off)"),
	REDSTONE_REPEATER_BLOCK(94, "Redstone Repeater Block (off)"),
	STAINED_GLASS(95, "Stained Glass"),
	ORANGE_STAINED_GLASS(95, 1, "Orange Stained Glass", "(95)|(stained[^0-A-Z]*glass):orange"),
	MAGENTA_STAINED_GLASS(95, 2, "Magenta Stained Glass", "(95)|(stained[^0-A-Z]*glass):magenta"),
	LIGHT_BLUE_STAINED_GLASS(95, 3, "Light Blue Stained Glass", "(aqua[^0-9A-Z]*stained[^0-A-Z]*glass)|((95)|(stained[^0-A-Z]*glass):(aqua)|(light[^0-9A-Z]*blue))"),
	YELLOW_STAINED_GLASS(95, 4, "Yellow Stained Glass", "(95)|(stained[^0-A-Z]*glass):yellow"),
	LIME_STAINED_GLASS(95, 5, "Lime Stained Glass", "((95)|(stained[^0-A-Z]*glass):(lime)|(light[^0-9A-Z]*green))|(light[^0-9A-Z]*green[^0-9A-Z]*stained[^0-A-Z]*glass)"),
	PINK_STAINED_GLASS(95, 6, "Pink Stained Glass", "(95)|(stained[^0-A-Z]*glass):pink"),
	GRAY_STAINED_GLASS(95, 7, "Gray Stained Glass", "(95)|(stained[^0-A-Z]*glass):gray"),
	LIGHT_GRAY_STAINED_GLASS(95, 8, "Light Gray Stained Glass", "(95)|(stained[^0-A-Z]*glass):light[^0-9A-Z]*gray"),
	CYAN_STAINED_GLASS(95, 9, "Cyan Stained Glass", "(95)|(stained[^0-A-Z]*glass):cyan"),
	PURPLE_STAINED_GLASS(95, 10, "Purple Stained Glass", "(95)|(stained[^0-A-Z]*glass):purple"),
	BLUE_STAINED_GLASS(95, 11, "Blue Stained Glass", "(95)|(stained[^0-A-Z]*glass):blue"),
	BROWN_STAINED_GLASS(95, 12, "Brown Stained Glass", "(95)|(stained[^0-A-Z]*glass):brown"),
	GREEN_STAINED_GLASS(95, 13, "Green Stained Glass", "(95)|(stained[^0-A-Z]*glass):green"),
	RED_STAINED_GLASS(95, 14, "Red Stained Glass", "(95)|(stained[^0-A-Z]*glass):red"),
	BLACK_STAINED_GLASS(95, 15, "Black Stained Glass", "(95)|(stained[^0-A-Z]*glass):black"),
	WOODEN_TRAPDOOR(96, "Wooden Trapdoor", "wood(en)?[^0-9A-Z]*trapdoor"),
	STONE_MONSTER_EGG(97, "Stone Monster Egg"),
	COBBLESTONE_MONSTER_EGG(97, 1, "Cobblestone Monster Egg", "cobble[^0-9A-Z]*monster[^0-9A-Z]*egg"),
	STONE_BRICK_MONSTER_EGG(97, 2, "Stone Brick Monster Egg"),
	MOSSY_STONE_BRICK_MONSTER_EGG(97, 3, "Mossy Stone Brick Monster Egg", "mossy?[^0-9A-Z]*stone[^0-9A-Z]*brick[^0-9A-Z]*monster[^0-9A-Z]*egg"),
	CRACKED_STONE_BRICK_MONSTER_EGG(97, 4, "Cracked Stone Brick Monster Egg"),
	CHISELED_STONE_BRICK_MONSTER_EGG(97, 5, "Chiseled Stone Brick Monster Egg"),
	STONE_BRICKS(98, "Stone Bricks", "stone[^0-9A-Z]*brick"),
	MOSSY_STONE_BRICKS(98, 1, "Mossy Stone Bricks", "mossy?[^0-9A-Z]*stone[^0-9A-Z]*bricks?"),
	CRACKED_STONE_BRICKS(98, 2, "Cracked Stone Bricks", "cracked[^0-9A-Z]*stone[^0-9A-Z]*brick"),
	CHISELED_STONE_BRICKS(98, 3, "Chiseled Stone Bricks", "chiseled[^0-9A-Z]*stone[^0-9A-Z]*brick"),
	BROWN_MUSHROOM_BLOCK(99, "Brown Mushroom Block"),
	
	RED_MUSHROOM_BLOCK(100, "Red Mushroom Block");
	
	public final int id;
	public final int data;
	public final String name;
	public final String regex;
	public final Version version;
	public final Tool tool;
	public final int enchantability;
	
	private Item(int id, String name) {
		this(id, 0, name, null, null, null, 0);
	}
	private Item(int id, int data, String name) {
		this(id, data, name, null, null, null, 0);
	}
	private Item(int id, String name, String regex) {
		this(id, 0, name, regex, null, null, 0);
	}
	private Item(int id, int data, String name, String regex) {
		this(id, data, name, regex, null, null, 0);
	}
	private Item(int id, int data, String name, Version version) {
		this(id, data, name, null, version, null, 0);
	}
	private Item(int id, int data, String name, String regex, Version version) {
		this(id, data, name, regex, version, null, 0);
	}
	private Item(int id, int data, String name, String regex, Tool tool, int enchantability) {
		this(id, data, name, regex, null, tool, 0);
	}
	
	private Item(int id, int data, String name, String regex, Version version, Tool tool, int enchantability) {
		this.id = id;
		this.data = data;
		this.name = name;
		this.regex = regex;
		this.version = version;
		this.tool = tool;
		this.enchantability = enchantability;
	}
	
	public boolean matches(String str) {
		//Item id and data
		String pattern = "^[ ]*" + id;
		if (data != 0) pattern += "[ ]*[:;|/.,\\-_~][ ]*" + data;
		if (Pattern.compile(pattern + "([^0-9:]|$)").matcher(str).find()) return true;
		//Display name
		if (name.equalsIgnoreCase(str)) return true;
		//Predefined regex
		if (regex != null && Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(str).find()) return true;
		//Display name regex
		String displayRegex = "^[^0-9A-Z]*";
		for (String s : name.split(" ")) {
			displayRegex += s + "[^0-9A-Z]*";
		}
		displayRegex += "$";
		return name.contains(" ") && Pattern.compile(displayRegex, Pattern.CASE_INSENSITIVE).matcher(str).find();
	}
	
	public EmbedBuilder getInfo() {
		EmbedBuilder eb = new EmbedBuilder();
		String desc = "Id: `" + id + "`\nData: `" + data + "`";
		
		//If a recipe for this item exists
		try {
			Recipe r = Recipe.valueOf(this.name());
			if (r.notes != null) desc += "\n" + r.notes;
			if (r.version != null) desc += "\n" + r.version.toString();
			String ext = ".png";
			if (r.gif) ext = ".gif";
			eb.setImage("https://minecord.github.io/recipes/" + id + "-" + data + ext);
		} catch (IllegalArgumentException ex) {
			if (version != null) desc += "\n" + version.toString();
			eb.setThumbnail("https://minecord.github.io/items/" + id + "-" + data + ".png");
		}
		
		eb.setDescription(desc);
		return eb;
	}
	
}
