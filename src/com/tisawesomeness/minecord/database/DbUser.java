package com.tisawesomeness.minecord.database;

public class DbUser {
	
	public long id;
	public boolean elevated;
	public boolean banned;
	public boolean upvoted;
	
	public DbUser(long id, boolean elevated, boolean banned, boolean upvoted) {
		this.id = id;
		this.elevated = elevated;
		this.banned = banned;
		this.upvoted = upvoted;
	}

}
