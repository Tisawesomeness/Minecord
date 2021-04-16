package com.tisawesomeness.minecord.setting.parse;

import com.tisawesomeness.minecord.command.meta.CommandContext;
import com.tisawesomeness.minecord.database.dao.SettingContainer;
import com.tisawesomeness.minecord.setting.Setting;
import com.tisawesomeness.minecord.setting.SettingRegistry;

import lombok.Getter;
import lombok.NonNull;

import java.util.Optional;
import java.util.StringJoiner;

/**
 * Parses the name of a setting from user input
 */
public class SettingChooser extends SettingCommandHandler {
    @Getter private final @NonNull CommandContext ctx;
    @Getter private final @NonNull SettingCommandType type;
    @Getter private final @NonNull SettingContainer obj;
    @Getter private int currentArg;

    public SettingChooser(SettingContext prev, @NonNull SettingContainer obj) {
        ctx = prev.getCtx();
        type = prev.getType();
        this.obj = obj;
        currentArg = prev.getCurrentArg();
    }

    /**
     * Adds arguments to a string until that string matches with a setting.
     * <br>If found, the {@link SettingChanger} changes that setting.
     */
    public void parse() {
        String[] args = ctx.getArgs();
        SettingRegistry settings = ctx.getBot().getSettings();

        StringJoiner settingName = new StringJoiner(" ");
        while (currentArg < args.length) {
            settingName.add(args[currentArg]);
            currentArg++;
            Optional<Setting<?>> settingOpt = settings.getSetting(settingName.toString());
            if (settingOpt.isPresent()) {
                changeSettingIfSpaceForValueExists(settingOpt.get());
                return;
            }
        }
        ctx.invalidArgs("That setting does not exist.");
    }

    private void changeSettingIfSpaceForValueExists(Setting<?> setting) {
        if (type == SettingCommandType.SET && currentArg == ctx.getArgs().length) {
            ctx.invalidArgs("You must specify a setting value.");
            return;
        }
        new SettingChanger(this, setting).parse();
    }
}
