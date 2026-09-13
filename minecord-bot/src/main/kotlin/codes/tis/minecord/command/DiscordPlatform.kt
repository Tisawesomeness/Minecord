package codes.tis.minecord.command

import codes.tis.minecord.TranslationKey
import codes.tis.minecord.command.argument.AttachmentArgumentType
import codes.tis.minecord.command.argument.BooleanArgumentType
import codes.tis.minecord.command.argument.ChannelArgumentType
import codes.tis.minecord.command.argument.DoubleArgumentType
import codes.tis.minecord.command.argument.LongArgumentType
import codes.tis.minecord.command.argument.MentionableArgumentType
import codes.tis.minecord.command.argument.RoleArgumentType
import codes.tis.minecord.command.argument.UserArgumentType
import codes.tis.minecord.command.tree.Argument
import codes.tis.minecord.command.tree.CommandBuilder
import codes.tis.minecord.command.tree.OptionalOption
import codes.tis.minecord.command.tree.ParseResult
import codes.tis.minecord.command.tree.RequiredOption
import codes.tis.minecord.command.tree.Resolver
import codes.tis.minecord.util.Err
import codes.tis.minecord.util.flatMap
import codes.tis.minecord.util.orErr
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType

fun discordCommand(name: String) = CommandBuilder<DiscordContext>(name)

val BAD_DATA_ERROR = TranslationKey("minecord.error.discord_bad_data")

class DiscordResolver(val e: SlashCommandInteractionEvent) : Resolver<DiscordContext> {
    override fun subcommandGroupOrSubcommand(candidates: List<String>): ParseResult<String> {
        return (e.subcommandGroup ?: e.subcommandName)
            ?.takeIf { name -> name in candidates }
            .orErr { BAD_DATA_ERROR }
    }

    override fun subcommandGroup(candidates: List<String>): ParseResult<String> {
        return e.subcommandGroup
            ?.takeIf { name -> name in candidates }
            .orErr { BAD_DATA_ERROR }
    }

    override fun subcommand(candidates: List<String>, subcommandGroup: String?, depth: Int): ParseResult<String> {
        return e.subcommandName
            ?.takeIf { name -> name in candidates }
            .orErr { BAD_DATA_ERROR }
    }

    override fun <T : Any> resolve(option: RequiredOption<DiscordContext, T>, depth: Int): ParseResult<T> {
        return e
            .getOption(option.name)
            .orErr { BAD_DATA_ERROR }
            .flatMap { mapping -> resolve(option.name, mapping, option.argument) }
    }

    override fun <T : Any> resolve(option: OptionalOption<DiscordContext, T>): ParseResult<T>? {
        return resolve(option.name, e.getOption(option.name) ?: return null, option.argument)
    }

    private fun <T : Any> resolve(name: String, opt: OptionMapping, argument: Argument<DiscordContext, T>): ParseResult<T> {
        when (argument) {
            is BooleanArgumentType<T> -> {
                if (opt.type == OptionType.BOOLEAN) {
                    return argument.resolve(name, opt.asString, opt.asBoolean)
                }
            }
            is LongArgumentType<T> -> {
                if (opt.type == OptionType.INTEGER) {
                    return argument.resolve(name, opt.asString, opt.asLong)
                }
            }
            is DoubleArgumentType<T> -> {
                if (opt.type == OptionType.NUMBER) {
                    return argument.resolve(name, opt.asString, opt.asDouble)
                }
            }
            is UserArgumentType<T> -> {
                if (opt.type == OptionType.USER) {
                    return argument.resolve(name, opt.asString, opt.asUser)
                }
            }
            is ChannelArgumentType<T> -> {
                if (opt.type == OptionType.CHANNEL) {
                    return argument.resolve(name, opt.asString, opt.asChannel)
                }
            }
            is RoleArgumentType<T> -> {
                if (opt.type == OptionType.CHANNEL) {
                    return argument.resolve(name, opt.asString, opt.asRole)
                }
            }
            is MentionableArgumentType<T> -> {
                if (opt.type == OptionType.MENTIONABLE) {
                    return argument.resolve(name, opt.asString, opt.asMentionable)
                }
            }
            is AttachmentArgumentType<T> -> {
                if (opt.type == OptionType.ATTACHMENT) {
                    return argument.resolve(name, opt.asString, opt.asAttachment)
                }
            }
            else -> {
                if (opt.type == OptionType.STRING) {
                    return argument.resolve(name, opt.asString)
                }
            }
        }
        return Err(BAD_DATA_ERROR)
    }
}

class DiscordContext(val e: SlashCommandInteractionEvent) : CommandContext {
    override fun reply(message: String) {
        e.reply(message)
    }
}
