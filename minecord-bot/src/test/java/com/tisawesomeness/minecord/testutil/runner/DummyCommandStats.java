package com.tisawesomeness.minecord.testutil.runner;

import com.tisawesomeness.minecord.database.dao.CommandStats;

import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.multiset.HashMultiSet;

/**
 * Does no stats tracking.
 */
public class DummyCommandStats implements CommandStats {
    public MultiSet<String> getCommandUses() {
        return new HashMultiSet<>();
    }
    public void pushCommandUses(MultiSet<String> commandUses) {}
}
