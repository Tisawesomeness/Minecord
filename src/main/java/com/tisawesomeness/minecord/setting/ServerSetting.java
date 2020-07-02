package com.tisawesomeness.minecord.setting;

import lombok.NonNull;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nullable;
import java.util.Optional;

public abstract class ServerSetting<T> extends ChannelSetting<T> {

    /**
     * Gets the value of this setting for the guild.
     * @param id The ID of the Discord guild.
     * @return The value of the setting, or null if unset.
     */
    public abstract Optional<T> getGuild(long id);
    /**
     * <p>Gets the value of this setting used in the current context.</p>
     * This will get the current channel if it is set, otherwise the current guild is used. Unset for DMs.
     * @param e The event that triggered the executing command.
     * @return The value of the setting, or null if unset.
     */
    @Override
    public Optional<T> get(@NonNull MessageReceivedEvent e) {
        if (e.isFromGuild()) {
            Optional<T> setting = getChannel(e.getTextChannel().getIdLong());
            if (setting.isPresent()) {
                return setting;
            }
            return getGuild(e.getGuild().getIdLong());
        }
        return Optional.empty();
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
     * Changes the setting for the specified guild.
     * @param id The ID of the discord guild.
     * @param setting The value of the setting.
     * @return Whether the setting could be changed.
     */
    protected abstract boolean changeGuild(long id, @NonNull T setting);
    private @NonNull SetResult setGuildInternal(long id, Optional<T> from, ResolveResult<T> toResult) {
        Optional<T> toOpt = toResult.value;
        if (!toOpt.isPresent()) {
            return toResult.toStatus();
        }
        T to = toOpt.get();

        if (from.isPresent()) {
            if (to.equals(getDefault())) {
                return changeGuild(id, to) ? SetStatus.SET_FROM_TO_DEFAULT : SetStatus.INTERNAL_FAILURE;
            } else if (to.equals(from.get())) {
                return SetStatus.SET_NO_CHANGE;
            }
        } else if (to.equals(getDefault())) {
            return changeGuild(id, to) ? SetStatus.SET_TO_DEFAULT : SetStatus.INTERNAL_FAILURE;
        }
        return changeGuild(id, to) ? SetStatus.SET : SetStatus.INTERNAL_FAILURE;
    }
    /**
     * Changes this setting for the guild.
     * @param id The ID of the Discord guild.
     * @param input The user-provided input to change the setting to. Resets if {@code null}.
     * @return The string describing the result of the set operation.
     */
    public @NonNull String setGuild(long id, @Nullable String input) {
        if (input == null) {
            return resetGuild(id);
        }
        Optional<T> from = getGuild(id);
        ResolveResult<T> toResult = resolve(input);
        return setGuildInternal(id, from, toResult).getMsg(getDisplayName(),
                from.orElse(getDefault()).toString(), toResult.value.orElse(getDefault()).toString());
    }

    /**
     * <p>Resets the setting for the guild, leaving it unset. {@link #getGuild(long id)} will return {@link #getDefault()}.</p>
     * <b>This is NOT equivalent to changing the setting to the default!</b> If the default value changes, then the setting for this channel will change also.
     * @param id The ID of the discord guild.
     * @return Whether the setting could be changed.
     */
    protected abstract boolean clearGuild(long id);
    private @NonNull SetResult resetGuildInternal(long id, Optional<T> from) {
        if (!from.isPresent()) {
            return SetStatus.RESET_NO_CHANGE;
        } else if (from.get().equals(getDefault())) {
            return SetStatus.RESET_TO_DEFAULT;
        }
        return clearGuild(id) ? SetStatus.RESET : SetStatus.INTERNAL_FAILURE;
    }
    /**
     * Resets this setting for the guild. This "unsets" the setting and does NOT change it to the default.
     * @param id The ID of the Discord guild.
     * @return The string describing the result of the set operation.
     */
    public @NonNull String resetGuild(long id) {
        Optional<T> from = getGuild(id);
        return resetGuildInternal(id, from).getMsg(getDisplayName(),
                from.orElse(getDefault()).toString(), "");
    }

}
