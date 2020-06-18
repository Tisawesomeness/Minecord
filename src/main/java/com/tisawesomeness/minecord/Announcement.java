package com.tisawesomeness.minecord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import com.tisawesomeness.minecord.util.DiscordUtils;

import org.json.JSONArray;
import org.json.JSONObject;

public class Announcement {

    private static ArrayList<Announcement> announcements = new ArrayList<Announcement>();
    private static int totalWeight;
    private String text;
    private int weight;

    private Announcement(String text, int weight) {
        this.text = DiscordUtils.parseConstants(text);
        this.weight = weight;
    }

    /**
     * Reads announcements from file and parses their {constants}
     * @param announcePath The path to the announce.json file
     * @throws IOException When the announce file couldn't be found
     */
    public static void read(Path announcePath) throws IOException {
        announcements = new ArrayList<>();
        JSONArray announceArr = new JSONArray(new String(Files.readAllBytes(announcePath)));
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
		while (rand >= 0) {
			i++;
			rand -= announcements.get(i).weight;
		}
		return DiscordUtils.parseVariables(announcements.get(i).text);
    }

}