package com.tisawesomeness.minecord.util.discord;

import com.tisawesomeness.minecord.common.PresenceType;
import com.tisawesomeness.minecord.config.branding.Presence;
import com.tisawesomeness.minecord.config.branding.PresenceConfig;
import com.tisawesomeness.minecord.testutil.Reflect;
import com.tisawesomeness.minecord.testutil.Resources;
import com.tisawesomeness.minecord.util.Lists;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.dv8tion.jda.api.OnlineStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PresenceSwitcherTest {

    private static PresenceSwitcher switcher;

    @BeforeAll
    private static void initConfig() throws JsonProcessingException {
        switcher = new PresenceSwitcher(Resources.branding().getPresenceConfig());
    }

    @ParameterizedTest(name = "{index} ==> The switcher updates the current for behavior {0}")
    @DisplayName("A presence switcher with one presence always switches to the same presence")
    @EnumSource
    public void testOnePresence(PresenceBehavior behavior) {
        assertThat(behavior.switchPresence(switcher)).isEqualTo(switcher.current());
    }

    @Test
    @DisplayName("A presence switcher with two presences alternates between presences when random unique")
    public void testRandomUniqueAlternate() throws JsonProcessingException, NoSuchFieldException {
        Presence presence1 = new Presence(OnlineStatus.ONLINE, PresenceType.PLAYING, "1", null, 1);
        Presence presence2 = new Presence(OnlineStatus.ONLINE, PresenceType.PLAYING, "2", null, 1);
        List<Presence> list = Lists.of(presence1, presence2);

        PresenceConfig config = Resources.branding().getPresenceConfig();
        Reflect.setField(config, "presences", list);
        PresenceSwitcher switcher = new PresenceSwitcher(config);

        Presence switchedPresence1 = PresenceBehavior.RANDOM_UNIQUE.switchPresence(switcher);
        assertThat(list).contains(switchedPresence1);
        Presence switchedPresence2 = PresenceBehavior.RANDOM_UNIQUE.switchPresence(switcher);
        assertThat(list).contains(switchedPresence2);
        assertThat(switchedPresence2).isNotEqualTo(switchedPresence1);
    }

}
