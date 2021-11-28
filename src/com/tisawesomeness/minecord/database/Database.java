package com.tisawesomeness.minecord.database;

import com.tisawesomeness.minecord.Config;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class Database {
	
	private static DataSource source;
	private static final HashMap<Long, DbGuild> guilds = new HashMap<>();
	private static final HashMap<Long, DbUser> users = new HashMap<>();
	
	private static Connection getConnect() throws SQLException {
		return source.getConnection();
	}
	
	public static void init() throws SQLException {
		
		//Build database source
		String url = "jdbc:";
		if (Config.getType().equals("mysql")) {
			url += "mysql://";
			try {
				Class.forName("com.mysql.jdbc.Driver").getDeclaredConstructor().newInstance();
			} catch (ReflectiveOperationException ex) {
				ex.printStackTrace();
			}
		} else {
			url += "sqlite:";
		}
		url += Config.getHost();
		if (Config.getType().equals("mysql")) {
			MysqlDataSource ds = new MysqlDataSource();
			ds.setUrl(url + ":" + Config.getPort() + "/" + Config.getDbName());
			ds.setUser(Config.getUser());
			ds.setPassword(Config.getPass());
			ds.setUseSSL(false);
			source = ds;
		} else {
			SQLiteDataSource ds = new SQLiteDataSource();
			ds.setUrl(url);
			source = ds;
		}

		//Connect to database
		Connection connect = getConnect();
		
		//Create tables if they do not exist
		connect.createStatement().executeUpdate(
			"CREATE TABLE IF NOT EXISTS guild (" +
			"  id BIGINT(18) NOT NULL," +
			"  prefix TINYTEXT," +
			"  lang TINYTEXT," +
			"  banned TINYINT(1) NOT NULL DEFAULT 0," +
			"  noCooldown TINYINT(1) NOT NULL DEFAULT 0," +
			"  deleteCommands TINYINT(1)," +
			"  noMenu TINYINT(1)," +
			"  PRIMARY KEY (id));"
		);
		connect.createStatement().executeUpdate(
			"CREATE TABLE IF NOT EXISTS user (" +
			"  id BIGINT(18) NOT NULL," +
			"  elevated TINYINT(1) NOT NULL DEFAULT 0," +
			"  banned TINYINT(1) NOT NULL DEFAULT 0," +
			"  PRIMARY KEY (id));"
		);
		
		// Add owner to elevated
		if (!Config.getOwner().equals("0")) {
			changeElevated(Long.parseLong(Config.getOwner()), true);
		}
		
		refresh();
		System.out.println("Database connected.");
		
	}

	public static void refresh() throws SQLException {
		Connection connect = getConnect();

		//Import guild list
		ResultSet rs = connect.createStatement().executeQuery(
			"SELECT * FROM guild;"
		);
		while (rs.next()) {
			long id = rs.getLong(1);
			Boolean deleteCommands = rs.getBoolean(6);
			if (rs.wasNull()) deleteCommands = null;
			Boolean noMenu = rs.getBoolean(7);
			if (rs.wasNull()) noMenu = null;
			guilds.put(id, new DbGuild(
				id,
				rs.getString(2),
				rs.getString(3),
				rs.getBoolean(4),
				rs.getBoolean(5),
				deleteCommands,
				noMenu
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
				rs.getBoolean(3)
			));
		}
		rs.close();
	}
	
	public static void close() throws SQLException {
		Connection connect = getConnect();
		if (connect != null) connect.close();
	}
	
	public static void changePrefix(long id, String prefix) throws SQLException {

		Connection connect = getConnect();
		PreparedStatement st = connect.prepareStatement(
			"REPLACE INTO guild (id, prefix, lang, banned, noCooldown, deleteCommands, noMenu) VALUES(?, ?, " +
			"COALESCE((SELECT lang FROM (SELECT * FROM guild) AS temp WHERE id=?), NULL), " +
			"COALESCE((SELECT banned FROM (SELECT * FROM guild) AS temp WHERE id=?), 0), " +
			"COALESCE((SELECT noCooldown FROM (SELECT * FROM guild) AS temp WHERE id=?), 0), " +
			"COALESCE((SELECT deleteCommands FROM (SELECT * FROM guild) AS temp WHERE id=?), NULL), " +
			"COALESCE((SELECT noMenu FROM (SELECT * FROM guild) AS temp WHERE id=?), NULL));"
		);
		st.setLong(1, id);
		st.setString(2, prefix);
		st.setLong(3, id);
		st.setLong(4, id);
		st.setLong(5, id);
		st.setLong(6, id);
		st.setLong(7, id);
		st.executeUpdate();

		//Mirror change in local guild list
		DbGuild g = guilds.get(id);
		if (g == null) {
			guilds.put(id, new DbGuild(id, prefix, null, false, false, null, null));
		} else {
			g.prefix = prefix;
		}
		
		//Delete guild if it contains only default values
		if (prefix.equals(Config.getPrefix())) {
			purgeGuilds(id);
		}
		
	}
	
	public static String getPrefix(long id) {
		DbGuild guild = guilds.get(id);
		String prefix = guild == null ? Config.getPrefix() : guild.prefix;
		return prefix == null ? Config.getPrefix() : prefix;
	}
	
	public static void changeBannedGuild(long id, boolean banned) throws SQLException {

		Connection connect = getConnect();
		PreparedStatement st = connect.prepareStatement(
			"REPLACE INTO guild (id, prefix, lang, banned, noCooldown, deleteCommands, noMenu) VALUES(?, " +
			"COALESCE((SELECT prefix FROM (SELECT * FROM guild) AS temp WHERE id=?), NULL), " +
			"COALESCE((SELECT lang FROM (SELECT * FROM guild) AS temp WHERE id=?), NULL), ?, " +
			"COALESCE((SELECT noCooldown FROM (SELECT * FROM guild) AS temp WHERE id=?), 0), " +
			"COALESCE((SELECT deleteCommands FROM (SELECT * FROM guild) AS temp WHERE id=?), 0), " +
			"COALESCE((SELECT noMenu FROM (SELECT * FROM guild) AS temp WHERE id=?), 0));"
		);
		st.setLong(1, id);
		st.setLong(2, id);
		st.setLong(3, id);
		st.setBoolean(4, banned);
		st.setLong(5, id);
		st.setLong(6, id);
		st.setLong(7, id);
		st.executeUpdate();
		
		//Mirror change in local user list
		DbGuild g = guilds.get(id);
		if (g == null) {
			guilds.put(id, new DbGuild(id, Config.getPrefix(), null, banned, false, null, null));
		} else {
			g.banned = banned;
		}
		
		//Delete guild if it contains only default values
		if (!banned) purgeGuilds(id);
		
	}
	
	public static void changeDeleteCommands(long id, boolean deleteCommands) throws SQLException {

		Connection connect = getConnect();
		PreparedStatement st = connect.prepareStatement(
			"REPLACE INTO guild (id, prefix, lang, banned, noCooldown, deleteCommands, noMenu) VALUES(?, " +
			"COALESCE((SELECT prefix FROM (SELECT * FROM guild) AS temp WHERE id=?), NULL), " +
			"COALESCE((SELECT lang FROM (SELECT * FROM guild) AS temp WHERE id=?), NULL), " +
			"COALESCE((SELECT banned FROM (SELECT * FROM guild) AS temp WHERE id=?), 0), " +
			"COALESCE((SELECT noCooldown FROM (SELECT * FROM guild) AS temp WHERE id=?), 0), ?, " +
			"COALESCE((SELECT noMenu FROM (SELECT * FROM guild) AS temp WHERE id=?), NULL));"
		);
		st.setLong(1, id);
		st.setLong(2, id);
		st.setLong(3, id);
		st.setLong(4, id);
		st.setLong(5, id);
		st.setBoolean(6, deleteCommands);
		st.setLong(7, id);
		st.executeUpdate();

		//Mirror change in local guild list
		DbGuild g = guilds.get(id);
		if (g == null) {
			guilds.put(id, new DbGuild(id, Config.getPrefix(), null, false, false, deleteCommands, null));
		} else {
			g.deleteCommands = deleteCommands;
		}
		
	}
	
	public static boolean getDeleteCommands(long id) {
		DbGuild guild = guilds.get(id);
		Boolean deleteCommands;
		if (guild == null) {
			deleteCommands = Config.getDeleteCommands();
		} else {
			deleteCommands = guild.deleteCommands == null ? null : guild.deleteCommands; // Blame java
		}
		return deleteCommands == null ? Config.getDeleteCommands() : deleteCommands;
	}
	
	public static void changeUseMenu(long id, boolean useMenu) throws SQLException {

		Connection connect = getConnect();
		PreparedStatement st = connect.prepareStatement(
			"REPLACE INTO guild (id, prefix, lang, banned, noCooldown, deleteCommands, noMenu) VALUES(?, " +
			"COALESCE((SELECT prefix FROM (SELECT * FROM guild) AS temp WHERE id=?), NULL), " +
			"COALESCE((SELECT lang FROM (SELECT * FROM guild) AS temp WHERE id=?), NULL), " +
			"COALESCE((SELECT banned FROM (SELECT * FROM guild) AS temp WHERE id=?), 0), " +
			"COALESCE((SELECT noCooldown FROM (SELECT * FROM guild) AS temp WHERE id=?), 0), " +
			"COALESCE((SELECT deleteCommands FROM (SELECT * FROM guild) AS temp WHERE id=?), NULL), ?);"
		);
		st.setLong(1, id);
		st.setLong(2, id);
		st.setLong(3, id);
		st.setLong(4, id);
		st.setLong(5, id);
		st.setLong(6, id);
		st.setBoolean(7, !useMenu);
		st.executeUpdate();

		//Mirror change in local guild list
		DbGuild g = guilds.get(id);
		if (g == null) {
			guilds.put(id, new DbGuild(id, Config.getPrefix(), null, false, false, null, !useMenu));
		} else {
			g.noMenu = !useMenu;
		}
		
	}
	
	public static boolean getUseMenu(long id) {
		DbGuild guild = guilds.get(id);
		Boolean useMenu;
		if (guild == null) {
			useMenu = Config.getUseMenus();
		} else {
			useMenu = guild.noMenu == null ? null : !guild.noMenu; // Blame java
		}
		return useMenu == null ? Config.getUseMenus() : useMenu;
	}

	private static void purgeGuilds(long id) throws SQLException {
		PreparedStatement st = getConnect().prepareStatement(
			"DELETE FROM guild WHERE prefix IS NULL AND lang IS NULL AND banned=0 AND noCooldown=0 AND deleteCommands IS NULL AND noMenu IS NULL;"
		);
		if (st.executeUpdate() > 0) guilds.remove(id);
	}
	
	public static void changeElevated(long id, boolean elevated) throws SQLException {

		Connection connect = getConnect();
		PreparedStatement st = connect.prepareStatement(
			"REPLACE INTO user (id, elevated, banned) VALUES(?, ?, " +
			"COALESCE((SELECT banned FROM (SELECT * FROM user) AS temp WHERE id=?), 0));"
		);
		st.setLong(1, id);
		st.setBoolean(2, elevated);
		st.setLong(3, id);
		st.executeUpdate();
		
		//Mirror change in local user list
		DbUser u = users.get(id);
		if (u == null) {
			users.put(id, new DbUser(id, elevated, false));
		} else {
			u.elevated = elevated;
		}
		
		//Delete user if it contains only default values
		if (!elevated) purgeUsers(id);
		
	}
	
	public static boolean isElevated(long id) {
		DbUser user = users.get(id);
		return user == null ? false : user.elevated;
	}
	
	public static void changeBannedUser(long id, boolean banned) throws SQLException {

		Connection connect = getConnect();
		PreparedStatement st = connect.prepareStatement(
			"REPLACE INTO user (id, elevated, banned) VALUES(?, " +
			"COALESCE((SELECT banned FROM (SELECT * FROM user) AS temp WHERE id=?), 0), ?);"
		);
		st.setLong(1, id);
		st.setLong(2, id);
		st.setBoolean(3, banned);
		st.executeUpdate();
		
		//Mirror change in local user list
		DbUser u = users.get(id);
		if (u == null) {
			users.put(id, new DbUser(id, false, banned));
		} else {
			u.banned = banned;
		}
		
		//Delete user if it contains only default values
		if (!banned) purgeUsers(id);
		
	}

	private static void purgeUsers(long id) throws SQLException {
		PreparedStatement st = getConnect().prepareStatement(
			"DELETE FROM user WHERE banned=0 AND elevated=0;"
		);
		if (st.executeUpdate() > 0) users.remove(id);
	}
	
	public static boolean isBanned(long id) {
		DbGuild guild = guilds.get(id);
		if (guild != null) return guild.banned;
		DbUser user = users.get(id);
		return user == null ? false : user.banned;
	}

}
