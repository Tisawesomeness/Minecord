package com.tisawesomeness.minecord.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.stream.Collectors;

public abstract class SlashCommand implements Command<SlashCommandInteractionEvent> {

    public final SlashCommandData getCommandSyntax() {
        CommandInfo info = getInfo();
        return addCommandSyntax(Commands.slash(info.name, info.description));
    }
    public SlashCommandData addCommandSyntax(SlashCommandData builder) {
        return builder;
    }

    /**
     * @return list of aliases that existed before this command moved to slash commands
     */
    public String[] getLegacyAliases() {
        return new String[0];
    }

    @Override
    public final void sendSuccess(SlashCommandInteractionEvent e, MessageCreateData message) {
        if (e.isAcknowledged()) {
            e.getHook().sendMessage(message).queue();
        } else {
            e.reply(message).queue();
        }
    }
    @Override
    public final void sendFailure(SlashCommandInteractionEvent e, MessageCreateData message) {
        if (e.isAcknowledged()) {
            e.getHook().sendMessage(message).setEphemeral(true).queue();
        } else {
            e.reply(message).setEphemeral(true).queue();
        }
    }

    @Override
    public String debugRunCommand(SlashCommandInteractionEvent e) {
        return debugRunSlashCommand(e);
    }
    public static String debugRunSlashCommand(SlashCommandInteractionEvent e) {
        return e.getName() + ": " + e.getOptions().stream()
                .map(o -> o.getName() + "=" + o.getAsString())
                .collect(Collectors.joining("\n"));
    }

    public abstract Result run(SlashCommandInteractionEvent e) throws Exception;

}
