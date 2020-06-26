package com.tisawesomeness.minecord.listen;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.util.DiscordUtils;
import com.tisawesomeness.minecord.util.RequestUtils;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.List;

@RequiredArgsConstructor
public class GuildCountListener extends ListenerAdapter {

    private final @NonNull Bot bot;
    private final @NonNull Config config;

    @Override
    public void onGuildJoin(GuildJoinEvent e) {
        Guild guild = e.getGuild();
        Member owner = guild.getOwner();
        List<Member> members = guild.getMembers();
        long size = members.stream()
                .map(m -> m.getUser())
                .filter(u -> !u.isBot() && !u.isFake())
                .count();
        EmbedBuilder eb = new EmbedBuilder()
                .setAuthor("Joined guild!", null, owner.getUser().getAvatarUrl())
                .addField("Name", guild.getName(), true)
                .addField("Guild ID", guild.getId(), true)
                .addField("Owner", owner.getEffectiveName(), true)
                .addField("Owner ID", owner.getUser().getId(), true)
                .addField("Users", String.valueOf(members.size()), true)
                .addField("Humans", String.valueOf(size), true)
                .addField("Bots", String.valueOf(members.size() - size), true)
                .addField("Channels", String.valueOf(guild.getTextChannels().size()), true);
        updateGuilds(eb, guild, e.getJDA().getShardManager());
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent e) {
        Guild guild = e.getGuild();
        User owner = guild.getOwner().getUser();
        EmbedBuilder eb = new EmbedBuilder()
                .setAuthor(owner.getAsTag(), null, owner.getAvatarUrl())
                .setDescription(String.format("Left guild %s (`%s`)", guild.getName(), guild.getId()));
        updateGuilds(eb, guild, e.getJDA().getShardManager());
    }

    private void updateGuilds(EmbedBuilder eb, Guild guild, ShardManager sm) {
        eb.setThumbnail(guild.getIconUrl());
        bot.log(eb.build());
        RequestUtils.sendGuilds(sm, config);
        DiscordUtils.update(sm, config); // Update guild, channel, and user count
    }

}
