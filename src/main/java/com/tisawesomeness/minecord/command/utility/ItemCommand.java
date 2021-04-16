package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.meta.CommandContext;
import com.tisawesomeness.minecord.config.serial.Config;
import com.tisawesomeness.minecord.mc.item.Item;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;

public class ItemCommand extends AbstractUtilityCommand {

    public @NonNull String getId() {
        return "item";
    }

    @Override
    public Object[] getHelpArgs(String prefix, String tag, Config config) {
        return new Object[]{prefix, tag, config.getSupportedMCVersion()};
    }

    public void run(String[] args, CommandContext ctx) {
        // Check for argument length
        if (args.length == 0) {
            ctx.showHelp();
            return;
        }
        ctx.triggerCooldown();

        // Search through the item database
        String item = Item.search(ctx.joinArgs(), "en_US");

        // If nothing is found
        if (item == null) {
            ctx.invalidArgs("That item does not exist!\nDid you spell it correctly?");
            return;
        }

        // Build message
        EmbedBuilder eb = Item.display(item, "en_US", ctx.getPrefix());
        eb.setFooter("See an error? Please report them at https://goo.gl/KWCxis", null);
        // eb = MessageUtils.addFooter(eb);

        ctx.replyRaw(eb);
    }

}
