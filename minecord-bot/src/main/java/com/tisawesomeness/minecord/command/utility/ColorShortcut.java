package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.meta.Command;
import com.tisawesomeness.minecord.command.meta.CommandContext;
import com.tisawesomeness.minecord.command.meta.IHiddenCommand;
import com.tisawesomeness.minecord.command.meta.IShortcutCommand;
import com.tisawesomeness.minecord.config.config.CommandConfig;
import com.tisawesomeness.minecord.lang.Lang;
import com.tisawesomeness.minecord.util.Colors;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.Color;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ColorShortcut extends ColorCommand implements IShortcutCommand, IHiddenCommand {

    private final Command colorCmd;
    private final String colorCode;
    private final Color color;
    public ColorShortcut(Command colorCmd, int index) {
        this.colorCmd = colorCmd;
        colorCode = String.format("%01x", index);
        color = Colors.getColor(index);
    }

    @Override
    public @NonNull String getId() {
        return colorCode;
    }
    @Override
    public @NonNull String getDisplayName(Lang lang) {
        return colorCode;
    }
    @Override
    public Optional<String> getUsage(Lang lang) {
        return Optional.empty();
    }
    @Override
    public List<String> getAliases(Lang lang) {
        return Collections.emptyList();
    }

    @Override
    public int getCooldown(CommandConfig config) {
        return colorCmd.getCooldown(config);
    }
    @Override
    public @NonNull String getCooldownId(CommandConfig config) {
        return colorCmd.getCooldownId(config);
    }
    @Override
    public Optional<String> getCooldownPool(CommandConfig config) {
        return colorCmd.getCooldownPool(config);
    }

    @Override
    public EmbedBuilder showHelp(CommandContext ctx) {
        return colorCmd.showHelp(ctx);
    }
    @Override
    public EmbedBuilder showAdminHelp(CommandContext ctx) {
        return colorCmd.showAdminHelp(ctx);
    }

    @Override
    public void run(String[] args, CommandContext ctx) {
        ctx.triggerCooldown();
        ctx.replyRaw(ctx.addFooter(buildColorInfo(color)));
    }

}