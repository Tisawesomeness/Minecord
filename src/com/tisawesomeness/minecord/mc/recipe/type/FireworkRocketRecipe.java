package com.tisawesomeness.minecord.mc.recipe.type;

import com.tisawesomeness.minecord.mc.recipe.Ingredient;
import com.tisawesomeness.minecord.mc.recipe.Recipe;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FireworkRocketRecipe extends CraftingRecipe {

    public FireworkRocketRecipe(String key, JSONObject recipe) {
        super(key, recipe);
    }

    @Override
    public boolean isShapeless() {
        return true;
    }

    @Override
    public List<Ingredient> getIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.addAll(getShell());
        ingredients.addAll(getFuel());
        ingredients.addAll(getStar());
        return ingredients;
    }
    public List<Ingredient> getShell() {
        return Recipe.parseIngredients(recipe.get("shell"));
    }
    public List<Ingredient> getFuel() {
        return Recipe.parseIngredients(recipe.get("fuel"));
    }
    public List<Ingredient> getStar() {
        return Recipe.parseIngredients(recipe.get("star"));
    }

}
