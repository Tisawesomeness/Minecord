package com.tisawesomeness.minecord;

import com.tisawesomeness.minecord.util.DiscordUtils;
import com.tisawesomeness.minecord.util.RequestUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.ArrayList;

public class Listener extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent e) {
        Bot.readyShards++;
    }

    @Override
    public void onGenericGuild(GenericGuildEvent e) {

        //Get guild info
        EmbedBuilder eb = new EmbedBuilder();
        Guild guild = e.getGuild();
        Member owner = guild.getOwner();

        //Create embed
        if (e instanceof GuildJoinEvent) {

            String avatarUrl = owner == null ? null : owner.getUser().getAvatarUrl();
            eb.setAuthor("Joined guild!", null, avatarUrl);
            eb.addField("Name", guild.getName(), true);
            eb.addField("Guild ID", guild.getId(), true);
            if (owner != null) {
                eb.addField("Owner", owner.getEffectiveName(), true);
                eb.addField("Owner ID", owner.getUser().getId(), true);
            }
            eb.addField("Users", String.valueOf(guild.getMembers().size()), true);
            ArrayList<Member> users = new ArrayList<>(guild.getMembers());
            users.removeIf(u -> u.getUser().isBot());
            eb.addField("Humans", String.valueOf(users.size()), true);
            eb.addField("Bots", String.valueOf(guild.getMembers().size() - users.size()), true);
            eb.addField("Channels", String.valueOf(guild.getTextChannels().size()), true);

        } else if (e instanceof GuildLeaveEvent) {
            if (owner != null) {
                eb.setAuthor(owner.getEffectiveName() + " (" + owner.getUser().getId() + ")",
                        null, owner.getUser().getAvatarUrl());
            }
            eb.setDescription("Left guild `" + guild.getName() + "` (" + guild.getId() + ")");
        } else {
            return;
        }

        eb.setThumbnail(guild.getIconUrl());
        Bot.logger.joinLog(MessageCreateData.fromEmbeds(eb.build()));
        RequestUtils.sendGuilds();
        DiscordUtils.update(); //Update guild, channel, and user count

    }

}
