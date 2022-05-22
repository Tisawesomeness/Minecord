package com.tisawesomeness.minecord;

import com.tisawesomeness.minecord.util.DiscordUtils;
import com.tisawesomeness.minecord.util.RequestUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class Announcement {

    private static ArrayList<Announcement> announcements = new ArrayList<>();
    private static int totalWeight;
    private final String text;
    private final int weight;

    private Announcement(String text, int weight) {
        this.text = DiscordUtils.parseConstants(text);
        this.weight = weight;
    }

    /**
     * Reads announcements from file and parses their {constants}
     * @param path The path to the announce.json file
     * @throws IOException If announce.json is not found
     */
    public static void init(String path) throws IOException {
        announcements = new ArrayList<>();
        JSONArray announceArr = RequestUtils.loadJSONArray(path + "/announce.json");
        for (int i = 0; i < announceArr.length(); i++) {
            JSONObject announceObj = announceArr.getJSONObject(i);
            int weight = announceObj.getInt("weight");
            announcements.add(new Announcement(announceObj.getString("text"), weight));
            totalWeight += weight;
        }
    }

    /**
     * Randomly selects an announcement based on their weights and parses their {variables}
     * @return The selected announcement string
     */
    public static String rollAnnouncement() {
        int rand = (int) (Math.random() * totalWeight);
        int i = -1;
        while (rand > 0) {
            i++;
            rand -= announcements.get(i).weight;
        }
        return DiscordUtils.parseVariables(announcements.get(i).text);
    }

}
