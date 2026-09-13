package codes.tis.minecord.command.tree

/**
 * An argument of a command with context type [C], resolving an input String to the argument type [T].
 *
 * Simple example that converts a String to an Int:
 * ```kotlin
 * object IntArgument : Argument<Any?, Int> {
 *     override fun resolve(name: String, raw: String): ParseResult<Int> {
 *         return Ok(raw.toIntOrNull()).notNull { INVALID_INT }
 *     }
 * }
 * ```
 *
 * Arguments only work with context type [C].
 * For example, if [C] is a `CommandLineContext`, but you are trying to create a `CommandTree<DiscordContext>`,
 * compilation will fail with a type error.
 */
interface Argument<in C, out T : Any> {
    /**
     * The name of the argument, first letter capital.
     */
    val name: String

    /**
     * Resolves an input String into a value of the argument type.
     * @param name The name of the argument (for use in error messages)
     * @param raw The raw input string
     * @return [codes.tis.minecord.util.Ok] with the resolved value, else [codes.tis.minecord.util.Err]
     */
    fun resolve(name: String, raw: String): ParseResult<T>
}
