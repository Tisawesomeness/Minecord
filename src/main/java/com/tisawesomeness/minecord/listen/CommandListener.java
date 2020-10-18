package com.tisawesomeness.minecord.listen;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Lang;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.CommandContextImpl;
import com.tisawesomeness.minecord.command.CommandExecutor;
import com.tisawesomeness.minecord.command.CommandRegistry;
import com.tisawesomeness.minecord.config.serial.Config;
import com.tisawesomeness.minecord.config.serial.FlagConfig;
import com.tisawesomeness.minecord.database.DatabaseCache;
import com.tisawesomeness.minecord.database.dao.CommandStats;
import com.tisawesomeness.minecord.database.dao.DbUser;
import com.tisawesomeness.minecord.setting.SettingRegistry;
import com.tisawesomeness.minecord.util.MessageUtils;

import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.Optional;

public class CommandListener extends ListenerAdapter {

    private final @NonNull Bot bot;
    private final @NonNull Config config;
    private final @NonNull CommandRegistry registry;
    @Getter private final @NonNull CommandExecutor commandExecutor;

    public CommandListener(
            @NonNull Bot bot, @NonNull Config config,
            @NonNull CommandRegistry registry, @NonNull CommandStats commandStats) {
        this.bot = bot;
        this.config = config;
        this.registry = registry;
        commandExecutor = new CommandExecutor(registry, commandStats, config);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        Message m = e.getMessage();
        DatabaseCache cache = bot.getDatabaseCache();

        // Get the settings needed before command execution
        String prefix;
        Lang lang;

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

        // If the message is a valid command
        SelfUser su = e.getJDA().getSelfUser();
        FlagConfig fc = config.getFlagConfig();
        String[] content = MessageUtils.getContent(m, prefix, su, fc.isRespondToMentions());
        if (content == null) {
            return;
        }
        // Extract name and argument list
        String name = content[0];
        if (name.isEmpty()) {
            return; //If there is a space after prefix, it's not a command
        }
        String[] args = Arrays.copyOfRange(content, 1, content.length);

        // Get command info if the command has been registered
        Optional<Command> cmdOpt = registry.getCommand(name, lang);
        if (!cmdOpt.isPresent()) {
            return;
        }
        Command cmd = cmdOpt.get();

        // Check for elevation
        boolean isElevated = dbUser.isElevated();

        // Run command
        CommandContext ctx = new CommandContextImpl(args, e, config, bot, cmd, commandExecutor, isElevated, prefix, lang);
        commandExecutor.run(cmd, ctx);
    }

}
