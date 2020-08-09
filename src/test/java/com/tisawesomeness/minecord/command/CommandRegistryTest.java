package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.Lang;
import com.tisawesomeness.minecord.config.ConfigReader;
import com.tisawesomeness.minecord.config.serial.Config;
import com.tisawesomeness.minecord.database.DatabaseCache;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.dv8tion.jda.api.sharding.DefaultShardManager;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Collection;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandRegistryTest {

    private static CommandRegistry cr;

    @BeforeAll
    public static void initRegistry() throws JsonProcessingException {
        ShardManager sm = new DefaultShardManager("dummy token");
        Config config = ConfigReader.readFromResources();
        DatabaseCache dc = new DatabaseCache(null, config);
        cr = new CommandRegistry(sm, dc, config.getCommandConfig());
    }

    @ParameterizedTest(name = "{index} ==> Scanning for conflicts in {0} lang")
    @EnumSource(Lang.class)
    @DisplayName("Every command's id, display name, and aliases do not conflict with other commands.")
    public void testNameAliasConflicts(Lang lang) {
        Collection<String> inputs = new HashSet<>();
        for (Command c : cr) {
            Collection<String> temp = new HashSet<>();
            temp.add(c.getId());
            temp.add(c.getDisplayName(lang));
            temp.addAll(c.getAliases(lang));
            assertThat(inputs).doesNotContainAnyElementsOf(temp);
            inputs.addAll(temp);
        }
    }

}
