package com.tisawesomeness.minecord;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Future;
import org.apache.commons.lang3.ArrayUtils;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.Registry;
import com.tisawesomeness.minecord.command.Command.CommandInfo;
import com.tisawesomeness.minecord.command.Command.Outcome;
import com.tisawesomeness.minecord.command.Command.Result;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.util.DiscordUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.RequestUtils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.GenericGuildEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Listener extends ListenerAdapter {
	
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		//Process text message
		if (e.getChannelType() == ChannelType.TEXT) {
			Message m = e.getMessage();
			
			//If commands are disabled, the message is null, or the sender is a bot, return
			if (!Registry.enabled || m == null || m.getAuthor().isBot()) {
				return;
			}
			
			String name = null;
			String[] args = null;
			
			//If the message is a valid command
			String[] content = MessageUtils.getContent(m, false, e.getGuild().getIdLong());
			if (content != null) {
				
				//Extract name and argument list
				name = content[0];
				if (name.equals("")) return; //If there is a space after prefix, don't process any more
				args = ArrayUtils.remove(content, 0);
				
			//If the bot is mentioned and does not mention everyone
			} else if (m.isMentioned(e.getJDA().getSelfUser()) && !m.mentionsEveryone()) {
				
				//Send the message to the logging channel
				EmbedBuilder eb = new EmbedBuilder();
				eb.setAuthor(e.getAuthor().getName() + " (" + e.getAuthor().getId() + ")",
					null, e.getAuthor().getEffectiveAvatarUrl());
				eb.setDescription("**`" + e.getGuild().getName() + "`** (" +
					e.getGuild().getId() + ") in channel `" + e.getChannel().getName() +
					"` (" + e.getChannel().getId() + ")\n" + m.getContentDisplay());
				MessageUtils.log(eb.build());
				return;
				
			//If none of the above are satisfied, get out
			} else {
				return;
			}
			
			//If the command has not been registered
			if (!Registry.commandMap.containsKey(name)) {
				return;
			}
			
			TextChannel c = e.getTextChannel();
			
			//Delete message if enabled in the config and the bot has permissions
			if (e.getGuild().getSelfMember().hasPermission(c, Permission.MESSAGE_MANAGE) && Config.getDeleteCommands()) {
				m.delete().complete();
			}
			
			//Get command info
			Command cmd = Registry.commandMap.get(name);
			CommandInfo ci = cmd.getInfo();

			//Check for elevation
			User a = e.getAuthor();
			if (ci.elevated && !Database.isElevated(a.getIdLong())) {
				c.sendMessage(":warning: Insufficient permissions!").queue();
				return;
			}
			
			//Check for cooldowns, skipping if user is elevated
			if (!(Config.getElevatedSkipCooldown() && Database.isElevated(a.getIdLong()))
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
			Result result = null;
			Exception exception = null;
			cmd.cooldowns.put(a, System.currentTimeMillis());
			cmd.uses++;
			try {
				result = cmd.run(args, e);
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
					e.getTextChannel().sendMessage(result.message).queue();
				} else {
					//Catch errors
					if (result.outcome == Outcome.ERROR) {
						System.out.println("Command \"" + ci.name + "\" returned an error: " +
							result.message.getContentRaw());
					}
					c.sendMessage(result.message).queue();
				}
			}
		
		//Send private message to logging channel if a human sent it
		} else if (e.getChannelType() == ChannelType.PRIVATE && !e.getAuthor().isBot()) {
			EmbedBuilder eb = new EmbedBuilder();
			eb.setAuthor(e.getAuthor().getName() + " (" + e.getAuthor().getId() + ")",
				null, e.getAuthor().getEffectiveAvatarUrl());
			eb.setDescription(e.getMessage().getContentDisplay());
			MessageUtils.log(eb.build());
		}
	}
	
	@Override
	public void onGenericGuild(GenericGuildEvent e) {
		
		//Get guild info
		EmbedBuilder eb = new EmbedBuilder();
		Guild guild = e.getGuild();
		Member owner = guild.getOwner();
		
		//Create embed
		if (e instanceof GuildJoinEvent) {
			
			eb.setAuthor("Joined guild!", null, owner.getUser().getAvatarUrl());
			eb.addField("Name", guild.getName(), true);
			eb.addField("Guild ID", guild.getId(), true);
			eb.addField("Owner", owner.getEffectiveName(), true);
			eb.addField("Owner ID", owner.getUser().getId(), true);
			eb.addField("Users", guild.getMembers().size() + "", true);
			ArrayList<Member> users = new ArrayList<Member>(guild.getMembers());
			for (Member u : new ArrayList<Member>(users)) {
				if (u.getUser().isBot() || u.getUser().isFake()) {
					users.remove(u);
				}
			}
			eb.addField("Humans", users.size() + "", true);
			eb.addField("Bots", guild.getMembers().size() - users.size() + "", true);
			eb.addField("Channels", guild.getTextChannels().size() + "", true);
			
		} else if (e instanceof GuildLeaveEvent) {
			eb.setAuthor(owner.getEffectiveName() + " (" + owner.getUser().getId() + ")",
				null, owner.getUser().getAvatarUrl());
			eb.setDescription("Left guild `" + guild.getName() + "` (" + guild.getId() + ")");
		} else {
			return;
		}
		
		eb.setThumbnail(guild.getIconUrl());
		MessageUtils.log(eb.build());
		RequestUtils.sendGuilds();
		DiscordUtils.update(); //Update guild, channel, and user count
		
	}
	
}
