package com.tisawesomeness.minecord.mc.recipe.type;

import com.tisawesomeness.minecord.mc.recipe.Recipe;
import org.json.JSONObject;

public abstract class CraftingRecipe extends Recipe {

    public CraftingRecipe(String key, JSONObject recipe) {
        super(key, recipe);
    }

    public abstract boolean isShapeless();

    @Override
    public String getTableItem() {
        return "minecraft:crafting_table";
    }

}
