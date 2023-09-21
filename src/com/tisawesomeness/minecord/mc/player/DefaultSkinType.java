package com.tisawesomeness.minecord.mc.player;

import com.tisawesomeness.minecord.util.UrlUtils;

import java.net.URL;

/**
 * An enum with every possible default skin types: Steve, Alex, and the 7 new skins.
 */
public enum DefaultSkinType {
    ALEX("Alex", "1abc803022d8300ab7578b189294cce39622d9a404cdc00d3feacfdf45be6981", "46acd06e8483b176e8ea39fc12fe105eb3a2a4970f5100057e9d84d4b60bdfa7"),
    ARI("Ari", "4c05ab9e07b3505dc3ec11370c3bdce5570ad2fb2b562e9b9dd9cf271f81aa44", "6ac6ca262d67bcfb3dbc924ba8215a18195497c780058a5749de674217721892"),
    EFE("Efe", "daf3d88ccb38f11f74814e92053d92f7728ddb1a7955652a60e30cb27ae6659f", "fece7017b1bb13926d1158864b283b8b930271f80a90482f174cca6a17e88236"),
    KAI("Kai", "e5cdc3243b2153ab28a159861be643a4fc1e3c17d291cdd3e57a7f370ad676f3", "226c617fde5b1ba569aa08bd2cb6fd84c93337532a872b3eb7bf66bdd5b395f8"),
    MAKENA("Makena", "dc0fcfaf2aa040a83dc0de4e56058d1bbb2ea40157501f3e7d15dc245e493095", "7cb3ba52ddd5cc82c0b050c3f920f87da36add80165846f479079663805433db"),
    NOOR("Noor", "90e75cd429ba6331cd210b9bd19399527ee3bab467b5a9f61cb8a27b177f6789", "6c160fbd16adbc4bff2409e70180d911002aebcfa811eb6ec3d1040761aea6dd"),
    STEVE("Steve", "31f477eb1a7beee631c2ca64d06f8f68fa93a3386d04452ab27f43acdf1b60cb", "d5c4ee5ce20aed9e33e866c66caa37178606234b3721084bf01d13320fb2eb3f"),
    SUNNY("Sunny", "a3bd16079f764cd541e072e888fe43885e711f98658323db0f9a6045da91ee7a", "b66bc80f002b10371e2fa23de6f230dd5e2f3affc2e15786f65bc9be4c6eb71a"),
    ZURI("Zuri", "f5dddb41dcafef616e959c2817808e0be741c89ffbfed39134a13e75b811863d", "eee522611005acf256dbd152e992c60c0bb7978cb0f3127807700e478ad97664");

    private static final String BASE_URL = "https://textures.minecraft.net/texture/";

    private final String label;
    private final URL wideUrl;
    private final URL slimUrl;

    DefaultSkinType(String label, String wide, String slim) {
        this.label = label;
        wideUrl = UrlUtils.createUrl(BASE_URL + wide);
        slimUrl = UrlUtils.createUrl(BASE_URL + slim);
    }

    /**
     * @param model whether the skin is slim or wide
     * @return The URL of the skin
     */
    public URL getUrl(SkinModel model) {
        return model == SkinModel.SLIM ? slimUrl : wideUrl;
    }

    @Override
    public String toString() {
        return label;
    }
}
