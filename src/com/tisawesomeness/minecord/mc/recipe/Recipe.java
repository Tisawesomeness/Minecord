package com.tisawesomeness.minecord.mc.recipe;

import com.tisawesomeness.minecord.mc.FeatureFlag;
import com.tisawesomeness.minecord.mc.FeatureFlagRegistry;
import com.tisawesomeness.minecord.mc.Version;
import com.tisawesomeness.minecord.util.Utils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.VisibleForTesting;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A recipe parsed from the recipes.json format.
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Recipe {

    private static final List<String> SHAPED_TYPES = Arrays.asList(
            "crafting_shaped", "crafting_special_tippedarrow", "crafting_decorated_pot"
    );
    private static final List<String> SHAPELSS_TYPES = Arrays.asList(
            "crafting_shapeless",
            "crafting_special_firework_star", "crafting_special_firework_star_fade", "crafting_special_firework_rocket",
            "crafting_special_shulkerboxcoloring", "crafting_special_suspiciousstew"
    );
    private static final List<String> SMELTING_TYPES = Arrays.asList(
            "smelting", "blasting", "smoking", "campfire_cooking"
    );
    private static final List<String> SMITHING_TYPES = Arrays.asList(
            "smithing_trim", "smithing_transform"
    );

    /**
     * The id/key/name of this recipe. Usually the same as the filename in data/minecraft/recipe,
     * but recipes defined in recipes.json do not need to have an in-game equivalent.
     */
    @Getter protected final String key;
    protected final JSONObject recipe;

    @VisibleForTesting
    public JSONObject json() {
        return recipe;
    }

    /**
     * Creates a new recipe from the given key and JSON. Note that even if a recipe is returned,
     * not all the JSON is parsed immediately, so recipe methods may error if the format is invalid.
     * @param key id/key/name of this recipe
     * @param recipe the JSON of a single recipe (passed by reference)
     * @return the recipe
     * @throws IllegalArgumentException if the recipe type is invalid or unsupported
     */
    public static Recipe parse(String key, JSONObject recipe) {
        String type = recipe.getString("type").substring("minecraft:".length());
        if (SHAPED_TYPES.contains(type)) {
            return new ShapedRecipe(key, recipe);
        }
        if (SHAPELSS_TYPES.contains(type)) {
            return new ShapelessRecipe(key, recipe);
        }
        if ("crafting_transmute".equals(type)) {
            return new TransmuteRecipe(key, recipe);
        }
        if (SMELTING_TYPES.contains(type)) {
            return new SmeltingRecipe(key, recipe);
        }
        if ("brewing".equals(type)) {
            return new BrewingRecipe(key, recipe);
        }
        if ("stonecutting".equals(type)) {
            return new StonecuttingRecipe(key, recipe);
        }
        if ("smithing".equals(type)) {
            return new LegacySmithingRecipe(key, recipe);
        }
        if (SMITHING_TYPES.contains(type)) {
            return new SmithingRecipe(key, recipe);
        }
        throw new IllegalArgumentException("invalid recipe type " + type);
    }

    /**
     * Creates a list of all ingredients that could possibly be used in this recipe, regardless of slot.
     * @return list of ingredients
     * @see TransmuteRecipe#shouldIngredientsIncludeResult()
     */
    public abstract List<Ingredient> getIngredients();

    /**
     * Gets the block used to craft this recipe, such as a crafting table or stonecutter.
     * If a recipe type has multiple possible crafting blocks (such as crafting table / crafter),
     * then only the most prominent block is returned.
     * @return namespaced ID of the block used to craft this recipe (such as crafting table, stonecutter)
     */
    public abstract String getTableItem();

    /**
     * Gets the output of crafting this recipe. Check {@link #getNotes()} for any special conditions.
     * @return output of crafting this recipe
     */
    public CraftResult getResult() {
        Object result = recipe.get("result");
        if (result instanceof String) {
            return new CraftResult((String) result, 1);
        }
        JSONObject obj = (JSONObject) result;
        return new CraftResult(obj.getString("id"), obj.optInt("count", 1));
    }

    /**
     * @return the version this recipe was added
     */
    public @Nullable Version getVersion() {
        return Utils.mapNullable(recipe.optJSONObject("properties"),
                prop -> prop.optString("version", null),
                Version::parse);
    }
    /**
     * @return the version this recipe was added to an experimental datapack
     */
    public @Nullable Version getDatapackVersion() {
        return Utils.mapNullable(recipe.optJSONObject("properties"),
                prop -> prop.optString("datapack_version", null),
                Version::parse);
    }
    /**
     * @return the version this recipe was removed
     */
    public @Nullable Version getRemovedVersion() {
        return Utils.mapNullable(recipe.optJSONObject("properties"),
                prop -> prop.optString("removed", null),
                Version::parse);
    }
    /**
     * @return the feature flag required to use this recipe in its introduction version
     */
    public @Nullable FeatureFlag getFeatureFlag() {
        String flag = Utils.mapNullable(recipe.optJSONObject("properties"),
                prop -> prop.optString("feature_flag", null));
        return FeatureFlagRegistry.get(flag).orElse(null);
    }
    /**
     * @return whether this recipes has been released, and is no longer experimental
     */
    public boolean isReleased() {
        FeatureFlag flag = getFeatureFlag();
        return flag == null || flag.isReleased();
    }
    /**
     * @return the feature flag that removes this recipe
     */
    public @Nullable FeatureFlag getRemovedInFlag() {
        String flag = Utils.mapNullable(recipe.optJSONObject("properties"),
                prop -> prop.optString("removed_in_flag", null));
        return FeatureFlagRegistry.get(flag).orElse(null);
    }
    /**
     * @return the version this recipe's feature flag was removed in, but only if that version is different from the flag's release version
     */
    public @Nullable Version getFlagRemovedVersion() {
        return Utils.mapNullable(recipe.optJSONObject("properties"),
                prop -> prop.optString("flag_removed_version", null),
                Version::parse);
    }

    /**
     * @return experience gained for crafting this recipe
     */
    public double getExperience() {
        return recipe.optDouble("experience", 0.0);
    }

    /**
     * Determines whether the image accompanying this recipe is a gif or a png.
     * @return whether this recipe generates an animated image
     */
    public boolean isAnimated() {
        Boolean animated = Utils.mapNullable(recipe.optJSONObject("properties"),
                prop -> prop.optBoolean("animated", false));
        return Boolean.TRUE.equals(animated);
    }

    /**
     * @return additional details about the recipe to display to the user
     */
    public @Nullable String getNotes() {
        return Utils.mapNullable(recipe.optJSONObject("lang"),
                langs -> langs.optJSONObject("en_US"),
                lang -> lang.optString("notes", null));
    }

    protected static List<Ingredient> parseIngredients(Object ingredients) {
        if (ingredients instanceof String) {
            String ingredient = (String) ingredients;
            if (ingredient.startsWith("#")) {
                return Collections.singletonList(new Ingredient.Tag(ingredient.substring(1)));
            }
            return Collections.singletonList(new Ingredient.Item(ingredient));
        }
        if (ingredients instanceof JSONArray) {
            JSONArray arr = (JSONArray) ingredients;
            List<Ingredient> list = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++) {
                list.add(new Ingredient.Item(arr.getString(i)));
            }
            return list;
        }
        throw new IllegalArgumentException("invalid ingredient " + ingredients);
    }

    @Override
    public String toString() {
        return key;
    }

}
