package com.tisawesomeness.minecord;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONObject;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

public class Main {
	
	static URL url;
	static final boolean propagate = true;
	static ClassLoader cl;
	static Bot bot;
	
	public static void main(String[] args) throws Exception {
		
		//Parse config arg
		String path = "./config.json";
		if (args.length > 1 && ArrayUtils.contains(args, "-c")) {
			int index = ArrayUtils.indexOf(args, "-c");
			if (index + 1 < args.length) {
				path = args[index + 1];
			}
		}
		
		//Check for dev mode
		JSONObject config = new JSONObject(FileUtils.readFileToString(new File(path), "UTF-8"));
		if (config.optBoolean("devMode", false)) {
			cl = Thread.currentThread().getContextClassLoader();
			load(args);
		} else {
			new Bot(args);
		}
	}
	
	//Start loader
	public static void load(String[] args) throws Exception {
		new Thread(new Loader(args)).start();
	}

	//Store data between reloads
	private static JDA jda;
	private static User user;
	private static Message message;
	private static long birth;
	
	//Getters and setters
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
	public static JDA getJDA(String ignore) {
		return jda;
	}
	public static void setJDA(JDA j) {
		jda = j;
	}
	public static long getBirth(String ignore) {
		return birth;
	}
	public static void setBirth(long b) {
		birth = b;
	}

}
