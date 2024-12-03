package com.tisawesomeness.minecord.mc.recipe;

import com.tisawesomeness.minecord.Emote;
import com.tisawesomeness.minecord.ReactMenu;
import com.tisawesomeness.minecord.mc.item.ItemRegistry;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A class representing the reaction menu for displaying lists of recipes
 */
public class RecipeMenu extends ReactMenu {
    private List<Recipe> recipeList;
    private String desc;
    private int startingIngredient = 0;
    /**
     * Creates a new recipe menu with a list of recipes
     *
     * @param recipeList A non-empty list of string recipe keys
     * @param page       The page to start on
     */
    public RecipeMenu(List<Recipe> recipeList, int page) {
        super(page);
        setRecipeList(recipeList);
    }

    private void setRecipeList(List<Recipe> recipeList) {
        this.recipeList = recipeList.stream()
                .sorted(RecipeRegistry::compareRecipes)
                .collect(Collectors.toList());
    }

    public EmbedBuilder getContent(int page) {
        Recipe recipe = recipeList.get(page);
        EmbedBuilder eb = RecipeRegistry.displayImg(recipe);
        if (eb.getDescriptionBuilder().length() > 0) {
            eb.getDescriptionBuilder().insert(0, desc + "\n");
        } else {
            eb.setDescription(desc);
        }
        return eb;
    }

    public LinkedHashMap<String, Runnable> createButtons(int page) {
        Recipe recipe = recipeList.get(page);
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
        String result = recipe.getResult().getItem();
        List<Recipe> outputMore = RecipeRegistry.searchIngredient(ItemRegistry.searchNoStats(result));
        boolean hasOutput = !outputMore.isEmpty();
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
        String tableItem = recipe.getTableItem().replace(':', '.');
        String table = ItemRegistry.getNamespacedID(tableItem).substring(10);
        boolean needsTable = !recipe.getKey().equals(table);
        if (needsTable) {
            desc += String.format("\n%s %s", Emote.T.getText(), ItemRegistry.getMenuDisplayNameWithFeature(tableItem));
        }
        buttons.put(Emote.T.getCodepoint(), () -> {
            if (needsTable) {
                recipeList = Collections.singletonList(RecipeRegistry.get(table));
                startingIngredient = 0;
                setPage(0);
            }
        });
        // Reserved ingredients
        int ingredientButtonCount = 0; // 0 for no buttons, 9 for buttons 1-9, and 10 for buttons 1-9 plus More...
        if (recipe instanceof SmeltingRecipe) {
            // Blast furnace, Smoker, Campfire
            boolean isSmoking = RecipeRegistry.contains(recipe + "_from_smoking");
            boolean isBlasting = recipe.getKey().contains("from_smelting") &&
                    RecipeRegistry.contains(recipe.getKey().replace("from_smelting", "from_blasting"));
            if (isSmoking) {
                desc += String.format("\n%s %s\n%s %s",
                        Emote.N1.getText(), ItemRegistry.getMenuDisplayNameWithFeature("minecraft.smoker"),
                        Emote.N2.getText(), ItemRegistry.getMenuDisplayNameWithFeature("minecraft.campfire"));
                ingredientButtonCount = 2;
            } else if (isBlasting) {
                desc += String.format("\n%s %s", Emote.N1.getText(), ItemRegistry.getMenuDisplayNameWithFeature("minecraft.blast_furnace"));
                ingredientButtonCount = 1;
            }
            buttons.put(Emote.N1.getCodepoint(), () -> {
                if (isSmoking || isBlasting) {
                    if (isSmoking) {
                        recipeList = RecipeRegistry.searchOutput("minecraft.smoker");
                    } else {
                        recipeList = RecipeRegistry.searchOutput("minecraft.blast_furnace");
                    }
                    startingIngredient = 0;
                    setPage(0);
                }
            });
            buttons.put(Emote.N2.getCodepoint(), () -> {
                if (isSmoking) {
                    recipeList = RecipeRegistry.searchOutput("minecraft.campfire");
                    startingIngredient = 0;
                    setPage(0);
                }
            });
        } else if (recipe instanceof BrewingRecipe) {
            // Blaze powder
            buttons.put(Emote.N1.getCodepoint(), () -> {
                recipeList = RecipeRegistry.searchOutput("minecraft.blaze_powder");
                startingIngredient = 0;
                setPage(0);
            });
            ingredientButtonCount = 1;
        }
        // Find how to craft each ingredient
        if (!(recipe instanceof StonecuttingRecipe)) {
            List<String> ingredientsList = RecipeRegistry.getIngredientItems(recipe);
            ingredientsList.remove(result);
            if (recipe instanceof BrewingRecipe) {
                if (ingredientsList.contains("minecraft:blaze_powder")) {
                    ingredientsList.remove("minecraft:blaze_powder");
                    desc += String.format("\n%s Blaze Powder", Emote.N1.getText());
                } else {
                    String version = recipe.getVersion();
                    if (version == null || version.equals("1.8")) {
                        desc += String.format("\n%s Blaze Powder (required for 1.9+)", Emote.N1.getText());
                    } else {
                        desc += String.format("\n%s Blaze Powder", Emote.N1.getText());
                    }
                }
                if (ingredientsList.contains("minecraft.potion.effect.water")) {
                    ingredientsList.add("minecraft:glass_bottle");
                }
            }
            String[] ingredients = new String[ingredientsList.size()];
            ingredientsList.toArray(ingredients);
            int i = startingIngredient;
            while (i < ingredients.length && ingredientButtonCount <= 9) {
                String ingredientItem = ItemRegistry.searchNoStats(ingredients[i]);
                String toSearch = ingredientItem;
                if (!ingredientItem.contains("potion") && !ingredientItem.contains("tipped_arrow")) {
                    toSearch = ItemRegistry.getNamespacedID(ingredientItem);
                }
                List<Recipe> ingredientMore = RecipeRegistry.searchItemOutput(toSearch);
                if (!ingredientMore.isEmpty()) {
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
                    desc += String.format("\n%s %s", emote.getText(), ItemRegistry.getMenuDisplayNameWithFeature(ingredientItem));
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
        } else {
            String ingredient = RecipeRegistry.getIngredientItems(recipe).toArray(new String[0])[0];
            List<Recipe> output = RecipeRegistry.searchItemOutput(ingredient);
            if (!output.isEmpty()) {
                desc += String.format("\n%s %s", Emote.N1.getText(), ItemRegistry.getMenuDisplayNameWithFeature(ItemRegistry.searchNoStats(ingredient)));
                buttons.put(Emote.N1.getCodepoint(), () -> {
                    setRecipeList(output);
                    startingIngredient = 0;
                    setPage(0);
                });
            } else {
                buttons.put(Emote.N1.getCodepoint(), null);
            }
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
