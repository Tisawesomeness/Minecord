package com.tisawesomeness.minecord.util;

import lombok.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ResultSetUtils {
    public static Optional<String> getOptionalString(@NonNull ResultSet rs, @NonNull String columnLabel) throws SQLException {
        return Optional.ofNullable(rs.getString(columnLabel));
    }
    public static Optional<Boolean> getOptionalBoolean(@NonNull ResultSet rs, @NonNull String columnLabel) throws SQLException {
        boolean b = rs.getBoolean(columnLabel);
        return rs.wasNull() ? Optional.empty() : Optional.of(b);
    }
}
