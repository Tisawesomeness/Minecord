package com.tisawesomeness.minecord.command.utility;

import java.awt.Color;
import java.util.regex.Pattern;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.item.Item;
//import com.tisawesomeness.minecord.util.MessageUtils;

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
		
		//Trim string
		string = Pattern.compile("(^[^0-9A-Z]+)|([^0-9A-Z\\)]+$)", Pattern.CASE_INSENSITIVE).matcher(string).replaceAll("");
		
		//Search through the item database
		Item item = null;
		boolean escape = false;
		for (Item i : Item.values()) {
			for (int mode = 0; mode <= 4; mode++) {
				if (i.matches(string, mode)) {
					item = i;
					escape = true;
					break;
				}
			}
			if (escape) break;
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
		eb.setFooter("See an error? Please report them at https://goo.gl/KWCxis", null);
		//eb = MessageUtils.addFooter(eb);
		
		return new Result(Outcome.SUCCESS, eb.build());
	}

}
