package com.tisawesomeness.minecord.command;

import java.util.HashMap;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

/**
 * Represents a command.
 */
public abstract class Command implements ICommand {
	
	public int uses = 0;
	public HashMap<User, Long> cooldowns = new HashMap<User, Long>();
	
	/**
	 * Represents all of the data needed to register a command.
	 */
	public static class CommandInfo {
		
		/**
		 * The name needed to call the command, shown on the help menu.
		 */
		public final String name;
		/**
		 * The description that appears in the help menu.
		 */
		public final String description;
		/**
		 * The command usage, such as "\<player\> [time]"
		 */
		public final String usage;
		/**
		 * A list of aliases that will also call this command.
		 */
		public final String[] aliases;
		/**
		 * The cooldown of the command in miliseconds. Enter anything less than 1 to disable the cooldown.
		 */
		public final int cooldown;
		/**
		 * Whether or not to hide the command from the help menu.
		 */
		public final boolean hidden;
		/**
		 * Whether or not the user must be an elevated user to execute this command.
		 */
		public final boolean elevated;
		/**
		 * Whether or not the bot will send a typing message.
		 */
		public final boolean typing;
		
		/**
		 * Represents all of the data needed to register a command.
		 * @param name The name needed to call the command, shown on the help menu.
		 * @param description The description shown on the help menu.
		 * @param usage The command usage, such as "&lt;player&gt; [time]"
		 * @param aliases A list of aliases that will also call this command.
		 * @param cooldown The cooldown of the command in miliseconds. Enter anything less than 1 to disable the cooldown.
		 * @param hidden Whether or not to hide the command from the help menu.
		 * @param elevated Whether or not the user must be an elevated user to execute this command.
		 * @param typing Whether or not the bot will send a typing message.
		 */
		public CommandInfo(String name, String description, String usage, String[] aliases,
				int cooldown, boolean hidden, boolean elevated, boolean typing) {
			
			if (name == null) {
				throw new IllegalArgumentException("Name cannot be null.");
			} else {
				this.name = name;
			}
			this.description = description == null ? "A command." : description;
			this.usage = usage;
			this.aliases = aliases == null ? new String[0] : aliases;
			this.cooldown = cooldown;
			this.hidden = hidden;
			this.elevated = elevated;
			this.typing = typing;
			
		}
		
	}
	
	/**
	 * Represents the result of a command.
	 */
	public static class Result {
		public Outcome outcome;
		public Message message;
		public double notifyMultiplier = 1;
		
		/**
		 * Represents the result of a command.
		 * @param outcome Represents the outcome of the command, either SUCCESS, WARNING, or ERROR.
		 */
		public Result(Outcome outcome) {
			this.outcome = outcome;
			this.message = null;
		}
		
		/**
		 * Represents the result of a command.
		 * @param outcome Represents the outcome of the command, either SUCCESS, WARNING, or ERROR.
		 * @param message The message to send.
		 */
		public Result(Outcome outcome, String message) {
			this.outcome = outcome;
			this.message = new MessageBuilder().append(message).build();
		}
		
		/**
		 * Represents the result of a command.
		 * @param outcome Represents the outcome of the command, either SUCCESS, WARNING, or ERROR.
		 * @param message The message to send.
		 */
		public Result(Outcome outcome, Message message) {
			this.outcome = outcome;
			this.message = message;
		}
		
		/**
		 * Represents the result of a command.
		 * @param outcome Represents the outcome of the command, either SUCCESS, WARNING, or ERROR.
		 * @param message The message to send.
		 */
		public Result(Outcome outcome, MessageEmbed message) {
			this.outcome = outcome;
			this.message = new MessageBuilder().setEmbed(message).build();
		}
		
		/**
		 * Represents the result of a command.
		 * @param outcome Represents the outcome of the command, either SUCCESS, WARNING, or ERROR.
		 * @param notifyMultiplier The value to multiply the notification time by.
		 */
		public Result(Outcome outcome, double notifyMultiplier) {
			this.outcome = outcome;
			this.message = null;
			this.notifyMultiplier = notifyMultiplier;
		}
		
		/**
		 * Represents the result of a command.
		 * @param outcome Represents the outcome of the command, either SUCCESS, WARNING, or ERROR.
		 * @param message The message to send.
		 * @param notifyMultiplier The value to multiply the notification time by.
		 */
		public Result(Outcome outcome, String message, double notifyMultiplier) {
			this.outcome = outcome;
			this.message = new MessageBuilder().append(message).build();
			this.notifyMultiplier = notifyMultiplier;
		}
		
		/**
		 * Represents the result of a command.
		 * @param outcome Represents the outcome of the command, either SUCCESS, WARNING, or ERROR.
		 * @param message The message to send.
		 * @param notifyMultiplier The value to multiply the notification time by.
		 */
		public Result(Outcome outcome, Message message, double notifyMultiplier) {
			this.outcome = outcome;
			this.message = message;
			this.notifyMultiplier = notifyMultiplier;
		}
		
		/**
		 * Represents the result of a command.
		 * @param outcome Represents the outcome of the command, either SUCCESS, WARNING, or ERROR.
		 * @param message The message to send.
		 * @param notifyMultiplier The value to multiply the notification time by.
		 */
		public Result(Outcome outcome, MessageEmbed message, double notifyMultiplier) {
			this.outcome = outcome;
			this.message = new MessageBuilder().setEmbed(message).build();
			this.notifyMultiplier = notifyMultiplier;
		}
	}
	
	/**
	 * Represents the end result of a command.
	 * SUCCESS - Message is sent permanently.
	 * WARNING - Message is sent temporarily.
	 * ERROR - Message is sent temporarily and logged to console.
	 */
	public enum Outcome {
		SUCCESS("Success"), WARNING("Warning"), ERROR("Error");

		private String s;
		private Outcome(String s) {
			this.s = s;
		}
		public String toString() {
			return s;
		}
	}
	
}
