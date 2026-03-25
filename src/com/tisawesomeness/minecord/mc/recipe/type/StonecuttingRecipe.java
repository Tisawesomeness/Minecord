package com.tisawesomeness.minecord.mc.recipe.type;

import com.tisawesomeness.minecord.mc.recipe.Ingredient;
import com.tisawesomeness.minecord.mc.recipe.Recipe;
import org.json.JSONObject;

import java.util.List;

public class StonecuttingRecipe extends Recipe {

    public StonecuttingRecipe(String key, JSONObject recipe) {
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
