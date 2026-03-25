package com.tisawesomeness.minecord.mc.recipe.type;

import com.tisawesomeness.minecord.mc.recipe.Ingredient;
import com.tisawesomeness.minecord.mc.recipe.Recipe;
import org.apache.commons.collections4.ListUtils;
import org.json.JSONObject;

import java.util.List;

public class FireworkStarFadeRecipe extends CraftingRecipe {

    public FireworkStarFadeRecipe(String key, JSONObject recipe) {
        super(key, recipe);
    }

    @Override
    public boolean isShapeless() {
        return true;
    }

    @Override
    public List<Ingredient> getIngredients() {
        return ListUtils.union(getTarget(), getDye());
    }
    public List<Ingredient> getTarget() {
        return Recipe.parseIngredients(recipe.get("target"));
    }
    public List<Ingredient> getDye() {
        return Recipe.parseIngredients(recipe.get("dye"));
    }

}
