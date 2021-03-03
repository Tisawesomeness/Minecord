package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.Category;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.IElevatedCommand;

public abstract class AbstractAdminCommand extends Command implements IElevatedCommand {
    @Override
    public final Category getCategory() {
        return Category.ADMIN;
    }
}
