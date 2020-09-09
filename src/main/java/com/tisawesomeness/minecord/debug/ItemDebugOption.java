package com.tisawesomeness.minecord.debug;

import com.tisawesomeness.minecord.item.Item;

import lombok.NonNull;

public class ItemDebugOption implements DebugOption {
    public @NonNull String getName() {
        return "item";
    }
    public @NonNull String debug(@NonNull String extra) {
        int hits = Item.getHits();
        int misses = Item.getMisses();
        int total = hits + misses;
        double rate = total == 0 ? 100.0 : 100.0 * hits / total;
        return String.format("Item search hit rate: `%d/%d %.2f%%`", hits, total, rate);
    }
}
