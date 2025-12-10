package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.mc.player.RenderType;
import com.tisawesomeness.minecord.util.ColorUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;

public class HistoryCommand extends BasePlayerCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "history",
                "Shows a player's name history.",
                "<player>",
                1000,
                false,
                false
        );
    }

    @Override
    public String[] getLegacyAliases() {
        return new String[]{"h", "hist", "namehist", "namehistory"};
    }

    @Override
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

    protected void onSuccessfulPlayer(SlashCommandInteractionEvent e, Player player) {
        String title = "Name History for " + player.getUsername();
        String nameMCUrl = player.getNameMCUrl().toString();
        String avatarUrl = player.createRender(RenderType.AVATAR, true).render().toString();
        Color color = player.isRainbow() ? ColorUtils.randomColor() : Bot.color;
        EmbedBuilder eb = MessageUtils.addFooter(new EmbedBuilder())
                .setAuthor(title, nameMCUrl, avatarUrl)
                .setColor(color)
                .setDescription("Mojang has removed the name history API, [read more here](https://help.minecraft.net/hc/en-us/articles/8969841895693). We are working on a different way to retrieve name history, stay tuned.\nUse `/profile` to look up a player's other information");
        e.getHook().sendMessageEmbeds(eb.build()).queue();
    }

}
