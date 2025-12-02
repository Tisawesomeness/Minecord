package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.LegacyCommand;
import com.tisawesomeness.minecord.command.Registry;
import com.tisawesomeness.minecord.command.SlashCommand;
import com.tisawesomeness.minecord.util.Utils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

public class DebugSlashCommand extends LegacyCommand {

    @Override
    public CommandInfo getInfo() {
        return new CommandInfo(
                "slash",
                "Print slash command JSON",
                "<command>|all",
                0,
                true,
                true
        );
    }

    @Override
    public Result run(String[] args, MessageReceivedEvent e) throws Exception {
        if (args.length < 1) {
            return new Result(Outcome.WARNING, ":warning: Specify a command or \"all\".");
        }

        String command = args[0];
        if (command.equalsIgnoreCase("all")) {
            Path path = Paths.get(Config.getPath(), "slash_commands.json");
            String allCommandStr = Utils.prettify(Registry.commandsJson());
            Files.write(path, allCommandStr.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return new Result(Outcome.SUCCESS, "Written to slash_commands.json");
        }

        Optional<JSONObject> commandOpt = Registry.getSlashCommand(command)
                .map(SlashCommand::getCommandSyntax)
                .map(Registry::convertToJson);
        if (!commandOpt.isPresent()) {
            return new Result(Outcome.WARNING, ":warning: Invalid command");
        }
        return new Result(Outcome.SUCCESS, MarkdownUtil.codeblock("json", Utils.prettify(commandOpt.get())));
    }

}
