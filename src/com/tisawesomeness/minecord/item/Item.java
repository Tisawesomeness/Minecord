package com.tisawesomeness.minecord.item;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.util.RequestUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import net.dv8tion.jda.api.EmbedBuilder;

public class Item {

    private static final String numberRegex = "^[0-9]{1,5}$";
    private static final String[] colorNames = new String[] { "white", "orange", "magenta", "light_blue", "yellow",
            "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black" };
    private static final String[] coloredEdgeCases = new String[] { "minecraft.white_wool",
            "minecraft.white_stained_glass", "minecraft.white_terracotta", "minecraft.white_stained_glass_pane",
            "minecraft.shield.white", "minecraft.white_shulker_box", "minecraft.white_bed",
            "minecraft.white_glazed_terracotta", "minecraft.white_concrete", "minecraft.white_concrete_powder" };

    private static JSONObject items;
    private static JSONObject data;

    public static final String help = "Items can be:\n" +
        "- Namespaced IDs: `minecraft:iron_block`\n" +
        "- Numeric IDs: `50`\n" +
        "- ID and data: `35:14`, `wool:14`\n" +
        "- ID and color: `35:red`, `wool:red`\n" +
        "- Display names: `Gold Ingot`\n" +
        "- Nicknames: `Notch Apple`\n" +
        "- Previous names: `White Hardened Clay`";

    /**
     * Initializes the item database by reading from file
     * @param path The path to read from
     * @throws IOException when a file isn't found
     */
    public static void init(String path) throws IOException {
        items = RequestUtils.loadJSON(path + "/items.json");
        data = RequestUtils.loadJSON(path + "/data.json");
    }

    /**
     * Creates an EmbedBuilder from an item
     * @param item The name of the item
     * @param lang The language code to pull names from
     * @return An EmbedBuilder containing properties of the item
     */
    public static EmbedBuilder display(String item, String lang, String prefix) {
        // All objects guarenteed to be there (except properties)
        JSONObject itemObj = items.getJSONObject(item);
        JSONObject langObj = itemObj.getJSONObject("lang").getJSONObject(lang);
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
            sb.append(String.format("**Version:** %s\n", properties.getString("version")));
        }

        // Previous name and id
        String prevString = "**Previously:**";
        boolean changed = false;
        String previousID = null;
        if (properties != null && properties.has("previous_id")) {
            previousID = properties.getString("previous_id").replace(".", ":");
            prevString += String.format(" `%s`", previousID);
            changed = true;
        }
        if (langObj.has("previously")) {
            prevString += " " + langObj.getString("previously");
            changed = true;
            if (langObj.has("previously2")) {
                prevString += ", " + langObj.getString("previously2");
            }
        }
        if (changed) {
            sb.append(prevString).append("\n");
        }

