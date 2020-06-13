package com.tisawesomeness.minecord;

import java.awt.Color;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.discordbots.api.client.DiscordBotListAPI;

import com.tisawesomeness.minecord.command.Registry;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.database.VoteHandler;
import com.tisawesomeness.minecord.item.Item;
import com.tisawesomeness.minecord.item.Recipe;
import com.tisawesomeness.minecord.util.ColorUtils;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.DiscordUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.RequestUtils;

import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.GatewayIntent;

@NoArgsConstructor
public class Bot {

	private static final String mainClass = "com.tisawesomeness.minecord.Main";
	public static final String author = "Tis_awesomeness";
	public static final String authorTag = "@Tis_awesomeness#8617";
	public static final String helpServer = "https://minecord.github.io/support";
	public static final String website = "https://minecord.github.io";
	public static final String github = "https://github.com/Tisawesomeness/Minecord";
	private static final String version = "0.10.0";
	public static final String javaVersion = "1.8";
	public static final String jdaVersion = "4.1.1_151";
	public static final Color color = Color.GREEN;

	private static final List<GatewayIntent> gateways = Arrays.asList(
			GatewayIntent.DIRECT_MESSAGES, GatewayIntent.DIRECT_MESSAGE_REACTIONS,
			GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS);
	private static final EnumSet<CacheFlag> disabledCacheFlags = EnumSet.of(
			CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.EMOTE, CacheFlag.VOICE_STATE);
	
	public ShardManager shardManager;
	private long id;
	private Listener listener;
	private ReactListener reactListener;
	private ReadyListener readyListener;
	@Getter private VoteHandler voteHandler;
	@Getter private long birth;
	@Getter private long bootTime;
	public String[] args;
	
	public Thread thread;
	private volatile int readyShards = 0;
	
	public boolean setup(String[] args, boolean devMode) {
		long startTime = System.currentTimeMillis();
		if (!devMode) {
			System.out.println("Bot starting...");
		}
		
		//Parse arguments
		this.args = args;
		Config.read(this, false);
		if (Config.getDevMode() && !devMode) return false;
		boolean reload = false;
		if (args.length > 0 && Arrays.asList(args).contains("-r")) reload = true;
		
		//Pre-init
		thread = Thread.currentThread();
		listener = new Listener(this);
		reactListener = new ReactListener();
		readyListener = new ReadyListener(this);
		try {
			Announcement.init(Config.getPath());
			ColorUtils.init(Config.getPath());
			Item.init(Config.getPath());
			Recipe.init(Config.getPath());
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
		ReactMenu.startPurgeThread();
		Registry.init();
		
		//Connect to database
		Thread db = new Thread(() -> {
			try {
				Database.init();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		});
		db.start();
		
		//Start web server
		Thread ws = null;
		if (Config.getReceiveVotes()) {
			ws = new Thread(() -> {
				voteHandler = new VoteHandler(shardManager);
				try {
					voteHandler.init();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			});
			ws.start();
		}
		
		//Fetch main class
		try {
			if (Config.getDevMode()) {
				@SuppressWarnings("static-access")
				Class<?> clazz = Thread.currentThread().getContextClassLoader().getSystemClassLoader()
					.loadClass(mainClass);
				MethodName.clazz = clazz;
			}
			
			//If this is a reload
			if (reload && Config.getDevMode()) {
				
				//Get main class info
				Message m = (Message) MethodName.GET_MESSAGE.method().invoke(null, "ignore");
				User u = (User) MethodName.GET_USER.method().invoke(null, "ignore");
				shardManager = (ShardManager) MethodName.GET_SHARDS.method().invoke(null, "ignore");
				birth = (long) MethodName.GET_BIRTH.method().invoke(null, "ignore");
				//Prepare commands
				for (JDA jda : shardManager.getShards()) {
					jda.setAutoReconnect(true);
					jda.addEventListener(listener, reactListener, readyListener);
				}
				m.editMessage(":white_check_mark: **Bot reloaded!**").queue();
				MessageUtils.log(":arrows_counterclockwise: **Bot reloaded by " + u.getName() + "**");
				
			//If this is the first run
			} else {
				
				//Initialize JDA
				shardManager = DefaultShardManagerBuilder.create(gateways)
					.setToken(Config.getClientToken())
					.setAutoReconnect(true)
					.addEventListeners(listener, reactListener, readyListener)
					.setShardsTotal(Config.getShardCount())
					.setActivity(Activity.playing("Loading..."))
					.setMemberCachePolicy(MemberCachePolicy.NONE)
					.disableCache(disabledCacheFlags)
					.build();
				
				//Update main class
				birth = startTime;
				if (Config.getDevMode()) {
					MethodName.SET_SHARDS.method().invoke(null, shardManager);
					MethodName.SET_BIRTH.method().invoke(null, birth);
				}

				// Wait for shards to ready
				while (readyShards < shardManager.getShardsTotal()) {
					//System.out.println("Ready shards: " + readyShards + " / " + shardManager.getShardsTotal());
					Thread.sleep(100);
				}
				System.out.println("Shards ready");
				
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		
		//Start discordbots.org API
		if (Config.getSendServerCount() || Config.getReceiveVotes()) {
			RequestUtils.api = new DiscordBotListAPI.Builder().token(Config.getOrgToken()).build();
		}

		//Wait for database and web server
		try {
			db.join();
			if (ws != null) ws.join();
			System.out.println("Bot ready!");
		} catch (InterruptedException ex) {}

		//Update persistent bot info
		if (!Config.getLogChannel().equals("0")) {
			MessageUtils.logChannel = shardManager.getTextChannelById(Config.getLogChannel());
		}
		
		//Post-init
		bootTime = System.currentTimeMillis() - birth;
		System.out.println("Boot Time: " + DateUtils.getBootTime(bootTime));
		MessageUtils.log(":white_check_mark: **Bot started!**");
		DiscordUtils.update(shardManager);
		RequestUtils.sendGuilds(shardManager);
		
		return true;
		
	}

	public void addReadyShard() {
		readyShards++;
	}
	
	public void shutdown(Message m, User u) {
		
		//Disable JDA
		for (JDA jda : shardManager.getShards()) {
			jda.setAutoReconnect(false);
			jda.removeEventListener(listener, reactListener, readyListener);
		}
		try {
			//Reload this class using reflection
			ArrayList<String> argsList = new ArrayList<String>(Arrays.asList(args));
			argsList.add(0, "-r");
			String[] args = argsList.toArray(new String[argsList.size()]);
			MethodName.SET_MESSAGE.method().invoke(null, m);
			MethodName.SET_USER.method().invoke(null, u);
			MethodName.LOAD.method().invoke(null, (Object) args);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		//Stop the thread
		thread.interrupt();
		
	}
	
	public static String getVersion() {
		return version;
	}
	
	//Helps with reflection
	private enum MethodName {
		MAIN("main"),
		LOAD("load"),
		GET_MESSAGE("getMessage"),
		SET_MESSAGE("setMessage"),
		GET_USER("getUser"),
		SET_USER("setUser"),
		GET_SHARDS("getShards"),
		SET_SHARDS("setShards"),
		GET_BIRTH("getBirth"),
		SET_BIRTH("setBirth"),
		GET_DEFAULT_LANG("getDefaultLang"),
		GET_LANG("getLang");

		private String name;
		private MethodName(String name) {
			this.name = name;
		}

		public static Class<?> clazz;
		public Method method() {
			for (Method m : clazz.getDeclaredMethods()) {
				if (m.getName().equals(name)) return m;
			}
			return null;
		}
	}

}