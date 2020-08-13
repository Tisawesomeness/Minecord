package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.Result;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.NameUtils;
import com.tisawesomeness.minecord.util.RequestUtils;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.io.IOException;
import java.util.Arrays;

public class CapeCommand extends AbstractPlayerCommand {

    public @NonNull String getId() {
        return "cape";
    }

    public Result run(String[] args, CommandContext ctx) {

        // No arguments message
        if (args.length == 0) {
            return ctx.showHelp();
        }

        // Get playername
        ctx.triggerCooldown();
        String player = args[0];
        String uuid = player;
        if (player.matches(NameUtils.uuidRegex)) {
            player = NameUtils.getName(player);

            // Check for errors
            if (player == null) {
                String m = "The Mojang API could not be reached." +
                    "\n" + "Are you sure that UUID exists?";
                return ctx.err(m);
            } else if (!player.matches(NameUtils.playerRegex)) {
                return ctx.err("The API responded with an error:\n" + player);
            }
        } else {
            // Parse date argument
            if (args.length > 1) {
                long timestamp = DateUtils.getTimestamp(Arrays.copyOfRange(args, 1, args.length));
                if (timestamp == -1) {
                    return ctx.showHelp();
                }

                // Get the UUID
                uuid = NameUtils.getUUID(player, timestamp);
            } else {
                uuid = NameUtils.getUUID(player);
            }

            // Check for errors
            if (uuid == null) {
                String m = "The Mojang API could not be reached." +
                        "\n" +"Are you sure that username exists?" +
                        "\n" + "Usernames are case-sensitive.";
                return ctx.err(m);
            } else if (!uuid.matches(NameUtils.uuidRegex)) {
                return ctx.err("The API responded with an error:\n" + uuid);
            }

            uuid = uuid.replace("-", "").toLowerCase();
        }

        // Minecraft capes
        boolean hasCape = false;
        if (NameUtils.mojangUUIDs.contains(uuid)) {
            // Mojang cape
            sendImage(ctx, "Minecraft Cape", "https://minecord.github.io/capes/mojang.png");
            hasCape = true;
        } else {
            // Other minecraft capes
            String url = "https://crafatar.com/capes/" + uuid;
            if (RequestUtils.checkURL(url)) {
                sendImage(ctx, "Minecraft Cape", url);
                hasCape = true;
            }
        }
        // Optifine cape
        String url = String.format("http://s.optifine.net/capes/%s.png", player);
        if (RequestUtils.checkURL(url)) {
            sendImage(ctx, "Optifine Cape", url);
            hasCape = true;
        }
        // LabyMod cape (doesn't show in embed, download required)
        url = String.format("http://capes.labymod.net/capes/%s", NameUtils.formatUUID(uuid));
        if (RequestUtils.checkURL(url)) {
            if (canSendFiles(ctx)) {
                MessageEmbed emb = new EmbedBuilder()
                        .setTitle("LabyMod Cape")
                        .setColor(Bot.color)
                        .setImage("attachment://cape.png")
                        .build();
                try {
                    ctx.e.getChannel().sendFile(RequestUtils.downloadImage(url), "cape.png").embed(emb).queue();
                    hasCape = true;
                } catch (IOException ex) {
                    ex.printStackTrace();
                    ctx.err("There was an error downloading the LabyMod cape.");
                }
            } else {
                ctx.warn("The player has a LabyMod cape, but it couldn't be downloaded" +
                        " since the bot doesn't have the Attach Files permission.");
            }
        }
        // MinecraftCapes.co.uk
        url = String.format("https://www.minecraftcapes.co.uk/gallery/grab-player-capes/%s", player);
        if (RequestUtils.checkURL(url, true)) {
            sendImage(ctx, "MinecraftCapes.co.uk Cape", url);
            hasCape = true;
        }

        if (!hasCape) {
            return ctx.warn(player + " does not have a cape!");
        }
        return Result.SUCCESS;
    }

    private static void sendImage(CommandContext ctx, String title, String url) {
        ctx.reply(new EmbedBuilder().setTitle(title).setColor(Bot.color).setImage(url));
    }

    private static boolean canSendFiles(CommandContext ctx) {
        if (!ctx.e.isFromGuild()) {
            return true;
        }
        return ctx.botHasPermission(Permission.MESSAGE_ATTACH_FILES);
    }

}
