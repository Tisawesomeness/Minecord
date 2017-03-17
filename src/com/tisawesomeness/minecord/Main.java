package com.tisawesomeness.minecord;

import java.net.URL;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;

public class Main {
	
	static URL url;
	static final boolean propagate = true;
	static ClassLoader cl;
	static Bot bot;
	
	//Start loader
	public static void main(String[] args) throws Exception {
		cl = Thread.currentThread().getContextClassLoader();
		load(args);
	}
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
