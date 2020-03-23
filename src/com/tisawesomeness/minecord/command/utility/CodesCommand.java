package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.Command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CodesCommand extends Command {
	
	private String img = "https://minecraft.gamepedia.com/media/minecraft.gamepedia.com/7/7e/Minecraft_Formatting.gif";
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"codes",
			"Lists the available chat codes.",
			"[detailed?]",
			new String[]{
				"code",
				"chat",
				"color",
				"colors"},
			10000,
			false,
			false,
			true
		);
	}
	
	public Result run(String[] args, MessageReceivedEvent e) {
		
		if (args.length > 0) {
			return new Result(Outcome.SUCCESS, "**Chat Codes:**" +
				"\n" + "Copy-Paste symbol: `\u00A7`" +
				"\n" + "MOTD code: `\\u00A7`" +
				"\n" + "Some servers with plugins let you use `&` instead of `\u00A7` in chat and config files." +
				"\n" + "http://i.imgur.com/MWCFs5S.png" +
				"\n" + "http://i.imgur.com/cWYjhkN.png");
		} else {
			return new Result(Outcome.SUCCESS, new EmbedBuilder().setImage(img).build());
		}
		
	}

}
