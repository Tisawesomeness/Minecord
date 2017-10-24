package com.tisawesomeness.minecord.command;

import java.util.TreeMap;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.Command.CommandInfo;
import com.tisawesomeness.minecord.command.admin.*;
import com.tisawesomeness.minecord.command.general.*;
import com.tisawesomeness.minecord.command.player.*;
import com.tisawesomeness.minecord.command.utility.*;

public class Registry {
	
	public static Command[] commands = {
		new Text("**General Commands:**"),
		new HelpCommand(),
		new InfoCommand(),
		new InviteCommand(),
		new StatusCommand(),
		new SalesCommand(),
		new PurgeCommand(),
		new Text("\n**Utility Commands:**"),
		new CodesCommand(),
		new ServerCommand(),
		new ItemCommand(),
		new RecipeCommand(),
		new Text("\n**Player Commands:**"),
		new UuidCommand(),
		new HistoryCommand(),
		new AvatarCommand(),
		new HeadCommand(),
		new BodyCommand(),
		new SkinCommand(),
		new CapeCommand(),
		new Text(true, "\n**Admin Commands:**"),
		new SayCommand(),
		new MsgCommand(),
		new NameCommand(),
		new UsageCommand(),
		new ReloadCommand(),
		new ShutdownCommand(),
		new EvalCommand(),
		new TestCommand(),
		new Text(
			"\n" + "**Arguments:**" +
			"\n" + "`<>` is required, `[]` is optional, and `?` is true/false." +
			"\n" + "Simply type a command like `" + Config.getPrefix() +
				"server` without any arguments to get more details."
		)
	};
	public static TreeMap<String, Command> commandMap;
	public static boolean enabled = false;
	
	public static void init() {

		//Map aliases to commands
		commandMap = new TreeMap<String, Command>(String.CASE_INSENSITIVE_ORDER);
		for (int i = 0; i < commands.length; i++) {
			
			//Add command to map
			Command c = commands[i];
			if (c == null) {continue;}
			CommandInfo ci = c.getInfo();
			commandMap.put(ci.name, c);
			
			//Add aliases
			for (String alias : ci.aliases) {
				commandMap.put(alias, c);
			}
			
		}
		
	}

}
