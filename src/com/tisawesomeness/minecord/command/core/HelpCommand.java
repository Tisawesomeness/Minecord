package com.tisawesomeness.minecord.command.core;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.Module;
import com.tisawesomeness.minecord.command.*;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Arrays;
import java.util.stream.Collectors;

public class HelpCommand extends SlashCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "help",
                "Displays help for the bot, a command, or a module.",
                "[<command>|<module>|extra]",
                0,
                false,
                false
        );
    }

    @Override
    public SlashCommandData addCommandSyntax(SlashCommandData builder) {
        return builder.addOption(OptionType.STRING, "command", "The command or page to show help for");
    }

    @Override
    public String[] getLegacyAliases() {
        return new String[]{"cmds", "commands", "module", "modules", "categories"};
    }

    @Override
    public String getHelp() {
        return "`{&}help` - Display help for the bot.\n" +
                "`{&}help <module>` - Display help for a module.\n" +
                "`{&}help <command>` - Display help for a command.\n" +
                "`{&}help extra` - Show more help.\n" +
                "\n" +
                "Examples:\n" +
                "- `{&}help server`\n" +
                "- `{&}help profile`\n" +
                "- `{&}help utility`\n" +
                "- `{&}help uuidInput`\n";
    }

    public Result run(SlashCommandInteractionEvent e) {
        EmbedBuilder eb = new EmbedBuilder().setColor(Bot.color);
        String url = e.getJDA().getSelfUser().getEffectiveAvatarUrl();

        String page = e.getOption("command", OptionMapping::getAsString);

        // General help
        if (page == null) {
            // Help menu only contains names of commands, tell user how to get more help
            String help = "Use `/help <command>` to get more information about a command.\n" +
                    "Use `/help <module>` to get help for that module.\n" +
                    "Use `/help extra` for more help.";
            eb.setAuthor("Minecord Help", null, url).setDescription(help);

            // Hidden modules must be viewed directly
            for (Module m : Registry.modules) {
                if (m.isHidden()) {
                    continue;
                }
                // Build that module's list of user-facing commands
                String mHelp = Arrays.stream(m.getCommands())
                        .map(Command::getInfo)
                        .filter(ci -> !ci.hidden)
                        .map(ci -> String.format("`/%s`", ci.name))
                        .collect(Collectors.joining(", "));
                eb.addField(m.getName(), mHelp, false);
            }
            return new Result(Outcome.SUCCESS, MessageUtils.addFooter(eb).build());
        }

        // Extra help
        ExtraHelpPage ehp = ExtraHelpPage.from(page);
        if (ehp != null) {
            eb.setTitle(ehp.getTitle()).setDescription(ehp.getHelp());
            return new Result(Outcome.SUCCESS, MessageUtils.addFooter(eb).build());
        }

        // General extra help
        if (page.equalsIgnoreCase("extra")) {
            String desc = Arrays.stream(ExtraHelpPage.values())
                    .map(HelpCommand::extraHelpLine)
                    .collect(Collectors.joining("\n"));
            eb.setTitle("Extra Help").setDescription(desc);
            return new Result(Outcome.SUCCESS, MessageUtils.addFooter(eb).build());
        }

        // Module help
        Module m = Registry.getModule(page);
        if (m != null) {
            if (m.isHidden() && !Database.isElevated(e.getUser().getIdLong())) {
                return new Result(Outcome.WARNING, ":warning: You do not have permission to view that module.");
            }
            String mUsage = Arrays.stream(m.getCommands())
                    .map(Command::getInfo)
                    .filter(ci -> !ci.hidden || m.isHidden()) // All admin commands are hidden
                    .map(ci -> {
                        // Formatting changes based on whether the command has arguments
                        if (ci.usage == null) {
                            return String.format("`/%s` - %s", ci.name, ci.description);
                        }
                        return String.format("`/%s %s` - %s", ci.name, ci.usage, ci.description);
                    })
                    .collect(Collectors.joining("\n"));
            // Add module-specific help if it exists
            String mHelp = m.getHelp();
            if (mHelp != null) {
                mUsage = mHelp + "\n" + mUsage;
            }
            eb.setAuthor(m.getName() + " Module Help", null, url).setDescription(mUsage);
            return new Result(Outcome.SUCCESS, MessageUtils.addFooter(eb).build());
        }

        // Command help
        Command<?> c = Registry.getCommand(page);
        if (c != null) {
            // Elevation check
            Command.CommandInfo ci = c.getInfo();
            if (ci.elevated && !Database.isElevated(e.getUser().getIdLong())) {
                return new Result(Outcome.WARNING, ":warning: You do not have permission to view that command.");
            }
            // {@} and {&} substitution
            String help = c.getHelp().replace("{@}", e.getJDA().getSelfUser().getAsMention()).replace("{&}", "/");
            // Alias list formatted with prefix in code blocks
            if (c instanceof LegacyCommand) {
                String[] aliases = ((LegacyCommand) c).getAliases();
                if (aliases.length > 0) {
                    String aliasesStr = Arrays.stream(aliases)
                            .map(s -> String.format("`/%s`", s))
                            .collect(Collectors.joining(", "));
                    help += "\nAliases: " + aliasesStr;
                }
            }
            // If the cooldown is exactly N seconds, treat as int
            if (ci.cooldown > 0) {
                if (ci.cooldown % 1000 == 0) {
                    help += String.format("\nCooldown: `%ss`", ci.cooldown / 1000);
                } else {
                    help += String.format("\nCooldown: `%ss`", ci.cooldown / 1000.0);
                }
            }
            String desc = String.format("%s\nModule: `%s`", help, Registry.findModuleName(ci.name));
            eb.setAuthor("/" + ci.name + " Help").setDescription(desc);
            return new Result(Outcome.SUCCESS, MessageUtils.addFooter(eb).build());
        }

        return new Result(Outcome.WARNING, ":warning: That command or module does not exist.");
    }

    private static String extraHelpLine(ExtraHelpPage ehp) {
        return String.format("`/help %s` - %s", ehp.getName(), ehp.getDescription());
    }

}
