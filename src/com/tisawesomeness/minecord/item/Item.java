package com.tisawesomeness.minecord.item;

import java.util.regex.Pattern;

import net.dv8tion.jda.core.EmbedBuilder;

/**
 * A list of nearly every item in Minecraft. If the item has a recipe,
 * there will be a corresponding entry in the Recipe enum.
 * @author Tis_awesomeness
 */
public enum Item {
	
	AIR(0, "Air"),
	STONE(1, "Stone"),
	GRANITE(1, 1, "Granite", "stone[ ]*[:;|/.,\\-_~][ ]*1", Version.V1_8),
	POLISHED_GRANITE(1, 2, "Polished Granite", "(polished[^0-9A-Z]*granite)|(stone[ ]*[:;|/.,\\-_~][ ]*2)", Version.V1_8),
	DIORITE(1, 3, "Diorite", "stone[ ]*[:;|/.,\\-_~][ ]*3", Version.V1_8),
	POLISHED_DIORITE(1, 4, "Polished Diorite", "(polished[^0-9A-Z]*diorite)|(stone[ ]*[:;|/.,\\-_~][ ]*4)", Version.V1_8),
	ANDESITE(1, 5, "Andesite", "stone[ ]*[:;|/.,\\-_~][ ]*5", Version.V1_8),
	POLISHED_ANDESITE(1, 6, "Polished Andesite", "(polished[^0-9A-Z]*andesite)|(stone[ ]*[:;|/.,\\-_~][ ]*6)", Version.V1_8),
	GRASS(2, "Grass"),
	DIRT(3, "Dirt"),
	COARSE_DIRT(3, 1, "Coarse Dirt", "(coarse[^0-9A-Z]*dirt)|(dirt[ ]*[:;|/.,\\-_~][ ]*1)"),
	PODZOL(3, 2, "Podzol"),
	COBBLESTONE(4, "Cobblestone", "^cobble[^0-9A-Z]*$"),
	OAK_PLANKS(5, "Oak Wood Planks", "^(oak[^0-9A-Z]*(wood[^0-9A-Z]*)?)?plank"),
	SPRUCE_PLANKS(5, 1, "Spruce Wood Planks", "(spruce[^0-9A-Z]*(wood[^0-9A-Z]*)?plank)|((wood[^0-9A-Z]*)?plank[ ]*[:;|/.,\\-_~][ ]*1)"),
	BIRCH_PLANKS(5, 2, "Birch Wood Planks", "(birch[^0-9A-Z]*(wood[^0-9A-Z]*)?plank)|((wood[^0-9A-Z]*)?plank[ ]*[:;|/.,\\-_~][ ]*2)"),
	JUNGLE_PLANKS(5, 3, "Jungle Wood Planks", "(jungle[^0-9A-Z]*(wood[^0-9A-Z]*)?plank)|((wood[^0-9A-Z]*)?plank[ ]*[:;|/.,\\-_~][ ]*3)"),
	ACACIA_PLANKS(5, 4, "Acacia Wood Planks", "(acacia[^0-9A-Z]*(wood[^0-9A-Z]*)?plank)|((wood[^0-9A-Z]*)?plank[ ]*[:;|/.,\\-_~][ ]*4)"),
	DARK_OAK_PLANKS(5, 5, "Dark Oak Wood Planks", "(dark[^0-9A-Z]*(oak[^0-9A-Z]*(wood[^0-9A-Z]*)?)?plank)|((wood[^0-9A-Z]*)?plank[ ]*[:;|/.,\\-_~][ ]*5)"),
	OAK_SAPLING(6, "Oak Sapling", "^sapling"),
	SPRUCE_SAPLING(6, 1, "Spruce Sapling", "sapling[ ]*[:;|/.,\\-_~][ ]*1"),
	BIRCH_SAPLING(6, 2, "Birch Sapling", "sapling[ ]*[:;|/.,\\-_~][ ]*2"),
	JUNGLE_SAPLING(6, 3, "Jungle Sapling", "sapling[ ]*[:;|/.,\\-_~][ ]*3"),
	ACACIA_SAPLING(6, 4, "Acacia Sapling", "sapling[ ]*[:;|/.,\\-_~][ ]*4"),
	DARK_OAK_SAPLING(6, 5, "Dark Oak Sapling", "(dark[^0-9A-Z]*sapling)|(sapling[ ]*[:;|/.,\\-_~][ ]*5)"),
	BEDROCK(7, "Bedrock"),
	FLOWING_WATER(8, "Flowing Water"),
	STILL_WATER(9, "Still Water"),
	
	FLOWING_LAVA(10, "Flowing Lava"),
	STILL_LAVA(11, "Still Lava"),
	SAND(12, "Sand"),
	RED_SAND(12, 1, "Red Sand", "sand[ ]*[:;|/.,\\-_~][ ]*1"),
	GRAVEL(13, "Gravel"),
	GOLD_ORE(14, "Gold Ore"),
	IRON_ORE(15, "Iron Ore"),
	COAL_ORE(16, "Coal Ore"),
	OAK_WOOD(17, "Oak Wood", "^(wood)|(log)$"), //Punch wood
	SPRUCE_WOOD(17, 1, "Spruce Wood", "(wood)|(log)[ ]*[:;|/.,\\-_~][ ]*1"),
	BIRCH_WOOD(17, 2, "Birch Wood", "(wood)|(log)[ ]*[:;|/.,\\-_~][ ]*2"),
	JUNGLE_WOOD(17, 3, "Jungle Wood", "(wood)|(log)[ ]*[:;|/.,\\-_~][ ]*3"),
	OAK_LEAVES(18, "Oak Leaves", "^leaves"),
	SPRUCE_LEAVES(18, 1, "Spruce Leaves", "leaves[ ]*[:;|/.,\\-_~][ ]*1"),
	BIRCH_LEAVES(18, 2, "Birch Leaves", "leaves[ ]*[:;|/.,\\-_~][ ]*2"),
	JUNGLE_LEAVES(18, 3, "Jungle Leaves", "leaves[ ]*[:;|/.,\\-_~][ ]*3"),
	SPONGE(19, "Sponge"),
	WET_SPONGE(19, 1, "Wet Sponge", "sponge[ ]*[:;|/.,\\-_~][ ]*1", Version.V1_8),
	
	GLASS(20, "Glass"),
	LAPIS_LAZULI_ORE(21, "Lapis Lazuli Ore", "lapis[^0-9A-Z]*ore"),
	LAPIS_LAZULI_BLOCK(22, "Lapis Lazuli Block", "lapis[^0-9A-Z]*block"),
	DISPENSER(23, "Dispenser"),
	SANDSTONE(24, "Sandstone"),
	CHISELED_SANDSTONE(24, 1, "Chiseled Sandstone", "sandstone[ ]*[:;|/.,\\-_~][ ]*1"),
	SMOOTH_SANDSTONE(24, 2, "Smooth Sandstone", "sandstone[ ]*[:;|/.,\\-_~][ ]*2"),
	NOTE_BLOCK(25, "Note Block"), //Play that noteblock nicely
	BED_BLOCK(26, "Bed Block"),
	POWERED_RAIL(27, "Powered Rail"),
	DETECTOR_RAIL(28, "Detector Rail"),
	STICKY_PISTON(29, "Sticky Piston"),
	
	COBWEB(30, "Cobweb"),
	DEAD_SHRUB(31, "Dead Shrub"),
	TALL_GRASS(31, 1, "Tall Grass", "(dead[^0-9A-Z]*shrub[ ]*[:;|/.,\\-_~][ ]*1)|(weed)"),
	FERN(31, 2, "Fern", "dead[^0-9A-Z]*shrub[ ]*[:;|/.,\\-_~][ ]*2"),
	DEAD_BUSH(32, "Dead Bush"), //[chat filter intensifies]
	PISTON(33, "Piston"),
	PISTON_HEAD(34, "Piston Head"),
	WOOL(35, "Wool"),
	ORANGE_WOOL(35, 1, "Orange Wool", "((35)|(wool))[ ]*[:;|/.,\\-_~][ ]*((orange)|(1$))"),
	MAGENTA_WOOL(35, 2, "Magenta Wool", "((35)|(wool))[ ]*[:;|/.,\\-_~][ ]*((magenta)|(2$))"),
	LIGHT_BLUE_WOOL(35, 3, "Light Blue Wool", "((aqua)|(light[^0-9A-Z]*blue)[^0-9A-Z]*wool)|(((35)|(wool))[ ]*[:;|/.,\\-_~][ ]*((aqua)|(light[^0-9A-Z]*blue)|(3$)))"),
	YELLOW_WOOL(35, 4, "Yellow Wool", "((35)|(wool))[ ]*[:;|/.,\\-_~][ ]*((yellow)|(4$))"),
	LIME_WOOL(35, 5, "Lime Wool", "(((35)|(wool))[ ]*[:;|/.,\\-_~][ ]*((lime)|(light[^0-9A-Z]*green)|(5$)))|(((light[^0-9A-Z]*green)|(lime))[^0-9A-Z]*wool)"),
	PINK_WOOL(35, 6, "Pink Wool", "((35)|(wool))[ ]*[:;|/.,\\-_~][ ]*((pink)|6)"),
	GRAY_WOOL(35, 7, "Gray Wool", "((35)|(wool))[ ]*[:;|/.,\\-_~][ ]*((gr(a|e)y)|7)"),
	LIGHT_GRAY_WOOL(35, 8, "Light Gray Wool", "((35)|(wool))[ ]*[:;|/.,\\-_~][ ]*((light[^0-9A-Z]*gr(a|e)y)|(silver)|8)"),
	CYAN_WOOL(35, 9, "Cyan Wool", "((35)|(wool))[ ]*[:;|/.,\\-_~][ ]*((cyan)|9)"),
	PURPLE_WOOL(35, 10, "Purple Wool", "((35)|(wool))[ ]*[:;|/.,\\-_~][ ]*((purple)|(10))"),
	BLUE_WOOL(35, 11, "Blue Wool", "((35)|(wool))[ ]*[:;|/.,\\-_~][ ]*((blue)|(11))"),
	BROWN_WOOL(35, 12, "Brown Wool", "((35)|(wool))[ ]*[:;|/.,\\-_~][ ]*((brown)|(12))"),
	GREEN_WOOL(35, 13, "Green Wool", "((35)|(wool))[ ]*[:;|/.,\\-_~][ ]*((green)|(13))"),
	RED_WOOL(35, 14, "Red Wool", "((35)|(wool))[ ]*[:;|/.,\\-_~][ ]*((red)|(14))"),
	BLACK_WOOL(35, 15, "Black Wool", "((35)|(wool))[ ]*[:;|/.,\\-_~][ ]*((black)|(15))"),
	//Block 36 is a technical block for the tile entity being moved by a piston
	DANDELION(37, "Dandelion", "yellow[^0-9A-Z]*flower"),
	POPPY(38, "Poppy", "(rose)|(red[^0-9A-Z]*flower)"),
	BLUE_ORCHID(38, 1, "Blue Orchid", "(red[^0-9A-Z]*)?flower[ ]*[:;|/.,\\-_~][ ]*1"),
	ALLIUM(38, 2, "Allium", "(red[^0-9A-Z]*)?flower[ ]*[:;|/.,\\-_~][ ]*2"),
	AZURE_BLUET(38, 3, "Azure Bluet", "(red[^0-9A-Z]*)?flower[ ]*[:;|/.,\\-_~][ ]*3"),
	RED_TULIP(38, 4, "Red Tulip", "(red[^0-9A-Z]*)?flower[ ]*[:;|/.,\\-_~][ ]*4"),
	ORANGE_TULIP(38, 5, "Orange Tulip", "(red[^0-9A-Z]*)?flower[ ]*[:;|/.,\\-_~][ ]*5"),
	WHITE_TULIP(38, 6, "White Tulip", "(red[^0-9A-Z]*)?flower[ ]*[:;|/.,\\-_~][ ]*6"),
	PINK_TULIP(38, 7, "Pink Tulip", "(red[^0-9A-Z]*)?flower[ ]*[:;|/.,\\-_~][ ]*7"),
	OXEYE_DAISY(38, 8, "Oxeye Daisy", "(red[^0-9A-Z]*)?flower[ ]*[:;|/.,\\-_~][ ]*8"),
	BROWN_MUSHROOM(39, "Brown Mushroom"),
	
