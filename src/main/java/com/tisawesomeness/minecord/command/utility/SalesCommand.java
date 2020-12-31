package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.util.RequestUtils;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class SalesCommand extends AbstractUtilityCommand {

    public @NonNull String getId() {
        return "sales";
    }

    public void run(String[] args, CommandContext ctx) {
        ctx.triggerCooldown();

        //Send a request to Mojang
        String payload = "{\"metricKeys\":[\"item_sold_minecraft\",\"prepaid_card_redeemed_minecraft\"]}";
        String request = RequestUtils.post("https://api.mojang.com/orders/statistics", payload);
        if (request == null) {
            ctx.err("The Mojang API could not be reached.");
            return;
        }

        //Extract JSON data
        JSONObject sales = new JSONObject(request);
        int total = sales.getInt("total");
        int last24h = sales.getInt("last24h");
        double velocity = 3600 * sales.getDouble("saleVelocityPerSeconds");

        //Build and format message
        String m = "**Total Sales:** " + format(total) +
            "\n" + "**Last 24 Hours:** " + format(last24h) +
            "\n" + "**Sales Per Hour:** " + format(velocity);

        ctx.reply(new EmbedBuilder().setTitle("Minecraft Sales").setDescription(m));
    }

    private String format(double num) {
        return new DecimalFormat("#,###").format(num);
    }

}
