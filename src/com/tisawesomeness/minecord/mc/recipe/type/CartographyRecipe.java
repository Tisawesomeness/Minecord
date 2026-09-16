package com.tisawesomeness.minecord.mc.recipe.type;

import com.tisawesomeness.minecord.mc.recipe.CraftResult;
import com.tisawesomeness.minecord.mc.recipe.Ingredient;
import com.tisawesomeness.minecord.mc.recipe.Recipe;
import org.apache.commons.collections4.ListUtils;
import org.json.JSONObject;

import java.util.List;

public class CartographyRecipe extends Recipe {

    public CartographyRecipe(String key, JSONObject recipe) {
        super(key, recipe);
    }

    @Override
    public List<Ingredient> getIngredients() {
        return ListUtils.union(getMap(), getMaterial());
    }

    @Override
    public String getTableItem() {
        return "minecraft:cartography_table";
    }

    public List<Ingredient> getMap() {
        return parseIngredients(recipe.get("map"));
    }
    public List<Ingredient> getMaterial() {
        return parseIngredients(recipe.get("material"));
    }

    @Override
    public CraftResult getResult() {
        Object result = recipe.get("result");
        if (result instanceof JSONObject) {
            JSONObject obj = (JSONObject) result;
            if (!obj.has("id")) {
                return new CraftResult.Input(getMap(), obj.optInt("count", 1));
            }
        }
        return super.getResult();
    }

}
