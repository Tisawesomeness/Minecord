package com.tisawesomeness.minecord.command.runner;

import com.tisawesomeness.minecord.Lang;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandExecutor;
import com.tisawesomeness.minecord.command.CommandRegistry;
import com.tisawesomeness.minecord.config.serial.Config;
import com.tisawesomeness.minecord.database.dao.CommandStats;
import com.tisawesomeness.minecord.mc.MCLibrary;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.StringJoiner;

/**
 * Creates a test environment used to test command input and output.
 * <br>If the command itself is deterministic, this runner will give the same output every time for the same input.
 * <br>Remember to also test with no arguments!
 * <br>
 * <br>Commands that:
 * <ul>
 *     <li>Directly query the {@link net.dv8tion.jda.api.events.message.MessageReceivedEvent MessageReceivedEvent},
 *     such as {@code &guild} or {@code &user}</li>
 *     <li>Manage the bot, such as {@code &ban} or {@code &usage}</li>
 *     <li>Require a running database, such as {@code &set} or {@code &settings}</li>
 * </ul>
 * are not supported and may either throw an exception or give a wrong response.
 * <br>
 * <br>Changes from normal execution:
 * <ul>
 *     <li>Logs are redirected to standard out.</li>
 *     <li>Users and the bot have all permissions.</li>
 *     <li>Menus are disabled.</li>
 *     <li>No footer or branding is added to embeds.</li>
 *     <li>The command is always treated as if it was executed in a guild.</li>
 * </ul>
 *
 * <br>Any non-deterministic command execution should be separated from unit tests,
 * since unit tests should give the same output no matter the environment.
 * <br>If a command requires an external resource, consider mocking that resource.
 */
@EqualsAndHashCode
public class TestCommandRunner {

    /**
     * The config file used in the {@link CommandRegistry}, {@link CommandExecutor}, and the command itself.
     * <br>To change, create a new runner.
     */
    public final @NonNull Config config;
    /**
     * The command to run.
     */
    public @Nullable Command cmd;
    /**
     * Whether the executor is an elevated user. Defaults to {@code false}.
     */
    public boolean isElevated;
    /**
     * The prefix used in command responses. Defaults to the config default.
     */
    public @NonNull String prefix;
    /**
     * The language used in command responses. Defaults to the config default.
     */
    public @Nullable Lang lang;
    /**
     * The MC library implementation.
     */
    public @Nullable MCLibrary library;

    @EqualsAndHashCode.Exclude
    private final CommandExecutor exe;

    /**
     * Creates a new runner by starting the {@link CommandRegistry} and {@link CommandExecutor}.
     * @param config The config containing the default prefix, lang, and command settings
     */
    public TestCommandRunner(@NonNull Config config) {
        this.config = config;
        prefix = config.getSettingsConfig().getDefaultPrefix();
        lang = config.getSettingsConfig().getDefaultLang();

        CommandRegistry cr = new CommandRegistry(config.getCommandConfig());
        CommandStats commandStats = new DummyCommandStats();
        exe = new CommandExecutor(cr, commandStats, config);
    }
    /**
     * Convenience constructor that calls {@link #TestCommandRunner(Config)} and sets the command at the same time.
     * @param config The config containing the default prefix, lang, and command settings
     * @param cmd The command to run
     */
    public TestCommandRunner(@NonNull Config config, @NonNull Command cmd) {
        this(config);
        this.cmd = cmd;
    }

    /**
     * Runs the command with no arguments.
     * <br>Remember to set the command ({@link #cmd}) before running.
     * @return The command context with recorded information about how the command ran
     * @throws IllegalStateException If the command is not set
     */
    public @NonNull TestContext run() {
        return run(new String[0]);
    }
    /**
     * Runs the command with arguments.
     * <br>Remember to set the command ({@link #cmd}) before running.
     * @param argumentString A string with the command arguments to be split by spaces
     * @return The command context with recorded information about how the command ran
     * @throws IllegalStateException If the command is not set
     */
    public @NonNull TestContext run(@NonNull String argumentString) {
        return run(argumentString.split(" "));
    }
    /**
     * Runs the command with arguments.
     * <br>Remember to set the command ({@link #cmd}) before running.
     * @param args An array of arguments split by spaces, may be length 0
     * @return The command context with recorded information about how the command ran
     * @throws IllegalStateException If the command is not set
     */
    public @NonNull TestContext run(String[] args) {
        if (cmd == null) {
            throw new IllegalStateException("You must set the command to run before calling run()!");
        }
        TestContext ctx = new TestContext(args, config, cmd, exe, isElevated, prefix, lang, library);
        exe.runCommand(cmd, ctx);
        return ctx;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TestCommandRunner.class.getSimpleName() + "[", "]")
                .add("config=" + config.hashCode())
                .add("cmd=" + cmd)
                .add("elevated=" + isElevated)
                .add("prefix='" + prefix + "'")
                .add("lang=" + lang)
                .add("library=" + library)
                .toString();
    }

}
