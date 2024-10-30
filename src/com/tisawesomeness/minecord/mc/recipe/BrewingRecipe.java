package com.tisawesomeness.minecord.mc.recipe;

import org.apache.commons.collections4.ListUtils;
import org.json.JSONObject;

import java.util.List;

public class BrewingRecipe extends Recipe {

    protected BrewingRecipe(String key, JSONObject recipe) {
        super(key, recipe);
    }

    @Override
    public List<Ingredient> getIngredients() {
        return ListUtils.union(getReagent(), getBase());
    }
    public List<Ingredient> getReagent() {
        return parseIngredients(recipe.get("reagent"));
    }
    public List<Ingredient> getBase() {
        return parseIngredients(recipe.get("base"));
    }

    @Override
    public String getTableItem() {
        return "minecraft:brewing_stand";
    }

}
