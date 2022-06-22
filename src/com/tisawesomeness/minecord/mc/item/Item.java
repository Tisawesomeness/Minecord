package com.tisawesomeness.minecord.mc.item;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.util.RequestUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Item {

    private static final String[] colorNames = new String[] { "white", "orange", "magenta", "light_blue", "yellow",
            "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black" };

    private static List<String> potionMaterials = Arrays.asList("POTION", "SPLASH_POTION", "LINGERING_POTION", "TIPPED_ARROW");
    private static Map<String, String> spigotToVanillaPotionType = new HashMap<String, String>(){
        {
            put("awkward", "awkward");
            put("fire_resistance", "fire_resistance");
            put("instant_damage", "harming");
            put("instant_heal", "healing");
            put("invisibility", "invisibility");
            put("jump", "leaping");
            put("luck", "luck");
            put("mundane", "mundane");
            put("night_vision", "night_vision");
            put("poison", "poison");
            put("regen", "regeneration");
            put("slow_falling", "slow_falling");
            put("slowness", "slowness");
            put("speed", "swiftness");
            put("strength", "strength");
            put("thick", "thick");
            put("turtle_master", "turtle_master");
            put("uncraftable", "empty");
            put("water", "water");
            put("water_breathing", "water_breathing");
            put("weakness", "weakness");
        }
    };

    public static JSONObject items;
    private static JSONObject data;
    // Maps item alias to internal id (key in items.json)
    private static Map<String, String> itemAliases = new HashMap<>();

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
        System.out.println("Loaded " + items.length() + " items");
        data = RequestUtils.loadJSON(path + "/data.json");
        itemAliases.clear();
        buildItemAliases(path + "/aliases.json");
        System.out.println("Loaded " + itemAliases.size() + " item aliases");
    }
    // aliases.json generated from https://github.com/EssentialsX/ItemDbGenerator
    private static void buildItemAliases(String path) throws IOException {
        String jsonStr = Files.readAllLines(Paths.get(path)).stream()
                .filter(line -> !line.startsWith("#"))
                .collect(Collectors.joining());
        JSONObject json = new JSONObject(jsonStr);
        for (String alias : json.keySet()) {
            if (json.optJSONObject(alias) == null) {
                String primaryName = json.getString(alias);
                String internalId = getInternalId(json.getJSONObject(primaryName));
                itemAliases.put(alias, internalId);
            } else {
                String internalId = getInternalId(json.getJSONObject(alias));
                itemAliases.put(alias, internalId);
            }
        }
    }
    private static String getInternalId(JSONObject item) {
        String material = "minecraft." + item.getString("material").toLowerCase();
        JSONObject potionData = item.optJSONObject("potionData");
        if (potionData != null) {
            String spigotType = potionData.getString("type").toLowerCase();
            if (spigotType.equals("slowness")) {
                material += ".effect.slowness.iv";
            } else {
                String type = spigotToVanillaPotionType.get(spigotType);
                material += ".effect." + type;
                if (potionData.getBoolean("extended")) {
                    material += ".extended";
                } else if (potionData.getBoolean("upgraded")) {
                    material += ".ii";
                }
            }
        } else if (potionMaterials.contains(material.substring("minecraft.".length()).toUpperCase())) {
            material += ".effect.water";
        }
        return material;
    }

    /**
     * Creates an EmbedBuilder from an item
     * @param item The name of the item
     * @param lang The language code to pull names from
     * @return An EmbedBuilder containing properties of the item
     */
    public static EmbedBuilder display(String item, String lang, String prefix) {
        // All objects guaranteed to be there (except properties)
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

    /**
     * Searches the database for an item
     * @param str The query
     * @param lang The language code to search through, changing display, previous, block, and color names
     * @return The name of the item or null otherwise
     */
    public static String search(String str, String lang) {
        String toMatch = str.trim();
        if (toMatch.startsWith("minecraft")) {
            return searchIDs(toMatch);
        } else if (Character.isDigit(toMatch.charAt(0))) {
            String search = searchNumerical(toMatch, lang);
            if (search != null) {
                return search;
            }
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
        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException ignored) {
            return null;
        }

        return searchNumerical(id, data);
    }
    private static String searchNumerical(int id, int data) {
        // Banners special case
        if (id == 176 || id == 425) {
            if (data < 0) {
                return "minecraft.white_banner";
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
     * @param lang The language code to search through
     * @return The name of the item or null otherwise
     */
    private static String searchGeneral(String str, String lang) {
        // TODO also stuff like 6:orange for spruce saplings make no sense
        String toParse = normalize(str);
        if (!toParse.contains(":")) {
            return itemAliases.get(toParse);
        }
        String[] split = toParse.split(":");
        if (split.length != 2) {
            return null;
        }
        String baseItem = itemAliases.get(split[0]);
        if (baseItem == null) {
            return null;
        }
        String dataStr = split[1];

        int id = getID(baseItem);
        if (id != -1) {
            int foundData = getData(baseItem);
            // data 0 not included in items.json, so it shows up as -1
            if (foundData < 1) {
                int data = parseData(dataStr, lang);
                if (data < 0) {
                    return null;
                }
                return searchNumerical(id, data);
            }
        }
        return null;
    }
    private static String normalize(String name) {
        return name.toLowerCase().replace(" ", "");
    }

    /**
     * Parses a string or numerical data value
     * @param data The string to parse
     * @param lang The language code to use, changing color names
     * @return An integer data value or -1 otherwise
     */
    private static int parseData(String data, String lang) {
        try {
            return Integer.parseInt(data);
        } catch (NumberFormatException ignored) {
            return parseDataFromFile(data, lang);
        }
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
        return properties == null ? -1 : properties.optInt("id", -1);
    }
    /**
     * Returns the numerical data of an item
     * @param item The item key
     * @return The numerical id, or -1 if there is no data
     */
    private static int getData(String item) {
        JSONObject properties = items.getJSONObject(item).optJSONObject("properties");
        return properties == null ? -1 : properties.optInt("data", -1);
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
        return getDisplayName(item, "en_US");
    }

}
