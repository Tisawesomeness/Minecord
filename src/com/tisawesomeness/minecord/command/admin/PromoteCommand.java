package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.LegacyCommand;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.util.DiscordUtils;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PromoteCommand extends LegacyCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "promote",
                "Elevate a user.",
                "<user>",
                5000,
                true,
                true
        );
    }

    public String[] getAliases() {
        return new String[]{"elevate", "rankup"};
    }

    public Result run(String[] args, MessageReceivedEvent e) throws Exception {

        if (args.length == 0) {
            return new Result(Outcome.WARNING, ":warning: You must specify a user!");
        }

        //Extract user
        User user = DiscordUtils.findUser(args[0]);
        if (user == null) return new Result(Outcome.ERROR, ":x: Not a valid user!");

        //Don't elevate a normal user
        if (Database.isElevated(user.getIdLong())) {
            return new Result(Outcome.WARNING, ":warning: User is already elevated!");
        }

        //Elevate user
        Database.changeElevated(user.getIdLong(), true);
        String msg = "Elevated " + DiscordUtils.tagAndId(user);
        System.out.println(msg);
        Bot.logger.log(":arrow_up: " + msg);
        return new Result(Outcome.SUCCESS, ":arrow_up: " + msg);

    }

}
