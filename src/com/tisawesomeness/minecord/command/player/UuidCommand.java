package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.mc.player.*;
import com.tisawesomeness.minecord.util.UuidUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UuidCommand extends AbstractPlayerCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "uuid",
                "Shows UUID info for a player or entity.",
                "<uuid|username>",
                1000,
                false,
                false
        );
    }

    @Override
    public SlashCommandData addCommandSyntax(SlashCommandData builder) {
        return builder.addOptions(new OptionData(OptionType.STRING, "uuid_or_username", "The UUID or username of the player or entity.", true)
                .setMaxLength(Player.MAX_PLAYER_ARGUMENT_LENGTH));
    }

    @Override
    public String[] getLegacyAliases() {
        return new String[]{"u", "uu"};
    }

    @Override
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

    public Result run(SlashCommandInteractionEvent e) {
        String input = e.getOption("uuid_or_username").getAsString();
        Optional<UUID> parsedUuidOpt = UuidUtils.fromString(input);
        if (parsedUuidOpt.isPresent()) {
            UUID uuid = parsedUuidOpt.get();
            e.deferReply().queue();
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

        e.deferReply().queue();
        fireUUIDRequest(e, username);
        return new Result(Outcome.SUCCESS);
    }
    private void fireUUIDRequest(SlashCommandInteractionEvent e, Username username) {
        CompletableFuture<Optional<UUID>> futureUUID = Bot.mcLibrary.getPlayerProvider().getUUID(username);
        String errorMessage = "IOE getting UUID from username " + username;
        newCallbackBuilder(futureUUID, e)
                .onFailure(ex -> handleIOE(ex, e, errorMessage))
                .onSuccess(uuidOpt -> processUUID(uuidOpt, e, username))
                .build();
    }

    private static void processLiteralUUID(UUID uuid, SlashCommandInteractionEvent e) {
        constructReply(e, uuid, "UUID for player/entity " + uuid);
    }
    private static void processUUID(Optional<UUID> uuidOpt, SlashCommandInteractionEvent e, Username username) {
        if (!uuidOpt.isPresent()) {
            e.getHook().sendMessage("That username does not currently exist.").queue();
            return;
        }
        String title = "UUID for " + username;
        constructReply(e, uuidOpt.get(), title);
    }
    private static void constructReply(SlashCommandInteractionEvent e, UUID uuid, String title) {
        String shortUuid = String.format("**Short**: `%s`", UuidUtils.toShortString(uuid));
        String longUuid = String.format("**Long**: `%s`", UuidUtils.toLongString(uuid));
        String skinModel = String.format("**Default Skin Model**: `%s`", Player.getDefaultSkinModelFor(uuid).getDescription());
        String newSkinModel = String.format("**1.19.3+ Skin**: `%s`", DefaultSkin.defaultFor(uuid));
        String intArray = String.format("**Post-1.16 NBT**: `%s`", UuidUtils.toIntArrayString(uuid));
        String mostLeast = String.format("**Pre-1.16 NBT**: `%s`", UuidUtils.toMostLeastString(uuid));
        String desc = shortUuid + "\n" + longUuid + "\n" + skinModel + "\n" + newSkinModel + "\n" + intArray + "\n" + mostLeast;
        String nameMCUrl = Player.getNameMCUrlFor(uuid).toString();
        String avatarUrl = new Render(uuid, RenderType.AVATAR, true).render().toString();

        EmbedBuilder eb = new EmbedBuilder()
                .setAuthor(title, nameMCUrl, avatarUrl)
                .setDescription(desc);
        e.getHook().sendMessageEmbeds(eb.build()).queue();
    }

}
