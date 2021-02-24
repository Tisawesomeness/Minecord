package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.lang.Lang;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Collection;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandRegistryTest {

    private static final CommandRegistry cr = new CommandRegistry();

    @ParameterizedTest(name = "{index} ==> Scanning for conflicts in {0} lang")
    @EnumSource(Lang.class)
    @DisplayName("Every command's id, display name, and aliases do not conflict with other commands.")
    public void testNameAliasConflicts(Lang lang) {
        Collection<String> inputs = new HashSet<>();
        inputs.add(Lang.getDefault().i18n("command.misc.help.extra"));
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

}
