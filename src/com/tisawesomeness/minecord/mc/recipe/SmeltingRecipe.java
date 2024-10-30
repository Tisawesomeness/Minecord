package com.tisawesomeness.minecord.mc.recipe;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;

import java.util.List;

public class SmeltingRecipe extends Recipe {

    protected SmeltingRecipe(String key, JSONObject recipe) {
        super(key, recipe);
    }

    @Override
    public List<Ingredient> getIngredients() {
        return parseIngredients(recipe.get("ingredient"));
    }

    @Override
    public String getTableItem() {
        return "minecraft:furnace";
    }

    public Type getType() {
        return Type.of(recipe.getString("type").substring("minecraft:".length()));
    }

    @RequiredArgsConstructor
    public enum Type {
        SMELTING("smelting"),
        BLASTING("blasting"),
        SMOKING("smoking"),
        CAMPFIRE_COOKING("campfire_cooking");

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
