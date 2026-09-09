package codes.tis.minecord.config

import codes.tis.minecord.util.value.PositiveInt

data class AdvancedConfig(
    val shardCount: ShardCount,
    val maxRequestsPerHost: PositiveInt,
    val maxIdleConnections: PositiveInt,
    val keepAlive: PositiveInt,
)
