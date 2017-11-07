package com.tisawesomeness.minecord;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.ArrayUtils;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.Registry;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.RequestUtils;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.requests.SessionReconnectQueue;
import net.dv8tion.jda.core.utils.SimpleLog;
import net.dv8tion.jda.core.utils.SimpleLog.Level;

public class Bot {

	private static final String mainClass = "com.tisawesomeness.minecord.Main";
	public static final String helpServer = "https://discord.io/minecord";
	public static final String website = "https://minecord.github.io";
	private static final String version = "0.6.0";
	
	public static ArrayList<JDA> shards = new ArrayList<JDA>();
	private static Listener listener;
	public static Config config;
	public static long birth;
	public static String[] args;
	
	public static Thread thread;
	
	@SuppressWarnings("unchecked")
	public static boolean setup(String[] args, boolean devMode) {
		Bot.args = args;
		
		//Parse config path
		String path = "./config.json";
		if (args.length > 1 && ArrayUtils.contains(args, "-c")) {
			int index = ArrayUtils.indexOf(args, "-c");
			if (index + 1 < args.length) {
				path = args[index + 1];
				System.out.println("Found custom config path: " + path);
			}
		}
		Config.read(new File(path)); //Init config
		
		//Exit if dev mode
		if (Config.getDevMode() && !devMode) {return false;}
		
		//Parse reload
		boolean reload = false;
		if (args.length > 0 && ArrayUtils.contains(args, "-r")) {
			reload = true;
		}

		//Pre-init
		thread = Thread.currentThread();
		listener = new Listener();
		Registry.init();
		
		//Fetch main class
		try {
			Class<?> main = null;
			if (Config.getDevMode()) {
				@SuppressWarnings("static-access")
				Class<?> clazz = Thread.currentThread().getContextClassLoader().getSystemClassLoader()
					.loadClass(mainClass);
				main = clazz;
			}
			
			//If this is a reload
			if (reload && Config.getDevMode()) {
				
				//Get main class info
				Object mo = main.getDeclaredMethods()[MethodName.GET_MESSAGE.num].invoke(null, "ignore");
				Message m = (Message) mo;
				Object uo = main.getDeclaredMethods()[MethodName.GET_USER.num].invoke(null, "ignore");
				User u = (User) uo;
				Object so = main.getDeclaredMethods()[MethodName.GET_SHARDS.num].invoke(null, "ignore");
				shards = (ArrayList<JDA>) so;
				Object bo = main.getDeclaredMethods()[MethodName.GET_BIRTH.num].invoke(null, "ignore");
				birth = (long) bo;
				//Prepare commands
				for (JDA jda : shards) {
					jda.addEventListener(listener);
				}
				m.editMessage(":white_check_mark: **Bot reloaded!**").queue();
				MessageUtils.log(":arrows_counterclockwise: **Bot reloaded by " + u.getName() + "**");
				
				//Delete notification
				if (Config.getNotificationTime() >= 0) {
					class Clean extends TimerTask {
						@Override
						public void run() {
							m.delete().queue();
						}
					}
				
					Timer timer = new Timer();
					timer.schedule(new Clean(), (long) (Config.getNotificationTime()));
				}
				
			//If this is the first run
			} else {
				
				//Initialize JDA
				JDABuilder builder = new JDABuilder(AccountType.BOT)
					.setToken(Config.getClientToken())
					.setAudioEnabled(false)
					.setAutoReconnect(true)
					.setReconnectQueue(new SessionReconnectQueue())
					.addEventListener(listener);
				if (!Config.getLogJDA()) {
					SimpleLog.LEVEL = Level.OFF;
				}
				try {
					//Create each shard
					for (int i = 0; i < Config.getShardCount(); i++) {
						System.out.println("Starting shard " + (i + 1) + "/" + Config.getShardCount());
						builder.useSharding(i, Config.getShardCount());
						shards.add(builder.buildBlocking());
						Thread.sleep(5000);
					}
				//Exit inescapable errors
				} catch (Exception ex) {
					ex.printStackTrace();
					System.exit(1);
				}
				
				//Update main class
				birth = System.currentTimeMillis();
				if (Config.getDevMode()) {
					main.getDeclaredMethods()[MethodName.SET_SHARDS.num].invoke(null, shards);
					main.getDeclaredMethods()[MethodName.SET_BIRTH.num].invoke(null, birth);
				}
				MessageUtils.log(":white_check_mark: **Bot started!**");
				
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		//Post-init TODO
		Config.update();
		Registry.enabled = true;
		System.out.println("Startup finished.");
		RequestUtils.sendGuilds();
		
		return true;
		
	}
	
	public Bot(String[] args) {
		//TODO
	}
	
	@SuppressWarnings("static-access")
	public static void shutdown(Message m, User u) {
		
		//Disable JDA
		for (JDA jda : shards) {
			jda.setAutoReconnect(false);
			jda.removeEventListener(listener);
		}
		try {
			//Reload this class using reflection
			Class<?> main = Thread.currentThread().getContextClassLoader().getSystemClassLoader()
				.loadClass(mainClass);
			String[] args = new String[]{"-r"};
			ArrayUtils.addAll(args, Bot.args);
			main.getDeclaredMethods()[MethodName.SET_MESSAGE.num].invoke(null, m);
			main.getDeclaredMethods()[MethodName.SET_USER.num].invoke(null, u);
			main.getDeclaredMethods()[MethodName.LOAD.num].invoke(null, (Object) args);
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
		MAIN(0),
		LOAD(1),
		GET_MESSAGE(2),
		SET_MESSAGE(3),
		GET_USER(4),
		SET_USER(5),
		GET_SHARDS(6),
		SET_SHARDS(7),
		GET_BIRTH(8),
		SET_BIRTH(9);
		
		int num;
		private MethodName(int num) {
			this.num = num;
		}
	}

}
