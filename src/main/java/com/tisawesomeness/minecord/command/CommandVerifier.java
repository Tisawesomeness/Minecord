package com.tisawesomeness.minecord.command;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Objects;
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
        if (c.isEnabled(ctx.getConfig().getCommandConfig())) {
            return processGuildOnly(c, ctx);
        }
        ctx.warn(ctx.getLang().i18n("command.meta.disabled"));
        return false;
    }

    private boolean processGuildOnly(Command c, CommandContext ctx) {
        if (c instanceof IGuildOnlyCommand && !ctx.isFromGuild()) {
            IGuildOnlyCommand goc = (IGuildOnlyCommand) c;
            if (!ctx.isElevated() || goc.guildOnlyAppliesToAdmins()) {
                ctx.guildOnly("This command is not available in DMs.");
                return false;
            }
        }
        return processElevation(c, ctx);
    }
    private boolean processElevation(Command c, CommandContext ctx) {
        if (c instanceof IElevatedCommand && !ctx.isElevated()) {
            ctx.notElevated("You must be elevated to use that command!");
            return false;
        }
        return processPermissions(c, ctx);
    }

    private boolean processPermissions(Command c, CommandContext ctx) {
        if (ctx.isFromGuild()) {
            return processBotPermissions(c, ctx);
        }
        return processCooldown(c, ctx);
    }
    private boolean processBotPermissions(Command c, CommandContext ctx) {
        if (!ctx.botHasPermission(Permission.MESSAGE_EMBED_LINKS)) {
            ctx.noBotPermissions("I need Embed Links permissions to use commands!");
            return false;
        }
        EnumSet<Permission> rbp = c.getBotPermissions();
        if (!ctx.botHasPermission(rbp)) {
            Member sm = ctx.getE().getGuild().getSelfMember();
            TextChannel tc = ctx.getE().getTextChannel();
            String missingPermissions = getMissingPermissionString(sm, tc, rbp);
            String errMsg = String.format("I am missing the %s permissions.", missingPermissions);
            ctx.noBotPermissions(errMsg);
            return false;
        }
        return processUserPermissions(c, ctx);
    }
    private boolean processUserPermissions(Command c, CommandContext ctx) {
        if (!ctx.isElevated()) {
            EnumSet<Permission> rup = c.getUserPermissions();
            if (!ctx.userHasPermission(rup)) {
                Member mem = Objects.requireNonNull(ctx.getE().getMember());
                TextChannel tc = ctx.getE().getTextChannel();
                String missingPermissions = getMissingPermissionString(mem, tc, rup);
                String errMsg = String.format("You are missing the %s permissions.", missingPermissions);
                ctx.noUserPermissions(errMsg);
                return false;
            }
        }
        return processCooldown(c, ctx);
    }
    // Mutates permissions collection! Only use when done
    private static String getMissingPermissionString(Member m, TextChannel tc, Collection<Permission> permissions) {
        permissions.removeAll(m.getPermissions(tc));
        return permissions.stream()
                .map(Permission::getName)
                .collect(Collectors.joining(", "));
    }

    private boolean processCooldown(Command c, CommandContext ctx) {
        if (!exe.shouldSkipCooldown(ctx)) {
            long cooldown = exe.getCooldown(c);
            if (cooldown > 0) {
                long uid = ctx.getUserId();
                long lastExecutedTime = exe.getLastExecutedTime(c, uid);
                long msLeft = cooldown + lastExecutedTime - System.currentTimeMillis();
                if (msLeft > 0) {
                    String cooldownMsg = String.format("Wait `%.3f` more seconds.", (double) msLeft/1000);
                    ctx.sendResult(Result.COOLDOWN, cooldownMsg);
                    return false;
                }
            }
        }
        return true;
    }

}
