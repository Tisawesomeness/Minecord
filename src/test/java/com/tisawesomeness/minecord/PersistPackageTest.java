package com.tisawesomeness.minecord;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.sharding.DefaultShardManager;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("PersistPackage")
public class PersistPackageTest {
    @Test
    @DisplayName("Pack after disassociation and reassociation is the same")
    public void testToArray() {
        Message msg = new MessageBuilder().setContent("test msg").build();
        String userTag = "Test#1234";
        ShardManager sm = new DefaultShardManager("dummy token");
        long birth = 1234L;
        Object[] objs = PersistPackage.of(msg, userTag, sm, birth).toArray();
        assertEquals(msg, objs[0]);
        assertEquals(userTag, objs[1]);
        assertEquals(sm, objs[2]);
        assertEquals(birth, objs[3]);
    }
}
