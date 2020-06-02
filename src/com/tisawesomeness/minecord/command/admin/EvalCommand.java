package com.tisawesomeness.minecord.command.admin;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

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

	String docsLink = "https://docs.oracle.com/javase/8/docs/technotes/guides/scripting/prog_guide/javascript.html#CIHFFHED";
	public String getHelp() {
		return "Evaluates some js code using the Rhino engine.\n" +
			"Variables: `jda`, `sm`, `config`, `db`, `event`, `user`, `channel`, `guild`\n" +
			"Use `help(obj)` to list the object's fields and methods.\n" +
			"See [the docs](" + docsLink + ") for information on accessing Java from scripts.\n" +
			"Sensitive info such as the bot token are cleaned from the input and output. " +
			"In case this fails, __**never request the bot token and never print all values of the jda or config.**__\n";
	}

	@Override
	public Result run(String[] args, MessageReceivedEvent e) throws Exception {
		
		// Parse args
		if (args.length == 0) {
			return new Result(Outcome.WARNING, "Missing code argument.");
		}
		
		// Javascript engine with JDA, event and config variables.
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		engine.put("jda", e.getJDA());
		engine.put("sm", e.getJDA().getShardManager());
		engine.put("config", Bot.config);
		engine.put("db", new Database());
		engine.put("event", e);
		engine.put("user", e.getAuthor());
		engine.put("channel", e.getChannel());
		if (e.isFromGuild()) engine.put("guild", e.getGuild());
		Function<Object, String> help = EvalCommand::help;
		engine.put("help", help);
		
		// Extract code from message
		String code = String.join(" ", args);
		
		// Evaluate and print code
		Object output = engine.eval(code);
		if (output == null) {
			return new Result(Outcome.ERROR, ":x: Recieved null as output.");
		}
		
		// Build embed
		EmbedBuilder eb = new EmbedBuilder();
		eb.addField("Input", "```js\n" + clean(code) + "\n```", false);
		eb.setTimestamp(OffsetDateTime.now());
		User u = e.getAuthor();
		eb.setFooter(String.format("Sent by %s (%s)", u.getAsTag(), u.getId()), u.getAvatarUrl());

		// Check for length
		String out = clean(output.toString());
		if (out.length() > 1024 - 10) {
			// Send up to 10 2000-char messages
			eb.addField("Output", "Too long!", false);
			if (out.length() > (2000 - 10) * 10 - 3) {
				out = out.substring(0, (2000 - 10) * 10 - 3);
			}
			for (String msg : MessageUtils.splitLinesByLength(out, 2000 - 10)) {
				e.getChannel().sendMessage(String.format("```js\n%s\n```", msg)).queue();
			}
		} else {
			eb.addField("Output", String.format("```js\n%s\n```", out), false);
		}
		
		return new Result(Outcome.SUCCESS, eb.build());
		
	}

	/**
	 * Removes all blacklisted strings and everyone/here mentions from the input.
	 * <b>NOT GUARENTEED TO WORK IN ALL CASES. NEVER REQUEST THE BOT TOKEN OR PRINT ALL JDA OR CONFIG VALUES.</b>
	 * @param s The input string
	 * @return A cleaned string with blacklisted strings replaced with [redacted]
	 */
	private String clean(String s) {
		String[] blacklist = new String[]{
			Config.getClientToken(),
			Config.getPwToken(),
			Config.getOrgToken(),
			Config.getWebhookURL(),
			Config.getWebhookAuth(),
			Config.getHost(),
			Config.getPass()
		};
		for (String nono : blacklist) {
			s = s.replace(nono, "[redacted]");
		}
		return MarkdownSanitizer.escape(s.replace("@everyone", "[everyone]").replace("@here", "[here]"));
	}

	/**
	 * Generates a list of fields and methods for an object
	 */
	private static String help(Object o) {
		Class<?> clazz = o.getClass();
		String fields = Arrays.asList(clazz.getFields()).stream()
			.sorted((f1, f2) -> f1.getName().compareTo(f2.getName())) // Sort by field name
			.map(f -> f.getType().getSimpleName() + " : " + f.getName())
			.collect(Collectors.joining("\n"));
		String methods = Arrays.asList(clazz.getMethods()).stream()
			.sorted((m1, m2) -> m1.getName().compareTo(m2.getName())) // Sort by method name
			.map(EvalCommand::getSignature)
			.collect(Collectors.joining("\n"));
		return String.format("%s\n\nFields:\n%s\n\nMethods:\n%s", clazz.getName(), fields, methods);
	}
	/**
	 * Generates a shortened signature for a method.
	 * Exceptions, non-static modifiers, generic parameters, and annotations are excluded for brevity.
	 * @param m The method object to generate a signature for
	 * @return The signature as a string
	 */
	private static String getSignature(Method m) {
		String params = Arrays.asList(m.getParameters()).stream()
			.map(p -> cleanType(p.getType()) + " " + p.getName())
			.collect(Collectors.joining(", ")); // Comma-separated args like in "add(int x, int y)"
		String staticc = Modifier.isStatic(m.getModifiers()) ? "static " : ""; // Only static is included for brevity
		return String.format("%s%s %s(%s)", staticc, cleanType(m.getGenericReturnType()), m.getName(), params);
	}
	/**
	 * Generates a clean string for a type, transforming "java.lang.String" into "String".
	 * Takes generics into account and parses them in the "List<String>" format.
	 * @param t The type reflection object
	 * @return The type name as a string
	 */
	private static String cleanType(Type t) {
		String typeName = t.getTypeName();
		if (typeName.contains("<")) {
			String[] split = typeName.split("<");
			String type = split[0].substring(split[0].lastIndexOf(".") + 1); // Thanks -1 on failure for making this super clean
			String generic = split[1].substring(split[1].lastIndexOf(".") + 1, split[1].length() - 1);
			return type + "<" + generic + ">";
		}
		return typeName.substring(typeName.lastIndexOf(".") + 1);
	}

}
