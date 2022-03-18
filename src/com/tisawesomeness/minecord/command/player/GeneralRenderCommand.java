package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.mc.player.RenderType;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Optional;

public class GeneralRenderCommand extends AbstractPlayerCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "render",
                "Renders an image of a player.",
                "<type> <player> [<scale>] [<overlay?>]",
                null,
                2000,
                true,
                false,
                true
        );
    }

    public String getHelp() {
        return "`{&}render <type> <player> [<scale>] [<overlay?>]` - Renders an image of a player.\n" +
                "This command redirects to either `{&}avatar`, `{&}head`, or `{&}body`.\n" +
                "- `<type>` can be `avatar`, `head`, or `body`.\n" +
                "- `<player>` can be a username or a UUID.\n" +
                "- `[<scale>]` changes the image size.\n" +
                "- `[<overlay?>]` is whether to include the second skin layer, defaults to true.\n" +
                "\n" +
                "If the type is `head` or `body`, the scale can be from 1 to " + RenderType.MAX_SCALE + ", defaults to " + RenderType.DEFAULT_SCALE + ".\n" +
                "If the type is `avatar`, the scale can be from 1 to " + RenderType.MAX_SIZE + ", defaults to " + RenderType.DEFAULT_SIZE + ".\n" +
                "\n" +
                "Use `{&}help usernameInput|uuidInput|phd` for more help.\n" +
                "Note that Crafatar caches images for 20-60 minutes.\n" +
                "\n" +
                "- `{&}render avatar Tis_awesomeness`\n" +
                "- `{&}render head LadyAgnes true`\n" +
                "- `{&}render avatar f6489b797a9f49e2980e265a05dbc3af 256`\n" +
                "- `{&}render head 069a79f4-44e9-4726-a5be-fca90e38aaf5 10 overlay`\n";
    }

    public Result run(String[] args, MessageReceivedEvent e) {
        if (args.length == 0) {
            return new Result(Outcome.WARNING, ":warning: You must specify a render type.");
        }
        if (args.length == 1) {
            return new Result(Outcome.WARNING, ":warning: You must specify a player.");
        }

        Optional<RenderType> typeOpt = RenderType.from(args[0]);
        if (!typeOpt.isPresent()) {
            return new Result(Outcome.WARNING, ":warning: The render type must be `avatar`, `head`, or `body`.");
        }
        RenderType type = typeOpt.get();
        return RenderCommand.parseAndSendRender(e, type, "render", args, 1);
    }

}
