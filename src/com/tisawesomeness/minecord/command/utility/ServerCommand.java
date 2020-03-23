package com.tisawesomeness.minecord.command.utility;

import java.awt.Color;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.RequestUtils;

import br.com.azalim.mcserverping.MCPing;
import br.com.azalim.mcserverping.MCPingOptions;
import br.com.azalim.mcserverping.MCPingResponse;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

import java.io.IOException;

public class ServerCommand extends Command {
	
	private final String serverAddressRegex = "[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,18}";
	private final String ipAddressRegex = "((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|0?[1-9]?[0-9])\\.){3}(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|0?[1-9]?[0-9])";
	private final String chatCodeRegex = "\u00A7[a-fA-Fklmnor0-9]"; //§
	
	private static Set<String> blockedServers;
	private static long timestamp = 0;
	
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
				"\n" + Database.getPrefix(e.getGuild().getIdLong()) + "server <address>[:port]";
			return new Result(Outcome.WARNING, m, 2);
		}
		String arg = args[0];
		boolean ip = true;
		if (!arg.matches(ipAddressRegex)) {
			ip = false;
			if (!arg.matches(serverAddressRegex)) {
				return new Result(Outcome.ERROR, ":x: That is not a valid server address.");
			}
		}
		
		//Query Mojang for blocked servers, cached by the hour TODO: bot should not fail if it cannot connect to mojang	
		if (System.currentTimeMillis() - 3600000 > timestamp) {
			String request = RequestUtils.get("https://sessionserver.mojang.com/blockedservers");
			if (request == null) {
				return new Result(Outcome.ERROR, ":x: The Mojang API could not be reached.");
			}
			blockedServers = new HashSet<String>(Arrays.asList(request.split("\n")));
			timestamp = System.currentTimeMillis();
		}

		String hostname = arg;
		int port = 25565;
		if (arg.contains(":")) {
			hostname = arg.substring(0, arg.indexOf(":"));
			port = Integer.parseInt(arg.substring(arg.indexOf(":") + 1));
		}
		MCPingResponse reply;
		try {
			MCPingOptions options = MCPingOptions.builder().hostname(hostname).port(port).build();
			reply = MCPing.getPing(options);
		} catch (IOException ex) {
			ex.printStackTrace();
			return new Result(Outcome.ERROR, ":x: The server is down or unreachable.");
		}

		String address = port == 25565 ? hostname : hostname + ":" + port;
		String version = reply.getVersion().getName().replaceAll(chatCodeRegex, "");
		String playerInfo = reply.getPlayers().getOnline() + "/" + reply.getPlayers().getMax();
		String motd = MarkdownSanitizer.escape(reply.getDescription().getStrippedText());
		
		//Build and format message
		String m = "**Address:** " + address +
			"\n" + "**Version:** " + version +
			"\n" + "**Players:** " + playerInfo +
			"\n" + "**MOTD:** " + motd;
		if (isBlocked(arg, ip)) m += "\n**BLOCKED BY MOJANG**";
		
		MessageEmbed me = MessageUtils.embedMessage("Server Status", null, m, Color.GREEN);
		
		//return new Result(Outcome.SUCCESS, new EmbedBuilder(me).setThumbnail(thumb).build());
		return new Result(Outcome.SUCCESS, new EmbedBuilder(me).build());
	}
	
	//Checks if a server is blocked by Mojang
	private static boolean isBlocked(String server, boolean ip) {
		server = server.toLowerCase();
		if (blockedServers.contains(sha1(server))) return true;
		if (ip) {
			int i = server.lastIndexOf('.');
			while (i >= 0) {
				i = server.lastIndexOf('.', i) - 1;
				if (blockedServers.contains(sha1(server.substring(0, i + 1) + ".*"))) return true;
			}
		} else {
			int i = 0;
			while (i != server.lastIndexOf('.') + 1) {
				i = server.indexOf('.', i) + 1;
				if (blockedServers.contains(sha1("*." + server.substring(i)))) return true;
			}
		}
		return false;
	}
	
	//Converts a string to SHA1 (from http://www.sha1-online.com/sha1-java/)
	private static String sha1(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA1");
			byte[] result = md.digest(str.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < result.length; i++) {
				sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
}
