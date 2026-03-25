package com.tisawesomeness.minecord.mc.recipe.type;

import com.tisawesomeness.minecord.mc.recipe.Ingredient;
import com.tisawesomeness.minecord.mc.recipe.Recipe;
import org.json.JSONObject;

import java.util.List;

public class BannerDuplicateRecipe extends CraftingRecipe {

    public BannerDuplicateRecipe(String key, JSONObject recipe) {
        super(key, recipe);
    }

    @Override
    public boolean isShapeless() {
        return true;
    }

    @Override
    public List<Ingredient> getIngredients() {
        return getBanner();
    }
    public List<Ingredient> getBanner() {
        return Recipe.parseIngredients(recipe.get("banner"));
    }

}
