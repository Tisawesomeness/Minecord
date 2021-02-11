package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.lang.Lang;
import com.tisawesomeness.minecord.config.serial.CommandConfig;
import com.tisawesomeness.minecord.config.serial.CommandOverride;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a command. Documentation refers to commands as the ID prefixed by {@code &}.
 * @see <a href="https://github.com/Tisawesomeness/Minecord/wiki/Adding-a-New-Command">The wiki</a>
 * for instructions on how to add your own commands.
 */
public abstract class Command {

    /**
     * Gets the ID of this command, used internally.
     * <br>Typing this ID as the command name will always work, no matter the language.
     * <br><b>Must be constant and unique for every {@link CommandRegistry}!</b>
     * <br>If you are creating only one instance of this command, {@code return "id"} is enough.
     * @return A string that should contains only lowercase letters and numbers, and start with a letter
     */
    public abstract @NonNull String getId();

    /**
     * Gets the module this command belongs to for organization purposes.
     * @return A non-null Module
     */
    public Module getModule() {
        return Module.CUSTOM;
    }

    /**
     * Gets all required user permissions.
     * <br>It is assumed that the user has send/receive message permissions, since they were able to run the command.
     * @return A possibly-empty set of permissions
     */
    public EnumSet<Permission> getUserPermissions() {
        return EnumSet.noneOf(Permission.class);
    }
    /**
     * Gets all required bot permissions.
     * <br>Read/send messages and embed links permissions are checked beforehand.
     * @return A possibly-empty set of permissions
     */
    public EnumSet<Permission> getBotPermissions() {
        return EnumSet.noneOf(Permission.class);
    }

    /**
     * This method is called when the command is run.
     * @param args An array of command arguments separated by spaces
     * @param ctx The message-specific context
     */
    public abstract void run(String[] args, CommandContext ctx);

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
    /**
     * Gets a string containing a list of command examples.
     * @return The examples text, or empty if not present
     */
    public @NonNull Optional<String> getExamples(Lang lang, String prefix, String tag) {
        return i18nfOpt(lang, "examples", prefix, tag);
    }
    /**
     * Gets a string containing a list of admin command examples.
     * @return The examples text, or empty if not present
     */
    public @NonNull Optional<String> getAdminExamples(Lang lang, String prefix, String tag) {
        Optional<String> examples = i18nfOpt(lang, "adminExamples", prefix, tag);
        if (examples.isPresent()) {
            return examples;
        }
        return getExamples(lang, prefix, tag);
    }

    public final @NonNull String i18n(Lang lang, @NonNull String key) {
        return lang.i18n(formatKey(key));
    }
    public final @NonNull String i18nf(Lang lang, @NonNull String key, Object... args) {
        return lang.i18nf(formatKey(key), args);
    }
    public final Optional<String> i18nOpt(Lang lang, @NonNull String key) {
        return lang.i18nOpt(formatKey(key));
    }
    public final Optional<String> i18nfOpt(Lang lang, @NonNull String key, Object... args) {
        return lang.i18nfOpt(formatKey(key), args);
    }
    public final List<String> i18nList(Lang lang, @NonNull String key) {
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
        int commandCooldown = getExplicitCooldown(config);
        if (commandCooldown > 0) {
            return commandCooldown;
        }
        int poolCooldown = getCooldownFromPool(config);
        if (poolCooldown > 0) {
            return poolCooldown;
        }
        return config.getDefaultCooldown();
    }
    /**
     * Gets the cooldown ID of this command.
     * <br>Multiple commands may share the same cooldown ID, and therefore the same cooldowns.
     * @param config The command config to pull cooldown pools from
     * @return The name of the cooldown pool this command is a part of, or this command's ID
     */
    public final @NonNull String getCooldownId(CommandConfig config) {
        return getCooldownPool(config).orElse(getId());
    }

    private int getExplicitCooldown(CommandConfig config) {
        CommandOverride co = config.getOverrides().get(getId());
        if (co == null) {
            return 0;
        }
        Integer cooldown = co.getCooldown();
        if (cooldown == null) {
            return 0;
        }
        return cooldown;
    }
    private int getCooldownFromPool(CommandConfig config) {
        Optional<String> poolOpt = getCooldownPool(config);
        if (poolOpt.isPresent()) {
            String pool = poolOpt.get();
            return config.getCooldownPools().get(pool);
        }
        return 0;
    }
    public Optional<String> getCooldownPool(CommandConfig config) {
        CommandOverride co = config.getOverrides().get(getId());
        if (co == null) {
            return Optional.empty();
        }
        String pool = co.getCooldownPool();
        if (pool == null) {
            return Optional.empty();
        }
        return Optional.of(pool);
    }

