package com.tisawesomeness.minecord.mc.item;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.ReactMenu;
import com.tisawesomeness.minecord.util.ArrayUtils;
import com.tisawesomeness.minecord.util.RequestUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Recipe {

    public static final String[] FEATURE_FLAGS = new String[] { "vanilla", "1.20", "bundle" };
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
            "1.20"
    };

    private static JSONObject recipes;
    private static JSONObject tags;

    /**
     * Initializes the recipe database by reading from file
     *
     * @param path The path to read from
     * @throws IOException on IO error
     */
    public static void init(String path) throws IOException {
        recipes = RequestUtils.loadJSON(path + "/recipes.json");
        System.out.println("Loaded " + recipes.length() + " recipes");
        tags = RequestUtils.loadJSON(path + "/tags.json");
    }

    /**
     * Creates an EmbedBuilder from a recipe
     *
     * @param recipe The name of the recipe
     * @param lang The language code to pull names from
     * @return An EmbedBuilder containing properties of the item
     */
    public static EmbedBuilder displayImg(String recipe, String lang) {
        EmbedBuilder eb = new EmbedBuilder();
        String item = Item.searchNoStats(getResult(recipe), lang);
        eb.setTitle(Item.getDistinctDisplayName(item, lang));
        eb.setImage(Config.getRecipeImageHost() + getImage(recipe));
        eb.setColor(Bot.color);
        eb.setDescription(getMetadata(recipe, lang));
        return eb;
    }
    private static String getMetadata(String recipe, String lang) {
        StringJoiner lines = new StringJoiner("\n");
        double xp = getXP(recipe);
        if (xp > 0) {
            lines.add(String.format("**XP:** %s", xp));
        }
        String version = getVersion(recipe);
        String removed = getRemovedVersion(recipe);
        if (version != null && removed != null) {
            lines.add(String.format("**Version:** %s - %s", version, getPreviousVersion(removed)));
        } else if (version != null) {
            lines.add(String.format("**Version:** %s", version));
        } else if (removed != null) {
            lines.add(String.format("**Removed In:** %s", removed));
        }
        String feature = getFeature(recipe);
        if (!feature.equals("vanilla")) {
            lines.add(String.format("**Feature Toggle:** %s", feature));
        }
        String flagRemovedVersion = getFlagRemovedVersion(recipe);
        String removedInFlag = getRemovedInFlag(recipe);
        if (flagRemovedVersion != null && removedInFlag != null) {
            lines.add(String.format("Removed in feature toggle %s, version %s", removedInFlag, flagRemovedVersion));
        }
        String notes = getNotes(recipe, lang);
        if (notes != null) {
            lines.add(notes);
        }
        return lines.toString();
    }
    private static String getPreviousVersion(String version) {
        return VERSIONS[ArrayUtils.indexOf(VERSIONS, version) - 1];
    }

    /**
     * Searches the database for all recipes with an item as the output
     * @param item The item to search for
     * @param lang The language code to pull names from
     * @return Null if the item cannot be found, otherwise a list of recipe names that may be empty
     */
    public static ArrayList<String> searchOutput(String item, String lang) {
        if (item.contains("potion") || item.contains("tipped_arrow")) {
            return searchItemOutput(item, lang);
        }
        return searchItemOutput(Item.getNamespacedID(item), lang);
    }

    /**
     * Searches the database for all recipes with an item as the output
     * @param namespacedID The namespaced ID of the item to search with
     * @param lang The language code to pull names from
     * @return A list of recipe names that may be empty
     */
    private static ArrayList<String> searchItemOutput(String namespacedID, String lang) {
        // Loop through all recipes
        ArrayList<String> recipesFound = new ArrayList<>();
        Iterator<String> iter = recipes.keys();
        while (iter.hasNext()) {
            String recipe = iter.next();
            String result = getResult(recipe);
            // Check if they match
            if (result != null && result.equals(namespacedID)) {
                String type = recipes.getJSONObject(recipe).getString("type");
                if (isValidType(type)) {
                    recipesFound.add(recipe);
                }
            }
        }
        // Wet sponge into bucket special case
        if (namespacedID.equals("minecraft:water_bucket")) {
            recipesFound.add("sponge_bucket");
        }
        return recipesFound;
    }

    private static final List<String> shapedTypes = Arrays.asList(
            "minecraft:crafting_shaped", "minecraft:crafting_special_tippedarrow", "minecraft:crafting_decorated_pot"
    );
    private static final List<String> shapelessTypes = Arrays.asList(
            "minecraft:crafting_shapeless",
            "minecraft:crafting_special_firework_star", "minecraft:crafting_special_firework_star_fade", "minecraft:crafting_special_firework_rocket",
            "minecraft:crafting_special_shulkerboxcoloring", "minecraft:crafting_special_suspiciousstew"
    );
    private static final List<String> smithingTypes = Arrays.asList(
            "minecraft:smithing", "minecraft:smithing_trim", "minecraft:smithing_transform"
    );
    private static final List<String> otherTypes = Arrays.asList(
            "minecraft:stonecutting", "minecraft.brewing"
    );
    /**
     * Checks if a recipe type is crafting
     * @param type The type string
     */
    private static boolean isCrafting(String type) {
        return shapedTypes.contains(type) || shapelessTypes.contains(type);
    }
    /**
     * Checks if a recipe type is smelting
     * @param type The type string
     */
    private static boolean isSmelting(String type) {
        return type.equals("minecraft:smelting") || type.equals("minecraft.smelting_special_sponge");
    }
    /**
     * Checks if a recipe type is smithing
     * @param type The type string
     */
    private static boolean isSmithing(String type) {
        return smithingTypes.contains(type);
    }
    /**
     * Checks if a recipe type is valid
     * @param type The type string
     */
    private static boolean isValidType(String type) {
        return isCrafting(type) || isSmelting(type) || isSmithing(type) || otherTypes.contains(type);
    }

    /**
     * Searches the database for all recipes with an item as an input
     * @param item The item to search for
     * @param lang The language code to pull names from
     * @return Null if the item cannot be found, otherwise a list of recipe names that may be empty
     */
    public static ArrayList<String> searchIngredient(String item, String lang) {
        if (item.contains("potion") || item.contains("tipped_arrow")) {
            return searchItemIngredient(item, lang);
        }
        return searchItemIngredient(Item.getNamespacedID(item), lang);
    }

    /**
     * Searches the database for all recipes with an item as an input
     * @param namespacedID The namespaced ID of the item to search with
     * @param lang The language code to pull names from
     * @return Null if the item cannot be found, otherwise a list of recipe names that may be empty
     */
    private static ArrayList<String> searchItemIngredient(String namespacedID, String lang) {
        // Loop through all recipes
        ArrayList<String> recipesFound = new ArrayList<>();
        Iterator<String> iter = recipes.keys();
        while (iter.hasNext()) {
            String recipe = iter.next();
            if (getIngredients(recipes.getJSONObject(recipe)).contains(namespacedID)) {
                recipesFound.add(recipe);
            }
        }
        return recipesFound;
    }

    /**
     * Finds the output of a recipe
     * @param recipe The recipe key
     * @return The namespaced ID of the output item or null if not found
     */
    private static String getResult(String recipe) {
        JSONObject recipeObj = recipes.getJSONObject(recipe);
        if (recipeObj.has("result")) {
            JSONObject resultObj = recipeObj.optJSONObject("result");
            if (resultObj == null) {
                return recipeObj.getString("result");
            }
            return resultObj.getString("item");
        }
        return null;
    }

    /**
     * Finds the ingredients of a recipe
     * @param recipe The recipe key
     * @return A set of namespaced item ids that may be empty
     */
    private static LinkedHashSet<String> getIngredients(JSONObject recipe) {
        LinkedHashSet<String> ingredients = new LinkedHashSet<>();
        String type = recipe.getString("type");
        // Shaped recipes
        if (shapedTypes.contains(type)) {
            JSONObject keyObj = recipe.getJSONObject("key");
            Iterator<String> keyIter = keyObj.keys();
            while (keyIter.hasNext()) {
                String key = keyIter.next();
                JSONObject symbolObj = keyObj.optJSONObject(key);
                // Ingredient has variants
                if (symbolObj == null) {
                    JSONArray symbolArr = keyObj.getJSONArray(key);
                    for (int i = 0; i < symbolArr.length(); i++) {
                        addItemsFromObj(ingredients, symbolArr.getJSONObject(i));
                    }
                // Ingredient is a single item or tag
                } else {
                    addItemsFromObj(ingredients, symbolObj);
                }
            }
            // Shapeless recipes
        } else if (shapelessTypes.contains(type)) {
            JSONArray keyArr = recipe.getJSONArray("ingredients");
            for (int i = 0; i < keyArr.length(); i++) {
                JSONObject symbolObj = keyArr.optJSONObject(i);
                // Ingredient has variants
                if (symbolObj == null) {
                    JSONArray symbolArr = keyArr.getJSONArray(i);
                    for (int j = 0; j < symbolArr.length(); j++) {
                        addItemsFromObj(ingredients, symbolArr.getJSONObject(j));
                    }
                // Ingredient is a single item or tag
                } else {
                    addItemsFromObj(ingredients, symbolObj);
                }
            }
            // Smelting recipes
        } else if (isSmelting(type)) {
            JSONObject ingredient = recipe.optJSONObject("ingredient");
            if (ingredient == null) {
                JSONArray variantsArr = recipe.getJSONArray("ingredient");
                for (int i = 0; i < variantsArr.length(); i++) {
                    addItemsFromObj(ingredients, variantsArr.getJSONObject(i));
                }
            } else {
                addItemsFromObj(ingredients, ingredient);
            }
            if (type.equals("minecraft.smelting_special_sponge")) {
                ingredients.add("minecraft:bucket");
            }
            // Stonecutting recipes
        } else if (type.equals("minecraft:stonecutting")) {
            addItemsFromObj(ingredients, recipe.getJSONObject("ingredient"));
            // Brewing recipes
        } else if (type.equals("minecraft.brewing")) {
            JSONObject reagent = recipe.optJSONObject("reagent");
            if (reagent == null) {
                JSONArray reagents = recipe.getJSONArray("reagent");
                for (int i = 0; i < reagents.length(); i++) {
                    addItemsFromObj(ingredients, reagents.getJSONObject(i));
                }
            } else {
                addItemsFromObj(ingredients, reagent);
            }
            JSONObject base = recipe.optJSONObject("base");
            if (base == null) {
                JSONArray bases = recipe.getJSONArray("base");
                for (int i = 0; i < bases.length(); i++) {
                    addItemsFromObj(ingredients, bases.getJSONObject(i));
                }
            } else {
                addItemsFromObj(ingredients, base);
            }
            // Smithing recipes
        } else if (isSmithing(type)) {
            addItemsFromObj(ingredients, recipe.getJSONObject("base"));
            JSONObject template = recipe.optJSONObject("template");
            if (template != null) {
                addItemsFromObj(ingredients, template);
            }
            addItemsFromObj(ingredients, recipe.getJSONObject("addition"));
        }
        return ingredients;
    }
    private static void addItemsFromObj(Collection<String> ingredients, JSONObject obj) {
        if (obj.has("item")) {
            ingredients.add(obj.getString("item"));
        } else if (obj.has("tag")) {
            ingredients.addAll(getTag(obj.getString("tag").substring(10)));
        } else {
            throw new IllegalArgumentException("Invalid ingredient: " + obj);
        }
    }

    /**
     * Recursively finds all blocks that belong to a tag
     * @param tag The name of the tag, without "minecraft:"
     * @return A list of blocks, with "minecraft:"
     */
    private static LinkedHashSet<String> getTag(String tag) {
        LinkedHashSet<String> items = new LinkedHashSet<>(); // remembers insertion order
        for (String flag : FEATURE_FLAGS) {
            JSONObject flagObj = tags.optJSONObject(flag);
            if (flagObj == null) {
                continue;
            }
            JSONArray tagArr = flagObj.optJSONArray(tag);
            if (tagArr == null) {
                continue;
            }
            for (int i = 0; i < tagArr.length(); i++) {
                String item = tagArr.getString(i);
                if (item.startsWith("#")) {
                    items.addAll(getTag(item.substring(11)));
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
    private static String getImage(String recipe) {
        JSONObject properties = recipes.getJSONObject(recipe).optJSONObject("properties");
        return recipe + (properties != null && properties.has("animated") ? ".gif" : ".png");
    }

    /**
     * Returns the version of the recipe
     * @param recipe The recipe key
     * @return A string version from 1.8 to 1.15, or null is 1.7 or lower
     */
    private static String getVersion(String recipe) {
        JSONObject properties = recipes.getJSONObject(recipe).optJSONObject("properties");
        return properties == null ? null : properties.optString("version", null);
    }

    private static String getFeature(String recipe) {
        JSONObject properties = recipes.getJSONObject(recipe).optJSONObject("properties");
        return properties == null ? "vanilla" : properties.optString("feature_flag", "vanilla");
    }

    /**
     * Returns the version when the recipe was removed
     * @param recipe The recipe key
     * @return A string version from 1.8 to 1.15, or null is 1.7 or lower
     */
    private static String getRemovedVersion(String recipe) {
        JSONObject properties = recipes.getJSONObject(recipe).optJSONObject("properties");
        return properties == null ? null : properties.optString("removed", null);
    }

    private static String getFlagRemovedVersion(String recipe) {
        JSONObject properties = recipes.getJSONObject(recipe).optJSONObject("properties");
        return properties == null ? null : properties.optString("flag_removed_version", null);
    }
    private static String getRemovedInFlag(String recipe) {
        JSONObject properties = recipes.getJSONObject(recipe).optJSONObject("properties");
        return properties == null ? null : properties.optString("removed_in_flag", null);
    }

    public static int compareRecipes(String recipe1, String recipe2) {
        double removedVer1 = getVersionNum(getRemovedVersion(recipe1));
        double removedVer2 = getVersionNum(getRemovedVersion(recipe2));
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
        int featureIdx1 = ArrayUtils.indexOf(FEATURE_FLAGS, getFeature(recipe1));
        int featureIdx2 = ArrayUtils.indexOf(FEATURE_FLAGS, getFeature(recipe2));
        int featureCompare = Integer.compare(featureIdx1, featureIdx2);
        if (featureCompare != 0) {
            return featureCompare;
        }
        int verCompare = Double.compare(getVersionNum(getVersion(recipe1)), getVersionNum(getVersion(recipe2)));
        if (verCompare != 0) {
            return verCompare;
        }
        return recipe1.compareTo(recipe2);
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

    /**
     * Returns the notes that should be displayed when a recipe is viewed
     * @param recipe The recipe key
     * @param lang The language code
     */
    private static String getNotes(String recipe, String lang) {
        JSONObject langObj = recipes.getJSONObject(recipe).optJSONObject("lang");
        return langObj == null ? null : langObj.getJSONObject(lang).optString("notes", null);
    }

    /**
     * Returns the amount of XP a smelting recipe grants
     * @param recipe The recipe key
     * @return A double where floor(xp) is given for each item smelted and xp % 1 is the chance for one addition xp to be given
     */
    private static double getXP(String recipe) {
        return recipes.getJSONObject(recipe).optDouble("experience");
    }

    /**
     * A class representing the reaction menu for displaying lists of recipes
     */
    public static class RecipeMenu extends ReactMenu {
        private List<String> recipeList;
        private String desc;
        private int startingIngredient = 0;
        /**
         * Creates a new recipe menu with a list of recipes
         * @param recipeList A non-empty list of string recipe keys
         * @param page The page to start on
         * @param lang The language code
         */
        public RecipeMenu(List<String> recipeList, int page, String lang) {
            super(page, lang);
            setRecipeList(recipeList);
        }

        private void setRecipeList(List<String> recipeList) {
            this.recipeList = recipeList.stream()
                    .sequential()
                    .sorted(Recipe::compareRecipes)
                    .collect(Collectors.toList());
        }

        public EmbedBuilder getContent(int page) {
            String recipe = recipeList.get(page);
            EmbedBuilder eb = displayImg(recipe, "en_US");
            if (eb.getDescriptionBuilder().length() > 0) {
                eb.getDescriptionBuilder().insert(0, desc + "\n");
            } else {
                eb.setDescription(desc);
            }
            return eb;
        }

        public LinkedHashMap<String, Runnable> createButtons(int page) {
            String recipe = recipeList.get(page);
            JSONObject recipeObj = recipes.getJSONObject(recipe);
            LinkedHashMap<String, Runnable> buttons = new LinkedHashMap<>();
            desc = ":track_previous: / :track_next: Go to beginning/end"
                    + "\n:rewind: / :fast_forward: Go back/forward 10"
                    + "\n:arrow_backward: / :arrow_forward: Go back/forward 1";
            // Go to first page
            buttons.put(Emote.FULL_BACK.getCodepoint(), () -> {
                if (page > 0) {
                    startingIngredient = 0;
                    setPage(0);
                }
            });
            // Go back 10
            buttons.put(Emote.SKIP_BACK.getCodepoint(), () -> {
                if (page >= 10) {
                    startingIngredient = 0;
                    setPage(page - 10);
                } else if (page > 0) {
                    startingIngredient = 0;
                    setPage(0);
                }
            });
            // Go back
            buttons.put(Emote.BACK.getCodepoint(), () -> {
                if (page > 0) {
                    startingIngredient = 0;
                    setPage(page - 1);
                }
            });
            // See what the output can craft
            ArrayList<String> outputMore = searchIngredient(Item.searchNoStats(getResult(recipe), getLang()), getLang());
            boolean hasOutput = outputMore != null && outputMore.size() > 0;
            buttons.put(Emote.UP.getCodepoint(), () -> {
                if (hasOutput) {
                    setRecipeList(outputMore);
                    startingIngredient = 0;
                    setPage(0);
                }
            });
            if (hasOutput) {
                desc += String.format("\n%s See what the output of this recipe can make", Emote.UP.getText());
            }
            // Go forward
            buttons.put(Emote.FORWARD.getCodepoint(), () -> {
                if (page < getLength() - 1) {
                    startingIngredient = 0;
                    setPage(page + 1);
                }
            });
            // Go forward 10
            buttons.put(Emote.SKIP_FORWARD.getCodepoint(), () -> {
                if (page <= getLength() - 11) {
                    startingIngredient = 0;
                    setPage(page + 10);
                } else if (page < getLength() - 1) {
                    startingIngredient = 0;
                    setPage(getLength() - 1);
                }
            });
            // Go to last page
            buttons.put(Emote.FULL_FORWARD.getCodepoint(), () -> {
                if (page < getLength() - 1) {
                    startingIngredient = 0;
                    setPage(getLength() - 1);
                }
            });
            // Craft table
            String item;
            switch (recipes.getJSONObject(recipe).getString("type")) {
                case "minecraft:smelting":
                case "minecraft.smelting_special_sponge":
                    item = "minecraft.furnace"; break;
                case "minecraft:stonecutting":
                    item = "minecraft.stonecutter"; break;
                case "minecraft.brewing":
                    item = "minecraft.brewing_stand"; break;
                case "minecraft:smithing":
                case "minecraft:smithing_trim":
                case "minecraft:smithing_transform":
                    item = "minecraft.smithing_table"; break;
                default:
                    item = "minecraft.crafting_table";
            }
            String table = Item.getNamespacedID(item).substring(10);
            boolean needsTable = !recipe.equals(table);
            if (needsTable) {
                desc += String.format("\n%s %s", Emote.T.getText(), Item.getMenuDisplayNameWithFeature(item, getLang()));
            }
            buttons.put(Emote.T.getCodepoint(), () -> {
                if (needsTable) {
                    recipeList = Collections.singletonList(table);
                    startingIngredient = 0;
                    setPage(0);
                }
            });
            // Reserved ingredients
            String type = recipeObj.getString("type");
            int ingredientButtonCount = 0; // 0 for no buttons, 9 for buttons 1-9, and 10 for buttons 1-9 plus More...
            if (type.equals("minecraft:smelting")) {
                // Blast furnace, Smoker, Campfire
                boolean isSmoking = recipes.has(recipe + "_from_smoking");
                boolean isBlasting = recipes.has(recipe.replace("from_smelting", "from_blasting"));
                if (isSmoking) {
                    desc += String.format("\n%s %s\n%s %s",
                            Emote.N1.getText(), Item.getMenuDisplayNameWithFeature("minecraft.smoker", getLang()),
                            Emote.N2.getText(), Item.getMenuDisplayNameWithFeature("minecraft.campfire", getLang()));
                    ingredientButtonCount = 2;
                } else if (isBlasting) {
                    desc += String.format("\n%s %s", Emote.N1.getText(), Item.getMenuDisplayNameWithFeature("minecraft.blast_furnace", getLang()));
                    ingredientButtonCount = 1;
                }
                buttons.put(Emote.N1.getCodepoint(), () -> {
                    if (isSmoking || isBlasting) {
                        if (isSmoking) {
                            recipeList = Collections.singletonList(Item.getNamespacedID("minecraft.smoker").substring(10));
                        } else {
                            recipeList = Collections.singletonList(Item.getNamespacedID("minecraft.blast_furnace").substring(10));
                        }
                        startingIngredient = 0;
                        setPage(0);
                    }
                });
                buttons.put(Emote.N2.getCodepoint(), () -> {
                    if (isSmoking) {
                        recipeList = Collections.singletonList(Item.getNamespacedID("minecraft.campfire").substring(10));
                        startingIngredient = 0;
                        setPage(0);
                    }
                });
            } else if (type.equals("minecraft.brewing")) {
                // Blaze powder
                buttons.put(Emote.N1.getCodepoint(), () -> {
                    recipeList = Collections.singletonList(Item.getNamespacedID("minecraft.blaze_powder").substring(10));
                    startingIngredient = 0;
                    setPage(0);
                });
                ingredientButtonCount = 1;
            }
            // Find how to craft each ingredient
            if (isCrafting(type) || isSmelting(type) || type.equals("minecraft.brewing") || isSmithing(type)) {
                LinkedHashSet<String> ingredientsSet = getIngredients(recipeObj);
                if (type.equals("minecraft.brewing")) {
                    if (ingredientsSet.contains("minecraft:blaze_powder")) {
                        ingredientsSet.remove("minecraft:blaze_powder");
                        desc += String.format("\n%s Blaze Powder", Emote.N1.getText());
                    } else {
                        desc += String.format("\n%s Blaze Powder (required for 1.9+)", Emote.N1.getText());
                    }
                    if (ingredientsSet.contains("minecraft.potion.effect.water")) {
                        ingredientsSet.add("minecraft:glass_bottle");
                    }
                }
                String[] ingredients = new String[ingredientsSet.size()];
                ingredientsSet.toArray(ingredients);
                int i = startingIngredient;
                while (i < ingredients.length && ingredientButtonCount <= 9) {
                    String ingredientItem = Item.searchNoStats(ingredients[i], getLang());
                    String toSearch = ingredientItem;
                    if (!ingredientItem.contains("potion") && !ingredientItem.contains("tipped_arrow")) {
                        toSearch = Item.getNamespacedID(ingredientItem);
                    }
                    ArrayList<String> ingredientMore = searchItemOutput(toSearch, getLang());
                    if (ingredientMore.size() > 0) {
                        if (ingredientButtonCount == 9) {
                            ingredientButtonCount++;
                            break;
                        }
                        Emote emote = Emote.valueOf(ingredientButtonCount + 1);
                        buttons.put(emote.getCodepoint(), () -> {
                            setRecipeList(ingredientMore);
                            startingIngredient = 0;
                            setPage(0);
                        });
                        desc += String.format("\n%s %s", emote.getText(), Item.getMenuDisplayNameWithFeature(ingredientItem, getLang()));
                        ingredientButtonCount++;
                    }
                    i++;
                }
                boolean hasMore = ingredientButtonCount > 9 || startingIngredient > 0;
                while (ingredientButtonCount < 9) {
                    Emote emote = Emote.valueOf(ingredientButtonCount + 1);
                    buttons.put(emote.getCodepoint(), null);
                    ingredientButtonCount++;
                }
                // Cycle through ingredients if there's more than 9
                if (hasMore) {
                    desc += String.format("\n%s More...", Emote.MORE.getText());
                }
                int iFinal = i;
                if (hasMore) {
                    buttons.put(Emote.MORE.getCodepoint(), () -> {
                        startingIngredient = iFinal;
                        if (startingIngredient >= ingredients.length) {
                            startingIngredient = 0;
                        }
                        setPage(page);
                    });
                } else {
                    buttons.put(Emote.MORE.getCodepoint(), null);
                }
            } else if (type.equals("minecraft:stonecutting")) {
                String ingredient = getIngredients(recipeObj).toArray(new String[0])[0];
                ArrayList<String> output = searchItemOutput(ingredient, getLang());
                desc += String.format("\n%s %s", Emote.N1.getText(), Item.getMenuDisplayNameWithFeature(Item.searchNoStats(ingredient, getLang()), getLang()));
                buttons.put(Emote.N1.getCodepoint(), () -> {
                    setRecipeList(output);
                    startingIngredient = 0;
                    setPage(0);
                });
                for (int i = 1; i < 9; i++) {
                    buttons.put(Emote.valueOf(i + 1).getCodepoint(), null);
                }
                buttons.put(Emote.MORE.getCodepoint(), null);
            }
            return buttons;
        }

        public int getLength() {
            return recipeList.size();
        }

    }

}
