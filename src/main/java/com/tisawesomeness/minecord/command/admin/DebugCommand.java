package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.CommandExecutor;
import com.tisawesomeness.minecord.command.Result;
import com.tisawesomeness.minecord.database.DatabaseCache;
import com.tisawesomeness.minecord.debug.*;
import com.tisawesomeness.minecord.debug.cache.*;
import com.tisawesomeness.minecord.mc.player.PlayerProvider;
import com.tisawesomeness.minecord.util.ListUtils;
import com.tisawesomeness.minecord.util.MessageUtils;

import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.util.List;
import java.util.stream.Collectors;

public class DebugCommand extends AbstractAdminCommand {

    public @NonNull String getId() {
        return "debug";
    }

    public void run(String[] args, CommandContext ctx) {
        if (!ctx.getConfig().getFlagConfig().isDebugMode()) {
            ctx.warn("The bot is not in debug mode.");
            return;
        }

        List<DebugOption> debugOptions = buildDebugOptionList(ctx);
        if (args.length == 0) {
            String possibleOptions = debugOptions.stream()
                    .map(d -> MarkdownUtil.monospace(d.getName()))
                    .collect(Collectors.joining(", "));
            ctx.reply("Possible options: " + possibleOptions);
            return;
        }

        if ("all".equalsIgnoreCase(args[0])) {
            for (DebugOption d : debugOptions) {
                sendDebugInfo(ctx, d);
            }
            ctx.commandResult(Result.SUCCESS);
            return;
        }

        for (DebugOption d : debugOptions) {
            if (d.getName().equalsIgnoreCase(args[0])) {
                sendDebugInfo(ctx, d);
                ctx.commandResult(Result.SUCCESS);
                return;
            }
        }
        ctx.invalidArgs("Not a valid debug option.");
    }

    private static List<DebugOption> buildDebugOptionList(CommandContext ctx) {
        ShardManager sm = ctx.getBot().getShardManager();
        DatabaseCache dbCache = ctx.getBot().getDatabaseCache();
        CommandExecutor executor = ctx.getExecutor();
        PlayerProvider playerProvider = ctx.getMCLibrary().getPlayerProvider();
        return ListUtils.of(
                new JDADebugOption(sm),
                new ThreadDebugOption(),
                new RegionDebugOption(sm),
                new ItemDebugOption(),
                new PoolsDebugOption(executor),
                new ClientDebugOption(ctx.getBot().getApiClient()),
                new CooldownCacheDebugOption(executor),
                new GuildCacheDebugOption(dbCache),
                new ChannelCacheDebugOption(dbCache),
                new UserCacheDebugOption(dbCache),
                new UuidCacheDebugOption(playerProvider),
                new PlayerCacheDebugOption(playerProvider)
        );
    }

    private static void sendDebugInfo(CommandContext ctx, DebugOption d) {
        String[] args = ctx.getArgs();
        String extra = args.length > 1 ? args[1] : "";
        String debugInfo = d.debug(extra);
        printToConsole(debugInfo, ctx.getE().getAuthor());
        List<String> messages = MessageUtils.splitLinesByLength(debugInfo, Message.MAX_CONTENT_LENGTH);
        for (String message : messages) {
            ctx.getE().getChannel().sendMessage(message).queue();
        }
    }

    private static void printToConsole(String debugInfo, User author) {
        String requestedBy = String.format("Requested By %s (%s)\n", author.getAsTag(), author.getId());
        System.out.println("\n" + requestedBy + MarkdownSanitizer.sanitize(debugInfo) + "\n");
    }
}
