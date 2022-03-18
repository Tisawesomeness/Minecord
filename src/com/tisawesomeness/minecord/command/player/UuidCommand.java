package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.mc.player.Render;
import com.tisawesomeness.minecord.mc.player.RenderType;
import com.tisawesomeness.minecord.mc.player.Username;
import com.tisawesomeness.minecord.util.UuidUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UuidCommand extends AbstractPlayerCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "uuid",
                "Shows UUID info for a player or entity.",
                "<uuid|username>",
                new String[]{"u", "uu"},
                2000,
                false,
                false,
                true
        );
    }

    public String getHelp() {
        return "`{&}uuid <uuid|username>` - Shows UUID info for a player or entity.\n" +
                "- `<uuid>` can be any valid short or long UUID, including UUIDs that do not belong to a player.\n" +
                "- `<username>` can be any username.\n" +
                "Use `{&}help usernameInput|uuidInput` for more help.\n" +
                "\n" +
                "Examples:\n" +
                "- `{&}uuid Tis_awesomeness`\n" +
                "- `{&}uuid LadyAgnes`\n" +
                "- `{&}uuid f6489b79-7a9f-49e2-980e-265a05dbc3af`\n" +
                "- `{&}uuid 853c80ef3c3749fdaa49938b674adae6`\n";
    }

    public Result run(String[] args, MessageReceivedEvent e) {
        if (args.length == 0) {
            return new Result(Outcome.WARNING, ":warning: You must specify a player.");
        }

        String input = String.join(" ", args);
        Optional<UUID> parsedUuidOpt = UuidUtils.fromString(input);
        if (parsedUuidOpt.isPresent()) {
            UUID uuid = parsedUuidOpt.get();
            processLiteralUUID(uuid, e);
            return new Result(Outcome.SUCCESS);
        }

        if (input.length() > Username.MAX_LENGTH) {
            String msg = String.format("Usernames must be %d characters or less.", Username.MAX_LENGTH);
            return new Result(Outcome.WARNING, msg);
        }
        Username username = Username.parse(input);
        if (!username.isSupportedByMojangAPI()) {
            return new Result(Outcome.WARNING, ":warning: Unfortunately, the Mojang API does not support " +
                    "special characters other than spaces and `_!@$-.?`");
        }

        fireUUIDRequest(e, username);
        return new Result(Outcome.SUCCESS);
    }
    private static void fireUUIDRequest(MessageReceivedEvent e, Username username) {
        CompletableFuture<Optional<UUID>> futureUUID = Bot.mcLibrary.getPlayerProvider().getUUID(username);
        String errorMessage = "IOE getting UUID from username " + username;
        newCallbackBuilder(futureUUID, e)
                .onFailure(ex -> handleIOE(ex, e, errorMessage))
                .onSuccess(uuidOpt -> processUUID(uuidOpt, e, username))
                .build();
    }

    private static void processLiteralUUID(UUID uuid, MessageReceivedEvent e) {
        constructReply(e, uuid, "UUID for player/entity " + uuid);
    }
    private static void processUUID(Optional<UUID> uuidOpt, MessageReceivedEvent e, Username username) {
        if (!uuidOpt.isPresent()) {
            e.getChannel().sendMessage("That username does not currently exist.").queue();
            return;
        }
        String title = "UUID for " + username;
        constructReply(e, uuidOpt.get(), title);
    }
    private static void constructReply(MessageReceivedEvent e, UUID uuid, String title) {
        String shortUuid = String.format("**Short**: `%s`", UuidUtils.toShortString(uuid));
        String longUuid = String.format("**Long**: `%s`", UuidUtils.toLongString(uuid));
        String skinType = String.format("**Default Skin Model**: `%s`", Player.getDefaultSkinTypeFor(uuid));
        String intArray = String.format("**Post-1.16 NBT**: `%s`", UuidUtils.toIntArrayString(uuid));
        String mostLeast = String.format("**Pre-1.16 NBT**: `%s`", UuidUtils.toMostLeastString(uuid));
        String desc = shortUuid + "\n" + longUuid + "\n" + skinType + "\n" + intArray + "\n" + mostLeast;
        String nameMCUrl = Player.getNameMCUrlFor(uuid).toString();
        String avatarUrl = new Render(uuid, RenderType.AVATAR, true).render().toString();

        EmbedBuilder eb = new EmbedBuilder()
                .setAuthor(title, nameMCUrl, avatarUrl)
                .setDescription(desc);
        e.getChannel().sendMessageEmbeds(eb.build()).queue();
    }

}
