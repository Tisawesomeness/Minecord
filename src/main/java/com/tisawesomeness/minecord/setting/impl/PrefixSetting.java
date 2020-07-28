package com.tisawesomeness.minecord.setting.impl;

import com.tisawesomeness.minecord.config.serial.SettingsConfig;
import com.tisawesomeness.minecord.database.dao.SettingContainer;
import com.tisawesomeness.minecord.setting.Setting;
import com.tisawesomeness.minecord.util.DiscordUtils;
import com.tisawesomeness.minecord.util.type.Validation;
import com.tisawesomeness.minecord.util.type.Verification;

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
public class PrefixSetting extends Setting<String> {

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

    private static final String DESC = "The prefix used before every command.\n" +
            "`@%s command` will work regardless of prefix.\n" +
            "Possible values: Any text between 1-8 characters that does not contain Discord formatting.";

    private final @NonNull SettingsConfig config;

    public @NonNull String getDisplayName() {
        return "Prefix";
    }
    public boolean isAlias(@NonNull String input) {
        return "prefix".equalsIgnoreCase(input);
    }
    public @NonNull String getDescription(@NonNull String prefix, @NonNull String tag) {
        return String.format(DESC, tag);
    }

    public @NonNull String getDefault() {
        return config.getDefaultPrefix();
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
        return verify(input).toValidation(input);
    }
    public static Verification verify(String input) {
        // The mention "@a" is 18 characters long when sent to discord
        // Telling the user the prefix is too long, or other errors, would be confusing
        if (DiscordUtils.ANY_MENTION.matcher(input).find()) {
            return Verification.invalid("The prefix cannot contain user/channel/role mentions or emojis.");
        }
        if ("unset".equalsIgnoreCase(input)) {
            return Verification.invalid("The prefix cannot be literally `unset`.");
        }
        return Verification.combineAll(
                verifyLength(input),
                verifyEndNotLetter(input),
                verifyLegalChars(input),
                verifyPattern(input, DISCORD_CONFLICT_PATTERN, DISCORD_CONFLICT_ERROR),
                verifyPattern(input, STRIKETHROUGH_CONFLICT_PATTERN, STRIKETHROUGH_CONFLICT_ERROR),
                verifyPattern(input, SPOILER_CONFLICT_PATTERN, SPOILER_CONFLICT_ERROR));
    }
    private static Verification verifyLength(String input) {
        if (input.length() > MAX_LENGTH) {
            return Verification.invalid(String.format("The prefix must be %s or fewer characters.", MAX_LENGTH));
        }
        return Verification.valid();
    }
    private static Verification verifyEndNotLetter(String input) {
        char lastChar = input.charAt(input.length() - 1);
        if (Character.isLetter(lastChar)) {
            return Verification.invalid(ENDS_WITH_LETTER_ERROR);
        }
        return Verification.valid();
    }
    private static Verification verifyLegalChars(String input) {
        for (char c : input.toCharArray()) {
            if (c < '!' || '~' < c) {
                return Verification.invalid("The prefix can only contain letters, numbers, and keyboard symbols.");
            }
        }
        return Verification.valid();
    }
    private static Verification verifyPattern(String input, Pattern pattern, String error) {
        if (pattern.matcher(input).find()) {
            return Verification.invalid(error);
        }
        return Verification.valid();
    }

    public Optional<String> get(@NonNull SettingContainer obj) {
        return obj.getPrefix();
    }
    public void set(@NonNull SettingContainer obj, @NonNull String setting) throws SQLException {
        obj.withPrefix(Optional.of(setting)).update();
    }
    public void reset(@NonNull SettingContainer obj) throws SQLException {
        obj.withPrefix(Optional.empty()).update();
    }
}
