package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.SlashCommand;
import com.tisawesomeness.minecord.util.DiscordUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.RequestUtils;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class StatusCommand extends SlashCommand {

    private static final List<MinecraftService> SERVICES = Arrays.asList(
            new UrlMinecraftService("https://minecraft.net", true),
            new AccountsService(),
            new UrlMinecraftService("https://sessionserver.mojang.com/session/minecraft/profile/853c80ef3c3749fdaa49938b674adae6"),
            new UrlMinecraftService("https://textures.minecraft.net/texture/7fd9ba42a7c81eeea22f1524271ae85a8e045ce0af5a6ae16c6406ae917e68b5"),
            new UrlMinecraftService("https://api.mojang.com"),
            new AzureService()
    );
    private static final int CACHE_TIME = 1000 * 60;

    private static long timestamp = 0;
    private static MessageEmbed statusResponse;

    public CommandInfo getInfo() {
        return new CommandInfo(
                "status",
                "Checks the status of Mojang servers.",
                null,
                2000,
                false,
                false
        );
    }

    public Result run(SlashCommandInteractionEvent e) {
        // Command is cached to prevent slow requests
        if (System.currentTimeMillis() - CACHE_TIME > timestamp) {
            e.deferReply().queue();
            statusResponse = getStatusResponse();
            timestamp = System.currentTimeMillis();
        }
        return new Result(Outcome.SUCCESS, statusResponse);
    }

    private static MessageEmbed getStatusResponse() {
        // Pings done in separate threads to speed up in case some URLs timeout
        List<CompletableFuture<Boolean>> statusRequests = SERVICES.stream()
                .map(StatusCommand::check)
                .collect(Collectors.toList());
        CompletableFuture.allOf(statusRequests.toArray(new CompletableFuture[SERVICES.size()])).join();
        List<Boolean> statuses = statusRequests.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        // Transform list of true/false into emote check/x
        List<String> statusEmotes = statuses.stream()
                .map(DiscordUtils::getBoolEmote)
                .collect(Collectors.toList());

        String m = "**Minecraft:** " + statusEmotes.get(0) +
                "\n" + "**Accounts:** " + statusEmotes.get(1) +
                "\n" + "**Session Server:** " + statusEmotes.get(2) +
                "\n" + "**Textures:** " + statusEmotes.get(3) +
                "\n" + "**Mojang API:** " + statusEmotes.get(4) +
                "\n" + "**Azure:** " + statusEmotes.get(5);

        Color color = getColor(statuses);
        return MessageUtils.embedMessage("Minecraft Status", null, m, color);
    }

    private static CompletableFuture<Boolean> check(MinecraftService service) {
        return CompletableFuture.supplyAsync(service::check);
    }

    private static Color getColor(List<Boolean> statuses) {
        boolean allGood = statuses.stream().allMatch(b -> b);
        if (allGood) {
            return Color.GREEN;
        }
        boolean allBad = statuses.stream().noneMatch(b -> b);
        if (allBad) {
            return Color.RED;
        }
        return Color.YELLOW;
    }

    private interface MinecraftService {
        boolean check();
    }

    @RequiredArgsConstructor
    private static class UrlMinecraftService implements MinecraftService {
        private final String url;
        private final boolean useSocket;

        public UrlMinecraftService(String url) {
            this(url, false);
        }

        public boolean check() {
            if (useSocket) {
                if (url.startsWith("https://")) {
                    return RequestUtils.checkWithSocket(url.substring(8));
                }
                return RequestUtils.checkWithSocket(url);
            }
            return RequestUtils.checkURLWithGet(url);
        }
    }

    private static class AccountsService implements MinecraftService {
        @Override
        public boolean check() {
            try {
                RequestUtils.post("https://api.minecraftservices.com/minecraft/profile/lookup/bulk/byname", "[\"jeb_\"]");
                return true;
            } catch (IOException ignore) {
                return false;
            }
        }
    }

    private static class AzureService implements MinecraftService {
        @Override
        public boolean check() {
            try {
                String response = RequestUtils.get("https://status.dev.azure.com/_apis/status/health");
                JSONObject json = new JSONObject(response);
                return json.getJSONObject("status").getString("health").equals("healthy");
            } catch (IOException | JSONException ignore) {
                return false;
            }
        }
    }

}
