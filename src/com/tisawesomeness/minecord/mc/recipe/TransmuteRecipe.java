package com.tisawesomeness.minecord.mc.recipe;

import com.tisawesomeness.minecord.util.Utils;
import org.apache.commons.collections4.ListUtils;
import org.json.JSONObject;

import java.util.List;

public class TransmuteRecipe extends CraftingRecipe {

    protected TransmuteRecipe(String key, JSONObject recipe) {
        super(key, recipe);
    }

    @Override
    public List<Ingredient> getIngredients() {
        return ListUtils.union(getMaterial(), getInput());
    }
    public List<Ingredient> getInput() {
        return parseIngredients(recipe.get("input"));
    }
    public List<Ingredient> getMaterial() {
        return parseIngredients(recipe.get("material"));
    }

    /**
     * Whether it is okay for this recipe's ingredients to include the result.
     * If false, the result of this recipe should be manually removed from the list of ingredients.
     * Note that the result item can be hidden in a tag.
     * @return true or false
     */
    public boolean shouldIngredientsIncludeResult() {
        Boolean includeResult = Utils.mapNullable(recipe.optJSONObject("properties"),
                prop -> prop.optBoolean("include_result", false));
        return Boolean.TRUE.equals(includeResult);
    }

}
