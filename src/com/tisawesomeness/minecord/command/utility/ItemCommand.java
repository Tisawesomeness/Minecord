package com.tisawesomeness.minecord.command.utility;

import java.awt.Color;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.item.Item;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class ItemCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"item",
			"Looks up an item.",
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
		Item item = null;
		for (Item i : Item.values()) {
			if (i.matches(string)) {
				item = i;
				break;
			}
		}
		
		//If nothing is found
		if (item == null) {
			return new Result(Outcome.WARNING,
				":warning: That item does not exist! " +
				"\n" + "Did you spell it correctly?");
		}
		
		//Build message
		EmbedBuilder eb = item.getInfo();
		eb.setTitle(item.name);
		eb.setColor(Color.GREEN);
		eb = MessageUtils.addFooter(eb);
		
		return new Result(Outcome.SUCCESS, eb.build());
	}

}
