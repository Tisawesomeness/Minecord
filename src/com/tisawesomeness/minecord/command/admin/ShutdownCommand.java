package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.LegacyCommand;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ShutdownCommand extends LegacyCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "shutdown",
                "Shuts down the bot.",
                null,
                0,
                true,
                true
        );
    }

    public String getHelp() {
        return "Shuts down the bot. Note that the bot may reboot if it is run by a restart script.\n";
    }

    public Result run(String[] args, MessageReceivedEvent e) {
        MessageUtils.log(":x: **Bot shut down by " + e.getAuthor().getAsTag() + "**");
        e.getChannel().sendMessage(":wave: Goodbye!").complete();
        e.getJDA().shutdown();
        System.exit(0);
        return new Result(Outcome.SUCCESS);
    }

}
