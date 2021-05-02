package com.tisawesomeness.minecord.config.branding;

import com.tisawesomeness.minecord.util.type.Verification;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Value;

import javax.annotation.Nullable;

@Value
public class Announcement {
    @JsonProperty("text")
    String text;
    @JsonProperty("weight") @JsonSetter(nulls = Nulls.SET)
    @Nullable Integer weight;

    public int getWeight() {
        return weight == null ? 1 : weight;
    }

    public Verification verify() {
        if (weight == null || weight >= 0) {
            return Verification.valid();
        }
        return Verification.invalid("Announcement weight must be non-negative");
    }

}
