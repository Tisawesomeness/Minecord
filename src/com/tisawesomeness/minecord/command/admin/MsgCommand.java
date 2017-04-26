package com.tisawesomeness.minecord.command.admin;

import java.util.concurrent.ExecutionException;
import org.apache.commons.lang3.ArrayUtils;

import com.tisawesomeness.minecord.Bot;
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
	
	private final String mentionRegex = "<@!?[0-9]+> ";
	private final String idRegex = "[0-9]{18}";
	
	public Result run(String[] args, MessageReceivedEvent e) {
		
		//Check for proper argument length
		if (args.length < 2) {
			return new Result(Outcome.WARNING, ":warning: Please specify a message.");
		}
		
		String param = e.getMessage().getRawContent().split(" ")[0];
		User user = null;
		if (param.matches(mentionRegex)) {
			user = e.getMessage().getMentionedUsers().get(0);
		} else if (param.matches(idRegex)) {
			user = Bot.jda.getUserById(param);
		} else {
			return new Result(Outcome.ERROR, ":x: Not a valid user!");
		}
		
		//Send the message
		try {
			PrivateChannel channel = user.openPrivateChannel().submit().get();
			String msg = String.join(" ", ArrayUtils.remove(args, 0));
			channel.sendMessage(msg).queue();
		} catch (InterruptedException | ExecutionException ex) {
			ex.printStackTrace();
		}
		
		return new Result(Outcome.SUCCESS, "");
	}
	
}
