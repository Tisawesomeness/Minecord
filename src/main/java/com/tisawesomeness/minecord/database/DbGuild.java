package com.tisawesomeness.minecord.database;

import lombok.NonNull;
import lombok.Value;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Value
public class DbGuild {

	public long id;
	public Optional<String> prefix;
	public Optional<String> lang;
	public boolean banned;
	public boolean noCooldown;
	public Optional<Boolean> deleteCommands;
	public Optional<Boolean> noMenu;

	/**
	 * Constructs a new database guild object from a SQL SELECT query
	 * @param rs The result of the query
	 * @return The equivalent object
	 * @throws SQLException If the given ResultSet does not match this object
	 */
	public static DbGuild from(@NonNull ResultSet rs) throws SQLException {
		return new DbGuild(
				rs.getLong("id"),
				getOptionalString(rs, "prefix"),
				getOptionalString(rs, "lang"),
				rs.getBoolean("banned"),
				rs.getBoolean("noCooldown"),
				getOptionalBoolean(rs, "deleteCommands"),
				getOptionalBoolean(rs, "noMenu")
		);
	}

	private static Optional<String> getOptionalString(@NonNull ResultSet rs, @NonNull String columnLabel) throws SQLException {
		return Optional.ofNullable(rs.getString(columnLabel));
	}
	private static Optional<Boolean> getOptionalBoolean(@NonNull ResultSet rs, @NonNull String columnLabel) throws SQLException {
		boolean b = rs.getBoolean(columnLabel);
		return rs.wasNull() ? Optional.empty() : Optional.ofNullable(b);
	}

}
