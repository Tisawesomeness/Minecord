package com.tisawesomeness.minecord.mc.recipe;

import org.json.JSONObject;

import java.util.List;

public class StonecuttingRecipe extends Recipe {

    protected StonecuttingRecipe(String key, JSONObject recipe) {
        super(key, recipe);
    }

    @Override
    public List<Ingredient> getIngredients() {
        return parseIngredients(recipe.get("ingredient"));
    }

    @Override
    public String getTableItem() {
        return "minecraft:stonecutter";
    }

}
