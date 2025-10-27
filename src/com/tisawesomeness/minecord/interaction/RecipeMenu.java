package com.tisawesomeness.minecord.interaction;

import com.tisawesomeness.minecord.mc.item.ItemRegistry;
import com.tisawesomeness.minecord.mc.recipe.*;
import com.tisawesomeness.minecord.util.MathUtils;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.dv8tion.jda.api.modals.Modal;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;

public class RecipeMenu implements UpdatingMessage {

    private static final String BACK_ID = "recipemenu.back";
    private static final String NEXT_ID = "recipemenu.next";
    private static final String JUMP_ID = "recipemenu.jump";
    private static final String OUTPUT_ID = "recipemenu.output";
    private static final String INGREDIENT_ID = "recipemenu.ingredient";

    private static final String MORE_INDICATOR = "recipemenu.more";

    private static final String JUMP_MODAL_ID = "recipemenu.jump_modal";
    private static final String PAGE_SELECT_ID = "recipemenu.page_select";

    private List<Recipe> recipes;
    private int page;
    private int ingredientsPage;

    public RecipeMenu(List<Recipe> recipes, int page) {
        if (recipes.isEmpty()) {
            throw new IllegalArgumentException("Recipes cannot be empty");
        }
        if (page < 0 || page >= recipes.size()) {
            throw new IllegalArgumentException("Page must be between 0 and " + (recipes.size() - 1));
        }
        setRecipes(recipes);
    }

