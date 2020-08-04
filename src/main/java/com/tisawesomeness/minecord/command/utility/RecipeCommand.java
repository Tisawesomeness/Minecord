package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.ReactMenu;
import com.tisawesomeness.minecord.ReactMenu.MenuStatus;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.item.Recipe;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.ArrayList;
import java.util.Arrays;

public class RecipeCommand extends AbstractUtilityCommand {

    public @NonNull String getId() {
        return "recipe";
    }

    public Result run(String[] args, CommandContext ctx) {
        // Check for argument length
        if (args.length == 0) {
            return ctx.showHelp();
        }

        // Parse page number
        int page = 0;
        if (args.length > 1) {
            if (args[args.length - 1].matches("^[0-9]+$")) {
                page = Integer.valueOf(args[args.length - 1]) - 1;
                args = Arrays.copyOf(args, args.length - 1);
            }
        }

        // Search through the recipe database
        ArrayList<String> recipes = Recipe.searchOutput(ctx.joinArgs(), "en_US");
        if (recipes == null) {
            return new Result(Outcome.WARNING,
                    ":warning: That item does not exist! " + "\n" + "Did you spell it correctly?");
        }
        if (recipes.size() == 0) {
            return new Result(Outcome.WARNING, ":warning: That item does not have a recipe!");
        }
        if (page >= recipes.size()) {
            return new Result(Outcome.WARNING, ":warning: That page does not exist!");
        }

        // Create menu
        MenuStatus status = ReactMenu.getMenuStatus(ctx);
        if (status.isValid()) {
            new Recipe.RecipeMenu(recipes, page, "en_US").post(ctx.e.getChannel(), ctx.e.getAuthor());
            return new Result(Outcome.SUCCESS);
        }
        EmbedBuilder eb = Recipe.displayImg(recipes.get(page), "en_US");
        eb.setFooter(String.format("Page %s/%s%s", page + 1, recipes.size(), status.getReason()), null);
        return new Result(Outcome.SUCCESS, eb.build());

    }

}
