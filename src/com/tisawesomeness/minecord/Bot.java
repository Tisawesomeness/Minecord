package com.tisawesomeness.minecord;

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import org.apache.commons.lang3.ArrayUtils;
import org.discordbots.api.client.DiscordBotListAPI;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.Registry;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.database.VoteHandler;
import com.tisawesomeness.minecord.util.DiscordUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.RequestUtils;

import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

public class Bot {

	private static final String mainClass = "com.tisawesomeness.minecord.Main";
	public static final String helpServer = "https://discord.io/minecord";
	public static final String website = "https://minecord.github.io";
	private static final String version = "0.7.0-BETA-3";
	
	public static ShardManager shardManager;
	public static String id;
	private static Listener listener;
	public static Config config;
	public static long birth;
	public static String[] args;
	
	public static Thread thread;
	
	public static boolean setup(String[] args, boolean devMode) {
		if (!devMode) System.out.println("Bot starting.");
		
		//Parse arguments
		Bot.args = args;
		Config.read(false);
		if (Config.getDevMode() && !devMode) return false;
		boolean reload = false;
		if (args.length > 0 && ArrayUtils.contains(args, "-r")) reload = true;
		
		//Pre-init
		thread = Thread.currentThread();
		listener = new Listener();
		Registry.init();
		
		//Connect to database
		Thread db = new Thread() {
			public void run() {
				try {
					Database.init();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		};
		db.start();
		
		//Start web server
		Thread ws = null;
		if (Config.getReceiveVotes()) {
			ws = new Thread() {
				public void run() {
					try {
						VoteHandler.init();
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			};
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
					jda.addEventListener(listener);
				}
				m.editMessage(":white_check_mark: **Bot reloaded!**").queue();
				MessageUtils.log(":arrows_counterclockwise: **Bot reloaded by " + u.getName() + "**");
				
			//If this is the first run
			} else {
				
				//Initialize JDA
				shardManager = new DefaultShardManagerBuilder()
					.setToken(Config.getClientToken())
					.setAudioEnabled(false)
					.setAutoReconnect(true)
					.addEventListeners(listener)
					.setShardsTotal(Config.getShardCount())
					.setGame(Game.playing("Loading..."))
					.build();
				
				//Update main class
				birth = System.currentTimeMillis();
				if (Config.getDevMode()) {
					MethodName.SET_SHARDS.method().invoke(null, shardManager);
					MethodName.SET_BIRTH.method().invoke(null, birth);
				}
				
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		//Start discordbots.org API
		if (Config.getSendServerCount() || Config.getReceiveVotes()) {
			RequestUtils.api = new DiscordBotListAPI.Builder().token(Config.getOrgToken()).build();
		}
		
		//Wait for database and web server
		try {
			db.join();
			if (ws != null) ws.join();
			while (shardManager.getShardsQueued() > 0) {
				Thread.sleep(1000);
			}
			System.out.println("Done");
		} catch (InterruptedException ex) {}

		MessageUtils.log(":white_check_mark: **Bot started!**");
		
		//Update persistent bot info
		id = shardManager.getShardById(0).getSelfUser().getId();
		if (!Config.getOwner().equals("0")) {
			MessageUtils.owner = shardManager.getUserById(Config.getOwner());
		}
		if (!Config.getLogChannel().equals("0")) {
			MessageUtils.logChannel = shardManager.getTextChannelById(Config.getLogChannel());
		}
		
		//Post-init
		DiscordUtils.update();
		System.out.println("Startup finished.");
		RequestUtils.sendGuilds();
		
		return true;
		
	}
	
	public static void shutdown(Message m, User u) {
		
		//Disable JDA
		for (JDA jda : shardManager.getShards()) {
			jda.setAutoReconnect(false);
			jda.removeEventListener(listener);
		}
		try {
			//Reload this class using reflection
			String[] args = new String[]{"-r"};
			ArrayUtils.addAll(args, Bot.args);
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
		SET_BIRTH("setBirth");
		
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
