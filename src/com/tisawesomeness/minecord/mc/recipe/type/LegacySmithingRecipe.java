package com.tisawesomeness.minecord.mc.recipe.type;

import com.tisawesomeness.minecord.mc.recipe.Ingredient;
import com.tisawesomeness.minecord.mc.recipe.Recipe;
import org.apache.commons.collections4.ListUtils;
import org.json.JSONObject;

import java.util.List;

public class LegacySmithingRecipe extends Recipe {

    public LegacySmithingRecipe(String key, JSONObject recipe) {
        super(key, recipe);
    }

    @Override
    public List<Ingredient> getIngredients() {
        return ListUtils.union(getBase(), getAddition());
    }
    public List<Ingredient> getBase() {
        return parseIngredients(recipe.get("base"));
    }
    public List<Ingredient> getAddition() {
        return parseIngredients(recipe.get("addition"));
    }

    @Override
    public String getTableItem() {
        return "minecraft:smithing_table";
    }

}
