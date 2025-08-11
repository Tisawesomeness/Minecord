package com.tisawesomeness.minecord.interaction;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.util.Utils;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Keeps track of messages with interactions that a user can respond to.
 */
public class InteractionTracker {

    private static final Map<Long, Tracker> interactions = new HashMap<>();

    /**
     * Posts a message that can be updated with interactions.
     * @param hook the webhook API of the interaction used to post the message
     * @param updatingMessage the message to post
     */
    public static void post(InteractionHook hook, UpdatingMessage updatingMessage) {
        hook.sendMessage(updatingMessage.render(true)).queue(message -> {
            interactions.put(message.getIdLong(), new Tracker(updatingMessage, hook.getExpirationTimestamp()));
        });
    }

    public static void onInteract(GenericComponentInteractionCreateEvent e) {
        UpdatingMessage updatingMessage = get(e.getMessage());
        if (updatingMessage == null) {
            e.reply("That menu has expired.").setEphemeral(true).queue();
            return;
        }
        boolean modified = updatingMessage.onInteract(e);
        if (!e.isAcknowledged()) {
            e.deferEdit().queue();
        }
        if (modified) {
            e.getHook().editOriginal(MessageEditData.fromCreateData(updatingMessage.render(true))).queue();
        }
    }

    public static void onSubmit(ModalInteractionEvent e) {
        UpdatingMessage updatingMessage = get(e.getMessage());
        if (updatingMessage == null) {
            e.reply("That menu has expired.").setEphemeral(true).queue();
            return;
        }
        boolean modified = updatingMessage.onSubmit(e);
        if (!e.isAcknowledged()) {
            e.deferEdit().queue();
        }
        if (modified) {
            e.getHook().editOriginal(MessageEditData.fromCreateData(updatingMessage.render(true))).queue();
        }
    }

    private static UpdatingMessage get(@Nullable Message m) {
        if (m == null) {
            return null;
        }
        return Utils.mapNullable(interactions.get(m.getIdLong()), t -> t.message);
    }

    /**
     * Checks if the bot should use menus for the given command interaction.
     * @param e the command interaction
     * @return true if the bot should use menus
     */
    public static boolean shouldUseMenus(SlashCommandInteractionEvent e) {
        if (!Config.getUseMenus()) {
            return false;
        }
        Guild g = e.getGuild();
        if (g != null) {
            return Database.getUseMenu(g.getIdLong());
        }
        return true;
    }

    public static void startPurgeThread() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(InteractionTracker::purge, 5, 1, TimeUnit.MINUTES);
    }
    private static void purge() {
        interactions.values().removeIf(Tracker::isExpired);
    }

    @AllArgsConstructor
    private static class Tracker {
        private final UpdatingMessage message;
        private final long expirationTimestamp;

        public boolean isExpired() {
            return expirationTimestamp < System.currentTimeMillis();
        }
    }

}
