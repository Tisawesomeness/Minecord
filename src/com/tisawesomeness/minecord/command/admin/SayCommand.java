package com.tisawesomeness.minecord.command.admin;

import org.apache.commons.lang3.ArrayUtils;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class SayCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"say",
			"Send a message",
			"<channel> <message>",
			new String[]{
				"talk",
				"announce"
			},
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
		System.out.println(raw);
		String param = raw.split(" ")[1];
		TextChannel channel = null;
		if (param.matches(MessageUtils.channelRegex)) {
			channel = e.getMessage().getMentionedChannels().get(0);
		} else if (param.matches(MessageUtils.idRegex)) {
			channel = Bot.jda.getTextChannelById(param);
		} else {
			return new Result(Outcome.ERROR, ":x: Not a valid channel!");
		}
		
		//Send the message
		String msg = String.join(" ", ArrayUtils.remove(args, 0));
		channel.sendMessage(msg).queue();
		
		//Log it
		EmbedBuilder eb = new EmbedBuilder();
		eb.setAuthor(e.getAuthor().getName() + " (" + e.getAuthor().getId() + ")",
			null, e.getAuthor().getAvatarUrl());
		msg = raw.replaceFirst(MessageUtils.messageRegex, "");
		eb.setDescription("**Sent a msg to `" + channel.getName() + "` (" + channel.getId() + ")**\non `" +
			e.getGuild().getName() + "` (" + e.getGuild().getId() + "):\n" + msg);
		eb.setThumbnail(e.getGuild().getIconUrl());
		MessageUtils.log(eb.build());
		
		return new Result(Outcome.SUCCESS, "");
	}
	
}
