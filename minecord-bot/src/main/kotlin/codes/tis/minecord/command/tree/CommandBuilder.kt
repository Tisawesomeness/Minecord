package codes.tis.minecord.command.tree

import codes.tis.minecord.command.CommandContext
import codes.tis.minecord.util.value.NonEmptyList

/**
 * Creates a [CommandBuilder] for a generic, platform-independent command.
 * @throws IllegalArgumentException if [name] does not follow the [CommandName] format
 */
fun genericCommand(name: String) = CommandBuilder<CommandContext>(name)

/**
 * Kotlin DSL for a [CommandTree]. Can be either:
 * - A single [topLevel] command
 * - A [grouped] list of one or more subcommand groups and/or subcommands
 *
 * Use [genericCommand] to create a cross-platform command, or use your platform's builder function
 * to create a command builder which supports that platform's command context.
 *
 * Example:
 * ```kotlin
 * val tree = genericCommand("ping").topLevel {
 *     command { ctx -> ctx.reply("pong") }
 * }
 * ```
 *
 * A more complex example:
 * ```kotlin
 * val tree = genericCommand("random").grouped {
 *     subcommandGroup("number") {
 *         subcommand("uniform") {
 *             command(
 *                 required("min", IntArgument),
 *                 required("max", IntArgument)
 *             ) { ctx, min, max -> /* ... */ }
 *         }
 *         subcommand("normal") {
 *             command(
 *                 optional("mean", DoubleArgument),
 *                 optional("stddev", DoubleArgument)
 *             ) { ctx, mean, stddev -> /* ... */ }
 *         }
 *     }
 *     subcommand("uuid") {
 *         command { ctx -> /* ... */ }
 *     }
 * }
 * ```
 *
 * @param C type of the [CommandTree] to be built, determines which [Argument]s are supported
 * @throws IllegalArgumentException if [name] does not follow the [CommandName] format
 */
class CommandBuilder<C>(val name: String) {
    /**
     * Creates a top-level command tree with the given command
     */
    fun topLevel(block: () -> Command<C>): CommandTree<C> {
        return TopLevel(CommandName.must(name), block())
    }

    /**
     * Creates a grouped command tree with one or more subcommand groups and/or subcommands.
     * @throws IllegalStateException if no subcommand groups or subcommands are added
     */
    fun grouped(block: GroupedBuilder<C>.() -> Unit): CommandTree<C> {
        val builder = GroupedBuilder<C>(CommandName.must(name))
        builder.block()
        return builder.build()
    }
}

class GroupedBuilder<C> internal constructor(val name: CommandName) {
    private val list: MutableList<SubcommandGrouping<C>> = mutableListOf()

    /**
     * Adds a subcommand group to this builder.
     * @throws IllegalArgumentException if [name] does not follow the [CommandName] format
     * @throws IllegalStateException if the subcommand group has no subcommands
     */
    fun subcommandGroup(name: String, block: SubcommandGroupBuilder<C>.() -> Unit) {
        val builder = SubcommandGroupBuilder<C>(CommandName.must(name))
        builder.block()
        list.add(builder.build())
    }

    /**
     * Adds a subcommand to this builder.
     * @throws IllegalArgumentException if [name] does not follow the [CommandName] format
     */
    fun subcommand(name: String, block: () -> Command<C>) {
        list.add(Subcommand(CommandName.must(name), block()))
    }

    internal fun build(): Grouped<C> {
        return Grouped(name, NonEmptyList.make(list) ?: error("Group must have at least one subcommand group or subcommand"))
    }
}

class SubcommandGroupBuilder<C> internal constructor(val name: CommandName) {
    private val list: MutableList<Subcommand<C>> = mutableListOf()

    /**
     * Adds a subcommand to this builder.
     * @throws IllegalArgumentException if [name] does not follow the [CommandName] format
     */
    fun subcommand(name: String, block: () -> Command<C>) {
        list.add(Subcommand(CommandName.must(name), block()))
    }

    internal fun build(): SubcommandGroup<C> {
        return SubcommandGroup(name, NonEmptyList.make(list) ?: error("Subcommand group must have at least one subcommand"))
    }
}

inline fun <reified C> command(
    noinline handler: suspend (C) -> Unit,
) = Command0(handler)

inline fun <reified C, reified T1> command(
    option1: Option<C, T1>,
    noinline handler: suspend (C, T1) -> Unit,
) = Command1(
    option1,
    handler,
)

inline fun <reified C, reified T1, reified T2> command(
    option1: Option<C, T1>,
    option2: Option<C, T2>,
    noinline handler: suspend (C, T1, T2) -> Unit,
) = Command2(
    option1,
    option2,
    handler,
)

inline fun <reified C, reified T1, reified T2, reified T3> command(
    option1: Option<C, T1>,
    option2: Option<C, T2>,
    option3: Option<C, T3>,
    noinline handler: suspend (C, T1, T2, T3) -> Unit,
) = Command3(
    option1,
    option2,
    option3,
    handler,
)

inline fun <reified C, reified T1, reified T2, reified T3, reified T4> command(
    option1: Option<C, T1>,
    option2: Option<C, T2>,
    option3: Option<C, T3>,
    option4: Option<C, T4>,
    noinline handler: suspend (C, T1, T2, T3, T4) -> Unit,
) = Command4(
    option1,
    option2,
    option3,
    option4,
    handler,
)

/**
 * Creates a required option that resolves to a value of type [T].
 *
 * Arguments only work with context type C.
 * For example, if C is a CommandLineContext, but you are trying to create a CommandTree<DiscordContext>,
 * compilation will fail with a type error.
 */
inline fun <C, reified T : Any> required(name: String, argument: Argument<C, T>) = RequiredOption(name, argument)

/**
 * Creates an optional option that resolves to a value of type [T] if present, or null if absent.
 *
 * Arguments only work with context type C.
 * For example, if C is a CommandLineContext, but you are trying to create a CommandTree<DiscordContext>,
 * compilation will fail with a type error.
 */
inline fun <C, reified T : Any> optional(name: String, argument: Argument<C, T>) = OptionalOption(name, argument)
