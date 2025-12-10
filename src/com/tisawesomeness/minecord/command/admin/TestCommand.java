package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.LegacyCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class TestCommand extends LegacyCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "test",
                "Test command.",
                null,
                5000,
                false,
                false
        );
    }

    public Result run(String[] args, MessageReceivedEvent e) {
        return new Result(Outcome.SUCCESS, "Test");
    }

}
