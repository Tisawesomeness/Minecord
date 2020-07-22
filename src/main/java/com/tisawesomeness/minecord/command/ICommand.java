package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.command.Command.CommandInfo;
import com.tisawesomeness.minecord.command.Command.Result;

public interface ICommand {
	
	/**
	 * @return The command info.
	 */
	CommandInfo getInfo();

	/**
	 * Defines the help text shown by {@code &help <command>}.
	 * Use {@code {&}} to substitute the current prefix, or {@code {\@}} to substitute the bot mention.
	 * @return Never-null help string
	 */
	default String getHelp() {
		return getInfo().description + "\n";
	}

	default String getAdminHelp() {
		return getHelp();
	}
	
	/**
	 * This method is called when the command is run.
	 * @param ctx The message-specific context.
	 * @return The Result of the command.
	 */
	Result run(CommandContext ctx) throws Exception;

}
