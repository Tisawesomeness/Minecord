package com.tisawesomeness.minecord.listen;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Lang;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.Command.CommandInfo;
import com.tisawesomeness.minecord.command.Command.Outcome;
import com.tisawesomeness.minecord.command.Command.Result;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.CommandRegistry;
import com.tisawesomeness.minecord.config.serial.Config;
import com.tisawesomeness.minecord.config.serial.FlagConfig;
import com.tisawesomeness.minecord.database.DatabaseCache;
import com.tisawesomeness.minecord.database.dao.DbUser;
import com.tisawesomeness.minecord.setting.SettingRegistry;
import com.tisawesomeness.minecord.util.MessageUtils;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Future;

@RequiredArgsConstructor
public class CommandListener extends ListenerAdapter {

	private @NonNull Bot bot;
	private @NonNull Config config;
	private @NonNull CommandRegistry registry;
	
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		Message m = e.getMessage();
		if (m == null) {
			return;
		}
		DatabaseCache cache = bot.getDatabaseCache();

		// Get the settings needed before command execution
		String prefix;
		Lang lang;
		boolean canEmbed = true;

		SettingRegistry settings = bot.getSettings();
		if (e.isFromType(ChannelType.TEXT)) {
			long cid = e.getTextChannel().getIdLong();
			long gid = e.getGuild().getIdLong();
			prefix = settings.prefix.getEffective(cache, cid, gid);
			lang = settings.lang.getEffective(cache, cid, gid);
			Member sm = e.getGuild().getSelfMember();
			// No permissions or guild banned? Don't send message
			if (!sm.hasPermission(e.getTextChannel(), Permission.MESSAGE_WRITE) ||
					cache.getGuild(gid).isBanned()) {
				return;
			}
			TextChannel tc = e.getTextChannel();
			canEmbed = sm.hasPermission(tc, Permission.MESSAGE_EMBED_LINKS);
		} else if (e.isFromType(ChannelType.PRIVATE)) {
			DbUser dbUser = cache.getUser(e.getAuthor().getIdLong());
			prefix = settings.prefix.getEffective(dbUser);
			lang = settings.lang.getEffective(dbUser);
		} else {
			return;
		}
		
		// Check if message can be acted upon
		User a = m.getAuthor();
		DbUser dbUser = cache.getUser(a.getIdLong());
		if (a.isBot() || dbUser.isBanned()) {
			return;
		}
		
		String name = null;
		String[] args = null;
		
		// If the message is a valid command
		SelfUser su = e.getJDA().getSelfUser();
		FlagConfig fc = config.getFlagConfig();
		String[] content = MessageUtils.getContent(m, prefix, su, fc.isRespondToMentions());
		if (content != null) {
			
			// Extract name and argument list
			name = content[0];
			if ("".equals(name)) return; //If there is a space after prefix, don't process any more
			args = Arrays.copyOfRange(content, 1, content.length);

		// TODO temporarily (probably permanently) disabled
		// If the bot is mentioned and does not mention everyone
//		} else if (m.isMentioned(e.getJDA().getSelfUser(), MentionType.USER) && e.isFromGuild()) {
//
//			// Send the message to the logging channel
//			EmbedBuilder eb = new EmbedBuilder();
//			eb.setAuthor(a.getId() + " (" + a.getId() + ")", null, a.getEffectiveAvatarUrl());
//			eb.setDescription("**`" + e.getGuild().getId() + "`** (" +
//				e.getGuild().getId() + ") in channel `" + e.getChannel().getId() +
//				"` (" + e.getChannel().getId() + ")\n" + m.getContentDisplay());
//			MessageUtils.log(eb.build());
//			return;
			
		// If none of the above are satisfied, get out
		} else {
			return;
		}

		// Embed links is required for 90% of commands, so send a message if the bot does not have it.
		if (!canEmbed) {
			e.getChannel().sendMessage(":warning: I need Embed Links permissions to use commands!").queue();
			return;
		}
		
