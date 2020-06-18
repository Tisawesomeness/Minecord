package com.tisawesomeness.minecord.listen;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Future;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.Registry;
import com.tisawesomeness.minecord.command.Command.CommandInfo;
import com.tisawesomeness.minecord.command.Command.Outcome;
import com.tisawesomeness.minecord.command.Command.Result;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.util.MessageUtils;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@RequiredArgsConstructor
public class CommandListener extends ListenerAdapter {

	private @NonNull Bot bot;
	
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		Message m = e.getMessage();
		if (m == null) return;

		// Get all values that change based on channel type
		String prefix;
		boolean deleteCommands = false;
		boolean canEmbed = true;
		if (e.isFromType(ChannelType.TEXT)) {
			Member sm = e.getGuild().getSelfMember();
			TextChannel tc = e.getTextChannel();
			if (!sm.hasPermission(e.getTextChannel(), Permission.MESSAGE_WRITE)) return;
			if (Database.isBanned(e.getGuild().getIdLong())) return;
			prefix = Database.getPrefix(e.getGuild().getIdLong());
			deleteCommands = sm.hasPermission(tc, Permission.MESSAGE_MANAGE) &&
					Database.getDeleteCommands(e.getGuild().getIdLong());
			canEmbed = sm.hasPermission(tc, Permission.MESSAGE_EMBED_LINKS);
		} else if (e.isFromType(ChannelType.PRIVATE)) {
			prefix = Config.getPrefix();
		} else {
			return;
		}
		
		//Check if message can be acted upon
		User a = m.getAuthor();
		if (a.isBot() || Database.isBanned(a.getIdLong())) return;
		
		String name = null;
		String[] args = null;
		
		//If the message is a valid command
		String[] content = MessageUtils.getContent(m, prefix, e.getJDA().getSelfUser());
		if (content != null) {
			
			//Extract name and argument list
			name = content[0];
			if ("".equals(name)) return; //If there is a space after prefix, don't process any more
			args = Arrays.copyOfRange(content, 1, content.length);
			
		//If the bot is mentioned and does not mention everyone
		} else if (m.isMentioned(e.getJDA().getSelfUser(), MentionType.USER) && e.isFromGuild()) {
			
			//Send the message to the logging channel
			EmbedBuilder eb = new EmbedBuilder();
			eb.setAuthor(a.getName() + " (" + a.getId() + ")", null, a.getEffectiveAvatarUrl());
			eb.setDescription("**`" + e.getGuild().getName() + "`** (" +
				e.getGuild().getId() + ") in channel `" + e.getChannel().getName() +
				"` (" + e.getChannel().getId() + ")\n" + m.getContentDisplay());
			MessageUtils.log(eb.build());
			return;
			
		//If none of the above are satisfied, get out
		} else {
			return;
		}

		// Embed links is required for 90% of commands, so send a message if the bot does not have it.
		if (!canEmbed) {
			e.getChannel().sendMessage(":warning: I need Embed Links permissions to use commands!").queue();
			return;
		}
		
		// Get command info if the command has been registered
		Command cmd = Registry.getCommand(name);
		if (cmd == null) return;
		CommandInfo ci = cmd.getInfo();
		
		//Delete message if enabled in the config and the bot has permissions
		if (deleteCommands) {
			m.delete().queue();
		}
		
		MessageChannel c = e.getChannel();

		//Check for elevation
		boolean isElevated = Database.isElevated(a.getIdLong());
		if (ci.elevated && !isElevated) {
			c.sendMessage(":warning: Insufficient permissions!").queue();
			return;
		}
		
		//Check for cooldowns, skipping if user is elevated
		if (!(Config.getElevatedSkipCooldown() && isElevated)
				&& cmd.cooldowns.containsKey(a) && ci.cooldown > 0) {
			long last = cmd.cooldowns.get(a);
			if (System.currentTimeMillis() - ci.cooldown < last) {
				//Format warning message
				long time = (long) ci.cooldown + last - System.currentTimeMillis();
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

		//Class to send typing notification every 5 seconds
		class Typing extends TimerTask {
			private Future<Void> fv = null;
			@Override
			public void run() {
				synchronized (this) {
					fv = c.sendTyping().submit();
				}
			}
		}
		
		//Instantiate timer
		Timer timer = null;
		Typing typing = null;
		if (Config.getSendTyping() && ci.typing) {
			timer = new Timer();
			typing = new Typing();
			timer.schedule(typing, 0, 5000);
		}
		
		//Run command
		CommandContext txt = new CommandContext(args, e, bot, isElevated, prefix, bot.getSettings());
		Result result = null;
		Exception exception = null;
		cmd.cooldowns.put(a, System.currentTimeMillis());
		cmd.uses++;
		try {
			result = cmd.run(txt);
		} catch (Exception ex) {
			exception = ex;
		}
		
		//Cancel typing
		if (Config.getSendTyping() && ci.typing) {
			timer.cancel();
			if (typing.fv != null) {
				typing.fv.cancel(true);
				synchronized (this) {
					notifyAll();
				}
			}
		}
		
		//Catch exceptions
		if (result == null) {
			if (exception != null) {exception.printStackTrace();}
			String err = ":x: There was an unexpected exception: `" + exception.toString() + "`\n```";
			if (Config.getDebugMode()) {
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
			MessageUtils.log(err);
			c.sendMessage(err).queue();
		//If message is empty
		} if (result.message == null) {
			if (result.outcome != null && result.outcome != Outcome.SUCCESS) {
				System.out.println("Command \"" + ci.name + "\" returned an empty " +
					result.outcome.toString().toLowerCase());
			}
		} else {
			//Wait for "typing..." to send, then print message
			//TODO: Find out if typing after sent message is client-specific
			if (result.outcome == Outcome.SUCCESS) {
				while (typing != null && typing.fv != null && !typing.fv.isDone()) {
					synchronized (this) {
						try {wait();} catch (InterruptedException ex) {}
					}
				}
				e.getChannel().sendMessage(result.message).queue();
			} else {
				//Catch errors
				if (result.outcome == Outcome.ERROR) {
					System.out.println("Command \"" + ci.name + "\" returned an error: " +
						result.message.getContentRaw());
				}
				c.sendMessage(result.message).queue();
			}
		}
	}
	
}
