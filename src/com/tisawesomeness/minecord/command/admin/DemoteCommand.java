package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.LegacyCommand;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.util.DiscordUtils;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DemoteCommand extends LegacyCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "demote",
                "De-elevate a user.",
                "<user>",
                5000,
                true,
                true
        );
    }

    public String[] getAliases() {
        return new String[]{"delevate", "normie", "badboi"};
    }

    public Result run(String[] args, MessageReceivedEvent e) throws Exception {

        if (args.length == 0) {
            return new Result(Outcome.WARNING, ":warning: You must specify a user!");
        }

        //Extract user
        User user = DiscordUtils.findUser(args[0]);
        if (user == null) {
            return new Result(Outcome.ERROR, ":x: Not a valid user!");
        }
        long id = user.getIdLong();

        //Don't demote a normal user
        if (!Database.isElevated(id)) {
            return new Result(Outcome.WARNING, ":warning: User is not elevated!");
        }

        //Can't demote the owner
        if (id == Long.parseLong(Config.getOwner())) {
            return new Result(Outcome.WARNING, ":warning: You can't demote the owner!");
        }

        //Demote user
        Database.changeElevated(id, false);
        String msg = "Demoted " + DiscordUtils.tagAndId(user);
        System.out.println(msg);
        Bot.logger.log(":arrow_down: " + msg);
        return new Result(Outcome.SUCCESS, ":arrow_down: " + msg);

    }

}
