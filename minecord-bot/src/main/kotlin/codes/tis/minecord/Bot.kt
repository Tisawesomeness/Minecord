package codes.tis.minecord

import codes.tis.minecord.util.ext.addEventListener
import codes.tis.minecord.util.shardManagerLight
import dev.minn.jda.ktx.util.SLF4J
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.exceptions.ErrorResponseException
import net.dv8tion.jda.api.exceptions.InvalidTokenException
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.sharding.ShardManager
import okhttp3.ConnectionPool
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

object Bot {

    private val GATEWAYS = setOf(
        GatewayIntent.DIRECT_MESSAGES, GatewayIntent.DIRECT_MESSAGE_REACTIONS,
        GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS
    )

    private val log by SLF4J
    private lateinit var shardManager: ShardManager
    private lateinit var dispatcher: Dispatcher
    private lateinit var connectionPool: ConnectionPool

    /**
     * Starts the bot for the first time.
     * @return false if an error occurred, and the program should no longer proceed
     */
    fun start(token: Token): Boolean {
        dispatcher = Dispatcher().apply { maxRequestsPerHost = 25 }
        connectionPool = ConnectionPool(5, 10, TimeUnit.SECONDS)
        val httpClientBuilder = OkHttpClient.Builder().connectionPool(connectionPool).dispatcher(dispatcher)

        log.info("Logging in...")
        try {
            shardManager = shardManagerLight(token.value(), enableCoroutines = true, intents = GATEWAYS) {
                setAutoReconnect(true)
                setShardsTotal(1)
                setStatus(OnlineStatus.IDLE)
                setActivity(Activity.playing("Loading..."))
                setHttpClientBuilder(httpClientBuilder)

                addEventListener<ReadyEvent> { log.info("Shard ready") }
            }
        } catch (e: ErrorResponseException) {
            log.error("Error while logging in: ${e.errorCode}: ${e.meaning}")
            return false
        } catch (_: InvalidTokenException) {
            log.error("Invalid token")
            return false
        }

        shardManager.setStatus(OnlineStatus.ONLINE)
        shardManager.setActivity(Activity.playing("Minecord"))

        Runtime.getRuntime().addShutdownHook(thread(start = false) { shutdown() })
        return true
    }

    fun shutdown() {
        log.info("Shutting down...")
        shardManager.shutdown()
        connectionPool.evictAll()
        dispatcher.executorService.shutdown()
    }

}
