package com.tisawesomeness.minecord.util;

import lombok.NonNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Optional;

public class ResultSetUtils {
    public static Optional<String> getOptionalString(@NonNull ResultSet rs, @NonNull String columnLabel) throws SQLException {
        return Optional.ofNullable(rs.getString(columnLabel));
    }
    public static Optional<Boolean> getOptionalBoolean(@NonNull ResultSet rs, @NonNull String columnLabel) throws SQLException {
        boolean b = rs.getBoolean(columnLabel);
        return rs.wasNull() ? Optional.empty() : Optional.of(b);
    }

    public static void setOptionalString(@NonNull PreparedStatement st, int columnIndex, Optional<String> value) throws SQLException {
        if (value.isPresent()) {
            st.setString(columnIndex, value.get());
            return;
        }
        st.setNull(columnIndex, Types.VARCHAR);
    }
    public static void setOptionalBoolean(@NonNull PreparedStatement st, int columnIndex, Optional<Boolean> value) throws SQLException {
        if (value.isPresent()) {
            st.setBoolean(columnIndex, value.get());
            return;
        }
        st.setNull(columnIndex, Types.BOOLEAN);
    }
}
