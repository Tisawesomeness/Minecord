package com.tisawesomeness.minecord;

import java.awt.Color;
import java.io.IOException;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.tisawesomeness.minecord.listen.CommandListener;
import com.tisawesomeness.minecord.listen.GuildCountListener;
import com.tisawesomeness.minecord.listen.ReactListener;
import com.tisawesomeness.minecord.listen.ReadyListener;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import org.discordbots.api.client.DiscordBotListAPI;

import com.tisawesomeness.minecord.command.Registry;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.database.VoteHandler;
import com.tisawesomeness.minecord.item.Item;
import com.tisawesomeness.minecord.item.Recipe;
import com.tisawesomeness.minecord.setting.SettingRegistry;
import com.tisawesomeness.minecord.util.ColorUtils;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.DiscordUtils;
import com.tisawesomeness.minecord.util.RequestUtils;

import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;

/**
 * <p>The entry point and central point for Minecord.</p>
 * To start the bot, call {@link #setup(ArgsHandler args)}.
 */
@NoArgsConstructor
public class Bot {

	// Bot constants, all others defined in config
	public static final String author = "Tis_awesomeness";
	public static final String authorTag = "@Tis_awesomeness#8617";
	public static final String helpServer = "https://minecord.github.io/support";
	public static final String website = "https://minecord.github.io";
	public static final String github = "https://github.com/Tisawesomeness/Minecord";
	public static final String version = "0.10.0";
	public static final String javaVersion = "1.8";
	public static final String jdaVersion = "4.1.1_151";
	public static final Color color = Color.GREEN;

	// Only use what's necessary
	private static final List<GatewayIntent> gateways = Arrays.asList(
			GatewayIntent.DIRECT_MESSAGES, GatewayIntent.DIRECT_MESSAGE_REACTIONS,
			GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS);
	private static final EnumSet<CacheFlag> disabledCacheFlags = EnumSet.of(
			CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.EMOTE, CacheFlag.VOICE_STATE);

	private CommandListener commandListener;
	private ReactListener reactListener;
	private ReadyListener readyListener;
	private GuildCountListener guildCountListener;

	private Config config;

	@Getter private ArgsHandler args;
	@Getter private ShardManager shardManager;
	@Getter private Database database;
	@Getter private SettingRegistry settings;
	@Getter private AnnounceRegistry announceRegistry;
	@Getter private VoteHandler voteHandler;
	@Getter private long birth;
	@Getter private long bootTime;

	/**
	 * Starts the bot. Will return early if one of the following happens:
	 * <ul>
	 *     <li>The config or announcement files couldn't load.</li>
	 *     <li>An internal resource file fails to load.</li>
	 *     <li>JDA had an error logging in, usually invalid token.</li>
	 *     <li>The database fails to connect.</li>
	 * </ul>
	 * <b>This method is blocking!</b>
	 * @param args The parsed command-line arguments
	 */
	public void setup(ArgsHandler args) {
		birth = System.currentTimeMillis();
		System.out.println("Bot starting...");
		this.args = args;

		// Pre-init
		try {
			config = new Config(args.getConfigPath(), args.getTokenOverride());
			announceRegistry = new AnnounceRegistry(args.getAnnouncePath(), config);
			ColorUtils.init();
			Item.init();
			Recipe.init();
		} catch (IOException ex) {
			ex.printStackTrace();
			return;
		}
		ReactMenu.startPurgeThread();
		Registry.init();

		CountDownLatch readyLatch = new CountDownLatch(config.getShardCount());
		commandListener = new CommandListener(this, config);
		reactListener = new ReactListener();
		readyListener = new ReadyListener(readyLatch);
		guildCountListener = new GuildCountListener(this, config);
		
		// Connect to database
		database = new Database(config);
		Future<Boolean> db = database.start();

		try {
			// Initialize JDA
			shardManager = DefaultShardManagerBuilder.create(gateways)
				.setToken(config.getClientToken())
				.setAutoReconnect(true)
				.addEventListeners(readyListener)
				.setShardsTotal(config.getShardCount())
				.setActivity(Activity.playing("Loading..."))
				.setMemberCachePolicy(MemberCachePolicy.NONE)
				.disableCache(disabledCacheFlags)
				.build();

			// Wait for shards to ready
			readyLatch.await();
			System.out.println("All shards ready!");

		} catch (LoginException | InterruptedException ex) {
			ex.printStackTrace();
			return;
		}

		// Start discordbots.org API
		if (config.shouldSendServerCount() || config.shouldReceiveVotes()) {
			RequestUtils.api = new DiscordBotListAPI.Builder().token(config.getOrgToken()).build();
		}

		// Wait for database
		try {
			if (!db.get()) {
				return;
			}
		} catch (ExecutionException ex) {
			ex.printStackTrace();
			return;
		} catch (InterruptedException ex) {
			throw new AssertionError();
		}

		// Create settings
		settings = new SettingRegistry(config, database);

		// Bot has started, start accepting messages
		shardManager.addEventListener(commandListener, reactListener, guildCountListener);
		System.out.println("Bot ready!");

		// Start web server
		voteHandler = new VoteHandler(this, config);
		Future<Boolean> ws = voteHandler.start();
		
		// Post-init
		bootTime = System.currentTimeMillis() - birth;
		System.out.println("Boot Time: " + DateUtils.getBootTime(bootTime));
		log(":white_check_mark: **Bot started!**");
		DiscordUtils.update(shardManager, config);
		RequestUtils.sendGuilds(shardManager, config);
		
	}

