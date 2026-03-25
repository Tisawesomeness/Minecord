package com.tisawesomeness.minecord.mc.recipe.type;

import com.tisawesomeness.minecord.mc.item.FireworkShape;
import com.tisawesomeness.minecord.mc.recipe.Ingredient;
import com.tisawesomeness.minecord.mc.recipe.Recipe;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class FireworkStarRecipe extends CraftingRecipe {

    public FireworkStarRecipe(String key, JSONObject recipe) {
        super(key, recipe);
    }

    @Override
    public boolean isShapeless() {
        return true;
    }

    @Override
    public List<Ingredient> getIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.addAll(getTrail());
        ingredients.addAll(getTwinkle());
        ingredients.addAll(getFuel());
        ingredients.addAll(getDye());
        for (List<Ingredient> shapeIngredients : getShapes().values()) {
            ingredients.addAll(shapeIngredients);
        }
        return ingredients;
    }
    public List<Ingredient> getTrail() {
        return Recipe.parseIngredients(recipe.get("trail"));
    }
    public List<Ingredient> getTwinkle() {
        return Recipe.parseIngredients(recipe.get("twinkle"));
    }
    public List<Ingredient> getFuel() {
        return Recipe.parseIngredients(recipe.get("fuel"));
    }
    public List<Ingredient> getDye() {
        return Recipe.parseIngredients(recipe.get("dye"));
    }
    public Map<FireworkShape, List<Ingredient>> getShapes() {
        EnumMap<FireworkShape, List<Ingredient>> shapes = new EnumMap<>(FireworkShape.class);
        JSONObject shapesObj = recipe.getJSONObject("shapes");
        for (FireworkShape shape : FireworkShape.values()) {
            String shapeIngredientStr = shapesObj.optString(shape.getId());
            if (!shapeIngredientStr.isEmpty()) {
                shapes.put(shape, Recipe.parseIngredients(shapeIngredientStr));
            }
        }
        return shapes;
    }

}
