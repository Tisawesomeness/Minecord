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
			1000,
			true,
			false,
			true
		);
    }

    public String getHelp() {
        return colorCmd.getHelp();
    }

    public Result run(CommandContext txt) throws Exception {
        return colorCmd.run(txt.withArgs(new String[]{colorCode}));
    }

}