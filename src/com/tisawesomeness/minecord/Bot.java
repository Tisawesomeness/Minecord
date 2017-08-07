package com.tisawesomeness.minecord;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import javax.security.auth.login.LoginException;

import org.apache.commons.lang3.ArrayUtils;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.Registry;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.RequestUtils;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.utils.SimpleLog;
import net.dv8tion.jda.core.utils.SimpleLog.Level;

public class Bot {
	
	public static JDA jda;
	private static Listener listener;
	public static Config config;
	public static long birth;
	public static String[] args;

	private static final String version = "0.4.7";
	
	public static Thread thread;
	private static final String mainClass = "com.tisawesomeness.minecord.Main";

	public Bot(String[] args) {

		//Parse reload
		boolean reload = false;
		if (args.length > 0 && ArrayUtils.contains(args, "-r")) {
			reload = true;
			args = ArrayUtils.remove(args, ArrayUtils.indexOf(args, "-r"));
		}
		Bot.args = args;
		
		//Parse config path
		String path = "./config.json";
		if (args.length > 1 && ArrayUtils.contains(args, "-c")) {
			int index = ArrayUtils.indexOf(args, "-c");
			if (index + 1 < args.length) {
				args = ArrayUtils.remove(args, index);
				path = args[index];
				System.out.println("Found custom client token: " + path);
				args = ArrayUtils.remove(args, index);
			}
		}

		//Pre-init
		thread = Thread.currentThread();
		config = new Config(new File(path));
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
				Object jo = main.getDeclaredMethods()[MethodName.GET_JDA.num].invoke(null, "ignore");
				jda = (JDA) jo;
				Object bo = main.getDeclaredMethods()[MethodName.GET_BIRTH.num].invoke(null, "ignore");
				birth = (long) bo;
				//Prepare commands
				jda.addEventListener(listener);
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
				System.out.println("Starting JDA...");
				JDABuilder builder = new JDABuilder(AccountType.BOT)
					.setToken(Config.getClientToken())
					.setAudioEnabled(false)
					.setAutoReconnect(true)
					.setGame(Game.of(Config.getGame()))
					.addEventListener(listener);
				try {
					if (!Config.getLogJDA()) {
						SimpleLog.LEVEL = Level.OFF;
					}
					jda = builder.buildBlocking();
				//Exit inescapable errors
				} catch (LoginException | IllegalArgumentException | InterruptedException | RateLimitedException ex) {
					ex.printStackTrace();
					System.exit(1);
				}
				
				//Update main class
				birth = System.currentTimeMillis();
				if (Config.getDevMode()) {
					main.getDeclaredMethods()[MethodName.SET_JDA.num].invoke(null, jda);
					main.getDeclaredMethods()[MethodName.SET_BIRTH.num].invoke(null, birth);
				}
				MessageUtils.log(":white_check_mark: **Bot started!**");
				
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		//Post-init
		Config.update();
		Registry.enabled = true;
		System.out.println("Bot started.");
		RequestUtils.sendGuilds();

	}
	
	@SuppressWarnings("static-access")
	public static void shutdown(Message m, User u) {
		
		//Disable JDA
		jda.setAutoReconnect(false);
		jda.removeEventListener(listener);
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
		return;
		
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
		GET_JDA(6),
		SET_JDA(7),
		GET_BIRTH(8),
		SET_BIRTH(9);
		
		int num;
		private MethodName(int num) {
			this.num = num;
		}
	}

}
