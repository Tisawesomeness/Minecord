package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.LegacyCommand;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.util.DiscordUtils;
import com.tisawesomeness.minecord.util.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EvalCommand extends LegacyCommand {

    @Override
    public CommandInfo getInfo() {
        return new CommandInfo(
                "eval",
                "Evaluates some js code.",
                "<js code>",
                0,
                true,
                true
        );
    }

    private static final String docsLink = "https://docs.oracle.com/javase/8/docs/technotes/guides/scripting/prog_guide/javascript.html#CIHFFHED";
    public String getHelp() {
        return "Evaluates some js code using the Rhino engine.\n" +
                "Variables: `jda`, `sm`, `config`, `db`, `event`, `user`, `channel`\n" +
                "Not available in DM: `member`, `guild`\n" +
                "Use `help(obj)` to list the object's fields and methods.\n" +
                "\n" +
                "See [the docs](" + docsLink + ") for information on accessing Java from scripts.\n" +
                "Sensitive info such as the bot token are cleaned from the input and output. " +
                "In case this fails, __**never request the bot token and never print all values of the jda or config.**__\n";
    }

    @Override
    public Result run(String[] args, MessageReceivedEvent e) {

        if (!Config.getEvil()) {
            return new Result(Outcome.SUCCESS, "Eval is evil.");
        }

        // Parse args
        if (args.length == 0) {
            return new Result(Outcome.WARNING, "Missing code argument.");
        }

        // Javascript engine with JDA, event and config variables.
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("JavaScript");
        engine.put("jda", e.getJDA());
        engine.put("sm", e.getJDA().getShardManager());
        engine.put("config", new Config());
        engine.put("db", new Database());
        engine.put("event", e);
        engine.put("user", e.getAuthor());
        engine.put("channel", e.getChannel());
        if (e.isFromGuild()) {
            engine.put("guild", e.getGuild());
            engine.put("member", e.getMember());
        }
        Function<Object, String> help = EvalCommand::help;
        engine.put("help", help);

        // Extract code from message
        String code = String.join(" ", args);

        // Evaluate code, and catch errors
        Object output = null;
        String exMsg = null;
        try {
            output = engine.eval(code);
        } catch (ScriptException ex) {
            exMsg = ex.getMessage() == null ? "Null Script Exception" : clean(ex.getMessage());
        }
        if (output == null) {
            output = "null";
        }

        // Build embed
        EmbedBuilder eb = new EmbedBuilder();
        String in = clean(code);
        if (in.length() > MessageEmbed.DESCRIPTION_MAX_LENGTH - 10) {
            eb.addField("Input", "Input too long!", false);
        } else if (in.length() > MessageEmbed.VALUE_MAX_LENGTH - 10) {
            eb.setDescription(MarkdownUtil.codeblock("js", in));
        } else {
            eb.addField("Input", MarkdownUtil.codeblock("js", in), false);
        }
        eb.setTimestamp(OffsetDateTime.now());
        User u = e.getAuthor();
        eb.setFooter("Sent by " + DiscordUtils.tagAndId(u), u.getAvatarUrl());

        // Log embed with just input
        Bot.logger.log(MessageCreateData.fromEmbeds(eb.build()));
        System.out.println(DiscordUtils.tagAndId(u) + " ran eval:\n" + in);

        // Exception check
        if (exMsg != null) {
            eb.addField("Output", MarkdownUtil.monospace(exMsg), false);
            return new Result(Outcome.WARNING, eb.build());
        }

        // Check for length
        String out = clean(output.toString());
        if (out.length() > MessageEmbed.VALUE_MAX_LENGTH - 10) {
            // Send up to 10 messages within limit
            ArrayList<String> lines = StringUtils.splitLinesByLength(out, MessageEmbed.DESCRIPTION_MAX_LENGTH - 10);
            int i = 0;
            while (i < 10 && i < lines.size()) {
                e.getChannel().sendMessage(MarkdownUtil.codeblock("js", lines.get(i))).queue();
                i++;
            }
            // Let user know if the code went over 10 messages
            if (i == 10) {
                eb.addField("Output", "Too long! Output truncated.", false);
            } else {
                eb.addField("Output", "See above.", false);
            }
        } else {
            eb.addField("Output", MarkdownUtil.codeblock("js", out), false);
        }

        return new Result(Outcome.SUCCESS, eb.build());

    }

    /**
     * Removes all blacklisted strings and everyone/here mentions from the input.
     * <b>NOT GUARANTEED TO WORK IN ALL CASES. NEVER REQUEST THE BOT TOKEN OR PRINT ALL JDA OR CONFIG VALUES.</b>
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
        return s.replace("@everyone", "[everyone]").replace("@here", "[here]");
    }

    /**
     * Generates a list of fields and methods for an object
     */
    private static String help(Object o) {
        Class<?> clazz = o.getClass();
        String fields = "NONE";
        if (clazz.getFields().length > 0) {
            fields = Arrays.stream(clazz.getFields())
                    .sorted(Comparator.comparing(Field::getName)) // Sort by field name
                    .map(f -> f.getType().getSimpleName() + " : " + f.getName())
                    .collect(Collectors.joining("\n"));
        }
        String methods = "NONE";
        if (clazz.getMethods().length > 0) {
            methods = Arrays.stream(clazz.getMethods())
                    .sorted(Comparator.comparing(Method::getName)) // Sort by method name
                    .map(EvalCommand::getSignature)
                    .collect(Collectors.joining("\n"));
        }
        return String.format("%s\n\nFields:\n%s\n\nMethods:\n%s", clazz.getName(), fields, methods);
    }
    /**
     * Generates a shortened signature for a method.
     * Exceptions, non-static modifiers, generic parameters, and annotations are excluded for brevity.
     * @param m The method object to generate a signature for
     * @return The signature as a string
     */
    private static String getSignature(Method m) {
        String params = Arrays.stream(m.getParameters())
                .map(p -> cleanType(p.getType()))
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
            String type = split[0].substring(split[0].lastIndexOf('.') + 1); // Thanks -1 on failure for making this super clean
            String generic = split[1].substring(split[1].lastIndexOf('.') + 1, split[1].length() - 1);
            return type + "<" + generic + ">";
        }
        return typeName.substring(typeName.lastIndexOf('.') + 1);
    }

}
