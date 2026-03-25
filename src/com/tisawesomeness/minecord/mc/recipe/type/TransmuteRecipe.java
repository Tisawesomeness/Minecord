package com.tisawesomeness.minecord.mc.recipe.type;

import com.tisawesomeness.minecord.mc.recipe.Ingredient;
import com.tisawesomeness.minecord.util.Utils;
import com.tisawesomeness.minecord.util.type.IntRange;
import org.apache.commons.collections4.ListUtils;
import org.json.JSONObject;

import java.util.List;

public class TransmuteRecipe extends CraftingRecipe {

    public TransmuteRecipe(String key, JSONObject recipe) {
        super(key, recipe);
    }

    @Override
    public boolean isShapeless() {
        return true;
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

    public IntRange getMaterialCount() {
        JSONObject materialCount = recipe.optJSONObject("material_count");
        if (materialCount == null) {
            return new IntRange(1, 1);
        }
        return new IntRange(materialCount.optInt("min", 1), materialCount.optInt("max", 1));
    }
    public boolean addMaterialCountToResult() {
        return recipe.optBoolean("add_material_count_to_result", false);
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
