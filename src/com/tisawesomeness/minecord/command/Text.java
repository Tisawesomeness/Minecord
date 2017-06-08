package com.tisawesomeness.minecord.command;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Text extends Command {

	boolean elevated = false;
	String text;
	
	/**
	 * A class used represent extra info in the help command.
	 * @param text The string to display in the help menu.
	 */
	public Text(String text) {
		this.text = text;
	}
	
	/**
	 * A class used represent extra info in the help command.
	 * @param elevated If the text requires elevated permissions to view.
	 * @param text The string to display in the help menu.
	 */
	public Text(boolean elevated, String text) {
		this.elevated = elevated;
		this.text = text;
	}
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"",
			text,
			null,
			null,
			0,
			false,
			elevated,
			false
		);
	}

	//When someone types in just "&", meaning a blank name, it should be stopped by the listener.
	//If this command executes, it will fail silently and log an empty warning.
	public Result run(String[] args, MessageReceivedEvent e) throws Exception {
		return new Result(Outcome.WARNING);
	}

}
