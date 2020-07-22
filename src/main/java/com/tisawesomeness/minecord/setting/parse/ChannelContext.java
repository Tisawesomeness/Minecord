package com.tisawesomeness.minecord.setting.parse;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
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
    @Getter private final @NonNull CommandContext txt;
    @Getter private final @NonNull SettingCommandType type;
    private final boolean isAdmin;
    @Getter private int currentArg;

    public ChannelContext(SettingContextParser prev) {
        txt = prev.getTxt();
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
     * @return The result of the command
     */
    public Command.Result parse() {
        return isAdmin ? parseAdmin() : parseNotAdmin();
    }

    private Command.Result parseNotAdmin() {
        if (!txt.e.isFromGuild()) {
            return new Command.Result(Command.Outcome.WARNING,
                    String.format(":warning: `%ssettings channel` cannot be used in DMs.", txt.prefix));
        }
        if (currentArg >= txt.args.length) {
            return displayCurrentChannelSettingsIfQuery();
        }
        return displayOrParseChannel();
    }
    private Command.Result displayCurrentChannelSettingsIfQuery() {
        if (type != SettingCommandType.QUERY) {
            return new Command.Result(Command.Outcome.WARNING, ":warning: You must specify a channel.");
        }
        TextChannel c = txt.e.getTextChannel();
        String title = "Channel settings for #" + c.getName();
        DbChannel channel = txt.getChannel(c.getIdLong(), txt.e.getGuild().getIdLong());
        return displaySettings(title, s -> s.getDisplay(channel));
    }
    private Command.Result displayOrParseChannel() {
        String channelArg = txt.args[currentArg];
        Either<String, TextChannel> maybeChannel = getChannel(channelArg);
        if (!maybeChannel.isRight()) {
            return eitherToWarning(maybeChannel);
        }
        TextChannel c = maybeChannel.getRight();
        currentArg++;
        return displayOrParseChannelIfUserHasPermission(c);
    }
    private Command.Result displayOrParseChannelIfUserHasPermission(TextChannel c) {
        Member m = Objects.requireNonNull(txt.e.getMember());
        if (m.hasPermission(c, Permission.VIEW_CHANNEL, Permission.MESSAGE_READ)) {
            return new Command.Result(Command.Outcome.WARNING,
                    "That channel does not exist in the current guild or is not visible to you.");
        } else if (m.hasPermission(c, Permission.MESSAGE_WRITE)) {
            return new Command.Result(Command.Outcome.WARNING,
                    ":warning: You do not have permission to write in that channel.");
        }
        DbChannel channel = txt.getChannel(c.getIdLong(), txt.e.getGuild().getIdLong());
        return displayOrParse("Channel settings for #" + c.getName(), channel);
    }

    private Either<String, TextChannel> getChannel(String input) {
        long gid = txt.e.getGuild().getIdLong();
        if (DiscordUtils.isDiscordId(input)) {
            return getChannelFromIdIfInGuild(input, gid);
        }
        return getChannelFromMentions(gid);
    }
    private Either<String, TextChannel> getChannelFromIdIfInGuild(String input, long gid) {
        TextChannel c = txt.bot.getShardManager().getTextChannelById(input);
        if (c == null || c.getGuild().getIdLong() != gid) {
            return Either.left("That channel does not exist in the current guild or is not visible to you.");
        }
        return Either.right(c);
    }
    private Either<String, TextChannel> getChannelFromMentions(long gid) {
        List<TextChannel> mentioned = txt.e.getMessage().getMentionedChannels();
        if (mentioned.isEmpty()) {
            return Either.left(
                    "Not a valid channel format. Use a `#channel` mention or a valid ID.");
        }
        TextChannel c = mentioned.get(0);
        if (c.getGuild().getIdLong() != gid) {
            return Either.left(
                    "That channel does not exist in the current guild or is not visible to you.");
        }
        return Either.right(c);
    }

    private Command.Result parseAdmin() {
        if (currentArg >= txt.args.length) {
            return new Command.Result(Command.Outcome.WARNING, ":warning: You must specify a channel id.");
        }
        return displayOrParseChannelId();
    }
    private Command.Result displayOrParseChannelId() {
        String channelArg = txt.args[currentArg];
        Either<String, Long> maybeCid = getChannelId(channelArg);
        if (!maybeCid.isRight()) {
            return eitherToWarning(maybeCid);
        }
        long cid = maybeCid.getRight();
        currentArg++;

        return displayOrParseIfChannelIdInDatabase(cid);
    }
    private Command.Result displayOrParseIfChannelIdInDatabase(long cid) {
        Optional<DbChannel> channelOpt = txt.getChannel(cid);
        if (!channelOpt.isPresent()) {
            return new Command.Result(Command.Outcome.WARNING, ":warning: That channel is not in the database.");
        }
        DbChannel channel = channelOpt.get();
        return displayOrParse("Channel settings for " + cid, channel);
    }

    private Either<String, Long> getChannelId(String input) {
        if (DiscordUtils.isDiscordId(input)) {
            return Either.right(Long.parseLong(input));
        }
        return getChannelIdFromMentions();
    }
    private Either<String, Long> getChannelIdFromMentions() {
        List<TextChannel> mentioned = txt.e.getMessage().getMentionedChannels();
        if (!mentioned.isEmpty()) {
            return Either.right(mentioned.get(0).getIdLong());
        }
        return Either.left("Not a valid channel format. Use a `#channel` mention or a valid ID.");
    }

    private static Command.Result eitherToWarning(Either<String, ?> either) {
        return new Command.Result(Command.Outcome.WARNING, ":warning: " + either.getLeft());
    }
}
