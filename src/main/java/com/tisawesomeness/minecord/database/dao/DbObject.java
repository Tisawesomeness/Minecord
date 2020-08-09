package com.tisawesomeness.minecord.database.dao;

import java.sql.SQLException;

/**
 * Mirrors a database table.
 * <br>This should be an immutable object in order to work well with Caffeine caches.
 */
public interface DbObject {
    /**
     * The unique, unchangeable ID of this object.
     * <br>For Discord IDs, this is 17 to 20 digits long.
     * @return A positive number
     */
    long getId();
    /**
     * Updates or inserts this object into the database, using {@link #getId()} as the {@code id} column.
     * @throws SQLException If a database error occurs
     */
    void update() throws SQLException;
}
