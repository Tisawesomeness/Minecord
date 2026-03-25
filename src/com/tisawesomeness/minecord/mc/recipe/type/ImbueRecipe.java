package com.tisawesomeness.minecord.mc.recipe.type;

import com.tisawesomeness.minecord.mc.recipe.Ingredient;
import com.tisawesomeness.minecord.mc.recipe.Recipe;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ImbueRecipe extends CraftingRecipe {

    public ImbueRecipe(String key, JSONObject recipe) {
        super(key, recipe);
    }

    @Override
    public boolean isShapeless() {
        return false;
    }

    @Override
    public List<Ingredient> getIngredients() {
        List<Ingredient> ingredients = new ArrayList<>(getSource());
        for (int i = 0; i < 8; i++) {
            ingredients.addAll(getMaterial());
        }
        return ingredients;
    }
    public List<Ingredient> getSource() {
        return Recipe.parseIngredients(recipe.get("source"));
    }
    public List<Ingredient> getMaterial() {
        return Recipe.parseIngredients(recipe.get("material"));
    }

}
