package com.tisawesomeness.minecord.command.core;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class PingCommand extends SlashCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "ping",
                "Pings the bot.",
                null,
                0,
                true,
                false
        );
    }

    public static final String help = "Pings the bot.\nUse `/server` to ping a server.\n";
    @Override
    public String getHelp() {
        return help;
    }

    public Result run(SlashCommandInteractionEvent e) {
        return run();
    }
    public static Result run() {
        String msg = String.format(":ping_pong: **Pong!** `%.3f ms`\nUse `/server` to ping a server.", Bot.getPing());
        return new Result(Outcome.SUCCESS, msg);
    }

}
