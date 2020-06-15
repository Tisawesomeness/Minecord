package com.tisawesomeness.minecord.setting;

import com.tisawesomeness.minecord.command.CommandContext;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Represents a setting, which can be changed for either a guild or user (in DMs).
 * @param <T> The type of the setting.
 */
public abstract class Setting<T> implements ISetting<T> {
    /**
     * Gets the value of this setting for the user.
     * @param u The Discord user.
     * @return The value of the setting, or null if unset.
     */
    public Optional<T> get(@NonNull User u) {
        return getUser(u.getIdLong());
    }
    /**
     * Gets the value of this setting for the guild.
     * @param g The Discord guild.
     * @return The value of the setting, or null if unset.
     */
    public Optional<T> get(@NonNull Guild g) {
        return getGuild(g.getIdLong());
    }
    /**
     * Gets the value of this setting used in the current context.
     * @param txt The context of the executing command.
     * @return The value of the setting, or null if unset.
     */
    public Optional<T> get(@NonNull CommandContext txt) {
        return txt.e.isFromGuild() ? get(txt.e.getGuild()) : get(txt.e.getAuthor());
    }

    /**
     * Gets the effective value of this setting for the user.
     * @param id The ID of the Discord user.
     * @return The value of the setting, or the default if unset.
     */
    public @NonNull T getEffectiveUser(long id) {
        return getUser(id).orElse(getDefault());
    }
    /**
     * Gets the effective value of this setting for the guild.
     * @param id The ID of the Discord guild.
     * @return The value of the setting, or the default if unset.
     */
    public @NonNull T getEffectiveGuild(long id) {
        return getGuild(id).orElse(getDefault());
    }
    /**
     * Gets the effective value of this setting for the user.
     * @param u The Discord user.
     * @return The value of the setting, or the default if unset.
     */
    public @NonNull T getEffective(@NonNull User u) {
        return getEffectiveUser(u.getIdLong());
    }
    /**
     * Gets the effective value of this setting for the guild.
     * @param g The Discord guild.
     * @return The value of the setting, or the default if unset.
     */
    public @NonNull T getEffective(@NonNull Guild g) {
        return getEffectiveGuild(g.getIdLong());
    }
    /**
     * Gets the value of this setting used in the current context.
     * @param txt The context of the executing command.
     * @return The value of the setting, or the default if unset.
     */
    public @NonNull T getEffective(@NonNull CommandContext txt) {
        return txt.e.isFromGuild() ? getEffective(txt.e.getGuild()) : getEffective(txt.e.getAuthor());
    }

    /**
     * Changes this setting for the user.
     * @param id The ID of the Discord user.
     * @param input The user-provided input to change the setting to. Resets if {@code null}.
     * @return A status object representing the result of this function.
     */
    public @NonNull SetStatus setUser(long id, @Nullable String input) {
        if (!supportsUsers()) {
            return SetStatus.UNSUPPORTED;
        }
        if (input == null) {
            return resetUser(id);
        }
        Optional<T> settingOpt = resolve(input);
        if (!settingOpt.isPresent()) {
            return SetStatus.INVALID_INPUT;
        }
        T setting = settingOpt.get();
        Optional<T> currentOpt = getUser(id);
        if (!currentOpt.isPresent() && setting == getDefault()) {
            return changeUser(id, setting) ? SetStatus.UNSET_TO_DEFAULT : SetStatus.INTERNAL_FAILURE;
        } else if (currentOpt.isPresent() && setting == currentOpt.get()) {
            return SetStatus.SET_NO_CHANGE;
        }
        return changeUser(id, setting) ? SetStatus.SET : SetStatus.INTERNAL_FAILURE;
    }
    /**
     * Changes this setting for the guild.
     * @param id The ID of the Discord guild.
     * @param input The user-provided input to change the setting to. Resets if {@code null}.
     * @return A status object representing the result of this function.
     */
    public @NonNull SetStatus setGuild(long id, @Nullable String input) {
        if (!supportsGuilds()) {
            return SetStatus.UNSUPPORTED;
        }
        if (input == null) {
            return resetGuild(id);
        }
        Optional<T> settingOpt = resolve(input);
        if (!settingOpt.isPresent()) {
            return SetStatus.INVALID_INPUT;
        }
        T setting = settingOpt.get();
        Optional<T> currentOpt = getGuild(id);
        if (!currentOpt.isPresent() && setting == getDefault()) {
            return changeGuild(id, setting) ? SetStatus.UNSET_TO_DEFAULT : SetStatus.INTERNAL_FAILURE;
        } else if (currentOpt.isPresent() && setting == currentOpt.get()) {
            return SetStatus.SET_NO_CHANGE;
        }
        return changeGuild(id, setting) ? SetStatus.SET : SetStatus.INTERNAL_FAILURE;
    }
    /**
     * Changes this setting for the user.
     * @param u The Discord user.
     * @param input The user-provided input to change the setting to. Resets if {@code null}.
     * @return A status object representing the result of this function.
     */
    public @NonNull SetStatus set(@NonNull User u, @Nullable String input) {
        return setUser(u.getIdLong(), input);
    }
    /**
     * Changes this setting for the guild.
     * @param g The Discord guild.
     * @param input The user-provided input to change the setting to. Resets if {@code null}.
     * @return A status object representing the result of this function.
     */
    public @NonNull SetStatus set(@NonNull Guild g, @Nullable String input) {
        return setGuild(g.getIdLong(), input);
    }
    /**
     * Changes this setting for the current context.
     * @param txt The context of the executing command.
     * @param input The user-provided input to change the setting to. Resets if {@code null}.
     * @return A status object representing the result of this function.
     */
    public @NonNull SetStatus set(@NonNull CommandContext txt, @Nullable String input) {
        return txt.e.isFromGuild() ? set(txt.e.getGuild(), input) : set(txt.e.getAuthor(), input);
    }

