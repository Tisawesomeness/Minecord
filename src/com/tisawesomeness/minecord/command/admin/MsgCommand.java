package com.tisawesomeness.minecord.command.admin;

import java.util.concurrent.ExecutionException;
import org.apache.commons.lang3.ArrayUtils;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class MsgCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"msg",
			"Open the DMs.",
			"<mention|id> <message>",
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
	
	public Result run(String[] args, MessageReceivedEvent e) {
		
		//Check for proper argument length
		if (args.length < 2) {
			return new Result(Outcome.WARNING, ":warning: Please specify a message.");
		}
		
		String raw = e.getMessage().getRawContent();
		String param = raw.split(" ")[1];
		User user = null;
		if (param.matches(MessageUtils.mentionRegex)) {
			user = e.getMessage().getMentionedUsers().get(0);
		} else if (param.matches(MessageUtils.idRegex)) {
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
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setAuthor(e.getAuthor().getName() + " (" + e.getAuthor().getId() + ")",
			null, e.getAuthor().getAvatarUrl());
		String msg = raw.replaceFirst(MessageUtils.messageRegex, "");
		eb.setDescription("**Sent a DM to " + user.getName() + " (" + user.getId() + "):**\n" + msg);
		eb.setThumbnail(user.getAvatarUrl());
		MessageUtils.log(eb.build());
		
		return new Result(Outcome.SUCCESS, "");
	}
	
}
