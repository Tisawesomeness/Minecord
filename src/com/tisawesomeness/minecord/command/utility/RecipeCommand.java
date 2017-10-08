package com.tisawesomeness.minecord.command.utility;

import java.awt.Color;

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
			"Looks up an item recipe.",
			"<item name|id>",
			null,
			1000,
			true,
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
		
		//Search through the item database
		Recipe recipe = null;
		for (Recipe r : Recipe.values()) {
			Item i = r.item;
			if (i.matches(string)) {
				recipe = r;
				break;
			}
		}
		
		//If nothing is found
		if (recipe == null) {
			return new Result(Outcome.WARNING,
				":warning: That item does not exist or does not have a recipe! " +
				"\n" + "Did you spell it correctly?");
		}
		
		//Build message
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(recipe.type.toString() + " Recipe");
		if (recipe.version != null) eb.setDescription(recipe.version.toString());
		eb.setImage(recipe.image);
		eb.setColor(Color.GREEN);
		eb = MessageUtils.addFooter(eb);
		
		return new Result(Outcome.SUCCESS, eb.build());
	}

}