	RED_MUSHROOM(40, "Red Mushroom"),
	GOLD_BLOCK(41, "Gold Block"),
	IRON_BLOCK(42, "Iron Block"),
	DOUBLE_STONE_SLAB(43, "Double Stone Slab"),
	DOUBLE_SANDSTONE_SLAB(43, 1, "Double Sandstone Slab", "double[^0-9A-Z]stone[^0-9A-Z]slab[ ]*[:;|/.,\\-_~][ ]*1"),
	DOUBLE_WOODEN_SLAB(43, 2, "Double Wooden Slab", "double[^0-9A-Z]stone[^0-9A-Z]slab[ ]*[:;|/.,\\-_~][ ]*2"),
	DOUBLE_COBBLESTONE_SLAB(43, 3, "Double Cobblestone Slab", "double[^0-9A-Z]stone[^0-9A-Z]slab[ ]*[:;|/.,\\-_~][ ]*3"),
	DOUBLE_BRICK_SLAB(43, 4, "Double Brick Slab", "double[^0-9A-Z]stone[^0-9A-Z]slab[ ]*[:;|/.,\\-_~][ ]*4"),
	DOUBLE_STONE_BRICK_SLAB(43, 5, "Double Stone Brick Slab", "double[^0-9A-Z]stone[^0-9A-Z]slab[ ]*[:;|/.,\\-_~][ ]*5"),
	DOUBLE_NETHER_BRICK_SLAB(43, 6, "Double Nether Brick Slab", "double[^0-9A-Z]stone[^0-9A-Z]slab[ ]*[:;|/.,\\-_~][ ]*6"),
	DOUBLE_QUARTZ_SLAB(43, 7, "Double Quartz Slab", "double[^0-9A-Z]stone[^0-9A-Z]slab[ ]*[:;|/.,\\-_~][ ]*7"),
	STONE_SLAB(44, "Stone Slab"),
	SANDSTONE_SLAB(44, 1, "Sandstone Slab", "stone[^0-9A-Z]slab[ ]*[:;|/.,\\-_~][ ]*1"),
	WOODEN_SLAB(44, 2, "Wooden Slab", "stone[^0-9A-Z]slab[ ]*[:;|/.,\\-_~][ ]*2"),
	COBBLESTONE_SLAB(44, 3, "Cobblestone Slab", "stone[^0-9A-Z]slab[ ]*[:;|/.,\\-_~][ ]*3"),
	BRICK_SLAB(44, 4, "Brick Slab", "stone[^0-9A-Z]slab[ ]*[:;|/.,\\-_~][ ]*4"),
	STONE_BRICK_SLAB(44, 5, "Stone Brick Slab", "stone[^0-9A-Z]slab[ ]*[:;|/.,\\-_~][ ]*5"),
	NETHER_BRICK_SLAB(44, 6, "Nether Brick Slab", "stone[^0-9A-Z]slab[ ]*[:;|/.,\\-_~][ ]*6"),
	QUARTZ_SLAB(44, 7, "Quartz Slab", "stone[^0-9A-Z]slab[ ]*[:;|/.,\\-_~][ ]*7"),
	BRICKS(45, "Bricks"), //Brown bricks
	TNT(46, "TNT"), //The first thing we all used when we got Minecraft
	BOOKSHELF(47, "Bookshelf"), //You need 15 to get full enchants. You're welcome.
	MOSSY_COBBLESTONE(48, "Mossy Cobblestone"),
	OBSIDIAN(49, "Obsidian"),
	
	TORCH(50, "Torch"),
	FIRE(51, "Fire"),
	MONSTER_SPAWNER(52, "Monster Spawner"),
	OAK_WOOD_STAIRS(53, "Oak Wood Stairs", "^wood[^0-9A-Z]*stairs"),
	CHEST(54, "Chest"),
	REDSTONE_WIRE(55, "Redstone Wire", "wire"),
	DIAMOND_ORE(56, "Diamond Ore"), //Please mine around
	DIAMOND_BLOCK(57, "Diamond Block"),
	CRAFTING_TABLE(58, "Crafting Table"),
	WHEAT_CROPS(59, "Wheat Crops"),
	
	FARMLAND(60, "Farmland"),
	FURNACE(61, "Furnace"),
	BURNING_FURNACE(62, "Burning Furnace"),
	STANDING_SIGN(63, "Standing Sign"),
	OAK_DOOR_BLOCK(64, "Oak Door Block", "^(wood(en)?[^0-9A-Z]*)?door[^0-9A-Z]*block"),
	LADDER(65, "Ladder"),
	RAIL(66, "Rail"),
	COBBLESTONE_STAIRS(67, "Cobblestone Stairs", "cobble(stone)?[^0-9A-Z]*stairs?"),
	WALL_SIGN(68, "Wall-mounted Sign Block", "wall[^0-9A-Z]*sign"),
	LEVER(69, "Lever"),
	
	STONE_PRESSURE_PLATE(70, "Stone Pressure Plate"),
	IRON_DOOR_BLOCK(71, "Iron Door Block"),
	WOODEN_PRESSURE_PLATE(72, "Wooden Pressure Plate", "wood[^0-9A-Z]*pressure[^0-9A-Z]*plate"),
	REDSTONE_ORE(73, "Redstone Ore"),
	GLOWING_REDSTONE_ORE(74, "Glowing Redstone Ore"),
	UNLIT_REDSTONE_TORCH(75, "Redstone Torch (off)"),
	REDSTONE_TORCH(76, "Redstone Torch"),
	STONE_BUTTON(77, "Stone Button"),
	SNOW(78, "Snow Layer"),
	ICE(79, "Ice"),
	
	SNOW_BLOCK(80, "Snow Block"),
	CACTUS(81, "Cactus"),
	CLAY_BLOCK(82, "Clay Block"),
	SUGAR_CANE_BLOCK(83, "Sugar Cane Block"),
	JUKEBOX(84, "Jukebox"),
	OAK_FENCE(85, "Oak Fence", "^(oak[^0-9A-Z]*(wood[^0-9A-Z]*)?)?fence$"),
	PUMPKIN(86, "Pumpkin"),
	NETHERRACK(87, "Netherrack"),
	SOUL_SAND(88, "Soul Sand"),
	GLOWSTONE(89, "Glowstone"),
	
	NETHER_PORTAL(90, "Nether Portal", "^portal"),
	JACK_O_LANTERN(91, "Jack o'Lantern", "jack[^0-9A-Z]*o[^0-9A-Z]*lantern"),
	CAKE_BLOCK(92, "Cake Block"),
	REDSTONE_REPEATER_BLOCK(93, "Redstone Repeater Block"),
	POWERED_REDSTONE_REPEATER_BLOCK(94, "Redstone Repeater Block (on)"),
	STAINED_GLASS(95, "Stained Glass"),
	ORANGE_STAINED_GLASS(95, 1, "Orange Stained Glass", "((95)|(stained[^0-9A-Z]*glass))[ ]*[:;|/.,\\-_~][ ]*((orange)|(1$))"),
	MAGENTA_STAINED_GLASS(95, 2, "Magenta Stained Glass", "((95)|(stained[^0-9A-Z]*glass))[ ]*[:;|/.,\\-_~][ ]*((magenta)|(2$))"),
	LIGHT_BLUE_STAINED_GLASS(95, 3, "Light Blue Stained Glass", "(((aqua)|(light[^0-9A-Z]*blue))[^0-9A-Z]*stained[^0-9A-Z]*glass)|(((95)|(stained[^0-9A-Z]*glass))[ ]*[:;|/.,\\-_~][ ]*((aqua)|(light[^0-9A-Z]*blue)|(3$)))"),
	YELLOW_STAINED_GLASS(95, 4, "Yellow Stained Glass", "((95)|(stained[^0-9A-Z]*glass))[ ]*[:;|/.,\\-_~][ ]*((yellow)|(4$))"),
	LIME_STAINED_GLASS(95, 5, "Lime Stained Glass", "(((95)|(stained[^0-9A-Z]*glass))[ ]*[:;|/.,\\-_~][ ]*((lime)|(light[^0-9A-Z]*green)|(5$)))|(((light[^0-9A-Z]*green)|(lime))[^0-9A-Z]*stained[^0-9A-Z]*glass)"),
	PINK_STAINED_GLASS(95, 6, "Pink Stained Glass", "((95)|(stained[^0-9A-Z]*glass))[ ]*[:;|/.,\\-_~][ ]*((pink)|6)"),
	GRAY_STAINED_GLASS(95, 7, "Gray Stained Glass", "((95)|(stained[^0-9A-Z]*glass))[ ]*[:;|/.,\\-_~][ ]*((gr(a|e)y)|7)"),
	LIGHT_GRAY_STAINED_GLASS(95, 8, "Light Gray Stained Glass", "((95)|(stained[^0-9A-Z]*glass))[ ]*[:;|/.,\\-_~][ ]*((light[^0-9A-Z]*gr(a|e)y)|(silver)|8)"),
	CYAN_STAINED_GLASS(95, 9, "Cyan Stained Glass", "((95)|(stained[^0-9A-Z]*glass))[ ]*[:;|/.,\\-_~][ ]*((cyan)|9)"),
	PURPLE_STAINED_GLASS(95, 10, "Purple Stained Glass", "((95)|(stained[^0-9A-Z]*glass))[ ]*[:;|/.,\\-_~][ ]*((purple)|(10))"),
	BLUE_STAINED_GLASS(95, 11, "Blue Stained Glass", "((95)|(stained[^0-9A-Z]*glass))[ ]*[:;|/.,\\-_~][ ]*((blue)|(11))"),
	BROWN_STAINED_GLASS(95, 12, "Brown Stained Glass", "((95)|(stained[^0-9A-Z]*glass))[ ]*[:;|/.,\\-_~][ ]*((brown)|(12))"),
	GREEN_STAINED_GLASS(95, 13, "Green Stained Glass", "((95)|(stained[^0-9A-Z]*glass))[ ]*[:;|/.,\\-_~][ ]*((green)|(13))"),
	RED_STAINED_GLASS(95, 14, "Red Stained Glass", "((95)|(stained[^0-9A-Z]*glass))[ ]*[:;|/.,\\-_~][ ]*((red)|(14))"),
	BLACK_STAINED_GLASS(95, 15, "Black Stained Glass", "((95)|(stained[^0-9A-Z]*glass))[ ]*[:;|/.,\\-_~][ ]*((black)|(15))"),
	WOODEN_TRAPDOOR(96, "Wooden Trapdoor", "wood(en)?[^0-9A-Z]*trapdoor"),
	STONE_MONSTER_EGG(97, "Stone Monster Egg"),
	COBBLESTONE_MONSTER_EGG(97, 1, "Cobblestone Monster Egg", "(cobble[^0-9A-Z]*monster[^0-9A-Z]*egg)|(monster[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*1)"),
	STONE_BRICK_MONSTER_EGG(97, 2, "Stone Brick Monster Egg", "monster[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*2"),
	MOSSY_STONE_BRICK_MONSTER_EGG(97, 3, "Mossy Stone Brick Monster Egg", "(mossy?[^0-9A-Z]*stone[^0-9A-Z]*brick[^0-9A-Z]*monster[^0-9A-Z]*egg)|(monster[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*3)"),
	CRACKED_STONE_BRICK_MONSTER_EGG(97, 4, "Cracked Stone Brick Monster Egg", "monster[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*4"),
	CHISELED_STONE_BRICK_MONSTER_EGG(97, 5, "Chiseled Stone Brick Monster Egg", "monster[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*5"),
	STONE_BRICKS(98, "Stone Bricks", "^stone[^0-9A-Z]*brick"),
	MOSSY_STONE_BRICKS(98, 1, "Mossy Stone Bricks", "(mossy?[^0-9A-Z]*stone[^0-9A-Z]*brick)|(monster[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*1)"),
	CRACKED_STONE_BRICKS(98, 2, "Cracked Stone Bricks", "(cracked[^0-9A-Z]*stone[^0-9A-Z]*brick)|(monster[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*2)"),
	CHISELED_STONE_BRICKS(98, 3, "Chiseled Stone Bricks", "(chiseled[^0-9A-Z]*stone[^0-9A-Z]*brick)|(monster[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*3)"),
	BROWN_MUSHROOM_BLOCK(99, "Brown Mushroom Block"),
	
	
	
