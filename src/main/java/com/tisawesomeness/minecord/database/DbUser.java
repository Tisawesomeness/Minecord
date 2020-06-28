package com.tisawesomeness.minecord.database;

import lombok.NonNull;
import lombok.Value;

import java.sql.ResultSet;
import java.sql.SQLException;

@Value
public class DbUser {
	
	public long id;
	public boolean elevated;
	public boolean banned;

	/**
	 * Constructs a new database guild object from a SQL SELECT query
	 * @param rs The result of the query
	 * @return The equivalent object
	 * @throws SQLException If the given ResultSet does not match this object
	 */
	public static DbUser from(@NonNull ResultSet rs) throws SQLException {
		return new DbUser(
				rs.getLong("id"),
				rs.getBoolean("elevated"),
				rs.getBoolean("banned")
		);
	}

}
