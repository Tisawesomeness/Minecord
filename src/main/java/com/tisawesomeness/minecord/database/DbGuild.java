package com.tisawesomeness.minecord.database;

import com.tisawesomeness.minecord.Lang;
import com.tisawesomeness.minecord.util.ResultSetUtils;

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

@Value
@With
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DbGuild implements SettingContainer {

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

	public DbGuild(Database db, long id) {
		this(db, false, id, false, Optional.empty(), Optional.empty(), Optional.empty());
	}

	public static Optional<DbGuild> load(@NonNull Database db, @NonNull Long key) throws SQLException {
		@Cleanup Connection connect = db.getConnect();
		@Cleanup PreparedStatement st = connect.prepareStatement(SQL_SELECT);
		st.setLong(1, key);
		ResultSet rs = st.executeQuery();
		// The first next() call returns true if results exist
		if (!rs.next()) {
			return Optional.empty();
		}
		return Optional.of(new DbGuild(db, true,
				rs.getLong("id"),
				rs.getBoolean("banned"),
				ResultSetUtils.getOptionalString(rs, "prefix"),
				ResultSetUtils.getOptionalString(rs, "lang").flatMap(Lang::from),
				ResultSetUtils.getOptionalBoolean(rs, "use_menu")
		));
	}

	public void update() throws SQLException {
		@Cleanup Connection connect = db.getConnect();
		String sql = inDB ? SQL_UPDATE : SQL_INSERT;
		@Cleanup PreparedStatement st = connect.prepareStatement(sql);
		st.setBoolean(1, banned);
		ResultSetUtils.setOptionalString(st, 2, prefix);
		ResultSetUtils.setOptionalString(st, 3, lang.map(Lang::getCode));
		ResultSetUtils.setOptionalBoolean(st, 4, useMenu);
		st.setLong(5, id);
		st.executeUpdate();
		db.getCache().invalidateGuild(id);
	}

}
