package com.tisawesomeness.minecord.command.utility;

import java.awt.Color;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.item.Item;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ItemCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"item",
			"Looks up an item.",
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
		Item item = Item.search(Item.values(), Item.prepareArgs(args));
		
		MessageUtils.log(
			"Item command executed" +
			"\nCommand: `" + e.getMessage().getContentDisplay() + "`" +
			"\nItem: `" + item + "`"
		);
		
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
		eb.setFooter("See an error? Please report them at https://goo.gl/KWCxis", null);
		//eb = MessageUtils.addFooter(eb);
		
		return new Result(Outcome.SUCCESS, eb.build());
	}

}
