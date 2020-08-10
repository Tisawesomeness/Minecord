package com.tisawesomeness.minecord.setting.parse;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.Result;
import com.tisawesomeness.minecord.database.dao.DbChannel;
import com.tisawesomeness.minecord.util.DiscordUtils;

import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Parses the context arg, picks a subcommand, or ends the chain if the context is invalid.
 */
public class SettingContextParser extends SettingCommandHandler {
    private final static List<String> USER_WORDS = Arrays.asList("user", "dm", "dms");
    private final static List<String> GUILD_WORDS = Arrays.asList("guild", "server");

    @Getter private final @NonNull CommandContext ctx;
    @Getter private final @NonNull SettingCommandType type;
    @Getter private final boolean isAdmin;
    @Getter private int currentArg;

    public SettingContextParser(SettingCommandParser prev, boolean isAdmin) {
        ctx = prev.getCtx();
        type = prev.getType();
        this.isAdmin = isAdmin;
        currentArg = prev.getCurrentArg();
    }

    /**
     * Parses the context arg of the setting command.
     * <br>May be guild/channel/user, but also {@code &settings list} and {@code &settings admin <channel id>}.
     * <br>If found, parsing is chained to the context.
     * @return The result of this command
     */
    public Result parse() {
        String contextArg = ctx.args[currentArg];
        currentArg++;
        if (type == SettingCommandType.QUERY) {
            if ("list".equalsIgnoreCase(contextArg)) {
                return new ListSubcommand(this).parse();
            }
        } else if (!isAdmin && !SettingCommandHandler.userHasManageServerPermissions(ctx.e)) {
            return ctx.warn("You must have Manage Server permissions.");
        }
        Optional<SettingContext> settingContextOpt = getContext(contextArg);
        if (settingContextOpt.isPresent()) {
            return settingContextOpt.get().parse();
        }
        currentArg--;
        return parseFallthrough();
    }
    private Optional<SettingContext> getContext(String contextArg) {
        if (GUILD_WORDS.contains(contextArg.toLowerCase())) {
            return Optional.of(new GuildContext(this));
        }
        if ("channel".equalsIgnoreCase(contextArg)) {
            return Optional.of(new ChannelContext(this));
        }
        if (USER_WORDS.contains(contextArg.toLowerCase())) {
            return Optional.of(new UserContext(this));
        }
        return Optional.empty();
    }

    private Result parseFallthrough() {
        if (isAdmin && type == SettingCommandType.QUERY) {
            return parseChannelAndDisplay();
        }
        return ctx.warn("Incorrect context.");
    }

    private Result parseChannelAndDisplay() {
        String contextArg = ctx.args[currentArg];
        if (DiscordUtils.isDiscordId(contextArg)) {
            return displayChannelIdSettings(contextArg);
        }
        return displayChannelMentionSettings();
    }
    private Result displayChannelIdSettings(String contextArg) {
        long cid = Long.parseLong(contextArg);
        Optional<DbChannel> dbChannelOpt = ctx.getChannel(cid);
        if (dbChannelOpt.isPresent()) {
            DbChannel dbChannel = dbChannelOpt.get();
            ctx.triggerCooldown();
            return displayCurrentSettings(cid, dbChannel.getGuildId());
        }
        TextChannel c = ctx.bot.getShardManager().getTextChannelById(cid);
        if (c != null) {
            ctx.triggerCooldown();
            return displayCurrentSettings(c);
        }
        return ctx.warn("That channel is not visible to the bot.");
    }
    private Result displayChannelMentionSettings() {
        List<TextChannel> mentioned = ctx.e.getMessage().getMentionedChannels();
        if (!mentioned.isEmpty()) {
            TextChannel c = mentioned.get(0);
            ctx.triggerCooldown();
            return displayCurrentSettings(c);
        }
        return ctx.warn("Not a valid channel format. Use a `#channel` mention or a valid ID.");
    }

    private Result displayCurrentSettings(TextChannel c) {
        return displayCurrentSettings(c.getIdLong(), c.getGuild().getIdLong());
    }
    private Result displayCurrentSettings(long cid, long gid) {
        String title = "Currently Active Settings for Channel " + cid;
        return displaySettings(title, s -> s.getDisplay(ctx.getCache(), cid, gid));
    }
}
