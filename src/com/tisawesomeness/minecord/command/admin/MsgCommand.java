package com.tisawesomeness.minecord.command.admin;

import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.Command;

import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class MsgCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"msg",
			"Open the DMs.",
			"<mention> <message>",
			new String[]{
				"dm",
				"tell",
				"pm"},
			0,
			true,
			true,
			false
		);
	}
	
	private final String regex = "[a-zA-Z]+ <@!?[0-9]+> ";
	
	public Result run(String[] args, MessageReceivedEvent e) {
		
		//Check for proper argument length
		if (args.length < 2) {
			return new Result(Outcome.WARNING, ":warning: Please specify a message.");
		}
		
		//Send the message
		User user = e.getMessage().getMentionedUsers().get(0);
		try {
			PrivateChannel channel = user.openPrivateChannel().submit().get();
			String msg = e.getMessage().getRawContent().replaceFirst(Pattern.quote(Config.getPrefix()) + regex, "");
			channel.sendMessage(msg).queue();
		} catch (InterruptedException | ExecutionException ex) {
			ex.printStackTrace();
		}
		
		return new Result(Outcome.SUCCESS, "");
	}
	
}
