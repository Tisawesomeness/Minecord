package com.tisawesomeness.minecord.mc.recipe.type;

import com.tisawesomeness.minecord.mc.recipe.Ingredient;
import com.tisawesomeness.minecord.mc.recipe.Recipe;
import org.apache.commons.collections4.ListUtils;
import org.json.JSONObject;

import java.util.List;

public class BookCloningRecipe extends CraftingRecipe {

    public BookCloningRecipe(String key, JSONObject recipe) {
        super(key, recipe);
    }

    @Override
    public boolean isShapeless() {
        return true;
    }

    @Override
    public List<Ingredient> getIngredients() {
        return ListUtils.union(getSource(), getMaterial());
    }
    public List<Ingredient> getSource() {
        return Recipe.parseIngredients(recipe.get("source"));
    }
    public List<Ingredient> getMaterial() {
        return Recipe.parseIngredients(recipe.get("material"));
    }

}
