package com.tisawesomeness.minecord;

import java.util.ArrayList;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

public class Main {
	
	protected static ClassLoader cl;

	//Store data between reloads
	private static ArrayList<JDA> shards;
	private static User user;
	private static Message message;
	private static long birth;
	
	public static void main(String[] args) throws Exception {
		
		if (!Bot.setup(args, false)) {
			cl = Thread.currentThread().getContextClassLoader();
			load(args);
		}
		
	}
	
	//Start loader
	public static void load(String[] args) throws Exception {
		new Thread(new Loader(args)).start();
	}
	
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
	public static ArrayList<JDA> getShards(String ignore) {
		return shards;
	}
	public static void setShards(ArrayList<JDA> s) {
		shards = s;
	}
	public static long getBirth(String ignore) {
		return birth;
	}
	public static void setBirth(long b) {
		birth = b;
	}

}
