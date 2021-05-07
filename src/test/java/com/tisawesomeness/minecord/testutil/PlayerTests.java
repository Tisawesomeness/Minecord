package com.tisawesomeness.minecord.testutil;

import com.tisawesomeness.minecord.mc.player.*;
import com.tisawesomeness.minecord.util.URLs;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class PlayerTests {

    public static final UUID TIS_STEVE_UUID = UUID.fromString("f6489b79-7a9f-49e2-980e-265a05dbc3af");
    public static final Username TIS_USERNAME = new Username("Tis_awesomeness");

    public static final UUID JEB_ALEX_UUID = UUID.fromString("853c80ef-3c37-49fd-aa49-938b674adae6");
    public static final Username JEB_USERNAME = new Username("jeb_");

    public static final UUID NOTCH_STEVE_UUID = UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5");
    public static final Username NOTCH_USERNAME = new Username("Notch");

    public static final URL CUSTOM_SKIN_URL = URLs.createUrl("https://textures.minecraft.net/texture/" +
                    "8c38fdb8e126e8416edf8864d6b5f69c072836abbc8d6ebc6b3d72644e48b1bd");

    private static final Username SAMPLE = new Username("SamplePlayer");

    public static Player initPlayerWithDefaultSkin(UUID uuid) {
        return initPlayerWithDefaultSkin(uuid, SAMPLE);
    }
    public static Player initPlayerWithDefaultSkin(UUID uuid, Username name) {
        List<NameChange> history = Collections.singletonList(NameChange.original(name));
        // skin type does not matter if both URLs are null
        Profile profile = new Profile(false, false, SkinType.STEVE, null, null);
        return new Player(uuid, history, profile);
    }

    public static Player initPlayerWithSkinType(UUID uuid, SkinType skinType) {
        return initPlayerWithSkinType(uuid, SAMPLE, skinType);
    }
    public static Player initPlayerWithSkinType(UUID uuid, Username name, SkinType skinType) {
        List<NameChange> history = Collections.singletonList(NameChange.original(name));
        Profile profile = new Profile(false, false, skinType, CUSTOM_SKIN_URL, null);
        return new Player(uuid, history, profile);
    }

    public static Player initPlayerWithDefaultSkinUrl(UUID uuid, SkinType skinType) {
        return initPlayerWithDefaultSkinUrl(uuid, SAMPLE, skinType);
    }
    public static Player initPlayerWithDefaultSkinUrl(UUID uuid, Username name, SkinType skinType) {
        List<NameChange> history = Collections.singletonList(NameChange.original(name));
        URL skinUrl = skinType == SkinType.STEVE ? Player.STEVE_SKIN_URL : Player.ALEX_SKIN_URL;
        Profile profile = new Profile(false, false, skinType, skinUrl, null);
        return new Player(uuid, history, profile);
    }

}
