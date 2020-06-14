package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.util.DiscordUtils;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.Arrays;

public class SayCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"say",
			"Send a message.",
			"<channel> <message>",
			new String[]{
				"talk",
				"announce"},
			0,
			true,
			true,
			false
		);
	}

	public String getHelp() {
		return "`{&}say <channel> <message>` - Make the bot send a message.\n" +
			"`<channel>` is either a # channel name or an 18-digit ID.\n";
	}
	
	public Result run(CommandContext txt) {
		
		//Check for proper argument length
		if (txt.args.length < 2) {
			return new Result(Outcome.WARNING, ":warning: Please specify a message.");
		}
		
		//Extract channel
		TextChannel channel = DiscordUtils.findChannel(txt.args[0], txt.bot.getShardManager());
		if (channel == null) return new Result(Outcome.ERROR, ":x: Not a valid channel!");
		
		//Send the message
		String msg = String.join(" ", Arrays.copyOfRange(txt.args, 1, txt.args.length));
		channel.sendMessage(msg).queue();
		
		//Log it
		EmbedBuilder eb = new EmbedBuilder();
		Guild guild = channel.getGuild();
		User a = txt.e.getAuthor();
		eb.setAuthor(a.getAsTag() + " (`" + a.getId() + "`)", null, a.getAvatarUrl());
		eb.setDescription("**Sent a msg to `" + channel.getName() + "` (`" + channel.getId() + "`)**\non `" +
			guild.getName() + "` (" + guild.getId() + "):\n" + msg);
		eb.setThumbnail(guild.getIconUrl());
		MessageUtils.log(eb.build());
		
		return new Result(Outcome.SUCCESS);
	}
	
}