	RED_MUSHROOM_BLOCK(100, "Red Mushroom Block"),
	IRON_BARS(101, "Iron Bars", "iron[^0-9A-Z]*bar"),
	GLASS_PANE(102, "Glass Pane"),
	MELON_BLOCK(103, "Melon Block"),
	PUMPKIN_STEM(104, "Pumpkin Stem"),
	MELON_STEM(105, "Melon Stem"),
	VINES(106, "Vines"),
	OAK_FENCE_GATE(107, "Oak Fence Gate", "(oak[^0-9A-Z]*(wood[^0-9A-Z]*)?)?fence[^0-9A-Z]*gate"),
	BRICK_STAIRS(108, "Brick Stairs", "^brick[^0-9A-Z]*stair"),
	STONE_BRICK_STAIRS(109, "Stone Brick Stairs", "stone[^0-9A-Z]*brick[^0-9A-Z]*stair"),
	
	MYCELIUM(110, "Mycelium"),
	LILY_PAD(111, "Lily Pad"),
	NETHER_BRICK(112, "Nether Brick"),
	NETHER_BRICK_FENCE(113, "Nether Brick Fence"),
	NETHER_BRICK_STAIRS(114, "Nether Brick Stairs", "nether[^0-9A-Z]*brick[^0-9A-Z]*stair"),
	NETHER_WART_PLANT(115, "Nether Wart (plant)"),
	ENCHANTMENT_TABLE(116, "Enchantment Table", "enchant(ing)?[^0-9A-Z]*table"),
	BREWING_STAND_BLOCK(117, "Brewing Stand Block"),
	CAULDRON_BLOCK(118, "Cauldron Block"),
	END_PORTAL(119, "End Portal"),
	
	END_PORTAL_FRAME(120, "End Portal Frame"),
	END_STONE(121, "End Stone"),
	DRAGON_EGG(122, "Dragon Egg"),
	REDSTONE_LAMP(123, "Redstone Lamp"),
	POWERED_REDSTONE_LAMP(124, "Powered Redstone Lamp"),
	DOUBLE_OAK_WOOD_SLAB(125, "Double Oak Wood Slab", "double[^0-9A-Z]*(oak[^0-9A-Z]*)?wood[^0-9A-Z]*slab"),
	DOUBLE_BIRCH_WOOD_SLAB(125, 1, "Double Birch Wood Slab", "(double[^0-9A-Z]*birch[^0-9A-Z]*slab)|(double[^0-9A-Z]*wood(en)?[^0-9A-Z]*slab[ ]*[:;|/.,\\-_~][ ]*1)"),
	DOUBLE_SPRUCE_WOOD_SLAB(125, 2, "Double Spruce Wood Slab", "(double[^0-9A-Z]*spruce[^0-9A-Z]*slab)|(double[^0-9A-Z]*wood(en)?[^0-9A-Z]*slab[ ]*[:;|/.,\\-_~][ ]*2)"),
	DOUBLE_JUNGLE_WOOD_SLAB(125, 3, "Double Jungle Wood Slab", "(double[^0-9A-Z]*jungle[^0-9A-Z]*slab)|(double[^0-9A-Z]*wood(en)?[^0-9A-Z]*slab[ ]*[:;|/.,\\-_~][ ]*3)"),
	DOUBLE_ACACIA_WOOD_SLAB(125, 4, "Double Acacia Wood Slab", "(double[^0-9A-Z]*acacia[^0-9A-Z]*slab)|(double[^0-9A-Z]*wood(en)?[^0-9A-Z]*slab[ ]*[:;|/.,\\-_~][ ]*4)"),
	DOUBLE_DARK_OAK_WOOD_SLAB(125, 5, "Double Dark Oak Wood Slab", "(double[^0-9A-Z]*dark[^0-9A-Z]*(oak[^0-9A-Z]*)?(wood[^0-9A-Z]*)?slab)|(double[^0-9A-Z]*wood(en)?[^0-9A-Z]*slab[ ]*[:;|/.,\\-_~][ ]*5)"),
	OAK_WOOD_SLAB(126, "Oak Wood Slab", "^(oak[^0-9A-Z]*)?wood[^0-9A-Z]*slab"),
	BIRCH_WOOD_SLAB(126, 1, "Birch Wood Slab", "^(birch[^0-9A-Z]*slab)|(wood(en)?[^0-9A-Z]*slab[ ]*[:;|/.,\\-_~][ ]*1)"),
	SPRUCE_WOOD_SLAB(126, 2, "Spruce Wood Slab", "^(spruce[^0-9A-Z]*slab)|(wood(en)?[^0-9A-Z]*slab[ ]*[:;|/.,\\-_~][ ]*2)"),
	JUNGLE_WOOD_SLAB(126, 3, "Jungle Wood Slab", "^(jungle[^0-9A-Z]*slab)|(wood(en)?[^0-9A-Z]*slab[ ]*[:;|/.,\\-_~][ ]*3)"),
	ACACIA_WOOD_SLAB(126, 4, "Acacia Wood Slab", "^(acacia[^0-9A-Z]*slab)|(wood(en)?[^0-9A-Z]*slab[ ]*[:;|/.,\\-_~][ ]*4)"),
	DARK_OAK_WOOD_SLAB(126, 5, "Dark Oak Wood Slab", "^(dark[^0-9A-Z]*(oak[^0-9A-Z]*)?(wood[^0-9A-Z]*)?slab)|(wood(en)?[^0-9A-Z]*slab[ ]*[:;|/.,\\-_~][ ]*5)"),
	COCOA(127, "Cocoa"),
	SANDSTONE_STAIRS(128, "Sandstone Stairs", "sandstone[^0-9A-Z]*stair"),
	EMERALD_ORE(129, "Emerald Ore"),
	
	ENDER_CHEST(130, "Ender Chest"),
	TRIPWIRE_HOOK(131, "Tripwire Hook"),
	TRIPWIRE(132, "Tripwire"),
	EMERALD_BLOCK(133, "Emerald Block"),
	SPRUCE_STAIRS(134, "Spruce Wood Stairs", "^spruce[^0-9A-Z]*(wood[^0-9A-Z]*)?stair"),
	BIRCH_STAIRS(135, "Birch Wood Stairs", "^birch[^0-9A-Z]*(wood[^0-9A-Z]*)?stair"),
	JUNGLE_STAIRS(136, "Jungle Wood Stairs", "^jungle[^0-9A-Z]*(wood[^0-9A-Z]*)?stair"),
	COMMAND_BLOCK(137, "Command Block"), //Overpowered
	BEACON(138, "Beacon"),
	COBBLESTONE_WALL(139, "Cobblestone Wall", "^(cobble(stone)?[^0-9A-Z]*)?wall"), //We need to build a wall
	MOSSY_COBBLESTONE_WALL(139, 1, "Mossy Cobblestone Wall", "(^mossy?[^0-9A-Z]*(cobble(stone)?[^0-9A-Z]*)?wall)|((cobble(stone)?[^0-9A-Z]*)?wall[ ]*[:;|/.,\\-_~][ ]*1)"),
	
	FLOWER_POT_BLOCK(140, "Flower Pot Block"),
	CARROTS(141, "Carrots", "carrot[^0-9A-Z]*block"),
	POTATOES(142, "Potatoes", "potato[^0-9A-Z]*block"),
	WOODEN_BUTTON(143, "Wooden Button", "wood[^0-9A-Z]*button"),
	SKULL_BLOCK(144, "Mob Head"),
	ANVIL(145, "Anvil"),
	TRAPPED_CHEST(146, "Trapped Chest"),
	LIGHT_WEIGHTED_PRESSURE_PLATE(147, "Weighted Pressure Plate (light)", "light[^0-9A-Z]*pressure[^0-9A-Z]*plate"),
	HEAVY_WEIGHTED_PRESSURE_PLATE(148, "Weighted Pressure Plate (heavy)", "heavy[^0-9A-Z]*pressure[^0-9A-Z]*plate"),
	UNPOWERED_COMPARATOR(149, "Redstone Comparator (inactive)"),
	
