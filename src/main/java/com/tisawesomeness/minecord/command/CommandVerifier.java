package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.lang.Lang;
import com.tisawesomeness.minecord.util.TimeUtils;

import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

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
     * @param c The command
     * @param ctx The context of the command
     * @return Whether the command should run
     */
    public boolean shouldRun(Command c, CommandContext ctx) {
        if (c.isEnabled(ctx.getConfig().getCommandConfig()) || shouldBypassDisabled(c, ctx)) {
            return processElevation(c, ctx);
        }
        ctx.warn(ctx.getLang().i18nf("command.meta.disabled", ctx.formatCommandName()));
        return false;
    }
    private static boolean shouldBypassDisabled(Command c, CommandContext ctx) {
        return ctx.getConfig().getFlagConfig().isElevatedBypassDisabled()
                && ctx.isElevated()
                && !(c instanceof IElevatedCommand);
    }

    private boolean processElevation(Command c, CommandContext ctx) {
        if (c instanceof IElevatedCommand && !ctx.isElevated()) {
            ctx.notElevated(ctx.getLang().i18nf("command.meta.notBotAdmin", ctx.formatCommandName()));
            return false;
        }
        return processGuildOnly(c, ctx);
    }
    private boolean processGuildOnly(Command c, CommandContext ctx) {
        if (c instanceof IGuildOnlyCommand && !ctx.isFromGuild()) {
            IGuildOnlyCommand goc = (IGuildOnlyCommand) c;
            if (!ctx.isElevated() || goc.guildOnlyAppliesToAdmins()) {
                ctx.guildOnly(ctx.getLang().i18nf("command.meta.noDMs", ctx.formatCommandName()));
                return false;
            }
        }
        return processPerms(c, ctx);
    }

    private boolean processPerms(Command c, CommandContext ctx) {
        if (ctx.isFromGuild()) {
            return processBotPerms(c, ctx);
        }
        return processCooldown(c, ctx);
    }
    private boolean processBotPerms(Command c, CommandContext ctx) {
        Lang lang = ctx.getLang();
        if (!ctx.botHasPermission(Permission.MESSAGE_EMBED_LINKS)) {
            ctx.noBotPermissions(lang.i18n("command.meta.missingEmbedLinks"));
            return false;
        }

        EnumSet<Permission> requiredBotPerms = c.getBotPermissions();
        if (!ctx.botHasPermission(requiredBotPerms)) {

            EnumSet<Permission> currentBotPerms = getCurrentBotPerms(ctx);
            Set<Permission> missingPerms = Sets.difference(requiredBotPerms, currentBotPerms);
            String missingPermsStr = getMissingPermissionString(ctx, missingPerms);
            // Size included to determine if "permission" is plural
            String errMsg = lang.i18nf("command.meta.missingBotPermissions",
                    missingPermsStr, missingPerms.size());
            ctx.noBotPermissions(errMsg);
            return false;

        }
        return processUserPerms(c, ctx);
    }
    private static EnumSet<Permission> getCurrentBotPerms(CommandContext ctx) {
        MessageReceivedEvent e = ctx.getE();
        return e.getGuild().getSelfMember().getPermissions(e.getTextChannel());
    }
    private boolean processUserPerms(Command c, CommandContext ctx) {
        if (!ctx.isElevated()) {
            EnumSet<Permission> requiredUserPerms = c.getUserPermissions();
            if (!ctx.userHasPermission(requiredUserPerms)) {

                EnumSet<Permission> currentUserPerms = getCurrentUserPerms(ctx);
                Set<Permission> missingPerms = Sets.difference(requiredUserPerms, currentUserPerms);
                String missingPermsStr = getMissingPermissionString(ctx, requiredUserPerms);
                Lang lang = ctx.getLang();
                // Size included to determine if "permission" is plural
                String errMsg = lang.i18nf("command.meta.missingUserPermissions",
                        missingPermsStr, missingPerms.size());
                ctx.noUserPermissions(errMsg);
                return false;

            }
        }
        return processMultiLines(c, ctx);
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

    private boolean processMultiLines(Command c, CommandContext ctx) {
        if (!(c instanceof IMultiLineCommand)) {
            for (String arg : ctx.getArgs()) {
                if (arg.contains("\n") || arg.contains("\r")) {
                    ctx.warn(ctx.getLang().i18nf("command.meta.oneLine", ctx.formatCommandName()));
                    return false;
                }
            }
        }
        return processCooldown(c, ctx);
    }

    private boolean processCooldown(Command c, CommandContext ctx) {
        if (!exe.shouldSkipCooldown(ctx)) {
            long cooldown = exe.getCooldown(c);
            if (cooldown > 0) {
                return checkCooldown(c, ctx, cooldown);
            }
        }
        return true;
    }
    private boolean checkCooldown(Command c, CommandContext ctx, long cooldown) {
        long uid = ctx.getUserId();
        long lastExecutedTime = exe.getLastExecutedTime(c, uid);
        long msLeft = cooldown + lastExecutedTime - System.currentTimeMillis();
        if (msLeft > 0) {
            Lang lang = ctx.getLang();
            String cooldownSeconds = TimeUtils.formatMillisAsSeconds(msLeft, lang.getLocale());
            String cooldownMsg = lang.i18nf("command.meta.onCooldown", MarkdownUtil.monospace(cooldownSeconds));
            ctx.sendResult(Result.COOLDOWN, cooldownMsg);
            return false;
        }
        return true;
    }

}
