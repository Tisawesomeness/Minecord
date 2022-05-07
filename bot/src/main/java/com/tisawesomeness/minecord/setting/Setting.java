package com.tisawesomeness.minecord.setting;

import com.tisawesomeness.minecord.command.meta.CommandContext;
import com.tisawesomeness.minecord.database.DatabaseCache;
import com.tisawesomeness.minecord.database.dao.SettingContainer;
import com.tisawesomeness.minecord.share.util.Validation;

import lombok.NonNull;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.SQLException;
import java.util.Optional;

/**
 * A bot setting that can be changed per guild and per channel.
 * <br>If there is no setting value for a channel, the guild value is used instead.
 * @param <T> The type of the setting
 */
public abstract class Setting<T> implements ISetting<T> {

    /**
     * Gets the current value of this setting.
     * @param obj The guild or channel to lookup
     * @return The value of the setting, or empty if unset
     */
    public abstract Optional<T> get(@NonNull SettingContainer obj);
    /**
     * Changes the value of this setting.
     * @param obj The guild or channel to change
     * @throws SQLException If the setting couldn't be changed due to an error
     */
    public abstract void set(@NonNull SettingContainer obj, @NonNull T setting) throws SQLException;
    /**
     * Resets the value of this setting.
     * <br>If successful, {@link #get(SettingContainer)} should return {@link Optional#empty()}.
     * @param obj The guild or channel to reset
     * @throws SQLException If the setting couldn't be reset due to an error
     */
    public abstract void reset(@NonNull SettingContainer obj) throws SQLException;

    /**
     * Gets the default value for this setting, usually defined by config.
     */
    public abstract @NonNull T getDefault();

    /**
     * Gets the value of this setting for a channel.
     * <br>If there is no setting value for a channel, the guild value is used instead.
     * @param cache The database cache to pull from
     * @param channelId The channel ID
     * @param guildId The guild ID
     * @return The value of the setting, or empty if unset
     */
    public Optional<T> get(DatabaseCache cache, long channelId, long guildId) {
        Optional<T> setting = get(cache.getChannel(channelId, guildId));
        if (setting.isPresent()) {
            return setting;
        }
        return get(cache.getGuild(guildId));
    }
    /**
     * Gets the value of this setting used in the provided context.
     * @param ctx The context of the executing command
     * @return The value of the setting, or empty if unset
     */
    public Optional<T> get(@NonNull CommandContext ctx) {
        MessageReceivedEvent e = ctx.getE();
        if (e.isFromGuild()) {
            return get(ctx.getCache(), e.getTextChannel().getIdLong(), e.getGuild().getIdLong());
        }
        return Optional.empty();
    }

    /**
     * Gets the current value of this setting.
     * @param obj The guild or channel to lookup
     * @return The value of the setting, or the default if unset
     */
    public T getEffective(@NonNull SettingContainer obj) {
        return get(obj).orElse(getDefault());
    }
    /**
     * Gets the current value of this setting for a channel.
     * @param cache The database cache to pull from
     * @param channelId The channel ID
     * @param guildId The guild ID
     * @return The value of the setting, or the default if unset
     */
    public @NonNull T getEffective(DatabaseCache cache, long channelId, long guildId) {
        return get(cache, channelId, guildId).orElse(getDefault());
    }
    /**
     * Gets the value of this setting used in the provided context.
     * @param ctx The context of the executing command
     * @return The value of the setting, or the default if unset
     */
    public @NonNull T getEffective(@NonNull CommandContext ctx) {
        return get(ctx).orElse(getDefault());
    }

    /**
     * Transforms a setting value into a user-friendly string.
     * @param setting The setting value from {@link Setting#get(SettingContainer)}
     * @return The string used in {@code &settings} messages
     */
    public @NonNull String display(T setting) {
        return setting.toString();
    }
    /**
     * Gets the current user-readable value of this setting.
     * @param obj The guild or channel to lookup
     * @return The value of the setting converted to a string
     */
    public @NonNull String getDisplay(@NonNull SettingContainer obj) {
        return get(obj).map(this::display).orElse("unset");
    }
    /**
     * Gets the current user-readable value of this setting for a channel.
     * @param cache The database cache to pull from
     * @param channelId The channel ID
     * @param guildId The guild ID
     * @return The value of the setting converted to a string
     */
    public @NonNull String getDisplay(DatabaseCache cache, long channelId, long guildId) {
        return get(cache, channelId, guildId).map(this::display).orElse("unset");
    }
    /**
     * Gets the current user-readable value of this setting in the current context.
     * @param ctx The context of the executing command
     * @return The value of the setting converted to a string
     */
    public @NonNull String getDisplay(@NonNull CommandContext ctx) {
        return get(ctx).map(this::display).orElse("unset");
    }

    /**
     * Attempts to change this setting for the given object based on user input.
     * <br>Use {@link Validation#isValid()} to determine if the attempt was successful.
     * @param obj The guild or channel to try to set
     * @param input User input that will be parsed into a setting value
     * @return Either a success message, or an error message
     * @throws SQLException If a database error occurs
     */
    public @NonNull Validation<String> tryToSet(SettingContainer obj, @NonNull String input) throws SQLException {
        Optional<T> from = get(obj);
        Validation<T> toValidation = resolve(input);
        if (!toValidation.isValid()) {
            return Validation.propogateError(toValidation);
        }
        T to = toValidation.getValue();

        String name = getDisplayName();
        String fromStr = display(from.orElse(getDefault()));
        String toStr = display(to);
        return tryToSetInternal(obj, from, to).toValidation(name, fromStr, toStr);
    }
    private @NonNull SetStatus tryToSetInternal(SettingContainer obj, Optional<T> from, T to) throws SQLException {
        if (from.isPresent()) {
            if (to.equals(getDefault())) {
                set(obj, to);
                return SetStatus.SET_FROM_TO_DEFAULT;
            } else if (to.equals(from.get())) {
                return SetStatus.SET_NO_CHANGE;
            }
        } else if (to.equals(getDefault())) {
            set(obj, to);
            return SetStatus.SET_TO_DEFAULT;
        }
        set(obj, to);
        return SetStatus.SET;
    }
    /**
     * Attempts to reset this setting for the given object.
     * <br>Use {@link Validation#isValid()} to determine if the attempt was successful.
     * @param obj The guild or channel to try to reset
     * @return Either a success message, or an error message
     * @throws SQLException If a database error occurs
     */
    public @NonNull Validation<String> tryToReset(SettingContainer obj) throws SQLException {
        Optional<T> from = get(obj);
        String name = getDisplayName();
        String fromStr = display(from.orElse(getDefault()));
        return tryToResetInternal(obj, from).toValidation(name, fromStr, "");
    }
    private @NonNull SetStatus tryToResetInternal(SettingContainer obj, Optional<T> from) throws SQLException {
        if (!from.isPresent()) {
            return SetStatus.RESET_NO_CHANGE;
        } else if (from.get().equals(getDefault())) {
            reset(obj);
            return SetStatus.RESET_FROM_DEFAULT;
        }
        reset(obj);
        return SetStatus.RESET;
    }

}
