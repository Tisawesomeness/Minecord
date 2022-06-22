package com.tisawesomeness.minecord.command.discord;

import com.tisawesomeness.minecord.command.meta.Category;
import com.tisawesomeness.minecord.command.meta.Command;

public abstract class AbstractDiscordCommand extends Command {
    @Override
    public final Category getCategory() {
        return Category.DISCORD;
    }
}
