package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.ReactMenu;
import com.tisawesomeness.minecord.ReactMenu.MenuStatus;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.item.Item;
import com.tisawesomeness.minecord.item.Recipe;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class RecipeCommand extends Command {

	public CommandInfo getInfo() {
		return new CommandInfo(
			"recipe",
			"Look up recipes.",
			"<item name|id>",
			new String[]{
				"r",
				"craft"},
			2500,
			false,
			false,
			true
		);
	}

	public String getHelp() {
		return "Shows the recipes for an item.\n" +
			"Items and recipes are from Java Edition 1.7 to 1.17.\n" +
			"All recipe types are searchable, including brewing.\n" +
			"\n" +
			Item.help + "\n";
	}

	public Result run(String[] args, MessageReceivedEvent e) {

		// Parse page number
		int page = 0;
		if (args.length > 1) {
			try {
				page = Integer.parseInt(args[args.length - 1]) - 1;
				args = Arrays.copyOf(args, args.length - 1);
			} catch (NumberFormatException ignored) {}
		}

		// Check for argument length
		if (args.length == 0) {
			return new Result(Outcome.WARNING, ":warning: You must specify an item!");
		}

		// Search through the recipe database
		ArrayList<String> recipes = Recipe.searchOutput(String.join(" ", args), "en_US");
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
		MenuStatus status = ReactMenu.getMenuStatus(e);
		if (status.isValid()) {
			new Recipe.RecipeMenu(recipes, page, "en_US").post(e.getChannel(), e.getAuthor());
			return new Result(Outcome.SUCCESS);
		}
		EmbedBuilder eb = Recipe.displayImg(recipes.get(page), "en_US");
		eb.setFooter(String.format("Page %s/%s%s", page + 1, recipes.size(), status.getReason()), null);
		return new Result(Outcome.SUCCESS, eb.build());

	}

}
