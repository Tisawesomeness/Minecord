package com.tisawesomeness.minecord.listen;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Lang;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.CommandExecutor;
import com.tisawesomeness.minecord.command.CommandRegistry;
import com.tisawesomeness.minecord.config.serial.Config;
import com.tisawesomeness.minecord.config.serial.FlagConfig;
import com.tisawesomeness.minecord.database.DatabaseCache;
import com.tisawesomeness.minecord.database.dao.DbUser;
import com.tisawesomeness.minecord.setting.SettingRegistry;
import com.tisawesomeness.minecord.util.MessageUtils;

import lombok.NonNull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.Optional;

public class CommandListener extends ListenerAdapter {

    private final @NonNull Bot bot;
    private final @NonNull Config config;
    private final @NonNull CommandRegistry registry;
    private final @NonNull CommandExecutor commandExecutor;

    public CommandListener(@NonNull Bot bot, @NonNull Config config, @NonNull CommandRegistry registry) {
        this.bot = bot;
        this.config = config;
        this.registry = registry;
        commandExecutor = new CommandExecutor(registry, config);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        Message m = e.getMessage();
        DatabaseCache cache = bot.getDatabaseCache();

        // Get the settings needed before command execution
        String prefix;
        Lang lang;
        boolean canEmbed = true;

        SettingRegistry settings = bot.getSettings();
        if (e.isFromType(ChannelType.TEXT)) {
            long cid = e.getTextChannel().getIdLong();
            long gid = e.getGuild().getIdLong();
            prefix = settings.prefix.getEffective(cache, cid, gid);
            lang = settings.lang.getEffective(cache, cid, gid);
            Member sm = e.getGuild().getSelfMember();
            // No permissions or guild banned? Don't send message
            if (!sm.hasPermission(e.getTextChannel(), Permission.MESSAGE_WRITE) ||
                    cache.getGuild(gid).isBanned()) {
                return;
            }
            TextChannel tc = e.getTextChannel();
            canEmbed = sm.hasPermission(tc, Permission.MESSAGE_EMBED_LINKS);
        } else if (e.isFromType(ChannelType.PRIVATE)) {
            DbUser dbUser = cache.getUser(e.getAuthor().getIdLong());
            prefix = settings.prefix.getEffective(dbUser);
            lang = settings.lang.getEffective(dbUser);
        } else {
            return;
        }

        // Check if message can be acted upon
        User a = m.getAuthor();
        DbUser dbUser = cache.getUser(a.getIdLong());
        if (a.isBot() || dbUser.isBanned()) {
            return;
        }

        String name = null;
        String[] args = null;

        // If the message is a valid command
        SelfUser su = e.getJDA().getSelfUser();
        FlagConfig fc = config.getFlagConfig();
        String[] content = MessageUtils.getContent(m, prefix, su, fc.isRespondToMentions());
        if (content != null) {

            // Extract name and argument list
            name = content[0];
            if ("".equals(name)) return; //If there is a space after prefix, don't process any more
            args = Arrays.copyOfRange(content, 1, content.length);

        // TODO temporarily (probably permanently) disabled
        // If the bot is mentioned and does not mention everyone
//        } else if (m.isMentioned(e.getJDA().getSelfUser(), MentionType.USER) && e.isFromGuild()) {
//
//            // Send the message to the logging channel
//            EmbedBuilder eb = new EmbedBuilder();
//            eb.setAuthor(a.getId() + " (" + a.getId() + ")", null, a.getEffectiveAvatarUrl());
//            eb.setDescription("**`" + e.getGuild().getId() + "`** (" +
//                e.getGuild().getId() + ") in channel `" + e.getChannel().getId() +
//                "` (" + e.getChannel().getId() + ")\n" + m.getContentDisplay());
//            MessageUtils.log(eb.build());
//            return;

        // If none of the above are satisfied, get out
        } else {
            return;
        }

        // Embed links is required for 90% of commands, so send a message if the bot does not have it.
        if (!canEmbed) {
            e.getChannel().sendMessage(":warning: I need Embed Links permissions to use commands!").queue();
            return;
        }

        // Get command info if the command has been registered
        Optional<Command> cmdOpt = registry.getCommand(name, lang);
        if (!cmdOpt.isPresent()) return;
        Command cmd = cmdOpt.get();

        // Check for elevation
        boolean isElevated = dbUser.isElevated();

        // Run command
        CommandContext ctx = new CommandContext(args, e, config, bot, cmd, commandExecutor, isElevated, prefix, lang);
        commandExecutor.run(cmd, ctx);
    }

}
