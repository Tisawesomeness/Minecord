package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.Lang;
import com.tisawesomeness.minecord.config.serial.CommandConfig;
import com.tisawesomeness.minecord.config.serial.CommandOverride;

import lombok.NonNull;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Represents a command.
 */
public abstract class Command implements ICommand {

	public int uses = 0;
	public HashMap<User, Long> cooldowns = new HashMap<>();

	/**
	 * Gets the module this command belongs to for organization purposes.
	 * @return A non-null Module
	 */
	public abstract Module getModule();
	/**
	 * Gets the ID of this command, used internally.
	 * <br>Typing this ID as the command name will always work, no matter the language.
	 * @return A <b>unique</b> string that contains only lowercase letters and numbers, and starts with a letter
	 */
	public abstract @NonNull String getId();

	/**
	 * Gets the display name of this command, or how it should be displayed to the user. Defaults to the id.
	 * @param lang The language used
	 * @return A string that contains only lowercase letters and numbers, and starts with a letter
	 */
	public @NonNull String getDisplayName(Lang lang) {
		return i18nOpt(lang, "name").orElse(getId());
	}
	/**
	 * Gets a description of what this command does.
	 * @param lang The language used
	 * @return A single-line string
	 */
	public @NonNull String getDescription(Lang lang) {
		return i18nOpt(lang, "description").orElse("A command.");
	}
	/**
	 * Gets a list of the command's arguments. Uses this format:
	 * <ul>
	 *     <li>{@code word} (literal)</li>
	 *     <li>{@code <required>}</li>
	 *     <li>{@code [optional]}</li>
	 *     <li>{@code [boolean?]} (default false)</li>
	 *     <li>{@code one|two} (one or two)</li>
	 * </ul>
	 * @param lang The language used
	 * @return The usage, or empty if not defined
	 */
	public Optional<String> getUsage(Lang lang) {
		return i18nOpt(lang, "usage");
	}
	/**
	 * Gets a list of aliases for this command.
	 * @param lang The language used
	 * @return A possibly-empty list
	 */
	public List<String> getAliases(Lang lang) {
		return i18nList(lang, "aliases");
	}

	/**
	 * Defines the help text shown by {@code &help <command>}.
	 * @return Never-null help string
	 */
	public @NonNull String getHelp(Lang lang, String prefix, String tag) {
		return i18nfOpt(lang, "help", prefix, tag).orElseGet(() -> getDescription(lang));
	}
	/**
	 * Defines the help text shown by {@code &help <command> admin}.
	 * @return Never-null help string
	 */
	public @NonNull String getAdminHelp(Lang lang, String prefix, String tag) {
		Optional<String> help = i18nfOpt(lang, "adminHelp", prefix, tag);
		return help.orElseGet(() -> getHelp(lang, prefix, tag));
	}

	private Optional<String> i18nOpt(Lang lang, @NonNull String key) {
		return lang.i18nOpt(formatKey(key));
	}
	private Optional<String> i18nfOpt(Lang lang, @NonNull String key, Object... args) {
		return lang.i18nfOpt(formatKey(key), args);
	}
	private List<String> i18nList(Lang lang, @NonNull String key) {
		return lang.i18nList(formatKey(key));
	}
	private String formatKey(String key) {
		return String.format("command.%s.%s.%s", getModule().getId(), getId(), key);
	}


	/**
	 * Gets the cooldown of this command
	 * @param config The command config to pull cooldowns from
	 * @return A positive cooldown in miliseconds, or 0 or less for no cooldown
	 */
	public int getCooldown(CommandConfig config) {
		CommandOverride co = config.getOverrides().get(getId());
		if (co == null) {
			return config.getDefaultCooldown();
		}
		return co.getCooldown();
	}

	/**
	 * Represents all of the data needed to register a command.
	 */
	public static class CommandInfo {
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
		 * @param hidden Whether or not to hide the command from the help menu.
		 * @param elevated Whether or not the user must be an elevated user to execute this command.
		 * @param typing Whether or not the bot will send a typing message.
		 */
		public CommandInfo(boolean hidden, boolean elevated, boolean typing) {
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
