package codes.tis.minecord.testutil

import codes.tis.minecord.TranslationKey
import codes.tis.minecord.command.CommandContext
import codes.tis.minecord.command.tree.OptionalOption
import codes.tis.minecord.command.tree.ParseResult
import codes.tis.minecord.command.tree.RequiredOption
import codes.tis.minecord.command.tree.Resolver
import codes.tis.minecord.util.Err
import codes.tis.minecord.util.Ok

class TestResolver(
    val options: Map<String, String> = emptyMap(),
    val subcommandGroup: String? = null,
    val subcommand: String? = null,
) : Resolver<CommandContext> {
    override fun subcommandGroupOrSubcommand(candidates: List<String>): ParseResult<String> {
        if (subcommandGroup != null && subcommandGroup in candidates) {
            return Ok(subcommandGroup)
        }
        if (subcommand != null && subcommand in candidates) {
            return Ok(subcommand)
        }
        return Err(TranslationKey("minecord.error.no_subcommand_group_or_subcommand").format(candidates.joinToString()))
    }

    override fun subcommandGroup(candidates: List<String>): ParseResult<String> {
        if (subcommandGroup != null && subcommandGroup in candidates) {
            return Ok(subcommandGroup)
        }
        return Err(TranslationKey("minecord.error.no_subcommand_group").format(candidates.joinToString()))
    }

    override fun subcommand(candidates: List<String>, subcommandGroup: String?, depth: Int): ParseResult<String> {
        if (subcommand != null && subcommand in candidates) {
            return Ok(subcommand)
        }
        return if (subcommandGroup != null) {
            Err(TranslationKey("minecord.error.no_subcommand_for_group").format(subcommandGroup, candidates.joinToString()))
        } else {
            Err(TranslationKey("minecord.error.no_subcommand").format(candidates.joinToString()))
        }
    }

    override fun <T : Any> resolve(option: RequiredOption<CommandContext, T>, depth: Int): ParseResult<T> {
        val value = options[option.name] ?: return Err(TranslationKey("minecord.error.no_required_option"))
        return option.argument.resolve(option.name, value)
    }

    override fun <T : Any> resolve(option: OptionalOption<CommandContext, T>): ParseResult<T>? {
        val value = options[option.name] ?: return null
        return option.argument.resolve(option.name, value)
    }
}
