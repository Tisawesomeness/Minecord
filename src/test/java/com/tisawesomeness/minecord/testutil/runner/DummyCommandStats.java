package com.tisawesomeness.minecord.testutil.runner;

import com.tisawesomeness.minecord.database.dao.CommandStats;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 * Does no stats tracking.
 */
public class DummyCommandStats implements CommandStats {
    public Multiset<String> getCommandUses() {
        return HashMultiset.create();
    }
    public void pushCommandUses(Multiset<String> commandUses) {}
}
