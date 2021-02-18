package com.tisawesomeness.minecord.setting.parse;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.database.dao.DbChannel;
import com.tisawesomeness.minecord.util.DiscordUtils;
import com.tisawesomeness.minecord.util.ListUtils;

import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;
import java.util.Optional;

/**
 * Parses the context arg, picks a subcommand, or ends the chain if the context is invalid.
 */
public class SettingContextParser extends SettingCommandHandler {
    private final static List<String> USER_WORDS = ListUtils.of("user", "dm", "dms");
    private final static List<String> GUILD_WORDS = ListUtils.of("guild", "server");

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
     */
    public void parse() {
        String contextArg = ctx.getArgs()[currentArg];
        currentArg++;
        if (type == SettingCommandType.QUERY) {
            if ("list".equalsIgnoreCase(contextArg)) {
                new ListSubcommand(this).parse();
                return;
            }
        } else if (!isAdmin && !userHasManageServerPermissions()) {
            ctx.noUserPermissions("You must have Manage Server permissions.");
            return;
        }
        Optional<SettingContext> settingContextOpt = getContext(contextArg);
        if (settingContextOpt.isPresent()) {
            settingContextOpt.get().parse();
            return;
        }
        currentArg--;
        parseFallthrough();
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

    private void parseFallthrough() {
        if (isAdmin && type == SettingCommandType.QUERY) {
            parseChannelAndDisplay();
            return;
        }
        ctx.invalidArgs("Incorrect context.");
    }

    private void parseChannelAndDisplay() {
        String contextArg = ctx.getArgs()[currentArg];
        if (DiscordUtils.isDiscordId(contextArg)) {
            displayChannelIdSettings(contextArg);
            return;
        }
        displayChannelMentionSettings();
    }
    private void displayChannelIdSettings(String contextArg) {
        long cid = Long.parseLong(contextArg);
        Optional<DbChannel> dbChannelOpt = ctx.getChannel(cid);
        if (dbChannelOpt.isPresent()) {
            DbChannel dbChannel = dbChannelOpt.get();
            ctx.triggerCooldown();
            displayCurrentSettings(cid, dbChannel.getGuildId());
            return;
        }
        TextChannel c = ctx.getBot().getShardManager().getTextChannelById(cid);
        if (c != null) {
            ctx.triggerCooldown();
            displayCurrentSettings(c);
            return;
        }
        ctx.warn("That channel is not visible to the bot.");
    }
    private void displayChannelMentionSettings() {
        List<TextChannel> mentioned = ctx.getE().getMessage().getMentionedChannels();
        if (!mentioned.isEmpty()) {
            TextChannel c = mentioned.get(0);
            ctx.triggerCooldown();
            displayCurrentSettings(c);
            return;
        }
        ctx.invalidArgs("Not a valid channel format. Use a `#channel` mention or a valid ID.");
    }

    private void displayCurrentSettings(TextChannel c) {
        displayCurrentSettings(c.getIdLong(), c.getGuild().getIdLong());
    }
    private void displayCurrentSettings(long cid, long gid) {
        String title = "Currently Active Settings for Channel " + cid;
        displaySettings(title, s -> s.getDisplay(ctx.getCache(), cid, gid));
    }
}
