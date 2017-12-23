package com.tisawesomeness.minecord.database;

public class DbGuild {
	
	public long id;
	public String prefix;
	public boolean banned;
	public boolean noCooldown;
	
	public DbGuild(long id, String prefix, boolean banned, boolean noCooldown) {
		this.id = id;
		this.prefix = prefix;
		this.banned = banned;
		this.noCooldown = noCooldown;
	}

}
