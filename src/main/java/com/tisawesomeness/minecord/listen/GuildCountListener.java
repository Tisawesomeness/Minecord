package com.tisawesomeness.minecord.listen;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.config.serial.Config;
import com.tisawesomeness.minecord.service.BotListService;
import com.tisawesomeness.minecord.service.PresenceService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

@RequiredArgsConstructor
public class GuildCountListener extends ListenerAdapter {

    private final @NonNull Bot bot;
    private final @NonNull Config config;
    private final @NonNull PresenceService presenceService;
    private final @NonNull BotListService botListService;

    @Override
    public void onGuildJoin(GuildJoinEvent e) {
        Guild guild = e.getGuild();
        Member owner = guild.getOwner();
        List<Member> members = guild.getMembers();
        long size = members.stream()
                .map(Member::getUser)
                .filter(u -> !u.isBot() && !u.isFake())
                .count();
        EmbedBuilder eb = new EmbedBuilder()
                .addField("Name", guild.getName(), true)
                .addField("Guild ID", guild.getId(), true);
        if (owner != null) {
            eb.setAuthor("Joined guild!", null, owner.getUser().getAvatarUrl())
                    .addField("Owner", owner.getEffectiveName(), true)
                    .addField("Owner ID", owner.getUser().getId(), true);
        }
        eb.addField("Users", String.valueOf(members.size()), true)
                .addField("Humans", String.valueOf(size), true)
                .addField("Bots", String.valueOf(members.size() - size), true)
                .addField("Channels", String.valueOf(guild.getTextChannels().size()), true);
        updateGuilds(eb, guild);
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent e) {
        Guild guild = e.getGuild();
        Member owner = guild.getOwner();
        EmbedBuilder eb = new EmbedBuilder()
                .setDescription(String.format("Left guild %s (`%s`)", guild.getName(), guild.getId()));
        if (owner != null) {
            eb.setAuthor(owner.getUser().getAsTag(), null, owner.getUser().getAvatarUrl());
        }
        updateGuilds(eb, guild);
    }

    private void updateGuilds(EmbedBuilder eb, Guild guild) {
        eb.setThumbnail(guild.getIconUrl());
        bot.log(eb.build());
        if (config.getPresenceConfig().getChangeInterval() == -1) {
            presenceService.run();
        }
        if (config.getBotListConfig().getSendGuildsInterval() == -1) {
            botListService.run();
        }
    }

}
