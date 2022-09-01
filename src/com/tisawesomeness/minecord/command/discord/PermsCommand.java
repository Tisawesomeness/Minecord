package com.tisawesomeness.minecord.command.discord;

import com.tisawesomeness.minecord.command.SlashCommand;
import com.tisawesomeness.minecord.util.DiscordUtils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.EnumSet;

public class PermsCommand extends SlashCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "perms",
                "Test the bot's permissions in a channel.",
                "[<channel>]",
                0,
                false,
                false
        );
    }

    @Override
    public SlashCommandData addCommandSyntax(SlashCommandData builder) {
        return builder.addOption(OptionType.CHANNEL, "channel", "The channel to test permissions in", false);
    }

    @Override
    public String[] getLegacyAliases() {
        return new String[]{"permissions"};
    }

    @Override
    public String getHelp() {
        return "`{&}perms` - Test the bot's permissions for the current channel.\n" +
                "`{&}perms <channel>` - Test the bot's permissions for a channel in the same guild.\n" +
                "You must have permission to send messages in the channel being tested.\n" +
                "\n" +
                "Examples:\n" +
                "- `{&}perms #bot-commands`\n";
    }

    // Error message cannot be "you cannot see that channel" since it reveals the channel exists when the user couldn't have known that otherwise
    private static final Result invalidChannel = new Result(Outcome.WARNING, ":warning: That channel does not exist in the current guild or is not visible to you.");

    public Result run(SlashCommandInteractionEvent e) {
        if (!e.isFromGuild()) {
            return new Result(Outcome.WARNING, ":warning: This command is not available in DMs.");
        }
        GuildChannel c = e.getOption("channel", e.getGuildChannel(), OptionMapping::getAsChannel);

        // Check for user permissions (prevent using this command to get unseen channel info)
        if (!e.getMember().hasPermission(c, Permission.VIEW_CHANNEL, Permission.VIEW_CHANNEL)) {
            return invalidChannel;
        } else if (!e.getMember().hasPermission(c, Permission.MESSAGE_SEND)) {
            return new Result(Outcome.WARNING, ":warning: You do not have permission to write in that channel.");
        }

        return run(c);
    }

    public static Result run(GuildChannel c) {
        EnumSet<Permission> perms = c.getGuild().getSelfMember().getPermissions(c);
        String m = String.format("**Bot Permissions for %s:**", c.getAsMention()) +
                "\nView channels: " + DiscordUtils.getBoolEmote(perms.contains(Permission.VIEW_CHANNEL)) +
                "\nRead message history: " + DiscordUtils.getBoolEmote(perms.contains(Permission.MESSAGE_HISTORY)) +
                "\nSend messages: " + DiscordUtils.getBoolEmote(perms.contains(Permission.MESSAGE_SEND)) +
                "\nSend messages in threads: " + DiscordUtils.getBoolEmote(perms.contains(Permission.MESSAGE_SEND_IN_THREADS)) +
                "\nEmbed links: " + DiscordUtils.getBoolEmote(perms.contains(Permission.MESSAGE_EMBED_LINKS)) +
                "\nAttach files: " + DiscordUtils.getBoolEmote(perms.contains(Permission.MESSAGE_ATTACH_FILES)) +
                "\nAdd reactions: " + DiscordUtils.getBoolEmote(perms.contains(Permission.MESSAGE_ADD_REACTION)) +
                "\nManage messages: " + DiscordUtils.getBoolEmote(perms.contains(Permission.MESSAGE_MANAGE)) +
                "\nCan upload favicons: " + getFaviconEmote(perms) +
                "\nCan use reaction menus: " + getMenuEmote(perms);

        return new Result(Outcome.SUCCESS, m);
    }

    private static String getFaviconEmote(EnumSet<Permission> perms) {
        if (perms.contains(Permission.MESSAGE_ATTACH_FILES)) {
            return ":white_check_mark:";
        }
        return ":x: Give attach files permissions and add reactions permissions to fix";
    }

    private static String getMenuEmote(EnumSet<Permission> perms) {
        if (perms.contains(Permission.MESSAGE_EMBED_LINKS) && perms.contains(Permission.MESSAGE_ADD_REACTION)) {
            if (perms.contains(Permission.MESSAGE_MANAGE)) {
                return ":white_check_mark:";
            }
            return ":warning: Partial, users must remove reactions manually, give manage messages permissions to fix";
        }
        return ":x: Give embed links and add reactions permissions to fix";
    }

}
