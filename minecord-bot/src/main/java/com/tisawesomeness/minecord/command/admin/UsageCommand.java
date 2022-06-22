package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.CommandRegistry;
import com.tisawesomeness.minecord.command.meta.*;
import com.tisawesomeness.minecord.lang.Lang;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.type.EnumMultiSet;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.apache.commons.collections4.MultiSet;

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

    public void run(String[] args, CommandContext ctx) {
        if (args.length == 0) {
            processGlobalUsage(ctx);
            return;
        } else if ("full".equalsIgnoreCase(args[0])) {
            processFullUsage(ctx);
            return;
        }
        Lang lang = ctx.getLang();
        Optional<Category> categoryOpt = Category.from(args[0], lang);
        if (categoryOpt.isPresent()) {
            getCategoryUsage(ctx, categoryOpt.get());
            return;
        }
        Optional<Command> cmdOpt = registry.getCommand(args[0], lang);
        if (cmdOpt.isPresent()) {
            getCommandUsage(ctx, cmdOpt.get());
            return;
        }
        ctx.invalidArgs("That command or category does not exist.");
    }

    private void processGlobalUsage(CommandContext ctx) {
        MultiSet<Result> totalResults = accumulateResults(registry, ctx);
        int uses = ctx.getExecutor().getTotalUses();
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(commandUsageTitle(ctx))
                .setDescription(formatResults(totalResults, uses));
        addFields(ctx, eb, c -> formatCommand(c, ctx));
        ctx.reply(eb);
    }
    private void processFullUsage(CommandContext ctx) {
        ctx.getExecutor().pushUses(); // Make sure uses are up-to-date
        try {
            MultiSet<String> commandUses = ctx.getExecutor().getCommandStats().getCommandUses();
            EmbedBuilder eb = new EmbedBuilder()
                    .setTitle("Full Command Usage")
                    .setDescription(usesHeader(commandUses.size()));
            addFields(ctx, eb, c -> formatCommandFull(c, ctx, commandUses));
            ctx.reply(eb);
            return;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        ctx.err("There was an internal error.");
    }
    private void addFields(CommandContext ctx, EmbedBuilder eb, Function<? super Command, String> commandToLineMapper) {
        for (Category cat : Category.values()) {
            Collection<Command> cmds = registry.getCommandsInCategory(cat);
            if (cmds.isEmpty()) {
                continue;
            }
            String field = buildUsageString(cmds, commandToLineMapper);
            eb.addField(MarkdownUtil.bold(ctx.getLang().localize(cat)), field, true);
        }
    }

    private void getCategoryUsage(CommandContext ctx, Category cat) {
        Collection<Command> cmds = registry.getCommandsInCategory(cat);
        if (cmds.isEmpty()) {
            ctx.warn("That category has no commands!");
            return;
        }
        MultiSet<Result> totalResults = accumulateResults(cmds, ctx);
        String usage = buildUsageString(cmds, c -> formatCommandDetailed(c, ctx));
        int uses = ctx.getExecutor().getUses(cat);
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(commandUsageTitle(ctx))
                .setDescription(formatResults(totalResults, uses) + "\n\n" + usage);
        ctx.reply(eb);
    }
    private static void getCommandUsage(CommandContext ctx, Command c) {
        MultiSet<Result> results = ctx.getExecutor().getResults(c);
        int uses = ctx.getExecutor().getUses(c);
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(commandUsageTitle(ctx))
                .setDescription(formatResults(results, uses));
        ctx.reply(eb);
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
    private static String formatCommandFull(Command c, CommandContext ctx, MultiSet<String> uses) {
        return formatLine(c, ctx, String.valueOf(uses.getCount(c.getId())));
    }
    private static String formatCommandDetailed(Command c, CommandContext ctx) {
        MultiSet<Result> results = ctx.getExecutor().getResults(c);
        return formatLine(c, ctx, getResultListString(results));
    }
    private static String getResultListString(MultiSet<Result> results) {
        if (results.isEmpty()) {
            return "None";
        }
        String resultListString = results.entrySet().stream()
                .sorted(Comparator.comparing(MultiSet.Entry::getElement))
                .map(en -> String.format("**%d** %s", en.getCount(), en.getElement().getInternalEmote()))
                .collect(Collectors.joining(" | "));
        return String.format("%s | Total **%d**", resultListString, results.size());
    }
    private static String formatLine(Command c, CommandContext ctx, String line) {
        return String.format("`%s%s` **-** %s", ctx.getPrefix(), c.getDisplayName(ctx.getLang()), line);
    }
    private static String totalHeader(MultiSet<?> results, int uses) {
        return String.format("**__Total: %d__ (%d uses)**", results.size(), uses);
    }
    private static String usesHeader(int uses) {
        return String.format("**__Total: %d uses__**", uses);
    }
    private static String formatResults(MultiSet<Result> results, int uses) {
        String header = totalHeader(results, uses);
        String resultLines = results.entrySet().stream()
                .sorted(Comparator.comparing(MultiSet.Entry::getElement))
                .map(en -> formatResult(en, results.size()))
                .collect(Collectors.joining("\n"));
        return header + "\n" + resultLines;
    }
    private static String formatResult(MultiSet.Entry<Result> en, int total) {
        Result result = en.getElement();
        int c = en.getCount();
        return String.format("**%s** %s `%d` | `%.2f%%`", result, result.getInternalEmote(), c, 100.0 * c / total);
    }

    private static MultiSet<Result> accumulateResults(Iterable<? extends Command> cmds, CommandContext ctx) {
        MultiSet<Result> totalResults = new EnumMultiSet<>(Result.class);
        for (Command c : cmds) {
            totalResults.addAll(ctx.getExecutor().getResults(c));
        }
        return totalResults;
    }

    private static String commandUsageTitle(CommandContext ctx) {
        return "Command usage for " + DateUtils.getDurationString(ctx.getBot().getBirth());
    }

}
