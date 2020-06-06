package com.tisawesomeness.minecord;

import java.util.HashMap;

import com.tisawesomeness.minecord.ReactMenu.Emote;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.events.message.MessageEmbedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

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
     * Removes an emote, whitelisting stars for starboard purposes
     * @param e
     */
    private void removeEmote(MessageReactionAddEvent e) {
        MessageReaction r = e.getReaction();
        ReactionEmote re = r.getReactionEmote();
        if (e.getGuild().getSelfMember().hasPermission(e.getTextChannel(), Permission.MESSAGE_MANAGE)
                && (!re.isEmoji() || !re.getAsCodepoints().equals(Emote.STAR.toString()))) {
            r.removeReaction(e.getUser()).queue();
        }
    }
}