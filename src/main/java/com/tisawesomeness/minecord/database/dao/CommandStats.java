package com.tisawesomeness.minecord.database.dao;

import com.google.common.collect.Multiset;

import java.sql.SQLException;

/**
 * Holds a list of each command and how many times it has been used
 */
public interface CommandStats {

    /**
     * Queries the database for the number of times each command has been used.
     * @return A Multiset of strings where each key is a command ID and its count is the number of uses
     * @throws SQLException When a database error occurs
     */
    Multiset<String> getCommandUses() throws SQLException;

    /**
     * Adds the provided uses to the running count in the database.
     * <br>This does not clear the input.
     * @param commandUses A Multiset of strings where each key is a command ID and its count is the number of uses
     * @throws SQLException When a database error occurs
     */
    void pushCommandUses(Multiset<String> commandUses) throws SQLException;

}
