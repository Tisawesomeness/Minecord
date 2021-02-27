package com.tisawesomeness.minecord.config;

import com.tisawesomeness.minecord.Branding;
import com.tisawesomeness.minecord.config.serial.Config;

import lombok.NonNull;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Random;

/**
 * Holds a list of possible announcements and their weights
 */
public class AnnounceRegistry {

    private static final Random random = new Random();
    private final ArrayList<Announcement> announcements;
    private int totalWeight;

    /**
     * Reads announcements from file and parses their {constants}
     * @param announcePath The path to the announce.json file
     * @throws IOException When the announce file couldn't be found
     */
    public AnnounceRegistry(@NonNull Path announcePath, @NonNull Config config,
                            @NonNull Branding branding) throws IOException {
        announcements = new ArrayList<>();
        JSONArray announceArr = new JSONArray(new String(Files.readAllBytes(announcePath)));
        for (int i = 0; i < announceArr.length(); i++) {
            JSONObject announceObj = announceArr.getJSONObject(i);
            String text = announceObj.getString("text");
            int weight = announceObj.getInt("weight");
            announcements.add(new Announcement(text, weight, config, branding));
            totalWeight += weight;
            // Integer overflow check (should never happen, still checking because this is user input)
            if (totalWeight < 0) {
                throw new IllegalStateException("The total weight was so high it caused an integer overflow.");
            }
        }
        if (totalWeight == 0) {
            throw new IllegalStateException("The total weight cannot be 0.");
        }
    }

    /**
     * Randomly selects an announcement based on their weights and parses their {variables}
     * @param sm The ShardManager to pull variables from
     * @return The selected announcement string
     */
    public @NonNull String roll(ShardManager sm) {
        int rand = random.nextInt(totalWeight);
        int i = -1;
        while (rand >= 0) {
            i++;
            rand -= announcements.get(i).weight;
        }
        return announcements.get(i).parse(sm);
    }

}
