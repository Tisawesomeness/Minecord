package com.tisawesomeness.minecord;

import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.util.Locale;
import java.util.ResourceBundle;

public class Main {
	
	private static ClassLoader cl;

	// Store data between reloads
	private static ShardManager shards;
	private static User user;
	private static Message message;
	private static long birth;
	
	public static void main(String[] args) {
		
		if (!new Bot().setup(args, false)) {
			cl = Thread.currentThread().getContextClassLoader();
			load(args);
		}
		
	}

    /**
     * Starts the dynamic loader in a new thread for use in hot code reloading
     * @param args The program's args
     */
	public static void load(String[] args) {
		new Thread(new Loader(args, cl)).start();
	}
	
	// Getters and setters
	public static Message getMessage(String ignore) {
		return message;
	}
	public static void setMessage(Message m) {
		message = m;
	}
	public static User getUser(String ignore) {
		return user;
	}
	public static void setUser(User u) {
		user = u;
	}
	public static ShardManager getShards(String ignore) {
		return shards;
	}
	public static void setShards(ShardManager s) {
		shards = s;
	}
	public static long getBirth(String ignore) {
		return birth;
	}
	public static void setBirth(long b) {
		birth = b;
	}

	// i18n handlers
    public static ResourceBundle getDefaultLang(String ignore) {
	    return ResourceBundle.getBundle("lang");
    }
    public static ResourceBundle getLang(Locale l) {
        return ResourceBundle.getBundle("lang", l);
    }

}