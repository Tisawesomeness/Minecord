package com.tisawesomeness.minecord.command.utility;

import java.util.regex.Pattern;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.item.Item;

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
			string = string + arg + " ";
		}
		string.substring(0, string.length() - 1);
		
		//Search through the item database
		Item item = null;
		for (Item search : Item.values()) {
			//Item id
			if (Pattern.compile("^" + search.getId() + "([^0-9:]|$)").matcher(string).find()) {
				item = search;
				break;
			}
			//Display name
			if (Pattern.compile(Pattern.quote(search.toString()), Pattern.CASE_INSENSITIVE).matcher(string).find()) {
				item = search;
				break;
			}
			//Regex list
			boolean escape = false;
			for (String alias : search.getAliases()) {
				if (Pattern.compile(alias, Pattern.CASE_INSENSITIVE).matcher(string).find()) {
					item = search;
					escape = true;
					break;
				}
			}
			if (escape) {break;}
		}
		if (item == null) {
			return new Result(Outcome.WARNING,
				":warning: That item does not exist or does not have a recipe! " +
				"\n" + "Did you spell it correctly?");
		}
		
		return new Result(Outcome.SUCCESS, "Found item: " + item.toString());
	}

}
