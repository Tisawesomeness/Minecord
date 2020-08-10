package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.Module;

public abstract class AbstractPlayerCommand extends Command {
    @Override
    public final Module getModule() {
        return Module.PLAYER;
    }
}
