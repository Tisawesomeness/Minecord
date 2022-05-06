package com.tisawesomeness.minecord.command.config;

import com.tisawesomeness.minecord.command.meta.Category;
import com.tisawesomeness.minecord.command.meta.Command;

public abstract class AbstractConfigCommand extends Command {
    @Override
    public final Category getCategory() {
        return Category.CONFIG;
    }
}
