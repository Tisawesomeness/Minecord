package com.tisawesomeness.minecord;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.tisawesomeness.minecord.command.CommandContext;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Represents a menu the user can interact with by reacting to the message
 */
public abstract class ReactMenu {

    private final static long timeout = 10 * 60 * 1000;
    private static HashMap<Long, ReactMenu> menus = new HashMap<Long, ReactMenu>();
    private Message message;
    private int page;
    private boolean ready;
    private long expire;
    private HashMap<String, Runnable> buttons;
    private long ownerID;
    private String ownerName;
    private String lang;

    /**
     * Creates, but does not activate a reaction menu object that users can interact with, starting on page 0
     * @param lang The language code to use
     */
    public ReactMenu(String lang) {
        this(0, lang);
    }
    /**
     * Creates, but does not activate a reaction menu object that users can interact with
     * @param lang The language code to use
     * @param startPage The page to start on
     */
    public ReactMenu(int startPage, String lang) {
        this.lang = lang;
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
     * @param updateButtons Whether or not to remove and re-add buttons
     */
    public void setPage(int page, boolean updateButtons) {
        ready = false;
        buttons = createButtons(page);
        this.page = page;
        if (hasPerms(Permission.MESSAGE_MANAGE, Permission.MESSAGE_ADD_REACTION)) {
            message = message.editMessage(getEmbed(page)).complete();
            ready = true;
            if (updateButtons) {
                List<String> currentButtons = message.getReactions().stream()
                    .map(r -> r.getReactionEmote())
                    .filter(re -> re.isEmoji())
                    .map(re -> re.getAsCodepoints())
                    .collect(Collectors.toList());
                for (String button : buttons.keySet()) {
                    if (buttons.get(button) != null && !currentButtons.contains(button)) {
                        message.addReaction(button).submit();
                    }
                }
            }
        } else {
            message = message.editMessage(getEmbed(page, true)).complete();
        }
    }
    /**
     * Removes this menu from the registry, meaning nobody can react to it
     */
    public void disable() {
        ready = false;
        MessageEmbed emb = message.getEmbeds().get(0);
        menus.remove(getMessageID());
        message = message.editMessage( new EmbedBuilder(emb).setFooter(emb.getFooter().getText() + " (expired)").build()).complete();
        if (hasPerms(Permission.MESSAGE_MANAGE)) {
            message.getReactions().stream()
                .filter(r -> r.isSelf())
                .forEach(r -> r.removeReaction().queue());
        }
    }
    /**
     * Posts the menu in chat
     * @param channel The channel to post in
     * @param owner The owner the menu is assigned to, only they can use buttons
     */
    public void post(MessageChannel channel, User owner) {
        this.ownerID = owner.getIdLong();
        this.ownerName = owner.getName();
        buttons = createButtons(page);
        message = channel.sendMessage(getEmbed(page)).complete();
        for (String button : buttons.keySet()) {
            if (buttons.get(button) != null) {
                message.addReaction(button).queue();
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
     * @param error True if the bot does not have correct permissoins
     * @return The built MessageEmbed
     */
    private MessageEmbed getEmbed(int page, boolean error) {
        String err = error ? " | Give the bot manage messages and add reactions permissions to use an interactive menu!" : " | Requested by " + ownerName;
        return getContent(page).setFooter(String.format("**Page %d/%d**%s", page + 1, getLength(), err)).build();
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
    public static void purge() {
        menus.values().stream()
                .filter(m -> m.expire < System.currentTimeMillis())
                .forEach(ReactMenu::disable);
    }
    /**
     * Check for permissions with less typing
     */
    private boolean hasPerms(Permission... permissions) {
        return message.getGuild().getSelfMember().hasPermission(message.getTextChannel(), permissions);
    }

    /**
     * Checks if the bot is able to make a react menu in the specified channel
     * @return True if the guild has menus enabled and the bot has manage message and add reaction permissions
     */
    public static MenuStatus getMenuStatus(CommandContext txt) {
        MessageReceivedEvent e = txt.e;
        if (!e.isFromGuild()) {
            return txt.config.shouldUseMenusDefault() ? MenuStatus.PRIVATE_MESSAGE : MenuStatus.DISABLED;
        }
        if (!txt.shouldUseMenus()) {
            return MenuStatus.DISABLED;
        }
        if (!e.getGuild().getSelfMember().hasPermission(e.getTextChannel(),
                Permission.MESSAGE_MANAGE, Permission.MESSAGE_ADD_REACTION)) {
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
     * @return The current page
     */
    public int getPage() {
        return page;
    }
    /**
     * @return A list of buttons, with emoji codepoint strings as the key and the button's function as the value
     */
    public HashMap<String, Runnable> getButtons() {
        return buttons;
    }
    /**
     * @return Whether the menu is ready for user input
     */
    public boolean isReady() {
        return ready;
    }
    /**
     * @return The language code
     */
    public String getLang() {
        return lang;
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
        FULL_BLANK("U+1f5a4"),
        SKIP_BLANK("U+2b1b"),
        BLANK("U+26ab"),
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
        private Emote(String codepoint) {
            this(codepoint, null);
        }
        private Emote(String codepoint, String text) {
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
        PRIVATE_MESSAGE("Reaction menus cannot be used in DMs."),
        NO_PERMISSION("Give the bot manage messages permissions to use an interactive menu!");

        private String reason;
        private boolean useSpacer;
        private MenuStatus() {
            this("");
            this.useSpacer = false;
        }
        private MenuStatus(String reason) {
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