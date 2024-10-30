package com.tisawesomeness.minecord.mc.recipe;

import com.tisawesomeness.minecord.util.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShapelessRecipe extends Recipe {

    protected ShapelessRecipe(String key, JSONObject recipe) {
        super(key, recipe);
    }

    @Override
    public List<Ingredient> getIngredients() {
        return Utils.flatten(getIngredientsPerSlot());
    }
    public List<List<Ingredient>> getIngredientsPerSlot() {
        List<List<Ingredient>> slots = new ArrayList<>();
        JSONArray ingredients = recipe.getJSONArray("ingredients");
        for (int i = 0; i < ingredients.length(); i++) {
            slots.add(parseIngredients(ingredients.get(i)));
        }
        return slots;
    }

    @Override
    public String getTableItem() {
        return "minecraft:crafting_table";
    }

}
