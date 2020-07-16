package com.tisawesomeness.minecord.setting.impl;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.database.DMSettingContainer;
import com.tisawesomeness.minecord.setting.DMSetting;
import com.tisawesomeness.minecord.util.DiscordUtils;
import com.tisawesomeness.minecord.util.type.Validation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * The prefix of the bot.
 * <br>Messages are only accepted and parsed into commands if they start with the prefix.
 * <br>In documentation, {@code &} is used for the prefix,
 * and {@code {&}} is used as a placeholder for the prefix.
 */
@RequiredArgsConstructor
public class PrefixSetting extends DMSetting<String> {

    private static final int MAX_LENGTH = 8;
    private static final Pattern DISCORD_CONFLICT_PATTERN = Pattern.compile("[@#*()`_\\[\\]\\\\:]");
    private static final Pattern STRIKETHROUGH_CONFLICT_PATTERN = Pattern.compile("~($|~)");
    private static final Pattern SPOILER_CONFLICT_PATTERN = Pattern.compile("\\|($|\\|)");
    private static final String DISCORD_CONFLICT_ERROR =
            "The prefix cannot contain Discord formatting symbols ``@#*()`_[]:``.";
    private static final String STRIKETHROUGH_CONFLICT_ERROR =
            "The prefix cannot have two `~` in a row or at the end since it conflicts with strikethrough formatting.";
    private static final String SPOILER_CONFLICT_ERROR =
            "The prefix cannot have two `|` in a row or at the end since it conflicts with spoiler formatting.";
    private static final String ENDS_WITH_LETTER_ERROR =
            "The prefix cannot end with a letter, since the recipe command would be `abcrecipe` with prefix `abc`.";

    private static final String desc = "The prefix used before every command.\n" +
            "`@%s command` will work regardless of prefix.\n" +
            "Possible values: Any text between 1-8 characters that does not contain Discord formatting.";

    private final @NonNull Config config;

    public @NonNull String getDisplayName() {
        return "Prefix";
    }
    public boolean isAlias(@NonNull String input) {
        return "prefix".equalsIgnoreCase(input);
    }
    public @NonNull String getDescription(@NonNull String prefix, @NonNull String tag) {
        return String.format(desc, tag);
    }

    public String getDefault() {
        return config.prefixDefault;
    }

    /**
     * Imposes some sanity checks on the prefix and make sure it doesn't cause Discord formatting issues.
     * <br>A prefix is valid if it:
     * <ul>
     *     <li>Does not contain user, channel, or role mentions</li>
     *     <li>Does not contain emojis</li>
     *     <li>Is not too long</li>
     *     <li>Does not end with a letter (to prevent {@code abcrecipe} for prefix {@code abc})</li>
     *     <li>Only contains keyboard-reachable characters (letters, digits, symbols),
     *         or chars {@code 0x21} to {@code 0x7E}</li>
     *     <li>Does not contain the formatting characters {@code @#*()`_[]\:}</li>
     *     <li>Does not have {@code ~} or {@code |} twice in a row or at the end of the prefix,
     *         to prevent conflicts with strikethrough and spoiler formatting</li>
     * </ul>
     * Legal symbols: {@code !$%^&-=+;',./{}"<>?}.
     */
    public Validation<String> resolve(@NonNull String input) {
        // The mention "@a" is 18 characters long when sent to discord
        // Telling the user the prefix is too long, or other errors, would be confusing
        if (DiscordUtils.ANY_MENTION.matcher(input).find()) {
            return Validation.invalid("The prefix cannot contain user/channel/role mentions or emojis.");
        }
        if ("unset".equalsIgnoreCase(input)) {
            return Validation.invalid("The prefix cannot be literally `unset`.");
        }
        return Validation.combine(
                validateLength(input),
                validateEndNotLetter(input),
                validateLegalChars(input),
                validatePattern(input, DISCORD_CONFLICT_PATTERN, DISCORD_CONFLICT_ERROR),
                validatePattern(input, STRIKETHROUGH_CONFLICT_PATTERN, STRIKETHROUGH_CONFLICT_ERROR),
                validatePattern(input, SPOILER_CONFLICT_PATTERN, SPOILER_CONFLICT_ERROR));
    }
    private static Validation<String> validateLength(String input) {
        if (input.length() > MAX_LENGTH) {
            return Validation.invalid(String.format("The prefix must be %s or fewer characters.", MAX_LENGTH));
        }
        return Validation.valid(input);
    }
    private static Validation<String> validateLegalChars(String input) {
        for (char c : input.toCharArray()) {
            if (c < '!' || '~' < c) {
                return Validation.invalid("The prefix can only contain letters, numbers, and keyboard symbols.");
            }
        }
        return Validation.valid(input);
    }
    private static Validation<String> validatePattern(String input, Pattern pattern, String error) {
        if (pattern.matcher(input).find()) {
            return Validation.invalid(error);
        }
        return Validation.valid(input);
    }
    private static Validation<String> validateEndNotLetter(String input) {
        char lastChar = input.charAt(input.length() - 1);
        if (Character.isLetter(lastChar)) {
            return Validation.invalid(ENDS_WITH_LETTER_ERROR);
        }
        return Validation.valid(input);
    }

    public Optional<String> get(@NonNull DMSettingContainer obj) {
        return obj.getPrefix();
    }
    public void set(@NonNull DMSettingContainer obj, @NonNull String setting) throws SQLException {
        obj.withPrefix(Optional.of(setting)).update();
    }
    public void reset(@NonNull DMSettingContainer obj) throws SQLException {
        obj.withPrefix(Optional.empty()).update();
    }
}
