package com.tisawesomeness.minecord;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;

public class Main {
	
	static URL url;
	static final boolean propagate = true;
	static ClassLoader cl;
	static Bot bot;
	
	//Check for dev mode
	public static void main(String[] args) throws Exception {
		JSONObject config = new JSONObject(FileUtils.readFileToString(new File("./config.json"), "UTF-8"));
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
	private static Message message;
	private static long birth;
	
	//Getters and setters
	public static Message getMessage(String ignore) {
		return message;
	}
	public static void setMessage(Message m) {
		message = m;
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
