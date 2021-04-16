package com.tisawesomeness.minecord.command.meta;

/**
 * Marks a command as only available in guidls
 */
public interface IGuildOnlyCommand {
    default boolean guildOnlyAppliesToAdmins() {
        return true;
    }
}
