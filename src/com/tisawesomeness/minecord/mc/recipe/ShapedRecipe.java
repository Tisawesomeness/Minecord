package com.tisawesomeness.minecord.mc.recipe;

import com.tisawesomeness.minecord.util.Utils;
import org.apache.commons.collections4.OrderedMap;
import org.apache.commons.collections4.map.LinkedMap;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class ShapedRecipe extends CraftingRecipe {

    protected ShapedRecipe(String key, JSONObject recipe) {
        super(key, recipe);
    }

    @Override
    public List<Ingredient> getIngredients() {
        return Utils.flatten(getIngredientKey().values());
    }

    public String[] getPattern() {
        String[] pattern = new String[3];
        JSONArray givenPattern = recipe.getJSONArray("pattern");
        for (int i = 0; i < 3; i++) {
            StringBuilder row = new StringBuilder(givenPattern.optString(i, "   "));
            while (row.length() < 3) {
                row.append(" ");
            }
            pattern[i] = row.toString();
        }
        return pattern;
    }

    public OrderedMap<Character, List<Ingredient>> getIngredientKey() {
        OrderedMap<Character, List<Ingredient>> map = new LinkedMap<>();
        JSONObject keyObj = recipe.getJSONObject("key");
        keyObj.keys().forEachRemaining(k -> map.put(k.charAt(0), parseIngredients(keyObj.get(k))));
        return map;
    }

}
