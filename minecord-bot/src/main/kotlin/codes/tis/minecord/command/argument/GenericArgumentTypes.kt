package codes.tis.minecord.command.argument

import codes.tis.minecord.command.tree.Argument
import codes.tis.minecord.command.tree.ParseResult

interface StringArgumentType<out T : Any> : Argument<Any?, T>

interface BooleanArgumentType<out T : Any> : Argument<Any?, T> {
    override fun resolve(name: String, raw: String) = resolve(name, raw, raw.lowercase().toBooleanStrictOrNull())

    fun resolve(name: String, raw: String, boolean: Boolean?): ParseResult<T>
}

interface LongArgumentType<out T : Any> : Argument<Any?, T> {
    override fun resolve(name: String, raw: String) = resolve(name, raw, raw.toLongOrNull())

    fun resolve(name: String, raw: String, long: Long?): ParseResult<T>
}

interface DoubleArgumentType<out T : Any> : Argument<Any?, T> {
    override fun resolve(name: String, raw: String) = resolve(name, raw, raw.toDoubleOrNull())

    fun resolve(name: String, raw: String, double: Double?): ParseResult<T>
}
