package com.tisawesomeness.minecord.command.misc;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.Module;

public abstract class AbstractMiscCommand extends Command {
    @Override
    public final Module getModule() {
        return Module.MISC;
    }
}
