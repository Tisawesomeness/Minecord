package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.command.meta.CommandContext;
import com.tisawesomeness.minecord.mc.player.Player;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HistoryCommand extends BasePlayerCommand {

    public @NonNull String getId() {
        return "history";
    }

    protected void onSuccessfulPlayer(CommandContext ctx, Player player) {
        ctx.reply("Mojang has removed the ability to get a player's name history.");
    }

}
