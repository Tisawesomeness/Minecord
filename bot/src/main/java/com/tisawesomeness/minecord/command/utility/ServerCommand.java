package com.tisawesomeness.minecord.command.utility;

import br.com.azalim.mcserverping.MCPing;
import br.com.azalim.mcserverping.MCPingOptions;
import br.com.azalim.mcserverping.MCPingResponse;
import br.com.azalim.mcserverping.MCPingResponse.Player;
import br.com.azalim.mcserverping.MCPingUtil;
import com.tisawesomeness.minecord.command.meta.CommandContext;
import com.tisawesomeness.minecord.command.meta.Result;
import com.tisawesomeness.minecord.util.Mth;
import com.tisawesomeness.minecord.util.RequestUtils;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ServerCommand extends AbstractUtilityCommand {

    private final String serverAddressRegex = "([a-z0-9][a-z0-9\\-]*\\.)+[a-z0-9][a-z0-9\\-]*(:[0-9]{1,6})?";
    private final String ipAddressRegex = "((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|0?[1-9]?[0-9])\\.){3}(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|0?[1-9]?[0-9])";
    private final String chatCodeRegex = "\u00A7[a-fA-Fklmnor0-9]"; //§

    private static Set<String> blockedServers = new HashSet<String>();
    private static long timestamp = 0;

    public @NonNull String getId() {
        return "server";
    }

    public void run(String[] args, CommandContext ctx) {

        // Parse arguments
        if (args.length == 0) {
            ctx.showHelp();
            return;
        }
        String arg = args[0];
        boolean ip = true;
        if (!arg.matches(ipAddressRegex)) {
            ip = false;
            if (!arg.matches(serverAddressRegex)) {
                ctx.invalidArgs(ctx.i18n("invalidAddress"));
                return;
            }
        }
        ctx.triggerCooldown();

        // Query Mojang for blocked servers, cached by the hour
        if (System.currentTimeMillis() - 3600000 > timestamp) {
            String request = RequestUtils.getPlain("https://sessionserver.mojang.com/blockedservers");
            if (request != null) {
                blockedServers = new HashSet<String>(Arrays.asList(request.split("\n")));
                timestamp = System.currentTimeMillis();
            }
        }

        String hostname = arg;
        int port = 25565;
        if (arg.contains(":")) {
            hostname = arg.substring(0, arg.indexOf(":"));
            port = Integer.parseInt(arg.substring(arg.indexOf(":") + 1));
        }
        MCPingResponse reply;
        try {
            MCPingOptions options = MCPingOptions.builder().hostname(hostname).port(port).build();
            reply = MCPing.getPing(options);
        } catch (IOException ignore) {
            ctx.possibleErr(ctx.i18n("unreachable"));
            return;
        }
        MCPingResponse.Players players = reply.getPlayers();

        String address = port == 25565 ? hostname : hostname + ":" + port;
        String version = reply.getVersion().getName().replaceAll(chatCodeRegex, "");
        String playerInfo = ctx.i18nf("playerCount", players.getOnline(), players.getMax());
        String motd = MarkdownSanitizer.escape(reply.getDescription().getStrippedText());
        List<Player> sample = players.getSample();

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(ctx.i18nf("title", address))
                .addField(ctx.i18n("version"), version, true)
                .addField(ctx.i18n("players"), playerInfo, true)
                .addField(ctx.i18n("motd"), motd, false);

        if (sample != null && !sample.isEmpty()) {
            String sampleStr = sample.stream()
                    .map(p -> MCPingUtil.stripColors(p.getName()))
                    .collect(Collectors.joining("\n"));
            eb.addField(ctx.i18n("sample"), sampleStr, false);
        }

        StringBuilder sb = eb.getDescriptionBuilder();
        if (isBlocked(arg, ip)) {
            sb.append(ctx.i18n("blocked"));
        }

        if (reply.getFavicon() != null) {
            try {
                String b64String = reply.getFavicon().replace("\n", "").split(",")[1];
                byte[] data = Base64.getDecoder().decode(b64String);
                MessageEmbed me = ctx.brand(eb).setThumbnail("attachment://favicon.png").build();
                ctx.getE().getChannel().sendFile(data, "favicon.png").embed(me).queue();
                ctx.commandResult(Result.SUCCESS);
                return;
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
                if (sb.length() > 0) {
                    sb.append("\n");
                }
                sb.append(ctx.i18n("invalidIcon"));
            }
        }
        ctx.reply(eb);

    }

    // Checks if a server is blocked by Mojang
    private static boolean isBlocked(String server, boolean ip) {
        server = server.toLowerCase();
        if (blockedServers.contains(Mth.sha1(server))) return true;
        if (ip) {
            int i = server.lastIndexOf('.');
            while (i >= 0) {
                if (blockedServers.contains(Mth.sha1(server.substring(0, i + 1) + ".*"))) return true;
                i = server.lastIndexOf('.', i) - 1;
            }
        } else {
            int i = 0;
            while (i != server.lastIndexOf('.') + 1) {
                i = server.indexOf('.', i) + 1;
                if (blockedServers.contains(Mth.sha1("*." + server.substring(i)))) return true;
            }
        }
        return false;
    }

}
