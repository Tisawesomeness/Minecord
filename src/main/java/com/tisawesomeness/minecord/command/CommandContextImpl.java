package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Lang;
import com.tisawesomeness.minecord.command.misc.HelpCommand;
import com.tisawesomeness.minecord.config.serial.Config;
import com.tisawesomeness.minecord.database.DatabaseCache;
import com.tisawesomeness.minecord.database.dao.DbChannel;
import com.tisawesomeness.minecord.database.dao.DbGuild;
import com.tisawesomeness.minecord.database.dao.DbUser;
import com.tisawesomeness.minecord.setting.impl.UseMenusSetting;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Contains the information needed for a command to execute in a Discord channel.
 */
@RequiredArgsConstructor
public class CommandContextImpl implements CommandContext {

    @Getter private final @NonNull String[] args;
    @Getter private final @NonNull MessageReceivedEvent e;
    @Getter private final @NonNull Config config;
    @Getter private final @NonNull Bot bot;
    @Getter private final @NonNull Command cmd;
    @Getter private final @NonNull CommandExecutor executor;
    @Getter private final boolean isElevated;
    // These settings are used often and calculated beforehand, so simply passing their values makes sense
    @Getter private final @NonNull String prefix;
    @Getter private final @NonNull Lang lang;
    // These settings are only used occasionally, it's best to pass the setting and evaluate when needed
    private final @NonNull UseMenusSetting useMenusSetting;

    public CommandContextImpl(@NonNull String[] args, @NonNull MessageReceivedEvent e, @NonNull Config config,
                          @NonNull Bot bot, @NonNull Command cmd, @NonNull CommandExecutor executor,
                          boolean isElevated, @NonNull String prefix, @NonNull Lang lang) {
        this.args = args;
        this.e = e;
        this.config = config;
        this.bot = bot;
        this.cmd = cmd;
        this.executor = executor;
        this.isElevated = isElevated;
        this.prefix = prefix;
        this.lang = lang;
        useMenusSetting = bot.getSettings().useMenus;
    }

    public Result reply(CharSequence text) {
        e.getChannel().sendMessage(text).queue();
        return Result.SUCCESS;
    }
    public Result replyRaw(EmbedBuilder eb) {
        e.getChannel().sendMessage(eb.build()).queue();
        return Result.SUCCESS;
    }
    public Result reply(EmbedBuilder eb) {
        return replyRaw(brand(eb));
    }
    public Result showHelp() {
        reply(HelpCommand.showHelp(this, cmd));
        return Result.HELP;
    }

    public Result sendResult(Result result, CharSequence text) {
        String msg = result.addEmote(text, lang);
        e.getChannel().sendMessage(msg).queue();
        return result;
    }

    public void triggerCooldown() {
        if (!executor.shouldSkipCooldown(this)) {
            executor.startCooldown(cmd, e.getAuthor().getIdLong());
        }
    }

    public EmbedBuilder addFooter(EmbedBuilder eb) {
        if (config.getFlagConfig().isUseAnnouncements()) {
            return eb.setFooter(bot.getAnnounceRegistry().roll(bot.getShardManager()));
        }
        User author = e.getAuthor();
        String requestedBy = "Requested by " + author.getAsTag();
        return eb.setFooter(requestedBy, author.getEffectiveAvatarUrl())
                .setTimestamp(Instant.now());
        // TODO temporarily disabled (change to static image)
//        if (Config.getOwner().equals("0")) {
//            return eb.setFooter(announcement);
//        }
//        User owner = Bot.shardManager.retrieveUserById(Config.getOwner()).complete();
//        return eb.setFooter(announcement, owner.getAvatarUrl());
    }
    public EmbedBuilder brand(EmbedBuilder eb) {
        return addFooter(eb).setColor(Bot.color);
    }

    public boolean userHasPermission(Permission... permissions) {
        if (isElevated()) {
            return true;
        }
        if (!e.isFromGuild()) {
            throw new IllegalStateException("Permisssions can only be checked in commands sent from guilds.");
        }
        return Objects.requireNonNull(e.getMember()).hasPermission(e.getTextChannel(), permissions);
    }
    public boolean botHasPermission(Permission... permissions) {
        return e.getGuild().getSelfMember().hasPermission(e.getTextChannel(), permissions);
    }

    public boolean shouldUseMenus() {
        return useMenusSetting.getEffective(this);
    }

    public void log(String m) {
        bot.log(m);
    }
    public void log(Message m) {
        bot.log(m);
    }
    public void log(MessageEmbed m) {
        bot.log(m);
    }

}
