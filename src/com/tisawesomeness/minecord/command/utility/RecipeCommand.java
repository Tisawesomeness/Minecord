package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.SlashCommand;
import com.tisawesomeness.minecord.interaction.InteractionTracker;
import com.tisawesomeness.minecord.interaction.RecipeMenu;
import com.tisawesomeness.minecord.mc.VersionRegistry;
import com.tisawesomeness.minecord.mc.item.ItemRegistry;
import com.tisawesomeness.minecord.mc.recipe.Recipe;
import com.tisawesomeness.minecord.mc.recipe.RecipeRegistry;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.List;

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
                "Items and recipes are from Java Edition 1.7 to " + VersionRegistry.getLatestVersion() + ".\n" +
                "All recipe types are searchable, including brewing.\n" +
                "\n" +
                ItemRegistry.help + "\n";
    }

    public Result run(SlashCommandInteractionEvent e) {

        // Search through the recipe database with full args first
        String search = e.getOption("item").getAsString();
        String item = ItemRegistry.search(search);
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

        List<Recipe> recipes = RecipeRegistry.searchOutput(item);
        if (recipes.isEmpty()) {
            String displayName = ItemRegistry.getDistinctDisplayName(item);
            return new Result(Outcome.SUCCESS, ":warning: " + displayName + " does not have any recipes.");
        }
        if (page >= recipes.size()) {
            if (recipes.size() == 1) {
                return new Result(Outcome.WARNING, ":warning: There is only 1 page.");
            }
            return new Result(Outcome.WARNING, ":warning: Choose a page 1-" + recipes.size() + ".");
        }

        RecipeMenu menu = new RecipeMenu(recipes, page);
        if (InteractionTracker.shouldUseMenus(e)) {
            e.deferReply().queue();
            InteractionTracker.post(e.getHook(), menu);
            return new Result(Outcome.SUCCESS);
        }
        return new Result(Outcome.SUCCESS, menu.render(false));

    }

}
