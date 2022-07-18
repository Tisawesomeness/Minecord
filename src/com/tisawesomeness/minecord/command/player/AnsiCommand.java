package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.mc.player.Render;
import com.tisawesomeness.minecord.mc.player.RenderType;
import com.tisawesomeness.minecord.mc.player.Username;
import com.tisawesomeness.minecord.util.ColorUtils;
import com.tisawesomeness.minecord.util.StringUtils;
import com.tisawesomeness.minecord.util.type.Either;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.StringJoiner;

public class AnsiCommand extends BaseRenderCommand {

    private static final int IMAGE_SIZE = 8;

    public CommandInfo getInfo() {
        return new CommandInfo(
                "ansi",
                "Converts a player's avatar to a colored text message.",
                "<player> [<overlay?>]",
                null,
                3000,
                false,
                false,
                true
        );
    }

    public String getHelp() {
        return "`{&}ansi <player> [<overlay?>]` - Converts a player's avatar to a colored text message using Discord ANSI code blocks.\n" +
                "Note that only [limited colors](https://gist.github.com/kkrypt0nn/a02506f3712ff2d1c8ca7c9e0aed7c06) are available.\n" +
                "- `<player>` can be a username or a UUID.\n" +
                "- `[<overlay?>]` is whether to include the second skin layer, defaults to true.\n" +
                "Use `{&}help usernameInput|uuidInput|phd` for more help.\n" +
                "Note that Crafatar caches images for 20-60 minutes.\n" +
                "\n" +
                "- `{&}ansi Tis_awesomeness`\n" +
                "- `{&}ansi LadyAgnes true`\n" +
                "- `{&}ansi f6489b797a9f49e2980e265a05dbc3af`\n" +
                "- `{&}ansi 069a79f4-44e9-4726-a5be-fca90e38aaf5 overlay`\n";
    }

    public Result run(String[] args, MessageReceivedEvent e) {
        if (args.length == 0) {
            return new Result(Outcome.WARNING, ":warning: You must specify a player.");
        }
        return parseAndSendRender(e, RenderType.AVATAR, args,0);
    }

    protected Either<String, ImpersonalRender> parseRender(RenderType type, String[] args,
                                                           int argsUsed, int playerArgIndex) {
        if (argsUsed + 1 < args.length) {
            return Either.left("This command takes up to 2 arguments.");
        }
        boolean overlay = RenderCommand.DEFAULT_OVERLAY;
        if (argsUsed + 1 == args.length) {
            String arg = args[argsUsed];
            if (arg.equalsIgnoreCase("Overlay") || StringUtils.isTruthy(arg)) {
                overlay = true;
            } else if (StringUtils.isFalsy(arg)) {
                overlay = false;
            } else {
                return Either.left(arg + " is not a valid true/false value.");
            }
        }

        return Either.right(new ImpersonalRender(RenderType.AVATAR, overlay, IMAGE_SIZE));
    }

    protected void onSuccessfulRender(MessageReceivedEvent e, Username username, Render render) {
        URL url = render.render();
        try {
            BufferedImage img = ImageIO.read(url);
            if (img.getWidth() != IMAGE_SIZE || img.getHeight() != IMAGE_SIZE) {
                e.getChannel().sendMessage(":x: There was an error downloading the avatar.").queue();
                return;
            }
            e.getChannel().sendMessage(buildAnsiMessage(img, username)).queue();
        } catch (IOException ex) {
            e.getChannel().sendMessage(":x: There was an error downloading the avatar.").queue();
        }
    }
    private static String buildAnsiMessage(BufferedImage img, Username username) {
        StringJoiner sj = new StringJoiner("\n");
        for (int y = 0; y < IMAGE_SIZE; y++) {
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < IMAGE_SIZE; x++) {
                int rgb = Player.isUpsideDown(username) ? img.getRGB(x, IMAGE_SIZE - y - 1) : img.getRGB(x, y);
                Color color = new Color(rgb);
                sb.append(ColorUtils.nearestAnsiColorCode(color));
            }
            sj.add(sb.toString());
        }
        return MarkdownUtil.codeblock("ansi", sj.toString());
    }

}