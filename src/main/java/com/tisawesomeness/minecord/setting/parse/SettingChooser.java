package com.tisawesomeness.minecord.setting.parse;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
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
    @Getter private final @NonNull CommandContext txt;
    @Getter private final @NonNull SettingCommandType type;
    @Getter private final @NonNull SettingContainer obj;
    @Getter private int currentArg;

    public SettingChooser(SettingContext prev, SettingContainer obj) {
        txt = prev.getTxt();
        type = prev.getType();
        this.obj = obj;
        currentArg = prev.getCurrentArg();
    }

    /**
     * Adds arguments to a string until that string matches with a setting.
     * <br>If found, the {@link SettingChanger} changes that setting.
     * @return The result of the command
     */
    public Command.Result parse() {
        String[] args = txt.args;
        SettingRegistry settings = txt.bot.getSettings();

        StringJoiner settingName = new StringJoiner(" ");
        while (currentArg < args.length) {
            settingName.add(args[currentArg]);
            currentArg++;
            Optional<Setting<?>> settingOpt = settings.getSetting(settingName.toString());
            if (settingOpt.isPresent()) {
                return changeSettingIfSpaceForValueExists(settingOpt.get());
            }
        }
        return new Command.Result(Command.Outcome.WARNING, ":warning: That setting does not exist.");
    }

    private Command.Result changeSettingIfSpaceForValueExists(Setting<?> setting) {
        if (type == SettingCommandType.SET && currentArg == txt.args.length) {
            return new Command.Result(Command.Outcome.WARNING, ":warning: You must specify a setting value.");
        }
        return new SettingChanger(this, setting).parse();
    }
}
