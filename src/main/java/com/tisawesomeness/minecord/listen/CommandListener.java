package com.tisawesomeness.minecord.listen;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.CommandExecutor;
import com.tisawesomeness.minecord.command.CommandRegistry;
import com.tisawesomeness.minecord.command.meta.Command;
import com.tisawesomeness.minecord.command.meta.CommandContext;
import com.tisawesomeness.minecord.command.meta.DiscordContext;
import com.tisawesomeness.minecord.config.serial.Config;
import com.tisawesomeness.minecord.database.DatabaseCache;
import com.tisawesomeness.minecord.database.dao.CommandStats;
import com.tisawesomeness.minecord.database.dao.DbUser;
import com.tisawesomeness.minecord.lang.Lang;
import com.tisawesomeness.minecord.setting.SettingRegistry;
import com.tisawesomeness.minecord.util.Discord;
import com.tisawesomeness.minecord.util.type.ListValuedEnumMap;

import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

public class CommandListener extends ListenerAdapter {

    private final @NonNull Bot bot;
    private final @NonNull Config config;
    private final @NonNull CommandRegistry registry;
    @Getter private final @NonNull CommandExecutor commandExecutor;
    private final ListValuedEnumMap<Lang, String> prefixQuestions;

    public CommandListener(@NonNull Bot bot, @NonNull Config config,
            @NonNull CommandRegistry registry, @NonNull CommandStats commandStats) {
        this.bot = bot;
        this.config = config;
        this.registry = registry;
        commandExecutor = new CommandExecutor(registry, commandStats, config);
        prefixQuestions = buildPrefixQuestionsMap(); // Pre-loading prefix questions since it will be queried a lot
    }
    private static ListValuedEnumMap<Lang, String> buildPrefixQuestionsMap() {
        ListValuedEnumMap<Lang, String> prefixQuestions;
        prefixQuestions = ListValuedEnumMap.create(Lang.class);
        for (Lang lang : Lang.values()) {
            Collection<String> temp = new HashSet<>(lang.i18nList("command.meta.prefixQuestions"));
            if (lang != Lang.getDefault()) {
                temp.addAll(Lang.getDefault().i18nList("command.meta.prefixQuestions"));
            }
            prefixQuestions.putAll(lang, temp);
        }
        return prefixQuestions;
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

        // Command string is empty if only the prefix or mention is typed
        if (commandString.isEmpty()) {
            // If "@Minecord" and nothing else is typed, show help for new users
            if (e.getMessage().getContentRaw().startsWith("<@")) {
                runHelp(e, prefix, lang, isElevated);
            }
            return;
        }
        // Some people expect the bot to respond to "@Minecord what's your prefix"
        if (lang.containsIgnoreCase(prefixQuestions.get(lang), commandString)) {
            String prefixMsg = lang.i18nf("command.meta.currentPrefix", MarkdownUtil.monospace(prefix));
            e.getChannel().sendMessage(prefixMsg).queue();
            return;
        }

        Optional<Command> cmdOpt = registry.getCommand(commandName, lang);
        if (!cmdOpt.isPresent()) {
            return;
        }
        Command cmd = cmdOpt.get();
        String[] args = getArgs(commandString, nameArgsSeparatorIndex);

        CommandContext ctx = new DiscordContext(args, e, config, bot, cmd, commandExecutor, isElevated, prefix, lang);
        commandExecutor.run(ctx);
    }
    private Optional<String> getCommandString(MessageReceivedEvent e, String prefix) {
        boolean respondToMentions = config.getFlagConfig().isRespondToMentions();
        String selfId = e.getJDA().getSelfUser().getId();
        Discord.ParseOptions options = Discord.parseOptionsBuilder()
                .respondToMentions(respondToMentions, selfId)
                .prefixRequired(e.isFromGuild())
                .build();
        return Discord.parseCommand(e.getMessage().getContentRaw(), prefix, options);
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

    private void runHelp(MessageReceivedEvent e, String prefix, Lang lang, boolean isElevated) {
        String[] args = new String[0];
        Command cmd = registry.getHelpCommand();
        CommandContext ctx = new DiscordContext(args, e, config, bot, cmd, commandExecutor, isElevated, prefix, lang);
        commandExecutor.run(ctx);
    }

}
