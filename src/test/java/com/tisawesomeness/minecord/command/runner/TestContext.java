package com.tisawesomeness.minecord.command.runner;

import com.tisawesomeness.minecord.Lang;
import com.tisawesomeness.minecord.command.AbstractContext;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.CommandExecutor;
import com.tisawesomeness.minecord.command.Result;
import com.tisawesomeness.minecord.config.serial.Config;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Provides the test environment to commands and keeps track of replies.
 * <br>The methods in the "See Also" section track the output of the command.
 * <br>
 * <br>Note that the @link #getE()}, {@link #getBot()}, and all database cache methods are unsupported.
 * <br>See the docs in {@link TestCommandRunner} for changes from normal execution.
 * @see #getResult()
 * @see #getReplies()
 * @see #getEmbedReplies()
 * @see #hasRequestedHelp()
 * @see #hasTriggeredCooldown()
 */
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TestContext extends AbstractContext {

    /**
     * The result of the command according to the {@link CommandExecutor}, null if not ran yet
     */
    @Getter protected @Nullable Result result;

    // Overrides getters in AbstractContext
    @Getter private final String[] args;
    @Getter private final @NonNull Config config;
    @Getter private final @NonNull Command cmd;
    @Getter private final @NonNull CommandExecutor executor;
    @Getter private final boolean isElevated;
    @Getter private final @NonNull String prefix;
    @Getter private final Lang lang;

    private final List<CharSequence> replies = new ArrayList<>();
    private final List<MessageEmbed> embedReplies = new ArrayList<>();
    private boolean requestedHelp = false;
    private boolean triggeredCooldown = false;

    /**
     * @return An immutable list of <b>text</b> replies, in order of appearance
     */
    public List<CharSequence> getReplies() {
        return Collections.unmodifiableList(replies);
    }
    /**
     * @return An immutable list of <b>embed</b> replies, in order of appearance
     */
    public List<MessageEmbed> getEmbedReplies() {
        return Collections.unmodifiableList(embedReplies);
    }
    /**
     * @return True if {@link CommandContext#showHelp()} has been called at least once
     */
    public boolean hasRequestedHelp() {
        return requestedHelp;
    }
    /**
     * @return True if {@link CommandContext#triggerCooldown()} has been called at least once
     */
    public boolean hasTriggeredCooldown() {
        return triggeredCooldown;
    }

    @Override
    public Result reply(@NonNull CharSequence text) {
        replies.add(text);
        return Result.SUCCESS;
    }
    @Override
    public Result replyRaw(@NonNull EmbedBuilder eb) {
        embedReplies.add(eb.build());
        return Result.SUCCESS;
    }
    @Override
    public Result showHelp() {
        requestedHelp = true;
        return Result.HELP;
    }

    @Override
    public Result sendResult(Result result, @NonNull CharSequence text) {
        replies.add(text);
        return result;
    }

    @Override
    public void triggerCooldown() {
        triggeredCooldown = true;
    }

    @Override
    public @NonNull EmbedBuilder addFooter(@NonNull EmbedBuilder eb) {
        return eb;
    }
    @Override
    public @NonNull EmbedBuilder brand(@NonNull EmbedBuilder eb) {
        return eb;
    }

    public boolean userHasPermission(Permission... permissions) {
        return true;
    }
    @Override
    public boolean userHasPermission(Collection<Permission> permissions) {
        return true;
    }
    @Override
    public boolean botHasPermission(Permission... permissions) {
        return true;
    }
    @Override
    public boolean botHasPermission(Collection<Permission> permissions) {
        return true;
    }

    @Override
    public boolean isFromGuild() {
        return true;
    }

    @Override
    public boolean shouldUseMenus() {
        return false;
    }

    @Override
    public void log(@NonNull String m) {
        System.out.println(m);
    }
    @Override
    public void log(@NonNull Message m) {
        System.out.println(m.getContentRaw());
    }
    @Override
    public void log(@NonNull MessageEmbed m) {
        System.out.println(m.toData());
    }

    @Override
    protected void unsupported() {
        throw new UnsupportedOperationException("This operation is not supported in a testing context!");
    }

    @Override
    public String toString() {
        String argsStr = getArgs().length == 0 ? "" : " " + joinArgs();
        String cmdStr = String.format("'%s%s'", cmd, argsStr);
        String embedRepliesStr = embedReplies.stream()
                .map(MessageEmbed::toData)
                .map(Object::toString)
                .collect(Collectors.joining(" "));
        return new StringJoiner("\n  ", TestContext.class.getSimpleName() + "{", "\n}")
                .add(cmdStr)
                .add("elevated=" + isElevated)
                .add("config=" + config.hashCode()) // Listing all fields of config would take too long
                .add("prefix=`" + prefix + "`")
                .add("lang=" + lang)
                .add("result=" + result)
                .add("replies=" + replies)
                .add("embedReplies=" + embedRepliesStr)
                .add("help=" + requestedHelp)
                .add("cooldown=" + triggeredCooldown)
                .toString();
    }

}
