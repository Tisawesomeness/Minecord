package com.tisawesomeness.minecord.mc.recipe;

import org.json.JSONObject;

public abstract class CraftingRecipe extends Recipe {

    protected CraftingRecipe(String key, JSONObject recipe) {
        super(key, recipe);
    }

    @Override
    public String getTableItem() {
        return "minecraft:crafting_table";
    }

}
