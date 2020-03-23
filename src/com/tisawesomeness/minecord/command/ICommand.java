package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.command.Command.CommandInfo;
import com.tisawesomeness.minecord.command.Command.Result;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface ICommand {
	
	/**
	 * @return The command info.
	 */
	public CommandInfo getInfo();
	
	/**
	 * This method is executed when the command is called.
	 * @param args The list of arguments passed in by the user, separated by the spaces.
	 * @param e The message event.
	 * @return The Result of the command.
	 */
	public Result run(String[] args, MessageReceivedEvent e) throws Exception;

}
