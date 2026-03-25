package com.tisawesomeness.minecord.mc.recipe;

import com.tisawesomeness.minecord.mc.FeatureFlag;
import com.tisawesomeness.minecord.mc.FeatureFlagRegistry;
import com.tisawesomeness.minecord.mc.Version;
import com.tisawesomeness.minecord.mc.recipe.type.*;
import com.tisawesomeness.minecord.util.Utils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.VisibleForTesting;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A recipe parsed from the recipes.json format.
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Recipe {

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
        switch (type) {
            case "crafting_shaped":
                return new ShapedRecipe(key, recipe);
            case "crafting_decorated_pot":
                return new DecoratedPotRecipe(key, recipe);
            case "crafting_imbue":
                return new ImbueRecipe(key, recipe);
            case "crafting_special_mapextending":
                return new MapExtendingRecipe(key, recipe);
            case "crafting_shapeless":
            case "crafting_special_shulkerboxcoloring":
            case "crafting_special_suspiciousstew":
                return new ShapelessRecipe(key, recipe);
            case "crafting_special_firework_star":
                return new FireworkStarRecipe(key, recipe);
            case "crafting_special_firework_star_fade":
                return new FireworkStarFadeRecipe(key, recipe);
            case "crafting_special_firework_rocket":
                return new FireworkRocketRecipe(key, recipe);
            case "crafting_transmute":
                return new TransmuteRecipe(key, recipe);
            case "crafting_dye":
                return new DyeRecipe(key, recipe);
            case "crafting_special_bannerduplicate":
                return new BannerDuplicateRecipe(key, recipe);
            case "crafting_special_bookcloning":
                return new BookCloningRecipe(key, recipe);
            case "crafting_special_shielddecoration":
                return new ShieldDecorationRecipe(key, recipe);
            case "smelting":
            case "blasting":
            case "smoking":
            case "campfire_cooking":
                return new SmeltingRecipe(key, recipe);
            case "stonecutting":
                return new StonecuttingRecipe(key, recipe);
            case "smithing":
                return new LegacySmithingRecipe(key, recipe);
            case "smithing_trim":
            case "smithing_transform":
                return new SmithingRecipe(key, recipe);
            case "brewing":
                return new BrewingRecipe(key, recipe);
            case "cartography":
                return new CartographyRecipe(key, recipe);
            // no repair item recipes since that would clutter the recipe browser
            default:
                throw new IllegalArgumentException("invalid recipe type " + type);
        }
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
     * @return whether this recipe has been released, and is no longer experimental
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
