package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.CommandContext;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class NameCommand extends AbstractAdminCommand {

	public @NonNull String getId() {
		return "name";
	}
	public CommandInfo getInfo() {
		return new CommandInfo(
                true,
				true,
				false
		);
	}
	
	public Result run(CommandContext ctx) {
		String[] args = ctx.args;
		
		//Check for proper argument length
		if (args.length < 1) {
			return ctx.showHelp();
		}
		
		//Get guild
		Guild guild = ctx.bot.getShardManager().getGuildById(args[0]);
		if (guild == null) return new Result(Outcome.ERROR, ":x: Not a valid guild!");
		
		//Check for permissions
		if (!guild.getSelfMember().hasPermission(Permission.NICKNAME_CHANGE)) {
			return new Result(Outcome.WARNING, ":warning: No permissions!");
		}
		
		//Set the nickname
		String name;
		if (args.length > 1) {
			name = ctx.joinArgsSlice(1);
		} else {
			name = ctx.e.getJDA().getSelfUser().getName();
		}
		guild.modifyNickname(guild.getSelfMember(), name).queue();
		
		//Log it
		EmbedBuilder eb = new EmbedBuilder();
		User a = ctx.e.getAuthor();
		eb.setAuthor(a.getAsTag() + " (`" + a.getId() + "`)",
			null, a.getAvatarUrl());
		String desc = args.length == 1 ? "**Reset" : "**Changed";
		desc += " nickname on `" + guild.getName() + "` (`" + guild.getId() + "`):**";
		if (args.length == 1) desc += "\n" + name;
		eb.setDescription(desc);
		eb.setThumbnail(guild.getIconUrl());
		ctx.log(eb.build());
		
		return new Result(Outcome.SUCCESS);
	}
	
}
