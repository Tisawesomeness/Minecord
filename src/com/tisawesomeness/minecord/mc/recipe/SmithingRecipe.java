package com.tisawesomeness.minecord.mc.recipe;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.json.JSONObject;

import java.util.List;

public class SmithingRecipe extends Recipe {

    protected SmithingRecipe(String key, JSONObject recipe) {
        super(key, recipe);
    }

    @Override
    public List<Ingredient> getIngredients() {
        return ListUtils.union(getBase(), ListUtils.union(getTemplate(), getAddition()));
    }
    public List<Ingredient> getTemplate() {
        return parseIngredients(recipe.get("template"));
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

    public Type getType() {
        return Type.of(recipe.getString("type").substring("minecraft:".length()));
    }

    @RequiredArgsConstructor
    public enum Type {
        TRANSFORM("smithing_transform"),
        TRIM("smithing_trim");

        private final String id;

        public static Type of(String id) {
            for (Type type : values()) {
                if (type.id.equals(id)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("invalid id " + id);
        }
    }

}
