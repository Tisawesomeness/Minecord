package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.ColorUtils;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CodesCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"codes",
			"Get info on chat codes.",
			null,
			new String[]{
				"code",
				"chat"},
			1000,
			false,
			false,
			true
		);
	}
	
	private static final String img = "https://minecraft.gamepedia.com/media/minecraft.gamepedia.com/7/7e/Minecraft_Formatting.gif";
	
	public Result run(String[] args, MessageReceivedEvent e) {
		
		String desc = String.format("Symbol copy-paste: `\u00A7`, `\\u00A7`\nUse `%scolor` to get info on a color.", MessageUtils.getPrefix(e));
		EmbedBuilder eb = new EmbedBuilder()
			.setTitle("Minecraft Chat Codes")
			.setColor(ColorUtils.randomColor())
			.setDescription(desc)
			.setImage(img);
		return new Result(Outcome.SUCCESS, MessageUtils.addFooter(eb).build());
		
	}

}
