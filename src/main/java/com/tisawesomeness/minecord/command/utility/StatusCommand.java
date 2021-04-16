package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.meta.CommandContext;
import com.tisawesomeness.minecord.util.RequestUtils;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.Color;
import java.util.ArrayList;

public class StatusCommand extends AbstractUtilityCommand {

    public @NonNull String getId() {
        return "status";
    }

    public void run(String[] args, CommandContext ctx) {
        ctx.triggerCooldown();

        //Request information from Mojang
        String request = RequestUtils.get("https://status.mojang.com/check");
        if (request == null) {
            ctx.err("The Mojang API could not be reached.");
            return;
        }

        Color color = Color.GREEN;
        //Iterate over response sections
        ArrayList<String> responses = new ArrayList<String>();
        JSONArray status = new JSONArray(request);
        for (int i = 0; i < status.length(); i++) {

            //Fetch the response
            JSONObject json = status.getJSONObject(i);
            String[] names = JSONObject.getNames(json);
            String response = json.getString(names[0]);

            //Parse the response
            String output = ":x:";
            if ("green".equals(response)) {
                output = ":white_check_mark:";
            } else if ("yellow".equals(response)) {
                output = ":warning:";
                if (color != Color.RED) color = Color.YELLOW;
            } else {
                color = Color.RED;
            }

            responses.add(output);
        }

        //Build message (some status messages seem to not be working)
        String m = /*"**Minecraft:** " + responses.get(0) +
            "\n" +*/ "**Accounts:** " + responses.get(2) +
            "\n" + "**Textures:** " + responses.get(6) +
            "\n" + "**Session:** " + responses.get(1) +
            "\n" + /*"**Session Server:** " + responses.get(4) +
            "\n" + */"**Auth Server:** " + responses.get(3) +
            "\n" + /*"**Mojang:** " + responses.get(7) +
            "\n" +*/ "**Mojang API:** " + responses.get(5);
        m += "\nSome statuses in the Mojang API are broken, so they are not shown.\n" +
        "Vote, watch, and comment on [this bug](https://bugs.mojang.com/browse/WEB-2303) if you want it fixed!";

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Minecraft Status")
                .setDescription(m)
                .setColor(color);
        ctx.replyRaw(ctx.addFooter(eb));
    }

}
