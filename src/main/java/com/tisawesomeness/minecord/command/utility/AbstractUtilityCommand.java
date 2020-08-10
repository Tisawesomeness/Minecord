package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.Module;

public abstract class AbstractUtilityCommand extends Command {
    @Override
    public final Module getModule() {
        return Module.UTILITY;
    }
}
