package com.tisawesomeness.minecord.command.utility;

import java.awt.Color;
import org.json.JSONObject;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.RequestUtils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class ServerCommand extends Command {
	
	final String serverAddressRegex = "^((?=[a-z0-9-]{1,63}\\.)[a-z0-9]+(-[a-z0-9]+)*\\.)+[a-z0-9]+(\\2)*(:([0-5]?[0-9]{1,4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5]))?$";
	final String chatCodeRegex = "§[a-fA-Fklmnor0-9]";
	final String deleteChars = "Â";
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"server",
			"Fetches the stats of a server.",
			"<address>[:port]",
			null,
			2000,
			false,
			false,
			true
		);
	}
	
	public Result run(String[] args, MessageReceivedEvent e) {
		
		//Parse arguments
		if (args.length == 0) {
			String m = ":warning: Incorrect arguments." +
				"\n" + Config.getPrefix() + "server <address>[:port]";
			return new Result(Outcome.WARNING, m, 2);
		}
		String arg = args[0];
		if (!arg.matches(serverAddressRegex)) {
			return new Result(Outcome.ERROR, ":x: That is not a valid server address.");
		}
		
		//Send a request to MCAPI
		String url = "https://mcapi.ca/query/" + arg + "/info";
		String request = RequestUtils.get(url);
		if (request == null) {
			return new Result(Outcome.ERROR, ":x: The MCAPI could not be reached.");
		}
		
		//Check status
		JSONObject server = new JSONObject(request);
		boolean status = server.getBoolean("status");
		if (!status) {
			String error = server.getString("error");
			String m = ":x: A connection error occured: " +
					"\n" + "`" + error + "`" +
					"\n" + "The server may be offline, nonexistent, or cannot be contacted." +
					"\n" + "Did you type the IP correctly? For example," +
					"\n" + "Use `mc.hypixel.net` instead of `hypixel.net`";
			return new Result(Outcome.ERROR, m, 3);
		}
		
		//Extract JSON data
		String hostname = server.getString("hostname");
		int port = server.getInt("port");
		String version = server.getString("version");
		JSONObject players = server.getJSONObject("players");
		int online = players.getInt("online");
		int max = players.getInt("max");
		String motd = server.getString("motd");

		String address = hostname + ":" + port;
		version = clean(version).replaceAll(chatCodeRegex, "");
		String playerInfo = online + "/" + max;
		motd = clean(motd).replaceAll(chatCodeRegex, "");
		//Build and format message
		String m = "**Address:** " + address +
			"\n" + "**Version:** " + version +
			"\n" + "**Players:** " + playerInfo +
			"\n" + "**MOTD:** " + motd;
		String thumb = "https://mcapi.ca/query/" + arg + "/icon";
		e.getChannel().sendMessage("https://mcapi.ca/query/" + arg + "/icon");
		
		MessageEmbed me = MessageUtils.embedMessage("Server Status", null, m, Color.GREEN);
		
		return new Result(Outcome.SUCCESS, new EmbedBuilder(me).setThumbnail(thumb).build());
	}
	
	//Deletes each letter in the charset from the string.
	private String clean(String string) {
		if (deleteChars.length() > 0) {
			char[] chars = deleteChars.toCharArray();
			for (char c : chars) {
				string = string.replace(String.valueOf(c), "");
			}
		}
		return string;
	}
	
}