    private void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes.stream()
                .sorted(RecipeRegistry::compareRecipes)
                .collect(Collectors.toList());
        setPage(0);
    }
    private void setPage(int page) {
        this.page = page;
        ingredientsPage = 0;
    }

    @Override
    public boolean onInteract(GenericComponentInteractionCreateEvent e) {
        switch (e.getComponentId()) {
            case BACK_ID:
                if (page > 0) {
                    setPage(page - 1);
                    return true;
                }
                return false;
            case NEXT_ID:
                if (page < recipes.size() - 1) {
                    setPage(page + 1);
                    return true;
                }
                return false;
            case JUMP_ID:
                if (recipes.size() > 1) {
                    e.replyModal(jumpModal()).queue();
                }
                return false;
            case OUTPUT_ID:
                List<Recipe> output = craftableFromOutput();
                if (!output.isEmpty()) {
                    setRecipes(output);
                    return true;
                }
                return false;
            case INGREDIENT_ID:
                return handleIngredient(e);
        }
        return false;
    }

    private boolean handleIngredient(GenericComponentInteractionCreateEvent genericEvent) {
        if (!(genericEvent instanceof StringSelectInteractionEvent)) {
            return false;
        }
        StringSelectInteractionEvent e = (StringSelectInteractionEvent) genericEvent;
        List<String> values = e.getValues();
        if (values.size() != 1) {
            return false;
        }
        String ingredientMinecordId = values.get(0);
        if (ingredientMinecordId.equals(MORE_INDICATOR)) {
            ingredientsPage++;
            if (ingredientsPage >= buildIngredientPages().size()) {
                ingredientsPage = 0;
            }
            return true;
        }
        List<Recipe> recipes = craftableFromIngredient(ingredientMinecordId);
        if (recipes.isEmpty()) {
            return false;
        }
        setRecipes(recipes);
        return true;
    }

    private Modal jumpModal() {
        String label = String.format("Page Number (1-%d)", recipes.size());
        return Modal.create(JUMP_MODAL_ID, "Go to page")
                .addComponents(Label.of(label,
                        TextInput.create(PAGE_SELECT_ID, TextInputStyle.SHORT)
                                .setMaxLength(MathUtils.stringLength(recipes.size()))
                                .build())
                )
                .build();
    }

    @Override
    public boolean onSubmit(ModalInteractionEvent e) {
        if (e.getModalId().equals(JUMP_MODAL_ID)) {
            return handleJump(e);
        }
        return false;
    }

    private boolean handleJump(ModalInteractionEvent e) {
        ModalMapping input = e.getValue(PAGE_SELECT_ID);
        if (input == null) {
            return false;
        }
        OptionalInt pageOpt = MathUtils.safeParseInt(input.getAsString());
        if (!pageOpt.isPresent()) {
            e.reply("Invalid page number").setEphemeral(true).queue();
            return false;
        }
        int page = pageOpt.getAsInt() - 1;
        if (page < 0 || page >= recipes.size()) {
            e.reply(String.format("Page number must be between 1 and %d", recipes.size())).setEphemeral(true).queue();
            return false;
        }
        setPage(page);
        return true;
    }

    @Override
    public MessageCreateData render(boolean supportsInteractions) {
        String pageText = String.format("Page %d/%d", page + 1, recipes.size());
        if (supportsInteractions) {
            return new MessageCreateBuilder()
                    .setEmbeds(RecipeRegistry.displayImg(currentRecipe()).build())
                    .addComponents(ActionRow.of(
                            Button.primary(BACK_ID, "Back").withDisabled(page == 0),
                            Button.secondary(JUMP_ID, pageText).withDisabled(recipes.size() < 2),
                            Button.primary(NEXT_ID, "Next").withDisabled(page == recipes.size() - 1),
                            Button.primary(OUTPUT_ID, "Output").withDisabled(craftableFromOutput().isEmpty())
                    ))
                    .addComponents(ActionRow.of(ingredientSelect()))
                    .build();
        } else {
            return new MessageCreateBuilder()
                    .setEmbeds(RecipeRegistry.displayImg(currentRecipe())
                            .setFooter(pageText, null)
                            .build())
                    .build();
        }
    }

    private StringSelectMenu ingredientSelect() {
        StringSelectMenu.Builder builder = StringSelectMenu.create(INGREDIENT_ID)
                .setPlaceholder("Select Ingredient...");

        List<List<String>> pages = buildIngredientPages();
        List<String> currentPage = pages.get(Math.min(ingredientsPage, pages.size() - 1));
        for (String ingredient : currentPage) {
            String displayName = ItemRegistry.getMenuDisplayNameWithFeature(ingredient);
            builder.addOption(displayName, ingredient);
        }
        if (pages.size() > 1) {
            String label = String.format("More... (%d/%d)", ingredientsPage + 1, pages.size());
            builder.addOption(label, MORE_INDICATOR);
        }

        return builder.build();
    }

    /**
     * Builds a list of ingredient pages. Each page is a list of craftable ingredients, then auxiliary ingredients,
     * up to {@link StringSelectMenu#OPTIONS_MAX_AMOUNT}. If there must be more than one page, the max size is one
     * shorter to make room for a "More..." option.
     * @return A list of at least one page, each with at least one ingredient
     */
    private List<List<String>> buildIngredientPages() {
        List<List<String>> pages = new ArrayList<>();
        List<String> craftable = craftableIngredients();

        List<String> currentPage = new ArrayList<>();
        List<String> currentAuxiliary = auxiliaryIngredients();
        for (int i = 0; i < craftable.size(); i++) {
            String ingredientMinecordId = craftable.get(i);
            // prevents duplicate ingredient showing up in the same page
            currentAuxiliary.remove(ingredientMinecordId);

            int craftableIngredientsLeft = craftable.size() - i;
            int effectiveSize = currentPage.size() + currentAuxiliary.size();
            boolean isAboutToRunOutOfOptions = effectiveSize == StringSelectMenu.OPTIONS_MAX_AMOUNT - 1;
            if (isAboutToRunOutOfOptions && (pages.size() > 1 || craftableIngredientsLeft > 1)) {
                currentPage.addAll(currentAuxiliary);
                pages.add(currentPage);
                currentPage = new ArrayList<>();
            }

            currentPage.add(ingredientMinecordId);
        }

        currentPage.addAll(currentAuxiliary);
        pages.add(currentPage);
        return pages;
    }

    private Recipe currentRecipe() {
        return recipes.get(page);
    }

    private List<String> ingredients() {
        return RecipeRegistry.getIngredientItems(currentRecipe());
    }

    private List<String> craftableIngredients() {
        List<String> craftable = ingredients().stream()
                .map(ItemRegistry::searchNoStats)
                .filter(ingredientMinecordId -> !craftableFromIngredient(ingredientMinecordId).isEmpty())
                .collect(Collectors.toCollection(ArrayList::new));
        if (craftable.contains("minecraft.potion.effect.water") && !craftable.contains("minecraft:glass_bottle")) {
            craftable.add("minecraft:glass_bottle");
        }
        return craftable;
    }

    // ingredients not directly part of the recipe but still needed to craft
    private List<String> auxiliaryIngredients() {
        Recipe recipe = currentRecipe();
        List<String> list = new ArrayList<>();

        if (recipe instanceof BrewingRecipe) {
            list.add("minecraft:blaze_powder");
        }

        list.add(recipe.getTableItem()); // always at least one item!
        if (recipe instanceof CraftingRecipe) {
            list.add("minecraft:crafter");
        }
        if (recipe instanceof SmeltingRecipe) {
            boolean isBlasting = recipe.getKey().contains("from_smelting") &&
                    RecipeRegistry.contains(recipe.getKey().replace("from_smelting", "from_blasting"));
            if (isBlasting) {
                list.add("minecraft:blast_furnace");
            }
            boolean isSmoking = RecipeRegistry.contains(recipe + "_from_smoking");
            if (isSmoking) {
                list.add("minecraft:smoker");
                list.add("minecraft:campfire");
            }
        }

        return list.stream()
                .map(ItemRegistry::searchNoStats)
                .collect(Collectors.toList());
    }

    private static List<Recipe> craftableFromIngredient(String ingredientMinecordId) {
        String toSearch;
        if (!ingredientMinecordId.contains("potion") && !ingredientMinecordId.contains("tipped_arrow")) {
            toSearch = ItemRegistry.getNamespacedID(ingredientMinecordId);
        } else {
            toSearch = ingredientMinecordId;
        }
        return RecipeRegistry.searchItemOutput(toSearch);
    }

    private List<Recipe> craftableFromOutput() {
        return RecipeRegistry.searchIngredient(ItemRegistry.searchNoStats(currentRecipe().getResult().getItem()));
    }

}
