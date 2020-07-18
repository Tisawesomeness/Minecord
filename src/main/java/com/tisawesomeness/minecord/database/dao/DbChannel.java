package com.tisawesomeness.minecord.database.dao;

import com.tisawesomeness.minecord.Lang;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.util.StatementUtils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Mirrors the {@code channel} database table.
 * <br>Created or modified channels can be sent to the database with {@link #update()}.
 */
@Value
@With
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DbChannel implements SettingContainer, Bannable {

    private static final String SQL_SELECT = "SELECT * FROM channel WHERE id = ?;";
    private static final String SQL_UPDATE =
            "UPDATE channel SET guild_id = ?, banned = ?, prefix = ?, lang = ?, use_menu = ? WHERE id = ?;";
    private static final String SQL_INSERT =
            "INSERT INTO channel (guild_id, banned, prefix, lang, use_menu, id) VALUES (?, ?, ?, ?, ?, ?);";

    Database db;
    boolean inDB;

    long id;
    long guildId;
    boolean banned;
    Optional<String> prefix;
    Optional<Lang> lang;
    Optional<Boolean> useMenu;

    /**
     * Creates a new channel object that is not banned, and all settings are unset.
     * @param db The database this channel object is mirroring
     * @param id The channel id
     */
    public DbChannel(Database db, long id, long guildId) {
        this(db, false, id, guildId, false, Optional.empty(), Optional.empty(), Optional.empty());
    }

    /**
     * Loads a channel from the database for use in a {@link com.google.common.cache.LoadingCache}.
     * @param db The database to pull from
     * @param key The channel id
     * @return The channel, or empty if it is not in the database
     * @throws SQLException If a database error occurs
     */
    public static Optional<DbChannel> load(@NonNull Database db, @NonNull Long key) throws SQLException {
        @Cleanup Connection connect = db.getConnect();
        @Cleanup PreparedStatement st = connect.prepareStatement(SQL_SELECT);
        st.setLong(1, key);
        ResultSet rs = st.executeQuery();
        // The first next() call returns true if results exist
        if (!rs.next()) {
            return Optional.empty();
        }
        return Optional.of(from(db, rs));
    }

    /**
     * Creates a new channel object from a SQL query result.
     * @param db The database reference needed to create the channel object
     * @param rs The SQL query result, must be pointing to a valid row
     * @return A new channel object
     * @throws SQLException If the ResultSet is closed or a column doesn't exist
     */
    public static DbChannel from(Database db, ResultSet rs) throws SQLException {
        return new DbChannel(db, true,
                rs.getLong("id"),
                rs.getLong("guild_id"),
                rs.getBoolean("banned"),
                StatementUtils.getOptionalString(rs, "prefix"),
                StatementUtils.getOptionalString(rs, "lang").flatMap(Lang::from),
                StatementUtils.getOptionalBoolean(rs, "use_menu")
        );
    }

    /**
     * Updates or inserts this channel into the database.
     * @throws SQLException If a database error occurs
     */
    public void update() throws SQLException {
        @Cleanup Connection connect = db.getConnect();
        String sql = inDB ? SQL_UPDATE : SQL_INSERT;
        @Cleanup PreparedStatement st = connect.prepareStatement(sql);
        st.setLong(1, guildId);
        st.setBoolean(2, banned);
        StatementUtils.setOptionalString(st, 3, prefix);
        StatementUtils.setOptionalString(st, 4, lang.map(Lang::getCode));
        StatementUtils.setOptionalBoolean(st, 5, useMenu);
        st.setLong(6, id);
        st.executeUpdate();
        db.getCache().invalidateChannel(id);
    }

}
