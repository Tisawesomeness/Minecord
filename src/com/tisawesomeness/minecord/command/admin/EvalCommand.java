package com.tisawesomeness.minecord.command.admin;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.tisawesomeness.minecord.command.Command;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class EvalCommand extends Command {

	@Override
	public CommandInfo getInfo() {
		return new CommandInfo(
			"eval",
			"Please don't use this.",
			"<js code>",
			new String[]{},
			0,
			true,
			true,
			false
			);
	}

	@Override
	public Result run(String[] args, MessageReceivedEvent e) throws Exception {
		if (args.length == 0) {
			return new Result(Outcome.WARNING, "Missing code argument.");
		}
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		engine.put("jda", e.getJDA());
		engine.put("e", e);
		String code = "";
		for (String arg : args) {
			code += arg + " ";
		}
		code = code.substring(0, code.length() - 1);
		Object output = engine.eval(code);
		if (output == null) {output = "null";}
		return new Result(Outcome.SUCCESS, output.toString());
		
	}

}
