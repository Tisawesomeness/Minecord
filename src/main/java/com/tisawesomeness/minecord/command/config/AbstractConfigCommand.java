package com.tisawesomeness.minecord.command.config;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.Category;

public abstract class AbstractConfigCommand extends Command {
    @Override
    public final Category getCategory() {
        return Category.CONFIG;
    }
}
