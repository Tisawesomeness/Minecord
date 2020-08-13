package com.tisawesomeness.minecord.command.discord;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.IGuildOnlyCommand;
import com.tisawesomeness.minecord.command.Result;

import lombok.NonNull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.RateLimitedException;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class PurgeCommand extends AbstractDiscordCommand implements IGuildOnlyCommand {

    private static final String ERR_W_MANAGE_MESSAGE = "The number must be between 1-1000.";
    private static final String ERR_WO_MANAGE_MESSAGE =
            "The number must be between 1-50, I don't have permission to manage messages!";

    public @NonNull String getId() {
        return "purge";
    }

    @Override
    public EnumSet<Permission> getUserPermissions() {
        return EnumSet.of(Permission.MESSAGE_MANAGE);
    }
    @Override
    public EnumSet<Permission> getBotPermissions() {
        return EnumSet.of(Permission.MESSAGE_HISTORY);
    }

    public Result run(String[] args, CommandContext ctx) {
        MessageReceivedEvent e = ctx.e;

        // Guild-only command
        if (!e.isFromGuild()) {
            return ctx.warn("This command is not available in DMs.");
        }

        //Parse args
        int num;
        boolean perms;
        if (args.length > 0 && args[0].matches("^[0-9]+$")) {
            num = Integer.valueOf(args[0]);

            //Check for bot permissions
            perms = ctx.botHasPermission(Permission.MESSAGE_MANAGE);
            String errMsg = perms ? ERR_W_MANAGE_MESSAGE : ERR_WO_MANAGE_MESSAGE;
            if (!perms && 50 < num && num <= 1000) {
                return ctx.noBotPermissions(errMsg);
            } else if (num <= 0 || 1000 < num) {
                return ctx.invalidArgs(errMsg);
            }

        } else {
            return ctx.showHelp();
        }

        //Repeat until either the amount of messages are found or 100 non-bot messages in a row
        ctx.triggerCooldown();
        MessageHistory mh = new MessageHistory(e.getTextChannel());
        int empty = 0;
        ArrayList<Message> mine = new ArrayList<>();
        while (mine.size() < num) {
            try {

                //Fetch messages in batches of 25
                ArrayList<Message> temp = new ArrayList<>();
                List<Message> msgs = mh.retrievePast(25).complete(true);
                boolean exit = false;
                long botID = e.getJDA().getSelfUser().getIdLong();
                for (Message m : msgs) {
                    if (m.getAuthor().getIdLong() == botID) {
                        temp.add(m);
                    }
                    if (mine.size() + temp.size() >= num) {
                        exit = true;
                        break;
                    }
                }

                mine.addAll(temp);
                if (exit) {
                    break;
                }

                //If no messages were found, log it
                if (temp.size() > 0) {
                    empty = 0;
                } else {
                    empty++;
                }

            } catch (RateLimitedException ex) {
                ex.printStackTrace();
            }
            if (empty >= 4) {
                if (mine.size() == 0) {
                    return ctx.err("Could not find any bot messages within the last 100 messages.");
                }
                break;
            }
        }

        //Delete messages
        if (mine.size() == 1) {
            mine.get(0).delete().queue();
            return ctx.reply("1 message purged.");
        } else if (!perms) {
            for (Message m : mine) {
                m.delete().queue();
            }
        } else {
            e.getTextChannel().deleteMessages(mine).queue();
        }
        return ctx.reply(mine.size() + " messages purged.");
    }

}
