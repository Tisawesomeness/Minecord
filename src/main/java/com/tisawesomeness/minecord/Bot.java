package com.tisawesomeness.minecord;

import com.tisawesomeness.minecord.command.CommandRegistry;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.database.VoteHandler;
import com.tisawesomeness.minecord.listen.CommandListener;
import com.tisawesomeness.minecord.listen.GuildCountListener;
import com.tisawesomeness.minecord.listen.ReactListener;
import com.tisawesomeness.minecord.listen.ReadyListener;
import com.tisawesomeness.minecord.setting.SettingRegistry;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.DiscordUtils;
import com.tisawesomeness.minecord.util.RequestUtils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import okhttp3.OkHttpClient;
import org.discordbots.api.client.DiscordBotListAPI;

import javax.security.auth.login.LoginException;
import java.awt.Color;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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
	public static final String version = "1.0.0-ALPHA";
	public static final String jdaVersion = "4.2.0_168";
	public static final Color color = Color.GREEN;

	// Only use what's necessary
	private static final List<GatewayIntent> gateways = Arrays.asList(
			GatewayIntent.DIRECT_MESSAGES, GatewayIntent.DIRECT_MESSAGE_REACTIONS,
			GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS);
	private static final EnumSet<CacheFlag> disabledCacheFlags = EnumSet.of(
			CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.EMOTE, CacheFlag.VOICE_STATE);

	private ScheduledExecutorService menuExe;
	private ScheduledExecutorService updateExe;
	private ScheduledFuture<?> updateFuture;

	private Config config;
	private DiscordBotListAPI api;
	private CommandRegistry registry;
	private EventListener commandListener;
	private EventListener guildCountListener;
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
			if (config.useAnnouncements) {
                announceRegistry = new AnnounceRegistry(args.getAnnouncePath(), config);
            }
		} catch (IOException ex) {
			ex.printStackTrace();
			return;
		}
		menuExe = Executors.newSingleThreadScheduledExecutor();
		menuExe.scheduleAtFixedRate(ReactMenu::purge, 10, 1, TimeUnit.MINUTES);

		CountDownLatch readyLatch = new CountDownLatch(config.shardCount);
		EventListener readyListener = new ReadyListener(readyLatch);
		EventListener reactListener = new ReactListener();
		guildCountListener = new GuildCountListener(this, config);
		
		// Connect to database
		ExecutorService exe = Executors.newSingleThreadExecutor();
		Future<Database> futureDB = exe.submit(() -> new Database(config));

		try {
			// Initialize JDA
			shardManager = DefaultShardManagerBuilder.create(gateways)
				.setToken(config.clientToken)
				.setAutoReconnect(true)
				.addEventListeners(readyListener)
				.setShardsTotal(config.shardCount)
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
		if (config.sendServerCount) {
			api = new DiscordBotListAPI.Builder().token(config.orgToken).build();
		} else {
		    api = null;
        }

		// Wait for database
		try {
			database = futureDB.get();
		} catch (ExecutionException ex) {
			ex.printStackTrace();
			return;
		} catch (InterruptedException ex) {
			throw new AssertionError(
					"It should not be possible to interrupt the main thread before the bot can accept commands.");
		}

		// These depend on database
		registry = new CommandRegistry(shardManager, database);
		commandListener = new CommandListener(this, config, registry);
		settings = new SettingRegistry(config);

		// Bot has started, start accepting messages
		shardManager.addEventListener(commandListener, reactListener, guildCountListener);
		System.out.println("Bot ready!");

		// Start web server
		Future<VoteHandler> futureVH = null;
		if (config.receiveVotes) {
			futureVH = exe.submit(() -> new VoteHandler(this, config));
		}
		
		// Post-init
		bootTime = System.currentTimeMillis() - birth;
		System.out.println("Boot Time: " + DateUtils.getBootTime(bootTime));
		log(":white_check_mark: **Bot started!**");
		if (config.updateTime > 0) {
			updateExe = Executors.newSingleThreadScheduledExecutor();
			scheduleUpdate();
		}

		// Make sure vote handler finishes
		if (futureVH != null) {
			try {
				voteHandler = futureVH.get();
			} catch (ExecutionException | InterruptedException ex) {
				// It's possible to be interrupted if the shutdown command
				// after the bot starts but before the vote handler starts
				ex.printStackTrace();
			}
		}
		exe.shutdown();
		
	}

	private void scheduleUpdate() {
		updateFuture = updateExe.scheduleAtFixedRate(() -> {
			DiscordUtils.update(shardManager, config);
			sendGuilds(shardManager, config);
		}, 0, config.updateTime, TimeUnit.SECONDS);
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
	 * @throws IOException If a file wasn't found, or there was an error starting the vote server.
	 * @throws ExecutionException If the database couldn't open, the initial read failed, or creating a missing table failed.
	 * If there is an exception, shut down the bot with &shutdown or do a hard reset.
	 */
	public void reload() throws IOException, ExecutionException, InterruptedException {
		System.out.println("Reloading...");

		// Closing everything down
		if (config.receiveVotes) {
			voteHandler.close();
		}
		if (config.updateTime > 0) {
			updateFuture.cancel(false);
		}
		shardManager.removeEventListener(commandListener, guildCountListener);
		config = new Config(args.getConfigPath(), args.getTokenOverride());

		// Database and vote handler need separate threads
		ExecutorService exe = Executors.newSingleThreadExecutor();
		Future<Database> futureDB = exe.submit(() -> new Database(config));
		Future<VoteHandler> futureVH = null;
		if (config.receiveVotes) {
			futureVH = exe.submit(() -> new VoteHandler(this, config));
		}

		// These can be started before the database
        if (config.useAnnouncements) {
            announceRegistry = new AnnounceRegistry(args.getAnnouncePath(), config);
        }
        settings = new SettingRegistry(config);
        guildCountListener = new GuildCountListener(this ,config);
        if (updateExe == null) {
        	updateExe = Executors.newSingleThreadScheduledExecutor();
		}
		if (config.updateTime > 0) {
			scheduleUpdate();
		}

		// Login to API with new token
        if (config.sendServerCount) {
            api = new DiscordBotListAPI.Builder().token(config.orgToken).build();
        } else {
            api = null;
        }

		// Start everything up again
		database = futureDB.get();
        registry = new CommandRegistry(shardManager, database);
        commandListener = new CommandListener(this, config, registry);
        shardManager.addEventListener(commandListener, guildCountListener);
		if (futureVH != null) {
			voteHandler = futureVH.get();
		}
		exe.shutdown();

	}

	/**
	 * Gracefully shuts down the bot.Z
	 * <br>Use this method instead of {@link System#exit(int)} except for emergencies.
	 */
	public void shutdown() {
		System.out.println("Shutting down...");
		menuExe.shutdown();
		updateExe.shutdown();
		shardManager.shutdown();
		if (config.receiveVotes) {
			voteHandler.close();
		}
		for (JDA jda : shardManager.getShards()) {
			OkHttpClient client = jda.getHttpClient();
			client.connectionPool().evictAll();
			client.dispatcher().executorService().shutdown();
			jda.shutdown();
		}
	}

	/**
	 * Logs a message to the logging channel.
	 */
	public void log(String m) {
		long logChannel = config.logChannel;
		if (logChannel == 0) {
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
		long logChannel = config.logChannel;
		if (logChannel == 0) {
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
		long logChannel = config.logChannel;
		if (logChannel == 0) {
			return;
		}
		TextChannel c = shardManager.getTextChannelById(logChannel);
		if (c == null) {
			return;
		}
		EmbedBuilder eb = new EmbedBuilder(m).setTimestamp(OffsetDateTime.now());
		c.sendMessage(eb.build()).queue();
	}

    /**
     * Sends the guild count
     * @param sm The ShardManager used to determine the guild count
     */
    public void sendGuilds(ShardManager sm, Config config) {
        if (config.sendServerCount) {
            int servers = sm.getGuilds().size();
            String id = sm.getShardById(0).getSelfUser().getId();

            String url = "https://bots.discord.pw/api/bots/" + id + "/stats";
            String query = "{\"server_count\": " + servers + "}";
            RequestUtils.post(url, query, config.pwToken);

            /*
             * url = "https://discordbots.org/api/bots/" + id + "/stats"; query =
             * "{\"server_count\": " + servers + "}"; post(url, query,
             * Config.getOrgToken());
             */

            List<Integer> serverCounts = new ArrayList<>();
            for (JDA jda : sm.getShards()) {
                serverCounts.add(jda.getGuilds().size());
            }
            api.setStats(id, serverCounts);
        }
    }

}
