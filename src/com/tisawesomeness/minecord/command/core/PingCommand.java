package com.tisawesomeness.minecord.command.core;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.SlashCommand;
import com.tisawesomeness.minecord.util.type.HumanDecimalFormat;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.math.RoundingMode;

public class PingCommand extends SlashCommand {

    private static final HumanDecimalFormat FORMAT = HumanDecimalFormat.builder()
            .minimumFractionDigits(0)
            .maximumFractionDigits(3)
            .roundingMode(RoundingMode.UP)
            .build();

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
        String ping = FORMAT.format(Bot.getPing());
        String msg = String.format(":ping_pong: **Pong!** `%s ms`\nUse `/server` to ping a server.", ping);
        return new Result(Outcome.SUCCESS, msg);
    }

}
