package codes.tis.minecord.command.tree

private val PATTERN = "^[-_\u02BC\\p{L}\\p{N}\\p{sc=Deva}\\p{sc=Thai}]{1,32}$".toRegex()

/**
 * A string that satisfies Discord's
 * [command name syntax](https://docs.discord.com/developers/interactions/application-commands#application-command-object-application-command-naming):
 *
 * ```regex
 * ^[-_\u02BC\p{L}\p{N}\p{sc=Deva}\p{sc=Thai}]{1,32}$
 * ```
 */
@JvmInline
value class CommandName private constructor(val name: String) {
    companion object {
        fun make(name: String?): CommandName? {
            return if (name != null && PATTERN.matches(name)) CommandName(name) else null
        }

        fun must(name: String) = requireNotNull(make(name)) { "`$name` does not match command name pattern" }
    }

    override fun toString() = name
}