	POWERED_COMPARATOR(150, "Redstone Comparator (active)"),
	DAYLIGHT_DETECTOR(151, "Daylight Sensor"),
	REDSTONE_BLOCK(152, "Redstone Block"),
	QUARTZ_ORE(153, "Nether Quartz Ore", "quartz[^0-9A-Z]*ore"),
	HOPPER(154, "Hopper"),
	QUARTZ_BLOCK(155, "Quartz Block", "^quartz$"),
	CHISELED_QUARTZ_BLOCK(155, 1, "Chiseled Quartz Block", "quartz[^0-9A-Z]*(block[^0-9A-Z]*)?[ ]*[:;|/.,\\-_~][ ]*1"),
	PILLAR_QUARTZ_BLOCK(155, 2, "Pillar Quartz Block", "quartz[^0-9A-Z]*(block[^0-9A-Z]*)?[ ]*[:;|/.,\\-_~][ ]*2"),
	QUARTZ_STAIRS(156, "Quartz Stairs", "quartz[^0-9A-Z]*stair"),
	ACTIVATOR_RAIL(157, "Activator Rail"),
	DROPPER(158, "Dropper"),
	STAINED_HARDENED_CLAY(159, "White Hardened Clay", "^(white[^0-9A-Z]*)?stained[^0-9A-Z]*clay$"), //Regex why
	ORANGE_STAINED_HARDENED_CLAY(159, 1, "Orange Hardened Clay", "(orange[^0-9A-Z]*(((hardened)|(stained([^0-9A-Z]*hardened)?))[^0-9A-Z]*clay|(terr?acott?a)|(159))|(((hardened)|(stained([^0-9A-Z]*hardened)?))[^0-9A-Z]*clay|(terr?acott?a)|(159))[ ]*[:;|/.,\\-_~][ ]*((orange)|(1$)))"),
	MAGENTA_STAINED_HARDENED_CLAY(159, 2, "Magenta Hardened Clay", "(magenta[^0-9A-Z]*(((hardened)|(stained([^0-9A-Z]*hardened)?))[^0-9A-Z]*clay|(terr?acott?a)|(159))|(((hardened)|(stained([^0-9A-Z]*hardened)?))[^0-9A-Z]*clay|(terr?acott?a)|(159))[ ]*[:;|/.,\\-_~][ ]*((magenta)|(2$)))"),
	LIGHT_BLUE_STAINED_HARDENED_CLAY(159, 3, "Light Blue Hardened Clay", "(((aqua)|(light[^0-9A-Z]*blue))[^0-9A-Z]*(((hardened)|(stained([^0-9A-Z]*hardened)?))[^0-9A-Z]*clay|(terr?acott?a)|(159))|((((hardened)|(stained([^0-9A-Z]*hardened)?))[^0-9A-Z]*clay|(terr?acott?a)|(159))[ ]*[:;|/.,\\-_~][ ]*((aqua)|(light[^0-9A-Z]*blue)|(3$))))"),
	YELLOW_STAINED_HARDENED_CLAY(159, 4, "Yellow Hardened Clay", "(yellow[^0-9A-Z]*(((hardened)|(stained([^0-9A-Z]*hardened)?))[^0-9A-Z]*clay|(terr?acott?a)|(159))|(((hardened)|(stained([^0-9A-Z]*hardened)?))[^0-9A-Z]*clay|(terr?acott?a)|(159))[ ]*[:;|/.,\\-_~][ ]*((yellow)|(4$)))"),
	LIME_STAINED_HARDENED_CLAY(159, 5, "Lime Hardened Clay", "(((lime)|(light[^0-9A-Z]*green))[^0-9A-Z]*(((hardened)|(stained([^0-9A-Z]*hardened)?))[^0-9A-Z]*clay|(terr?acott?a)|(159))|((((hardened)|(stained([^0-9A-Z]*hardened)?))[^0-9A-Z]*clay|(terr?acott?a)|(159))[ ]*[:;|/.,\\-_~][ ]*((lime)|(light[^0-9A-Z]*green)|(3$))))"),
	PINK_STAINED_HARDENED_CLAY(159, 6, "Pink Hardened Clay", "(pink[^0-9A-Z]*(((hardened)|(stained([^0-9A-Z]*hardened)?))[^0-9A-Z]*clay|(terr?acott?a)|(159))|(((hardened)|(stained([^0-9A-Z]*hardened)?))[^0-9A-Z]*clay|(terr?acott?a)|(159))[ ]*[:;|/.,\\-_~][ ]*((pink)|6))"),
	GRAY_STAINED_HARDENED_CLAY(159, 7, "Gray Hardened Clay", "(gr(a|e)y[^0-9A-Z]*(((hardened)|(stained([^0-9A-Z]*hardened)?))[^0-9A-Z]*clay|(terr?acott?a)|(159))|(((hardened)|(stained([^0-9A-Z]*hardened)?))[^0-9A-Z]*clay|(terr?acott?a)|(159))[ ]*[:;|/.,\\-_~][ ]*((gr(a|e)y)|7))"),
	LIGHT_GRAY_STAINED_HARDENED_CLAY(159, 8, "Light Gray Hardened Clay", "((light[^0-9A-Z]*gr(a|e)y)|(silver)[^0-9A-Z]*(((hardened)|(stained([^0-9A-Z]*hardened)?))[^0-9A-Z]*clay|(terr?acott?a)|(159))|(((hardened)|(stained([^0-9A-Z]*hardened)?))[^0-9A-Z]*clay|(terr?acott?a)|(159))[ ]*[:;|/.,\\-_~][ ]*((light[^0-9A-Z]*gr(a|e)y)|(silver)|8))"),
	CYAN_STAINED_HARDENED_CLAY(159, 9, "Cyan Hardened Clay", "(cyan[^0-9A-Z]*(((hardened)|(stained([^0-9A-Z]*hardened)?))[^0-9A-Z]*clay|(terr?acott?a)|(159))|(((hardened)|(stained([^0-9A-Z]*hardened)?))[^0-9A-Z]*clay|(terr?acott?a)|(159))[ ]*[:;|/.,\\-_~][ ]*((cyan)|9))"),
	PURPLE_STAINED_HARDENED_CLAY(159, 10, "Purple Hardened Clay", "(purple[^0-9A-Z]*(((hardened)|(stained([^0-9A-Z]*hardened)?))[^0-9A-Z]*clay|(terr?acott?a)|(159))|(((hardened)|(stained([^0-9A-Z]*hardened)?))[^0-9A-Z]*clay|(terr?acott?a)|(159))[ ]*[:;|/.,\\-_~][ ]*((purple)|(10)))"),
	BLUE_STAINED_HARDENED_CLAY(159, 11, "Blue Hardened Clay", "(blue[^0-9A-Z]*(((hardened)|(stained([^0-9A-Z]*hardened)?))[^0-9A-Z]*clay|(terr?acott?a)|(159))|(((hardened)|(stained([^0-9A-Z]*hardened)?))[^0-9A-Z]*clay|(terr?acott?a)|(159))[ ]*[:;|/.,\\-_~][ ]*((blue)|(11)))"),
	BROWN_STAINED_HARDENED_CLAY(159, 12, "Brown Hardened Clay", "(brown[^0-9A-Z]*(((hardened)|(stained([^0-9A-Z]*hardened)?))[^0-9A-Z]*clay|(terr?acott?a)|(159))|(((hardened)|(stained([^0-9A-Z]*hardened)?))[^0-9A-Z]*clay|(terr?acott?a)|(159))[ ]*[:;|/.,\\-_~][ ]*((brown)|(12)))"),
	GREEN_STAINED_HARDENED_CLAY(159, 13, "Green Hardened Clay", "(green[^0-9A-Z]*(((hardened)|(stained([^0-9A-Z]*hardened)?))[^0-9A-Z]*clay|(terr?acott?a)|(159))|(((hardened)|(stained([^0-9A-Z]*hardened)?))[^0-9A-Z]*clay|(terr?acott?a)|(159))[ ]*[:;|/.,\\-_~][ ]*((green)|(13)))"),
	RED_STAINED_HARDENED_CLAY(159, 14, "Red Hardened Clay", "(red[^0-9A-Z]*(((hardened)|(stained([^0-9A-Z]*hardened)?))[^0-9A-Z]*clay|(terr?acott?a)|(159))|(((hardened)|(stained([^0-9A-Z]*hardened)?))[^0-9A-Z]*clay|(terr?acott?a)|(159))[ ]*[:;|/.,\\-_~][ ]*((red)|(14)))"),
	BLACK_STAINED_HARDENED_CLAY(159, 15, "Black Hardened Clay", "(black[^0-9A-Z]*(((hardened)|(stained([^0-9A-Z]*hardened)?))[^0-9A-Z]*clay|(terr?acott?a)|(159))|(((hardened)|(stained([^0-9A-Z]*hardened)?))[^0-9A-Z]*clay|(terr?acott?a)|(159))[ ]*[:;|/.,\\-_~][ ]*((black)|(15)))"),
	
	STAINED_GLASS_PANE(160, "White Stained Glass Pane", "^stained[^0-9A-Z]*glass[^0-9A-Z]*pane"),
	ORANGE_STAINED_GLASS_PANE(160, 1, "Orange Stained Glass Pane", "((160)|(stained[^0-9A-Z]*glass[^0-9A-Z]*pane))[ ]*[:;|/.,\\-_~][ ]*((orange)|(1$))"),
	MAGENTA_STAINED_GLASS_PANE(160, 2, "Magenta Stained Glass Pane", "((160)|(stained[^0-9A-Z]*glass[^0-9A-Z]*pane))[ ]*[:;|/.,\\-_~][ ]*((magenta)|(2$))"),
	LIGHT_BLUE_STAINED_GLASS_PANE(160, 3, "Light Blue Stained Glass Pane", "((160)|(stained[^0-9A-Z]*glass[^0-9A-Z]*pane))[ ]*[:;|/.,\\-_~][ ]*((aqua)|(light[^0-9A-Z]*blue)|(3$))"),
	YELLOW_STAINED_GLASS_PANE(160, 4, "Yellow Stained Glass Pane", "((160)|(stained[^0-9A-Z]*glass[^0-9A-Z]*pane))[ ]*[:;|/.,\\-_~][ ]*((yellow)|(4$))"),
	LIME_STAINED_GLASS_PANE(160, 5, "Lime Stained Glass Pane", "((160)|(stained[^0-9A-Z]*glass[^0-9A-Z]*pane))[ ]*[:;|/.,\\-_~][ ]*((lime)|(light[^0-9A-Z]*green)|(5$))"),
	PINK_STAINED_GLASS_PANE(160, 6, "Pink Stained Glass Pane", "((160)|(stained[^0-9A-Z]*glass[^0-9A-Z]*pane))[ ]*[:;|/.,\\-_~][ ]*((pink)|6)"),
	GRAY_STAINED_GLASS_PANE(160, 7, "Gray Stained Glass Pane", "((160)|(stained[^0-9A-Z]*glass[^0-9A-Z]*pane))[ ]*[:;|/.,\\-_~][ ]*((gr(a|e)y)|7)"),
	LIGHT_GRAY_STAINED_GLASS_PANE(160, 8, "Light Gray Stained Glass Pane", "((160)|(stained[^0-9A-Z]*glass[^0-9A-Z]*pane))[ ]*[:;|/.,\\-_~][ ]*((light[^0-9A-Z]*gr(a|e)y)|(silver)|8)"),
	CYAN_STAINED_GLASS_PANE(160, 9, "Cyan Stained Glass Pane", "((160)|(stained[^0-9A-Z]*glass[^0-9A-Z]*pane))[ ]*[:;|/.,\\-_~][ ]*((cyan)|9)"),
	PURPLE_STAINED_GLASS_PANE(160, 10, "Purple Stained Glass Pane", "((160)|(stained[^0-9A-Z]*glass[^0-9A-Z]*pane))[ ]*[:;|/.,\\-_~][ ]*((purple)|(10))"),
	BLUE_STAINED_GLASS_PANE(160, 11, "Blue Stained Glass Pane", "((160)|(stained[^0-9A-Z]*glass[^0-9A-Z]*pane))[ ]*[:;|/.,\\-_~][ ]*((blue)|(11))"),
	BROWN_STAINED_GLASS_PANE(160, 12, "Brown Stained Glass Pane", "((160)|(stained[^0-9A-Z]*glass[^0-9A-Z]*pane))[ ]*[:;|/.,\\-_~][ ]*((brown)|(12))"),
	GREEN_STAINED_GLASS_PANE(160, 13, "Green Stained Glass Pane", "((160)|(stained[^0-9A-Z]*glass[^0-9A-Z]*pane))[ ]*[:;|/.,\\-_~][ ]*((green)|(13))"),
	RED_STAINED_GLASS_PANE(160, 14, "Red Stained Glass Pane", "((160)|(stained[^0-9A-Z]*glass[^0-9A-Z]*pane))[ ]*[:;|/.,\\-_~][ ]*((red)|(14))"),
	BLACK_STAINED_GLASS_PANE(160, 15, "Black Stained Glass Pane", "((160)|(stained[^0-9A-Z]*glass[^0-9A-Z]*pane))[ ]*[:;|/.,\\-_~][ ]*((black)|(15))"),
	ACACIA_LEAVES(161, "Acacia Leaves", "leaves2$"),
	DARK_OAK_LEAVES(161, 1, "Dark Oak Leaves", "(dark[^0-9A-Z]*leaves)|(leaves2[ ]*[:;|/.,\\-_~][ ]*1)"),
	ACACIA_WOOD(162, "Acacia Wood", "log2$"),
	DARK_OAK_WOOD(162, 1, "Dark Oak Wood", "(^dark[^0-9A-Z]*wood)|(log2[ ]*[:;|/.,\\-_~][ ]*1)"),
	ACACIA_STAIRS(163, "Acacia Wood Stairs", "acacia[^0-9A-Z]*(wood[^0-9A-Z]*)?stair"),
	DARK_OAK_STAIRS(164, "Dark Oak Wood Stairs", "dark[^0-9A-Z]*(oak[^0-9A-Z]*)?(wood[^0-9A-Z]*)?stair"),
	SLIME_BLOCK(165, "Slime Block", Version.V1_8),
	BARRIER(166, "Barrier", Version.V1_8),
	IRON_TRAPDOOR(167, "Iron Trapdoor", Version.V1_8),
	PRISMARINE(168, "Prismarine", Version.V1_8),
	PRISMARINE_BRICKS(168, 1, "Prismarine Bricks", "prismarine[ ]*[:;|/.,\\-_~][ ]*1", Version.V1_8),
	DARK_PRISMARINE(168, 2, "Dark Prismarine", "prismarine[ ]*[:;|/.,\\-_~][ ]*2", Version.V1_8),
	SEA_LANTERN(169, "Sea Lantern", Version.V1_8),
	
