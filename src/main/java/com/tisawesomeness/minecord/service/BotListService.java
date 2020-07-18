package com.tisawesomeness.minecord.service;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.util.RequestUtils;

import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.discordbots.api.client.DiscordBotListAPI;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BotListService extends Service {
    private final @NonNull ShardManager sm;
    private final @NonNull Config config;
    private final @Nullable DiscordBotListAPI api;

    public BotListService(@NonNull ShardManager sm, @NonNull Config config) {
        this.sm = sm;
        this.config = config;
        if (config.sendServerCount) {
            api = new DiscordBotListAPI.Builder().token(config.orgToken).build();
        } else {
            api = null;
        }
    }

    @Override
    public boolean shouldRun() {
        return config.sendServerCount && config.sendGuildsInterval > 0;
    }

    public void schedule(ScheduledExecutorService exe) {
        exe.scheduleAtFixedRate(this::run, 0, config.sendGuildsInterval, TimeUnit.SECONDS);
    }

    public void run() {
        int servers = sm.getGuilds().size();
        String id = sm.getShardById(0).getSelfUser().getId();

        String url = "https://bots.discord.pw/api/bots/" + id + "/stats";
        String query = "{\"server_count\": " + servers + "}";
        RequestUtils.post(url, query, config.pwToken);

        /*
         * url = "https://discordbots.org/api/bots/" + id + "/stats"; query =
         * "{\"server_count\": " + servers + "}"; post(url, query,
         * Config.getOrgToken());
         */

        List<Integer> serverCounts = new ArrayList<>();
        for (JDA jda : sm.getShards()) {
            serverCounts.add(jda.getGuilds().size());
        }
        if (api != null) {
            api.setStats(id, serverCounts);
        }
    }

}
