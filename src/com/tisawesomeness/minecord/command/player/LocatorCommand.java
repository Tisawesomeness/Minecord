package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.command.utility.ColorCommand;
import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.util.ColorUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;

public class LocatorCommand extends BasePlayerCommand {

    @Override
    public CommandInfo getInfo() {
        return new CommandInfo(
                "locator",
                "Shows a player's name history.",
                "<player>",
                1000,
                false,
                false
        );
    }

    @Override
    public String getHelp() {
        return "`{&}locator <player>` - Shows a player's default locator bar color.\n" +
                "- `<player>` can be a username or UUID.\n" +
                "Use `{&}help usernameInput|uuidInput|phd` for more help.\n" +
                "\n" +
                "Examples:\n" +
                "- `{&}locator Tis_awesomeness`\n" +
                "- `{&}locator LadyAgnes`\n" +
                "- `{&}locator f6489b797a9f49e2980e265a05dbc3af`\n" +
                "- `{&}locator 069a79f4-44e9-4726-a5be-fca90e38aaf5`\n";
    }

    @Override
    protected void onSuccessfulPlayer(SlashCommandInteractionEvent e, Player player) {
        Color locatorBarColor = ColorUtils.getDefaultLocatorBarColor(player.getUuid());
        EmbedBuilder eb = ColorCommand.buildEmbed(player.getUsername() + " Default Locator Bar Color", locatorBarColor);
        e.getHook().sendMessageEmbeds(MessageUtils.addFooter(eb).build()).queue();
    }

}
