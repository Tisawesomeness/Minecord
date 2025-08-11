package com.tisawesomeness.minecord.command.utility;

import br.com.azalim.mcserverping.MCPing;
import br.com.azalim.mcserverping.MCPingOptions;
import br.com.azalim.mcserverping.MCPingResponse;
import br.com.azalim.mcserverping.MCPingResponse.Player;
import br.com.azalim.mcserverping.MCPingUtil;
import com.google.gson.JsonSyntaxException;
import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.SlashCommand;
import com.tisawesomeness.minecord.mc.Favicon;
import com.tisawesomeness.minecord.network.NetUtil;
import com.tisawesomeness.minecord.util.MathUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.RequestUtils;
import com.tisawesomeness.minecord.util.type.Dimensions;
import com.tisawesomeness.minecord.util.type.Either;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.PortUnreachableException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ServerCommand extends SlashCommand {

    // modified from https://mkyong.com/regular-expressions/domain-name-regular-expression-example/
    private static final Pattern IP_PATTERN = Pattern.compile("((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|0?[1-9]?[0-9])\\.){3}(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|0?[1-9]?[0-9])(:[0-9]{1,6})?");
    private static final Pattern SERVER_PATTERN = Pattern.compile("((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)*[A-Za-z]{2,24}(:[0-9]{1,6})?");
    private static final Pattern CHAT_CODE_PATTERN = Pattern.compile("§[a-fA-Fklmnor0-9]"); //§

    private static Set<String> blockedServers = new HashSet<>();
    private static long timestamp = 0;

    public CommandInfo getInfo() {
        return new CommandInfo(
                "server",
                "Fetches the status of a server.",
                "<address>[:<port>]",
                3000,
                false,
                false
        );
    }

    @Override
    public SlashCommandData addCommandSyntax(SlashCommandData builder) {
        return builder.addOptions(new OptionData(OptionType.STRING, "address", "The server address with optional port", true)
                .setMinLength(4));
    }

    @Override
    public String[] getLegacyAliases() {
        return new String[]{"s"};
    }

    @Override
    public String getHelp() {
        return "`{&}server <address>[:<port>]` - Fetches the status of a server.\n" +
                "\n" +
                "Examples:\n" +
                "- `{&}server hypixel.net`\n" +
                "- `{&}server 1.2.3.4`\n" +
                "- `{&}server mc.example.com:25566`\n";
    }

    public Result run(SlashCommandInteractionEvent e) {

        // Parse arguments
        String arg = e.getOption("address").getAsString();
        boolean ip;
        if (!IP_PATTERN.matcher(arg).matches()) {
            ip = false;
            if (!SERVER_PATTERN.matcher(arg).matches()) {
                return new Result(Outcome.WARNING, ":warning: That is not a valid server address.");
            }
        } else {
            ip = true;
        }

        String hostname;
        int port;
        if (arg.contains(":")) {
            hostname = arg.substring(0, arg.indexOf(':'));
            port = Integer.parseInt(arg.substring(arg.indexOf(':') + 1));
            if (port > 65535) {
                return new Result(Outcome.WARNING, ":warning: That is not a valid server address.");
            }
        } else {
            port = 25565;
            hostname = arg;
        }

        e.deferReply().queue();
        CompletableFuture.runAsync(ServerCommand::refreshBlockedServers)
                .thenRun(() -> ping(e, hostname, port, arg, isBlocked(arg, ip)));
        return new Result(Outcome.SUCCESS);
    }

    // Query Mojang for blocked servers, cached by the hour
    private static void refreshBlockedServers() {
        if (System.currentTimeMillis() - 3600000 > timestamp) {
            try {
                String request = RequestUtils.getPlain("https://sessionserver.mojang.com/blockedservers");
                if (request != null) {
                    blockedServers = new HashSet<>(Arrays.asList(request.split("\n")));
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                // not a big deal if we can't get blocked servers
            }
            timestamp = System.currentTimeMillis();
        }
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

    private static void ping(SlashCommandInteractionEvent e, String hostname, int port, String inputHostname, boolean blocked) {
        String m = blocked ? "**BLOCKED BY MOJANG**\n" : "";

        MCPingOptions options = MCPingOptions.builder()
                .hostname(hostname)
                .port(port)
                .timeout(Config.getServerTimeout())
                .readTimeout(Config.getServerReadTimeout())
                .build();
        MCPingResponse reply;
        try {
            reply = MCPing.getPing(options);
            if (reply == null) {
                String msg = m + ":x: The server gave a bad response. It might be just starting up, try again later.";
                e.getHook().sendMessage(msg).queue();
                return;
            }
        } catch (IOException | JsonSyntaxException ex) {
            m += ":x: " + getPingErrorType(ex).getErrorMessage(hostname, port, inputHostname);
            e.getHook().sendMessage(m).queue();
            return;
        }

        String address = port == 25565 ? hostname : hostname + ":" + port;
        String versionName = reply.getVersion().getName();
        String version = CHAT_CODE_PATTERN.matcher(versionName).replaceAll("");
        String playerInfo = reply.getPlayers().getOnline() + "/" + reply.getPlayers().getMax();
        String motd = null;
        if (reply.getDescription() != null && reply.getDescription().getStrippedText() != null) {
            motd = MarkdownSanitizer.escape(reply.getDescription().getStrippedText());
        }
        List<Player> sample = reply.getPlayers().getSample();

        // Build and format message
        if (reply.isEnforcesSecureChat()) {
            if (reply.isPreventsChatReports()) {
                m += ":interrobang: **Enforces and prevents chat reports at the same time? " +
                        "Server is sending contradictory messages.\n";
            } else {
                m += ":shield: **Enforces chat reports**\n";
            }
        }
        if (reply.isPreviewsChat()) {
            m += ":speech_balloon: Enables chat preview\n";
        }
        m += "**Address:** " + address +
                "\n" + "**Version:** " + version +
                "\n" + "**Players:** " + playerInfo;
        if (motd != null) {
            m += "\n" + "**MOTD:** " + motd;
        }
        if (sample != null && !sample.isEmpty()) {
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
            Optional<Favicon> iconOpt = Favicon.parse(reply.getFavicon());
            if (iconOpt.isPresent()) {
                Favicon icon = iconOpt.get();
                Either<Favicon.PngError, Dimensions> errorOrDimensions = icon.validate();
                if (errorOrDimensions.isLeft()) {
                    Favicon.PngError error = errorOrDimensions.getLeft();
                    m += "\n" + getMessage(error);
                } else {
                    Dimensions dimensions = errorOrDimensions.getRight();
                    if (dimensions.getWidth() != Favicon.EXPECTED_SIZE || dimensions.getHeight() != Favicon.EXPECTED_SIZE) {
                        m += String.format("\n:information_source: Icon is %dx%d, only %dx%d icons may display properly.",
                                dimensions.getWidth(), dimensions.getHeight(), Favicon.EXPECTED_SIZE, Favicon.EXPECTED_SIZE);
                    }
                }
                MessageEmbed embed = eb.setDescription(m).setThumbnail("attachment://favicon.png").build();
                e.getHook().sendFiles(FileUpload.fromData(icon.getData(), "favicon.png")).setEmbeds(embed).queue();
                return;
            } else {
                eb.setDescription(m + "\n:x: Server returned an invalid icon.");
            }
        }
        e.getHook().sendMessageEmbeds(eb.build()).queue();
    }

    private static PingError getPingErrorType(Exception ex) {
        if (ex instanceof UnknownHostException) {
            return PingError.UNKNOWN_HOST;
        }
        if (ex instanceof PortUnreachableException) {
            return PingError.PORT_UNREACHABLE;
        }
        String msg = ex.getMessage();
        if (ex instanceof SocketTimeoutException) {
            if (msg.contains("Read")) {
                return PingError.READ_TIMEOUT;
            } else {
                return PingError.TIMEOUT;
            }
        }
        if (msg.equals("Server prematurely ended stream.")) {
            return PingError.END_OF_STREAM;
        }
        if (ex instanceof JsonSyntaxException ||
                msg.equals("Server returned invalid packet.") ||
                msg.equals("Server returned unexpected value.")) {
            return PingError.INVALID_DATA;
        }
        return PingError.GENERIC;
    }

    @AllArgsConstructor
    private enum PingError {
        GENERIC, UNKNOWN_HOST, PORT_UNREACHABLE, TIMEOUT, READ_TIMEOUT, END_OF_STREAM, INVALID_DATA;

        public String getErrorMessage(String host, int port, String input) {
            String hint = getHint(host);
            switch (this) {
                case GENERIC:
                    return String.format("An error occurred trying to ping `%s`.\n%s", input, hint);
                case UNKNOWN_HOST:
                    return String.format("The server `%s` is down or unreachable.\n%s", input, hint);
                case PORT_UNREACHABLE:
                    return String.format("The server `%s` cannot be reached on port `%d`.\n%s", host, port, hint);
                case TIMEOUT:
                    return String.format("The connection timed out while trying to ping `%s`.\n%s", input, hint);
                case READ_TIMEOUT:
                    return String.format("The server `%s` took too long to respond.", input);
                case END_OF_STREAM:
                    return String.format("The server `%s` stopped responding.", input);
                case INVALID_DATA:
                    return String.format("The server `%s` returned invalid data.", input);
                default:
                    throw new AssertionError("unreachable");
            }
        }
        private static String getHint(String host) {
            if (Config.getWarnOnLocalPing() && isLocal(host)) {
                return "You are trying to connect to a local address. Try using the server's external IP or hostname instead.";
            } else if (!host.equals(host.toLowerCase())) {
                return "Try using lowercase letters.";
            } else {
                return "Did you spell it correctly?";
            }
        }
        private static boolean isLocal(String host) {
            return !host.contains(".") || NetUtil.getAddress(host).map(Inet4Address::isSiteLocalAddress).orElse(false);
        }
    }

    private static String getMessage(@NonNull Favicon.PngError error) {
        switch (error) {
            case TOO_SHORT:
                return "Icon data is too short to be a valid PNG image.";
            case BAD_SIGNATURE:
            case BAD_IHDR_LENGTH:
            case BAD_IHDR_TYPE:
            case NEGATIVE_WIDTH:
            case NEGATIVE_HEIGHT:
                return "Icon is not a valid PNG image.";
            default:
                throw new AssertionError("unreachable");
        }
    }

}
