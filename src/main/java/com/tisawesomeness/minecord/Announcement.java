package com.tisawesomeness.minecord;

import com.tisawesomeness.minecord.util.DiscordUtils;

import lombok.NonNull;

public class Announcement {

    /**
     * The text of the announcement with only constants parsed.
     */
    public final @NonNull String text;
    /**
     * <p>The weight of the announcement, where {@code weight / totalWeight} is the chance of this announcement being picked.</p>
     * <p>An announcement with a weight of 5 is five times as likely to be chosen as one with a weight of 1.</p>
     * A weight of 0 will never be picked.
     */
    public final int weight;

    /**
     * Creates a new announcement.
     * @param text The unparsed text of the announcement. Constants will be parsed.
     * @param weight The relative chance of this announcement being picked. Weights of 0 are disabled.
     * @param config The config used to parse variables.
     * @throws IllegalArgumentException If the weight is negative.
     */
    public Announcement(@NonNull String text, int weight, @NonNull Config config) {
        if (weight < 0) {
            throw new IllegalArgumentException("Announcement weight cannot be negative!");
        }
        this.text = DiscordUtils.parseConstants(text, config);
        this.weight = weight;
    }

    /**
     * Parses the variables of this announcement.
     * @return The parsed announcement text.
     */
    public String parse() {
        return DiscordUtils.parseVariables(text);
    }

}