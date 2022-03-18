package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.mc.player.RenderType;
import com.tisawesomeness.minecord.util.ColorUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.StringUtils;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.Color;
import java.util.List;

public class HistoryCommand extends BasePlayerCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "history",
                "Shows a player's name history.",
                "<player>",
                new String[]{"h", "hist", "namehist", "namehistory"},
                2000,
                false,
                false,
                true
        );
    }

    public String getHelp() {
        return "`{&}history <player>` - Shows a player''s name history.\n" +
                "- `<player>` can be a username or UUID.\n" +
                "Use `{&}help usernameInput|uuidInput|phd` for more help.\n" +
                "\n" +
                "Examples:\n" +
                "- `{&}history Tis_awesomeness`\n" +
                "- `{&}history LadyAgnes`\n" +
                "- `{&}history f6489b797a9f49e2980e265a05dbc3af`\n" +
                "- `{&}history 069a79f4-44e9-4726-a5be-fca90e38aaf5`\n";
    }

    protected boolean shouldRejectPHD() {
        return false;
    }

    protected void onSuccessfulPlayer(MessageReceivedEvent e, Player player) {
        List<String> historyLines = buildHistoryLines(player.getNameHistory());
        if (player.isPHD()) {
            historyLines.add(0, "**This player is pseudo hard-deleted (PHD)!**");
        }
        List<String> historyPartitions = StringUtils.partitionLinesByLength(
                historyLines, MessageEmbed.VALUE_MAX_LENGTH);

        MessageEmbed baseEmbed = constructBaseEmbed(player);
        List<MessageEmbed> embeds = MessageUtils.splitEmbeds(baseEmbed, "Name History", historyPartitions, "\n");
        for (MessageEmbed emb : embeds) {
            e.getChannel().sendMessageEmbeds(emb).queue();
        }
    }

    private static @NonNull MessageEmbed constructBaseEmbed(Player player) {
        String title = "Name History for " + player.getUsername();
        String nameMCUrl = player.getNameMCUrl().toString();
        String avatarUrl = player.createRender(RenderType.AVATAR, true).render().toString();
        Color color = player.isRainbow() ? ColorUtils.randomColor() : Bot.color;
        return MessageUtils.addFooter(new EmbedBuilder())
                .setAuthor(title, nameMCUrl, avatarUrl)
                .setColor(color)
                .build();
    }

}
