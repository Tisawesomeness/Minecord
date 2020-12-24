package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.ReactMenu;
import com.tisawesomeness.minecord.ReactMenu.MenuStatus;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.Result;
import com.tisawesomeness.minecord.mc.item.Recipe;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.ArrayList;
import java.util.Arrays;

public class RecipeCommand extends AbstractUtilityCommand {

    public @NonNull String getId() {
        return "recipe";
    }

    public Result run(String[] argsOrig, CommandContext ctx) {
        String[] args = Arrays.copyOf(argsOrig, argsOrig.length);

        // Check for argument length
        if (args.length == 0) {
            return ctx.showHelp();
        }
        ctx.triggerCooldown();

        // Parse page number
        int page = 0;
        if (args.length > 1) {
            if (args[args.length - 1].matches("^[0-9]+$")) {
                page = Integer.valueOf(args[args.length - 1]) - 1;
                args = Arrays.copyOf(args, args.length - 1);
            }
        }
        if (page < 0) {
            return ctx.invalidArgs("The page number must be positive.");
        }

        // Search through the recipe database
        ArrayList<String> recipes = Recipe.searchOutput(String.join(" ", args), "en_US");
        if (recipes == null) {
            return ctx.invalidArgs("That item does not exist!\nDid you spell it correctly?");
        }
        if (recipes.size() == 0) {
            return ctx.warn("That item does not have a recipe!");
        }
        if (page >= recipes.size()) {
            return ctx.warn("That page does not exist!");
        }

        // Create menu
        MenuStatus status = ReactMenu.getMenuStatus(ctx);
        if (status == MenuStatus.NO_PERMISSION) {
            return ctx.noBotPermissions(MenuStatus.NO_PERMISSION.getReason());
        }
        if (status.isValid()) {
            new Recipe.RecipeMenu(recipes, page, "en_US").post(ctx.getE().getChannel(), ctx.getE().getAuthor());
            return Result.SUCCESS;
        }
        EmbedBuilder eb = Recipe.displayImg(recipes.get(page), "en_US");
        eb.setFooter(String.format("Page %s/%s%s", page + 1, recipes.size(), status.getReason()), null);
        return ctx.replyRaw(eb);

    }

}
