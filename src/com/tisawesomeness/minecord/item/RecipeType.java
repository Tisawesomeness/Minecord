package com.tisawesomeness.minecord.item;

public enum RecipeType {
	CRAFTING("Crafting"),
	SHAPELESS("Shapeless"),
	CRAFTING_SHAPELESS("Crafting/Shapeless"),
	SMELTING("Smelting"),
	BREWING("Brewing");
	
	private String s;
	private RecipeType(String s) {
		this.s = s;
	}
	public String toString() {
		return s;
	}
}