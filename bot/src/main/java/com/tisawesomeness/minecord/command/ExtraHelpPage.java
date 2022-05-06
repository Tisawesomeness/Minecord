package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.command.meta.CommandContext;
import com.tisawesomeness.minecord.lang.Lang;
import com.tisawesomeness.minecord.lang.Localizable;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 *     A help page not connected to a command, shown in {@code &help extra}.
 * </p>
 * <p>
 *     An extra help page's ID, display name, and aliases cannot conflict with...
 *     <ul>
 *         <li>Any other {@link ExtraHelpPage} ID or display name, or aliases</li>
 *         <li>Any command's ID, display name, or aliases</li>
 *         <li>The word "extra" in the default or current language as used in {@code &help extra}</li>
 *     </ul>
 *     ...for each language.
 *     <br>A help page's ID and display name also cannot conflict with any category name in any language.
 * </p>
 */
@RequiredArgsConstructor
public enum ExtraHelpPage implements Localizable {
    USERNAME_INPUT("usernameInput"),
    UUID_INPUT("uuidInput"),
    PHD("phd");

    /**
     * The internal ID of the help page
     */
    @Getter private final @NonNull String id;

    /**
     * Gets a help page from its ID, name, or aliases.
     * @param name A case-insensitive string
     * @param lang The current language
     * @return The help page, or empty if not found
     */
    public static Optional<ExtraHelpPage> from(@NonNull String name, Lang lang) {
        return Arrays.stream(values())
                .filter(ehp -> ehp.matches(name, lang))
                .findFirst();
    }
    private boolean matches(@NonNull String name, Lang lang) {
        return lang.equalsIgnoreCase(id, name)
                || lang.equalsIgnoreCase(lang.localize(this), name)
                || lang.containsIgnoreCase(getAliases(lang), name);
    }

    /**
     * Gets the description of this help page.
     * @param lang The current language
     * @return The lang-specific description
     */
    public @NonNull String getDescription(Lang lang) {
        return lang.i18n(formatKey("description"));
    }
    /**
     * Gets the aliases of this help page.
     * @param lang The current language
     * @return A lang-specific list of aliases, may be empty
     */
    public List<String> getAliases(Lang lang) {
        return lang.i18nList(formatKey("aliases"));
    }

    /**
     * Creates an embed from this help page.
     * @param ctx The context of the executing command
     * @return An embed
     */
    public @NonNull EmbedBuilder showHelp(CommandContext ctx) {
        Lang lang = ctx.getLang();
        String prefix = ctx.getPrefix();
        String tag = ctx.getE().getJDA().getSelfUser().getAsMention();
        return new EmbedBuilder()
                .setTitle(lang.i18n(formatKey("title")))
                .setDescription(lang.i18nf(formatKey("help"), prefix, tag));
    }

    public @NonNull String getTranslationKey() {
        return formatKey("name");
    }
    public Object[] getTranslationArgs() {
        return new Object[0];
    }

    private String formatKey(String key) {
        return String.format("help.extra.%s.%s", id, key);
    }

    @Override
    public String toString() {
        return String.format("ExtraHelpPage(%s)", id);
    }

}
