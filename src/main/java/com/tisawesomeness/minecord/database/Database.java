package com.tisawesomeness.minecord.database;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.util.RequestUtils;

import com.google.common.base.Splitter;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import lombok.Cleanup;
import lombok.NonNull;
import org.sqlite.SQLiteDataSource;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Database {

	private static final int VERSION = 1;
	private final DataSource source;
	private final LoadingCache<Long, Optional<DbGuild>> guilds;
	private final LoadingCache<Long, Optional<DbUser>> users;

	private Connection getConnect() throws SQLException {
		return source.getConnection();
	}

	/**
	 * Sets up the database connection pool and creates the caches.
	 * @throws SQLException when either the initial read or creating a missing table fails.
	 */
	public Database(Config config) throws SQLException {

		String url = "jdbc:sqlite:" + config.dbPath;
		SQLiteDataSource ds = new SQLiteConnectionPoolDataSource();
		ds.setUrl(url);
		source = ds;

		CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder()
				.expireAfterAccess(10, TimeUnit.MINUTES);
		if (config.debugMode) {
			builder.recordStats();
		}
		guilds = builder.build(new CacheLoader<Long, Optional<DbGuild>>() {
			@Override
			public Optional<DbGuild> load(@NonNull Long key) throws SQLException {
				return loadGuild(key);
			}
		});
		users = builder.build(new CacheLoader<Long, Optional<DbUser>>() {
			@Override
			public Optional<DbUser> load(@NonNull Long key) throws SQLException {
				return loadUser(key);
			}
		});

		// For now, only creating the database is needed
		// In the future, every database change increments the version
		// and this code will run the correct upgrade scripts
		if (getVersion() != VERSION) {
			runScript("init.sql");
		}

		if (!config.owner.equals("0")) {
			changeElevated(Long.parseLong(config.owner), true);
		}

		System.out.println("Database connected.");
		
	}

	/**
	 * Gets the current Minecord database version, used to determine which upgrade scripts to use.
	 * @return A positive integer version, or 0 if the version is not tracked.
	 */
	private int getVersion() throws SQLException {
		@Cleanup Connection connect = getConnect();
		@Cleanup Statement st = connect.createStatement();
		// Query returns 1 result if table exists, 0 results if table does not exist
		@Cleanup ResultSet tableRS = st.executeQuery(
				"SELECT name FROM sqlite_master WHERE type='table' AND name='minecord';"
		);
		// The first next() call returns false if there are 0 results
		if (!tableRS.next()) {
			return 0;
		}
		@Cleanup ResultSet versionRS = st.executeQuery(
				"SELECT version FROM minecord;"
		);
		// Minecord table has only one row
		versionRS.next();
		return versionRS.getInt("version");
	}

	public CacheStats getGuildStats() {
		return guilds.stats();
	}
	public CacheStats getUserStats() {
		return users.stats();
	}

	private Optional<DbGuild> loadGuild(@NonNull Long key) throws SQLException {
		@Cleanup Connection connect = getConnect();
		@Cleanup PreparedStatement st = connect.prepareStatement(
				"SELECT * FROM guild WHERE id = ?;"
		);
		st.setLong(1, key);
		@Cleanup ResultSet rs = st.executeQuery();
		// The first next() call returns true if results exist
		return rs.next() ? Optional.of(DbGuild.from(rs)) : Optional.empty();
	}
	private Optional<DbUser> loadUser(@NonNull Long key) throws SQLException {
		@Cleanup Connection connect = getConnect();
		@Cleanup PreparedStatement st = connect.prepareStatement(
				"SELECT * FROM user WHERE id = ?;"
		);
		st.setLong(1, key);
		@Cleanup ResultSet rs = st.executeQuery();
		// The first next() call returns true if results exist
		return rs.next() ? Optional.of(DbUser.from(rs)) : Optional.empty();
	}

	public boolean isElevated(long id) {
		return getUser(id).map(u -> u.elevated).orElse(false);
	}
	public boolean isBanned(long id) {
		return getUser(id).map(u -> u.banned).orElse(
				getGuild(id).map(g -> g.banned).orElse(false)
		);
	}

	/**
	 * Runs a .sql script from resources.
	 * <br>This assumes that each statement in the script is separated by semicolons.
	 * @param resourceName The filename of the script in the resources folder.
	 * @throws SQLException If there is an error executing the script.
	 */
	private void runScript(String resourceName) throws SQLException {
		String initScript = RequestUtils.loadResource(resourceName);
		@Cleanup Connection connect = getConnect();
		@Cleanup Statement statement = connect.createStatement();
		for (String query : Splitter.on(";").omitEmptyStrings().split(initScript)) {
			statement.executeUpdate(query);
		}
	}

	/**
	 * Either gets a guild from the cache or queries the database for it.
	 * <br>The guild is cleared from the cache once it is altered.
	 * @param id The guild id.
	 * @return The guild if present, or an empty Optional if not present in the database or an exception occured.
	 */
	public Optional<DbGuild> getGuild(long id) {
		try {
			return guilds.get(id);
		} catch (ExecutionException ex) {
			ex.printStackTrace();
		}
		return Optional.empty();
	}
	/**
	 * Either gets a user from the cache or queries the database for it.
	 * <br>The user is cleared from the cache once it is altered.
	 * @param id The user id.
	 * @return The user if present, or an empty Optional if not present in the database or an exception occured.
	 */
	public Optional<DbUser> getUser(long id) {
		try {
			return users.get(id);
		} catch (ExecutionException ex) {
			ex.printStackTrace();
		}
		return Optional.empty();
	}
	
	public void changePrefix(long id, String prefix) throws SQLException {
		@Cleanup Connection connect = getConnect();
		@Cleanup PreparedStatement st = connect.prepareStatement(
				"INSERT INTO guild (id, prefix)" +
						"  VALUES(?, ?)" +
						"  ON CONFLICT (id) DO" +
						"  UPDATE SET prefix = ?;"
		);
		st.setLong(1, id);
		st.setString(2, prefix);
		st.setString(3, prefix);
		st.executeUpdate();
		guilds.invalidate(id);
	}
	public void changeBannedGuild(long id, boolean banned) throws SQLException {
		@Cleanup Connection connect = getConnect();
		@Cleanup PreparedStatement st = connect.prepareStatement(
				"INSERT INTO guild (id, banned)" +
						"  VALUES(?, ?)" +
						"  ON CONFLICT (id) DO" +
						"  UPDATE SET banned = ?;"
		);
		st.setLong(1, id);
		st.setBoolean(2, banned);
		st.setBoolean(3, banned);
		st.executeUpdate();
		guilds.invalidate(id);
	}
	public void changeUseMenu(long id, boolean useMenu) throws SQLException {
		@Cleanup Connection connect = getConnect();
		@Cleanup PreparedStatement st = connect.prepareStatement(
				"INSERT INTO guild (id, noMenu)" +
						"  VALUES(?, ?)" +
						"  ON CONFLICT (id) DO" +
						"  UPDATE SET noMenu = ?;"
		);
		st.setLong(1, id);
		st.setBoolean(2, !useMenu);
		st.setBoolean(3, !useMenu);
		st.executeUpdate();
		guilds.invalidate(id);
	}
	
	public void changeElevated(long id, boolean elevated) throws SQLException {
		@Cleanup Connection connect = getConnect();
		@Cleanup PreparedStatement st = connect.prepareStatement(
				"INSERT INTO user (id, elevated)" +
						"  VALUES(?, ?)" +
						"  ON CONFLICT (id) DO" +
						"  UPDATE SET elevated = ?;"
		);
		st.setLong(1, id);
		st.setBoolean(2, elevated);
		st.setBoolean(3, elevated);
		st.executeUpdate();
		users.invalidate(id);
	}
	public void changeBannedUser(long id, boolean banned) throws SQLException {
		@Cleanup Connection connect = getConnect();
		@Cleanup PreparedStatement st = connect.prepareStatement(
				"INSERT INTO user (id, banned)" +
						"  VALUES(?, ?)" +
						"  ON CONFLICT (id) DO" +
						"  UPDATE SET banned = ?;"
		);
		st.setLong(1, id);
		st.setBoolean(2, banned);
		st.setBoolean(3, banned);
		st.executeUpdate();
		users.invalidate(id);
	}

}
