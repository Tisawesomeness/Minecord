package com.tisawesomeness.minecord.command.discord;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.LegacyCommand;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RoleCommandAdmin extends LegacyCommand {

    @Override
    public CommandInfo getInfo() {
        return new CommandInfo(
                "roleadmin",
                "Shows role info.",
                "<role id>",
                0,
                true,
                true
        );
    }

    @Override
    public String getHelp() {
        return "`{&}roleadmin <role id>` - Shows the info of any role.\n" +
                "\n" +
                "Examples:\n" +
                "- `{&}role 347797250266628108`\n";
    }

    @Override
    public Result run(String[] args, MessageReceivedEvent e) throws Exception {
        if (args.length == 0) {
            return new Result(Outcome.WARNING, ":warning: You must specify a role id!");
        }
        Role role = Bot.shardManager.getRoleById(args[0]);
        if (role == null) {
            return new Result(Outcome.WARNING, ":warning: That role does not exist.");
        }
        return RoleCommand.run(role, null);
    }

}
