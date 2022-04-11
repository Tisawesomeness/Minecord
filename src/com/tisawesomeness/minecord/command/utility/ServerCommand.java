package com.tisawesomeness.minecord.command.utility;

import br.com.azalim.mcserverping.MCPing;
import br.com.azalim.mcserverping.MCPingOptions;
import br.com.azalim.mcserverping.MCPingResponse;
import br.com.azalim.mcserverping.MCPingResponse.Player;
import br.com.azalim.mcserverping.MCPingUtil;
import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.MathUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.RequestUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ServerCommand extends Command {

    // modified from https://mkyong.com/regular-expressions/domain-name-regular-expression-example/
    private static final Pattern IP_PATTERN = Pattern.compile("((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|0?[1-9]?[0-9])\\.){3}(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|0?[1-9]?[0-9])(:[0-9]{1,6})?");
    private static final Pattern SERVER_PATTERN = Pattern.compile("((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,24}(:[0-9]{1,6})?");
    private static final Pattern CHAT_CODE_PATTERN = Pattern.compile("\u00A7[a-fA-Fklmnor0-9]"); //§

    private static Set<String> blockedServers = new HashSet<>();
    private static long timestamp = 0;

    public CommandInfo getInfo() {
        return new CommandInfo(
                "server",
                "Fetches the stats of a server.",
                "<address>[:<port>]",
                new String[]{"s"},
                2000,
                false,
                false,
                true
        );
    }

    public String getHelp() {
        return "`{&}server <address>[:<port>]` - Fetches the status of a server.\n" +
                "\n" +
                "Examples:\n" +
                "- `{&}server hypixel.net`\n" +
                "- `{&}server 1.2.3.4`\n" +
                "- `{&}server mc.example.com:25566`\n";
    }

    public Result run(String[] args, MessageReceivedEvent e) {

        // Parse arguments
        if (args.length == 0) {
            String m = ":warning: You must specify a server." +
                    "\n" + MessageUtils.getPrefix(e) + "server <address>[:port]";
            return new Result(Outcome.WARNING, m);
        }
        String arg = args[0];
        boolean ip = true;
        if (!IP_PATTERN.matcher(arg).matches()) {
            ip = false;
            if (!SERVER_PATTERN.matcher(arg).matches()) {
                return new Result(Outcome.WARNING, ":warning: That is not a valid server address.");
            }
        }

        String hostname = arg;
        int port = 25565;
        if (arg.contains(":")) {
            hostname = arg.substring(0, arg.indexOf(':'));
            port = Integer.parseInt(arg.substring(arg.indexOf(':') + 1));
            if (port > 65535) {
                return new Result(Outcome.WARNING, ":warning: That is not a valid server address.");
            }
        }

        // Query Mojang for blocked servers, cached by the hour
        if (System.currentTimeMillis() - 3600000 > timestamp) {
            String request;
            try {
                request = RequestUtils.getPlain("https://sessionserver.mojang.com/blockedservers");
                if (request != null) {
                    blockedServers = new HashSet<>(Arrays.asList(request.split("\n")));
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                // not getting blocked servers okay
            }
            timestamp = System.currentTimeMillis();
        }
        boolean blocked = isBlocked(arg, ip);
        String m = blocked ? "**BLOCKED BY MOJANG**\n" : "";

        MCPingResponse reply;
        try {
            MCPingOptions options = MCPingOptions.builder().hostname(hostname).port(port).build();
            reply = MCPing.getPing(options);
            if (reply == null) {
                return new Result(Outcome.ERROR, m + ":x: The server gave a bad response. It might be just starting up, try again later.");
            }
        } catch (IOException ignore) {
            if (hostname.equals(hostname.toLowerCase())) {
                m += ":warning: The server is down or unreachable.\nDid you spell it correctly?";
            } else {
                m += ":warning: The server is down or unreachable.\nTry using lowercase letters.";
            }
            return new Result(Outcome.WARNING, m);
        }

        String address = port == 25565 ? hostname : hostname + ":" + port;
        String versionName = reply.getVersion().getName();
        String version = CHAT_CODE_PATTERN.matcher(versionName).replaceAll("");
        String playerInfo = reply.getPlayers().getOnline() + "/" + reply.getPlayers().getMax();
        String motd = null;
        if (reply.getDescription() != null) {
            motd = MarkdownSanitizer.escape(reply.getDescription().getStrippedText());
        }
        List<Player> sample = reply.getPlayers().getSample();

        // Build and format message
        m += "**Address:** " + address +
                "\n" + "**Version:** " + version +
                "\n" + "**Players:** " + playerInfo;
        if (motd != null) {
            m += "\n" + "**MOTD:** " + motd;
        }
        if (sample != null && sample.size() > 0) {
            String sampleStr = sample.stream()
                    .map(p -> MCPingUtil.stripColors(p.getName()))
                    .collect(Collectors.joining("\n"));
            m += "\n\n" + sampleStr;
        }

        // Upload favicon as byte array
        EmbedBuilder eb = MessageUtils.addFooter(new EmbedBuilder().setTitle("Server Status").setColor(Bot.color));
        if (reply.getFavicon() == null) {
            eb.setDescription(m);
        } else {
            String favicon = reply.getFavicon().replace("\n", "");
            if (favicon.contains(",")) {
                if (!e.isFromGuild() || e.getGuild().getSelfMember().hasPermission(e.getTextChannel(), Permission.MESSAGE_ATTACH_FILES)) {
                    try {
                        byte[] data = Base64.getDecoder().decode(favicon.split(",")[1]);
                        e.getChannel().sendFile(data, "favicon.png").setEmbeds(eb.setDescription(m).setThumbnail("attachment://favicon.png").build()).queue();
                        return new Result(Outcome.SUCCESS);
                    } catch (IllegalArgumentException ex) {
                        ex.printStackTrace();
                        eb.setDescription(m + "\n:x: Server returned an invalid icon.");
                    }
                } else {
                    eb.setDescription(m + "\n:warning: Give Minecord permission to attach files to see server icons.");
                }
            }
        }
        return new Result(Outcome.SUCCESS, eb.build());
    }

    // Checks if a server is blocked by Mojang
    private static boolean isBlocked(String server, boolean ip) {
        server = server.toLowerCase();
        if (blockedServers.contains(MathUtils.sha1(server))) return true;
        if (ip) {
            int i = server.lastIndexOf('.');
            while (i >= 0) {
                if (blockedServers.contains(MathUtils.sha1(server.substring(0, i + 1) + ".*"))) return true;
                i = server.lastIndexOf('.', i) - 1;
            }
        } else {
            int i = 0;
            while (i != server.lastIndexOf('.') + 1) {
                i = server.indexOf('.', i) + 1;
                if (blockedServers.contains(MathUtils.sha1("*." + server.substring(i)))) return true;
            }
        }
        return false;
    }

}
