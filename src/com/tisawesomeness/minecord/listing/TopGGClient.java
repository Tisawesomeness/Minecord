package com.tisawesomeness.minecord.listing;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.Registry;
import com.tisawesomeness.minecord.network.APIClient;
import com.tisawesomeness.minecord.network.NetUtil;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TopGGClient {

    private final APIClient apiClient;

    public void sendGuilds() {
        if (!Config.getSendServerCount() || Config.getOrgToken() == null) {
            return;
        }

        try {
            String id = Bot.getSelfUser().getId();
            URL url = new URL(String.format("https://top.gg/api/bots/%s/stats", id));

            JSONObject payload = new JSONObject();
            payload.put("server_count", Bot.getGuildCount());

            @Cleanup Response response = apiClient.post(url, payload, "Bearer " + Config.getOrgToken());
            NetUtil.throwIfError(response, "top.gg");
        } catch (IOException ex) {
            throw new RuntimeException("Sending guilds to top.gg failed", ex);
        }
    }

    public void sendSlashCommands() {
        if (!Config.getSendSlashCommands() || Config.getOrgToken() == null) {
            return;
        }

        try {
            URL url = new URL("https://top.gg/api/v1/projects/@me/commands");
            @Cleanup Response response = apiClient.post(url, commandsJson(), "Bearer " + Config.getOrgToken());
            NetUtil.throwIfError(response, "top.gg");
        } catch (IOException ex) {
            throw new RuntimeException("Sending guilds to top.gg failed", ex);
        }
    }

    private JSONArray commandsJson() {
        Collection<JSONObject> jsonObjects = Registry.getSlashCommands().stream()
                .map(this::convertToJson)
                .collect(Collectors.toList());
        return new JSONArray(jsonObjects);
    }
    private JSONObject convertToJson(CommandData cmd) {
        return new JSONObject(new String(cmd.toData().toJson(), StandardCharsets.UTF_8));
    }

}