        // Block form
        String blockForm = "\n**__Block Form:__**";
        changed = false;
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
            sb.append(String.format("\nTo see the item for 1.12 and below, use `%sitem %s`\n", prefix, previousID));
        }

        // Sprite
        try {
            eb.setThumbnail(new URI("https", "minecord.github.io", String.format("/item/%s.png", getImageKey(item)), null).toASCIIString());
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }

        // Get rid of trailing newline
        sb.delete(sb.length() - 1, sb.length());
        return eb.setColor(Bot.color);
    }

    /**
     * Searches the database for an item
     * @param str The query
     * @param lang The language code to search through, changing display, previous, block, and color names
     * @return The name of the item or null otherwise
     */
    public static String search(String str, String lang) {
        String toMatch = str.trim().replace("block.", "").replace("item.", "");
        if (toMatch.startsWith("minecraft")) {
            return searchIDs(toMatch);
        } else if (Character.isDigit(toMatch.charAt(0))) {
            return searchNumerical(toMatch, lang);
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
        return searchGeneral(toMatch, lang);
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
     * @param lang The language code to search through
     * @return The name of the item or null otherwise
     */
    private static String searchNumerical(String str, String lang) {
        // Convert to id:data format
        String toParse = str.replace(".", ":").replace("-", ":").replace(" : ", ":");
        String idStr;
        int data = -1;
        // If there's an id:data split, get id and data
        if (toParse.contains(":")) {
            String[] split = toParse.split(":");
            idStr = split[0];
            data = parseData(split[1], lang);
        } else {
            idStr = toParse;
        }
        // Id must be a nonnegative number
        if (!idStr.matches(numberRegex)) {
            return null;
        }
        int id = Integer.valueOf(idStr);

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

    // Easter egg error
    static class IndentationError extends RuntimeException {
        private static final long serialVersionUID = -7502746416663829075L;
        public IndentationError(String message) {
            super(message);
        }
    }

    /**
     * Searches the item database assuming the query is not id-based or numerical
     * @param str The string to search
     * @param lang The language code to search through
     * @return The name of the item or null otherwise
     */
    private static String searchGeneral(String str, String lang) {

        // Easter egg exceptions
        if (str.equalsIgnoreCase("java")) {
            throw new NullPointerException("Enjoy the RAM usage and NPEs lol");
        } else if (str.equalsIgnoreCase("python")) {
            throw new IndentationError("expected an indented block");
        }

        // Extract ID and data
        String toParse = str.replace("_", " ").replace(".", " ").replace(" : ", ":").toLowerCase();
        int data = -1;
        if (toParse.contains(":")) {
            String[] split = toParse.split(":");
            toParse = split[0];
            data = parseData(split[1], lang);
            if (data < 0) {
                return null;
            }
        }

        // Colored items special case
        for (String coloredItem : coloredEdgeCases) {
            JSONArray coloredNames = items.getJSONObject(coloredItem).getJSONObject("lang").getJSONObject(lang).getJSONArray("uncolored");
            for (int i = 0; i < coloredNames.length(); i++) {
                String coloredName = coloredNames.getString(i).toLowerCase();
                if (toParse.equals(coloredName)) {
                    if (data == 0) {
                        return coloredItem;
                    } else if (data > 0) {
                        return coloredItem.replace("white", colorNames[data]);
                    }
                } else {
                    String color = toParse.replace(coloredName, "").trim();
                    int colorData = parseDataFromFile(color, lang);
                    if (colorData == 0) {
                        return coloredItem;
                    } else if (colorData > 0) {
                        return coloredItem.replace("white", colorNames[colorData]);
                    }
                }
            }
        }

        // Banners special case
        String banner = items.getJSONObject("legacy.banner").getJSONObject("lang").getJSONObject(lang).getString("display_name");
        String standingBanner = items.getJSONObject("legacy.standing_banner").getJSONObject("lang").getJSONObject(lang).getString("internal_name");
        String wallBanner = items.getJSONObject("legacy.wall_banner").getJSONObject("lang").getJSONObject(lang).getString("internal_name");
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
        String bannerPattern = items.getJSONObject("minecraft.flower_banner_pattern").getJSONObject("lang").getJSONObject(lang).getString("display_name");
        if (toParse.equalsIgnoreCase(bannerPattern)) {
            return "minecraft.flower_banner_pattern";
        }
        // Music discs special case
        String musicDisc13 = items.getJSONObject("minecraft.music_disc_13").getJSONObject("lang").getJSONObject(lang).getString("display_name");
        if (str.equalsIgnoreCase(musicDisc13)) {
            return "minecraft.music_disc_13";
        }

        // Loop through every item
        Iterator<String> iter = items.keys();
        while (iter.hasNext()) {
            String item = iter.next();
            if (isMatch(item, toParse, -1, lang)) {
                if (data < 0) {
                    return item;
                }
                int id = getID(item);
                if (id >= 0) {
                    return searchNumerical(id + ":" + data, lang);
                }
            }
        }
        return null;
    }
    /**
     * Determines if a non-id, non-numerical search string matches an item
     * @param item The item key to check
     * @param id The id to use
     * @param data The data value to use
     * @param lang The language code to use
     * @return Whether the item matched
     */
    private static boolean isMatch(String item, String id, int data, String lang) {
        JSONObject itemObj = items.getJSONObject(item);
        JSONObject langObj = itemObj.getJSONObject("lang").getJSONObject(lang);
        JSONObject properties = itemObj.optJSONObject("properties");
        ArrayList<String> toCheck = new ArrayList<String>();
        // Data must match or not matter
        if (data < 0 || (properties != null && properties.optInt("data", 0) == data)) {
            // Display, block, and previous names (but don't match display name if another item has it)
            if (!langObj.has("name_conflict")) {
                toCheck.add(langObj.getString("display_name"));
            }
            toCheck.add(langObj.optString("block_name"));
            if (!langObj.has("previous_conflict")) {
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
            toCheck.removeIf(t -> t == null);
            toCheck.replaceAll(t -> t.toLowerCase());
            if (toCheck.contains(id)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Converts a string id to name searching format, with minecraft. removed and underscores replaced with spaces
     * @param id The id to convert
     * @return The converted id, or a blank string if a blank string is provided
     */
    private static String convertID(String id) {
        return "".equals(id) ? "" : id.replace("minecraft.", "").replace("legacy.", "").replace("_", " ");
    }
    
    /**
     * Parses a string or numerical data value
     * @param data The string to parse
     * @param lang The language code to use, changing color names
     * @return An integer data value from 0-69 or -1 otherwise
     */
    private static int parseData(String data, String lang) {
        if (data.matches(numberRegex)) {
            return Integer.valueOf(data);
        }
        return parseDataFromFile(data, lang);
    }
    /**
     * Looks up a data value in data.json
     * @param color The string to look up
     * @param lang The language code
     * @return An integer from 0-15 representing the data value, or -1 if not found
     */
    private static int parseDataFromFile(String color, String lang) {
        return data.getJSONObject(lang).optInt(color.toLowerCase().replace(" ", "_").replace("-", "_"), -1);
    }

    /**
     * Returns the display name of an item
     * @param item The item key
     * @param lang The language code
     */
    public static String getDisplayName(String item, String lang) {
        return items.getJSONObject(item).getJSONObject("lang").getJSONObject(lang).getString("display_name");
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
        return properties == null ? null : properties.optInt("id", -1);
    }

    /**
     * Gets the name of the image file used for an item
     * @param item The item key
     * @return The image filename, without the extension or URL
     */
    private static String getImageKey(String item) {
        JSONObject properties = items.getJSONObject(item).optJSONObject("properties");
        if (properties != null && properties.has("image_key")) {
            return properties.getString("image_key");
        }
        return getDisplayName(item, "en_US");
    }

}