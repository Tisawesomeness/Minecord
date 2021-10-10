package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.DiscordUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.RequestUtils;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class StatusCommand extends Command {

	private static final List<String> URLS = Arrays.asList(
			"https://minecraft.net",
			"https://account.mojang.com",
			"https://authserver.mojang.com",
			"https://textures.minecraft.net",
			"https://api.mojang.com"
	);
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"status",
			"Checks the status of Mojang servers.",
			null,
			null,
			2000,
			false,
			false,
			true
		);
	}
	
	public Result run(String[] args, MessageReceivedEvent e) {
		// Pings done in separate threads to speed up in case some URLs timeout
		List<CompletableFuture<Boolean>> statusRequests = URLS.stream()
				.map(StatusCommand::checkUrl)
				.collect(Collectors.toList());
		CompletableFuture.allOf(statusRequests.toArray(new CompletableFuture[URLS.size()])).join();
		List<Boolean> statuses = statusRequests.stream()
				.map(CompletableFuture::join)
				.collect(Collectors.toList());

		// Transform list of true/false into emote check/x
		List<String> statusEmotes = statuses.stream()
				.map(DiscordUtils::getBoolEmote)
				.collect(Collectors.toList());

		String m = "**Minecraft:** " + statusEmotes.get(0) +
				"\n" + "**Accounts:** " + statusEmotes.get(1) +
				"\n" + "**Auth Server:** " + statusEmotes.get(2) +
				"\n" + "**Textures:** " + statusEmotes.get(3) +
				"\n" + "**Mojang API:** " + statusEmotes.get(4);

		boolean allGood = statuses.stream().allMatch(b -> b);
		Color color = allGood ? Color.GREEN : Color.RED;
		
		MessageEmbed me = MessageUtils.embedMessage("Minecraft Status", null, m, color);
		return new Result(Outcome.SUCCESS, me);
	}

	private static CompletableFuture<Boolean> checkUrl(String url) {
		return CompletableFuture.supplyAsync(() -> check(url));
	}
	private static boolean check(String url) {
		// The Minecraft website likes to bug out for some reason, regular GET request sometimes breaks
		if (url.equals("https://minecraft.net")) {
			return RequestUtils.checkWithSocket("minecraft.net");
		}
		return RequestUtils.checkURLWithGet(url);
	}
	
}