	HAY_BLOCK(170, "Hay Bale", "hay"),
	CARPET(171, "White Carpet"),
	ORANGE_CARPET(171, 1, "Orange Carpet", "((171)|(carpet))[ ]*[:;|/.,\\-_~][ ]*((orange)|(1$))"),
	MAGENTA_CARPET(171, 2, "Magenta Carpet", "((171)|(carpet))[ ]*[:;|/.,\\-_~][ ]*((magenta)|(2$))"),
	LIGHT_BLUE_CARPET(171, 3, "Light Blue Carpet", "((aqua)|light[^0-9A-Z]*blue[^0-9A-Z]*carpet)|(((171)|(carpet))[ ]*[:;|/.,\\-_~][ ]*((aqua)|(light[^0-9A-Z]*blue)|(3$)))"),
	YELLOW_CARPET(171, 4, "Yellow Carpet", "((171)|(carpet))[ ]*[:;|/.,\\-_~][ ]*((yellow)|(4$))"),
	LIME_CARPET(171, 5, "Lime Carpet", "((lime)|light[^0-9A-Z]*green[^0-9A-Z]*carpet)|(((171)|(carpet))[ ]*[:;|/.,\\-_~][ ]*((lime)|(light[^0-9A-Z]*green)|(5$)))"),
	PINK_CARPET(171, 6, "Pink Carpet", "((171)|(carpet))[ ]*[:;|/.,\\-_~][ ]*((pink)|6)"),
	GRAY_CARPET(171, 7, "Gray Carpet", "((171)|(carpet))[ ]*[:;|/.,\\-_~][ ]*((gray)|7)"),
	LIGHT_GRAY_CARPET(171, 8, "Light Gray Carpet", "((171)|(carpet))[ ]*[:;|/.,\\-_~][ ]*((light[^0-9A-Z]*gr(a|e)y)|(silver)|8)"),
	CYAN_CARPET(171, 9, "Cyan Carpet", "((171)|(carpet))[ ]*[:;|/.,\\-_~][ ]*((cyan)|9)"),
	PURPLE_CARPET(171, 10, "Purple Carpet", "((171)|(carpet))[ ]*[:;|/.,\\-_~][ ]*((purple)|(10))"),
	BLUE_CARPET(171, 11, "Blue Carpet", "((171)|(carpet))[ ]*[:;|/.,\\-_~][ ]*((blue)|(11))"),
	BROWN_CARPET(171, 12, "Brown Carpet", "((171)|(carpet))[ ]*[:;|/.,\\-_~][ ]*((brown)|(12))"),
	GREEN_CARPET(171, 13, "Green Carpet", "((171)|(carpet))[ ]*[:;|/.,\\-_~][ ]*((green)|(13))"),
	RED_CARPET(171, 14, "Red Carpet", "((171)|(carpet))[ ]*[:;|/.,\\-_~][ ]*((red)|(14))"),
	BLACK_CARPET(171, 15, "Black Carpet", "((171)|(carpet))[ ]*[:;|/.,\\-_~][ ]*((black)|(15))"),
	HARDENED_CLAY(172, "Hardened Clay"),
	COAL_BLOCK(173, "Block of Coal"),
	PACKED_ICE(174, "Packed Ice"),
	SUNFLOWER(175, "Sunflower", "(sun[^0-9A-Z]*flower)|(double[^0-9A-Z]*plant([ ]*[:;|/.,\\-_~][ ]*0)?$)"),
	LILAC(175, 1, "Lilac", "double[^0-9A-Z]*plant[ ]*[:;|/.,\\-_~][ ]*1"),
	DOUBLE_TALL_GRASS(175, 2, "Double Tallgrass", "double[^0-9A-Z]*plant[ ]*[:;|/.,\\-_~][ ]*2"),
	LARGE_FERN(175, 3, "Large Fern", "double[^0-9A-Z]*plant[ ]*[:;|/.,\\-_~][ ]*3"),
	ROSE_BUSH(175, 4, "Rose Bush", "double[^0-9A-Z]*plant[ ]*[:;|/.,\\-_~][ ]*4"),
	PEONY(175, 5, "Peony", "double[^0-9A-Z]*plant[ ]*[:;|/.,\\-_~][ ]*5"),
	STANDING_BANNER(176, "Free-standing Banner", Version.V1_8),
	WALL_BANNER(177, "Wall-mounted Banner", Version.V1_8),
	DAYLIGHT_DETECTOR_INVERTED(178, "Inverted Daylight Sensor"),
	RED_SANDSTONE(179, "Red Sandstone", Version.V1_8),
	CHISELED_RED_SANDSTONE(179, 1, "Chiseled Red Sandstone", "red[^0-9A-Z]*sandstone[ ]*[:;|/.,\\-_~][ ]*1", Version.V1_8),
	SMOOTH_RED_SANDSTONE(179, 2, "Smooth Red Sandstone", "red[^0-9A-Z]*sandstone[ ]*[:;|/.,\\-_~][ ]*2", Version.V1_8),
	
	RED_SANDSTONE_STAIRS(180, "Red Sandstone Stairs", "red[^0-9A-Z]*sandstone[^0-9A-Z]*stair", Version.V1_8),
	DOUBLE_STONE_SLAB2(181, "Double Red Sandstone Slab", "double[^0-9A-Z]*stone[^0-9A-Z]*slab2", Version.V1_8),
	STONE_SLAB2(182, "Red Sandstone Slab", "stone[^0-9A-Z]*slab2", Version.V1_8),
	SPRUCE_FENCE_GATE(183, "Spruce Fence Gate", "spruce[^0-9A-Z]*(wood[^0-9A-Z]*)?fence[^0-9A-Z]*gate", Version.V1_8),
	BIRCH_FENCE_GATE(184, "Birch Fence Gate", "birch[^0-9A-Z]*(wood[^0-9A-Z]*)?fence[^0-9A-Z]*gate", Version.V1_8),
	JUNGLE_FENCE_GATE(185, "Jungle Fence Gate", "jungle[^0-9A-Z]*(wood[^0-9A-Z]*)?fence[^0-9A-Z]*gate", Version.V1_8),
	DARK_OAK_FENCE_GATE(186, "Dark Oak Fence Gate", "dark[^0-9A-Z]*(oak[^0-9A-Z]*)?(wood[^0-9A-Z]*)?fence[^0-9A-Z]*gate", Version.V1_8),
	ACACIA_FENCE_GATE(187, "Acacia Fence Gate", "acacia[^0-9A-Z]*(wood[^0-9A-Z]*)?fence[^0-9A-Z]*gate", Version.V1_8),
	SPRUCE_FENCE(188, "Spruce Fence", "spruce[^0-9A-Z]*(wood[^0-9A-Z]*)?fence", Version.V1_8),
	BIRCH_FENCE(189, "Birch Fence", "birch[^0-9A-Z]*(wood[^0-9A-Z]*)?fence", Version.V1_8),
	
	JUNGLE_FENCE(190, "Jungle Fence", "jungle[^0-9A-Z]*wood[^0-9A-Z]*fence", Version.V1_8),
	DARK_OAK_FENCE(191, "Dark Oak Fence", "dark[^0-9A-Z]*(oak[^0-9A-Z]*)?(wood[^0-9A-Z]*)?fence", Version.V1_8),
	ACACIA_FENCE(192, "Acacia Fence", "acacia[^0-9A-Z]*wood[^0-9A-Z]*fence", Version.V1_8),
	SPRUCE_DOOR_BLOCK(193, "Spruce Door Block", "spruce[^0-9A-Z]*wood[^0-9A-Z]*door[^0-9A-Z]*block", Version.V1_8),
	BIRCH_DOOR_BLOCK(194, "Birch Door Block", "birch[^0-9A-Z]*wood[^0-9A-Z]*door[^0-9A-Z]*block", Version.V1_8),
	JUNGLE_DOOR_BLOCK(195, "Jungle Door Block", "jungle[^0-9A-Z]*wood[^0-9A-Z]*door[^0-9A-Z]*block", Version.V1_8),
	ACACIA_DOOR_BLOCK(196, "Acacia Door Block", "acacia[^0-9A-Z]*wood[^0-9A-Z]*door[^0-9A-Z]*block", Version.V1_8),
	DARK_OAK_DOOR_BLOCK(197, "Dark Oak Door Block", "dark[^0-9A-Z]*(oak[^0-9A-Z]*)?(wood[^0-9A-Z]*)?door[^0-9A-Z]*block", Version.V1_8),
	END_ROD(198, "End Rod", Version.V1_9),
	CHORUS_PLANT(199, "Chorus Plant", Version.V1_9),
	
	
	
	CHORUS_FLOWER(200, "Chorus Flower", Version.V1_9),
	PURPUR_BLOCK(201, "Purpur Block", "purpur$", Version.V1_9),
	PURPUR_PILLAR(202, "Purpur Pillar", Version.V1_9),
	PURPUR_STAIRS(203, "Purpur Stairs", Version.V1_9),
	PURPUR_DOUBLE_SLAB(204, "Purpur Double Slab", Version.V1_9),
	PURPUR_SLAB(205, "Purpur Slab", Version.V1_9),
	END_BRICKS(206, "End Stone Bricks", "end[^0-9A-Z]*(stone[^0-9A-Z]*)?brick", Version.V1_9),
	BEETROOTS(207, "Beetroot Block", Version.V1_9),
	GRASS_PATH(208, "Grass Path", "path", Version.V1_9),
	END_GATEWAY(209, "End Gateway", "gateway", Version.V1_9),
	
	REPEATING_COMMAND_BLOCK(210, "Repeating Command Block", Version.V1_9),
	CHAIN_COMMAND_BLOCK(211, "Chain Command Block", Version.V1_9),
	FROSTED_ICE(212, "Frosted Ice", Version.V1_9),
	MAGMA(213, "Magma Block", Version.V1_10),
	NETHER_WART_BLOCK(214, "Nether Wart Block", Version.V1_10),
	RED_NETHER_BRICK(215, "Red Nether Brick", "red[^0-9A-Z]*nether[^0-9A-Z]*bricks", Version.V1_10),
	BONE_BLOCK(216, "Bone Block", Version.V1_10),
	STRUCTURE_VOID(217, "Structure Void", Version.V1_10),
	OBSERVER(218, "Observer", Version.V1_11),
	WHITE_SHULKER_BOX(219, "White Shulker Box", Version.V1_11),
	
	ORANGE_SHULKER_BOX(220, "Orange Shulker Box", Version.V1_11),
	MAGENTA_SHULKER_BOX(221, "Magenta Shulker Box", Version.V1_11),
	LIGHT_BLUE_SHULKER_BOX(222, "Light Blue Shulker Box", "aqua[^0-9A-Z]*shulker[^0-9A-Z]*box", Version.V1_11),
	YELLOW_SHULKER_BOX(223, "Yellow Shulker Box", Version.V1_11),
	LIME_SHULKER_BOX(224, "Lime Shulker Box", "light[^0-9A-Z]*green[^0-9A-Z]*shulker[^0-9A-Z]*box", Version.V1_11),
	PINK_SHULKER_BOX(225, "Pink Shulker Box", Version.V1_11),
	GRAY_SHULKER_BOX(226, "Gray Shulker Box", "grey[^0-9A-Z]*shulker[^0-9A-Z]*box", Version.V1_11),
	SILVER_SHULKER_BOX(227, "Light Gray Shulker Box", "light[^0-9A-Z]*grey[^0-9A-Z]*shulker[^0-9A-Z]*box", Version.V1_11),
	CYAN_SHULKER_BOX(228, "Cyan Shulker Box", Version.V1_11),
	PURPLE_SHULKER_BOX(229, "Purple Shulker Box", "^shulker[^0-9A-Z]*box", Version.V1_11),
	
	BLUE_SHULKER_BOX(230, "Blue Shulker Box", Version.V1_11),
	BROWN_SHULKER_BOX(231, "Brown Shulker Box", Version.V1_11),
	GREEN_SHULKER_BOX(232, "Green Shulker Box", Version.V1_11),
	RED_SHULKER_BOX(233, "Red Shulker Box", Version.V1_11),
	BLACK_SHULKER_BOX(234, "Black Shulker Box", Version.V1_11),
	WHITE_GLAZED_TERRACOTTA(235, "White Glazed Terracotta", "^(glazed[^0-9A-Z]*)?terr?acott?a", Version.V1_12),
	ORANGE_GLAZED_TERRACOTTA(236, "Orange Glazed Terracotta", "orange[^0-9A-Z]*terr?acott?a", Version.V1_12),
	MAGENTA_GLAZED_TERRACOTTA(237, "Magenta Glazed Terracotta", "magenta[^0-9A-Z]*terr?acott?a", Version.V1_12),
	LIGHT_BLUE_GLAZED_TERRACOTTA(238, "Light Blue Glazed Terracotta", "((aqua)|(light[^0-9A-Z]*blue))[^0-9A-Z](glazed[^0-9A-Z]*)?terr?acott?a", Version.V1_12),
	YELLOW_GLAZED_TERRACOTTA(239, "Yellow Glazed Terracotta", "yellow[^0-9A-Z]*terr?acott?a", Version.V1_12),
	
