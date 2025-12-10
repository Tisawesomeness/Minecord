package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.OptionTypes;
import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.mc.player.Render;
import com.tisawesomeness.minecord.mc.player.RenderType;
import com.tisawesomeness.minecord.mc.player.Username;
import com.tisawesomeness.minecord.util.ColorUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.awt.*;

public class RenderCommand extends BaseRenderCommand {

    /** Whether render overlay is enabled by default */
    public static final boolean DEFAULT_OVERLAY = true;

    private final String id;
    private final RenderType type;
    public RenderCommand(RenderType type) {
        id = type.getId();
        this.type = type;
    }

    public CommandInfo getInfo() {
        return new CommandInfo(
                id,
                String.format("Shows an image of the player's %s.", type.getId()),
                "<player> [<scale>] [<overlay?>]",
                1000,
                false,
                false
        );
    }

    @Override
    public SlashCommandData addCommandSyntax(SlashCommandData builder) {
        return super.addCommandSyntax(builder)
                .addOptions(new OptionData(OptionType.INTEGER, "scale", "The scale of the image, defaults to " + type.getDefaultScale())
                        .setRequiredRange(1, type.getMaxScale()))
                .addOption(OptionType.BOOLEAN, "overlay", "Whether to show the overlay, defaults to " + DEFAULT_OVERLAY);
    }

    @Override
    public String getHelp() {
        return "`{&}" + id + " <player> [<scale>] [<overlay?>]` - Shows an image of the player's " + type.getId() + ".\n" +
                "- `<player>` can be a username or a UUID.\n" +
                "- `[<scale>]` changes the image size, can be from 1 to " + type.getMaxScale() + ", defaults to " + type.getDefaultScale() + ".\n" +
                "- `[<overlay?>]` is whether to include the second skin layer, defaults to true.\n" +
                "Use `{&}help usernameInput|uuidInput|phd` for more help.\n" +
                "Note that Crafatar caches images for 20-60 minutes.\n" +
                "\n" +
                "- `{&}" + id + " avatar Tis_awesomeness`\n" +
                "- `{&}" + id + " head LadyAgnes true`\n" +
                "- `{&}" + id + " avatar f6489b797a9f49e2980e265a05dbc3af 256`\n" +
                "- `{&}" + id + " head 069a79f4-44e9-4726-a5be-fca90e38aaf5 10 overlay`\n";
    }

    public Result run(SlashCommandInteractionEvent e) {
        return parseAndSendRender(e, type);
    }

    protected ImpersonalRender parseRender(RenderType type, SlashCommandInteractionEvent e) {
        return parseRenderFromArgs(type, e);
    }
    protected static ImpersonalRender parseRenderFromArgs(RenderType type, SlashCommandInteractionEvent e) {
        int scale = getOption(e, "scale", type.getDefaultScale(), OptionTypes.INTEGER);
        boolean overlay = getOption(e, "overlay", DEFAULT_OVERLAY, OptionTypes.BOOLEAN);
        return new ImpersonalRender(type, overlay, scale);
    }

    protected void onSuccessfulRender(SlashCommandInteractionEvent e, Username username, Render render) {
        sendRenderEmbed(e, username, render);
    }
    protected void sendRenderEmbed(SlashCommandInteractionEvent e, Username username, Render render) {
        RenderType type = render.getType();

        Color color = Player.isRainbow(username) ? ColorUtils.randomColor() : Bot.color;
        String title = type + " for " + username;
        EmbedBuilder eb = MessageUtils.addFooter(new EmbedBuilder())
                .setTitle(title)
                .setImage(render.render().toString())
                .setColor(color);
        if (render.getProvidedScale() > type.getMaxScale()) {
            String msg = String.format("The scale was too high, so it was set to the max, %d.", type.getMaxScale());
            eb.setDescription(msg);
        }
        uploadOrEmbedImages(e, eb.build());
    }

}
