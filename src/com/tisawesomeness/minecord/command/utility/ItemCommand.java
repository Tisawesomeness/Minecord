package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.SlashCommand;
import com.tisawesomeness.minecord.mc.item.Item;
import com.tisawesomeness.minecord.util.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class ItemCommand extends SlashCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "item",
                "Looks up an item.",
                "<item name|id>",
                1000,
                false,
                false
        );
    }

    @Override
    public SlashCommandData addCommandSyntax(SlashCommandData builder) {
        return builder.addOption(OptionType.STRING, "item", "The Minecraft item to look up", true);
    }

    @Override
    public String[] getLegacyAliases() {
        return new String[]{"i"};
    }

    @Override
    public String getHelp() {
        return "Searches for a Minecraft item.\n" +
                "Items are from Java Edition 1.7 to " + Config.getSupportedMCVersion() + ".\n" +
                "\n" +
                Item.help + "\n";
    }

    public Result run(SlashCommandInteractionEvent e) {
        // Search through the item database
        String search = e.getOption("item").getAsString();
        String item = Item.search(search, "en_US");

        // If nothing is found
        if (item == null) {
            return new Result(Outcome.WARNING,
                    ":warning: That item does not exist! " +
                            "\n" + "Did you spell it correctly?");
        }

        // Build message
        EmbedBuilder eb = Item.display(item, "en_US", "/");
        eb = MessageUtils.addFooter(eb);

        return new Result(Outcome.SUCCESS, eb.build());
    }

}
