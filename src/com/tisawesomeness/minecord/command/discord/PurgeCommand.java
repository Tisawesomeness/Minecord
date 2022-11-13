package com.tisawesomeness.minecord.command.discord;

import com.tisawesomeness.minecord.command.SlashCommand;
import com.tisawesomeness.minecord.database.Database;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

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
        return "`{&}purge <number>` - Cleans 1-1000 messages in the current channel **sent by the bot**.\n" +
                "The user must have the *Manage Messages* permission.\n" +
                "The bot must have the *Manage Messages* and *Read Message History* permissions.\n";
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
        if (!perms) {
            return new Result(Outcome.ERROR, ":x: Give the bot manage message and read message history permissions to use `/purge`");
        }

        MessageChannel channel = e.getGuildChannel();
        User self = e.getJDA().getSelfUser();
        List<Message> messages = new ArrayList<>();
        channel.getIterableHistory()
                .forEachAsync(m -> {
                    if (m.getAuthor().equals(self)) {
                        messages.add(m);
                    }
                    return messages.size() < num;
                })
                .thenRun(() -> channel.purgeMessages(messages))
                .thenRun(() -> sendSuccess(e, MessageCreateData.fromContent("Purging " + messages.size() + " bot messages...")));

        return new Result(Outcome.SUCCESS);
    }

}
