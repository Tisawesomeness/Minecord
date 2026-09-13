package codes.tis.minecord.command.tree

import codes.tis.minecord.util.Err
import codes.tis.minecord.util.Ok
import codes.tis.minecord.util.orElse
import codes.tis.minecord.util.value.NonEmptyList
import org.jetbrains.annotations.ApiStatus

/**
 * A command tree that models a valid Discord slash command, though these commands can work on any platform.
 *
 * A command tree is either:
 * - A single [TopLevel] command
 * - A list of groups in a [Grouped] instance
 *
 * A group can be either:
 *   * A [SubcommandGroup], containing one or more [Subcommand]s
 *   * A [Subcommand]
 *
 * See the [Discord docs](https://docs.discord.com/developers/interactions/application-commands#subcommands-and-subcommand-groups)
 * for details.
 *
 * The command context type [C] determines which [Argument]s can be used in this command tree. For example:
 * ```kotlin
 * interface CommandContext
 * class CommandLineContext : CommandContext
 * class DiscordContext : CommandContext
 *
 * // Can be used with `CommandTree<CommandLineContext>` or `CommandTree<DiscordContext>`
 * object IntArgument : Argument<CommandContext, Int>
 * // Can only be used with `CommandTree<DiscordContext>`
 * object UserArgument : Argument<DiscordContext, User>
 * ```
 */
sealed interface CommandTree<C> {
    val name: CommandName

    /**
     * Given a [Resolver] of a command invocation, determine whether the input command
     * satisfies this command tree and all options.
     * @param resolver the resolver
     * @return If parsing is successful, returns [Ok] with the command's handler function, otherwise [Err]
     */
    fun parse(resolver: Resolver<C>): ParseResult<HandleFunc<C>>

    override fun toString(): String
}

/**
 * A command tree consisting of a single top-level command.
 */
class TopLevel<C>(
    override val name: CommandName,
    val command: Command<C>,
) : CommandTree<C> {
    override fun parse(resolver: Resolver<C>): ParseResult<HandleFunc<C>> = command.parse(resolver, 1)

    override fun toString(): String = "/" + formatCommand(name, command)
}

/**
 * A command tree consisting of one or more groups.
 */
class Grouped<C>(
    override val name: CommandName,
    val subcommandGroupings: NonEmptyList<SubcommandGrouping<C>>,
) : CommandTree<C> {
    override fun parse(resolver: Resolver<C>): ParseResult<HandleFunc<C>> {
        val candidates = subcommandGroupings.map { it.name.toString() }
        val childName = if (subcommandGroupings.all { grouping -> grouping is SubcommandGroup<C> }) {
            resolver.subcommandGroup(candidates).orElse { return this }
        } else if (subcommandGroupings.all { grouping -> grouping is Subcommand<C> }) {
            resolver.subcommand(candidates, null, 1).orElse { return this }
        } else {
            resolver.subcommandGroupOrSubcommand(candidates).orElse { return this }
        }
        val child = subcommandGroupings.find { c -> c.name.toString() == childName }
            ?: error("Resolver returned nonexistent child $childName")
        return child.parse(resolver, 1)
    }

    override fun toString(): String {
        return subcommandGroupings
            .flatMap { grouping ->
                grouping.toString().lines().map { line -> "/$name $line" }
            }.joinToString("\n")
    }
}

/**
 * Either a [SubcommandGroup] or a [Subcommand].
 */
sealed interface SubcommandGrouping<C> {
    val name: CommandName

    @ApiStatus.Internal
    fun parse(resolver: Resolver<C>, depth: Int): ParseResult<HandleFunc<C>>

    override fun toString(): String
}

/**
 * A subcommand group containing one or more subcommands.
 */
class SubcommandGroup<C>(
    override val name: CommandName,
    val subcommands: NonEmptyList<Subcommand<C>>,
) : SubcommandGrouping<C> {
    override fun parse(resolver: Resolver<C>, depth: Int): ParseResult<HandleFunc<C>> {
        val subcommandNames = subcommands.map { it.name.toString() }
        val childName = resolver.subcommand(subcommandNames, name.toString(), depth).orElse { return this }
        val child = subcommands.find { c -> c.name.toString() == childName } ?: error("Resolver returned nonexistent child $childName")
        return child.parse(resolver, depth + 1)
    }

    override fun toString() = subcommands.joinToString("\n") { subcommand -> "$name $subcommand" }
}

/**
 * A subcommand.
 */
class Subcommand<C>(
    override val name: CommandName,
    val command: Command<C>,
) : SubcommandGrouping<C> {
    override fun parse(resolver: Resolver<C>, depth: Int) = command.parse(resolver, depth + 1)

    override fun toString() = formatCommand(name, command)
}

private fun formatCommand(name: CommandName, command: Command<*>): String {
    val options = command.options()
    if (options.isEmpty()) {
        return name.toString()
    }
    val optionsStr = options.joinToString(" ")
    return "$name $optionsStr"
}
