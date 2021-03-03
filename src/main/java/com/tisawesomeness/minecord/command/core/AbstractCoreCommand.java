package com.tisawesomeness.minecord.command.core;

import com.tisawesomeness.minecord.command.Category;
import com.tisawesomeness.minecord.command.Command;

public abstract class AbstractCoreCommand extends Command {
    @Override
    public final Category getCategory() {
        return Category.CORE;
    }
}
