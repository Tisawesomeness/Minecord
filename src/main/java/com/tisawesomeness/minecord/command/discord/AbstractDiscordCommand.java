package com.tisawesomeness.minecord.command.discord;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.Module;

public abstract class AbstractDiscordCommand extends Command {
    @Override
    public Module getModule() {
        return Module.DISCORD;
    }
}
