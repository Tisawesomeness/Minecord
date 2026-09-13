package codes.tis.minecord.util

/**
 * A type used to represent the result of a computation that may be either:
 * - Success ([Ok]) with a value of type [T]
 * - Error ([Err]) with an error of type [E]
 *
 * @param T The success type, may be nullable
 * @param E The error type, may be nullable
 */
sealed interface Result<out T, out E>

/**
 * Represents a successful [Result] containing a [value].
 *
 * @param T The type of the value.
 */
data class Ok<out T>(val value: T) : Result<T, Nothing>

/**
 * Represents a failed [Result] containing an [err].
 *
 * @param E The type of the error.
 */
data class Err<out E>(val err: E) : Result<Nothing, E>

/**
 * Creates an [Ok] [Result] if this value is non-null, otherwise creates an [Err] with the error produced by [func].
 *
 * @param func A function that produces an error if the value is null.
 * @return An [Ok] containing the value if it is not null, otherwise an [Err] with the error produced by [func].
 */
inline fun <T, E> T?.orErr(func: () -> E) = if (this != null) Ok(this) else Err(func())

/**
 * Returns the value if this result is [Ok], otherwise throws an exception.
 * If the error is a [Throwable], it will be thrown. Otherwise, an [IllegalStateException]
 * is thrown with the error value as its message.
 *
 * @return The value if this result is [Ok].
 * @throws Throwable if the error is a [Throwable].
 * @throws IllegalStateException if the error is not a [Throwable].
 */
fun <T, E> Result<T, E>.unwrap(): T = when (this) {
    is Ok -> value
    is Err -> when (err) {
        is Throwable -> throw err
        else -> throw IllegalStateException(err.toString())
    }
}

/**
 * Filters null values from a successful result.
 *
 * If this is [Ok] and its value is non-null, returns this [Ok] unchanged.
 * If this is [Ok] and its value is null, returns an [Err] with the error produced by [func].
 * If this is [Err], returns this error unchanged.
 *
 * @param func A function that produces an error if the value is null.
 * @return This [Ok] with a non-null value, or an [Err] if the value was null.
 */
inline fun <T, E> Result<T, E>.notNull(func: () -> E): Result<T & Any, E> = when (this) {
    is Ok<T> -> when (value) {
        null -> Err(func())
        else -> Ok(value)
    }
    is Err<E> -> this
}

/**
 * If this is [Ok], applies [func] to the value and returns its [Result].
 * If this is [Err], returns this error unchanged.
 *
 * @param func A function that maps the success value to a new [Result].
 * @return The result of applying [func], or this [Err] if this result is an error.
 */
inline fun <T, E, R> Result<T, E>.and(func: (T) -> Result<R, E>) = when (this) {
    is Ok<T> -> func(value)
    is Err<E> -> this
}

/**
 * Alias for [and].
 */
inline fun <T, E, R> Result<T, E>.flatMap(func: (T) -> Result<R, E>) = and(func)

/**
 * If this is [Ok], returns its value.
 * If this is [Err], uses [func] to compute a fallback value.
 *
 * The receiver of [func] is the [Err] instance, so writing `orElse { return this }` allows returning early from a
 * function that returns [Result]:
 * ```kotlin
 * fun doWork(): Result<Int, ErrorType> {
 *   val result: Result<String, ErrorType> = someComputation()
 *   val string: String = result.orElse { return this }
 *   // function continues...
 * }
 * ```
 *
 * @param func A function that produces a fallback value from the error.
 * @return The success value, or the fallback produced by [func].
 */
inline fun <T, E> Result<T, E>.orElse(func: Err<E>.() -> T) = when (this) {
    is Ok<T> -> value
    is Err<E> -> func()
}

/**
 * If this is [Ok], returns this [Result].
 * If this is [Err], use [func] to compute a fallback [Result].
 *
 * @param func A function that produces a fallback [Result] when this is an [Err].
 * @return This result if [Ok], otherwise the result of [func].
 */
inline fun <T, E> Result<T, E>.or(func: () -> Result<T, E>) = when (this) {
    is Ok<T> -> this
    is Err<E> -> func()
}
