package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.command.meta.CommandContext;
import com.tisawesomeness.minecord.command.meta.IHiddenCommand;
import com.tisawesomeness.minecord.config.config.Config;
import com.tisawesomeness.minecord.lang.Lang;
import com.tisawesomeness.minecord.mc.player.RenderType;

import lombok.NonNull;

import java.util.Optional;

public class GeneralRenderCommand extends AbstractPlayerCommand implements IHiddenCommand {

    public @NonNull String getId() {
        return "render";
    }

    @Override
    public Object[] getHelpArgs(String prefix, String tag, Config config) {
        return new Object[]{prefix, tag, RenderType.MAX_SCALE, RenderType.DEFAULT_SCALE,
                RenderType.MAX_SIZE, RenderType.DEFAULT_SIZE};
    }

    public void run(String[] args, CommandContext ctx) {
        if (args.length <= 1) {
            ctx.showHelp();
            return;
        }

        Optional<RenderType> typeOpt = from(args[0], ctx.getLang());
        if (!typeOpt.isPresent()) {
            ctx.invalidArgs(ctx.i18n("invalidRenderType"));
            return;
        }
        RenderType type = typeOpt.get();
        RenderCommand.parseAndSendRender(ctx, type, 1);
    }

    private static Optional<RenderType> from(String str, Lang lang) {
        for (RenderType rt : RenderType.values()) {
            String key = String.format("command.player.%s.name", rt.getId());
            if (lang.equalsIgnoreCase(str, lang.i18n(key))) {
                return Optional.of(rt);
            }
        }
        return Optional.empty();
    }

}
