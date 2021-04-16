package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.command.meta.*;
import com.tisawesomeness.minecord.lang.Lang;
import com.tisawesomeness.minecord.util.Time;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.apache.commons.collections4.SetUtils;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Verifies that a command satisfies all requirements to run.
 */
@RequiredArgsConstructor
public class CommandVerifier {
    private final CommandExecutor exe;

    /**
     * Checks whether a command should run, by checking cooldowns, permissions, etc. This method will automatically
     * send the appropriate warning messages based on which requirement was not satisfied.
     * @param ctx The context of the command
     * @return Whether the command should run
     */
    public boolean shouldRun(CommandContext ctx) {
        if (ctx.getCmd().isEnabled(ctx.getConfig().getCommandConfig()) || shouldBypassDisabled(ctx)) {
            return processElevation(ctx);
        }
        ctx.warn(ctx.getLang().i18nf("command.meta.disabled", ctx.formatCommandName()));
        return false;
    }
    private static boolean shouldBypassDisabled(CommandContext ctx) {
        return ctx.getConfig().getFlagConfig().isElevatedBypassDisabled()
                && ctx.isElevated()
                && !(ctx.getCmd() instanceof IElevatedCommand);
    }

    private boolean processElevation(CommandContext ctx) {
        if (ctx.getCmd() instanceof IElevatedCommand && !ctx.isElevated()) {
            ctx.notElevated(ctx.getLang().i18nf("command.meta.notBotAdmin", ctx.formatCommandName()));
            return false;
        }
        return processGuildOnly(ctx);
    }
    private boolean processGuildOnly(CommandContext ctx) {
        Command c = ctx.getCmd();
        if (c instanceof IGuildOnlyCommand && !ctx.isFromGuild()) {
            IGuildOnlyCommand goc = (IGuildOnlyCommand) c;
            if (!ctx.isElevated() || goc.guildOnlyAppliesToAdmins()) {
                ctx.guildOnly(ctx.getLang().i18nf("command.meta.noDMs", ctx.formatCommandName()));
                return false;
            }
        }
        return processPerms(ctx);
    }

    private boolean processPerms(CommandContext ctx) {
        if (ctx.isFromGuild()) {
            return processBotPerms(ctx);
        }
        return processCooldown(ctx);
    }
    private boolean processBotPerms(CommandContext ctx) {
        Lang lang = ctx.getLang();
        if (!ctx.botHasPermission(Permission.MESSAGE_EMBED_LINKS)) {
            ctx.noBotPermissions(lang.i18n("command.meta.missingEmbedLinks"));
            return false;
        }

        EnumSet<Permission> requiredBotPerms = ctx.getCmd().getBotPermissions();
        if (!ctx.botHasPermission(requiredBotPerms)) {

            EnumSet<Permission> currentBotPerms = getCurrentBotPerms(ctx);
            Set<Permission> missingPerms = SetUtils.difference(requiredBotPerms, currentBotPerms);
            String missingPermsStr = getMissingPermissionString(ctx, missingPerms);
            // Size included to determine if "permission" is plural
            String errMsg = lang.i18nf("command.meta.missingBotPermissions",
                    missingPermsStr, missingPerms.size());
            ctx.noBotPermissions(errMsg);
            return false;

        }
        return processUserPerms(ctx);
    }
    private static EnumSet<Permission> getCurrentBotPerms(CommandContext ctx) {
        MessageReceivedEvent e = ctx.getE();
        return e.getGuild().getSelfMember().getPermissions(e.getTextChannel());
    }
    private boolean processUserPerms(CommandContext ctx) {
        if (!ctx.isElevated()) {
            EnumSet<Permission> requiredUserPerms = ctx.getCmd().getUserPermissions();
            if (!ctx.userHasPermission(requiredUserPerms)) {

                EnumSet<Permission> currentUserPerms = getCurrentUserPerms(ctx);
                Set<Permission> missingPerms = SetUtils.difference(requiredUserPerms, currentUserPerms);
                String missingPermsStr = getMissingPermissionString(ctx, requiredUserPerms);
                Lang lang = ctx.getLang();
                // Size included to determine if "permission" is plural
                String errMsg = lang.i18nf("command.meta.missingUserPermissions",
                        missingPermsStr, missingPerms.size());
                ctx.noUserPermissions(errMsg);
                return false;

            }
        }
        return processMultiLines(ctx);
    }
    private static EnumSet<Permission> getCurrentUserPerms(CommandContext ctx) {
        MessageReceivedEvent e = ctx.getE();
        return Objects.requireNonNull(e.getMember()).getPermissions(e.getTextChannel());
    }
    private static String getMissingPermissionString(CommandContext ctx, Collection<Permission> missingPerms) {
        Lang lang = ctx.getLang();
        return missingPerms.stream()
                .map(lang::localize)
                .map(MarkdownUtil::bold)
                .collect(Collectors.joining(", "));
    }

    private boolean processMultiLines(CommandContext ctx) {
        if (!(ctx.getCmd() instanceof IMultiLineCommand)) {
            for (String arg : ctx.getArgs()) {
                if (arg.contains("\n") || arg.contains("\r")) {
                    ctx.warn(ctx.getLang().i18nf("command.meta.oneLine", ctx.formatCommandName()));
                    return false;
                }
            }
        }
        return processCooldown(ctx);
    }

    private boolean processCooldown(CommandContext ctx) {
        if (!exe.shouldSkipCooldown(ctx)) {
            long cooldown = exe.getCooldown(ctx.getCmd());
            if (cooldown > 0) {
                return checkCooldown(ctx, cooldown);
            }
        }
        return true;
    }
    private boolean checkCooldown(CommandContext ctx, long cooldown) {
        long uid = ctx.getUserId();
        long lastExecutedTime = exe.getLastExecutedTime(ctx.getCmd(), uid);
        long msLeft = cooldown + lastExecutedTime - System.currentTimeMillis();
        if (msLeft > 0) {
            Lang lang = ctx.getLang();
            String cooldownSeconds = Time.formatMillisAsSeconds(msLeft, lang.getLocale());
            String cooldownMsg = lang.i18nf("command.meta.onCooldown", MarkdownUtil.monospace(cooldownSeconds));
            ctx.sendResult(Result.COOLDOWN, cooldownMsg);
            return false;
        }
        return true;
    }

}
