package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.LegacyCommand;
import com.tisawesomeness.minecord.util.ArrayUtils;
import com.tisawesomeness.minecord.util.DiscordUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class NameCommand extends LegacyCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "name",
                "Changes the bot's nickname per-guild, enter nothing to reset.",
                "<guild id> <name>",
                0,
                true,
                true
        );
    }

    public String[] getAliases() {
        return new String[]{"nick", "nickname"};
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
        String author = DiscordUtils.tagAndId(e.getAuthor());
        String action = args.length == 1 ? "reset" : "changed";
        String descName = args.length == 1 ? "\n" + name : "";
        String desc = author + " " + action + " nickname on `" + guild.getName() + "` (" + guild.getId() + "):";
        System.out.println(desc + "\n" + descName);
        eb.setDescription(MarkdownUtil.bold(desc) + "\n" + descName);
        eb.setThumbnail(guild.getIconUrl());
        Bot.logger.log(MessageCreateData.fromEmbeds(eb.build()));

        return new Result(Outcome.SUCCESS);
    }

}
