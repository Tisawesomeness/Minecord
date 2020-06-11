package com.tisawesomeness.minecord.database;

public class DbUser {
	
	public long id;
	public boolean elevated;
	public boolean banned;
	
	public DbUser(long id, boolean elevated, boolean banned) {
		this.id = id;
		this.elevated = elevated;
		this.banned = banned;
	}

}
