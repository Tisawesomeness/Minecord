package com.tisawesomeness.minecord;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.sharding.ShardManager;

/**
 * A container for all the information kept after a hot reload
 */
@RequiredArgsConstructor(staticName = "of")
public class PersistPackage {
    @Getter @NonNull private final Message msg;
    @Getter @NonNull private final String userTag;
    @Getter @NonNull private final ShardManager shardManager;
    @Getter private final long birth;

    /**
     * Disassociates the container into a list of objects, keeping the same order as the constructor.
     * @return An array of objects with the same length as the number of constructor arguments.
     */
    public Object[] toArray() {
        return new Object[]{msg, userTag, shardManager, birth};
    }
}
