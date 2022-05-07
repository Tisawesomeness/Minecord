package com.tisawesomeness.minecord.share;

import lombok.NonNull;
import lombok.ToString;
import lombok.Value;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient.Builder;

import java.time.Instant;

/**
 * Contains data available to the bot during the pre-init stage.
 */
@Value
public class BootContext {
    /** When the boot process started, specifically the instant the args stage started. */
    @NonNull Instant startTime;
    /** The total shard count. */
    int shardCount;
    /** The token used to log into Discord. This is a sensitive secret! */
    @ToString.Exclude
    @NonNull String token;
    /** Builder used to create the HTTP client */
    @NonNull Builder httpClientBuilder;
    /** Dispatcher used to deploy HTTP requests */
    @NonNull Dispatcher dispatcher;
    /** Pool used to manage HTTP connections */
    @NonNull ConnectionPool connectionPool;
}
