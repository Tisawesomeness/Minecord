package com.tisawesomeness.minecord.command.core;

import com.tisawesomeness.minecord.command.LegacyCommand;
import com.tisawesomeness.minecord.database.Database;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

public class PrefixCommand extends LegacyCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "prefix",
                "Change the prefix.",
                "[<prefix>]",
                1000,
                true,
                false
        );
    }

    @Override
    public String[] getAliases() {
        return new String[]{"resetprefix", "changeprefix"};
    }

    @Override
    public String getHelp() {
        return "`{&}prefix` - Show the current prefix.\n" +
                "`{&}prefix <prefix>` - Change the prefix. The user must have **Manage Server** permissions.\n" +
                "The prefix can be any text between 1-16 characters.\n" +
                "\n" +
                "Examples:\n" +
                "- `{&}prefix mc!`\n" +
                "- {@}` prefix &`\n";
    }

    public Result run(String[] args, MessageReceivedEvent e) throws Exception {

        if (!e.isFromGuild()) {
            String username = e.getJDA().getSelfUser().getName();
            return new Result(Outcome.SUCCESS, "The current prefix is " + MarkdownUtil.monospace(username));
        }

        if (args.length == 0) {

            //Print current prefix
            return new Result(Outcome.SUCCESS,
                    "The current prefix is `" + Database.getPrefix(e.getGuild().getIdLong()) + "`"
            );

        } else {

            //Check if user is elevated or has the manage messages permission
            if (!Database.isElevated(e.getAuthor().getIdLong())
                    && !e.getMember().hasPermission(e.getGuildChannel(), Permission.MANAGE_SERVER)) {
                return new Result(Outcome.WARNING, ":warning: You must have manage server permissions!");
            }

            //No prefixes longer than 16 characters
            if (args[0].length() > 16) {
                return new Result(Outcome.WARNING, ":warning: The prefix you specified is too long!");
            }
            //Easter egg for those naughty bois
            if (args.length == 3 && args[0].equals("'") && args[1].equals("OR") && args[2].equals("1=1")) {
                return new Result(Outcome.SUCCESS, "Nice try.");
            }
            //Set new prefix
            Database.changePrefix(e.getGuild().getIdLong(), args[0]);
            return new Result(Outcome.SUCCESS, ":white_check_mark: Prefix changed to `" + args[0] + "`");

        }
    }

}
