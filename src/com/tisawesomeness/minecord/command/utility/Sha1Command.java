package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.MathUtils;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Sha1Command extends Command {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "sha1",
                "Computes the sha1 hash of some text.",
                "<text>",
                new String[]{"sha", "hash"},
                500,
                false,
                false,
                true
        );
    }

    public String getHelp() {
        return "`{&}sha1 <text>` - Computes the sha1 hash of some text.\n" +
                "Useful for comparing a server against Mojang's blocked server list.\n" +
                "\n" +
                "Examples:\n" +
                "- `{&}sha1 any string here`\n" +
                "- `{&}sha1 mc.hypixel.net`\n";
    }

    public Result run(String[] args, MessageReceivedEvent e) {
        if (args.length == 0) {
            return new Result(Outcome.WARNING, ":warning: You must specify some text to hash.");
        }
        return new Result(Outcome.SUCCESS, MathUtils.sha1(String.join(" ", args)));
    }

}
