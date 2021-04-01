package com.tisawesomeness.minecord.command;

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
        for (Command c : cr) {
            Collection<String> temp = new HashSet<>();
            temp.add(c.getId());
            temp.add(c.getDisplayName(lang));
            temp.addAll(c.getAliases(lang));
            assertThat(inputs)
                    .withFailMessage("Conflict found in command %s, lang %s, possible matches %s",
                            c, lang, temp)
                    .doesNotContainAnyElementsOf(temp);
            inputs.addAll(temp);
        }
        for (ExtraHelpPage ehp : ExtraHelpPage.values()) {
            Collection<String> temp = new HashSet<>();
            temp.add(lang.localize(ehp));
            temp.addAll(ehp.getAliases(lang));
            assertThat(inputs)
                    .withFailMessage("Conflict found in extra help page %s, lang %s, possible matches %s",
                            ehp, lang, temp)
                    .doesNotContainAnyElementsOf(temp);
            inputs.addAll(temp);
        }
    }

    @ParameterizedTest(name = "{index} ==> Command {0} has valid ID")
    @DisplayName("All command IDs are valid")
    @MethodSource("commandProvider")
    public void test(Command cmd) {
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
