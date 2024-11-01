package com.tisawesomeness.minecord;

import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.util.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Represents a menu the user can interact with by reacting to the message
 */
public abstract class ReactMenu {

    private final static long timeout = 10 * 60 * 1000;
    private static final HashMap<Long, ReactMenu> menus = new HashMap<>();
    private SlashCommandInteractionEvent e;
    private Message message;
    private int page;
    private long expire;
    private HashMap<String, Runnable> buttons;
    private long ownerID;

    /**
     * Creates, but does not activate a reaction menu object that users can interact with
     * @param startPage The page to start on
     */
    public ReactMenu(int startPage) {
        this.page = startPage;
    }

    /**
     * Moves the menu to a certain page by editing the message
     * @param page The page >= 0 to move to
     */
    public void setPage(int page) {
        setPage(page, true);
    }
    /**
     * Moves the menu to a certain page by editing the message
     * @param page The page >= 0 to move to
     * @param updateButtons Whether to remove and re-add buttons
     */
    public void setPage(int page, boolean updateButtons) {
        buttons = createButtons(page);
        this.page = page;
        if (hasPerms(Permission.MESSAGE_ADD_REACTION)) {
            e.getHook().editOriginalEmbeds(getEmbed(page)).complete();
            if (updateButtons) {
                List<String> currentButtons = message.getReactions().stream()
                        .map(MessageReaction::getEmoji)
                        .filter(em -> em.getType() == Emoji.Type.UNICODE)
                        .map(EmojiUnion::asUnicode)
                        .map(UnicodeEmoji::getAsCodepoints)
                        .collect(Collectors.toList());
                for (Map.Entry<String, Runnable> entry : buttons.entrySet()) {
                    String button = entry.getKey();
                    if (entry.getValue() != null && !currentButtons.contains(button)) {
                        message.addReaction(Emoji.fromUnicode(button)).submit();
                    }
                }
            }
        } else {
            e.getHook().editOriginalEmbeds(getEmbed(page, true)).complete();
        }
    }
    /**
     * Removes this menu from the registry, meaning nobody can react to it
     */
    public void disable() {
        MessageEmbed emb = message.getEmbeds().get(0);
        menus.remove(getMessageID());
        MessageEmbed edited = new EmbedBuilder(emb).setTitle("(expired) " + emb.getTitle()).build();
        e.getHook().editOriginalEmbeds(edited).queue();
        if (hasPerms(Permission.MESSAGE_MANAGE)) {
            message.getReactions().stream()
                    .filter(MessageReaction::isSelf)
                    .forEach(r -> r.removeReaction().queue());
        }
    }
    /**
     * Posts the menu in chat
     * @param e the event
     */
    public void post(SlashCommandInteractionEvent e) {
        this.e = e;
        User owner = e.getUser();
        this.ownerID = owner.getIdLong();
        buttons = createButtons(page);
        message = e.getHook().sendMessageEmbeds(getEmbed(page)).complete();
        for (Map.Entry<String, Runnable> entry : buttons.entrySet()) {
            if (entry.getValue() != null) {
                message.addReaction(Emoji.fromUnicode(entry.getKey())).submit();
            }
        }
        keepAlive();
        menus.put(getMessageID(), this);
    }
    /**
     * Adds a page display to the footer
     * @param page The current page number
     * @return The built MessageEmbed
     */
    private MessageEmbed getEmbed(int page) {
        return getEmbed(page, false);
    }
    /**
     * Adds a page display to the footer
     * @param page The current page number
     * @param error True if the bot does not have correct permissions
     * @return The built MessageEmbed
     */
    private MessageEmbed getEmbed(int page, boolean error) {
        EmbedBuilder eb = getContent(page);
        int length = getLength();
        if (length > 1) {
            eb.setTitle(String.format("(%d/%d) %s", page + 1, getLength(), eb.build().getTitle()));
        }
        if (error) {
            eb.setFooter("Give the bot manage messages and add reactions permissions to use an interactive menu!");
        } else {
            eb = MessageUtils.addFooter(eb);
        }
        return eb.build();
    }
    /**
     * Resets the expiration timer
     */
    public void keepAlive() {
        expire = System.currentTimeMillis() + timeout;
    }
    /**
     * Purges all expired menus from the list
     */
    public static void startPurgeThread() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(ReactMenu::purge, 10, 1, TimeUnit.MINUTES);
    }
    private static void purge() {
        menus.values().stream()
                .filter(m -> m.expire < System.currentTimeMillis())
                .forEach(ReactMenu::disable);
    }
    /**
     * Check for permissions with less typing
     */
    private boolean hasPerms(Permission... permissions) {
        if (!message.isFromGuild()) {
            return true;
        }
        return message.getGuild().getSelfMember().hasPermission(message.getGuildChannel(), permissions);
    }

    /**
     * Checks if the bot is able to make a react menu in the specified channel
     * @return Whether menus are valid, disabled, or the bot has no permission
     */
    public static MenuStatus getMenuStatus(SlashCommandInteractionEvent e) {
        if (!Config.getUseMenus()) {
            return MenuStatus.DISABLED;
        }
        if (!e.isFromGuild()) {
            return MenuStatus.VALID;
        }
        Guild g = e.getGuild();
        if (!Database.getUseMenu(g.getIdLong())) {
            return MenuStatus.DISABLED;
        }
        if (!g.getSelfMember().hasPermission(e.getGuildChannel(), Permission.MESSAGE_ADD_REACTION) || !g.getSelfMember().hasPermission(e.getGuildChannel(), Permission.MESSAGE_HISTORY)) {
            return MenuStatus.NO_PERMISSION;
        }
        return MenuStatus.VALID;
    }

    /**
     * @return The menu registry with message IDs as keys
     */
    public static HashMap<Long, ReactMenu> getMenus() {
        return menus;
    }
    /**
     * @return The message id the menu belongs to
     */
    public long getMessageID() {
        return message.getIdLong();
    }
    /**
     * @return The id of the only user allowed to use the menu
     */
    public long getOwnerID() {
        return ownerID;
    }
    /**
     * @return A list of buttons, with emoji codepoint strings as the key and the button's function as the value
     */
    public HashMap<String, Runnable> getButtons() {
        return buttons;
    }

    /**
     * Generates the contents of one page in the menu
     * @param page The page to generate content for
     * @return An EmbedBuilder with the page content, the footer will be overwritten
     */
    public abstract EmbedBuilder getContent(int page);
    /**
     * Generates a list of buttons and their functions
     * @param page The page to generate buttons for
     * @return A map of emoji codepoint strings to functions, when a user reacts with the emoji, the function will run.
     */
    public abstract LinkedHashMap<String, Runnable> createButtons(int page);
    /**
     * @return The total number of pages the menu has, which may vary.
     */
    public abstract int getLength();

    /**
     * Stores the emotes used in menus and their string codepoints
     */
    public enum Emote {
        STAR("U+2b50"),
        FULL_BACK("U+23ee"),
        SKIP_BACK("U+23ea"),
        BACK("U+25c0"),
        FORWARD("U+25b6"),
        SKIP_FORWARD("U+23e9"),
        FULL_FORWARD("U+23ed"),
        T("U+1f1f9", ":regional_indicator_t:"),
        UP("U+1f53c", ":arrow_up_small:"),
        N1("U+31U+fe0fU+20e3", ":one:"),
        N2("U+32U+fe0fU+20e3", ":two:"),
        N3("U+33U+fe0fU+20e3", ":three:"),
        N4("U+34U+fe0fU+20e3", ":four:"),
        N5("U+35U+fe0fU+20e3", ":five:"),
        N6("U+36U+fe0fU+20e3", ":six:"),
        N7("U+37U+fe0fU+20e3", ":seven:"),
        N8("U+38U+fe0fU+20e3", ":eight:"),
        N9("U+39U+fe0fU+20e3", ":nine:"),
        MORE("U+1f504", ":arrows_counterclockwise:");

        private final String codepoint;
        private final String text;
        Emote(String codepoint) {
            this(codepoint, null);
        }
        Emote(String codepoint, String text) {
            this.codepoint = codepoint;
            this.text = text;
        }
        /**
         * @return The string codepoint of the emote
         */
        public String getCodepoint() {
            return codepoint;
        }
        /**
         * @return The text used to show the emote in a message
         */
        public String getText() {
            return text;
        }
        /**
         * @return The Emote associated with a number 1-9
         */
        public static Emote valueOf(int i) {
            switch(i) {
                case 1: return N1;
                case 2: return N2;
                case 3: return N3;
                case 4: return N4;
                case 5: return N5;
                case 6: return N6;
                case 7: return N7;
                case 8: return N8;
                case 9: return N9;
            }
            return null;
        }
    }

    /**
     * Represents the reason a menu cannot be created, or VALID
     */
    public enum MenuStatus {
        VALID(),
        DISABLED(),
        NO_PERMISSION("Give the bot add reactions and message history permissions to use an interactive menu!");

        private final String reason;
        private boolean useSpacer;
        MenuStatus() {
            this("");
            this.useSpacer = false;
        }
        MenuStatus(String reason) {
            this.reason = reason;
            this.useSpacer = true;
        }

        /**
         * Gets the reason a menu is invalid, useful for printing error messages.
         * @return The reason, formatted with a spacer. May be blank.
         * @throws IllegalArgumentException If the menu is valid.
         */
        public String getReason() {
            if (isValid()) {
                throw new IllegalArgumentException("Menu is valid, there is no reason it is invalid.");
            }
            String spacer = useSpacer ? " | " : "";
            return spacer + reason;
        }
        /**
         * @return Whether the menu is valid.
         */
        public boolean isValid() {
            return this == MenuStatus.VALID;
        }
    }

}
