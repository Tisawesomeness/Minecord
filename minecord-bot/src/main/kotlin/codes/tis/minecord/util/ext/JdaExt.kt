package codes.tis.minecord.util.ext

import dev.minn.jda.ktx.events.CoroutineEventListener
import dev.minn.jda.ktx.events.EventTimeout
import dev.minn.jda.ktx.events.toTimeout
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import kotlin.time.Duration

// Adapted from https://github.com/MinnDevelopment/jda-ktx/blob/master/src/main/kotlin/dev/minn/jda/ktx/events/events.kt
inline fun <reified T : GenericEvent> DefaultShardManagerBuilder.addEventListener(
    timeout: Duration? = null,
    crossinline consumer: suspend CoroutineEventListener.(T) -> Unit,
): CoroutineEventListener {
    return object : CoroutineEventListener {
        override val timeout: EventTimeout
            get() = timeout.toTimeout()

        override fun cancel() {
            removeEventListeners(this)
        }

        override suspend fun onEvent(event: GenericEvent) {
            if (event is T) {
                consumer(event)
            }
        }
    }.also { addEventListeners(it) }
}
