package com.tisawesomeness.minecord.common;

import lombok.NonNull;
import lombok.ToString;
import lombok.Value;

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
    /** The OkHttp connection the shard manager will use */
    @NonNull OkHttpConnection connection;
}
