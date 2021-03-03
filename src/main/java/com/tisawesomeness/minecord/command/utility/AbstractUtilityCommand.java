package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.Category;

public abstract class AbstractUtilityCommand extends Command {
    @Override
    public final Category getCategory() {
        return Category.UTILITY;
    }
}
