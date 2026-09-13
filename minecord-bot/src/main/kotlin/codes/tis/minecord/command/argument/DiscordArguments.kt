@file:Suppress("FileName")

package codes.tis.minecord.command.argument

import codes.tis.minecord.TranslationKey
import codes.tis.minecord.util.orErr
import net.dv8tion.jda.api.entities.IMentionable
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.Channel

object MentionableArgument : MentionableArgumentType<IMentionable> {
    override val name = "Mentionable"

    override fun resolve(name: String, raw: String, mentionable: IMentionable?) = mentionable.orErr {
        TranslationKey("minecord.error.invalid_mentionable").format(name, raw)
    }
}

object UserArgument : UserArgumentType<User> {
    override val name = "User"

    override fun resolve(name: String, raw: String, user: User?) = user.orErr {
        TranslationKey("minecord.error.invalid_user").format(name, raw)
    }
}

object ChannelArgument : ChannelArgumentType<Channel> {
    override val name = "Channel"

    override fun resolve(name: String, raw: String, channel: Channel?) = channel.orErr {
        TranslationKey("minecord.error.invalid_channel").format(name, raw)
    }
}

object RoleArgument : RoleArgumentType<Role> {
    override val name = "Role"

    override fun resolve(name: String, raw: String, role: Role?) = role.orErr {
        TranslationKey("minecord.error.invalid_role").format(name, raw)
    }
}

object AttachmentArgument : AttachmentArgumentType<Message.Attachment> {
    override val name = "Attachment"

    override fun resolve(name: String, raw: String, attachment: Message.Attachment?) = attachment.orErr {
        TranslationKey("minecord.error.invalid_attachment").format(name)
    }
}
