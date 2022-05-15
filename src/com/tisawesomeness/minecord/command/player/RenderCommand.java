package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.mc.player.Render;
import com.tisawesomeness.minecord.mc.player.RenderType;
import com.tisawesomeness.minecord.mc.player.Username;
import com.tisawesomeness.minecord.util.ColorUtils;
import com.tisawesomeness.minecord.util.MathUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.StringUtils;
import com.tisawesomeness.minecord.util.type.Either;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.Color;
import java.util.OptionalInt;

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
                null,
                2000,
                false,
                false,
                true
        );
    }

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

    public Result run(String[] args, MessageReceivedEvent e) {
        if (args.length == 0) {
            return new Result(Outcome.WARNING, ":warning: You must specify a player.");
        }
        return parseAndSendRender(e, type, args,0);
    }

    protected Either<String, BaseRenderCommand.ImpersonalRender> parseRender(RenderType type, String[] args,
                                                                             int argsUsed, int playerArgIndex) {
        return parseRenderFromArgs(type, args, argsUsed, playerArgIndex);
    }
    protected static Either<String, ImpersonalRender> parseRenderFromArgs(RenderType type, String[] args,
                                                                          int argsUsed, int playerArgIndex) {
        int currentArg = argsUsed;

        if (currentArg + 2 < args.length) {
            String msg = String.format("This command takes up to %d arguments.", playerArgIndex + 3);
            return Either.left(msg);
        }

        int scale = type.getDefaultScale();
        boolean overlay = DEFAULT_OVERLAY;
        // Each argument type should only be processed once
        boolean scaleSet = false;
        boolean overlaySet = false;

        // [<scale>] and [<overlay?>] can be in any order
        while (currentArg < args.length) {
            String arg = args[currentArg++]; // Current arg is incremented for next loop no matter what route

            if (!overlaySet) {
                if (arg.equalsIgnoreCase("Overlay") || StringUtils.isTruthy(arg)) {
                    overlay = true;
                    overlaySet = true;
                    continue;
                } else if (StringUtils.isFalsy(arg)) {
                    overlay = false;
                    overlaySet = true;
                    continue;
                } else if (scaleSet) {
                    // If the scale was already processed, we know this argument is the overlay
                    return Either.left(arg + " is not a valid true/false value.");
                }
            }

            OptionalInt scaleOpt = MathUtils.safeParseInt(arg);
            if (scaleOpt.isPresent()) {
                int potentialScale = Integer.parseInt(arg);
                if (potentialScale < 1) {
                    String msg = String.format("The scale must be from 1 to %d.", type.getMaxScale());
                    return Either.left(msg);
                }
                scale = potentialScale;
                scaleSet = true;
                continue;
            } else if (overlaySet) {
                // If the overlay was already processed, we know this argument is the scale
                return Either.left(arg + " is not a valid number.");
            }

            // If reached, all attempts to process arguments failed
            return Either.left(arg + " is not a valid true/false value or number.");
        }
        return Either.right(new ImpersonalRender(type, overlay, scale));
    }

    protected void onSuccessfulRender(MessageReceivedEvent e, Username username, Render render) {
        sendRenderEmbed(e, username, render);
    }
    protected static void sendRenderEmbed(MessageReceivedEvent e, Username username, Render render) {
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
        e.getChannel().sendMessageEmbeds(eb.build()).queue();
    }

}
