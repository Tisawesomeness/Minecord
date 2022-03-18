package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.ArrayUtils;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

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

    public String getHelp() {
        return "`{&}name <guild id>` - Resets the bot's nickname for the guild.\n" +
                "`{&}name <guild id> <name>` - Sets the bot's nickname for the guild. Requires *Change Nickname* permissions.\n";
    }

    public Result run(String[] args, MessageReceivedEvent e) {

        //Check for proper argument length
        if (args.length < 1) {
            return new Result(Outcome.WARNING, ":warning: Please specify a guild.");
        }

        //Get guild
        Guild guild = Bot.shardManager.getGuildById(args[0]);
        if (guild == null) return new Result(Outcome.ERROR, ":x: Not a valid guild!");

        //Check for permissions
        if (!guild.getSelfMember().hasPermission(Permission.NICKNAME_CHANGE)) {
            return new Result(Outcome.WARNING, ":warning: No permissions!");
        }

        //Set the nickname
        String name = args.length > 1 ? String.join(" ", ArrayUtils.remove(args, 0)) : e.getJDA().getSelfUser().getName();
        guild.modifyNickname(guild.getSelfMember(), name).queue();

        //Log it
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(e.getAuthor().getName() + " (" + e.getAuthor().getId() + ")",
                null, e.getAuthor().getAvatarUrl());
        String desc = args.length == 1 ? "**Reset" : "**Changed";
        desc += " nickname on `" + guild.getName() + "` (" + guild.getId() + "):**";
        if (args.length == 1) desc += "\n" + name;
        eb.setDescription(desc);
        eb.setThumbnail(guild.getIconUrl());
        MessageUtils.log(eb.build());

        return new Result(Outcome.SUCCESS);
    }

}
