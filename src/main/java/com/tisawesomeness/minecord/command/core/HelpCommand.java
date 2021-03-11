package com.tisawesomeness.minecord.command.core;

import com.tisawesomeness.minecord.command.*;
import com.tisawesomeness.minecord.lang.Lang;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class HelpCommand extends AbstractCoreCommand implements IMultiNameCommand {
    private final @NonNull CommandRegistry registry;

    public @NonNull String getId() {
        return "help";
    }

    public void run(String[] args, CommandContext ctx) {

        if (args.length == 0) {
            ctx.triggerCooldown();
            generalHelp(ctx);
            return;
        }

        if (firstArgMatchesExtra(ctx)) {
            ctx.triggerCooldown();
            extraHelp(ctx);
            return;
        }

        // Check category first
        Lang lang = ctx.getLang();
        Optional<Category> categoryOpt = Category.from(args[0], lang);
        if (categoryOpt.isPresent()) {
            ctx.triggerCooldown();
            Category cat = categoryOpt.get();
            if (cat.isHidden() && !ctx.isElevated()) {
                ctx.noUserPermissions(ctx.i18n("noCategoryPerms"));
                return;
            }
            categoryHelp(ctx, cat);
            return;
        }

        // Check commands last
        Optional<Command> cmdOpt = registry.getCommand(args[0], lang);
        if (cmdOpt.isPresent()) {
            ctx.triggerCooldown();
            Command c = cmdOpt.get();
            // Elevation check
            if (c instanceof IElevatedCommand && !ctx.isElevated()) {
                ctx.notElevated(ctx.i18n("noCommandPerms"));
                return;
            }
            // Admin check
            if (args.length > 1 && "admin".equalsIgnoreCase(args[1])) {
                ctx.reply(c.showAdminHelp(ctx));
                return;
            }
            ctx.reply(c.showHelp(ctx));
            return;
        }

        // Extra help
        Optional<ExtraHelpPage> ehpOpt = ExtraHelpPage.from(args[0], lang);
        if (ehpOpt.isPresent()) {
            ctx.triggerCooldown();
            ExtraHelpPage ehp = ehpOpt.get();
            ctx.reply(ehp.showHelp(ctx));
            return;
        }

        ctx.invalidArgs(ctx.i18n("doesNotExist"));
    }

    private boolean firstArgMatchesExtra(CommandContext ctx) {
        String arg = ctx.getArgs()[0];
        return arg.equalsIgnoreCase(ctx.i18n("extra"))
                || arg.equalsIgnoreCase(Lang.getDefault().i18n(formatKey("extra")));
    }

    private void generalHelp(CommandContext ctx) {
        Lang lang = ctx.getLang();

        EmbedBuilder eb = new EmbedBuilder()
                .setAuthor(ctx.i18n("title"), null, getAvatarUrl(ctx))
                .setDescription(getMoreHelp(ctx));

        // Hidden categories must be viewed directly
        for (Category cat : Category.values()) {
            if (cat.isHidden()) {
                continue;
            }
            // Build that category's list of user-facing commands
            Collection<Command> cmds = registry.getCommandsInCategory(cat);
            if (cmds.isEmpty()) {
                continue;
            }
            String mHelp = cmds.stream()
                    .filter(c -> !(c instanceof IHiddenCommand))
                    .map(c -> c.formatCommandName(ctx))
                    .collect(Collectors.joining(", "));
            eb.addField(lang.localize(cat), mHelp, false);
        }
        ctx.reply(eb);
    }
    // Help menu only contains names of commands, tell user how to get more help
    private static String getMoreHelp(CommandContext ctx) {
        String prefix = ctx.getPrefix();
        String commandUsage = MarkdownUtil.monospace(ctx.i18nf("commandUsage", prefix));
        String commandHelp = ctx.i18nf("commandHelp", commandUsage);
        String categoryUsage = MarkdownUtil.monospace(ctx.i18nf("categoryUsage", prefix));
        String categoryHelp = ctx.i18nf("categoryHelp", categoryUsage);
        String extraUsage = MarkdownUtil.monospace(ctx.i18nf("extraUsage", prefix));
        String extraHelp = ctx.i18nf("extraHelp", extraUsage);
        return commandHelp + "\n" + categoryHelp + "\n" + extraHelp;
    }

    private void categoryHelp(CommandContext ctx, Category cat) {
        Lang lang = ctx.getLang();
        String prefix = ctx.getPrefix();

        String mUsage = registry.getCommandsInCategory(cat).stream()
                .filter(c -> !(c instanceof IHiddenCommand) || cat.isHidden()) // All admin commands are hidden
                .map(c -> formatCommandFull(ctx, c))
                .collect(Collectors.joining("\n"));
        // Add category-specific help if it exists
        Optional<String> mHelp = cat.getHelp(lang, prefix);
        if (mHelp.isPresent()) {
            mUsage = mHelp.get() + "\n\n" + mUsage;
        }
        String title = ctx.i18nf("categoryTitle", lang.localize(cat));
        EmbedBuilder eb = new EmbedBuilder()
                .setAuthor(title, null, getAvatarUrl(ctx))
                .setDescription(mUsage);
        ctx.reply(eb);
    }
    private static String formatCommandFull(CommandContext ctx, Command c) {
        String prefix = ctx.getPrefix();
        Lang lang = ctx.getLang();
        // Formatting changes based on whether the command has arguments
        Optional<String> usageOpt = c.getUsage(lang);
        if (usageOpt.isPresent()) {
            if (c.isEnabled(ctx.getConfig().getCommandConfig())) {
                return String.format("`%s%s %s` - %s",
                        prefix, c.getDisplayName(lang), usageOpt.get(), c.getDescription(lang));
            }
            return String.format("~~`%s%s %s`~~ - %s",
                    prefix, c.getDisplayName(lang), usageOpt.get(), c.getDescription(lang));
        }
        if (c.isEnabled(ctx.getConfig().getCommandConfig())) {
            return String.format("`%s%s` - %s",
                    prefix, c.getDisplayName(lang), c.getDescription(lang));
        }
        return String.format("~~`%s%s`~~ - %s",
                prefix, c.getDisplayName(lang), c.getDescription(lang));
    }

    private static String getAvatarUrl(CommandContext ctx) {
        return ctx.getE().getJDA().getSelfUser().getEffectiveAvatarUrl();
    }

    private static void extraHelp(CommandContext ctx) {
        String desc = Arrays.stream(ExtraHelpPage.values())
                .map(ehp -> extraHelpLine(ctx, ehp))
                .collect(Collectors.joining("\n"));
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(ctx.i18n("extraHelpTitle"))
                .setDescription(desc);
        ctx.reply(eb);
    }
    private static String extraHelpLine(CommandContext ctx, ExtraHelpPage ehp) {
        Lang lang = ctx.getLang();
        return String.format("`%shelp %s` - %s", ctx.getPrefix(), lang.localize(ehp), ehp.getDescription(lang));
    }

}
