package com.tisawesomeness.minecord.mc.recipe;

import lombok.Value;

import java.util.List;

public interface CraftResult {

    int getCount();

    @Value
    class Item implements CraftResult {
        String item;
        int count;
    }

    @Value
    class Input implements CraftResult {
        List<Ingredient> ingredients;
        int count;
    }

}
