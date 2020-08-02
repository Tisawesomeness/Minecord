package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.Lang;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;

import lombok.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ColorShortcut extends AbstractUtilityCommand {

    private Command colorCmd;
    private String colorCode;
    public ColorShortcut(Command colorCmd, String colorCode) {
        this.colorCmd = colorCmd;
        this.colorCode = colorCode;
    }

    public @NonNull String getId() {
        return colorCode;
    }
    @Override
    public @NonNull String getDisplayName(Lang lang) {
        return colorCode;
    }
    @Override
    public @NonNull String getDescription(Lang lang) {
        return colorCmd.getDescription(lang);
    }
    @Override
    public Optional<String> getUsage(Lang lang) {
        return Optional.empty();
    }
    @Override
    public List<String> getAliases(Lang lang) {
        return Collections.emptyList();
    }

    public CommandInfo getInfo() {
		return new CommandInfo(
                true,
                false,
                true
		);
    }

    public @NonNull String getHelp(Lang lang) {
        return colorCmd.getHelp(lang);
    }

    public Result run(CommandContext ctx) throws Exception {
        return colorCmd.run(ctx.withArgs(new String[]{colorCode}));
    }

}