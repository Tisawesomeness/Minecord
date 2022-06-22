package com.tisawesomeness.minecord.database.dao;

import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.lang.Lang;
import com.tisawesomeness.minecord.util.Statements;

import lombok.*;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Mirrors the {@code guild} database table.
 * <br>Created or modified guilds can be sent to the database with {@link #update()}.
 */
@Value
@With
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DbGuild implements SettingContainer, Bannable {

    private static final String SQL_SELECT = "SELECT * FROM guild WHERE id = ?;";
    private static final String SQL_UPDATE =
            "UPDATE guild SET banned = ?, prefix = ?, lang = ?, use_menu = ? WHERE id = ?;";
    private static final String SQL_INSERT =
            "INSERT INTO guild (banned, prefix, lang, use_menu, id) VALUES (?, ?, ?, ?, ?);";

    Database db;
    boolean inDB;

    long id;
    boolean banned;
    Optional<String> prefix;
    Optional<Lang> lang;
    Optional<Boolean> useMenu;

    /**
     * Creates a new guild object that is not banned, and all settings are unset.
     * @param db The database this guild object is mirroring
     * @param id The guild id
     */
    public DbGuild(Database db, long id) {
        this(db, false, id, false, Optional.empty(), Optional.empty(), Optional.empty());
    }

    /**
     * Loads a guild from the database for use in a {@link com.github.benmanes.caffeine.cache.LoadingCache}.
     * @param db The database to pull from
     * @param key The guild id
     * @return The guild, or empty if it is not in the database
     * @throws SQLException If a database error occurs
     */
    public static @Nullable DbGuild load(@NonNull Database db, @NonNull Long key) throws SQLException {
        @Cleanup Connection connect = db.getConnect();
        @Cleanup PreparedStatement st = connect.prepareStatement(SQL_SELECT);
        st.setLong(1, key);
        ResultSet rs = st.executeQuery();
        // The first next() call returns true if results exist
        if (!rs.next()) {
            return null;
        }
        return new DbGuild(db, true,
                rs.getLong("id"),
                rs.getBoolean("banned"),
                Statements.getOptionalString(rs, "prefix"),
                Statements.getOptionalString(rs, "lang").flatMap(Lang::from),
                Statements.getOptionalBoolean(rs, "use_menu")
        );
    }

    /**
     * Updates or inserts this guild into the database.
     * @throws SQLException If a database error occurs
     */
    public void update() throws SQLException {
        @Cleanup Connection connect = db.getConnect();
        String sql = inDB ? SQL_UPDATE : SQL_INSERT;
        @Cleanup PreparedStatement st = connect.prepareStatement(sql);
        st.setBoolean(1, banned);
        Statements.setOptionalString(st, 2, prefix);
        Statements.setOptionalString(st, 3, lang.map(Lang::getCode));
        Statements.setOptionalBoolean(st, 4, useMenu);
        st.setLong(5, id);
        st.executeUpdate();
        db.getCache().invalidateGuild(id);
    }

}
