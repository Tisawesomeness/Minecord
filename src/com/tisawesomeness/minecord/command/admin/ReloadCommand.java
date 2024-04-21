package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.Announcement;
import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.LegacyCommand;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.database.VoteHandler;
import com.tisawesomeness.minecord.mc.item.ItemRegistry;
import com.tisawesomeness.minecord.mc.item.RecipeRegistry;
import com.tisawesomeness.minecord.util.DiscordUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.io.IOException;
import java.sql.SQLException;

public class ReloadCommand extends LegacyCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "reload",
                "Reloads the bot.",
                null,
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

        String msg = "Bot reloaded by " + DiscordUtils.tagAndId(e.getAuthor());
        System.out.println(msg);
        Bot.logger.log(":arrows_counterclockwise: " + MarkdownUtil.bold(msg));
        Message m = e.getChannel().sendMessage(":arrows_counterclockwise: Reloading...").complete();
        if (Config.getDevMode()) {
            Bot.shutdown(m, e.getAuthor());
        } else {
            Bot.setNotReady();
            try {
                Database.close();
                Database.init();
                if (Config.getReceiveVotes()) {
                    VoteHandler.close();
                }
                Config.read(true);
                if (Config.getReceiveVotes()) {
                    VoteHandler.init();
                }
                Announcement.init(Config.getPath());
                ItemRegistry.init(Config.getPath());
                RecipeRegistry.init(Config.getPath());
                Bot.reloadMCLibrary();
            } catch (SQLException | IOException ex) {
                ex.printStackTrace();
            }
            Bot.setReady();
            m.editMessage(":white_check_mark: Reloaded!").queue();
        }

        return new Result(Outcome.SUCCESS);
    }

}
