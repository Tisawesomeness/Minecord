package com.tisawesomeness.minecord.command.utility;

import java.awt.Color;
import java.util.regex.Pattern;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.item.Item;
import com.tisawesomeness.minecord.item.Recipe;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class RecipeCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"recipe",
			"Looks up a recipe.",
			"<item name|id>",
			null,
			1000,
			false,
			false,
			true
		);
	}

	public Result run(String[] args, MessageReceivedEvent e) throws Exception {
		
		//Check for argument length.
		if (args.length == 0) {
			return new Result(Outcome.WARNING, ":warning: You must specify an item!");
		}
		
		//Convert to single string
		String string = "";
		for (String arg : args) {
			string += arg + " ";
		}
		string = string.substring(0, string.length() - 1);
		
		//Trim string
		string = Pattern.compile("(^[^0-9A-Z]+)|([^0-9A-Z\\)]+$)", Pattern.CASE_INSENSITIVE).matcher(string).replaceAll("");
		
		//Search through the item database
		Recipe recipe = null;
		boolean escape = false;
		for (Recipe r : Recipe.values()) {
			Item i = r.item;
			for (int mode = 0; mode <= 4; mode++) {
				if (i.matches(string, mode)) {
					recipe = r;
					escape = true;
					break;
				}
			}
			if (escape) break;
		}
		
		MessageUtils.log(
			"Recipe command executed" +
			"\nCommand: `" + e.getMessage().getContent() + "`" +
			"\nRecipe: `" + recipe + "`"
		);
		
		//If nothing is found
		if (recipe == null) {
			return new Result(Outcome.WARNING,
				":warning: That item does not exist or does not have a recipe! " +
				"\n" + "Did you spell it correctly?");
		}
		
		//Build message
		EmbedBuilder eb = recipe.item.getInfo();
		eb.setTitle(recipe.type.toString() + " Recipe");
		eb.setColor(Color.GREEN);
		eb.setFooter("See an error? Please report them at https://goo.gl/KWCxis", null);
		//eb = MessageUtils.addFooter(eb);
		
		return new Result(Outcome.SUCCESS, eb.build());
	}

}
