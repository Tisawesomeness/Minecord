package com.tisawesomeness.minecord.command.discord;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.database.dao.DbGuild;
import com.tisawesomeness.minecord.setting.Setting;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.DiscordUtils;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Guild.BoostTier;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import net.dv8tion.jda.api.utils.TimeUtil;

import java.util.stream.Collectors;

public class GuildCommand extends AbstractDiscordCommand {

    public @NonNull String getId() {
        return "guild";
    }
    public CommandInfo getInfo() {
		return new CommandInfo(
                false,
                false,
                false
		);
    }
    
    public Result run(CommandContext ctx) {
        String[] args = ctx.args;

        // If the author used the admin keyword and is an elevated user
        boolean elevated = false;
        Guild g;
		if (args.length > 1 && args[1].equals("admin") && ctx.isElevated) {
            elevated = true;
            if (!DiscordUtils.isDiscordId(args[0])) {
                return new Result(Outcome.WARNING, ":warning: Not a valid ID!");
            }
            g = ctx.bot.getShardManager().getGuildById(args[0]);
            if (g == null) {
                long gid = Long.valueOf(args[0]);
                DbGuild dbGuild = ctx.getGuild(gid);
                if (dbGuild.isBanned()) {
                    return new Result(Outcome.SUCCESS,
                            "__**GUILD BANNED FROM MINECORD**__\n" + getSettingsStr(dbGuild, ctx));
                }
                return new Result(Outcome.SUCCESS, getSettingsStr(dbGuild, ctx));
            }
        } else {
            if (!ctx.e.isFromGuild()) {
                return new Result(Outcome.WARNING, ":warning: This command is not available in DMs.");
            }
            g = ctx.e.getGuild();
        }
        User owner = g.retrieveOwner().complete().getUser();
		DbGuild dbGuild = ctx.getGuild(g);

        // Generate guild info
        int textChannels = g.getTextChannels().size();
        int voiceChannels = g.getVoiceChannels().size();
        EmbedBuilder eb = new EmbedBuilder()
            .setTitle(MarkdownSanitizer.escape(g.getName()))
            .setImage(g.getIconUrl())
            .addField("ID", g.getId(), true)
            .addField("Users", String.valueOf(g.getMemberCount()), true)
            .addField("Roles", String.valueOf(g.getRoles().size()), true)
            .addField("Categories", String.valueOf(g.getCategories().size()), true)
            .addField("Channels", String.format("%d (%d text, %d voice)", textChannels + voiceChannels, textChannels, voiceChannels), true)
            .addField("Region", g.getRegion().getName(), true)
            .addField("Verification Level", g.getVerificationLevel().toString(), true)
            .addField("Owner", MarkdownSanitizer.escape(owner.getAsTag()), true)
            .addField("Owner ID", owner.getId(), true)
            .addField("Created", DateUtils.getDateAgo(TimeUtil.getTimeCreated(g)), false);
         if (g.getBoostTier() == BoostTier.UNKNOWN) {
            eb.addField("Boosts", g.getBoostCount() + " (Unknown Tier)", true);
        } else {
            eb.addField("Boosts", String.format("%d (Tier %s)", g.getBoostCount(), g.getBoostTier().getKey()), true);
        }
        if (g.getVanityCode() != null) {
            eb.addField("Vanity Code", g.getVanityCode(), true);
        }
        if (g.getDescription() != null) {
            eb.addField("Description", MarkdownSanitizer.escape(g.getDescription()), false);
        }
        if (elevated) {
            eb.addField("Settings", getSettingsStr(dbGuild, ctx), false);
            if (dbGuild.isBanned()) {
                eb.setDescription("__**GUILD BANNED FROM MINECORD**__");
            }
        }
        return new Result(Outcome.SUCCESS, ctx.brand(eb).build());
    }

    private static String getSettingsStr(DbGuild guild, CommandContext ctx) {
        return ctx.bot.getSettings().stream()
                .map(s -> s.getDisplayName() + ": " + displaySetting(guild, s))
                .collect(Collectors.joining("\n"));
    }
    private static String displaySetting(DbGuild guild, Setting<?> setting) {
        return MarkdownUtil.monospace(setting.get(guild).map(Object::toString).orElse("unset"));
    }
    
}