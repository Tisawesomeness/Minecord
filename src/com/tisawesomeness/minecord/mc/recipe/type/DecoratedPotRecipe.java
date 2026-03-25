package com.tisawesomeness.minecord.mc.recipe.type;

import com.tisawesomeness.minecord.mc.recipe.Ingredient;
import com.tisawesomeness.minecord.mc.recipe.Recipe;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DecoratedPotRecipe extends CraftingRecipe {

    public DecoratedPotRecipe(String key, JSONObject recipe) {
        super(key, recipe);
    }

    @Override
    public boolean isShapeless() {
        return false;
    }

    @Override
    public List<Ingredient> getIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.addAll(getBack());
        ingredients.addAll(getLeft());
        ingredients.addAll(getRight());
        ingredients.addAll(getFront());
        return ingredients;
    }
    public List<Ingredient> getBack() {
        return Recipe.parseIngredients(recipe.get("back"));
    }
    public List<Ingredient> getLeft() {
        return Recipe.parseIngredients(recipe.get("left"));
    }
    public List<Ingredient> getRight() {
        return Recipe.parseIngredients(recipe.get("right"));
    }
    public List<Ingredient> getFront() {
        return Recipe.parseIngredients(recipe.get("front"));
    }

}