		// Get command info if the command has been registered
		Optional<Command> cmdOpt = registry.getCommand(name, lang);
        if (!cmdOpt.isPresent()) return;
        Command cmd = cmdOpt.get();
		CommandInfo ci = cmd.getInfo();
		
		MessageChannel c = e.getChannel();

		// Check for elevation
		boolean isElevated = dbUser.isElevated();
		if (ci.elevated && !isElevated) {
			c.sendMessage(":warning: Insufficient permissions!").queue();
			return;
		}
		
		// Check for cooldowns, skipping if user is elevated
		int cooldown = cmd.getCooldown(config.getCommandConfig());
		if (!(fc.isElevatedSkipCooldown() && isElevated)
				&& cmd.cooldowns.containsKey(a) && cooldown > 0) {
			long last = cmd.cooldowns.get(a);
			if (System.currentTimeMillis() - cooldown < last) {
				// Format warning message
				long time = (long) cooldown + last - System.currentTimeMillis();
				String seconds = String.valueOf(time);
				while (seconds.length() < 4) {
					seconds = "0" + seconds;
				}
				seconds = new StringBuilder(seconds).insert(seconds.length() - 3, ".").toString();
				c.sendMessage(":warning: Wait " + seconds + " more seconds.").queue();
				return;
			} else {
				cmd.cooldowns.remove(a);
			}
		}

		// Class to send typing notification every 5 seconds
		class Typing extends TimerTask {
			private Future<Void> fv = null;
			@Override
			public void run() {
				synchronized (this) {
					fv = c.sendTyping().submit();
				}
			}
		}
		
		// Instantiate timer
		Timer timer = null;
		Typing typing = null;
		if (fc.isSendTyping() && ci.typing) {
			timer = new Timer();
			typing = new Typing();
			timer.schedule(typing, 0, 5000);
		}
		
		// Run command
		CommandContext ctx = new CommandContext(args, e, bot, config, isElevated, prefix, lang, bot.getSettings());
		Result result = null;
		Exception exception = null;
		cmd.cooldowns.put(a, System.currentTimeMillis());
		cmd.uses++;
		try {
			result = cmd.run(ctx);
		} catch (Exception ex) {
			exception = ex;
		}
		
		// Cancel typing
		if (fc.isSendTyping() && ci.typing) {
			timer.cancel();
			if (typing.fv != null) {
				typing.fv.cancel(true);
				synchronized (this) {
					notifyAll();
				}
			}
		}
		
		// Catch exceptions
		if (result == null) {
			if (exception != null) {exception.printStackTrace();}
			String err = ":x: There was an unexpected exception: `" + exception.toString() + "`\n```";
			if (fc.isDebugMode()) {
				exception.printStackTrace();
				for (StackTraceElement ste : exception.getStackTrace()) {
					err += "\n" + ste.toString();
					String className = ste.getClassName();
					if (className.contains("net.dv8tion") || className.contains("com.neovisionaries")) {
						err += "...";
						break;
					}
				}
			}
			err += "```";
			ctx.log(err);
			c.sendMessage(err).queue();
		// If message is empty
		} if (result.message == null) {
			if (result.outcome != null && result.outcome != Outcome.SUCCESS) {
				System.out.println("Command \"" + cmd.getId() + "\" returned an empty " +
					result.outcome.toString().toLowerCase());
			}
		} else {
			// Wait for "typing..." to send, then print message
			//TODO: Find out if typing after sent message is client-specific
			if (result.outcome == Outcome.SUCCESS) {
				while (typing != null && typing.fv != null && !typing.fv.isDone()) {
					synchronized (this) {
						try {wait();} catch (InterruptedException ex) {}
					}
				}
				e.getChannel().sendMessage(result.message).queue();
			} else {
				// Catch errors
				if (result.outcome == Outcome.ERROR) {
					System.out.println("Command \"" + cmd.getId() + "\" returned an error: " +
						result.message.getContentRaw());
				}
				c.sendMessage(result.message).queue();
			}
		}
	}
	
}
