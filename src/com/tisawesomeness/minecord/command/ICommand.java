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
	 * Defines the help text shown by &help <command>.
	 * Use {&} to substitute the current prefix, or {@} to substitute the bot mention.
	 * @return Never-null help string
	 */
	public default String getHelp() {
		return getInfo().description + "\n";
	}
	
	/**
	 * This method is called when the command is run.
	 * @param args The list of arguments passed in by the user, separated by spaces.
	 * @param e The message event.
	 * @return The Result of the command.
	 */
	public Result run(String[] args, MessageReceivedEvent e) throws Exception;

}
