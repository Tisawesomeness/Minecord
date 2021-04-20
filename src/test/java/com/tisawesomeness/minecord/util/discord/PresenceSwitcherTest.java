package com.tisawesomeness.minecord.util.discord;

import com.tisawesomeness.minecord.testutil.Resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

public class PresenceSwitcherTest {

    private static PresenceSwitcher switcher;

    @BeforeAll
    private static void initConfig() throws JsonProcessingException {
        switcher = new PresenceSwitcher(Resources.branding().getPresenceConfig());
    }

    @ParameterizedTest(name = "{index} ==> The switcher updates the current for behavior {0}")
    @EnumSource
    public void test(PresenceBehavior behavior) {
        assertThat(behavior.switchPresence(switcher)).isEqualTo(switcher.current());
    }

}
