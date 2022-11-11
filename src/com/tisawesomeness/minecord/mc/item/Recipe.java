package com.tisawesomeness.minecord.mc.item;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.ReactMenu;
import com.tisawesomeness.minecord.util.RequestUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Recipe {

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
        eb.setTitle(Item.getDisplayName(item, lang));
        eb.setImage(Config.getRecipeImageHost() + getImage(recipe));
        eb.setColor(Bot.color);
        return eb;
    }

    /**
     * Searches the database for all recipes with an item as the output
     * @param str The string to search with
     * @param lang The language code to pull names from
     * @return Null if the item cannot be found, otherwise a list of recipe names that may be empty
     */
    public static ArrayList<String> searchOutput(String str, String lang) {
        // Search for an item
        String item = Item.search(str, lang);
        if (item == null) {
            return null;
        }
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
            "minecraft:crafting_shaped", "minecraft:crafting_special_tippedarrow"
    );
    private static final List<String> shapelessTypes = Arrays.asList(
            "minecraft:crafting_shapeless",
            "minecraft:crafting_special_firework_star", "minecraft:crafting_special_firework_star_fade", "minecraft:crafting_special_firework_rocket",
            "minecraft:crafting_special_shulkerboxcoloring", "minecraft:crafting_special_suspiciousstew"
    );
    private static final List<String> otherTypes = Arrays.asList(
            "minecraft:stonecutting", "minecraft.brewing", "minecraft:smithing"
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
     * Checks if a recipe type is valid
     * @param type The type string
     */
    private static boolean isValidType(String type) {
        return isCrafting(type) || isSmelting(type) || otherTypes.contains(type);
    }

    /**
     * Searches the database for all recipes with an item as an input
     * @param str The string to search with
     * @param lang The language code to pull names from
     * @return Null if the item cannot be found, otherwise a list of recipe names that may be empty
     */
    public static ArrayList<String> searchIngredient(String str, String lang) {
        // Search for an item
        String item = Item.search(str, lang);
        if (item == null) {
            return null;
        }
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
                        ingredients.add(symbolArr.getJSONObject(i).getString("item"));
                    }
                    // Ingredient is a single item
                } else if (symbolObj.has("item")) {
                    ingredients.add(symbolObj.getString("item"));
                    // Ingredient is a tag
                } else {
                    ingredients.addAll(getTag(symbolObj.getString("tag").substring(10)));
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
                        ingredients.add(symbolArr.getJSONObject(j).getString("item"));
                    }
                    // Ingredient is a single item
                } else if (symbolObj.has("item")) {
                    ingredients.add(symbolObj.getString("item"));
                    // Ingredient is a tag
                } else {
                    ingredients.addAll(getTag(symbolObj.getString("tag").substring(10)));
                }
            }
            // Smelting recipes
        } else if (isSmelting(type)) {
            JSONObject ingredient = recipe.optJSONObject("ingredient");
            if (ingredient == null) {
                JSONArray variantsArr = recipe.getJSONArray("ingredient");
                for (int i = 0; i < variantsArr.length(); i++) {
                    ingredients.add(variantsArr.getJSONObject(i).getString("item"));
                }
            } else if (ingredient.has("item")) {
                ingredients.add(ingredient.getString("item"));
            } else {
                ingredients.addAll(getTag(ingredient.getString("tag").substring(10)));
            }
            if (type.equals("minecraft.smelting_special_sponge")) {
                ingredients.add("minecraft:bucket");
            }
            // Stonecutting recipes
        } else if (type.equals("minecraft:stonecutting")) {
            ingredients.add(recipe.getJSONObject("ingredient").getString("item"));
            // Brewing recipes
        } else if (type.equals("minecraft.brewing")) {
            JSONObject reagent = recipe.optJSONObject("reagent");
            if (reagent == null) {
                JSONArray reagents = recipe.getJSONArray("reagent");
                for (int i = 0; i < reagents.length(); i++) {
                    ingredients.add(reagents.getJSONObject(i).getString("item"));
                }
            } else {
                ingredients.add(reagent.getString("item"));
            }
            JSONObject base = recipe.optJSONObject("base");
            if (base == null) {
                JSONArray bases = recipe.getJSONArray("base");
                for (int i = 0; i < bases.length(); i++) {
                    ingredients.add(bases.getJSONObject(i).getString("item"));
                }
            } else {
                ingredients.add(base.getString("item"));
            }
            // Smithing recipes
        } else if (type.equals("minecraft:smithing")) {
            ingredients.add(recipe.getJSONObject("base").getString("item"));
            ingredients.add(recipe.getJSONObject("addition").getString("item"));
        }
        return ingredients;
    }

    /**
     * Recursively finds all blocks that belong to a tag
     * @param tag The name of the tag, without "minecraft:"
     * @return A list of blocks, with "minecraft:"
     */
    private static ArrayList<String> getTag(String tag) {
        ArrayList<String> items = new ArrayList<>();
        JSONArray tagArr = tags.getJSONArray(tag);
        for (int i = 0; i < tagArr.length(); i++) {
            String item = tagArr.getString(i);
            if (item.startsWith("#")) {
                items.addAll(getTag(item.substring(11)));
            } else {
                items.add(item);
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

    /**
     * Returns the version when the recipe was removed
     * @param recipe The recipe key
     * @return A string version from 1.8 to 1.15, or null is 1.7 or lower
     */
    private static String getRemovedVersion(String recipe) {
        JSONObject properties = recipes.getJSONObject(recipe).optJSONObject("properties");
        return properties == null ? null : properties.optString("removed", null);
    }

    private static int compareRecipes(String recipe1, String recipe2) {
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
            double xp = getXP(recipe);
            if (xp > 0) {
                desc += String.format("\n**XP:** %s", xp);
            }
            String version = getVersion(recipe);
            if (version != null) {
                desc += String.format("\n**Version:** %s", version);
            }
            String removed = getRemovedVersion(recipe);
            if (removed != null) {
                if (version == null) {
                    desc += String.format("\n**Removed In:** %s", removed);
                } else {
                    desc += String.format(" **Removed In:** %s", removed);
                }
            }
            String notes = getNotes(recipe, getLang());
            if (notes != null) {
                desc += "\n" + notes;
            }
            eb.setDescription(desc);
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
            ArrayList<String> outputMore = searchIngredient(getResult(recipe), getLang());
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
                    item = "minecraft.smithing_table"; break;
                default:
                    item = "minecraft.crafting_table";
            }
            String table = Item.getNamespacedID(item).substring(10);
            boolean needsTable = !recipe.equals(table);
            if (needsTable) {
                desc += String.format("\n%s %s", Emote.T.getText(), Item.getDisplayName(item, getLang()));
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
            int c = 0;
            if (type.equals("minecraft:smelting")) {
                // Blast furnace, Smoker, Campfire
                boolean isSmoking = recipes.has(recipe + "_from_smoking");
                boolean isBlasting = recipes.has(recipe.replace("from_smelting", "from_blasting"));
                if (isSmoking) {
                    desc += String.format("\n%s %s\n%s %s",
                            Emote.N1.getText(), Item.getDisplayName("minecraft.smoker", getLang()),
                            Emote.N2.getText(), Item.getDisplayName("minecraft.campfire", getLang()));
                    c = 2;
                } else if (isBlasting) {
                    desc += String.format("\n%s %s", Emote.N1.getText(), Item.getDisplayName("minecraft.blast_furnace", getLang()));
                    c = 1;
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
                c = 1;
            }
            // Find how to craft each ingredient
            if (isCrafting(type) || isSmelting(type) || type.equals("minecraft.brewing") || type.equals("minecraft:smithing")) {
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
                while (i < ingredients.length && c < 9) {
                    String ingredientItem = Item.searchNoStats(ingredients[i], getLang());
                    String toSearch = ingredientItem;
                    if (!ingredientItem.contains("potion") && !ingredientItem.contains("tipped_arrow")) {
                        toSearch = Item.getNamespacedID(ingredientItem);
                    }
                    ArrayList<String> ingredientMore = searchItemOutput(toSearch, getLang());
                    if (ingredientMore.size() > 0) {
                        Emote emote = Emote.valueOf(c + 1);
                        buttons.put(emote.getCodepoint(), () -> {
                            setRecipeList(ingredientMore);
                            startingIngredient = 0;
                            setPage(0);
                        });
                        desc += String.format("\n%s %s", emote.getText(), Item.getDisplayName(ingredientItem, getLang()));
                        c++;
                    }
                    i++;
                }
                boolean hasMore = ingredients.length > 9 || startingIngredient > 0;
                while (c < 9) {
                    Emote emote = Emote.valueOf(c + 1);
                    buttons.put(emote.getCodepoint(), null);
                    c++;
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
                desc += String.format("\n%s %s", Emote.N1.getText(), Item.getDisplayName(Item.searchNoStats(ingredient, getLang()), getLang()));
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
