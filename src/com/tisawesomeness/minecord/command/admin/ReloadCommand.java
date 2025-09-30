package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.LegacyCommand;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ReloadCommand extends LegacyCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "reload",
                "Reloads the bot.",
                "[<reason>]",
                0,
                true,
                true
        );
    }

    public String[] getAliases() {
        return new String[]{"restart", "reboot", "refresh"};
    }

    public String getHelp() {
        if (Config.getDevMode()) {
            return "Reloads all non-reflection code, keeping the JDA instance.\n";
        }
        return "Reloads the config, announcement, and item/recipe files, and restarts the database and vote server.";
    }

    public Result run(String[] args, MessageReceivedEvent e) {

        String reason;
        if (args.length > 0) {
            reason = String.join(" ", args);
        } else {
            reason = null;
        }

        Message m = e.getChannel().sendMessage(":arrows_counterclockwise: Reloading...").complete();
        if (Config.getDevMode()) {
            Bot.shutdown(m, e.getAuthor());
        } else {
            if (Bot.reload(e.getAuthor(), reason)) {
                m.editMessage(":white_check_mark: Reloaded!").queue();
            } else {
                m.editMessage(":x: An Error occurred while reloading, check logs").queue();
            }
        }

        return new Result(Outcome.SUCCESS);
    }

}
