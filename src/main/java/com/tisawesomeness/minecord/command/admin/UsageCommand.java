package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.Lang;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.CommandRegistry;
import com.tisawesomeness.minecord.command.IShortcutCommand;
import com.tisawesomeness.minecord.command.Module;
import com.tisawesomeness.minecord.command.Result;
import com.tisawesomeness.minecord.util.DateUtils;

import com.google.common.collect.EnumMultiset;
import com.google.common.collect.Multiset;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;

import java.sql.SQLException;
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
        Lang lang = ctx.getLang();
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Command usage for " + DateUtils.getDurationString(ctx.getBot().getBirth()));

        if (args.length == 0) {
            return processGlobalUsage(ctx, eb);
        } else if ("full".equalsIgnoreCase(args[0])) {
            return processFullUsage(ctx, eb);
        }
        Optional<Module> moduleOpt = Module.from(args[0], lang);
        if (moduleOpt.isPresent()) {
            return getModuleUsage(ctx, eb, moduleOpt.get());
        }
        Optional<Command> cmdOpt = registry.getCommand(args[0], lang);
        if (cmdOpt.isPresent()) {
            return getCommandUsage(ctx, eb, cmdOpt.get());
        }
        return ctx.invalidArgs("That command or module does not exist.");
    }

    private Result processGlobalUsage(CommandContext ctx, EmbedBuilder eb) {
        addFields(ctx, eb, c -> formatCommand(c, ctx));
        Multiset<Result> totalResults = accumulateResults(registry, ctx);
        eb.setDescription(formatResults(totalResults));
        return ctx.reply(eb);
    }
    private Result processFullUsage(CommandContext ctx, EmbedBuilder eb) {
        ctx.getExecutor().pushUses(); // Make sure uses are up-to-date
        try {
            Multiset<String> commandUses = ctx.getExecutor().getCommandStats().getCommandUses();
            addFields(ctx, eb, c -> formatCommandFull(c, ctx, commandUses));
            eb.setDescription(totalHeader(commandUses));
            return ctx.reply(eb);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return ctx.err("There was an internal error.");
    }
    private void addFields(CommandContext ctx, EmbedBuilder eb, Function<Command, String> commandToLineMapper) {
        for (Module m : Module.values()) {
            Collection<Command> cmds = registry.getCommandsInModule(m);
            if (cmds.isEmpty()) {
                continue;
            }
            String field = buildUsageString(cmds, commandToLineMapper);
            eb.addField(String.format("**%s**", m.getDisplayName(ctx.getLang())), field, true);
        }
    }

    private Result getModuleUsage(CommandContext ctx, EmbedBuilder eb, Module m) {
        Collection<Command> cmds = registry.getCommandsInModule(m);
        if (cmds.isEmpty()) {
            return ctx.warn("That module has no commands!");
        }
        Multiset<Result> totalResults = accumulateResults(cmds, ctx);
        String usage = buildUsageString(cmds, c -> formatCommandDetailed(c, ctx));
        eb.setDescription(formatResults(totalResults) + "\n\n" + usage);
        return ctx.reply(eb);
    }
    private static Result getCommandUsage(CommandContext ctx, EmbedBuilder eb, Command c) {
        Multiset<Result> results = ctx.getExecutor().getResults(c);
        eb.setDescription(formatResults(results));
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
        return formatLine(c, ctx, String.valueOf(ctx.getExecutor().getResults(c).size()));
    }
    private static String formatCommandFull(Command c, CommandContext ctx, Multiset<String> uses) {
        return formatLine(c, ctx, String.valueOf(uses.count(c.getId())));
    }
    private static String formatCommandDetailed(Command c, CommandContext ctx) {
        Multiset<Result> results = ctx.getExecutor().getResults(c);
        return formatLine(c, ctx, getResultListString(results));
    }
    private static String getResultListString(Multiset<Result> results) {
        if (results.isEmpty()) {
            return "None";
        }
        String resultListString = results.entrySet().stream()
                .sorted(Comparator.comparing(Multiset.Entry::getElement))
                .map(en -> String.format("**%d** %s", en.getCount(), en.getElement().getInternalEmote()))
                .collect(Collectors.joining(" | "));
        return String.format("%s | Total **%d**", resultListString, results.size());
    }
    private static String formatLine(Command c, CommandContext ctx, String line) {
        return String.format("`%s%s` **-** %s", ctx.getPrefix(), c.getDisplayName(ctx.getLang()), line);
    }
    private static String totalHeader(Multiset<?> results) {
        return String.format("**__Total: %d__**", results.size());
    }
    private static String formatResults(Multiset<Result> results) {
        String header = totalHeader(results);
        String resultLines = results.entrySet().stream()
                .sorted(Comparator.comparing(Multiset.Entry::getElement))
                .map(en -> formatResult(en, results.size()))
                .collect(Collectors.joining("\n"));
        return header + "\n" + resultLines;
    }
    private static String formatResult(Multiset.Entry<Result> en, int total) {
        Result result = en.getElement();
        int c = en.getCount();
        return String.format("**%s** %s `%d` | `%.2f%%`", result, result.getInternalEmote(), c, 100.0 * c / total);
    }

    private static Multiset<Result> accumulateResults(Iterable<? extends Command> cmds, CommandContext ctx) {
        Multiset<Result> totalResults = EnumMultiset.create(Result.class);
        for (Command c : cmds) {
            totalResults.addAll(ctx.getExecutor().getResults(c));
        }
        return totalResults;
    }

}
