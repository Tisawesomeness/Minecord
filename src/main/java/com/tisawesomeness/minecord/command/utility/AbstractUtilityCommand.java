package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.Module;

public abstract class AbstractUtilityCommand extends Command {
    public Module getModule() {
        return Module.UTILITY;
    }
}
