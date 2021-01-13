package com.tisawesomeness.minecord.database.dao;

import com.tisawesomeness.minecord.lang.Lang;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.util.StatementUtils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Mirrors the {@code user} database table.
 * <br>Created or modified guilds can be sent to the database with {@link #update()}.
 */
@Value
@With
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DbUser implements SettingContainer, Bannable {

    private static final String SQL_SELECT = "SELECT * FROM user WHERE id = ?;";
    private static final String SQL_UPDATE =
            "UPDATE user SET banned = ?, elevated = ?, prefix = ?, lang = ?, use_menu = ? WHERE id = ?;";
    private static final String SQL_INSERT =
            "INSERT INTO user (banned, elevated, prefix, lang, use_menu, id) VALUES (?, ?, ?, ?, ?, ?);";

    Database db;
    boolean inDB;

    long id;
    boolean banned;
    boolean elevated;
    Optional<String> prefix;
    Optional<Lang> lang;
    Optional<Boolean> useMenu;

    /**
     * Creates a new user object that is not banned, and all settings are unset.
     * @param db The database this user object is mirroring
     * @param id The user id
     */
    public DbUser(Database db, long id) {
        this(db, false, id, false, false, Optional.empty(), Optional.empty(), Optional.empty());
    }

    /**
     * Loads a user from the database for use in a {@link com.google.common.cache.LoadingCache}.
     * @param db The database to pull from
     * @param key The user id
     * @return The user, or empty if it is not in the database
     * @throws SQLException If a database error occurs
     */
    public static @Nullable DbUser load(@NonNull Database db, @NonNull Long key) throws SQLException {
        @Cleanup Connection connect = db.getConnect();
        @Cleanup PreparedStatement st = connect.prepareStatement(SQL_SELECT);
        st.setLong(1, key);
        ResultSet rs = st.executeQuery();
        // The first next() call returns true if results exist
        if (!rs.next()) {
            return null;
        }
        return new DbUser(db, true,
                rs.getLong("id"),
                rs.getBoolean("banned"),
                rs.getBoolean("elevated"),
                StatementUtils.getOptionalString(rs, "prefix"),
                StatementUtils.getOptionalString(rs, "lang").flatMap(Lang::from),
                StatementUtils.getOptionalBoolean(rs, "use_menu")
        );
    }

    /**
     * Updates or inserts this user into the database.
     * @throws SQLException If a database error occurs
     */
    public void update() throws SQLException {
        @Cleanup Connection connect = db.getConnect();
        String sql = inDB ? SQL_UPDATE : SQL_INSERT;
        @Cleanup PreparedStatement st = connect.prepareStatement(sql);
        st.setBoolean(1, banned);
        st.setBoolean(2, elevated);
        StatementUtils.setOptionalString(st, 3, prefix);
        StatementUtils.setOptionalString(st, 4, lang.map(Lang::getCode));
        StatementUtils.setOptionalBoolean(st, 5, useMenu);
        st.setLong(6, id);
        st.executeUpdate();
        db.getCache().invalidateUser(id);
    }

}
