package com.tisawesomeness.minecord.command.config;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.setting.parse.SettingCommandParser;
import com.tisawesomeness.minecord.setting.parse.SettingCommandType;

import lombok.NonNull;
import net.dv8tion.jda.api.Permission;

import java.util.EnumSet;

public class ResetCommand extends AbstractConfigCommand {

    public @NonNull String getId() {
        return "reset";
    }

    @Override
    public EnumSet<Permission> getUserPermissions() {
        return EnumSet.of(Permission.MANAGE_SERVER);
    }

    public void run(String[] args, CommandContext ctx) {
        new SettingCommandParser(ctx, SettingCommandType.RESET).parse();
    }
}
