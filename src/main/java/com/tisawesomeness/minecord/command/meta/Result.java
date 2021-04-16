package com.tisawesomeness.minecord.command.meta;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nullable;

/**
 * An enum of all possible outcomes when a user tries to execute a command.
 */
@RequiredArgsConstructor
public enum Result {
    /**
     * Command succeeded, no problems
     */
    SUCCESS("Success", ":white_check_mark:"),
    /**
     * Command redirected to help
     */
    HELP("Help", ":mag:"),
    /**
     * The command is on cooldown
     */
    COOLDOWN("Cooldown", ":ice_cube:"),
    /**
     * The command was used in DMs but can only be used in a guild
     */
    GUILD_ONLY("Guild Only", ":warning:", ":no_entry:"),
    /**
     * User is not elevated but tried to do something that requires elevation
     */
    NOT_ELEVATED("Not Elevated", ":lock:"),
    /**
     * User does not have the necessary permissions
     */
    NO_USER_PERMISSIONS("No User Permissions", ":no_pedestrians:"),
    /**
     * Bot does not have the necessary permissions
     */
    NO_BOT_PERMISSIONS("No Bot Permissions", ":broken_heart:"),
    /**
     * The user hit a syntax error
     */
    INVALID_ARGS("Invalid Args", ":warning:", ":beginner:"),
    /**
     * The user did something wrong
     */
    WARNING("Warning", ":warning:"),
    /**
     * Either a warning or an error, often when an external service could be either down or the request is incorrect
     */
    POSSIBLE_ERROR("Possible Error", ":bangbang:"),
    /**
     * An external service did something wrong out of our control
     */
    ERROR("Error", ":x:"),
    /**
     * Command threw an exception, should never be seen
     */
    EXCEPTION("Exception", ":boom:");

    @Getter private final @NonNull String name;
    @Getter private final @NonNull String emote;
    private final @Nullable String internalEmote;

    Result(@NonNull String name, @NonNull String emote) {
        this.name = name;
        this.emote = emote;
        internalEmote = null;
    }

    /**
     * Gets the emote used for documentation and usage purposes, not shown to the user
     * @return The internal emote
     */
    public @NonNull String getInternalEmote() {
        return internalEmote == null ? emote : internalEmote;
    }

    /**
     * Adds the emote associated with this Result to a message.
     * @param msg The message
     * @return The modified message
     */
    public @NonNull String addEmote(CharSequence msg) {
        return emote + " " + msg;
    }

    @Override
    public String toString() {
        return name;
    }
}
