package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.meta.Category;
import com.tisawesomeness.minecord.command.meta.Command;
import com.tisawesomeness.minecord.command.meta.IElevatedCommand;

public abstract class AbstractAdminCommand extends Command implements IElevatedCommand {
    @Override
    public final Category getCategory() {
        return Category.ADMIN;
    }
}
