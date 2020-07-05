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
public class DbUser implements DMSettingContainer {

	private static final String SQL_SELECT = "SELECT * FROM user WHERE id = ?;";
	private static final String SQL_UPDATE =
			"UPDATE user SET banned = ?, elevated = ?, prefix = ?, lang = ? WHERE id = ?;";
	private static final String SQL_INSERT =
			"INSERT INTO user (banned, elevated, prefix, lang, id) VALUES (?, ?, ?, ?, ?);";

	Database db;
	boolean inDB;

	long id;
	boolean banned;
	boolean elevated;
	Optional<String> prefix;
	Optional<Lang> lang;

	public DbUser(Database db, long id) {
		this(db, false, id, false, false, Optional.empty(), Optional.empty());
	}

	public static Optional<DbUser> load(@NonNull Database db, @NonNull Long key) throws SQLException {
		@Cleanup Connection connect = db.getConnect();
		@Cleanup PreparedStatement st = connect.prepareStatement(SQL_SELECT);
		st.setLong(1, key);
		ResultSet rs = st.executeQuery();
		// The first next() call returns true if results exist
		if (!rs.next()) {
			return Optional.empty();
		}
		return Optional.of(new DbUser(db, true,
				rs.getLong("id"),
				rs.getBoolean("banned"),
				rs.getBoolean("elevated"),
				ResultSetUtils.getOptionalString(rs, "prefix"),
				ResultSetUtils.getOptionalString(rs, "lang").flatMap(Lang::from)
		));
	}

	public void update() throws SQLException {
		@Cleanup Connection connect = db.getConnect();
		String sql = inDB ? SQL_UPDATE : SQL_INSERT;
		@Cleanup PreparedStatement st = connect.prepareStatement(sql);
		st.setBoolean(1, banned);
		st.setBoolean(2, elevated);
		ResultSetUtils.setOptionalString(st, 3, prefix);
		ResultSetUtils.setOptionalString(st, 4, lang.map(Lang::getCode));
		st.setLong(5, id);
		st.executeUpdate();
		db.getCache().invalidateGuild(id);
	}

}
