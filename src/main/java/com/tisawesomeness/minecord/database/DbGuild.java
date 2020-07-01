package com.tisawesomeness.minecord.database;

import com.tisawesomeness.minecord.util.ResultSetUtils;

import lombok.NonNull;
import lombok.Value;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Value
public class DbGuild {

	long id;
	boolean banned;

	Optional<String> prefix;
	Optional<String> lang;
	Optional<Boolean> useMenu;

	/**
	 * Constructs a new database guild object from a SQL SELECT query
	 * @param rs The result of the query
	 * @return The equivalent object
	 * @throws SQLException If the given ResultSet does not match this object
	 */
	public static DbGuild from(@NonNull ResultSet rs) throws SQLException {
		return new DbGuild(
				rs.getLong("id"),
				rs.getBoolean("banned"),
				ResultSetUtils.getOptionalString(rs, "prefix"),
				ResultSetUtils.getOptionalString(rs, "lang"),
				ResultSetUtils.getOptionalBoolean(rs, "use_menu")
		);
	}

}
