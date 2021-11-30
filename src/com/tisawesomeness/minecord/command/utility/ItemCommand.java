package com.tisawesomeness.minecord.command.utility;

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
			new String[]{"i"},
			2500,
			false,
			false,
			true
		);
	}

	public String getHelp() {
		return "Searches for a Minecraft item.\n" +
			"Items are from Java Edition 1.7 to 1.18.\n" +
			"\n" +
			Item.help + "\n";
	}

	public Result run(String[] args, MessageReceivedEvent e) {
		// Check for argument length
		if (args.length == 0) {
			return new Result(Outcome.WARNING, ":warning: You must specify an item!");
		}
		
		// Search through the item database
		String item = Item.search(String.join(" ", args), "en_US");
		
		// If nothing is found
		if (item == null) {
			return new Result(Outcome.WARNING,
				":warning: That item does not exist! " +
				"\n" + "Did you spell it correctly?");
		}
		
		// Build message
		EmbedBuilder eb = Item.display(item, "en_US", MessageUtils.getPrefix(e));
		eb = MessageUtils.addFooter(eb);

		return new Result(Outcome.SUCCESS, eb.build());
	}

}
