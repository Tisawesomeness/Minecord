package com.tisawesomeness.minecord.config.branding;

import com.tisawesomeness.minecord.util.type.Verification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class Announcement {
    @JsonProperty("text")
    String text;
    @JsonProperty("weight")
    long weight;

    public Verification verify() {
        if (weight >= 0) {
            return Verification.valid();
        }
        return Verification.invalid("Announcement weight must be non-negative");
    }

}
