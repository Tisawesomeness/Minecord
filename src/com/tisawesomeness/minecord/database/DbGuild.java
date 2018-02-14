package com.tisawesomeness.minecord.database;

public class DbGuild {
	
	public long id;
	public String prefix;
	public String lang;
	public boolean banned;
	public boolean noCooldown;
	
	public DbGuild(long id, String prefix, String lang, boolean banned, boolean noCooldown) {
		this.id = id;
		this.prefix = prefix;
		this.lang = lang;
		this.banned = banned;
		this.noCooldown = noCooldown;
	}

}
