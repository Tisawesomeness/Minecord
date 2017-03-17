package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.Command;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CodesCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"codes",
			"Lists the available chat codes.",
			null,
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
		
		String m = "**Chat Codes:**" +
				"\n" + "Copy-Paste symbol: §" +
				"\n" + "MOTD code: \\u00A7" +
				"\n" + "Most servers with plugins let you use & instead of § in chat and config files." +
				"\n" + "http://i.imgur.com/MWCFs5S.png" +
				"\n" + "http://i.imgur.com/cWYjhkN.png";
		return new Result(Outcome.SUCCESS, m);
		
	}

}
