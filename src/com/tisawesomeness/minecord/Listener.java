package com.tisawesomeness.minecord;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.Registry;
import com.tisawesomeness.minecord.command.Command.CommandInfo;
import com.tisawesomeness.minecord.command.Command.Outcome;
import com.tisawesomeness.minecord.command.Command.Result;
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
			
			//If the message was sent by a human and commands are enabled
			if (Registry.enabled && m != null && !m.getAuthor().isBot()) {
				
				//Extract the command name and argument list
				String mention = e.getJDA().getSelfUser().getAsMention();
				String[] msg = m.getRawContent().split(" ");
				String name = "";
				
				//If it starts with the command prefix
				if (m.getContent().startsWith(Config.getPrefix())) {
					msg = m.getContent().split(" ");
					name = msg[0].replaceFirst(Pattern.quote(Config.getPrefix()), "");
				//Stop if it mentions everyone
				} else if (m.mentionsEveryone()) {
					return;
				//If it starts with the bot mention
				} else if (m.getRawContent().replaceFirst("@!", "@").startsWith(mention)) {
					msg = ArrayUtils.removeElement(msg, msg[0]);
					name = msg[0].replaceFirst(Pattern.quote(mention), "");
				//If it mentions the bot
				} else if (m.isMentioned(e.getJDA().getSelfUser())) {
					
					//Send the message to the logging channel
					EmbedBuilder eb = new EmbedBuilder();
					eb.setAuthor(e.getAuthor().getName() + " (" + e.getAuthor().getId() + ")",
						null, e.getAuthor().getEffectiveAvatarUrl());
					eb.setDescription(m.getContent());
					MessageUtils.log(eb.build());
					return;
				//Leave if it's none of the above
				} else {
					return;
				}
				
				String[] args = ArrayUtils.removeElement(msg, msg[0]);
				
				//If the command has been registered
				if (Registry.commandMap.containsKey(name)) {
					TextChannel c = e.getTextChannel();
					
					//Delete message if enabled in the config and the bot has permissions
					if (e.getGuild().getSelfMember().hasPermission(c, Permission.MESSAGE_MANAGE)) {
						if (Config.getDeleteCommands()) {
							m.delete().complete();
						}
					} else {
						System.out.println("No permission: MESSAGE_MANAGE");
					}
					
					//Get command
					Command cmd = Registry.commandMap.get(name);
					CommandInfo ci = cmd.getInfo();

					//Check for elevation
					if (ci.elevated && !Config.getElevatedUsers().contains(e.getAuthor().getId())) {
						MessageUtils.notify(":warning: Insufficient permissions!", c);
						return;
					}
					
					//Check for cooldowns, skipping if user is elevated
					User a = e.getAuthor();
					if (!(Config.getElevatedSkipCooldown() && Config.getElevatedUsers().contains(a.getId()))
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
							MessageUtils.notify(":warning: Wait " + seconds + " more seconds.", c);
							return;
						} else {
							cmd.cooldowns.remove(a);
						}
					}
					
					//Class to send typing notification every 5 seconds
					class Typing extends TimerTask {
						Future<Void> fv = null;
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
						String err = ":x: There was an unexpected exception: `" + exception + "`";
						if (Config.getDebugMode()) {
							err = err + "\n" + exception.getStackTrace();
						}
						MessageUtils.notify(err, c);
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
									result.message.getContent());
							}
							MessageUtils.notify(result.message, c, result.notifyMultiplier);
						}
					}
					
				}
			}
		
		//Send private message to logging channel if a human sent it
		} else if (e.getChannelType() == ChannelType.PRIVATE && !e.getAuthor().isBot()) {
			EmbedBuilder eb = new EmbedBuilder();
			eb.setAuthor(e.getAuthor().getName() + " (" + e.getAuthor().getId() + ")",
				null, e.getAuthor().getEffectiveAvatarUrl());
			eb.setDescription(e.getMessage().getContent());
			eb.setThumbnail(e.getAuthor().getAvatarUrl());
			MessageUtils.log(eb.build());
		}
	}
	
	@Override
	public void onGenericGuild(GenericGuildEvent e) {
		//Update guild, channel, and user count
		Config.update();
		
		if (!(e instanceof GuildJoinEvent || e instanceof GuildLeaveEvent)) {
			return;
		}
		
		//Create message
		String type = "Joined";
		if (e instanceof GuildLeaveEvent) {
			type = "Left";
		}
		
		//Build message
		Guild guild = e.getGuild();
		Member owner = guild.getOwner();
		EmbedBuilder eb = new EmbedBuilder();
		eb.setAuthor(owner.getEffectiveName() + " (" + owner.getUser().getId() + ")",
			null, owner.getUser().getAvatarUrl());
		String add = "`";
		if (e instanceof GuildJoinEvent) {
			 add = "` with `" + guild.getMembers().size() + "` users.";
		}
		eb.setDescription(type + " guild `" + guild.getName() + add);
		eb.setThumbnail(guild.getIconUrl());
		MessageUtils.log(eb.build());
		
		RequestUtils.sendGuilds();
	}
	
}
