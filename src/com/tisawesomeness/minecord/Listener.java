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

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.entities.impl.UserImpl;
import net.dv8tion.jda.core.events.guild.GuildAvailableEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.GuildUnavailableEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Listener extends ListenerAdapter {
	
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		//Process text message
		if (e.getChannelType() == ChannelType.TEXT) {
			
			if (
				//If the message starts with the right prefix and was sent by a human and commands are enabled
				Registry.enabled &&
				e.getMessage() != null &&
				e.getMessage().getContent().startsWith(Config.getPrefix()) &&
				!e.getMessage().getAuthor().isBot()
			) {
				//Extract the command name and argument list
				String[] msg = e.getMessage().getContent().split(" ");
				String name = msg[0].replaceFirst(Pattern.quote(Config.getPrefix()), "");
				String[] args = ArrayUtils.removeElement(msg, msg[0]);
				
				//If the command has been registered
				if (Registry.commandMap.containsKey(name)) {
					TextChannel c = e.getTextChannel();
					
					//Delete message if enabled in the config and the bot has permissions
					if (e.getGuild().getSelfMember().hasPermission(c, Permission.MESSAGE_MANAGE)) {
						if (Config.getDeleteCommands()) {
							e.getMessage().delete().complete();
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
					try {
						result = cmd.run(args, e);
					} catch (Exception ex) {
						exception = ex;
					}
					cmd.cooldowns.put(a, System.currentTimeMillis());
					
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
						MessageUtils.notify(":x: There was an unexpected exception: `" + exception + "`", c);
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
		
		//Process private message
		} else if (e.getChannelType() == ChannelType.PRIVATE) {
			if (e.getAuthor() != e.getJDA().getSelfUser()) {
				System.out.println("[DM] " + e.getAuthor().getName() + ": " + e.getMessage().getContent());
			}
		}
	}
	
	//Message elevated users on guild join/leave
	@Override
	public void onGuildJoin(GuildJoinEvent e) {
		Config.update();
		String m = 
			"Joined guild `" + e.getGuild().getName() +
			"` with owner `" + e.getGuild().getOwner().getEffectiveName() + "`";
		System.out.println(m);
		for (String id : Config.getElevatedUsers()) {
			User user = new UserImpl(id, (JDAImpl) e.getJDA());
			user.openPrivateChannel().complete().sendMessage(m).queue();
		}
	}
	@Override
	public void onGuildLeave(GuildLeaveEvent e) {
		Config.update();
		String m = 
			"Left guild `" + e.getGuild().getName() +
			"` with owner `" + e.getGuild().getOwner().getEffectiveName() + "`";
		System.out.println(m);
		for (String id : Config.getElevatedUsers()) {
			User user = new UserImpl(id, (JDAImpl) e.getJDA());
			user.openPrivateChannel().complete().sendMessage(m).queue();
		}
	}
	
	//Update config on guild available/unavailable
	@Override
	public void onGuildAvailable(GuildAvailableEvent e) {
		Config.update();
	}
	@Override
	public void onGuildUnavailable(GuildUnavailableEvent e) {
		Config.update();
	}
	
}
