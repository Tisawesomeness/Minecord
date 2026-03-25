package com.tisawesomeness.minecord.mc.recipe.type;

import com.tisawesomeness.minecord.mc.recipe.Ingredient;
import org.apache.commons.collections4.ListUtils;
import org.json.JSONObject;

import java.util.List;

public class ShieldDecorationRecipe extends CraftingRecipe {

    public ShieldDecorationRecipe(String key, JSONObject recipe) {
        super(key, recipe);
    }

    @Override
    public boolean isShapeless() {
        return true;
    }

    @Override
    public List<Ingredient> getIngredients() {
        return ListUtils.union(getBanner(), getTarget());
    }
    public List<Ingredient> getBanner() {
        return parseIngredients(recipe.get("banner"));
    }
    public List<Ingredient> getTarget() {
        return parseIngredients(recipe.get("target"));
    }

}
