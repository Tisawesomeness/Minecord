package com.tisawesomeness.minecord.setting;

import com.tisawesomeness.minecord.command.CommandContext;

import lombok.NonNull;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nullable;
import java.util.Optional;

public abstract class GlobalSetting<T> extends ServerSetting<T> {

    /**
     * Gets the value of this setting for the user.
     * @param id The ID of the Discord user.
     * @return The value of the setting, or null if unset.
     */
    public abstract Optional<T> getUser(long id);
    /**
     * <p>Gets the value of this setting used in the current context.</p>
     * This will get the user setting if used in a DM, otherwise it acts like {@link ServerSetting#get(CommandContext txt)}
     * @param e The event that triggered the executing command.
     * @return The value of the setting, or null if unset.
     */
    @Override
    public Optional<T> get(@NonNull MessageReceivedEvent e) {
        return e.isFromGuild() ? super.get(e) : getUser(e.getAuthor().getIdLong());
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
     * Gets the effective value of this setting for the user.
     * @param u The Discord user.
     * @return The value of the setting, or the default if unset.
     */
    public @NonNull T getEffective(@NonNull User u) {
        return getEffectiveUser(u.getIdLong());
    }

    /**
     * Changes the setting for the specified user.
     * @param id The ID of the discord user.
     * @param setting The value of the setting.
     * @return Whether the setting could be changed.
     */
    protected abstract boolean changeUser(long id, @NonNull T setting);
    private @NonNull SetResult setUserInternal(long id, Optional<T> from, ResolveResult<T> toResult) {
        Optional<T> toOpt = toResult.value;
        if (!toOpt.isPresent()) {
            return toResult.toStatus();
        }
        T to = toOpt.get();

        if (from.isPresent()) {
            if (to.equals(getDefault())) {
                return changeUser(id, to) ? SetStatus.SET_FROM_TO_DEFAULT : SetStatus.INTERNAL_FAILURE;
            } else if (to.equals(from.get())) {
                return SetStatus.SET_NO_CHANGE;
            }
        } else if (to.equals(getDefault())) {
            return changeUser(id, to) ? SetStatus.SET_TO_DEFAULT : SetStatus.INTERNAL_FAILURE;
        }
        return changeUser(id, to) ? SetStatus.SET : SetStatus.INTERNAL_FAILURE;
    }
    /**
     * Changes this setting for the user.
     * @param id The ID of the Discord user.
     * @param input The user-provided input to change the setting to. Resets if {@code null}.
     * @return The string describing the result of the set operation.
     */
    public @NonNull String setUser(long id, @Nullable String input) {
        if (input == null) {
            return resetUser(id);
        }
        Optional<T> from = getUser(id);
        ResolveResult<T> toResult = resolve(input);
        return setUserInternal(id, from, toResult).getMsg(getDisplayName(),
                from.orElse(getDefault()).toString(), toResult.value.orElse(getDefault()).toString());
    }

    /**
     * <p>Resets the setting for the user, leaving it unset. {@link #getUser(long id)} will return {@link #getDefault()}.</p>
     * <b>This is NOT equivalent to changing the setting to the default!</b> If the default value changes, then the setting for this channel will change also.
     * @param id The ID of the discord user.
     * @return Whether the setting could be changed.
     */
    protected abstract boolean clearUser(long id);
    private @NonNull SetResult resetUserInternal(long id, Optional<T> from) {
        if (!from.isPresent()) {
            return SetStatus.RESET_NO_CHANGE;
        } else if (from.get().equals(getDefault())) {
            return SetStatus.RESET_TO_DEFAULT;
        }
        return clearUser(id) ? SetStatus.RESET : SetStatus.INTERNAL_FAILURE;
    }
    /**
     * Resets this setting for the user. This "unsets" the setting and does NOT change it to the default.
     * @param id The ID of the Discord user.
     * @return The string describing the result of the set operation.
     */
    public @NonNull String resetUser(long id) {
        Optional<T> from = getUser(id);
        return resetUserInternal(id, from).getMsg(getDisplayName(),
                from.orElse(getDefault()).toString(), "");
    }

}
