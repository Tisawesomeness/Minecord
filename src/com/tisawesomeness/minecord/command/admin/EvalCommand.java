package com.tisawesomeness.minecord.command.admin;

import java.time.OffsetDateTime;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.Command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;
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
			return new Result(Outcome.ERROR, ":x: Recieved null as output.");
		}
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.addField("Input", "```js\n" + clean(code) + "\n```", false);
		eb.addField("Output", "```js\n" + clean(output.toString()) + "\n```", false);
		eb.setTimestamp(OffsetDateTime.now());
		User u = e.getAuthor();
		eb.setFooter("Sent by " + u.getName() + "#" + u.getDiscriminator() + " (" + u.getId() + ")", u.getAvatarUrl());
		
		return new Result(Outcome.SUCCESS, eb.build());
		
	}
	
	private String clean(String s) {
		return s.replaceAll("\\\\", "\\\\")
				.replaceAll("`", "\\\\`")
				.replaceAll("@everyone", "[everyone]")
				.replaceAll("@here", "[here]")
				.replaceAll(Pattern.quote(Bot.shards.get(0).getToken()), "[redacted]")
				.replaceAll(Pattern.quote(Config.getPwToken()), "[redacted]")
				.replaceAll(Pattern.quote(Config.getNetToken()), "[redacted]")
				.replaceAll(Pattern.quote(Config.getOrgToken()), "[redacted]")
				.replaceAll(Pattern.quote(Config.getHost()), "[redacted]")
				.replaceAll(Pattern.quote(Config.getPass()), "[redacted]");
	}

}
