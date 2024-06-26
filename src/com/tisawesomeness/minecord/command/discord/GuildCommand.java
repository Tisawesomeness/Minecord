package com.tisawesomeness.minecord.command.discord;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.SlashCommand;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Guild.BoostTier;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import net.dv8tion.jda.api.utils.TimeFormat;
import net.dv8tion.jda.api.utils.TimeUtil;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.concurrent.CompletableFuture;

public class GuildCommand extends SlashCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "guild",
                "Shows guild info.",
                null,
                0,
                false,
                false
        );
    }

    @Override
    public String[] getLegacyAliases() {
        return new String[]{"guildinfo"};
    }

    public Result run(SlashCommandInteractionEvent e) {
        if (!e.isFromGuild()) {
            return new Result(Outcome.WARNING, ":warning: This command is not available in DMs.");
        }
        buildReply(e.getGuild(), false)
                .thenAccept(eb -> sendSuccess(e, MessageCreateData.fromEmbeds(eb.build())));
        return new Result(Outcome.SUCCESS);
    }

    public static CompletableFuture<EmbedBuilder> buildReply(Guild g, boolean elevated) {
        return g.retrieveOwner().submit()
                .thenApply(m -> buildReply(g, m.getUser(), elevated));
    }

    private static EmbedBuilder buildReply(Guild g, User owner, boolean elevated) {
        // Generate guild info
        int textChannels = g.getTextChannels().size();
        int voiceChannels = g.getVoiceChannels().size();
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(MarkdownSanitizer.escape(g.getName()))
                .setColor(Bot.color)
                .addField("ID", g.getId(), true)
                .addField("Users", String.valueOf(g.getMemberCount()), true)
                .addField("Roles", String.valueOf(g.getRoles().size()), true)
                .addField("Categories", String.valueOf(g.getCategories().size()), true)
                .addField("Channels", String.format("%d (%d text, %d voice)", textChannels + voiceChannels, textChannels, voiceChannels), true)
                .addField("Verification Level", g.getVerificationLevel().toString(), true)
                .addField("Owner", MarkdownSanitizer.escape(owner.getEffectiveName()), true)
                .addField("Owner ID", owner.getId(), true)
                .addField("Created", TimeFormat.RELATIVE.format(TimeUtil.getTimeCreated(g)), true);
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
            eb.addField("Settings", getSettingsStr(g.getIdLong()), false);
            if (Database.isBanned(g.getIdLong())) {
                eb.setDescription("__**GUILD BANNED FROM MINECORD**__");
            }
        }

        return MessageUtils.addFooter(eb);
    }

    public static String getSettingsStr(long gid) {
        return String.format("prefix: `%s`\ndeleteCommands: `%s`\nuseMenus: `%s`",
                Database.getPrefix(gid), Database.getDeleteCommands(gid), Database.getUseMenu(gid));
    }

}
