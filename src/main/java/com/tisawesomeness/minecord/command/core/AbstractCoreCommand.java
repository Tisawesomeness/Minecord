package com.tisawesomeness.minecord.command.core;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.Module;

public abstract class AbstractCoreCommand extends Command {
    @Override
    public final Module getModule() {
        return Module.CORE;
    }
}
