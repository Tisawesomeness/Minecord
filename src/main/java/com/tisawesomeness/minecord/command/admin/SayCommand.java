package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.util.DiscordUtils;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.Arrays;

public class SayCommand extends AbstractAdminCommand {

	public @NonNull String getId() {
		return "say";
	}
	public CommandInfo getInfo() {
		return new CommandInfo(
                true,
				true,
				false
		);
	}
	
	public Result run(CommandContext ctx) {
		
		//Check for proper argument length
		if (ctx.args.length < 2) {
			return ctx.showHelp();
		}
		
		//Extract channel
		TextChannel channel = DiscordUtils.findChannel(ctx.args[0], ctx.bot.getShardManager());
		if (channel == null) return new Result(Outcome.ERROR, ":x: Not a valid channel!");
		
		//Send the message
		String msg = String.join(" ", Arrays.copyOfRange(ctx.args, 1, ctx.args.length));
		channel.sendMessage(msg).queue();
		
		//Log it
		EmbedBuilder eb = new EmbedBuilder();
		Guild guild = channel.getGuild();
		User a = ctx.e.getAuthor();
		eb.setAuthor(a.getAsTag() + " (`" + a.getId() + "`)", null, a.getAvatarUrl());
		eb.setDescription("**Sent a msg to `" + channel.getName() + "` (`" + channel.getId() + "`)**\non `" +
			guild.getName() + "` (" + guild.getId() + "):\n" + msg);
		eb.setThumbnail(guild.getIconUrl());
		ctx.log(eb.build());
		
		return new Result(Outcome.SUCCESS);
	}
	
}
