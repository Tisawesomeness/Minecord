package com.tisawesomeness.minecord.setting;

import com.tisawesomeness.minecord.command.CommandContext;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nullable;
import java.util.Optional;

public abstract class ChannelSetting<T> extends Setting<T> {

    /**
     * Gets the value of this setting for the text channel.
     * @param id The ID of the Discord text channel.
     * @return The value of the setting, or null if unset.
     */
    public abstract Optional<T> getChannel(long id);
    /**
     * Gets the value of this setting for the text channel.
     * @param c The Discord text channel.
     * @return The value of the setting, or null if unset.
     */
    public Optional<T> get(@NonNull GuildChannel c) {
        return getChannel(c.getIdLong());
    }
    /**
     * <p>Gets the value of this setting used in the current context.</p>
     * This will get the setting for the current channel, or unset for DMs.
     * @param txt The context of the executing command.
     * @return The value of the setting, or null if unset.
     */
    public Optional<T> get(@NonNull CommandContext txt) {
        MessageReceivedEvent e = txt.e;
        return e.isFromGuild() ? get(e.getTextChannel()) : Optional.empty();
    }

    /**
     * Gets the effective value of this setting for the text channel.
     * @param id The ID of the Discord text channel.
     * @return The value of the setting, or the default if unset.
     */
    public @NonNull T getEffectiveChannel(long id) {
        return getChannel(id).orElse(getDefault());
    }
    /**
     * Gets the effective value of this setting for the user.
     * @param c The Discord text channel.
     * @return The value of the setting, or the default if unset.
     */
    public @NonNull T getEffective(@NonNull GuildChannel c) {
        return getEffectiveChannel(c.getIdLong());
    }

    /**
     * Changes the setting for the specified text channel.
     * @param id The ID of the discord text channel.
     * @param setting The value of the setting.
     * @return Whether the setting could be changed.
     */
    protected abstract boolean changeChannel(long id, @NonNull T setting);
    private @NonNull SetResult setChannelInternal(long id, String input, Optional<T> from, ResolveResult<T> toResult) {
        Optional<T> toOpt = toResult.value;
        if (!toOpt.isPresent()) {
            return toResult.toStatus();
        }
        T to = toOpt.get();
        if (!from.isPresent() && to == getDefault()) {
            return changeChannel(id, to) ? SetStatus.SET_TO_DEFAULT : SetStatus.INTERNAL_FAILURE;
        } else if (from.isPresent() && to == from.get()) {
            return SetStatus.SET_NO_CHANGE;
        }
        return changeChannel(id, to) ? SetStatus.SET : SetStatus.INTERNAL_FAILURE;
    }
    /**
     * Changes this setting for the text channel.
     * @param id The ID of the Discord text channel.
     * @param input The user-provided input to change the setting to. Resets if {@code null}.
     * @return The string describing the result of the set operation.
     */
    public @NonNull String setChannel(long id, @Nullable String input) {
        if (input == null) {
            return resetChannel(id);
        }
        Optional<T> from = getChannel(id);
        ResolveResult<T> toResult = resolve(input);
        return setChannelInternal(id, input, from, toResult).getMsg(getDisplayName(),
                from.orElse(getDefault()).toString(), toResult.value.orElse(getDefault()).toString());
    }
    /**
     * Changes this setting for the text channel.
     * @param c The Discord text channel.
     * @param input The user-provided input to change the setting to. Resets if {@code null}.
     * @return The string describing the result of the set operation.
     */
    public @NonNull String set(@NonNull GuildChannel c, @Nullable String input) {
        return setChannel(c.getIdLong(), input);
    }

    /**
     * <p>Resets the setting for the text channel, leaving it unset. {@link #getChannel(long id)} will return {@link #getDefault()}.</p>
     * <b>This is NOT equivalent to changing the setting to the default!</b> If the default value changes, then the setting for this channel will change also.
     * @param id The ID of the discord text channel.
     * @return Whether the setting could be changed.
     */
    protected abstract boolean clearChannel(long id);
    private @NonNull SetResult resetChannelInternal(long id, Optional<T> from) {
        if (!from.isPresent()) {
            return SetStatus.RESET_NO_CHANGE;
        } else if (from.get() == getDefault()) {
            return SetStatus.RESET_TO_DEFAULT;
        }
        return clearChannel(id) ? SetStatus.RESET : SetStatus.INTERNAL_FAILURE;
    }
    /**
     * Resets this setting for the text channel. This "unsets" the setting and does NOT change it to the default.
     * @param id The ID of the Discord text channel.
     * @return The string describing the result of the set operation.
     */
    public @NonNull String resetChannel(long id) {
        Optional<T> from = getChannel(id);
        return resetChannelInternal(id, from).getMsg(getDisplayName(),
                from.orElse(getDefault()).toString(), "");
    }
    /**
     * Resets this setting for the text channel. This "unsets" the setting and does NOT change it to the default.
     * @param c The Discord text channel.
     * @return The string describing the result of the set operation.
     */
    public @NonNull String reset(@NonNull GuildChannel c) {
        return resetChannel(c.getIdLong());
    }

}