	LIME_GLAZED_TERRACOTTA(240, "Lime Glazed Terracotta", "((lime)|(light[^0-9A-Z]*green))[^0-9A-Z](glazed[^0-9A-Z]*)?terr?acott?a", Version.V1_12),
	PINK_GLAZED_TERRACOTTA(241, "Pink Glazed Terracotta", "pink[^0-9A-Z]*terr?acott?a", Version.V1_12),
	GRAY_GLAZED_TERRACOTTA(242, "Gray Glazed Terracotta", "gr(a|e)y[^0-9A-Z]*terr?acott?a", Version.V1_12),
	LIGHT_GRAY_GLAZED_TERRACOTTA(243, "Light Gray Glazed Terracotta", "light[^0-9A-Z]*gr(a|e)y[^0-9A-Z]*terr?acott?a", Version.V1_12),
	CYAN_GLAZED_TERRACOTTA(244, "Cyan Glazed Terracotta", "cyan[^0-9A-Z]*terr?acott?a", Version.V1_12),
	PURPLE_GLAZED_TERRACOTTA(245, "Purple Glazed Terracotta", "purple[^0-9A-Z]*terr?acott?a", Version.V1_12),
	BLUE_GLAZED_TERRACOTTA(246, "Blue Glazed Terracotta", "blue[^0-9A-Z]*terr?acott?a", Version.V1_12),
	BROWN_GLAZED_TERRACOTTA(247, "Brown Glazed Terracotta", "brown[^0-9A-Z]*terr?acott?a", Version.V1_12),
	GREEN_GLAZED_TERRACOTTA(248, "Green Glazed Terracotta", "green[^0-9A-Z]*terr?acott?a", Version.V1_12),
	RED_GLAZED_TERRACOTTA(249, "Red Glazed Terracotta", "red[^0-9A-Z]*terr?acott?a", Version.V1_12),
	
	BLACK_GLAZED_TERRACOTTA(250, "Black Glazed Terracotta", "black[^0-9A-Z]*terr?acott?a", Version.V1_12),
	CONCRETE(251, "White Concrete", "^concrete$", Version.V1_12),
	ORANGE_CONCRETE(251, 1, "Orange Concrete", "((251)|(concrete))[ ]*[:;|/.,\\-_~][ ]*((orange)|(1$))", Version.V1_12),
	MAGENTA_CONCRETE(251, 2, "Magenta Concrete", "((251)|(concrete))[ ]*[:;|/.,\\-_~][ ]*((magenta)|(2$))", Version.V1_12),
	LIGHT_BLUE_CONCRETE(251, 3, "Light Blue Concrete", "(aqua[^0-9A-Z]*concrete)|(((251)|(concrete))[ ]*[:;|/.,\\-_~][ ]*((aqua)|(light[^0-9A-Z]*blue)|(3$)))", Version.V1_12),
	YELLOW_CONCRETE(251, 4, "Yellow Concrete", "((251)|(concrete))[ ]*[:;|/.,\\-_~][ ]*((yellow)|(4$))", Version.V1_12),
	LIME_CONCRETE(251, 5, "Lime Concrete", "(light[^0-9A-Z]*green[^0-9A-Z]*concrete)|(((251)|(concrete))[ ]*[:;|/.,\\-_~][ ]*((lime)|(light[^0-9A-Z]*green)|(5$)))", Version.V1_12),
	PINK_CONCRETE(251, 6, "Pink Concrete", "((251)|(concrete))[ ]*[:;|/.,\\-_~][ ]*((pink)|6)", Version.V1_12),
	GRAY_CONCRETE(251, 7, "Gray Concrete", "((251)|(concrete))[ ]*[:;|/.,\\-_~][ ]*((gr(a|e)y)|7)", Version.V1_12),
	LIGHT_GRAY_CONCRETE(251, 8, "Light Gray Concrete", "((251)|(concrete))[ ]*[:;|/.,\\-_~][ ]*((light[^0-9A-Z]*gr(a|e)y)|(silver)|8)", Version.V1_12),
	CYAN_CONCRETE(251, 9, "Cyan Concrete", "((251)|(concrete))[ ]*[:;|/.,\\-_~][ ]*((cyan)|9)", Version.V1_12),
	PURPLE_CONCRETE(251, 10, "Purple Concrete", "((251)|(concrete))[ ]*[:;|/.,\\-_~][ ]*((purple)|(10))", Version.V1_12),
	BLUE_CONCRETE(251, 11, "Blue Concrete", "((251)|(concrete))[ ]*[:;|/.,\\-_~][ ]*((blue)|(11))", Version.V1_12),
	BROWN_CONCRETE(251, 12, "Brown Concrete", "((251)|(concrete))[ ]*[:;|/.,\\-_~][ ]*((brown)|(12))", Version.V1_12),
	GREEN_CONCRETE(251, 13, "Green Concrete", "((251)|(concrete))[ ]*[:;|/.,\\-_~][ ]*((green)|(13))", Version.V1_12),
	RED_CONCRETE(251, 14, "Red Concrete", "((251)|(concrete))[ ]*[:;|/.,\\-_~][ ]*((red)|(14))", Version.V1_12),
	BLACK_CONCRETE(251, 15, "Black Concrete", "((251)|(concrete))[ ]*[:;|/.,\\-_~][ ]*((black)|(15))", Version.V1_12),
	CONCRETE_POWDER(252, "White Concrete Powder", "^concrete[^0-9A-Z]*powder", Version.V1_12),
	ORANGE_CONCRETE_POWDER(252, 1, "Orange Concrete Powder", "((252)|(concrete[^0-9A-Z]*powder))[ ]*[:;|/.,\\-_~][ ]*((orange)|(1$))", Version.V1_12),
	MAGENTA_CONCRETE_POWDER(252, 2, "Magenta Concrete Powder", "((252)|(concrete[^0-9A-Z]*powder))[ ]*[:;|/.,\\-_~][ ]*((magenta)|(2$))", Version.V1_12),
	LIGHT_BLUE_CONCRETE_POWDER(252, 3, "Light Blue Concrete Powder", "(aqua[^0-9A-Z]*concrete[^0-9A-Z]*powder)|(((252)|(concrete[^0-9A-Z]*powder))[ ]*[:;|/.,\\-_~][ ]*((aqua)|(light[^0-9A-Z]*blue)|(3$)))", Version.V1_12),
	YELLOW_CONCRETE_POWDER(252, 4, "Yellow Concrete Powder", "((252)|(concrete[^0-9A-Z]*powder))[ ]*[:;|/.,\\-_~][ ]*((yellow)|(4$))", Version.V1_12),
	LIME_CONCRETE_POWDER(252, 5, "Lime Concrete Powder", "(light[^0-9A-Z]*green[^0-9A-Z]*concrete[^0-9A-Z]*powder)|(((252)|(concrete[^0-9A-Z]*powder))[ ]*[:;|/.,\\-_~][ ]*((lime)|(light[^0-9A-Z]*green)|(5$)))", Version.V1_12),
	PINK_CONCRETE_POWDER(252, 6, "Pink Concrete Powder", "((252)|(concrete[^0-9A-Z]*powder))[ ]*[:;|/.,\\-_~][ ]*((pink)|6)", Version.V1_12),
	GRAY_CONCRETE_POWDER(252, 7, "Gray Concrete Powder", "((252)|(concrete[^0-9A-Z]*powder))[ ]*[:;|/.,\\-_~][ ]*((gr(a|e)y)|7)", Version.V1_12),
	LIGHT_GRAY_CONCRETE_POWDER(252, 8, "Light Gray Concrete Powder", "((252)|(concrete[^0-9A-Z]*powder))[ ]*[:;|/.,\\-_~][ ]*((light[^0-9A-Z]*gr(a|e)y)|(silver)|8)", Version.V1_12),
	CYAN_CONCRETE_POWDER(252, 9, "Cyan Concrete Powder", "((252)|(concrete[^0-9A-Z]*powder))[ ]*[:;|/.,\\-_~][ ]*((cyan)|9)", Version.V1_12),
	PURPLE_CONCRETE_POWDER(252, 10, "Purple Concrete Powder", "((252)|(concrete[^0-9A-Z]*powder))[ ]*[:;|/.,\\-_~][ ]*((purple)|(10))", Version.V1_12),
	BLUE_CONCRETE_POWDER(252, 11, "Blue Concrete Powder", "((252)|(concrete[^0-9A-Z]*powder))[ ]*[:;|/.,\\-_~][ ]*((blue)|(11))", Version.V1_12),
	BROWN_CONCRETE_POWDER(252, 12, "Brown Concrete Powder", "((252)|(concrete[^0-9A-Z]*powder))[ ]*[:;|/.,\\-_~][ ]*((brown)|(12))", Version.V1_12),
	GREEN_CONCRETE_POWDER(252, 13, "Green Concrete Powder", "((252)|(concrete[^0-9A-Z]*powder))[ ]*[:;|/.,\\-_~][ ]*((green)|(13))", Version.V1_12),
	RED_CONCRETE_POWDER(252, 14, "Red Concrete Powder", "((252)|(concrete[^0-9A-Z]*powder))[ ]*[:;|/.,\\-_~][ ]*((red)|(14))", Version.V1_12),
	BLACK_CONCRETE_POWDER(252, 15, "Black Concrete Powder", "((252)|(concrete[^0-9A-Z]*powder))[ ]*[:;|/.,\\-_~][ ]*((black)|(15))", Version.V1_12),
	STRUCTURE_BLOCK(255, "Structure Block", Version.V1_9_USE_1_10),
	
	
	
	IRON_SHOVEL(256, "Iron Shovel"),
	IRON_PICKAXE(257, "Iron Pickaxe"),
	IRON_AXE(258, "Iron Axe"),
	FLINT_AND_STEEL(259, "Flint and Steel", "flint[^0-9A-Z]*&[^0-9A-Z]*steel"),
	
	APPLE(260, "Apple"),
	BOW(261, "Bow"),
	ARROW(262, "Arrow"),
	COAL(263, "Coal"),
	CHARCOAL(263, 1, "Charcoal", "coal[ ]*[:;|/.,\\-_~][ ]*1"),
	DIAMOND(264, "Diamond"),
	IRON_INGOT(265, "Iron Ingot"),
	GOLD_INGOT(266, "Gold Ingot"),
	IRON_SWORD(267, "Iron Sword"),
	WOODEN_SWORD(268, "Wooden Sword"),
	WOODEN_SHOVEL(269, "Wooden Shovel"),
	
	WOODEN_PICKAXE(270, "Wooden Pickaxe"),
	WOODEN_AXE(271, "Wooden Axe"),
	STONE_SWORD(272, "Stone Sword"),
	STONE_SHOVEL(273, "Stone Shovel"),
	STONE_PICKAXE(274, "Stone Pickaxe"),
	STONE_AXE(275, "Stone Axe"),
	DIAMOND_SWORD(276, "Diamond Sword"),
	DIAMOND_SHOVEL(277, "Diamond Shovel"),
	DIAMOND_PICKAXE(278, "Diamond Pickaxe"),
	DIAMOND_AXE(279, "Diamond Axe"),
	
	STICK(280, "Stick"),
	BOWL(281, "Bowl"),
	MUSHROOM_STEW(282, "Mushroom Stew"),
	GOLDEN_SWORD(283, "Golden Sword"),
	GOLDEN_SHOVEL(284, "Golden Shovel"),
	GOLDEN_PICKAXE(285, "Golden Pickaxe"),
	GOLDEN_AXE(286, "Golden Axe"),
	STRING(287, "String"),
	FEATHER(288, "Feather"),
	GUNPOWDER(289, "Gunpowder", "sulphur"),
	
