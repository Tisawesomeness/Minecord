package com.tisawesomeness.minecord.item;

public enum Recipe {
	
	STONE(Item.STONE, "https://minecord.github.io/recipes/0-0.png",
			RecipeType.SMELTING),
	GRANITE(Item.GRANITE, "https://minecord.github.io/recipes/1-1.png",
			RecipeType.SHAPELESS, Version.V1_8),
	POLISHED_GRANITE(Item.POLISHED_GRANITE, "https://minecord.github.io/recipes/1-2.png",
			RecipeType.CRAFTING, Version.V1_8),
	DIORITE(Item.DIORITE, "https://minecord.github.io/recipes/1-3.png",
			RecipeType.CRAFTING, Version.V1_8),
	POLISHED_DIORITE(Item.POLISHED_DIORITE, "https://minecord.github.io/recipes/1-4.png",
			RecipeType.CRAFTING, Version.V1_8),
	ANDESITE(Item.ANDESITE, "https://minecord.github.io/recipes/1-5.png",
			RecipeType.SHAPELESS, Version.V1_8),
	POLISHED_ANDESITE(Item.POLISHED_ANDESITE, "https://minecord.github.io/recipes/1-6.png",
			RecipeType.CRAFTING, Version.V1_8),
	OAK_PLANKS(Item.OAK_PLANKS, "https://minecord.github.io/recipes/5-0.png",
			RecipeType.CRAFTING),
	SPRUCE_PLANKS(Item.SPRUCE_PLANKS, "https://minecord.github.io/recipes/5-1.png",
			RecipeType.CRAFTING),
	BIRCH_PLANKS(Item.BIRCH_PLANKS, "https://minecord.github.io/recipes/5-2.png",
			RecipeType.CRAFTING),
	JUNGLE_PLANKS(Item.JUNGLE_PLANKS, "https://minecord.github.io/recipes/5-3.png",
			RecipeType.CRAFTING),
	ACACIA_PLANKS(Item.ACACIA_PLANKS, "https://minecord.github.io/recipes/5-4.png",
			RecipeType.CRAFTING),
	DARK_OAK_PLANKS(Item.DARK_OAK_PLANKS, "https://minecord.github.io/recipes/5-5.png",
			RecipeType.CRAFTING);
	
	public final Item item;
	public final String image;
	public final RecipeType type;
	public final Version version;
	
	private Recipe(Item item, String image, RecipeType type) {
		this(item, image, type, null);
	}
	
	private Recipe(Item item, String image, RecipeType type, Version version) {
		this.item = item;
		this.image = image;
		this.type = type;
		this.version = version;
	}

}
