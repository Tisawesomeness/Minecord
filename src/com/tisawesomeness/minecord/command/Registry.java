package com.tisawesomeness.minecord.command;

import java.util.TreeMap;

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
		new PingCommand(),
		new InviteCommand(),
		new PurgeCommand(),
		new PrefixCommand(),
		new GuildCommand(),
		new UserCommand(),
		new Text("\n**Utility Commands:**"),
		new StatusCommand(),
		new SalesCommand(),
		new CodesCommand(),
		new ServerCommand(),
		new ItemCommand(),
		new RecipeCommand(),
		new IngredientCommand(),
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
		new PromoteCommand(),
		new DemoteCommand(),
		new BanCommand(),
		new ReloadCommand(),
		new ShutdownCommand(),
		new EvalCommand(),
		new TestCommand()
	};
	public static TreeMap<String, Command> commandMap;
	
	public static void init() {
		commandMap = new TreeMap<String, Command>(String.CASE_INSENSITIVE_ORDER); //Map aliases to commands
		for (int i = 0; i < commands.length; i++) {
			//Add command to map
			Command c = commands[i];
			if (c != null) {
				CommandInfo ci = c.getInfo();
				commandMap.put(ci.name, c);
				for (String alias : ci.aliases) commandMap.put(alias, c); //Add aliases
			}
		}
	}

}
