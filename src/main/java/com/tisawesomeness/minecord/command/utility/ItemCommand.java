package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.item.Item;

import net.dv8tion.jda.api.EmbedBuilder;

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
			"Items are from Java Edition 1.7 to 1.15.\n" +
			"\n" +
			Item.help + "\n";
	}

	public Result run(CommandContext ctx) {
		// Check for argument length
		if (ctx.args.length == 0) {
			return new Result(Outcome.WARNING, ":warning: You must specify an item!");
		}
		
		// Search through the item database
		String item = Item.search(String.join(" ", ctx.args), "en_US");
		
		// If nothing is found
		if (item == null) {
			return new Result(Outcome.WARNING,
				":warning: That item does not exist! " +
				"\n" + "Did you spell it correctly?");
		}
		
		// Build message
		EmbedBuilder eb = Item.display(item, "en_US", ctx.prefix);
		eb.setFooter("See an error? Please report them at https://goo.gl/KWCxis", null);
		// eb = MessageUtils.addFooter(eb);
		
		return new Result(Outcome.SUCCESS, eb.build());
	}

}
