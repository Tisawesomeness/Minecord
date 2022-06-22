package com.tisawesomeness.minecord.util;

import lombok.NonNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Optional;

/**
 * Utility class to make {@link ResultSet} and {@link PreparedStatement} work with {@link Optional}.
 */
public final class Statements {
    private Statements() {}

    /**
     * Gets a string from the ResultSet, checking for null.
     * @param rs The results of a database query
     * @param columnLabel The name of the column
     * @return An optional string that is empty if the database value is null
     * @throws SQLException If the ResultSet is closed, a database access error occurs, or {@code columnLabel} is invalid.
     */
    public static Optional<String> getOptionalString(
            @NonNull ResultSet rs, @NonNull String columnLabel) throws SQLException {
        return Optional.ofNullable(rs.getString(columnLabel));
    }
    /**
     * Gets a boolean from the ResultSet, checking for null.
     * @param rs The results of a database query
     * @param columnLabel The name of the column
     * @return An optional boolean that is empty if the database value is null
     * @throws SQLException If the ResultSet is closed, a database access error occurs, or {@code columnLabel} is invalid.
     */
    public static Optional<Boolean> getOptionalBoolean(
            @NonNull ResultSet rs, @NonNull String columnLabel) throws SQLException {
        boolean b = rs.getBoolean(columnLabel);
        return rs.wasNull() ? Optional.empty() : Optional.of(b);
    }

    /**
     * Sets the value in a PreparedStatement to a string or null.
     * @param st The statement to be updated
     * @param parameterIndex Must be positive and at most the number of parameterized arguments in the statement
     * @param value The optional string to insert into the statement
     * @throws SQLException If the statement is closed, a database access error occurs, or {@code parameterIndex} is invalid.
     */
    public static void setOptionalString(
            @NonNull PreparedStatement st, int parameterIndex, Optional<String> value) throws SQLException {
        if (parameterIndex <= 0) {
            throw new IllegalArgumentException("Parameter index must be positive but was " + parameterIndex);
        }
        if (value.isPresent()) {
            st.setString(parameterIndex, value.get());
            return;
        }
        st.setNull(parameterIndex, Types.VARCHAR);
    }
    /**
     * Sets the value in a PreparedStatement to a boolean or null.
     * @param st The statement to be updated
     * @param parameterIndex Must be positive and at most the number of parameterized arguments in the statement
     * @param value The optional boolean to insert into the statement
     * @throws SQLException If the statement is closed, a database access error occurs, or {@code parameterIndex} is invalid.
     */
    public static void setOptionalBoolean(
            @NonNull PreparedStatement st, int parameterIndex, Optional<Boolean> value) throws SQLException {
        if (parameterIndex <= 0) {
            throw new IllegalArgumentException("Parameter index must be positive but was " + parameterIndex);
        }
        if (value.isPresent()) {
            st.setBoolean(parameterIndex, value.get());
            return;
        }
        st.setNull(parameterIndex, Types.BOOLEAN);
    }

}
