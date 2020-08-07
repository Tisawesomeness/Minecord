package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.ReactMenu;
import com.tisawesomeness.minecord.ReactMenu.MenuStatus;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.Result;
import com.tisawesomeness.minecord.item.Recipe;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;

public class IngredientCommand extends AbstractUtilityCommand {

    public @NonNull String getId() {
        return "ingredient";
    }

    @Override
    public EnumSet<Permission> getOptionalBotPermissions() {
        return EnumSet.of(Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_MANAGE);
    }

    public Result run(String[] argsOrig, CommandContext ctx)  {
        String[] args = Arrays.copyOf(argsOrig, argsOrig.length);

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
        ArrayList<String> recipes = Recipe.searchIngredient(String.join(" ", args), "en_US");
        if (recipes == null) {
            return ctx.warn("That item does not exist! Did you spell it correctly?");
        }
        if (recipes.size() == 0) {
            return ctx.warn("That item is not an ingredient for any recipes!");
        }

        // Create menu
        MenuStatus status = ReactMenu.getMenuStatus(ctx);
        if (status.isValid()) {
            new Recipe.RecipeMenu(recipes, page, "en_US").post(ctx.e.getChannel(), ctx.e.getAuthor());
            return Result.SUCCESS;
        }
        EmbedBuilder eb = Recipe.displayImg(recipes.get(page), "en_US");
        eb.setFooter(String.format("Page %s/%s%s", page + 1, recipes.size(), status.getReason()), null);
        return ctx.replyRaw(eb);
    }

}
