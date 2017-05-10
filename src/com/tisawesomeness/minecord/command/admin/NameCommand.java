package com.tisawesomeness.minecord.command.admin;

import org.apache.commons.lang3.ArrayUtils;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class NameCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"name",
			"Changes the bot's nickname per-guild, enter nothing to reset.",
			"<guild id> <name>",
			new String[]{
				"nick",
				"nickname"
			},
			0,
			true,
			true,
			false
		);
	}
	
	public Result run(String[] args, MessageReceivedEvent e) {
		
		//Check for proper argument length
		if (args.length < 1) {
			return new Result(Outcome.WARNING, ":warning: Please specify a guild.");
		}
		
		//Get guild
		String raw = e.getMessage().getRawContent();
		String param = raw.split(" ")[1];
		Guild guild = null;
		if (param.matches(MessageUtils.idRegex)) {
			guild = Bot.jda.getGuildById(param);
		} else {
			return new Result(Outcome.ERROR, ":x: Not a valid guild!");
		}
		
		//Check for permissions
		if (!guild.getSelfMember().hasPermission(Permission.NICKNAME_CHANGE)) {
			return new Result(Outcome.WARNING, ":warning: No permissions!");
		}
		
		//Set the nickname
		String name = Config.getName();
		if (args.length > 1) {
			name = String.join(" ", ArrayUtils.remove(args, 0));
		}
		guild.getController().setNickname(guild.getSelfMember(), name).queue();
		
		//Log it
		EmbedBuilder eb = new EmbedBuilder();
		eb.setAuthor(e.getAuthor().getName() + " (" + e.getAuthor().getId() + ")",
			null, e.getAuthor().getAvatarUrl());
		String msg = raw.replaceFirst(MessageUtils.messageRegex, "");
		String desc = null;
		if (args.length == 1) {
			desc = "**Reset nickname on `" + guild.getName() + "` (" + guild.getId() + "):**";
		} else {
			desc = "**Changed nickname on `" + guild.getName() + "` (" + guild.getId() + "):**\n" + msg;
		}
		eb.setDescription(desc);
		eb.setThumbnail(guild.getIconUrl());
		MessageUtils.log(eb.build());
		
		return new Result(Outcome.SUCCESS, "");
	}
	
}
