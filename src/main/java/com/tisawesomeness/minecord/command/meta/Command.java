package com.tisawesomeness.minecord.command.meta;

import com.tisawesomeness.minecord.command.CommandRegistry;
import com.tisawesomeness.minecord.command.ExtraHelpPage;
import com.tisawesomeness.minecord.config.config.CommandConfig;
import com.tisawesomeness.minecord.config.config.CommandOverride;
import com.tisawesomeness.minecord.config.config.Config;
import com.tisawesomeness.minecord.lang.Lang;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 *     Represents a command. Documentation refers to commands as the ID prefixed by {@code &}.
 * </p>
 * <p>
 *     A command's ID, display name, and aliases cannot conflict with...
 *     <ul>
 *         <li>Any other command's ID, display name, or aliases</li>
 *         <li>Any {@link ExtraHelpPage} ID, display name, or aliases</li>
 *         <li>The word "extra" in the default or current language as used in {@code &help extra}</li>
 *     </ul>
 *     ...for each language.
 *     <br>A command's ID and display name also cannot conflict with any category name in any language.
 * </p>
 * @see <a href="https://github.com/Tisawesomeness/Minecord/wiki/Adding-a-New-Command">The wiki</a>
 * for instructions on how to add your own commands.
 */
public abstract class Command {

    /**
     * The maximum length for command names.
     */
    public static final int MAX_NAME_LENGTH = 32;

    /**
     * Gets the ID of this command, used as its canonical, language-independent name.
     * <p>
     *     Typing this ID as the command name will always work, no matter the language.
     *     If you are creating only one instance of this command, {@code return "id"} is enough.
     * </p>
     * <p>
     *     <b>Must be unique for every {@link CommandRegistry} and constant for each instance!</b>
     *     Command IDs must have only ASCII letters and numbers (case does not matter but lowercase is preferred),
     *     must start with a letter, and have between 1 and {@link Command#MAX_NAME_LENGTH} characters.
     * </p>
     * @return A string that labels this command and satisfies the requirements above
     * @see Command for information on name conflicts
     */
    public abstract @NonNull String getId();

    /**
     * Gets the category this command belongs to for organization purposes.
     * @return A non-null Category
     */
    public Category getCategory() {
        return Category.CUSTOM;
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
     * @see Command for information on name conflicts
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
     * @see Command for information on name conflicts
     */
    public List<String> getAliases(Lang lang) {
        return i18nList(lang, "aliases");
    }

    /**
     * Defines the help text shown by {@code &help <command>}.
     * @return Never-null help string
     */
    public @NonNull String getHelp(Lang lang, String prefix, String tag, Config config) {
        return i18nfOpt(lang, "help", getHelpArgs(prefix, tag, config))
                .orElseGet(() -> getDescription(lang));
    }
    /**
     * Defines the help text shown by {@code &help <command> admin}.
     * @return Never-null help string
     */
    public @NonNull String getAdminHelp(Lang lang, String prefix, String tag, Config config) {
        return i18nfOpt(lang, "adminHelp", getHelpArgs(prefix, tag, config))
                .orElseGet(() -> getHelp(lang, prefix, tag, config));
    }
    /**
     * Creates the object array that the help menu will be formatted with.
     * By convention, {@code {0}} is the prefix and {@code {1}} is the bot's tag.
     * If the help menu needs information that can change (such as config values), they should be added here.
     * @param prefix The bot prefix
     * @param tag The bot's tag, such as {@code @Minecord}
     * @return The formatting arguments
     */
    public Object[] getHelpArgs(String prefix, String tag, Config config) {
        return new Object[]{prefix, tag};
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
    protected @NonNull String formatKey(@NonNull String key) {
        return String.format("command.%s.%s.%s", getCategory().getId(), getId(), key);
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
    public @NonNull String getCooldownId(CommandConfig config) {
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
    /**
     * Gets the cooldown pool for this command.
     * @param config The command config to pull cooldown pools from
     * @return The name of the cooldown pool if it exists
     */
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
        Config config = ctx.getConfig();

        String titleKey = isAdmin ? "command.meta.adminHelpTitle" : "command.meta.helpTitle";
        String help = isAdmin ? getAdminHelp(lang, prefix, tag, config) : getHelp(lang, prefix, tag, config);
        String desc = formatModifiers(lang, isAdmin) + help;
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(lang.i18nf(titleKey, ctx.formatCommandName(this)))
                .setDescription(desc);

        Optional<String> examplesOpt = isAdmin ? getAdminExamples(lang, prefix, tag) : getExamples(lang, prefix, tag);
        if (examplesOpt.isPresent()) {
            eb.addField(lang.i18n("command.meta.examples"), examplesOpt.get(), false);
        }

        EnumSet<Permission> userPerms = getUserPermissions();
        if (!userPerms.isEmpty()) {
            eb.addField(lang.i18n("command.meta.userPerms"), joinPerms(userPerms, lang), false);
        }
        EnumSet<Permission> botPerms = getBotPermissions();
        if (!botPerms.isEmpty()) {
            eb.addField(lang.i18n("command.meta.botPerms"), joinPerms(botPerms, lang), false);
        }

        int cooldown = getCooldown(ctx.getConfig().getCommandConfig());
        if (cooldown > 0) {
            String cooldownStr = MarkdownUtil.monospace(getCooldownString(cooldown, lang));
            eb.addField(lang.i18n("command.meta.cooldown"), cooldownStr, true);
        }

        eb.addField(lang.i18n("command.meta.category"), lang.localize(getCategory()), true);

        if (!getAliases(lang).isEmpty()) {
            eb.addField(lang.i18n("command.meta.aliases"), joinAliases(lang), true);
        }

        return eb;
    }

    private String formatModifiers(Lang lang, boolean isAdmin) {
        StringJoiner sj = new StringJoiner(", ", "", "\n\n");
        boolean modified = false;
        if (this instanceof IElevatedCommand) {
            sj.add(MarkdownUtil.underline(lang.i18n("command.meta.adminOnly")));
            modified = true;
        }
        if (this instanceof IGuildOnlyCommand) {
            IGuildOnlyCommand goThis = (IGuildOnlyCommand) this;
            if (!isAdmin || goThis.guildOnlyAppliesToAdmins()) {
                sj.add(MarkdownUtil.underline(lang.i18n("command.meta.guildOnly")));
                modified = true;
            }
        }
        if (modified) {
            return sj.toString();
        }
        return "";
    }
    private String joinAliases(Lang lang) {
        return getAliasesStream(lang)
                .map(MarkdownUtil::monospace)
                .collect(Collectors.joining(", "));
    }
    private Stream<String> getAliasesStream(Lang lang) {
        if (lang.equals(getId(), getDisplayName(lang))) {
            return getAliases(lang).stream();
        }
        return Stream.concat(getAliases(lang).stream(), Stream.of(getId()));
    }
    private static String joinPerms(Collection<Permission> permissions, Lang lang) {
        return permissions.stream()
                .map(lang::localize)
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
