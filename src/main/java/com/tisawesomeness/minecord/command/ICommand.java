package com.tisawesomeness.minecord.command;

public interface ICommand {

    /**
     * This method is called when the command is run.
     * @param ctx The message-specific context.
     * @return The Result of the command.
     */
    Result run(CommandContext ctx) throws Exception;

}
