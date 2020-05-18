package com.tisawesomeness.minecord.database;

public class DbGuild {
	
	public long id;
	public String prefix;
	public String lang;
	public boolean banned;
	public boolean noCooldown;
	public boolean deleteCommands;
	public boolean noMenu;
	
	public DbGuild(long id, String prefix, String lang, boolean banned, boolean noCooldown, boolean deleteCommands, boolean noMenu) {
		this.id = id;
		this.prefix = prefix;
		this.lang = lang;
		this.banned = banned;
		this.noCooldown = noCooldown;
		this.deleteCommands = deleteCommands;
		this.noMenu = noMenu;
	}

}
