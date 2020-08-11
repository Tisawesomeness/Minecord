package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.Lang;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.CommandRegistry;
import com.tisawesomeness.minecord.command.IShortcutCommand;
import com.tisawesomeness.minecord.command.Module;
import com.tisawesomeness.minecord.command.Result;
import com.tisawesomeness.minecord.util.DateUtils;

import com.google.common.collect.Multiset;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UsageCommand extends AbstractAdminCommand {

    private final @NonNull CommandRegistry registry;

    public @NonNull String getId() {
        return "usage";
    }

    public Result run(String[] args, CommandContext ctx) {
        Lang lang = ctx.lang;
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Command usage for " + DateUtils.getDurationString(ctx.bot.getBirth()));

        if (args.length == 0) {
            return processGlobalUsage(ctx, lang, eb);
        }
        Optional<Module> moduleOpt = Module.from(args[0], lang);
        if (moduleOpt.isPresent()) {
            return getModuleUsage(ctx, eb, moduleOpt.get());
        }
        return ctx.warn("That module does not exist.");
    }

    private Result getModuleUsage(CommandContext ctx, EmbedBuilder eb, Module m) {
        Collection<Command> cmds = registry.getCommandsInModule(m);
        if (cmds.isEmpty()) {
            return ctx.warn("That module has no commands!");
        }
        String desc = buildUsageString(cmds, c -> formatCommandDetailed(c, ctx));
        eb.setDescription(desc);
        return ctx.reply(eb);
    }
    private Result processGlobalUsage(CommandContext ctx, Lang lang, EmbedBuilder eb) {
        for (Module m : Module.values()) {
            Collection<Command> cmds = registry.getCommandsInModule(m);
            if (cmds.isEmpty()) {
                continue;
            }
            String field = buildUsageString(cmds, c -> formatCommand(c, ctx));
            eb.addField(String.format("**%s**", m.getDisplayName(lang)), field, true);
        }
        return ctx.reply(eb);
    }

    private static String buildUsageString(Collection<? extends Command> cmds,
                                           Function<? super Command, String> commandToLineMapper) {
        return cmds.stream()
                .filter(c -> !(c instanceof IShortcutCommand))
                .map(commandToLineMapper)
                .collect(Collectors.joining("\n"));
    }

    private static String formatCommand(Command c, CommandContext ctx) {
        return formatLine(c, ctx, String.valueOf(ctx.getResultsFor(c).size()));
    }
    private static String formatCommandDetailed(Command c, CommandContext ctx) {
        Multiset<Result> results = ctx.getResultsFor(c);
        return formatLine(c, ctx, getResultListString(results));
    }
    private static String getResultListString(Multiset<Result> results) {
        if (results.isEmpty()) {
            return "None";
        }
        return results.entrySet().stream()
                    .sorted(Comparator.comparing(Multiset.Entry::getElement))
                    .map(en -> String.format("**%d** %s", en.getCount(), en.getElement().getEmote()))
                    .collect(Collectors.joining(" | "));
    }
    private static String formatLine(Command c, CommandContext ctx, String line) {
        return String.format("`%s%s` **-** %s", ctx.prefix, c.getDisplayName(ctx.lang), line);
    }

}