    /**
     * Determines if this command is enabled.
     * <br>If disabled, the command will not give a response or show in {@code &help}.
     * @param config The command config
     * @return False only if an override is set in the config
     */
    public final boolean isEnabled(CommandConfig config) {
        CommandOverride co = config.getOverrides().get(getId());
        if (co == null) {
            return true;
        }
        return !co.isDisabled();
    }

    /**
     * Displays help for this command as an embed.
     * @param ctx The context where help was requested
     * @return A help embed
     */
    public EmbedBuilder showHelp(CommandContext ctx) {
        return showHelp(ctx, false);
    }
    /**
     * Displays admin help for this command as an embed.
     * @param ctx The context where help was requested
     * @return An admin help embed
     */
    public EmbedBuilder showAdminHelp(CommandContext ctx) {
        return showHelp(ctx, true);
    }
    private EmbedBuilder showHelp(CommandContext ctx, boolean isAdmin) {
        Lang lang = ctx.getLang();
        String prefix = ctx.getPrefix();
        String tag = ctx.getE().getJDA().getSelfUser().getAsMention();

        String helpDesc = isAdmin ? getAdminHelp(lang, prefix, tag) : getHelp(lang, prefix, tag);
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(lang.i18nf("command.meta.helpTitle", formatCommandUsage(ctx)))
                .setDescription(helpDesc);

        Optional<String> examplesOpt = isAdmin ? getAdminExamples(lang, prefix, tag) : getExamples(lang, prefix, tag);
        if (examplesOpt.isPresent()) {
            eb.addField(lang.i18n("command.meta.examples"), examplesOpt.get(), false);
        }

        EnumSet<Permission> userPerms = getUserPermissions();
        if (!userPerms.isEmpty()) {
            eb.addField(lang.i18n("command.meta.userPerms"), joinPerms(userPerms), false);
        }
        EnumSet<Permission> botPerms = getBotPermissions();
        if (!botPerms.isEmpty()) {
            eb.addField(lang.i18n("command.meta.botPerms"), joinPerms(botPerms), false);
        }

        int cooldown = getCooldown(ctx.getConfig().getCommandConfig());
        if (cooldown > 0) {
            String cooldownStr = MarkdownUtil.monospace(getCooldownString(cooldown, lang));
            eb.addField(lang.i18n("command.meta.cooldown"), cooldownStr, true);
        }

        eb.addField(lang.i18n("command.meta.module"), lang.localize(getModule()), true);

        if (!getAliases(lang).isEmpty()) {
            eb.addField(lang.i18n("command.meta.aliases"), joinAliases(ctx), true);
        }

        return eb;
    }

    private String joinAliases(CommandContext ctx) {
        return getAliases(ctx.getLang()).stream()
                .map(MarkdownUtil::monospace)
                .collect(Collectors.joining(", "));
    }
    private String formatCommandUsage(CommandContext ctx) {
        String prefix = ctx.getPrefix();
        Lang lang = ctx.getLang();
        // Formatting changes based on whether the command has arguments
        Optional<String> usageOpt = getUsage(lang);
        if (usageOpt.isEmpty()) {
            if (isEnabled(ctx.getConfig().getCommandConfig())) {
                return String.format("`%s%s`", prefix, getDisplayName(lang));
            }
            return String.format("~~`%s%s`~~", prefix, getDisplayName(lang));
        }
        if (isEnabled(ctx.getConfig().getCommandConfig())) {
            return String.format("`%s%s %s`", prefix, getDisplayName(lang), usageOpt.get());
        }
        return String.format("~~`%s%s %s`~~", prefix, getDisplayName(lang), usageOpt.get());
    }
    private static String joinPerms(Collection<Permission> permissions) {
        return permissions.stream()
                .map(Permission::getName)
                .map(MarkdownUtil::monospace)
                .collect(Collectors.joining(", "));
    }
    private static String getCooldownString(int cooldown, Lang lang) {
        if (cooldown % 1000 == 0) {
            return lang.i18nf("command.meta.cooldownFormat", cooldown / 1000);
        }
        return lang.i18nf("command.meta.cooldownFormat", cooldown / 1000.0);
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof Command) {
            Command other = (Command) obj;
            return getId().equals(other.getId());
        }
        return false;
    }
    @Override
    public final int hashCode() {
        return getId().hashCode();
    }

    @Override
    public final String toString() {
        return "&" + getId();
    }

}
