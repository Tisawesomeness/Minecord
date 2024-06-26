package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.LegacyCommand;
import com.tisawesomeness.minecord.command.Registry;
import com.tisawesomeness.minecord.util.DiscordUtils;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.util.List;

public class DeployCommand extends LegacyCommand {

    @Override
    public CommandInfo getInfo() {
        return new CommandInfo(
                "deploy",
                "Deploys global slash commands",
                null,
                0,
                true,
                true
        );
    }

    @Override
    public String getHelp() {
        return "Pray.";
    }

    @Override
    public Result run(String[] args, MessageReceivedEvent e) throws Exception {
        List<CommandData> slashCommands = Registry.getSlashCommands();
        Bot.shardManager.getShardById(0).updateCommands().addCommands(slashCommands).queue();
        String msg = DiscordUtils.tagAndId(e.getAuthor()) + " deployed global slash commands";
        Bot.logger.log(":rotating_light: " + MarkdownUtil.bold(msg));
        return new Result(Outcome.SUCCESS, "May the ratelimit have mercy on your soul.");
    }

}
