package com.tisawesomeness.minecord.database;

import java.sql.SQLException;

public interface DbObject {
    /**
     * The unique ID of this object.
     * <br>For Discord IDs, this is 17 to 20 digits long.
     * @return A positive number
     */
    long getId();
    /**
     * Updates or inserts this object into the database.
     * @throws SQLException If a database error occurs
     */
    void update() throws SQLException;
}
