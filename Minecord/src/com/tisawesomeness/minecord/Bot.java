package com.tisawesomeness.minecord;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import javax.security.auth.login.LoginException;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.Registry;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

public class Bot {
	
	protected static JDA jda;
	private static Listener listener; 
	private static final String version = "0.1.0";
	public static long birth;
	public static Thread thread;
	private static final String mainClass = "com.tisawesomeness.minecord.Main";

	public Bot(String[] args) {

		//Pre-init
		thread = Thread.currentThread();
		new Config(new File("./config.json"));
		listener = new Listener();
		Registry.init();
		
		//Fetch main class
		try {
			@SuppressWarnings("static-access")
			Class<?> main = Thread.currentThread().getContextClassLoader().getSystemClassLoader()
				.loadClass(mainClass);
			
			//If this is a reload
			if (args.length > 0 && args[0] == "reload") {
				
				//Get main class info
				Object jo = main.getDeclaredMethods()[MethodName.GET_JDA.num].invoke(null, "ignore");
				jda = (JDA) jo;
				Object mo = main.getDeclaredMethods()[MethodName.GET_MESSAGE.num].invoke(null, "ignore");
				Message m = (Message) mo;
				Object bo = main.getDeclaredMethods()[MethodName.GET_BIRTH.num].invoke(null, "ignore");
				birth = (long) bo;
				//Prepare commands
				jda.addEventListener(listener);
				m.editMessage(":white_check_mark: Bot reloaded!").queue();
				
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
				System.out.println("Booting...");
				JDABuilder builder = new JDABuilder(AccountType.BOT)
					.setToken(Config.getClientToken())
					.setAudioEnabled(false)
					.setAutoReconnect(true)
					.setGame(Game.of(Config.getGame()))
					.addListener(listener);
				try {
					jda = builder.buildBlocking();
				//Exit inescapable errors
				} catch (LoginException | IllegalArgumentException | InterruptedException | RateLimitedException e) {
					e.printStackTrace();
					System.exit(1);
				}
				
				//Update main class
				main.getDeclaredMethods()[MethodName.SET_JDA.num].invoke(null, jda);
				System.out.println("Bot started.");
				main.getDeclaredMethods()[MethodName.SET_BIRTH.num].invoke(null, System.currentTimeMillis());
				birth = System.currentTimeMillis();
				
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		//Post-init
		Config.update();
		Registry.enabled = true;

	}
	
	@SuppressWarnings("static-access")
	public static void shutdown(Message m) {
		
		//Disable JDA
		jda.setAutoReconnect(false);
		jda.removeEventListener(listener);
		try {
			//Reload this class using reflection
			Class<?> main = Thread.currentThread().getContextClassLoader().getSystemClassLoader()
				.loadClass(mainClass);
			String[] args = new String[]{"reload"};
			main.getDeclaredMethods()[MethodName.SET_MESSAGE.num].invoke(null, m);
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
		GET_JDA(4),
		SET_JDA(5),
		GET_BIRTH(6),
		SET_BIRTH(7);
		
		int num;
		private MethodName(int num) {
			this.num = num;
		}
	}

}
