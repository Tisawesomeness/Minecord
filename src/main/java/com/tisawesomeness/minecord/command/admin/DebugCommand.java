package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.Result;
import com.tisawesomeness.minecord.database.DatabaseCache;
import com.tisawesomeness.minecord.debug.ChannelCacheDebugOption;
import com.tisawesomeness.minecord.debug.CooldownCacheDebugOption;
import com.tisawesomeness.minecord.debug.DebugOption;
import com.tisawesomeness.minecord.debug.GuildCacheDebugOption;
import com.tisawesomeness.minecord.debug.JDADebugOption;
import com.tisawesomeness.minecord.debug.RegionDebugOption;
import com.tisawesomeness.minecord.debug.ThreadDebugOption;
import com.tisawesomeness.minecord.debug.UserCacheDebugOption;
import com.tisawesomeness.minecord.util.MessageUtils;

import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DebugCommand extends AbstractAdminCommand {

    public @NonNull String getId() {
        return "debug";
    }

    public Result run(String[] args, CommandContext ctx) {
        if (!ctx.config.getFlagConfig().isDebugMode()) {
            return ctx.warn("The bot is not in debug mode.");
        }

        ShardManager sm = ctx.bot.getShardManager();
        DatabaseCache dbCache = ctx.bot.getDatabaseCache();
        List<DebugOption> debugOptions = Arrays.asList(
                new JDADebugOption(sm),
                new ThreadDebugOption(),
                new RegionDebugOption(sm),
                new GuildCacheDebugOption(dbCache),
                new ChannelCacheDebugOption(dbCache),
                new UserCacheDebugOption(dbCache),
                new CooldownCacheDebugOption(ctx.executor)
        );

        if (args.length == 0) {
            String possibleOptions = debugOptions.stream()
                    .map(d -> MarkdownUtil.monospace(d.getName()))
                    .collect(Collectors.joining(", "));
            return ctx.reply("Possible options: " + possibleOptions);
        }

        if ("all".equalsIgnoreCase(args[0])) {
            for (DebugOption d : debugOptions) {
                sendDebugInfo(ctx, d);
            }
            return Result.SUCCESS;
        }

        for (DebugOption d : debugOptions) {
            if (d.getName().equalsIgnoreCase(args[0])) {
                sendDebugInfo(ctx, d);
                return Result.SUCCESS;
            }
        }
        return ctx.warn("Not a valid debug option.");
    }

    private static void sendDebugInfo(CommandContext ctx, DebugOption d) {
        String debugInfo = d.debug();
        printToConsole(debugInfo, ctx.e.getAuthor());
        List<String> messages = MessageUtils.splitLinesByLength(debugInfo, Message.MAX_CONTENT_LENGTH);
        for (String message : messages) {
            ctx.e.getChannel().sendMessage(message).queue();
        }
    }

    private static void printToConsole(String debugInfo, User author) {
        String requestedBy = String.format("Requested By %s (%s)\n", author.getAsTag(), author.getId());
        System.out.println("\n" + requestedBy + debugInfo + "\n");
    }
}
