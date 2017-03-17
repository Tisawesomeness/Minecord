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
		
		if (args.length == 0) {
			String m = ":warning: Incorrect arguments." +
				"\n" + Config.getPrefix() + "server <address>[:port]";
			return new Result(Outcome.WARNING, m, 2);
		}
		String arg = args[0];
		if (!arg.matches(Config.getServerAddressRegex())) {
			return new Result(Outcome.ERROR, ":x: That is not a valid server address.");
		}
		
		//Send a request to MCAPI
		String url = "https://mcapi.ca/query/" + arg + "/info";
		String request = RequestUtils.get(url, "application/json");
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
		version = clean(version.replaceAll(Config.getChatCodeRegex(), ""), Config.getDeleteChars());
		String playerInfo = online + "/" + max;
		motd = clean(motd.replaceAll(Config.getChatCodeRegex(), ""), Config.getDeleteChars());
		
		//Build and format message
		String m = "**Address:** " + address +
			"\n" + "**Version:** " + version +
			"\n" + "**Players:** " + playerInfo +
			"\n" + "**MOTD:** " + motd;
		String thumb = "https://mcapi.ca/query/" + arg + "/icon";
		e.getChannel().sendMessage("https://mcapi.ca/query/" + arg + "/icon");
		
		MessageEmbed me = MessageUtils.wrapMessage("Server Status", null, m, Color.GREEN);
		
		return new Result(Outcome.SUCCESS, new EmbedBuilder(me).setThumbnail(thumb).build());
	}
	
	//Deletes each letter in the charset from the string.
	private String clean(String string, String charset) {
		if (charset.length() > 0) {
			char[] chars = charset.toCharArray();
			for (char c : chars) {
				string = string.replace(String.valueOf(c), "");
			}
		}
		return string;
	}
	
}
