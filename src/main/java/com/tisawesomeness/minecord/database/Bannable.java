package com.tisawesomeness.minecord.database;

/**
 * Represents something that can be banned from using the bot.
 */
public interface Bannable {
    /**
     * Whether this is banned.
     */
    boolean isBanned();
}
