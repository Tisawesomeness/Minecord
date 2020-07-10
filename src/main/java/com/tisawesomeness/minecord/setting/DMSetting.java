package com.tisawesomeness.minecord.setting;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.database.DMSettingContainer;
import com.tisawesomeness.minecord.database.SettingContainer;

import lombok.NonNull;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.SQLException;
import java.util.Optional;

/**
 * A {@link Setting} that can also be changed in DMs.
 * <br>If the message was sent in a DM, the setting value for the user is used.
 * @param <T> The type of the setting
 */
public abstract class DMSetting<T> extends Setting<T> {

    // Since DM settings require a *less* specific container, casting is both necessary and safe
    /**
     * Gets the current value of this setting.
     * @param obj The guild or channel to lookup
     * @return The value of the setting, or empty if unset
     */
    public Optional<T> get(@NonNull SettingContainer obj) {
        return get((DMSettingContainer) obj);
    }
    /**
     * Gets the current value of this setting.
     * @param obj The guild or channel to lookup
     * @return The value of the setting, or the default if unset
     */
    @Override
    public T getEffective(@NonNull SettingContainer obj) {
        return getEffective((DMSettingContainer) obj);
    }
    /**
     * Changes the value of this setting.
     * @param obj The guild or channel to change
     * @throws SQLException If the setting couldn't be changed due to an error
     */
    public void set(@NonNull SettingContainer obj, @NonNull T setting) throws SQLException {
        set((DMSettingContainer) obj, setting);
    }
    /**
     * Resets the value of this setting.
     * <br>If successful, {@link #get(SettingContainer)} should return {@link Optional#empty()}.
     * @param obj The guild or channel to reset
     * @throws SQLException If the setting couldn't be reset due to an error
     */
    public void reset(@NonNull SettingContainer obj) throws SQLException {
        reset((DMSettingContainer) obj);
    }

    /**
     * Gets the current value of this setting.
     * @param obj The guild, channel, or user to lookup
     * @return The value of the setting, or empty if unset
     */
    public abstract Optional<T> get(@NonNull DMSettingContainer obj);
    /**
     * Gets the current value of this setting.
     * @param obj The guild, channel, or user to lookup
     * @return The value of the setting, or the default if unset
     */
    public T getEffective(@NonNull DMSettingContainer obj) {
        return get(obj).orElse(getDefault());
    }
    /**
     * Gets the current user-readable value of this setting.
     * @param obj The guild, channel, or user to lookup
     * @return The value of the setting converted to a string
     */
    public @NonNull String getDisplay(@NonNull DMSettingContainer obj) {
        return get(obj).map(this::display).orElse("unset");
    }
    /**
     * Changes the value of this setting.
     * @param obj The guild, channel, or user to change
     * @throws SQLException If the setting couldn't be changed due to an error
     */
    public abstract void set(@NonNull DMSettingContainer obj, @NonNull T setting) throws SQLException;
    /**
     * Resets the value of this setting.
     * <br>If successful, {@link #get(DMSettingContainer)} should return {@link Optional#empty()}.
     * @param obj The guild, channel, or user to reset
     * @throws SQLException If the setting couldn't be reset due to an error
     */
    public abstract void reset(@NonNull DMSettingContainer obj) throws SQLException;

    /**
     * Gets the value of this setting used in the provided context.
     * <br>If the message was sent in a DM, the setting value for the user is used.
     * <br>Otherwise, {@link Setting#get(CommandContext)} is used.
     * @param txt The context of the executing command
     * @return The value of the setting, or empty if unset
     */
    @Override
    public Optional<T> get(@NonNull CommandContext txt) {
        MessageReceivedEvent e = txt.e;
        if (e.isFromGuild()) {
            return super.get(txt);
        }
        return get(txt.bot.getDatabase().getCache().getUser(e.getAuthor().getIdLong()));
    }

}
