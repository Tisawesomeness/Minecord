package com.tisawesomeness.minecord.command.meta;

import com.tisawesomeness.minecord.Minecord;
import com.tisawesomeness.minecord.command.CommandExecutor;
import com.tisawesomeness.minecord.config.branding.Branding;
import com.tisawesomeness.minecord.config.config.Config;
import com.tisawesomeness.minecord.lang.Lang;
import com.tisawesomeness.minecord.util.Strings;

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
import java.util.Optional;
import java.util.StringJoiner;

/**
 * Contains the information needed for a command to execute in a Discord channel.
 */
public class DiscordContext extends CommandContext {

    @Getter private final String[] args;
    @Getter private final @NonNull MessageReceivedEvent e;
    @Getter private final @NonNull Config config;
    @Getter private final @NonNull Minecord bot;
    @Getter private final @NonNull Command cmd;
    @Getter private final @NonNull CommandExecutor executor;
    @Getter private final boolean isElevated;
    // These settings are used often and calculated beforehand, so simply passing their values makes sense
    @Getter private final @NonNull String rawPrefix;
    @Getter private final Lang lang;

    public DiscordContext(@NonNull String[] args, @NonNull MessageReceivedEvent e, @NonNull Config config,
                          @NonNull Minecord bot, @NonNull Command cmd, @NonNull CommandExecutor executor,
                          boolean isElevated, @NonNull String prefix, Lang lang) {
        this.args = args;
        this.e = e;
        this.config = config;
        this.bot = bot;
        this.cmd = cmd;
        this.executor = executor;
        this.isElevated = isElevated;
        rawPrefix = prefix;
        this.lang = lang;
    }

    protected void sendMessage(@NonNull CharSequence text) {
        e.getChannel().sendMessage(text).queue(this::link);
    }
    protected void sendMessage(@NonNull MessageEmbed emb) {
        e.getChannel().sendMessage(emb).queue(this::link);
    }
    public void requestHelp() {
        sendMessage(brand(cmd.showHelp(this)).build());
    }
    private void link(Message m) {
        executor.link(e.getMessageIdLong(), m.getIdLong());
    }

    public void triggerCooldown() {
        if (!executor.shouldSkipCooldown(this)) {
            executor.startCooldown(cmd, e.getAuthor().getIdLong());
        }
    }

    public @NonNull EmbedBuilder addFooter(@NonNull EmbedBuilder eb) {
        Optional<Branding> brandingConfigOpt = bot.getBrandingConfig();
        if (brandingConfigOpt.isPresent()) {
            Branding brandingConfig = brandingConfigOpt.get();
            if (brandingConfig.getAnnouncementConfig().isEnabled()) {
                Optional<String> announcementOpt = bot.getAnnounceRegistry()
                        .flatMap(ar -> ar.roll(lang, bot.getShardManager()));
                if (announcementOpt.isPresent()) {
                    return eb.setFooter(Strings.safeSubstring(announcementOpt.get(), 0, MessageEmbed.TEXT_MAX_LENGTH));
                }
            }
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

    @Override
    public String toString() {
        String argsStr = args.length == 0 ? "" : " " + joinArgs();
        String cmdStr = String.format("'%s%s'", cmd, argsStr);
        return new StringJoiner("\n  ", DiscordContext.class.getSimpleName() + "{", "\n}")
                .add(cmdStr)
                .add("e=#" + e.getResponseNumber())
                .add("elevated=" + isElevated)
                .add("config=" + config.hashCode()) // Listing all fields of config would be too long
                .add("prefix=`" + rawPrefix + "`")
                .add("lang=" + lang)
                .toString();
    }

}
