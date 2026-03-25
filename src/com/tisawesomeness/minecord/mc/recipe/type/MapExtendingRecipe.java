package com.tisawesomeness.minecord.mc.recipe.type;

import com.tisawesomeness.minecord.mc.recipe.Ingredient;
import com.tisawesomeness.minecord.mc.recipe.Recipe;
import org.apache.commons.collections4.ListUtils;
import org.json.JSONObject;

import java.util.List;

public class MapExtendingRecipe extends CraftingRecipe {

    public MapExtendingRecipe(String key, JSONObject recipe) {
        super(key, recipe);
    }

    @Override
    public boolean isShapeless() {
        return false;
    }

    @Override
    public List<Ingredient> getIngredients() {
        return ListUtils.union(getMap(), getMaterial());
    }
    public List<Ingredient> getMap() {
        return Recipe.parseIngredients(recipe.get("map"));
    }
    public List<Ingredient> getMaterial() {
        return Recipe.parseIngredients(recipe.get("material"));
    }

}
