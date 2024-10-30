package com.tisawesomeness.minecord.mc.recipe;

import lombok.Value;

public interface Ingredient {
    @Value
    class Item implements Ingredient {
        String item;
    }
    @Value
    class Tag implements Ingredient {
        String tag;
    }
}
