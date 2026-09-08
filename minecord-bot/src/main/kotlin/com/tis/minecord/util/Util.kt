package com.tis.minecord.util

import dev.minn.jda.ktx.jdabuilder.injectKTX
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import kotlin.time.Duration

fun env(name: String): String? {
    return System.getenv(name)
}

// Adapted from https://github.com/MinnDevelopment/jda-ktx/blob/master/src/main/kotlin/dev/minn/jda/ktx/jdabuilder/jdabuilder.kt
inline fun shardManagerLight(
    token: String,
    enableCoroutines: Boolean = true,
    timeout: Duration = Duration.INFINITE,
    intents: Collection<GatewayIntent>,
    builder: DefaultShardManagerBuilder.() -> Unit = {}
): ShardManager {
    return DefaultShardManagerBuilder.createLight(token, intents)
        .apply(builder)
        .apply {
            if (enableCoroutines) {
                injectKTX(timeout=timeout)
            }
        }
        .build()
}
