package codes.tis.minecord.command.argument

import codes.tis.minecord.TranslationKey
import codes.tis.minecord.command.DiscordContext
import codes.tis.minecord.command.tree.Argument
import codes.tis.minecord.command.tree.ParseResult
import codes.tis.minecord.util.Err
import net.dv8tion.jda.api.entities.IMentionable
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.Channel

interface MentionableArgumentType<out T : Any> : Argument<DiscordContext, T> {
    override fun resolve(name: String, raw: String): ParseResult<T> = Err(TranslationKey("minecord.error.discord_only"))

    fun resolve(name: String, raw: String, mentionable: IMentionable?): ParseResult<T>
}

interface UserArgumentType<out T : Any> : MentionableArgumentType<T> {
    override fun resolve(name: String, raw: String, mentionable: IMentionable?) = resolve(name, raw, mentionable as? User)

    fun resolve(name: String, raw: String, user: User?): ParseResult<T>
}

interface ChannelArgumentType<out T : Any> : MentionableArgumentType<T> {
    override fun resolve(name: String, raw: String, mentionable: IMentionable?) = resolve(name, raw, mentionable as? Channel)

    fun resolve(name: String, raw: String, channel: Channel?): ParseResult<T>
}

interface RoleArgumentType<out T : Any> : MentionableArgumentType<T> {
    override fun resolve(name: String, raw: String, mentionable: IMentionable?) = resolve(name, raw, mentionable as? Role)

    fun resolve(name: String, raw: String, role: Role?): ParseResult<T>
}

interface AttachmentArgumentType<out T : Any> : Argument<DiscordContext, T> {
    override fun resolve(name: String, raw: String): ParseResult<T> = Err(TranslationKey("minecord.error.discord_only"))

    fun resolve(name: String, raw: String, attachment: Message.Attachment?): ParseResult<T>
}