	WOODEN_HOE(290, "Wooden Hoe"),
	STONE_HOE(291, "Stone Hoe"),
	IRON_HOE(292, "Iron Hoe"),
	DIAMOND_HOE(293, "Diamond Hoe"), //srsly
	GOLDEN_HOE(294, "Golden Hoe"),
	WHEAT_SEEDS(295, "Wheat Seeds"),
	WHEAT(296, "Wheat"),
	BREAD(297, "Bread"),
	LEATHER_HELMET(298, "Leather Helmet"),
	LEATHER_CHESTPLATE(299, "Leather Tunic", "leather[^0-9A-Z]*shirt"),
	
	
	LEATHER_LEGGINGS(300, "Leather Pants"),
	LEATHER_BOOTS(301, "Leather Boots"),
	CHAINMAIL_HELMET(302, "Chainmail Helmet"),
	CHAINMAIL_CHESTPLATE(303, "Chainmail Chestplate"),
	CHAINMAIL_LEGGINGS(304, "Chainmail Leggings"),
	CHAINMAIL_BOOTS(305, "Chainmail Boots"),
	IRON_HELMET(306, "Iron Helmet"),
	IRON_CHESTPLATE(307, "Iron Chestplate"),
	IRON_LEGGINGS(308, "Iron Leggings"),
	IRON_BOOTS(309, "Iron Boots"),
	
	DIAMOND_HELMET(310, "Diamond Helmet"),
	DIAMOND_CHESTPLATE(311, "Diamond Chestplate"),
	DIAMOND_LEGGINGS(312, "Diamond Leggings"),
	DIAMOND_BOOTS(313, "Diamond Boots"),
	GOLDEN_HELMET(314, "Golden Helmet"),
	GOLDEN_CHESTPLATE(315, "Golden Chestplate"),
	GOLDEN_LEGGINGS(316, "Golden Leggings"),
	GOLDEN_BOOTS(317, "Golden Boots"),
	FLINT(318, "Flint"),
	PORKCHOP(319, "Raw Porkchop"),
	
	COOKED_PORKCHOP(320, "Cooked Porkchop"),
	PAINTING(321, "Painting"), //WHY ARE THE IMAGES RANDOM?
	GOLDEN_APPLE(322, "Golden Apple", "gapple"),
	ENCHANTED_GOLDEN_APPLE(322, 1, "Enchanted Golden Apple", "(god[^0-9A-Z]*apple)|(golden[^0-9A-Z]*apple[ ]*[:;|/.,\\-_~][ ]*1)"), //I actually like that this was removed. Fight me.
	SIGN(323, "Sign"),
	WOODEN_DOOR(324, "Oak Door", "((oak[^0-9A-Z]*)?wood[^0-9A-Z]*door)|(^door)"),
	BUCKET(325, "Bucket"),
	WATER_BUCKET(326, "Water Bucket"), //Always bring one while mining
	LAVA_BUCKET(327, "Lava Bucket"), //Never hold it in your hotbar
	MINECART(328, "Minecart"),
	SADDLE(329, "Saddle"),
	
	IRON_DOOR(330, "Iron Door"),
	REDSTONE(331, "Redstone"),
	SNOWBALL(332, "Snowball"),
	BOAT(333, "Oak Boat"),
	LEATHER(334, "Leather"),
	MILK_BUCKET(335, "Milk Bucket"),
	BRICK(336, "Brick"),
	CLAY_BALL(337, "Clay"),
	REEDS(338, "Sugar Canes", "sugar[^0-9A-Z]*cane"),
	PAPER(339, "Paper"),
	
	BOOK(340, "Book"),
	SLIME_BALL(341, "Slimeball"),
	CHEST_MINECART(342, "Minecart with Chest", "minecart[^0-9A-Z]*chest"),
	FURNACE_MINECART(343, "Minecart with Furnace", "minecraft[^0-9A-Z]*furnace"),
	EGG(344, "Egg"),
	COMPASS(345, "Compass"),
	FISHING_ROD(346, "Fishing Rod"),
	CLOCK(347, "Clock"),
	GLOWSTONE_DUST(348, "Glowstone Dust"),
	FISH(349, "Raw Fish"),
	SALMON(349, 1, "Raw Salmon", "fish[ ]*[:;|/.,\\-_~][ ]*1"),
	CLOWNFISH(349, 2, "Clownfish", "fish[ ]*[:;|/.,\\-_~][ ]*2"),
	PUFFERFISH(349, 3, "Pufferfish", "fish[ ]*[:;|/.,\\-_~][ ]*3"),
	
	COOKED_FISH(350, "Cooked Fish"),
	COOKED_SALMON(350, 1, "Cooked Salmon", "cooked[^0-9A-Z]*fish[ ]*[:;|/.,\\-_~][ ]*1"),
	INK_SACK(351, "Ink Sack", "(inc[^0-9A-Z]*sac)|(^dye)"),
	ROSE_RED(351, 1, "Rose Red", "(red[^0-9A-Z]*dye)|(dye[ ]*[:;|/.,\\-_~][ ]*1$)"),
	CACTUS_GREEN(351, 2, "Cactus Green", "(^green[^0-9A-Z]*dye)|(dye[ ]*[:;|/.,\\-_~][ ]*2$)"),
	COCO_BEANS(351, 3, "Coco Beans", "(cocoa[^0-9A-Z]*beans)|(brown[^0-9A-Z]*dye)|(dye[ ]*[:;|/.,\\-_~][ ]*3$)"),
	LAPIS_LAZULI(351, 4, "Lapis Lazuli", "(lapis)|(^blue[^0-9A-Z]*dye)|(dye[ ]*[:;|/.,\\-_~][ ]*4$)"),
	PURPLE_DYE(351, 5, "Purple Dye", "dye[ ]*[:;|/.,\\-_~][ ]*5$"),
	CYAN_DYE(351, 6, "Cyan Dye", "dye[ ]*[:;|/.,\\-_~][ ]*6"),
	LIGHT_GRAY_DYE(351, 7, "Light Gray Dye", "dye[ ]*[:;|/.,\\-_~][ ]*7"),
	GRAY_DYE(351, 8, "Gray Dye", "dye[ ]*[:;|/.,\\-_~][ ]*8"),
	PINK_DYE(351, 9, "Pink Dye", "dye[ ]*[:;|/.,\\-_~][ ]*9"),
	LIME_DYE(351, 10, "Lime Dye", "(light[^0-9A-Z]*green[^0-9A-Z]*dye)|(dye[ ]*[:;|/.,\\-_~][ ]*10)"),
	DANDELION_YELLOW(351, 11, "Dandelion Yellow", "(yellow[^0-9A-Z]*dye)|(dye[ ]*[:;|/.,\\-_~][ ]*11)"),
	LIGHT_BLUE_DYE(351, 12, "Light Blue Dye", "(aqua[^0-9A-Z]*dye)|(dye[ ]*[:;|/.,\\-_~][ ]*12)"),
	MAGENTA_DYE(351, 13, "Magenta Dye", "dye[ ]*[:;|/.,\\-_~][ ]*13"),
	ORANGE_DYE(351, 14, "Orange Dye", "dye[ ]*[:;|/.,\\-_~][ ]*14"),
	BONE_MEAL(351, 15, "Bone Meal", "(white[^0-9A-Z]*dye)|(dye[ ]*[:;|/.,\\-_~][ ]*15)"),
	BONE(352, "Bone"), //Do not put any of these in my pizza
	SUGAR(353, "Sugar"),
	CAKE(354, "Cake"),
	BED(355, "Bed"),
	REPEATER(356, "Redstone Repeater"),
	COOKIE(357, "Cookie"),
	FILLED_MAP(358, "Map"),
	SHEARS(359, "Shears"),
	
	MELON(360, "Melon"),
	PUMPKIN_SEEDS(361, "Pumpkin Seeds"),
	MELON_SEEDS(362, "Melon Seeds"),
	BEEF(363, "Raw Beef"),
	COOKED_BEEF(364, "Steak"),
	CHICKEN(365, "Raw Chicken"),
	COOKED_CHICKEN(366, "Cooked Chicken"),
	ROTTEN_FLESH(367, "Rotten Flesh"),
	ENDER_PEARL(368, "Ender Pearl"),
	BLAZE_ROD(369, "Blaze Rod"),
	
	GHAST_TEAR(370, "Ghast Tear"),
	GOLD_NUGGET(371, "Gold Nugget"),
	NETHER_WART(372, "Nether Wart"),
	POTION(373, "Potion"),
	//Potion ID variants go here when brewing is added
	GLASS_BOTTLE(374, "Glass Bottle"),
	SPIDER_EYE(375, "Spider Eye"),
	FERMENTED_SPIDER_EYE(376, "Fermented Spider Eye"),
	BLAZE_POWDER(377, "Blaze Powder"),
	MAGMA_CREAM(378, "Magma Cream"),
	BREWING_STAND(379, "Brewing Stand"),
	
