package com.tisawesomeness.minecord.service;

import com.tisawesomeness.minecord.config.config.BotListConfig;
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

/**
 * Sends the current guild count to bot lists.
 */
public class BotListService extends Service {
    private final @NonNull ShardManager sm;
    private final @NonNull BotListConfig config;
    private final @Nullable DiscordBotListAPI api;

    /**
     * Starts up the service, and the top.gg API if necessary.
     * @param sm The ShardManager to pull guild counts from
     * @param config The config that decides if and how often this service should run
     */
    public BotListService(@NonNull ShardManager sm, @NonNull BotListConfig config) {
        this.sm = sm;
        this.config = config;
        if (config.isSendServerCount() && config.getOrgToken() != null) {
            api = new DiscordBotListAPI.Builder().token(config.getOrgToken()).build();
        } else {
            api = null;
        }
    }

    @Override
    public boolean shouldRun() {
        return config.isSendServerCount();
    }

    public void schedule(ScheduledExecutorService exe) {
        exe.scheduleAtFixedRate(this::run, 0, config.getSendGuildsInterval(), TimeUnit.SECONDS);
    }

    /**
     * Call to manually run this service once.
     */
    public void run() {
        int servers = sm.getGuilds().size();
        String id = sm.getShardById(0).getSelfUser().getId();

        if (config.getPwToken() != null) {
            String url = "https://bots.discord.pw/api/bots/" + id + "/stats";
            String query = "{\"server_count\": " + servers + "}";
            RequestUtils.post(url, query, config.getPwToken());
        }

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