    /**
     * Resets this setting for the user. This "unsets" the setting and does NOT change it to the default.
     * @param id The ID of the Discord user.
     * @return A status object representing the result of this function.
     */
    public @NonNull SetStatus resetUser(long id) {
        if (!supportsUsers()) {
            return SetStatus.UNSUPPORTED;
        }
        Optional<T> current = getUser(id);
        if (!current.isPresent()) {
            return SetStatus.RESET_NO_CHANGE;
        } else if (current.get() == getDefault()) {
            return SetStatus.RESET_TO_DEFAULT;
        }
        return clearUser(id) ? SetStatus.RESET : SetStatus.INTERNAL_FAILURE;
    }
    /**
     * Resets this setting for the guild. This "unsets" the setting and does NOT change it to the default.
     * @param id The ID of the Discord guild.
     * @return A status object representing the result of this function.
     */
    public @NonNull SetStatus resetGuild(long id) {
        if (!supportsGuilds()) {
            return SetStatus.UNSUPPORTED;
        }
        Optional<T> current = getGuild(id);
        if (!current.isPresent()) {
            return SetStatus.RESET_NO_CHANGE;
        } else if (current.get() == getDefault()) {
            return SetStatus.RESET_TO_DEFAULT;
        }
        return clearGuild(id) ? SetStatus.RESET : SetStatus.INTERNAL_FAILURE;
    }
    /**
     * Resets this setting for the user. This "unsets" the setting and does NOT change it to the default.
     * @param u The Discord user.
     * @return A status object representing the result of this function.
     */
    public @NonNull SetStatus reset(@NonNull User u) {
        return resetUser(u.getIdLong());
    }
    /**
     * Resets this setting for the guild. This "unsets" the setting and does NOT change it to the default.
     * @param g The Discord guild.
     * @return A status object representing the result of this function.
     */
    public @NonNull SetStatus reset(@NonNull Guild g) {
        return resetUser(g.getIdLong());
    }
    /**
     * Resets this setting for the current context. This "unsets" the setting and does NOT change it to the default.
     * @param txt The context of the executing command.
     * @return A status object representing the result of this function.
     */
    public @Nullable SetStatus reset(@NonNull CommandContext txt) {
        return txt.e.isFromGuild() ? reset(txt.e.getGuild()) : reset(txt.e.getAuthor());
    }

    /**
     * Changes the setting for the specified user.
     * @param id The ID of the discord user.
     * @param setting The value of the setting.
     * @return Whether the setting could be changed.
     */
    protected abstract boolean changeUser(long id, @NonNull T setting);
    /**
     * Changes the setting for the specified guild.
     * @param id The ID of the discord guild.
     * @param setting The value of the setting.
     * @return Whether the setting could be changed.
     */
    protected abstract boolean changeGuild(long id, @NonNull T setting);
    /**
     * <p>Resets the setting for the user, leaving it unset. {@link #getUser(long id)} will return {@link #getDefault()}.</p>
     * <b>This is NOT equivalent to changing the setting to the default!</b> If the default value changes, then the setting for this user will change also.
     * @param id The ID of the discord user.
     * @return Whether the setting could be changed.
     */
    protected abstract boolean clearUser(long id);
    /**
     * <p>Resets the setting for the user, leaving it unset. {@link #getGuild(long id)} will return {@link #getDefault()}.</p>
     * <b>This is NOT equivalent to changing the setting to the default!</b> If the default value changes, then the setting for this guild will change also.
     * @param id The ID of the discord guild.
     * @return Whether the setting could be changed.
     */
     protected abstract boolean clearGuild(long id);
}
