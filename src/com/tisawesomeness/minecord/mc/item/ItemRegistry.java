package com.tisawesomeness.minecord.mc.item;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.mc.FeatureFlag;
import com.tisawesomeness.minecord.mc.FeatureFlagRegistry;
import com.tisawesomeness.minecord.util.RequestUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class ItemRegistry {

    private static final Pattern CANDLE_CAKE_PATTERN = Pattern.compile("cake with (.+) candle");
    private static final String[] colorNames = new String[] { "white", "orange", "magenta", "light_blue", "yellow",
            "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black" };
    private static final String[] coloredEdgeCases = new String[] { "minecraft.white_wool",
            "minecraft.white_stained_glass", "minecraft.white_terracotta", "minecraft.white_stained_glass_pane",
            "minecraft.shield.white", "minecraft.white_shulker_box", "minecraft.white_bed",
            "minecraft.white_glazed_terracotta", "minecraft.white_concrete", "minecraft.white_concrete_powder",
            "minecraft.white_dye", "minecraft.white_candle", "minecraft.white_bundle", "minecraft.white_harness" };

    private static JSONObject items;
    private static JSONObject data;

    public static final String LANG = "en_US";
    public static final String help = "Items can be:\n" +
            "- Namespaced IDs: `minecraft:iron_block`\n" +
            "- Numeric IDs: `50`\n" +
            "- ID and data: `35:14`, `wool:14`\n" +
            "- ID and color: `35:red`, `wool:red`\n" +
            "- Display names: `Gold Ingot`\n" +
            "- Nicknames: `Notch Apple`\n" +
            "- Previous names: `White Hardened Clay`";

    private static final AtomicInteger hits = new AtomicInteger();
    private static final AtomicInteger misses = new AtomicInteger();

    /**
     * Initializes the item database by reading from file
     * @param path The path to read from
     * @throws IOException when a file isn't found
     */
    public static void init(String path) throws IOException {
        items = RequestUtils.loadJSON(path + "/items.json");
        System.out.println("Loaded " + items.length() + " items");
        data = RequestUtils.loadJSON(path + "/data.json");
    }

    /**
     * Creates an EmbedBuilder from an item
     * @param item The name of the item
     * @return An EmbedBuilder containing properties of the item
     */
    public static EmbedBuilder display(String item, String prefix) {
        // All objects guaranteed to be there (except properties)
        JSONObject itemObj = items.getJSONObject(item);
        JSONObject langObj = itemObj.getJSONObject("lang").getJSONObject(LANG);
        JSONObject properties = itemObj.optJSONObject("properties");
        EmbedBuilder eb = new EmbedBuilder();
        StringBuilder sb = eb.getDescriptionBuilder();

        // ID, display name, lore
        String displayName = langObj.getString("display_name");
        eb.setTitle(displayName);
        if (langObj.has("lore")) {
            sb.append(String.format("*%s*\n", langObj.getString("lore")));
        }
        String legacyStr = item.startsWith("legacy.") ? " (before 1.13)" : "";
        sb.append(String.format("**Namespaced ID:** `%s`%s\n", getNamespacedID(item), legacyStr));

        // Numerical id and data
        if (properties != null && properties.has("id")) {
            sb.append(String.format("**Numerical ID:** `%d`", properties.getInt("id")));
            if (properties.has("data")) {
                sb.append(String.format(", **Data:** `%d`", properties.getInt("data")));
            }
            sb.append("\n");
        }

        // Version
        if (properties != null && properties.has("version")) {
            if (properties.has("feature_flag")) {
                FeatureFlag flag = FeatureFlagRegistry.get(properties.getString("feature_flag")).get();
                sb.append(String.format("**Version:** %s (%s experiment)\n", properties.getString("version"), flag.getDisplayName()));
                flag.getReleaseVersion().ifPresent(releaseVersion -> {
                    sb.append(String.format("**Released:** %s\n", releaseVersion));
                });
            } else {
                sb.append(String.format("**Version:** %s\n", properties.getString("version")));
            }
        }

        // Previous name and id
        String prevString = getPrevString(item);
        if (prevString != null) {
            sb.append("**Previously:** ").append(prevString).append("\n");
        }

        // Block form
        String blockForm = "\n**__Block Form:__**";
        boolean changed = false;
        if (langObj.has("block_name")) {
            blockForm += " " + langObj.getString("block_name");
            changed = true;
        }
        if (properties != null && properties.has("block_id")) {
            blockForm += String.format("\n**Block ID:** `%d`", properties.getInt("block_id"));
            changed = true;
        }
        // Previous block form
        boolean changed2 = false;
        prevString = "\n**Previously:**";
        if (properties != null && properties.has("previous_block_id")) {
            prevString += String.format(" `%s`", properties.getString("previous_block_id").replace(".", ":"));
            changed = true;
            changed2 = true;
        }
        if (langObj.has("previous_block_name")) {
            prevString += " " + langObj.getString("previous_block_name");
            changed = true;
            changed2 = true;
        }
        // Build block form string
        if (changed2) {
            blockForm += prevString;
        }
        if (changed) {
            sb.append(blockForm).append("\n");
        }

        // Reference old item
        if (properties != null && properties.has("reference")) {
            String previousID = properties.getString("previous_id").replace(".", ":");
            sb.append(String.format("\nTo see the item for 1.12 and below, use `%sitem %s`\n", prefix, previousID));
        }

        // Sprite
        try {
            URL url = new URL(Config.getItemImageHost());
            String path = url.getPath() + getImageKey(item) + ".png";
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), path, url.getQuery(), url.getRef());
            eb.setThumbnail(uri.toASCIIString());
        } catch (URISyntaxException | MalformedURLException ex) {
            throw new AssertionError(ex);
        }

        // Get rid of trailing newline
        sb.delete(sb.length() - 1, sb.length());
        return eb.setColor(Bot.color);
    }
    private static String getPrevString(String item) {
        JSONObject itemObj = items.getJSONObject(item);
        JSONObject langObj = itemObj.getJSONObject("lang").getJSONObject(LANG);
        JSONObject properties = itemObj.optJSONObject("properties");

        String previously1 = langObj.optString("previously", null);
        String previously2 = langObj.optString("previously2", null);
        String previousID1 = properties == null ? null : properties.optString("previous_id", null);
        String previousID2 = properties == null ? null : properties.optString("previous_id2", null);

        String prev1 = formatPrevString(previously1, previousID1);
        if (prev1 == null) {
            return null;
        }
        String prev2 = formatPrevString(previously2, previousID2);
        if (prev2 != null) {
            return prev1 + ", " + prev2;
        } else {
            return prev1;
        }
    }
    private static String formatPrevString(String previously, String previousID) {
        if (previousID != null) {
            String formattedID = MarkdownUtil.monospace(previousID.replace(".", ":"));
            return previously == null ? formattedID : formattedID + " " + previously;
        } else {
            return previously;
        }
    }

    public static String search(String str) {
        String item = searchNoStats(str);
        if (item == null) {
            misses.incrementAndGet();
        } else {
            hits.incrementAndGet();
        }
        return item;
    }
    /**
     * Searches the database for an item
     * @param str The query
     * @return The name of the item or null otherwise
     */
    public static String searchNoStats(String str) {
        String toMatch = str.trim();
        if (toMatch.startsWith("minecraft")) {
            return searchIDs(toMatch);
        } else if (Character.isDigit(toMatch.charAt(0))) {
            String search = searchNumerical(toMatch);
            if (search != null) {
                return search;
            }
        }
        // Default potions special case
        String prepped = toMatch.replace("_", " ").replace(".", " ");
        if (prepped.equalsIgnoreCase("potion")) {
            return "minecraft.potion.effect.water";
        } else if (prepped.equalsIgnoreCase("splash potion")) {
            return "minecraft.splash_potion.effect.water";
        } else if (prepped.equalsIgnoreCase("lingering potion")) {
            return "minecraft.lingering_potion.effect.water";
        } else if (prepped.equalsIgnoreCase("tipped arrow")) {
            return "minecraft.tipped_arrow.effect.water";
        }
        return searchGeneral(toMatch);
    }

    /**
     * Searches the item database assuming minecraft:id format
     * @param str The string to search
     * @return The name of the item or null otherwise
     */
    private static String searchIDs(String str) {
        // Clean up string
        String id = str.replace("minecraft ", "minecraft.").replace(":", ".").replace(" ", "_");
        // Banners special case
        if (id.equals("minecraft.banner") || id.equals("minecraft.standing_banner")) {
            return "minecraft.white_banner";
        }
        if (id.equals("minecraft.wall_banner")) {
            return "minecraft.white_wall_banner";
        }
        // Search
        return items.has(id) ? id : null;
    }

    /**
     * Searches the item database assuming id or id:data numerical format
     * @param str The string to search
     * @return The name of the item or null otherwise
     */
    private static String searchNumerical(String str) {
        // Convert to id:data format
        String toParse = str.replace(".", ":").replace("-", ":").replace(" : ", ":");
        String idStr;
        int data = -1;
        // If there's an id:data split, get id and data
        if (toParse.contains(":")) {
            String[] split = toParse.split(":");
            idStr = split[0];
            data = parseData(split[1]);
        } else {
            idStr = toParse;
        }
        // Id must be a nonnegative number
        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException ignored) {
            return null;
        }

        // Banners special case
        if (id == 176 || id == 425) {
            if (data < 0) {
                return "legacy.banner";
            }
            return String.format("minecraft.%s_banner", colorNames[data]);
        } else if (id == 177) {
            if (data < 0) {
                return "legacy.wall_banner";
            }
            return String.format("minecraft.%s_wall_banner", colorNames[data]);
            // Flower pot special case
        } else if (id == 140) {
            return "minecraft.flower_pot";
        }

        // Look through every item
        Iterator<String> iter = items.keys();
        while (iter.hasNext()) {
            String itemID = iter.next();
            JSONObject itemObj = items.getJSONObject(itemID);
            // Check if the ids match
            if (itemObj.has("properties")) {
                JSONObject properties = itemObj.getJSONObject("properties");
                if (properties.optInt("id", -1) == id || properties.optInt("block_id", -1) == id) {
                    // Check if data matches
                    if (properties.has("data")) {
                        if (properties.getInt("data") == data) {
                            return itemID;
                        }
                    } else if (data <= 0) {
                        return itemID;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Searches the item database assuming the query is not id-based or numerical
     * @param str The string to search
     * @return The name of the item or null otherwise
     */
    private static String searchGeneral(String str) {

        // Extract ID and data
        String toParse = str.replace("_", " ").replace(".", " ").replace(" : ", ":").toLowerCase();
        int data = -1;
        if (toParse.contains(":")) {
            String[] split = toParse.split(":");
            toParse = split[0];
            if (split.length > 1) {
                data = parseData(split[1]);
                if (data < 0) {
                    return null;
                }
            }
        }

        // Colored items special case
        for (String coloredItem : coloredEdgeCases) {
            JSONArray coloredNames = items.getJSONObject(coloredItem).getJSONObject("lang").getJSONObject(LANG).getJSONArray("uncolored");
            for (int i = 0; i < coloredNames.length(); i++) {
                String coloredName = coloredNames.getString(i).toLowerCase();
                if (toParse.equals(coloredName)) {
                    if (data == 0) {
                        return coloredItem;
                    } else if (data > 0) {
                        return coloredItem.replace("white", colorNames[data]);
                    }
                } else if (toParse.contains(coloredName)) {
                    String color = toParse.replace(coloredName, "").trim();
                    int colorData = parseDataFromFile(color);
                    if (colorData == 0) {
                        return coloredItem;
                    } else if (colorData > 0) {
                        return coloredItem.replace("white", colorNames[colorData]);
                    }
                }
            }
        }

        // Colored cake candles special case
        if (toParse.endsWith(" cake candle") || toParse.endsWith(" candle cake")) {
            String color = toParse.substring(0, toParse.length() - 12).trim();
            int colorData = parseDataFromFile(color);
            if (colorData == 0) {
                return "minecraft.white_candle_cake";
            } else if (colorData > 0) {
                return "minecraft.white_candle_cake".replace("white", colorNames[colorData]);
            }
        }
        String color = CANDLE_CAKE_PATTERN.matcher(toParse).replaceFirst("$1");
        if (!color.equals(toParse)) {
            int colorData = parseDataFromFile(color);
            if (colorData == 0) {
                return "minecraft.white_candle_cake";
            } else if (colorData > 0) {
                return "minecraft.white_candle_cake".replace("white", colorNames[colorData]);
            }
        }

        // Banners special case
        String banner = items.getJSONObject("legacy.banner").getJSONObject("lang").getJSONObject(LANG).getString("display_name");
        String standingBanner = items.getJSONObject("legacy.standing_banner").getJSONObject("lang").getJSONObject(LANG).getString("internal_name");
        String wallBanner = items.getJSONObject("legacy.wall_banner").getJSONObject("lang").getJSONObject(LANG).getString("internal_name");
        if (toParse.equalsIgnoreCase(banner) || toParse.equalsIgnoreCase(standingBanner)) {
            if (data < 0) {
                return "minecraft.white_banner";
            }
            return String.format("minecraft.%s_banner", colorNames[data]);
        } else if (toParse.equalsIgnoreCase(wallBanner)) {
            if (data < 0) {
                return "minecraft.white_wall_banner";
            }
            return String.format("minecraft.%s_wall_banner", colorNames[data]);
        }
        // Banner patterns special case
        String bannerPattern = items.getJSONObject("minecraft.flower_banner_pattern").getJSONObject("lang").getJSONObject(LANG).getString("display_name");
        if (toParse.equalsIgnoreCase(bannerPattern)) {
            return "minecraft.flower_banner_pattern";
        }
        // Music discs special case
        String musicDisc13 = items.getJSONObject("minecraft.music_disc_13").getJSONObject("lang").getJSONObject(LANG).getString("display_name");
        if (str.equalsIgnoreCase(musicDisc13)) {
            return "minecraft.music_disc_13";
        }

        // Loop through every item
        Iterator<String> iter = items.keys();
        while (iter.hasNext()) {
            String item = iter.next();
            if (isMatch(item, toParse, -1)) {
                if (data < 0) {
                    return item;
                }
                int id = getID(item);
                if (id >= 0) {
                    return searchNumerical(id + ":" + data);
                }
            }
        }
        return null;
    }
    /**
     * Determines if a non-id, non-numerical search string matches an item
     * @param item The item key to check
     * @param id   The id to use
     * @param data The data value to use
     * @return Whether the item matched
     */
    private static boolean isMatch(String item, String id, int data) {
        JSONObject itemObj = items.getJSONObject(item);
        JSONObject langObj = itemObj.getJSONObject("lang").getJSONObject(LANG);
        JSONObject properties = itemObj.optJSONObject("properties");
        ArrayList<String> toCheck = new ArrayList<>();
        // Data must match or not matter
        if (data < 0 || (properties != null && properties.optInt("data", 0) == data)) {
            // Display, block, and previous names (but don't match display name if another item has it)
            if (!langObj.has("name_conflict")) {
                toCheck.add(langObj.getString("display_name"));
            }
            if (langObj.has("distinct_display_name")) {
                toCheck.add(langObj.getString("distinct_display_name"));
            }
            toCheck.add(langObj.optString("block_name"));
            if (!langObj.has("previous_conflict") || langObj.has("skip_previous")) {
                toCheck.add(langObj.optString("previously"));
                toCheck.add(langObj.optString("previously2"));
            }
            // All the custom search names
            if (langObj.has("search_names")) {
                JSONArray names = langObj.getJSONArray("search_names");
                for (int i = 0; i < names.length(); i++) {
                    toCheck.add(names.getString(i));
                }
            }
            // Actual, previous, and previous block namespaced ids
            if (properties != null) {
                if (properties.has("actual_id")) {
                    toCheck.add(convertID(properties.getString("actual_id")));
                } else {
                    toCheck.add(convertID(item));
                }
                if (!properties.has("conflict")) {
                    toCheck.add(convertID(properties.optString("previous_id")));
                }
                toCheck.add(convertID(properties.optString("previous_block_id")));

            }
            // Equals ignore case
            toCheck.removeIf(Objects::isNull);
            toCheck.replaceAll(String::toLowerCase);
            return toCheck.contains(id);
        }
        return false;
    }
    /**
     * Converts a string id to name searching format, with minecraft. removed and underscores replaced with spaces
     * @param id The id to convert
     * @return The converted id, or a blank string if a blank string is provided
     */
    private static String convertID(String id) {
        return id.isEmpty() ? "" : id.replace("minecraft.", "").replace("legacy.", "").replace("_", " ");
    }

    /**
     * Parses a string or numerical data value
     * @param data The string to parse
     * @return An integer data value from 0-69 or -1 otherwise
     */
    private static int parseData(String data) {
        try {
            return Integer.parseInt(data);
        } catch (NumberFormatException ignored) {
            return parseDataFromFile(data);
        }
    }
    /**
     * Looks up a data value in data.json
     * @param color The string to look up
     * @return An integer from 0-15 representing the data value, or -1 if not found
     */
    private static int parseDataFromFile(String color) {
        return data.getJSONObject(LANG).optInt(color.toLowerCase().replace(" ", "_").replace("-", "_"), -1);
    }

    public static String getMenuDisplayNameWithFeature(String item) {
        String displayName = getDistinctDisplayName(item);
        JSONObject properties = items.getJSONObject(item).optJSONObject("properties");
        if (properties != null) {
            String feature = properties.optString("feature_flag", "vanilla");
            Optional<FeatureFlag> flagOpt = FeatureFlagRegistry.get(feature);
            if (flagOpt.isPresent()) {
                FeatureFlag flag = flagOpt.get();
                if (!flag.isReleased()) {
                    displayName += " (" + flag.getDisplayName() + ")";
                }
            }
        }
        return displayName;
    }
    public static String getDistinctDisplayName(String item) {
        JSONObject langObj = items.getJSONObject(item).getJSONObject("lang").getJSONObject(LANG);
        String distinctDisplayName = langObj.optString("distinct_display_name", null);
        if (distinctDisplayName != null) {
            return distinctDisplayName;
        }
        return langObj.getString("display_name");
    }

    /**
     * Returns the namespaced ID (minecraft:id) of an item
     * @param item The item key
     */
    public static String getNamespacedID(String item) {
        JSONObject properties = items.getJSONObject(item).optJSONObject("properties");
        if (item.startsWith("legacy.")) {
            if (properties != null && properties.has("actual_id")) {
                return properties.getString("actual_id").replace(".", ":");
            } else {
                return item.replace("legacy.", "minecraft:");
            }
        } else if (properties != null && properties.has("actual_id")) {
            return properties.getString("actual_id").replace(".", ":");
        }
        return item.replace(".", ":");
    }

    /**
     * Returns the numerical id of an item
     * @param item The item key
     * @return The numerical id, or -1 if there is no id
     */
    private static int getID(String item) {
        JSONObject properties = items.getJSONObject(item).optJSONObject("properties");
        return properties == null ? -1 : properties.optInt("id", -1);
    }

    /**
     * Gets the name of the image file used for an item
     * @param item The item key
     * @return The image filename, without the extension or URL
     */
    public static String getImageKey(String item) {
        JSONObject properties = items.getJSONObject(item).optJSONObject("properties");
        if (properties != null && properties.has("image_key")) {
            return properties.getString("image_key");
        }
        return items.getJSONObject(item).getJSONObject("lang").getJSONObject("en_US").getString("display_name");
    }

    /**
     * @return The number of times an item search was successful
     */
    public static int getHits() {
        return hits.get();
    }
    /**
     * @return The number of times an item search failed
     */
    public static int getMisses() {
        return misses.get();
    }

}
