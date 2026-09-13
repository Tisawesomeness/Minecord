package codes.tis.minecord.command.tree

import codes.tis.minecord.util.Err

/**
 * Given an input command, determines which path in the [CommandTree] to take, and the values of the requested options.
 *
 * Example for command `/permission set player player=Tis admin=true`:
 * - Subcommand group `set`
 * - Subcommand `player`
 * - Options `player=Tis` and `admin=true`
 *
 * If this resolver's platform supports more than just String arguments (such as Discord supporting ints, users...),
 * you can resolve that argument type directly instead of converting from String.
 *
 * First define the platform's argument types:
 * ```kotlin
 * // Can be used with any context, but discord can call `resolve(String, Int)` directly
 * interface IntArgumentType<out T : Any> : Argument<Any?, T> {
 *     override fun resolve(name: String, raw: String) = resolve(name, raw, raw.toIntOrNull())
 *     fun resolve(name: String, raw: String, int: Int?): ParseResult<T>
 * }
 *
 * // Can only be used on Discord, so restricted to DiscordContext
 * interface UserArgumentType<out T : Any> : Argument<DiscordContext, T> {
 *     override fun resolve(name: String, raw: String): ParseResult<T> = Err(DISCORD_ONLY)
 *     fun resolve(name: String, raw: String, user: User?): ParseResult<T>
 * }
 * ```
 *
 * Define arguments that extend the argument types:
 * ```kotlin
 * object IntArgument : IntArgumentType<Int> {
 *     override fun resolve(name: String, raw: String, int: Int?) = int.orErr { INVALID_INT }
 * }
 *
 * object PositiveIntArgument : IntArgumentType<PositiveInt> {
 *     override fun resolve(name: String, raw: String, int: Int?) = PositiveInt.make(int)
 *         .orErr { NOT_POSITIVE }
 * }
 * ```
 *
 * Then when resolving options, check if the argument is one of the types you defined earlier:
 * ```kotlin
 * val argument: Argument<DiscordContext, T> = option.argument
 * when (argument) {
 *     is IntArgumentType<T> -> argument.resolve(name, mapping.asString, mapping.asInt)
 *     is UserArgumentType<T> -> argument.resolve(name, mapping.asString, mapping.asUser)
 *     // ...
 *     else -> argument.resolve(name, mapping.asString)
 * }
 * ```
 *
 * @param C command context type
 */
interface Resolver<out C> {
    /**
     * Gets the name of the subcommand group or subcommand at depth 1. `/root` is depth 0, `/root child` is depth 1.
     * @param candidates list of subcommand group or subcommand names in this command tree
     * @return subcommand group or subcommand name, or an [Err] if none exist
     */
    fun subcommandGroupOrSubcommand(candidates: List<String>): ParseResult<String>

    /**
     * Gets the name of the subcommand group at depth 1. `/root` is depth 0, `/root child` is depth 1.
     * @param candidates list of subcommand group names in this command tree
     * @return subcommand group name, or an [Err] if no subcommands exist at that depth
     */
    fun subcommandGroup(candidates: List<String>): ParseResult<String>

    /**
     * Gets the name of the subcommand.
     * @param candidates list of subcommand names in this command tree
     * @param subcommandGroup subcommand group, if present
     * @param depth current parse depth, `/root` is depth 0, `/root child` is depth 1
     * @return subcommand name, or an [Err] if no subcommands exist at that depth
     */
    fun subcommand(candidates: List<String>, subcommandGroup: String?, depth: Int): ParseResult<String>

    /**
     * Resolves a required option into a value.
     * @param option the option to resolve, call `option.argument.resolve()` with the option's string input
     * @param depth current parse depth, `/root` is depth 0, `/root child` is depth 1
     * @return the resolved value, or an [Err] if resolving failed or the option was not provided
     */
    fun <T : Any> resolve(option: RequiredOption<C, T>, depth: Int): ParseResult<T>

    /**
     * Resolves an optional option into a value.
     * @param option the option to resolve, call `option.argument.resolve()` with the option's string input
     * @return the resolved value, an [Err] if resolving failed, or null if the option was not provided
     */
    fun <T : Any> resolve(option: OptionalOption<C, T>): ParseResult<T>?
}
