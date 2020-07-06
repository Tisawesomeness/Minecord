package com.tisawesomeness.minecord.setting;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.database.DatabaseCache;
import com.tisawesomeness.minecord.database.SettingContainer;
import com.tisawesomeness.minecord.util.type.Validation;

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
     * Gets the current value of this setting.
     * @param obj The guild or channel to lookup
     * @return The value of the setting, or the default if unset
     */
    public T getEffective(@NonNull SettingContainer obj) {
        return get(obj).orElse(getDefault());
    }
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
     * Gets the value of this setting used in the provided context.
     * <br>If there is no setting value for a channel, the guild value is used instead.
     * @param e The event that triggered the executing command
     * @param cache The database cache to pull from
     * @return The value of the setting, or empty if unset
     */
    public Optional<T> get(@NonNull MessageReceivedEvent e, DatabaseCache cache) {
        long gid = e.getGuild().getIdLong();
        if (e.isFromGuild()) {
            Optional<T> setting = get(cache.getChannel(e.getChannel().getIdLong(), gid));
            if (setting.isPresent()) {
                return setting;
            }
            return get(cache.getGuild(gid));
        }
        return Optional.empty();
    }
    /**
     * Gets the value of this setting used in the provided context.
     * @param txt The context of the executing command
     * @return The value of the setting, or empty if unset
     */
    public Optional<T> get(@NonNull CommandContext txt) {
        return get(txt.e, txt.bot.getDatabase().getCache());
    }
    /**
     * Gets the value of this setting used in the provided context.
     * @param e The event that triggered the executing command
     * @param cache The database cache to pull from
     * @return The value of the setting, or the default if unset
     */
    public @NonNull T getEffective(@NonNull MessageReceivedEvent e, DatabaseCache cache) {
        return get(e, cache).orElse(getDefault());
    }
    /**
     * Gets the value of this setting used in the provided context.
     * @param txt The context of the executing command
     * @return The value of the setting, or the default if unset
     */
    public @NonNull T getEffective(@NonNull CommandContext txt) {
        return get(txt).orElse(getDefault());
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
        String fromStr = from.orElse(getDefault()).toString();
        String toStr = to.toString();
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

}
