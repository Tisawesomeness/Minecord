package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Lang;
import com.tisawesomeness.minecord.command.misc.HelpCommand;
import com.tisawesomeness.minecord.config.serial.Config;

import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.Instant;
import java.util.Collection;
import java.util.Objects;

/**
 * Contains the information needed for a command to execute in a Discord channel.
 */
public class DiscordContext implements CommandContext {

    @Getter private final String[] args;
    @Getter private final @NonNull MessageReceivedEvent e;
    @Getter private final @NonNull Config config;
    @Getter private final @NonNull Bot bot;
    @Getter private final @NonNull Command cmd;
    @Getter private final @NonNull CommandExecutor executor;
    @Getter private final boolean isElevated;
    // These settings are used often and calculated beforehand, so simply passing their values makes sense
    @Getter private final @NonNull String prefix;
    @Getter private final Lang lang;

    public DiscordContext(@NonNull String[] args, @NonNull MessageReceivedEvent e, @NonNull Config config,
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
    }

    public Result reply(@NonNull CharSequence text) {
        e.getChannel().sendMessage(text).queue();
        return Result.SUCCESS;
    }
    public Result replyRaw(@NonNull EmbedBuilder eb) {
        e.getChannel().sendMessage(eb.build()).queue();
        return Result.SUCCESS;
    }
    public Result showHelp() {
        reply(HelpCommand.showHelp(this, cmd));
        return Result.HELP;
    }

    public Result sendResult(Result result, @NonNull CharSequence text) {
        String msg = result.addEmote(text, lang);
        e.getChannel().sendMessage(msg).queue();
        return result;
    }

    public void triggerCooldown() {
        if (!executor.shouldSkipCooldown(this)) {
            executor.startCooldown(cmd, e.getAuthor().getIdLong());
        }
    }

    public @NonNull EmbedBuilder addFooter(@NonNull EmbedBuilder eb) {
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
    public @NonNull EmbedBuilder brand(@NonNull EmbedBuilder eb) {
        return addFooter(eb).setColor(Bot.color);
    }

    public boolean userHasPermission(Permission... permissions) {
        if (isElevated) {
            return true;
        }
        if (!isFromGuild()) {
            throw new IllegalStateException("Permisssions can only be checked in commands sent from guilds.");
        }
        return Objects.requireNonNull(e.getMember()).hasPermission(e.getTextChannel(), permissions);
    }
    public boolean userHasPermission(Collection<Permission> permissions) {
        if (isElevated) {
            return true;
        }
        if (!isFromGuild()) {
            throw new IllegalStateException("Permisssions can only be checked in commands sent from guilds.");
        }
        return Objects.requireNonNull(e.getMember()).hasPermission(e.getTextChannel(), permissions);
    }
    public boolean botHasPermission(Permission... permissions) {
        if (!isFromGuild()) {
            throw new IllegalStateException("Permisssions can only be checked in commands sent from guilds.");
        }
        return e.getGuild().getSelfMember().hasPermission(e.getTextChannel(), permissions);
    }
    public boolean botHasPermission(Collection<Permission> permissions) {
        if (!isFromGuild()) {
            throw new IllegalStateException("Permisssions can only be checked in commands sent from guilds.");
        }
        return e.getGuild().getSelfMember().hasPermission(e.getTextChannel(), permissions);
    }

    public boolean isFromGuild() {
        return e.isFromGuild();
    }

    public boolean shouldUseMenus() {
        return bot.getSettings().useMenus.getEffective(this);
    }

    public void log(@NonNull String m) {
        bot.log(m);
    }
    public void log(@NonNull Message m) {
        bot.log(m);
    }
    public void log(@NonNull MessageEmbed m) {
        bot.log(m);
    }

}