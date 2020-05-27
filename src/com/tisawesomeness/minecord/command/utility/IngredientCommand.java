package com.tisawesomeness.minecord.command.utility;

import java.util.ArrayList;
import java.util.Arrays;

import com.tisawesomeness.minecord.ReactMenu;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.item.Recipe;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class IngredientCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"ingredient",
			"Looks up the recipes containing an ingredient.",
			"<item name|id>",
			new String[]{"ingredients"},
			2500,
			false,
			false,
			true
		);
	}

	public Result run(String[] args, MessageReceivedEvent e) throws Exception {

		// Parse page number
		int page = 0;
		if (args.length > 1) {
			if (args[args.length - 1].matches("^[0-9]+$")) {
				page = Integer.valueOf(args[args.length - 1]) - 1;
				args = Arrays.copyOf(args, args.length - 1);
			}
		}

		//Check for argument length
		if (args.length == 0) {
			return new Result(Outcome.WARNING, ":warning: You must specify an item!");
		}

		// Search through the recipe database
		ArrayList<String> recipes = Recipe.searchIngredient(String.join(" ", args), "en_US");
		if (recipes == null) {
			return new Result(Outcome.WARNING,
				":warning: That item does not exist! " +
				"\n" + "Did you spell it correctly?");
		}
		if (recipes.size() == 0) {
			return new Result(Outcome.WARNING, ":warning: That item is not an ingredient for any recipes!");
		}

		// Create menu
		if (ReactMenu.canMakeMenu(e.getGuild(), e.getTextChannel())) {
			new Recipe.RecipeMenu(recipes, page, "en_US").post(e.getChannel(), e.getAuthor());
			return new Result(Outcome.SUCCESS);
		}
		EmbedBuilder eb = Recipe.displayImg(recipes.get(page), "en_US");
		eb.setFooter(String.format(
			"Page %s/%s | Give the bot manage messages permissions to use an interactive menu!", page + 1, recipes.size())
		, null);
		return new Result(Outcome.SUCCESS, eb.build());
	}

}
