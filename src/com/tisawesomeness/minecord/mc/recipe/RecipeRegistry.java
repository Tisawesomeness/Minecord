package com.tisawesomeness.minecord.mc.recipe;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.mc.FeatureFlag;
import com.tisawesomeness.minecord.mc.item.ItemRegistry;
import com.tisawesomeness.minecord.util.ArrayUtils;
import com.tisawesomeness.minecord.util.RequestUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.collections4.OrderedMap;
import org.apache.commons.collections4.map.LinkedMap;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class RecipeRegistry {

    private static final String[] VERSIONS = new String[] {
            "1.7.10",
            "1.8", "1.8.1", "1.8.2", "1.8.3", "1.8.4", "1.8.5", "1.8.6", "1.8.7", "1.8.8", "1.8.9",
            "1.9", "1.9.1", "1.9.2", "1.9.3", "1.9.4",
            "1.10", "1.10.1", "1.10.2",
            "1.11", "1.11.1", "1.11.2",
            "1.12", "1.12.1", "1.12.2",
            "1.13", "1.13.1", "1.13.2",
            "1.14", "1.14.1", "1.14.2", "1.14.3", "1.14.4",
            "1.15", "1.15.1", "1.15.2",
            "1.16", "1.16.1", "1.16.2", "1.16.3", "1.16.4", "1.16.5",
            "1.17", "1.17.1",
            "1.18", "1.18.1", "1.18.2",
            "1.19", "1.19.1", "1.19.2", "1.19.3", "1.19.4",
            "1.20", "1.20.1", "1.20.2", "1.20.3", "1.20.4", "1.20.5", "1.20.6",
            "1.21", "1.21.1", "1.21.2", "1.21.3", "1.21.4"
    };
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
        System.out.println(img);
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
        String version = recipe.getVersion();
        String removed = recipe.getRemovedVersion();
        FeatureFlag feature = recipe.getFeatureFlag();
        if (version != null && removed != null) {
            if (feature != null) {
                lines.add(String.format("**Version:** %s (%s experiment) - %s", version, feature.getDisplayName(), getPreviousVersion(removed)));
                feature.getReleaseVersion().ifPresent(releaseVersion -> {
                    if (compareVersions(releaseVersion, removed) < 0) {
                        lines.add(String.format("**Released:** %s", releaseVersion));
                    }
                });
            } else {
                lines.add(String.format("**Version:** %s - %s", version, getPreviousVersion(removed)));
            }
        } else if (version != null) {
            if (feature != null) {
                lines.add(String.format("**Version:** %s (%s experiment)", version, feature.getDisplayName()));
                feature.getReleaseVersion().ifPresent(releaseVersion -> {
                    lines.add(String.format("**Released:** %s", releaseVersion));
                });
            } else {
                lines.add(String.format("**Version:** %s", version));
            }
        } else if (removed != null) {
            lines.add(String.format("**Removed In:** %s", removed));
        }
        String flagRemovedVersion = recipe.getFlagRemovedVersion();
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
    private static String getPreviousVersion(String version) {
        int idx = ArrayUtils.indexOf(VERSIONS, version);
        if (idx <= 0) {
            return "(none)";
        }
        return VERSIONS[idx - 1];
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
                .filter(r -> getIngredientItems(r).contains(namespacedID))
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
        List<String> items = new ArrayList<>();
        for (Ingredient ingredient : ingredients) {
            if (ingredient instanceof Ingredient.Item) {
                items.add(((Ingredient.Item) ingredient).getItem());
            } else {
                String tag = ((Ingredient.Tag) ingredient).getTag();
                items.addAll(getTag(tag));
            }
        }
        return items;
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
        for (FeatureFlag flag : FeatureFlag.RELEASE_ORDER) {
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
        double removedVer1 = getVersionNum(recipe1.getRemovedVersion());
        double removedVer2 = getVersionNum(recipe2.getRemovedVersion());
        if (removedVer1 == 0 && removedVer2 != 0) {
            return -1;
        }
        if (removedVer1 != 0 && removedVer2 == 0) {
            return 1;
        }
        int removedVerCompare = Double.compare(removedVer1, removedVer2);
        if (removedVerCompare != 0) {
            return removedVerCompare;
        }
        if (!recipe1.isReleased() || !recipe2.isReleased()) {
            int featureCompare = FeatureFlag.RELEASE_ORDER_COMPARATOR.compare(recipe1.getFeatureFlag(), recipe2.getFeatureFlag());
            if (featureCompare != 0) {
                return featureCompare;
            }
        }
        int verCompare = compareVersions(recipe1.getVersion(), recipe2.getVersion());
        if (verCompare != 0) {
            return verCompare;
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
    private static int compareVersions(String version1, String version2) {
        double ver1 = getVersionNum(version1);
        double ver2 = getVersionNum(version2);
        return Double.compare(ver1, ver2);
    }
    private static double getVersionNum(String version) {
        if (version == null) {
            return 0;
        }
        if (version.equals("1.17 Datapack")) {
            return 99;
        }
        return Double.parseDouble(version.substring(2));
    }

}
