package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.Bot;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.ICommandReference;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class SlashCommand implements Command<SlashCommandInteractionEvent> {

    public final SlashCommandData getCommandSyntax() {
        CommandInfo info = getInfo();
        SlashCommandData builder = Commands.slash(info.name, info.description)
                .setContexts(InteractionContextType.ALL)
                .setIntegrationTypes(IntegrationType.ALL);
        return addCommandSyntax(builder);
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
    public boolean supportsContext(Set<IntegrationType> install, InteractionContextType context) {
        Optional<net.dv8tion.jda.api.interactions.commands.Command> commandOpt = getDiscordCommand();
        if (!commandOpt.isPresent()) {
            return LegacyCommand.CONTEXTS.contains(context);
        }
        net.dv8tion.jda.api.interactions.commands.Command command = commandOpt.get();
        return install.stream().anyMatch(i -> command.getIntegrationTypes().contains(i))
                && command.getContexts().contains(context);
    }

    @Override
    public final String getMention() {
        return getDiscordCommand()
                .map(ICommandReference::getAsMention)
                .orElse(MarkdownUtil.monospace("/" + getInfo().name));
    }

    private Optional<net.dv8tion.jda.api.interactions.commands.Command> getDiscordCommand() {
        return Bot.getSlashCommands().stream()
                .filter(c -> c.getName().equals(getInfo().name))
                .findFirst();
    }

    /**
     * Like {@link SlashCommandInteractionEvent#getOption(String, Function) getOption(name, resolver)}, except that
     * trying to resolve an option that is different from the expected type returns null instead of throwing an exception.
     * @param e The slash command event that was just executed
     * @param name The option name
     * @param type The expected option type
     * @return The resolved value of the option, or null if the option doesn't exist or is of a different type than expected
     * @param <T> The expected type of the resolved option value
     */
    protected final <T> @Nullable T getOption(SlashCommandInteractionEvent e, String name, OptionTypes.Type<T> type) {
        return getOption(e, name, null, type);
    }
    /**
     * Like {@link SlashCommandInteractionEvent#getOption(String, T, Function) getOption(name, resolver)}, except that
     * resolving null returns the fallback, and trying to resolve an option that is different from the expected type
     * returns the fallback instead of throwing an exception.
     * @param e The slash command event that was just executed
     * @param name The option name
     * @param fallback The fallback value
     * @param type The expected option type
     * @return The resolved value of the option, or the fallback if the resolved value is null, the option doesn't exist,
     * or is of a different type than expected
     * @param <T> The expected type of the resolved option value
     */
    protected static <T> T getOption(SlashCommandInteractionEvent e, String name, T fallback, OptionTypes.Type<T> type) {
        OptionMapping mapping = e.getOption(name);
        if (mapping != null) {
            T result = type.resolve(mapping);
            if (result != null) {
                return result;
            }
        }
        return fallback;
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
