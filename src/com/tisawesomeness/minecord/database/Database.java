package com.tisawesomeness.minecord.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.tisawesomeness.minecord.Config;

public class Database {
	
	private static Connection connect = null;
	private static HashMap<Long, DbGuild> guilds = new HashMap<Long, DbGuild>();
	private static HashMap<Long, DbUser> users = new HashMap<Long, DbUser>();
	private static int goal = 0;
	
	public static void init() throws SQLException {
		
		//Build database url
		String url = "jdbc:";
		if (Config.getType().equals("mysql")) {
			url += "mysql://";
			try {
				Class.forName("com.mysql.jdbc.Driver").newInstance();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
				ex.printStackTrace();
			}
		} else {
			url += "sqlite:";
		}
		url += Config.getHost();
		if (Config.getType().equals("mysql")) {
			url += ":" + Config.getPort() + "/" + Config.getDbName() + "?autoReconnect=true&useSSL=false";
		}
		
		//Connect to database
		connect = DriverManager.getConnection(url, Config.getUser(), Config.getPass());
		
		//Create tables if they do not exist
		connect.createStatement().executeUpdate(
			"CREATE TABLE IF NOT EXISTS guild (" +
			"  id BIGINT(18) NOT NULL," +
			"  prefix TINYTEXT NOT NULL," +
			"  banned TINYINT(1) NOT NULL DEFAULT 0," +
			"  noCooldown TINYINT(1) NOT NULL DEFAULT 0," +
			"  PRIMARY KEY (id));"
		);
		connect.createStatement().executeUpdate(
			"CREATE TABLE IF NOT EXISTS user (" +
			"  id BIGINT(18) NOT NULL," +
			"  elevated TINYINT(1) NOT NULL DEFAULT 0," +
			"  banned TINYINT(1) NOT NULL DEFAULT 0," +
			"  upvote INT NOT NULL DEFAULT 0," +
			"  PRIMARY KEY (id));"
		);
		connect.createStatement().executeUpdate(
			"CREATE TABLE IF NOT EXISTS goal (" +
			"  id INT NOT NULL," +
			"  last INT NOT NULL);"
		);
		
		replaceElevated(Long.valueOf(Config.getOwner()), true); //Add owner to elevated
		
		//Import guild list
		ResultSet rs = connect.createStatement().executeQuery(
			"SELECT * FROM guild;"
		);
		while (rs.next()) {
			long id = rs.getLong(1);
			guilds.put(id, new DbGuild(
				id,
				rs.getString(2),
				rs.getBoolean(3),
				rs.getBoolean(4)
			));
		}
		rs.close();
		
		//Import user list
		rs = connect.createStatement().executeQuery(
			"SELECT * FROM user;"
		);
		while (rs.next()) {
			long id = rs.getLong(1);
			users.put(id, new DbUser(
				id,
				rs.getBoolean(2),
				rs.getBoolean(3),
				rs.getInt(4)
			));
		}
		rs.close();
		
		//Import current goal
		rs = connect.createStatement().executeQuery(
			"SELECT last FROM goal;"
		);
		while (rs.next()) {
			int latest = rs.getInt(1);
			if (latest > goal) goal = latest;
		}
		
		System.out.println("Database connected.");
		
	}
	
	public static void changePrefix(long id, String prefix) throws SQLException {
		
		if (prefix.equals(Config.getPrefix())) { //If prefix is being reset to default
			//If user is in database, has not been banned, and has not upvoted
			PreparedStatement st = connect.prepareStatement(
				"SELECT banned, noCooldown FROM guild WHERE id = ?;"
			);
			st.setLong(1, id);
			ResultSet rs = st.executeQuery();
			if (rs.next() && !rs.getBoolean(1) && !rs.getBoolean(2)) {
				//Delete guild
				st = connect.prepareStatement(
					"DELETE FROM guild WHERE id = ?;"
				);
				st.setLong(1, id);
				st.executeUpdate();
				guilds.remove(id); //Mirror change locally
				return;
			}
		}
		replacePrefix(id, prefix); //Update guild with new prefix
		
	}
	
	private static void replacePrefix(long id, String prefix) throws SQLException {
		PreparedStatement st = connect.prepareStatement(
			"REPLACE INTO guild (id, prefix) VALUES(?, ?);"
		);
		st.setLong(1, id);
		st.setString(2, prefix);
		st.executeUpdate();

		//Mirror change in local guild list
		DbGuild g = getGuild(id);
		if (g == null) {
			guilds.put(id, new DbGuild(id, prefix, false, false));
		} else {
			g.prefix = prefix;
		}
	}
	
	public static String getPrefix(long id) {
		DbGuild guild = getGuild(id);
		return guild == null ? Config.getPrefix() : guild.prefix;
	}
	
	public static DbGuild getGuild(long id) {
		return guilds.get(id);
	}
	
	public static void changeElevated(long id, boolean elevated) throws SQLException {
		
		if (!elevated) { //If demoting
			//If user is in database, has not been banned, and has not upvoted
			PreparedStatement st = connect.prepareStatement(
				"SELECT banned, upvoted FROM user WHERE id = ?;"
			);
			st.setLong(1, id);
			ResultSet rs = st.executeQuery();
			if (rs.next() && !rs.getBoolean(1) && rs.getInt(2) != 0) {
				//Delete user
				st = connect.prepareStatement(
					"DELETE FROM user WHERE id = ?;"
				);
				st.setLong(1, id);
				st.executeUpdate();
				users.remove(id); //Mirror change locally
				return;
			}
		}
		replaceElevated(id, elevated); //Update elevation
		
	}
	
	private static void replaceElevated(long id, boolean elevated) throws SQLException {
		PreparedStatement st = connect.prepareStatement(
			"REPLACE INTO user (id, elevated) VALUES(?, ?);"
		);
		st.setLong(1, id);
		st.setBoolean(2, elevated);
		st.executeUpdate();
		
		//Mirror change in local user list
		DbUser u = getUser(id);
		if (u == null) {
			users.put(id, new DbUser(id, elevated, false, 0));
		} else {
			u.elevated = elevated;
		}
	}
	
	public static boolean isElevated(long id) {
		DbUser user = getUser(id);
		return user == null ? false : user.elevated;
	}
	
	public static void changeUpvote(long id, int upvote) throws SQLException {
		PreparedStatement st = connect.prepareStatement(
			"REPLACE INTO user (id, upvote) VALUES(?, ?);"
		);
		st.setLong(1, id);
		st.setInt(2, upvote);
		st.executeUpdate();
		
		//Mirror change in local user list
		DbUser u = getUser(id);
		if (u == null) {
			users.put(id, new DbUser(id, false, false, upvote));
		} else {
			u.upvote = upvote;
		}
	}
	
	public static int getUpvote(long id) {
		DbUser user = getUser(id);
		return user == null ? 0 : user.upvote;
	}
	
	public static DbUser getUser(long id) {
		return users.get(id);
	}
	
	public static int getGoal() {return goal;}

}
