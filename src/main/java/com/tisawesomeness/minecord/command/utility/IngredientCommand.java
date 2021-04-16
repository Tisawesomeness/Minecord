package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.ReactMenu;
import com.tisawesomeness.minecord.ReactMenu.MenuStatus;
import com.tisawesomeness.minecord.command.meta.CommandContext;
import com.tisawesomeness.minecord.command.meta.Result;
import com.tisawesomeness.minecord.config.serial.Config;
import com.tisawesomeness.minecord.mc.item.Recipe;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.ArrayList;
import java.util.Arrays;

public class IngredientCommand extends AbstractUtilityCommand {

    public @NonNull String getId() {
        return "ingredient";
    }

    @Override
    public Object[] getHelpArgs(String prefix, String tag, Config config) {
        return new Object[]{prefix, tag, config.getSupportedMCVersion()};
    }

    public void run(String[] argsOrig, CommandContext ctx)  {
        String[] args = Arrays.copyOf(argsOrig, argsOrig.length);

        // Check for argument length
        if (args.length == 0) {
            ctx.showHelp();
            return;
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
            ctx.invalidArgs("The page number must be positive.");
            return;
        }

        // Search through the recipe database
        ArrayList<String> recipes = Recipe.searchIngredient(String.join(" ", args), "en_US");
        if (recipes == null) {
            ctx.invalidArgs("That item does not exist! Did you spell it correctly?");
            return;
        }
        if (recipes.size() == 0) {
            ctx.warn("That item is not an ingredient for any recipes!");
            return;
        }
        if (page >= recipes.size()) {
            ctx.warn("That page does not exist!");
            return;
        }

        // Create menu
        MenuStatus status = ReactMenu.getMenuStatus(ctx);
        if (status == MenuStatus.NO_PERMISSION) {
            ctx.noBotPermissions(MenuStatus.NO_PERMISSION.getReason());
            return;
        }
        if (status.isValid()) {
            new Recipe.RecipeMenu(recipes, page, "en_US").post(ctx.getE().getChannel(), ctx.getE().getAuthor());
            ctx.commandResult(Result.SUCCESS);
            return;
        }
        EmbedBuilder eb = Recipe.displayImg(recipes.get(page), "en_US")
                .setColor(ctx.getColor())
                .setFooter(String.format("Page %s/%s%s", page + 1, recipes.size(), status.getReason()), null);
        ctx.replyRaw(eb);
    }

}
