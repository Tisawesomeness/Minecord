package codes.tis.minecord.util.ext

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KProperty

/**
 * Logs a fatal message. Does not exit the program.
 */
fun Logger.fatal(msg: String) = error("FATAL: $msg")

/**
 * Logs a fatal message. Does not exit the program.
 */
fun Logger.fatal(msg: String, t: Throwable) = error("FATAL: $msg", t)

// Adapted from https://github.com/MinnDevelopment/jda-ktx/blob/master/src/main/kotlin/dev/minn/jda/ktx/util/proxies.kt
object Logger {
    operator fun getValue(thisRef: Any, prop: KProperty<*>): Logger {
        return LoggerFactory.getLogger(thisRef::class.java)!!
    }

    operator fun invoke(name: String) = lazy {
        LoggerFactory.getLogger(name)!!
    }

    inline operator fun <reified T> invoke() = lazy {
        LoggerFactory.getLogger(T::class.java)!!
    }
}
