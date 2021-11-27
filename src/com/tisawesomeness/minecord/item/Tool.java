package com.tisawesomeness.minecord.item;

public enum Tool {
	
	SWORD(new Enchantment[]{}),
	AXE(new Enchantment[]{}),
	PICKAXE(new Enchantment[]{}),
	SHOVEL(PICKAXE.getEnchantments()),
	BOW(new Enchantment[]{}),
	FISHING_ROD(new Enchantment[]{}),
	HELMET(new Enchantment[]{}),
	CHESTPLATE(new Enchantment[]{}),
	LEGGINGS(new Enchantment[]{}),
	BOOTS(new Enchantment[]{}),
	BOOK(new Enchantment[]{});
	
	Enchantment[] enchantments;
	
	Tool(Enchantment[] enchantments) {
		this.enchantments = enchantments;
	}
	
	public Enchantment[] getEnchantments() {return enchantments;}

}
