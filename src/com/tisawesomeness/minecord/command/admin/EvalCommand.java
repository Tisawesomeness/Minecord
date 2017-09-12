package com.tisawesomeness.minecord.command.admin;

import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class EvalCommand extends Command {

	@Override
	public CommandInfo getInfo() {
		return new CommandInfo(
			"eval",
			"Evaluates some js code.",
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
		
		//Parse args
		if (args.length == 0) {
			return new Result(Outcome.WARNING, "Missing code argument.");
		}
		
		//Javascript engine with JDA, event and config variables.
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		engine.put("jda", e.getJDA());
		engine.put("config", Bot.config);
		engine.put("event", e);
		engine.put("guild", e.getGuild());
		engine.put("channel", e.getChannel());
		engine.put("user", e.getAuthor());
		
		//Extract code from message
		String code = "";
		for (String arg : args) {
			code += arg + " ";
		}
		code = code.substring(0, code.length() - 1);
		
		//Evaluate and print code
		Object output = engine.eval(code);
		if (output == null) {
			return new Result(Outcome.ERROR, ":x: Invalid javascript.");
		}
		
		//Prevent @everyone and revealing token
		String outputStr = output.toString()
			.replaceAll("@everyone", "[everyone]")
			.replaceAll("@here", "[here]")
			.replaceAll(Pattern.quote(e.getJDA().getToken()), "[redacted]");
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.addField("Input", "```js\n" + code + "\n```", false);
		eb.addField("Output", "```js\n" + outputStr + "\n```", false);
		eb = MessageUtils.addFooter(eb);
		
		return new Result(Outcome.SUCCESS, eb.build());
		
	}

}
