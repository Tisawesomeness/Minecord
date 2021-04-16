package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.command.meta.Category;
import com.tisawesomeness.minecord.command.meta.Command;
import com.tisawesomeness.minecord.lang.Lang;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class CommandRegistryTest {

    // Tests ONLY that an ID contains just letters and numbers
    private static final Pattern ID_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");

    private static final CommandRegistry cr = new CommandRegistry();

    @ParameterizedTest(name = "{index} ==> Scanning for conflicts in {0} lang")
    @EnumSource
    @DisplayName("Every command's id, display name, and aliases do not conflict with other commands.")
    public void testNameAliasConflicts(Lang lang) {
        Collection<String> inputs = new HashSet<>();
        inputs.add(Lang.getDefault().i18n("command.core.help.extra"));
        inputs.add(lang.i18n("command.core.help.extra"));

        for (Command c : cr) {
            Collection<String> temp = new HashSet<>();
            String id = c.getId();
            if (lang.containsIgnoreCase(inputs, id)) {
                fail("Conflict found in command %s, lang %s, id %s",
                        c, lang, id);
            }
            temp.add(c.getId());
            String name = c.getDisplayName(lang);
            if (lang.containsIgnoreCase(inputs, name)) {
                fail("Conflict found in command %s, lang %s, display name %s",
                        c, lang, name);
            }
            temp.add(name);
            for (String alias : c.getAliases(lang)) {
                if (lang.containsIgnoreCase(inputs, alias)) {
                    fail("Conflict found in command %s, lang %s, alias %s",
                            c, lang, alias);
                }
                temp.add(alias);
            }
            inputs.addAll(temp);
        }

        for (ExtraHelpPage ehp : ExtraHelpPage.values()) {
            Collection<String> temp = new HashSet<>();
            String id = ehp.getId();
            if (lang.containsIgnoreCase(inputs, id)) {
                fail("Conflict found in extra help page %s, lang %s, id %s",
                        ehp, lang, id);
            }
            temp.add(id);
            String name = lang.localize(ehp);
            if (lang.containsIgnoreCase(inputs, name)) {
                fail("Conflict found in extra help page %s, lang %s, display name %s",
                        ehp, lang, name);
            }
            temp.add(name);
            for (String alias : ehp.getAliases(lang)) {
                if (lang.containsIgnoreCase(inputs, alias)) {
                    fail("Conflict found in extra help page %s, lang %s, alias %s",
                            ehp, lang, alias);
                }
                temp.add(alias);
            }
            inputs.addAll(temp);
        }
    }

    @ParameterizedTest(name = "{index} ==> Command {0} does not conflict with any category name")
    @DisplayName("All command IDs do not conflict with any category name in any language")
    @EnumSource
    public void testCommandIDCategoryConflict(Lang lang) {
        for (Command c : cr) {
            for (Category cat : Category.values()) {
                String categoryName = lang.localize(cat);
                if (lang.equalsIgnoreCase(categoryName, c.getId())) {
                    fail("Conflict found in command %s, lang %s, matched with category %s = command ID %s",
                            c, lang, cat, categoryName);
                }
                if (lang.equalsIgnoreCase(categoryName, c.getDisplayName(lang))) {
                    fail("Conflict found in command %s, lang %s, matched with category %s = command name %s",
                            c, lang, cat, categoryName);
                }
            }
        }
    }

    @ParameterizedTest(name = "{index} ==> Command {0} does not conflict with any category name")
    @DisplayName("All command IDs do not conflict with any category name in any language")
    @EnumSource
    public void testExtraHelpCategoryConflict(Lang lang) {
        for (ExtraHelpPage ehp : ExtraHelpPage.values()) {
            for (Category cat : Category.values()) {
                String categoryName = lang.localize(cat);
                if (lang.equalsIgnoreCase(categoryName, ehp.getId())) {
                    fail("Conflict found in extra help page %s, lang %s, matched with category %s = help ID %s",
                            ehp, lang, cat, categoryName);
                }
                if (lang.equalsIgnoreCase(categoryName, lang.localize(ehp))) {
                    fail("Conflict found in extra hep page %s, lang %s, matched with category %s = help name %s",
                            ehp, lang, cat, categoryName);
                }
            }
        }
    }

    @ParameterizedTest(name = "{index} ==> Command {0} has valid ID")
    @DisplayName("All command IDs are valid")
    @MethodSource("commandProvider")
    public void testCommandID(Command cmd) {
        assertThat(cmd.getId())
                .hasSizeBetween(1, Command.MAX_NAME_LENGTH)
                .matches(ID_PATTERN)
                .satisfies(CommandRegistryTest::startsWithAsciiLetter);
    }
    public static boolean startsWithAsciiLetter(CharSequence str) {
        char ch = str.charAt(0);
        return ('A' <= ch && ch <= 'Z') || ('a' <= ch && ch <= 'z');
    }

    private static Stream<Command> commandProvider() {
        return cr.stream();
    }

}
