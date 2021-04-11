package com.tisawesomeness.minecord.listen;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.*;
import com.tisawesomeness.minecord.config.serial.Config;
import com.tisawesomeness.minecord.database.DatabaseCache;
import com.tisawesomeness.minecord.database.dao.CommandStats;
import com.tisawesomeness.minecord.database.dao.DbUser;
import com.tisawesomeness.minecord.lang.Lang;
import com.tisawesomeness.minecord.setting.SettingRegistry;
import com.tisawesomeness.minecord.util.DiscordUtils;

import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Optional;

public class CommandListener extends ListenerAdapter {

    private final @NonNull Bot bot;
    private final @NonNull Config config;
    private final @NonNull CommandRegistry registry;
    @Getter private final @NonNull CommandExecutor commandExecutor;

    public CommandListener(@NonNull Bot bot, @NonNull Config config,
            @NonNull CommandRegistry registry, @NonNull CommandStats commandStats) {
        this.bot = bot;
        this.config = config;
        this.registry = registry;
        commandExecutor = new CommandExecutor(registry, commandStats, config);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        SettingRegistry settings = bot.getSettings();
        DatabaseCache cache = bot.getDatabaseCache();

        // Ignore all messages from bots, very important and highly recommended
        User author = e.getAuthor();
        if (author.isBot()) {
            return;
        }
        DbUser dbUser = cache.getUser(author.getIdLong());
        if (dbUser.isBanned()) {
            return;
        }
        boolean isElevated = dbUser.isElevated();

        if (e.isFromType(ChannelType.TEXT)) {
            if (canSendGuildMessage(e)) {
                long cid = e.getTextChannel().getIdLong();
                long gid = e.getGuild().getIdLong();
                String prefix = settings.prefix.getEffective(cache, cid, gid);
                Lang lang = settings.lang.getEffective(cache, cid, gid);
                processCommandMessage(e, prefix, lang, isElevated);
            }
        } else if (e.isFromType(ChannelType.PRIVATE)) {
            String prefix = settings.prefix.getEffective(dbUser);
            Lang lang = settings.lang.getEffective(dbUser);
            processCommandMessage(e, prefix, lang, isElevated);
        }
    }
    private boolean canSendGuildMessage(MessageReceivedEvent e) {
        Member sm = e.getGuild().getSelfMember();
        DatabaseCache cache = bot.getDatabaseCache();
        long gid = e.getGuild().getIdLong();
        return sm.hasPermission(e.getTextChannel(), Permission.MESSAGE_WRITE) && !cache.getGuild(gid).isBanned();
    }

    private void processCommandMessage(MessageReceivedEvent e, String prefix, Lang lang, boolean isElevated) {
        // Converts "&profile Dinnerbone" or "@Minecord profile Dinnerbone" to "profile Dinnerbone"
        Optional<String> commandStringOpt = getCommandString(e, prefix);
        if (!commandStringOpt.isPresent()) {
            return;
        }
        String commandString = commandStringOpt.get();
        int nameArgsSeparatorIndex = getSeparatorIndex(commandString);
        String commandName = commandString.substring(0, nameArgsSeparatorIndex);

        // Name is empty if there is a space after the prefix
        if (commandName.isEmpty()) {
            return;
        }
        Optional<Command> cmdOpt = registry.getCommand(commandName, lang);
        if (!cmdOpt.isPresent()) {
            return;
        }
        Command cmd = cmdOpt.get();
        String[] args = getArgs(commandString, nameArgsSeparatorIndex);

        CommandContext ctx = new DiscordContext(args, e, config, bot, cmd, commandExecutor, isElevated, prefix, lang);
        commandExecutor.run(cmd, ctx);
    }
    private Optional<String> getCommandString(MessageReceivedEvent e, String prefix) {
        String content = e.getMessage().getContentRaw();
        String selfId = e.getJDA().getSelfUser().getId();
        boolean respondToMentions = config.getFlagConfig().isRespondToMentions();

        return DiscordUtils.parseCommand(content, prefix, selfId, respondToMentions);
    }
    private static int getSeparatorIndex(String commandPart) {
        int spaceIndex = commandPart.indexOf(' ');
        if (spaceIndex == -1) {
            return commandPart.length();
        }
        return spaceIndex;
    }
    private static String[] getArgs(String commandString, int nameArgsSeparatorIndex) {
        if (nameArgsSeparatorIndex + 1 > commandString.length()) {
            return new String[0];
        }
        return commandString.substring(nameArgsSeparatorIndex + 1).split(" ");
    }

}
