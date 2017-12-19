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
		//Search through the item database
		Item[] items = new Item[Recipe.values().length];
		for (int i = 0; i < items.length; i++) {
			items[i] = Recipe.values()[i].item;
			System.out.println(items[i].name());
		}
		Item item = Item.search(items, Item.prepareArgs(args));
		Recipe recipe = null;
		if (item != null) {
			recipe = Recipe.valueOf(item.name());
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