	/**
	 * Reloads the bot. The parts that get reloaded are:
	 * <ul>
	 *     <li>The config and announce files.</li>
	 *     <li>The database.</li>
	 *     <li>The vote server.</li>
	 *     <li>The internal item and recipe resource files (open the JAR as archive and replace them to reload these).</li>
	 * </ul>
	 * The config is loaded before everything else, so options like login details can change.
	 * @throws SQLException If the database couldn't close.
	 * @throws IOException If a file wasn't found, or there was an error starting the vote server.
	 * @throws ExecutionException If the database couldn't open, the initial read failed, or creating a missing table failed.
	 * If there is an exception, shut down the bot with &shutdown or do a hard reset.
	 */
	public void reload() throws SQLException, IOException, ExecutionException {
		database.close();
		if (config.shouldReceiveVotes()) {
			voteHandler.close();
		}
		config = new Config(args.getConfigPath(), args.getTokenOverride());
		shardManager.restart();
		Database newDatabase = new Database(config);
		Future<Boolean> db = newDatabase.start();
		if (config.shouldReceiveVotes()) {
			voteHandler = new VoteHandler(this, config);
			Future<Boolean> ws = voteHandler.start();
		}
		announceRegistry = new AnnounceRegistry(args.getAnnouncePath(), config);
		Item.init();
		Recipe.init();
		try {
			if (!db.get()) {
				throw new SQLException("Database unable to load.");
			}
		} catch (InterruptedException ex) {
			throw new AssertionError();
		}
	}

	/**
	 * Shuts down the bot and exits the JVM.
	 * @param exit The program exit code, non-zero for failure.
	 */
	public void shutdown(int exit) {
		try {
			shardManager.shutdown();
			database.close();
			voteHandler.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.exit(exit);
	}

	/**
	 * Logs a message to the logging channel.
	 */
	public void log(String m) {
		String logChannel = config.getLogChannel();
		if (logChannel.equals("0")) {
			return;
		}
		TextChannel c = shardManager.getTextChannelById(logChannel);
		if (c == null) {
			return;
		}
		c.sendMessage(m).queue();
	}
	/**
	 * Logs a message to the logging channel.
	 */
	public void log(Message m) {
		String logChannel = config.getLogChannel();
		if (logChannel.equals("0")) {
			return;
		}
		TextChannel c = shardManager.getTextChannelById(logChannel);
		if (c == null) {
			return;
		}
		c.sendMessage(m).queue();
	}
	/**
	 * Logs a message to the logging channel.
	 */
	public void log(MessageEmbed m) {
		String logChannel = config.getLogChannel();
		if (logChannel.equals("0")) {
			return;
		}
		TextChannel c = shardManager.getTextChannelById(logChannel);
		if (c == null) {
			return;
		}
		EmbedBuilder eb = new EmbedBuilder(m).setTimestamp(OffsetDateTime.now());
		c.sendMessage(eb.build()).queue();
	}

}
