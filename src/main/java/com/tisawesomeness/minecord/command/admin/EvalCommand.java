package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.Secrets;
import com.tisawesomeness.minecord.command.meta.CommandContext;
import com.tisawesomeness.minecord.command.meta.Result;
import com.tisawesomeness.minecord.util.MessageUtils;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EvalCommand extends AbstractAdminCommand {

    private static final int CODEBLOCK_LENGTH = 12;

    private final Map<String, Object> storage = new ConcurrentHashMap<>();
    private final ScriptEngineManager factory = new ScriptEngineManager();

    public @NonNull String getId() {
        return "eval";
    }

    public void run(String[] args, CommandContext ctx) {
        MessageReceivedEvent e = ctx.getE();
        Secrets secrets = ctx.getBot().getSecrets();

        // Parse args
        if (args.length == 0 || (args.length == 1 && "help".equalsIgnoreCase(args[0]))) {
            ctx.showHelp();
            return;
        }

        // Javascript engine with JDA, event and config variables.
        ScriptEngine engine = factory.getEngineByExtension("js");
        engine.put("ctx", ctx);
        engine.put("bot", ctx.getBot());
        engine.put("jda", e.getJDA());
        engine.put("sm", e.getJDA().getShardManager());
        engine.put("config", ctx.getConfig());
        engine.put("lang", ctx.getLang());
        engine.put("dbCache", ctx.getBot().getDatabaseCache());
        engine.put("event", e);
        engine.put("user", e.getAuthor());
        engine.put("channel", e.getChannel());
        if (e.isFromGuild()) {
            engine.put("guild", e.getGuild());
            engine.put("member", e.getMember());
        }
        engine.put("storage", storage);
        Function<Object, String> help = EvalCommand::help;
        engine.put("help", help);

        // Extract code from message
        String code = ctx.joinArgs();

        // Evaluate code, and catch errors
        Object output = null;
        String exMsg = null;
        try {
            output = engine.eval(code);
        } catch (ScriptException ex) {
            ex.printStackTrace();
            exMsg = ex.getMessage() == null ? "Null Script Exception" : clean(ex.getMessage(), secrets);
        }
        if (output == null) {
            output = "null";
        }

        // Build embed
        EmbedBuilder eb = new EmbedBuilder();
        String in = clean(code, secrets);
        if (in.length() > MessageEmbed.TEXT_MAX_LENGTH - CODEBLOCK_LENGTH) {
            eb.addField("Input", "Input too long!", false);
        } else if (in.length() > MessageEmbed.VALUE_MAX_LENGTH - CODEBLOCK_LENGTH) {
            eb.setDescription(MarkdownUtil.codeblock("java", in));
        } else {
            eb.addField("Input", MarkdownUtil.codeblock("java", in), false);
        }
        eb.setTimestamp(OffsetDateTime.now());
        User u = e.getAuthor();
        eb.setFooter(String.format("Sent by %s (%s)", u.getAsTag(), u.getId()), u.getAvatarUrl());

        // Exception check
        if (exMsg != null) {
            eb.addField("Output", MarkdownUtil.monospace(exMsg), false);
            ctx.replyRaw(eb);
            ctx.commandResult(Result.WARNING);
            return;
        }

        // Check for length
        String out = clean(output.toString(), secrets);
        if (out.length() > MessageEmbed.VALUE_MAX_LENGTH - CODEBLOCK_LENGTH) {
            // Send up to 10 2000-char messages
            List<String> lines = MessageUtils.splitLinesByLength(out, Message.MAX_CONTENT_LENGTH - CODEBLOCK_LENGTH);
            int i = 0;
            while (i < CODEBLOCK_LENGTH && i < lines.size()) {
                e.getChannel().sendMessage(MarkdownUtil.codeblock("java", lines.get(i))).queue();
                i++;
            }
            // Let user know if the code went over 10 messages
            String outputMsg = i == CODEBLOCK_LENGTH ? "Too long! Output truncated." : "See above.";
            eb.addField("Output", outputMsg, false);
        } else {
            eb.addField("Output", MarkdownUtil.codeblock("java", out), false);
        }

        ctx.replyRaw(eb);

    }

    /**
     * Removes all blacklisted strings and everyone/here mentions from the input as a safety measure.
     * <b>NOT GUARENTEED TO WORK IN ALL CASES. NEVER REQUEST THE BOT TOKEN OR PRINT ALL JDA OR CONFIG VALUES.</b>
     * @param s The input string
     * @return A cleaned string with blacklisted strings replaced with [redacted]
     */
    private static String clean(String s, Secrets secrets) {
        return secrets.clean(s)
                .replace("@everyone", "[everyone]")
                .replace("@here", "[here]");
    }

    private static String help(Object o) {
        Class<?> clazz = o.getClass();
        String fields = "NONE";
        if (clazz.getFields().length > 0) {
            fields = Arrays.stream(clazz.getFields())
                    .sorted(Comparator.comparing(Field::getName))
                    .map(EvalCommand::getDeclaration)
                    .collect(Collectors.joining("\n"));
        }
        String methods = "NONE";
        if (clazz.getMethods().length > 0) {
            methods = Arrays.stream(clazz.getMethods())
                    .filter(m -> o.getClass().equals(Object.class) || !m.getDeclaringClass().equals(Object.class))
                    .sorted(Comparator.comparing(Method::getName))
                    .map(EvalCommand::getSignature)
                    .collect(Collectors.joining("\n"));
        }
        return String.format("%s\n\nFields:\n%s\n\nMethods:\n%s", clazz.getName(), fields, methods);
    }

    private static String getDeclaration(Field f) {
        String finall = Modifier.isFinal(f.getModifiers()) ? "final " : "";
        return finall + cleanType(f.getGenericType()) + " " + f.getName();
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
     * Takes generics into account and parses them into the diamond operator format.
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
