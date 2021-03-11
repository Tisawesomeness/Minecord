package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.Category;
import com.tisawesomeness.minecord.command.Command;

public abstract class AbstractUtilityCommand extends Command {
    @Override
    public final Category getCategory() {
        return Category.UTILITY;
    }
}
