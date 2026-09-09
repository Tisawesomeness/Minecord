package codes.tis.minecord

import codes.tis.minecord.config.Config
import codes.tis.minecord.util.env
import codes.tis.minecord.util.ext.Logger
import codes.tis.minecord.util.ext.addEventListener
import codes.tis.minecord.util.ext.fatal
import codes.tis.minecord.util.openResource
import codes.tis.minecord.util.shardManagerLight
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
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

object Bot {
    private val GATEWAYS = setOf(
        GatewayIntent.DIRECT_MESSAGES, GatewayIntent.DIRECT_MESSAGE_REACTIONS,
        GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS,
    )

    private val log by Logger
    private lateinit var shardManager: ShardManager
    private lateinit var dispatcher: Dispatcher
    private lateinit var connectionPool: ConnectionPool

    /**
     * Starts the bot for the first time.
     * @return false if an error occurred, and the program should no longer proceed
     */
    fun start(configDir: Path): Boolean {
        val configFile = configDir.resolve("config.yml")
        if (!Files.exists(configFile)) {
            Files.createDirectories(configDir)
            openResource("/config.yml").use { input ->
                Files.copy(input, configFile)
            }
            log.info("Created config.yml from default configuration")
        }
        val config = readConfig<Config>(Files.newInputStream(configFile))

        val token = Token.make(env("MINECORD_TOKEN") ?: config.token)
        if (token == null) {
            log.warn("!!! Token not found !!!")
            log.warn("!!! Follow the instructions in minecord/config.yml to run the bot. !!!")
            return true
        }

        val advanced = config.advanced
        dispatcher = Dispatcher().apply { maxRequestsPerHost = advanced.maxRequestsPerHost.value }
        connectionPool = ConnectionPool(advanced.maxIdleConnections.value, advanced.keepAlive.value.toLong(), TimeUnit.MILLISECONDS)
        val httpClientBuilder = OkHttpClient.Builder().connectionPool(connectionPool).dispatcher(dispatcher)

        log.info("Logging in...")
        try {
            shardManager = shardManagerLight(token.value(), enableCoroutines = true, intents = GATEWAYS) {
                setAutoReconnect(true)
                setShardsTotal(config.advanced.shardCount.value)
                setStatus(OnlineStatus.IDLE)
                setActivity(Activity.playing("Loading..."))
                setHttpClientBuilder(httpClientBuilder)

                addEventListener<ReadyEvent> { log.info("Shard ready") }
            }
        } catch (e: ErrorResponseException) {
            log.fatal("Error while logging in: ${e.errorCode}: ${e.meaning}")
            return false
        } catch (e: InvalidTokenException) {
            log.fatal("There was an error logging in, check if your token is correct.", e)
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
