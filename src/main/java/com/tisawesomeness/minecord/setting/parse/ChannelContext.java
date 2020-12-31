package com.tisawesomeness.minecord.setting.parse;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.Result;
import com.tisawesomeness.minecord.database.dao.DbChannel;
import com.tisawesomeness.minecord.util.DiscordUtils;
import com.tisawesomeness.minecord.util.type.Either;

import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Parses the user context, setting values with the highest priority.
 */
public class ChannelContext extends SettingContext {

    private static final String INVALID_CHANNEL_ERROR = "Not a valid channel format. Use a `#channel` mention or a valid ID.";

    @Getter private final @NonNull CommandContext ctx;
    @Getter private final @NonNull SettingCommandType type;
    private final boolean isAdmin;
    @Getter private int currentArg;

    public ChannelContext(SettingContextParser prev) {
        ctx = prev.getCtx();
        type = prev.getType();
        isAdmin = prev.isAdmin();
        currentArg = prev.getCurrentArg();
    }

    /**
     * Reads the channel id if this is an admin command, or tries to get the specified channel if not in DMs.
     * <br>The channel can be from an id or mention as long as it is within the same guild.
     * <br>If from a query command, specifying no channel defaults to the current one.
     * <br>The channel id is then saved as the context,
     * and is either displayed ({@code &settings}) or changed ({@code &set}/{@code &reset}).
     */
    public void parse() {
        if (isAdmin) {
            parseAdmin();
            return;
        }
        parseNotAdmin();
    }

    private void parseNotAdmin() {
        if (!ctx.isFromGuild()) {
            ctx.warn(String.format("`%ssettings channel` cannot be used in DMs.", ctx.getPrefix()));
            return;
        }
        if (currentArg >= ctx.getArgs().length) {
            displayCurrentChannelSettingsIfQuery();
            return;
        }
        displayOrParseChannel();
    }
    private void displayCurrentChannelSettingsIfQuery() {
        if (type != SettingCommandType.QUERY) {
            ctx.invalidArgs("You must specify a channel.");
            return;
        }
        TextChannel c = ctx.getE().getTextChannel();
        String title = "Channel settings for #" + c.getName();
        DbChannel channel = ctx.getChannel(c);
        ctx.triggerCooldown();
        displaySettings(title, s -> s.getDisplay(channel));
    }
    private void displayOrParseChannel() {
        String channelArg = ctx.getArgs()[currentArg];
        Either<String, TextChannel> maybeChannel = getChannel(channelArg);
        if (!maybeChannel.isRight()) {
            String msg = maybeChannel.getLeft();
            if (msg.equals(INVALID_CHANNEL_ERROR)) {
                ctx.invalidArgs(msg);
                return;
            }
            ctx.warn(msg);
            return;
        }
        TextChannel c = maybeChannel.getRight();
        currentArg++;
        displayOrParseChannelIfUserHasPermission(c);
    }
    private void displayOrParseChannelIfUserHasPermission(TextChannel c) {
        Member m = Objects.requireNonNull(ctx.getE().getMember());
        if (!m.hasPermission(c, Permission.VIEW_CHANNEL, Permission.MESSAGE_READ)) {
            ctx.warn("That channel does not exist in the current guild or is not visible to you.");
            ctx.commandResult(Result.NO_USER_PERMISSIONS);
            return;
        } else if (!m.hasPermission(c, Permission.MESSAGE_WRITE)) {
            ctx.noUserPermissions("You do not have permission to write in that channel.");
            return;
        }
        DbChannel channel = ctx.getChannel(c);
        displayOrParse("Channel settings for #" + c.getName(), channel);
    }

    private Either<String, TextChannel> getChannel(String input) {
        long gid = ctx.getE().getGuild().getIdLong();
        if (DiscordUtils.isDiscordId(input)) {
            return getChannelFromIdIfInGuild(input, gid);
        }
        return getChannelFromMentions(gid);
    }
    private Either<String, TextChannel> getChannelFromIdIfInGuild(String input, long gid) {
        TextChannel c = ctx.getBot().getShardManager().getTextChannelById(input);
        if (c == null || c.getGuild().getIdLong() != gid) {
            return Either.left("That channel does not exist in the current guild or is not visible to you.");
        }
        return Either.right(c);
    }
    private Either<String, TextChannel> getChannelFromMentions(long gid) {
        List<TextChannel> mentioned = ctx.getE().getMessage().getMentionedChannels();
        if (mentioned.isEmpty()) {
            return Either.left(INVALID_CHANNEL_ERROR);
        }
        TextChannel c = mentioned.get(0);
        if (c.getGuild().getIdLong() != gid) {
            return Either.left(
                    "That channel does not exist in the current guild or is not visible to you.");
        }
        return Either.right(c);
    }

    private void parseAdmin() {
        if (currentArg >= ctx.getArgs().length) {
            ctx.invalidArgs("You must specify a channel id.");
            return;
        }
        displayOrParseChannelId();
    }
    private void displayOrParseChannelId() {
        String channelArg = ctx.getArgs()[currentArg];
        Either<String, Long> maybeCid = getChannelId(channelArg);
        if (!maybeCid.isRight()) {
            ctx.invalidArgs(maybeCid.getLeft());
            return;
        }
        long cid = maybeCid.getRight();
        currentArg++;

        displayOrParseIfChannelIdInDatabase(cid);
    }
    private void displayOrParseIfChannelIdInDatabase(long cid) {
        Optional<DbChannel> channelOpt = ctx.getChannel(cid);
        if (!channelOpt.isPresent()) {
            ctx.warn("That channel is not in the database.");
            return;
        }
        DbChannel channel = channelOpt.get();
        displayOrParse("Channel settings for " + cid, channel);
    }

    private Either<String, Long> getChannelId(String input) {
        if (DiscordUtils.isDiscordId(input)) {
            return Either.right(Long.parseLong(input));
        }
        return getChannelIdFromMentions();
    }
    private Either<String, Long> getChannelIdFromMentions() {
        List<TextChannel> mentioned = ctx.getE().getMessage().getMentionedChannels();
        if (!mentioned.isEmpty()) {
            return Either.right(mentioned.get(0).getIdLong());
        }
        return Either.left(INVALID_CHANNEL_ERROR);
    }
}
