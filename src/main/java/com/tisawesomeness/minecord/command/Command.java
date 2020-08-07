package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.Lang;
import com.tisawesomeness.minecord.config.serial.CommandConfig;
import com.tisawesomeness.minecord.config.serial.CommandOverride;

import lombok.NonNull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Represents a command.
 */
public abstract class Command {

    public int uses = 0;
    public HashMap<User, Long> cooldowns = new HashMap<>();

    /**
     * Gets the ID of this command, used internally.
     * <br>Typing this ID as the command name will always work, no matter the language.
     * @return A <b>unique</b> string that contains only lowercase letters and numbers, and starts with a letter
     */
    public abstract @NonNull String getId();

    /**
     * Gets the module this command belongs to for organization purposes.
     * @return A non-null Module
     */
    public Module getModule() {
        return Module.CUSTOM;
    }
    public EnumSet<Permission> getRequiredUserPermissions() {
        return EnumSet.noneOf(Permission.class);
    }
    public EnumSet<Permission> getRequiredBotPermissions() {
        return EnumSet.noneOf(Permission.class);
    }

    /**
     * This method is called when the command is run.
     * @param args An array of command arguments separated by spaces
     * @param ctx The message-specific context
     * @return The Result of the command
     */
    public abstract Result run(String[] args, CommandContext ctx);

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

    public @NonNull String i18n(Lang lang, @NonNull String key) {
        return lang.i18n(formatKey(key));
    }
    public @NonNull String i18nf(Lang lang, @NonNull String key, Object... args) {
        return lang.i18nf(formatKey(key), args);
    }
    public Optional<String> i18nOpt(Lang lang, @NonNull String key) {
        return lang.i18nOpt(formatKey(key));
    }
    public Optional<String> i18nfOpt(Lang lang, @NonNull String key, Object... args) {
        return lang.i18nfOpt(formatKey(key), args);
    }
    public List<String> i18nList(Lang lang, @NonNull String key) {
        return lang.i18nList(formatKey(key));
    }
    private String formatKey(String key) {
        return String.format("command.%s.%s.%s", getModule().getId(), getId(), key);
    }

    /**
     * Gets the cooldown of this command.
     * @param config The command config to pull cooldowns from
     * @return A positive cooldown in miliseconds, or 0 or less for no cooldown
     */
    public int getCooldown(CommandConfig config) {
        CommandOverride co = config.getOverrides().get(getId());
        if (co == null) {
            return config.getDefaultCooldown();
        }
        Integer cooldown = co.getCooldown();
        if (cooldown == null) {
            return config.getDefaultCooldown();
        }
        return cooldown;
    }
    /**
     * Determines if this command is enabled.
     * <br>If disabled, the command will not give a response or show in {@code &help}.
     * @param config The command config
     * @return False only if an override is set in the config
     */
    public boolean isEnabled(CommandConfig config) {
        CommandOverride co = config.getOverrides().get(getId());
        if (co == null) {
            return true;
        }
        return !co.isDisabled();
    }

}
