package com.tisawesomeness.minecord.command.config;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.Module;

public abstract class AbstractConfigCommand extends Command {
    public Module getModule() {
        return Module.CONFIG;
    }
}
