package com.tisawesomeness.minecord.mc.recipe;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.mc.FeatureFlag;
import com.tisawesomeness.minecord.mc.FeatureFlagRegistry;
import com.tisawesomeness.minecord.mc.Version;
import com.tisawesomeness.minecord.mc.VersionRegistry;
import com.tisawesomeness.minecord.mc.item.ItemRegistry;
import com.tisawesomeness.minecord.util.RequestUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.collections4.OrderedMap;
import org.apache.commons.collections4.map.LinkedMap;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class RecipeRegistry {

    private static final List<Class<? extends Recipe>> RECIPE_TYPE_ORDER = Arrays.asList(
            ShapedRecipe.class, ShapelessRecipe.class, TransmuteRecipe.class, StonecuttingRecipe.class,
            SmeltingRecipe.class, SmithingRecipe.class, LegacySmithingRecipe.class, BrewingRecipe.class
    );

    private static OrderedMap<String, Recipe> recipes;
    private static JSONObject tags;

    @VisibleForTesting
    public static OrderedMap<String, Recipe> getRecipes() {
        return recipes;
    }
    public static @Nullable Recipe get(String key) {
        return recipes.get(key);
    }
    public static boolean contains(String key) {
        return recipes.containsKey(key);
    }

    /**
     * Initializes the recipe database by reading from file
     *
     * @param path The path to read from
     * @throws IOException on IO error
     */
    public static void init(String path) throws IOException {
        parseRecipes(RequestUtils.loadJSON(path + "/recipes.json"));
        System.out.println("Loaded " + recipes.size() + " recipes");
        tags = RequestUtils.loadJSON(path + "/tags.json");
    }
    private static void parseRecipes(JSONObject recipesObj) {
        OrderedMap<String, Recipe> recipes = new LinkedMap<>();
        Iterable<String> keys = recipesObj::keys;
        for (String key : keys) {
            Recipe recipe = Recipe.parse(key, recipesObj.getJSONObject(key));
            recipes.put(key, recipe);
        }
        RecipeRegistry.recipes = recipes;
    }

    /**
     * Creates an EmbedBuilder from a recipe
     *
     * @param recipe The name of the recipe
     * @return An EmbedBuilder containing properties of the item
     */
    public static EmbedBuilder displayImg(Recipe recipe) {
        EmbedBuilder eb = new EmbedBuilder();
        String item = ItemRegistry.searchNoStats(recipe.getResult().getItem());
        eb.setTitle(ItemRegistry.getDistinctDisplayName(item));
        String img = Config.getRecipeImageHost() + getImage(recipe);
        eb.setImage(img);
        eb.setColor(Bot.color);
        eb.setDescription(getMetadata(recipe));
        return eb;
    }
    private static String getMetadata(Recipe recipe) {
        StringJoiner lines = new StringJoiner("\n");
        double xp = recipe.getExperience();
        if (xp > 0) {
            lines.add(String.format("**XP:** %s", xp));
        }
        Version version = recipe.getVersion();
        Version datapackVersion = recipe.getDatapackVersion();
        String versionStr = datapackVersion == null
                ? (version == null ? null : version.toString())
                : String.format("%s Datapack or %s", version, datapackVersion);
        Version removed = recipe.getRemovedVersion();
        FeatureFlag feature = recipe.getFeatureFlag();
        if (version != null && removed != null) {
            String lastVersion = VersionRegistry.getPreviousVersion(removed)
                    .map(Version::toString)
                    .orElse("???");
            if (feature != null) {
                lines.add(String.format("**Version:** %s (%s experiment) - %s", versionStr, feature.getDisplayName(), lastVersion));
                feature.getReleaseVersion().ifPresent(releaseVersion -> {
                    if (releaseVersion.compareTo(removed) < 0) {
                        lines.add(String.format("**Released:** %s", releaseVersion));
                    }
                });
            } else {
                lines.add(String.format("**Version:** %s - %s", versionStr, lastVersion));
            }
        } else if (version != null) {
            if (feature != null) {
                lines.add(String.format("**Version:** %s (%s experiment)", versionStr, feature.getDisplayName()));
                feature.getReleaseVersion().ifPresent(releaseVersion -> {
                    lines.add(String.format("**Released:** %s", releaseVersion));
                });
            } else {
                lines.add(String.format("**Version:** %s", versionStr));
            }
        } else if (removed != null) {
            lines.add(String.format("**Removed In:** %s", removed));
        }
        Version flagRemovedVersion = recipe.getFlagRemovedVersion();
        FeatureFlag removedInFlag = recipe.getRemovedInFlag();
        if (flagRemovedVersion != null && removedInFlag != null) {
            lines.add(String.format("Removed in %s experiment, version %s", removedInFlag.getDisplayName(), flagRemovedVersion));
        }
        String notes = recipe.getNotes();
        if (notes != null) {
            lines.add(notes);
        }
        return lines.toString();
    }

    /**
     * Searches the database for all recipes with an item as the output
     *
     * @param item The item to search for
     * @return Null if the item cannot be found, otherwise a list of recipe names that may be empty
     */
    public static List<Recipe> searchOutput(String item) {
        if (item.contains("potion") || item.contains("tipped_arrow")) {
            return searchItemOutput(item);
        }
        return searchItemOutput(ItemRegistry.getNamespacedID(item));
    }

    /**
     * Searches the database for all recipes with an item as the output
     *
     * @param namespacedID The namespaced ID of the item to search with
     * @return A list of recipe names that may be empty
     */
    public static List<Recipe> searchItemOutput(String namespacedID) {
        // Loop through all recipes
        List<Recipe> recipesFound = new ArrayList<>();
        for (Recipe recipe : recipes.values()) {
            if (isIgnoredRecipe(recipe)) {
                continue;
            }
            String result = recipe.getResult().getItem();
            // Check if they match
            if (result.equals(namespacedID)) {
                recipesFound.add(recipe);
            }
        }
        // Wet sponge into bucket special case
        if (namespacedID.equals("minecraft:water_bucket")) {
            recipesFound.add(recipes.get("sponge"));
        }
        return recipesFound;
    }
    // Blasting, smoking, and campfire recipes are ignored to prevent cluttering with duplicates
    private static boolean isIgnoredRecipe(Recipe recipe) {
        if (recipe instanceof SmeltingRecipe) {
            SmeltingRecipe.Type type = ((SmeltingRecipe) recipe).getType();
            return type != SmeltingRecipe.Type.SMELTING;
        }
        return false;
    }

    /**
     * Searches the database for all recipes with an item as an input
     *
     * @param item The item to search for
     * @return Null if the item cannot be found, otherwise a list of recipe names that may be empty
     */
    public static List<Recipe> searchIngredient(String item) {
        if (item.contains("potion") || item.contains("tipped_arrow")) {
            return searchItemIngredient(item);
        }
        return searchItemIngredient(ItemRegistry.getNamespacedID(item));
    }

    /**
     * Searches the database for all recipes with an item as an input
     *
     * @param namespacedID The namespaced ID of the item to search with
     * @return Null if the item cannot be found, otherwise a list of recipe names that may be empty
     */
    private static List<Recipe> searchItemIngredient(String namespacedID) {
        return recipes.values().stream()
                .filter(r -> !isIgnoredRecipe(r) && getIngredientItems(r).contains(namespacedID))
                .collect(Collectors.toList());
    }

    /**
     * Finds the ingredients of a recipe
     * @param recipe The recipe key
     * @return A set of namespaced item ids that may be empty
     */
    public static List<String> getIngredientItems(Recipe recipe) {
        List<String> items = expandIngredients(recipe.getIngredients());
        if (recipe instanceof TransmuteRecipe && !((TransmuteRecipe) recipe).shouldIngredientsIncludeResult()) {
            items.remove(recipe.getResult().getItem());
        }
        return items;
    }
    @VisibleForTesting
    public static List<String> expandIngredients(List<Ingredient> ingredients) {
        // LinkedHashSet required to de-duplicate items in shapeless recipes while preserving consistent ordering
        LinkedHashSet<String> items = new LinkedHashSet<>();
        for (Ingredient ingredient : ingredients) {
            if (ingredient instanceof Ingredient.Item) {
                items.add(((Ingredient.Item) ingredient).getItem());
            } else {
                String tag = ((Ingredient.Tag) ingredient).getTag();
                items.addAll(getTag(tag));
            }
        }
        return new ArrayList<>(items);
    }

    /**
     * Recursively finds all blocks that belong to a tag
     * @param tag The namespaced ID of the tag
     * @return A list of blocks, with "minecraft:"
     */
    @VisibleForTesting
    public static List<String> getTag(String tag) {
        if (!tag.contains(":")) {
            tag = "minecraft:" + tag;
        }
        String[] parts = tag.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("invalid tag " + tag);
        }
        String namespace = parts[0];
        if (!namespace.equals("minecraft")) {
            throw new IllegalArgumentException("invalid tag namespace " + tag);
        }
        String tagName = parts[1];

        List<String> items = new ArrayList<>(); // remembers insertion order
        for (FeatureFlag flag : FeatureFlagRegistry.getFlags()) {
            String id = flag == null ? "vanilla" : flag.getId();
            JSONObject flagObj = tags.optJSONObject(id);
            if (flagObj == null) {
                continue;
            }
            JSONArray tagArr = flagObj.optJSONArray(tagName);
            if (tagArr == null) {
                continue;
            }
            for (int i = 0; i < tagArr.length(); i++) {
                String item = tagArr.getString(i);
                if (item.startsWith("#")) {
                    items.addAll(getTag(item.substring(1)));
                } else {
                    items.add(item);
                }
            }
        }
        return items;
    }

    /**
     * Returns the image filename for the recipe with extension
     * @param recipe The recipe key
     */
    private static String getImage(Recipe recipe) {
        return recipe.getKey() + (recipe.isAnimated() ? ".gif" : ".png");
    }

    public static int compareRecipes(Recipe recipe1, Recipe recipe2) {
        Version removedVer1 = recipe1.getRemovedVersion();
        Version removedVer2 = recipe2.getRemovedVersion();
        if (removedVer1 == null && removedVer2 != null) {
            return -1;
        }
        if (removedVer1 != null && removedVer2 == null) {
            return 1;
        }
        int removedVerCompare = Version.NULLS_FIRST_COMPARATOR.compare(removedVer1, removedVer2);
        if (removedVerCompare != 0) {
            return -removedVerCompare;
        }
        if (!recipe1.isReleased() || !recipe2.isReleased()) {
            int featureCompare = FeatureFlagRegistry.RELEASE_ORDER_COMPARATOR.compare(recipe1.getFeatureFlag(), recipe2.getFeatureFlag());
            if (featureCompare != 0) {
                return featureCompare;
            }
        }
        int verCompare = Version.NULLS_FIRST_COMPARATOR.compare(recipe1.getVersion(), recipe2.getVersion());
        if (verCompare != 0) {
            return verCompare;
        }
        int datapackVerCompare = Version.NULLS_FIRST_COMPARATOR.compare(recipe1.getDatapackVersion(), recipe2.getDatapackVersion());
        if (datapackVerCompare != 0) {
            return -datapackVerCompare;
        }
        int typeIdx1 = RECIPE_TYPE_ORDER.indexOf(recipe1.getClass());
        int typeIdx2 = RECIPE_TYPE_ORDER.indexOf(recipe2.getClass());
        int typeCompare = Integer.compare(typeIdx1, typeIdx2);
        if (typeCompare != 0) {
            return typeCompare;
        }
        if (recipe1 instanceof SmithingRecipe && recipe2 instanceof SmithingRecipe) {
            SmithingRecipe smithingRecipe1 = (SmithingRecipe) recipe1;
            SmithingRecipe smithingRecipe2 = (SmithingRecipe) recipe2;
            int smithingCompare = smithingRecipe1.getType().compareTo(smithingRecipe2.getType());
            if (smithingCompare != 0) {
                return smithingCompare;
            }
        }
        return recipe1.getKey().compareTo(recipe2.getKey());
    }

}
