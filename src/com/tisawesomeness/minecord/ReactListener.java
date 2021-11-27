package com.tisawesomeness.minecord;

import com.tisawesomeness.minecord.ReactMenu.Emote;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageEmbedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;

public class ReactListener extends ListenerAdapter {
    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent e) {
        // Make sure message matches
        HashMap<Long, ReactMenu> menus = ReactMenu.getMenus();
        if (menus.containsKey(e.getMessageIdLong())) {
            // Make sure owner matches
            ReactMenu menu = menus.get(e.getMessageIdLong());
            boolean removed = false;
            if (menu.getOwnerID() == e.getUserIdLong()) {
                // Make sure emote matches
                HashMap<String, Runnable> buttons = menu.getButtons();
                ReactionEmote re = e.getReactionEmote();
                if (e.getReactionEmote().isEmoji()) {
                    String emote = re.getAsCodepoints();
                    if (buttons.containsKey(emote)) {
                        // Run the code
                        removeEmote(e);
                        removed = true;
                        Runnable r = buttons.get(emote);
                        if (r != null) {
                            menu.keepAlive();
                            r.run();
                        }
                    }
                }
            }
            if (!e.getUser().isBot() && !removed) {
                removeEmote(e);
            }
        }
    }

    @Override
    public void onMessageEmbed(MessageEmbedEvent e) {
        // If embed was removed
        if (e.getMessageEmbeds().size() == 0) {
            HashMap<Long, ReactMenu> menus = ReactMenu.getMenus();
            // If valid menu, delete
            if (menus.containsKey(e.getMessageIdLong())) {
                menus.get(e.getMessageIdLong()).disable(true);
            }
        }
    }

    /**
     * Removes an emote, keeping stars for starboard purposes
     * @param e The event received from a message reaction
     */
    private void removeEmote(MessageReactionAddEvent e) {
        if (!e.isFromGuild() || !hasManageMessagePerms(e)) {
            return;
        }
        MessageReaction r = e.getReaction();
        if (!isRemovableEmoji(r.getReactionEmote())) {
            return;
        }
        User u = e.getUser();
        if (u != null) {
            r.removeReaction(u).queue();
        }
    }
    private static boolean hasManageMessagePerms(MessageReactionAddEvent e) {
        return e.getGuild().getSelfMember().hasPermission(e.getTextChannel(), Permission.MESSAGE_MANAGE);
    }
    private static boolean isRemovableEmoji(ReactionEmote re) {
        return !re.isEmoji() || !re.getAsCodepoints().equals(Emote.STAR.toString());
    }
}
