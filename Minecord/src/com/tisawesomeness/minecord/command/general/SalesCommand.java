package com.tisawesomeness.minecord.command.general;

import java.awt.Color;
import java.text.DecimalFormat;
import org.json.JSONObject;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.RequestUtils;

import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class SalesCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"sales",
			"Looks up the sale statistics.",
			null,
			new String[]{
				"sale",
				"sales"},
			2000,
			false,
			false,
			true
		);
	}
	
	public Result run(String[] args, MessageReceivedEvent e) {
		
		//Send a request to Mojang
		String payload = "{\"metricKeys\":[\"item_sold_minecraft\",\"prepaid_card_redeemed_minecraft\"]}";
		String request = RequestUtils.post("https://api.mojang.com/orders/statistics", payload, "application/json");
		if (request == null) {
			return new Result(Outcome.ERROR, ":x: The Mojang API could not be reached.");
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
		
		MessageEmbed me = MessageUtils.wrapMessage("Minecraft Sales", null, m, Color.GREEN);
		
		return new Result(Outcome.SUCCESS, me);
	}
	
	private String format(double num) {
		return (new DecimalFormat("#,###")).format(num);
	}
	
}
