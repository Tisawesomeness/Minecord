package com.tisawesomeness.minecord.command.misc;

import com.tisawesomeness.minecord.lang.Lang;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.CommandRegistry;
import com.tisawesomeness.minecord.command.IElevatedCommand;
import com.tisawesomeness.minecord.command.IHiddenCommand;
import com.tisawesomeness.minecord.command.Module;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class HelpCommand extends AbstractMiscCommand {

    private @NonNull CommandRegistry registry;

    public @NonNull String getId() {
        return "help";
    }

    public void run(String[] args, CommandContext ctx) {
        String prefix = ctx.getPrefix();
        Lang lang = ctx.getLang();

        EmbedBuilder eb = new EmbedBuilder();
        String url = ctx.getE().getJDA().getSelfUser().getEffectiveAvatarUrl();

        // General help
        if (args.length == 0) {
            ctx.triggerCooldown();
            // Help menu only contains names of commands, tell user how to get more help
            String help = String.format(
                "Use `%shelp <command>` to get more information about a command.\n" +
                "Use `%shelp <module>` to get help for that module.",
                prefix, prefix);
            eb.setAuthor("Minecord Help", null, url).setDescription(help);

            // Hidden modules must be viewed directly
            for (Module m : Module.values()) {
                if (m.isHidden()) {
                    continue;
                }
                // Build that module's list of user-facing commands
                Collection<Command> cmds = registry.getCommandsInModule(m);
                if (cmds.isEmpty()) {
                    continue;
                }
                String mHelp = cmds.stream()
                    .filter(c -> !(c instanceof IHiddenCommand))
                    .map(c -> formatCommand(ctx, prefix, lang, c))
                    .collect(Collectors.joining(", "));
                eb.addField(m.getDisplayName(lang), mHelp, false);
            }
            ctx.reply(eb);
            return;
        }

        // Module help
        Optional<Module> moduleOpt = Module.from(args[0], lang);
        if (moduleOpt.isPresent()) {
            ctx.triggerCooldown();
            Module m = moduleOpt.get();
            if (m.isHidden() && !ctx.isElevated()) {
                ctx.warn("You do not have permission to view that module.");
                return;
            }
            String mUsage = registry.getCommandsInModule(m).stream()
                .filter(c -> !(c instanceof IHiddenCommand) || m.isHidden()) // All admin commands are hidden
                .map(c -> formatCommandFull(ctx, prefix, lang, c))
                .collect(Collectors.joining("\n"));
            // Add module-specific help if it exists
            Optional<String> mHelp = m.getHelp(lang, prefix);
            if (mHelp.isPresent()) {
                mUsage = mHelp.get() + "\n\n" + mUsage;
            }
            eb.setAuthor(m.getDisplayName(lang) + " Module Help", null, url).setDescription(mUsage);
            ctx.reply(eb);
            return;
        }

        // Command help
        Optional<Command> cmdOpt = registry.getCommand(args[0], lang);
        if (cmdOpt.isPresent()) {
            ctx.triggerCooldown();
            Command c = cmdOpt.get();
            // Elevation check
            if (c instanceof IElevatedCommand && !ctx.isElevated()) {
                ctx.warn("You do not have permission to view that command.");
                return;
            }
            // Admin check
            if (args.length > 1 && "admin".equals(args[1])) {
                ctx.reply(showHelp(ctx, c, true));
                return;
            }
            ctx.reply(showHelp(ctx, c));
            return;
        }

        ctx.invalidArgs("That command or module does not exist.");
    }

    private static String formatCommand(CommandContext ctx, String prefix, Lang lang, Command c) {
        if (c.isEnabled(ctx.getConfig().getCommandConfig())) {
            return String.format("`%s%s`", prefix, c.getDisplayName(lang));
        }
        return String.format("~~`%s%s`~~", prefix, c.getDisplayName(lang));
    }

    private static String formatCommandFull(CommandContext ctx, String prefix, Lang lang, Command c) {
        // Formatting changes based on whether the command has arguments
        Optional<String> usageOpt = c.getUsage(lang);
        if (usageOpt.isEmpty()) {
            if (c.isEnabled(ctx.getConfig().getCommandConfig())) {
                return String.format("`%s%s` - %s", prefix, c.getDisplayName(lang), c.getDescription(lang));
            }
            return String.format("~~`%s%s`~~ - %s", prefix, c.getDisplayName(lang), c.getDescription(lang));
        }
        if (c.isEnabled(ctx.getConfig().getCommandConfig())) {
            return String.format("`%s%s %s` - %s", prefix, c.getDisplayName(lang), usageOpt.get(), c.getDescription(lang));
        }
        return String.format("~~`%s%s %s`~~ - %s", prefix, c.getDisplayName(lang), usageOpt.get(), c.getDescription(lang));
    }

    /**
     * Creates the help menu for a command.
     * @param ctx The incoming context, used to get prefix and language
     * @param c The command
     * @return An unbranded embed builder with the title and description set
     */
    public static EmbedBuilder showHelp(CommandContext ctx, Command c) {
        return showHelp(ctx, c, false);
    }
    private static EmbedBuilder showHelp(CommandContext ctx, Command c, boolean isAdmin) {
        Lang lang = ctx.getLang();
        String prefix = ctx.getPrefix();
        String tag = ctx.getE().getJDA().getSelfUser().getAsMention();

        String help = isAdmin ? c.getAdminHelp(lang, prefix, tag) : c.getHelp(lang, prefix, tag);
        Optional<String> examplesOpt = isAdmin ? c.getAdminExamples(lang, prefix, tag) : c.getExamples(lang, prefix, tag);
        if (examplesOpt.isPresent()) {
            help += "\n\n**Examples**:\n" + examplesOpt.get();
        }
        return showHelpInternal(ctx, c, help);
    }

    private static EmbedBuilder showHelpInternal(CommandContext ctx, Command c, String help) {
        String prefix = ctx.getPrefix();
        Lang lang = ctx.getLang();
        help += "\n";

        help += getAddedPermsHelp(c.getUserPermissions(), "**Required User Perms**")
                + getAddedPermsHelp(c.getBotPermissions(), "**Required Bot Perms**");

        // Alias list formatted with prefix in code blocks
        if (!c.getAliases(lang).isEmpty()) {
            String aliases = c.getAliases(lang).stream()
                    .map(s -> String.format("`%s%s`", prefix, s))
                    .collect(Collectors.joining(", "));
            help += "\n**Aliases**: " + aliases;
        }
        // If the cooldown is exactly N seconds, treat as int
        int cooldown = c.getCooldown(ctx.getConfig().getCommandConfig());
        if (cooldown > 0) {
            help += getCooldownString(cooldown);
        }

        String name;
        if (c.isEnabled(ctx.getConfig().getCommandConfig())) {
            name = prefix + c.getDisplayName(lang);
        } else {
            name = String.format("~~%s%s~~", prefix, c.getDisplayName(lang));
        }
        String desc = String.format("%s\n**Module**: `%s`", help, c.getModule().getDisplayName(lang));
        return new EmbedBuilder()
                .setTitle(name + " Help")
                .setDescription(desc);
    }
    private static String getAddedPermsHelp(Collection<Permission> permissions, String permissionDescriptor) {
        if (permissions.isEmpty()) {
            return "";
        }
        String permissionsString = permissions.stream()
                .map(Permission::getName)
                .collect(Collectors.joining(", "));
        return String.format("\n%s: %s", permissionDescriptor, permissionsString);
    }
    private static String getCooldownString(int cooldown) {
        if (cooldown % 1000 == 0) {
            return String.format("\n**Cooldown**: `%ss`", cooldown / 1000);
        }
        return String.format("\n**Cooldown**: `%ss`", cooldown / 1000.0);
    }

}
