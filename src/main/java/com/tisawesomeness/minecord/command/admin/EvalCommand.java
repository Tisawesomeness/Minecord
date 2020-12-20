package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.Result;
import com.tisawesomeness.minecord.config.serial.BotListConfig;
import com.tisawesomeness.minecord.config.serial.Config;
import com.tisawesomeness.minecord.util.EvalUtils;
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
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EvalCommand extends AbstractAdminCommand {

    private static final int CODEBLOCK_LENGTH = 12;

    private final Map<String, Object> storage = new ConcurrentHashMap<>();
    private final EvalUtils h = new EvalUtils(); // ScriptEngine needs a reference to an object

    public @NonNull String getId() {
        return "eval";
    }

    public Result run(String[] args, CommandContext ctx) {
        MessageReceivedEvent e = ctx.getE();

        // Parse args
        if (args.length == 0) {
            return ctx.showHelp();
        }

        // Javascript engine with JDA, event and config variables.
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("jshell");
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
        engine.put("h", h);

        // Extract code from message
        String code = ctx.joinArgs();

        // Evaluate code, and catch errors
        Object output = null;
        String exMsg = null;
        try {
            output = engine.eval(code);
        } catch (ScriptException ex) {
            ex.printStackTrace();
            exMsg = ex.getMessage() == null ? "Null Script Exception" : clean(ex.getMessage(), ctx.getConfig());
        }
        if (output == null) {
            output = "null";
        }

        // Build embed
        EmbedBuilder eb = new EmbedBuilder();
        String in = clean(code, ctx.getConfig());
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
            return Result.WARNING;
        }

        // Check for length
        String out = clean(output.toString(), ctx.getConfig());
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

        return ctx.replyRaw(eb);

    }

    /**
     * Removes all blacklisted strings and everyone/here mentions from the input as a safety measure.
     * <b>NOT GUARENTEED TO WORK IN ALL CASES. NEVER REQUEST THE BOT TOKEN OR PRINT ALL JDA OR CONFIG VALUES.</b>
     * @param s The input string
     * @return A cleaned string with blacklisted strings replaced with [redacted]
     */
    private static String clean(String s, Config config) {
        BotListConfig blc = config.getBotListConfig();
        String[] blacklist = {
                config.getToken(),
                blc.getPwToken(),
                blc.getOrgToken(),
                blc.getWebhookUrl(),
                blc.getWebhookAuth()
        };
        for (String nono : blacklist) {
            if (nono != null) {
                s = s.replace(nono, "[redacted]");
            }
        }
        return s.replace("@everyone", "[everyone]").replace("@here", "[here]");
    }

}
