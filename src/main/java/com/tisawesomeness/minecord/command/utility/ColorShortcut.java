package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;

public class ColorShortcut extends Command {

    private Command colorCmd;
    private String colorCode;
    public ColorShortcut(Command colorCmd, String colorCode) {
        this.colorCmd = colorCmd;
        this.colorCode = colorCode;
    }

    public CommandInfo getInfo() {
		return new CommandInfo(
			colorCode,
			"Look up a color code.",
			null,
			null,
                true,
			false,
			true
		);
    }

    public String getHelp() {
        return colorCmd.getHelp();
    }

    public Result run(CommandContext ctx) throws Exception {
        return colorCmd.run(ctx.withArgs(new String[]{colorCode}));
    }

}