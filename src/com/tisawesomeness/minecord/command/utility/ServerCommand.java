package com.tisawesomeness.minecord.command.utility;

import br.com.azalim.mcserverping.MCPing;
import br.com.azalim.mcserverping.MCPingOptions;
import br.com.azalim.mcserverping.MCPingResponse;
import br.com.azalim.mcserverping.MCPingResponse.Player;
import br.com.azalim.mcserverping.MCPingUtil;
import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.SlashCommand;
import com.tisawesomeness.minecord.mc.Favicon;
import com.tisawesomeness.minecord.util.MathUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.RequestUtils;
import com.tisawesomeness.minecord.util.type.Dimensions;
import com.tisawesomeness.minecord.util.type.Either;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ServerCommand extends SlashCommand {

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

        e.deferReply().queue();

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
                String msg = m + ":x: The server gave a bad response. It might be just starting up, try again later.";
                e.getHook().sendMessage(msg).setEphemeral(true).queue();
                return new Result(Outcome.ERROR);
            }
        } catch (IOException ignore) {
            m += ":warning: The server `" + arg + "` is down or unreachable.\n";
            if (hostname.equals(hostname.toLowerCase())) {
                m += "Did you spell it correctly?";
            } else {
                m += "Try using lowercase letters.";
            }
            e.getHook().sendMessage(m).queue();
            return new Result(Outcome.SUCCESS);
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
        if (reply.isPreventsChatReports() && reply.isEnforcesSecureChat()) {
            m += ":interrobang: **Enforces and prevents chat reports at the same time? " +
                    "Server is sending contradictory messages.\n";
        } else if (reply.isPreventsChatReports()) {
            m += ":white_check_mark: **Prevents chat reports**\n";
        } else if (reply.isEnforcesSecureChat()) {
            m += ":shield: **Enforces chat reports**\n";
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
                if (!e.isFromGuild() || e.getGuild().getSelfMember().hasPermission(e.getGuildChannel(), Permission.MESSAGE_ATTACH_FILES)) {
                    MessageEmbed embed = eb.setDescription(m).setThumbnail("attachment://favicon.png").build();
                    e.getHook().sendFiles(FileUpload.fromData(icon.getData(), "favicon.png")).setEmbeds(embed).queue();
                    return new Result(Outcome.SUCCESS);
                } else {
                    eb.setDescription(m + "\n:warning: Give Minecord attach files permissions to see server icons.");
                }
            } else {
                eb.setDescription(m + "\n:x: Server returned an invalid icon.");
            }
        }
        e.getHook().sendMessageEmbeds(eb.build()).queue();
        return new Result(Outcome.SUCCESS);
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
