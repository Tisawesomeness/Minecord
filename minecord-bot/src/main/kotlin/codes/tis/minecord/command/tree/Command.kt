package codes.tis.minecord.command.tree

import codes.tis.minecord.Localizable
import codes.tis.minecord.util.Ok
import codes.tis.minecord.util.Result
import codes.tis.minecord.util.orElse
import org.jetbrains.annotations.ApiStatus

/**
 * A command handler function.
 * Called with the command context [C], and the values of any [Option].
 * Required options are non-null, optional options are nullable.
 * @param C command context type
 */
typealias HandleFunc<C> = suspend (C) -> Unit

/**
 * A [Result] with a [Localizable] error
 * @param T success type
 */
typealias ParseResult<T> = Result<T, Localizable>

/**
 * A command with subclasses for different numbers of options.
 * @param C command context type
 */
sealed interface Command<C> {
    @ApiStatus.Internal
    fun parse(resolver: Resolver<C>, depth: Int): ParseResult<HandleFunc<C>>

    fun options(): List<Option<C, *>>
}

class Command0<C>(
    val handler: suspend (C) -> Unit,
) : Command<C> {
    override fun parse(resolver: Resolver<C>, depth: Int): ParseResult<HandleFunc<C>> {
        return Ok(suspend { ctx -> handler(ctx) })
    }

    override fun options(): List<Option<C, *>> = emptyList()
}

class Command1<C, T1>(
    val option1: Option<C, T1>,
    val handler: suspend (C, T1) -> Unit,
) : Command<C> {
    override fun parse(resolver: Resolver<C>, depth: Int): ParseResult<HandleFunc<C>> {
        val v1 = resolve(resolver, option1, depth).orElse { return this }
        return Ok(suspend { ctx -> handler(ctx, v1) })
    }

    override fun options() = listOf(option1)
}

class Command2<C, T1, T2>(
    val option1: Option<C, T1>,
    val option2: Option<C, T2>,
    val handler: suspend (C, T1, T2) -> Unit,
) : Command<C> {
    override fun parse(resolver: Resolver<C>, depth: Int): ParseResult<HandleFunc<C>> {
        val v1 = resolve(resolver, option1, depth).orElse { return this }
        val v2 = resolve(resolver, option2, depth + 1).orElse { return this }
        return Ok(suspend { ctx -> handler(ctx, v1, v2) })
    }

    override fun options() = listOf(option1, option2)
}

class Command3<C, T1, T2, T3>(
    val option1: Option<C, T1>,
    val option2: Option<C, T2>,
    val option3: Option<C, T3>,
    val handler: suspend (C, T1, T2, T3) -> Unit,
) : Command<C> {
    override fun parse(resolver: Resolver<C>, depth: Int): ParseResult<HandleFunc<C>> {
        val v1 = resolve(resolver, option1, depth).orElse { return this }
        val v2 = resolve(resolver, option2, depth + 1).orElse { return this }
        val v3 = resolve(resolver, option3, depth + 2).orElse { return this }
        return Ok(suspend { ctx -> handler(ctx, v1, v2, v3) })
    }

    override fun options() = listOf(option1, option2, option3)
}

class Command4<C, T1, T2, T3, T4>(
    val option1: Option<C, T1>,
    val option2: Option<C, T2>,
    val option3: Option<C, T3>,
    val option4: Option<C, T4>,
    val handler: suspend (C, T1, T2, T3, T4) -> Unit,
) : Command<C> {
    override fun parse(resolver: Resolver<C>, depth: Int): ParseResult<HandleFunc<C>> {
        val v1 = resolve(resolver, option1, depth).orElse { return this }
        val v2 = resolve(resolver, option2, depth + 1).orElse { return this }
        val v3 = resolve(resolver, option3, depth + 2).orElse { return this }
        val v4 = resolve(resolver, option4, depth + 3).orElse { return this }
        return Ok(suspend { ctx -> handler(ctx, v1, v2, v3, v4) })
    }

    override fun options() = listOf(option1, option2, option3, option4)
}

private fun <C, T> resolve(resolver: Resolver<C>, option: Option<C, T>, depth: Int): ParseResult<T> {
    return when (option) {
        is RequiredOption<C, *> -> {
            // RequiredOption<in C, T : any> bounds RequiredOption's T to non-null
            @Suppress("UNCHECKED_CAST")
            option as RequiredOption<C, T & Any>
            resolver.resolve(option, depth)
        }
        is OptionalOption<C, *> -> {
            // OptionalOption<in C, T : any> bounds OptionalOption's T to non-null
            @Suppress("UNCHECKED_CAST")
            option as OptionalOption<C, T & Any>
            // Since OptionalOption<in C, T : any> extends Option<C, T?>, this implies Option's T is nullable,
            // so casting `null` to T is ok
            @Suppress("UNCHECKED_CAST")
            resolver.resolve(option) ?: Ok(null as T)
        }
    }
}

/**
 * A command option, which may either be a [RequiredOption] (non-null value) or an [OptionalOption] (nullable value)
 *
 * Options only work with context type [C].
 * For example, if [C] is a `CommandLineContext`, but you are trying to create a `CommandTree<DiscordContext>`,
 * compilation will fail with a type error.
 *
 * @param C command context type
 * @param T argument type, non-null if required, nullable if optional
 */
sealed interface Option<in C, out T> {
    val name: String
    val argument: Argument<C, T & Any>
}

/**
 * A required [Option]
 * @param C command context type
 * @param T argument type
 */
class RequiredOption<in C, T : Any>(
    override val name: String,
    override val argument: Argument<C, T>,
) : Option<C, T> {
    override fun toString() = "<$name: ${argument.name}>"
}

/**
 * An optional [Option]
 * @param C command context type
 * @param T argument type
 */
class OptionalOption<in C, T : Any>(
    override val name: String,
    override val argument: Argument<C, T>,
) : Option<C, T?> {
    override fun toString() = "[<$name: ${argument.name}>]"
}
