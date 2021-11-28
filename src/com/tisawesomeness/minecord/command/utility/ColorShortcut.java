package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.Command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ColorShortcut extends Command {

    private final Command colorCmd;
    private final String colorCode;
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

    public Result run(String[] args, MessageReceivedEvent e) throws Exception {
        return colorCmd.run(new String[]{colorCode}, e);
    }

}