package com.tisawesomeness.minecord.command.discord;

import com.tisawesomeness.minecord.command.SlashCommand;
import com.tisawesomeness.minecord.database.Database;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.ArrayList;
import java.util.List;

public class PurgeCommand extends SlashCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "purge",
                "Cleans the bot messages.",
                "<number>",
                5000,
                false,
                false
        );
    }

    @Override
    public SlashCommandData addCommandSyntax(SlashCommandData builder) {
        return builder.addOptions(new OptionData(OptionType.INTEGER, "number", "The number of messages to delete", true)
                .setRequiredRange(1, 1000));
    }

    @Override
    public String[] getLegacyAliases() {
        return new String[]{"clear", "clean", "delete", "delet", "prune", "destroy"};
    }

    @Override
    public String getHelp() {
        return "`{&}purge <number>` - Cleans messages in the current channel **sent by the bot**.\n" +
                "The user must have *Manage Messages* permissions.\n" +
                "The number of messages must be between 1-1000.\n" +
                "If deleting more than 50 messages, the bot must have *Manage Messages* permissions.\n";
    }

    public Result run(SlashCommandInteractionEvent e) {
        if (!e.isFromGuild()) {
            return new Result(Outcome.WARNING, ":warning: This command is not available in DMs.");
        }

        //Check if user is elevated or has the manage messages permission
        if (!Database.isElevated(e.getUser().getIdLong())
                && !e.getMember().hasPermission(e.getGuildChannel(), Permission.MESSAGE_MANAGE)) {
            return new Result(Outcome.WARNING, ":warning: You must have permission to manage messages in this channel!");
        }

        //Parse args
        int num = e.getOption("number").getAsInt();

        //Check for bot permissions
        boolean perms = e.getGuild().getSelfMember().hasPermission(e.getGuildChannel(), Permission.MESSAGE_MANAGE);
        if (!perms && (num <= 0 || num > 50)) {
            return new Result(Outcome.ERROR,
                    ":x: The number must be between 1-50, I don't have permission to manage messages!");
        }

        //Repeat until either the amount of messages is found or 100 non-bot messages in a row
        MessageHistory mh = new MessageHistory(e.getGuildChannel());
        int empty = 0;
        ArrayList<Message> mine = new ArrayList<>();
        while (mine.size() < num) {
            try {

                //Fetch messages in batches of 25
                ArrayList<Message> temp = new ArrayList<>();
                List<Message> msgs = mh.retrievePast(25).complete(true);
                boolean exit = false;
                for (Message m : msgs) {
                    if (m.getAuthor().getId().equals(e.getJDA().getSelfUser().getId())) {
                        temp.add(m);
                    }
                    if (mine.size() + temp.size() >= num) {
                        exit = true;
                        break;
                    }
                }

                mine.addAll(temp);
                if (exit) {break;}

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
                    return new Result(Outcome.ERROR, "Could not find any bot messages within the last 100 messages.");
                }
                break;
            }
        }

        //Delete messages
        GuildMessageChannel c = e.getGuildChannel();
        if (mine.size() == 1) {
            mine.get(0).delete().queue();
            c.sendMessage("1 message purged.").queue();
        } else if (!perms) {
            for (Message m : mine) {
                m.delete().queue();
            }
            c.sendMessage(mine.size() + " messages purged.").queue();
        } else {
            c.deleteMessages(mine).queue();
            c.sendMessage(mine.size() + " messages purged.").queue();
        }

        return new Result(Outcome.SUCCESS);
    }

}