	CAULDRON(380, "Cauldron"),
	ENDER_EYE(381, "Eye of Ender"),
	SPECKLED_MELON(382, "Glistering Melon"),
	ELDER_GUARDIAN_SPAWN_EGG(383, 4, "Spawn Elder Guardian", "(elder[^0-9A-Z]*guardian[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*4)"),
	WITHER_SKELETON_SPAWN_EGG(383, 5, "Spawn Wither Skeleton", "(wither[^0-9A-Z]*skeleton[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*5)"),
	STRAY_SPAWN_EGG(383, 6, "Spawn Stray", "(stray[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*6)"),
	HUSK_SPAWN_EGG(383, 23, "Spawn Husk", "(husk[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*23)"),
	ZOMBIE_VILLAGER_SPAWN_EGG(383, 27, "Spawn Zombie Villager", "(zombie[^0-9A-Z]*villager[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*27)"),
	SKELETON_HORSE_SPAWN_EGG(383, 28, "Spawn Skeleton Horse", "(skeleton[^0-9A-Z]*horse[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*28)"),
	ZOMBIE_HORSE_SPAWN_EGG(383, 29, "Spawn Zombie Horse", "(zombie[^0-9A-Z]*horse[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*29)"),
	DONKEY_SPAWN_EGG(383, 31, "Spawn Donkey", "(donkey[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*31)"),
	MULE_SPAWN_EGG(383, 32, "Spawn Mule", "(mule[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*32)"),
	EVOKER_SPAWN_EGG(383, 34, "Spawn Evoker", "(evoker[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*34)"),
	VEX_SPAWN_EGG(383, 35, "Spawn Vex", "(vex[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*35)"),
	VINDICATOR_SPAWN_EGG(383, 36, "Spawn Vindicator", "(vindicator[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*36)"),
	CREEPER_SPAWN_EGG(383, 50, "Spawn Creeper", "(creeper[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*50)"),
	SKELETON_SPAWN_EGG(383, 51, "Spawn Skeleton", "(skeleton[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*51)"), //Spooky scary
	SPIDER_SPAWN_EGG(383, 52, "Spawn Spider", "(spider[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*52)"),
	ZOMBIE_SPAWN_EGG(383, 54, "Spawn Zombie", "(zombie[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*54)"),
	SLIME_SPAWN_EGG(383, 55, "Spawn Slime", "(slime[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*55)"),
	GHAST_SPAWN_EGG(383, 56, "Spawn Ghast", "(ghast[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*56)"),
	ZOMBIE_PIGMAN_SPAWN_EGG(383, 57, "Spawn Zombie Pigman", "(zombie[^0-9A-Z]*pigman[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*57)"),
	ENDERMAN_SPAWN_EGG(383, 58, "Spawn Enderman", "(enderman[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*58)"),
	CAVE_SPIDER_SPAWN_EGG(383, 59, "Spawn Cave Spider", "(cave[^0-9A-Z]*spider[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*59)"),
	SILVERFISH_SPAWN_EGG(383, 60, "Spawn Silverfish", "(silverfish[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*60)"),
	BLAZE_SPAWN_EGG(383, 61, "Spawn Blaze", "(blaze[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*61)"),
	MAGMA_CUBE_SPAWN_EGG(383, 62, "Spawn Magma Cube", "(magma[^0-9A-Z]*cube[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*62)"),
	BAT_SPAWN_EGG(383, 65, "Spawn Bat", "(bat[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*65)"),
	WITCH_SPAWN_EGG(383, 66, "Spawn Witch", "(witch[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*66)"),
	ENDERMITE_SPAWN_EGG(383, 67, "Spawn Endermite", "(endermite[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*67)"),
	GUARDIAN_SPAWN_EGG(383, 68, "Spawn Guardian", "(guardian[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*68)"),
	SHULKERSPAWN_EGG(383, 69, "Spawn Shulker", "(shulker[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*69)"),
	PIG_SPAWN_EGG(383, 90, "Spawn Pig", "(pig[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*90)"),
	SHEEP_SPAWN_EGG(383, 91, "Spawn Sheep", "(sheep[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*91)"),
	COW_SPAWN_EGG(383, 92, "Spawn Cow", "(cow[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*92)"),
	CHICKEN_SPAWN_EGG(383, 93, "Spawn Chicken", "(chicken[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*93)"),
	SQUID_SPAWN_EGG(383, 94, "Spawn Squid", "(squid[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*94)"),
	WOLF_SPAWN_EGG(383, 95, "Spawn Wolf", "(wolf[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*95)"),
	MOOSHROOM_SPAWN_EGG(383, 96, "Spawn Mooshroom", "(spawn[^0-9A-Z]*mushroom[^0-9A-Z]*cow)|((mooshroom)|(mushroom[^0-9A-Z]*cow)[^0-9A-Z]*(spawn[^0-9A-Z]*)?egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*96)"),
	OCELOT_SPAWN_EGG(383, 98, "Spawn Ocelot", "(spawn[^0-9A-Z]*ozelot)|(o(c|z)elot[^0-9A-Z]*(spawn[^0-9A-Z]*)?egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*98)"),
	HORSE_SPAWN_EGG(383, 100, "Spawn Horse", "(horse[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*100)"),
	RABBIT_SPAWN_EGG(383, 101, "Spawn Rabbit", "(spawn[^0-9A-Z]*bunny)|((rabbit)|(bunny)[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*101)"),
	POLAR_BEAR_SPAWN_EGG(383, 102, "Spawn Polar Bear", "(polar[^0-9A-Z]*bear[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*102)"),
	LLAMA_SPAWN_EGG(383, 103, "Spawn Llama", "(llama[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*103)"),
	VILLAGER_SPAWN_EGG(383, 120, "Spawn Villager", "(villager[^0-9A-Z]*egg)|(spawn[^0-9A-Z]*egg[ ]*[:;|/.,\\-_~][ ]*120)"),
	EXPERIENCE_BOTTLE(384, "Bottle o' Enchanting"),
	FIRE_CHARGE(385, "Fire Charge"),
	WRITABLE_BOOK(386, "Book and Quill", "book[^0-9A-Z]*&[^0-9A-Z]*quill"),
	WRITTEN_BOOK(387, "Written Book", "signed[^0-9A-Z]*book"),
	EMERALD(388, "Emerald"),
	ITEM_FRAME(389, "Item Frame"),
	
	FLOWER_POT(390, "Flower Pot"),
	CARROT(391, "Carrot"),
	POTATO(392, "Potato"),
	BAKED_POTATO(393, "Baked Potato"),
	POISONOUS_POTATO(394, "Poisonous Potato"),
	MAP(395, "Empty Map"),
	GOLDEN_CARROT(396, "Golden Carrot"),
	SKELETON_HEAD(397, "Mob Head (Skeleton)"),
	WITHER_SKELETON_HEAD(397, 1, "Mob Head (Wither Skeleton)"),
	ZOMBIE_HEAD(397, 2, "Mob Head (Zombie)"),
	PLAYER_HEAD(397, 3, "Mob Head (Human)"),
	CREEPER_HEAD(397, 4, "Mob Head (Creeper)"),
	DRAGON_HEAD(397, 5, "Mob Head (Dragon)"),
	CARROT_ON_A_STICK(398, "Carrot on a Stick", "carrot[^0-9A-Z]*stick"),
	NETHER_STAR(399, "Nether Star"),
	
	
	
	PUMPKIN_PIE(400, "Pumpkin Pie"),
	FIREWORKS(401, "Firework Rocket"),
	FIREWORK_CHARGE(402, "Firework Star"),
	ENCHANTED_BOOK(403, "Enchanted Book"),
	COMPARATOR(404, "Redstone Comparator"), //Not found
	NETHERBRICK(405, "Nether Brick"),
	QUARTZ(406, "Nether Quartz"),
	TNT_MINECART(407, "Minecart with TNT", "minecart[^0-9A-Z]*tnt"),
	HOPPER_MINECART(408, "Minecart with Hopper", "hopper[^0-9A-Z]*minecart"),
	PRISMARINE_SHARD(409, "Prismarine Shard", Version.V1_8),
	
	PRISMARINE_CRYSTALS(410, "Prismarine Crystals", Version.V1_8),
	RABBIT(411, "Raw Rabbit", Version.V1_8),
	COOKED_RABBIT(412, "Cooked Rabbit", Version.V1_8),
	RABBIT_STEW(413, "Rabbit Stew", Version.V1_8),
	RABBIT_FOOT(414, "Rabbit's Foot", Version.V1_8),
	RABBIT_HIDE(415, "Rabbit Hide", Version.V1_8),
	ARMOR_STAND(416, "Armor Stand", Version.V1_8),
	IRON_HORSE_ARMOR(417, "Iron Horse Armor"),
	GOLDEN_HORSE_ARMOR(418, "Golden Horse Armor"),
	DIAMOND_HORSE_ARMOR(419, "Diamond Horse Armor"),
	
	LEAD(420, "Lead"), //blaze it
	NAME_TAG(421, "Name Tag", "nametag"),
	COMMAND_BLOCK_MINECART(422, "Minecart with Command Block", "minecart[^0-9A-Z]*command[^0-9A-Z]*block"),
	MUTTON(423, "Raw Mutton", Version.V1_8),
	COOKED_MUTTON(424, "Cooked Mutton", Version.V1_8),
	BANNER(425, "Banner", Version.V1_8),
	//Missing ID
	SPRUCE_DOOR(427, "Spruce Door", "spruce[^0-9A-Z]*wood[^0-9A-Z]*door"),
	BIRCH_DOOR(428, "Birch Door", "birch[^0-9A-Z]*wood[^0-9A-Z]*door"),
	JUNGLE_DOOR(429, "Jungle Door", "jungle[^0-9A-Z]*wood[^0-9A-Z]*door"),
	
	ACACIA_DOOR(430, "Acacia Door", "acacia[^0-9A-Z]*wood[^0-9A-Z]*door"),
	DARK_OAK_DOOR(431, "Dark Oak Door", "dark[^0-9A-Z]*(oak[^0-9A-Z]*)?(wood[^0-9A-Z]*)?door"),
	CHORUS_FRUIT(432, "Chorus Fruit", Version.V1_9),
	POPPED_CHORUS_FRUIT(433, "Popped Chorus Fruit", Version.V1_9),
	BEETROOT(434, "Beetroot", Version.V1_9),
	BEETROOT_SEEDS(435, "Beetroot Seeds", Version.V1_9),
	BEETROOT_SOUP(436, "Beetroot Soup", Version.V1_9),
	DRAGON_BREATH(437, "Dragon's Breath", Version.V1_9),
	SPLASH_POTION(438, "Splash Potion"),
	//Potion IDs go here
	SPECTRAL_ARROW(439, "Spectral Arrow", "glowing[^0-9A-Z]*arrow", Version.V1_9),
	
	TIPPED_ARROW(440, "Tipped Arrow", Version.V1_9),
	LINGERING_POTION(441, "Lingering Potion", Version.V1_9),
	//Potion IDs go here
	SHIELD(442, "Shield", Version.V1_9),
	ELYTRA(443, "Elytra", Version.V1_9), //Best item in the game hands down
	SPRUCE_BOAT(444, "Spruce Boat", "spruce[^0-9A-Z]*(wood[^0-9A-Z]*)?boat", Version.V1_9),
	BIRCH_BOAT(445, "Birch Boat", "birch[^0-9A-Z]*(wood[^0-9A-Z]*)?boat", Version.V1_9),
	JUNGLE_BOAT(446, "Jungle Boat", "jungle[^0-9A-Z]*(wood[^0-9A-Z]*)?boat", Version.V1_9),
	ACACIA_BOAT(447, "Acacia Boat", "acacia[^0-9A-Z]*(wood[^0-9A-Z]*)?boat", Version.V1_9),
	DARK_OAK_BOAT(448, "Dark Oak Boat", "dark(oak[^0-9A-Z]*)?(wood[^0-9A-Z]*)?boat", Version.V1_9),
	TOTEM_OF_UNDYING(449, "Totem of Undying"), //I want to know what drugs they were on when they thought this was a good idea
	
	SHULKER_SHELL(450, "Shulker Shell", Version.V1_11),
	//Missing ID
	IRON_NUGGET(452, "Iron Nugget", Version.V1_11_1),
	
	
	
	RECORD_13(2256, "13 Disc"),
	RECORD_CAT(2257, "Cat Disc"),
	RECORD_BLOCKS(2258, "Blocks Disc"),
	RECORD_CHIRP(2259, "Chirp Disc"),
	RECORD_FAR(2260, "Far Disc"),
	RECORD_MALL(2261, "Mall Disc"),
	RECORD_MELLOHI(2262, "Mellohi Disc"),
	RECORD_STAL(2263, "Stal Disc"),
	RECORD_STRAD(2264, "Strad Disc"),
	RECORD_WARD(2265, "Ward Disc"),
	RECORD_11(2266, "11 Disc"),
	RECORD_WAIT(2267, "Wait Disc");
	
	
	
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
	private Item(int id, String name, Version version) {
		this(id, 0, name, null, version, null, 0);
	}
	private Item(int id, String name, String regex, Version version) {
		this(id, 0, name, regex, version, null, 0);
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
	
	/**
	 * Tests if a string matches the current item.
	 * The enum was designed with the assumption that items will be iterated top to bottom.
	 * @param str The string to match.
	 * Before sending a string to this method, trim with the below regex to save time.
	 * "(^[^0-9A-Z]+)|([^0-9A-Z\)]+$)" (case insensitive)
	 * @param mode The method of matching. It is preferred to iterate through this parameter.
	 * 0 is display name.
	 * 1 is item:data.
	 * 2 is regex assembled from display name.
	 * 3 is regex assembled from enum name.
	 * 4 is predefined regex.
	 * @throws IllegalArgumentException if mode is not between 0-4.
	 */
	public boolean matches(String str, int mode) {
		
		switch (mode) {
		case 0:
			//Display name
			return name.equalsIgnoreCase(str);
		case 1:
			//Item id and data
			String pattern = "^" + id;
			if (data != 0) pattern += "[ ]*[:;|/.,\\-_~][ ]*" + data;
			return Pattern.compile(pattern + "$").matcher(str).find();
		case 2:
			//Display name regex
			String displayRegex = "^";
			for (String s : name.split(" ")) {
				displayRegex += s + "[^0-9A-Z]*";
			}
			displayRegex += "$";
			return name.contains(" ") && Pattern.compile(displayRegex, Pattern.CASE_INSENSITIVE).matcher(str).find();
		case 3:
			//Enum name regex
			String enumRegex = "^";
			for (String s : this.name().split("_")) {
				enumRegex += s + "[^0-9A-Z]*";
			}
			enumRegex += "$";
			return name.contains(" ") && Pattern.compile(enumRegex, Pattern.CASE_INSENSITIVE).matcher(str).find();
		case 4:
			//Predefined regex
			return regex != null && Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(str).find();
		default:
			throw new IllegalArgumentException("Mode must be between 0-4!");
		}
		
	}
	
	/**
	 * @return The embed info for this item.
	 */
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
