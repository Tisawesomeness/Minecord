package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.ReactMenu;
import com.tisawesomeness.minecord.ReactMenu.MenuStatus;
import com.tisawesomeness.minecord.command.SlashCommand;
import com.tisawesomeness.minecord.mc.item.Item;
import com.tisawesomeness.minecord.mc.item.Recipe;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.ArrayList;

public class RecipeCommand extends SlashCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "recipe",
                "Look up recipes.",
                "<item name|id> [page]",
                1000,
                false,
                false
        );
    }

    @Override
    public SlashCommandData addCommandSyntax(SlashCommandData builder) {
        return builder.addOption(OptionType.STRING, "item", "A Minecraft item with a recipe", true)
                .addOptions(new OptionData(OptionType.INTEGER, "page", "The page of recipes to show", false)
                        .setRequiredRange(1, Integer.MAX_VALUE));
    }

    @Override
    public String[] getLegacyAliases() {
        return new String[]{"r", "craft"};
    }

    @Override
    public String getHelp() {
        return "Shows the recipes for an item.\n" +
                "Items and recipes are from Java Edition 1.7 to " + Bot.mcVersion + ".\n" +
                "All recipe types are searchable, including brewing.\n" +
                "\n" +
                Item.help + "\n";
    }

    public Result run(SlashCommandInteractionEvent e) {

        // Search through the recipe database with full args first
        String search = e.getOption("item").getAsString();
        String item = Item.search(search, "en_US");
        if (item == null) {
            return new Result(Outcome.WARNING,
                    ":warning: That item does not exist! " + "\n" + "Did you spell it correctly?");
        }

        OptionMapping option = e.getOption("page");
        int page;
        if (option == null) {
            page = 0;
        } else {
            page = option.getAsInt() - 1;
            if (page < 0) {
                return new Result(Outcome.WARNING, ":warning: Page must be 1 or higher.");
            }
        }

        ArrayList<String> recipes = Recipe.searchOutput(item, "en_US");
        if (recipes.size() == 0) {
            String displayName = Item.getDistinctDisplayName(item, "en_US");
            return new Result(Outcome.SUCCESS, ":warning: " + displayName + " does not have any recipes.");
        }
        if (page >= recipes.size()) {
            if (recipes.size() == 1) {
                return new Result(Outcome.WARNING, ":warning: There is only 1 page.");
            }
            return new Result(Outcome.WARNING, ":warning: Choose a page 1-" + recipes.size() + ".");
        }

        // Create menu
        MenuStatus status = ReactMenu.getMenuStatus(e);
        if (status.isValid()) {
            e.deferReply().queue();
            new Recipe.RecipeMenu(recipes, page, "en_US").post(e);
            return new Result(Outcome.SUCCESS);
        }
        recipes.sort(Recipe::compareRecipes);
        EmbedBuilder eb = Recipe.displayImg(recipes.get(page), "en_US");
        eb.setFooter(String.format("Page %s/%s%s", page + 1, recipes.size(), status.getReason()), null);
        return new Result(Outcome.SUCCESS, eb.build());

    }

}
