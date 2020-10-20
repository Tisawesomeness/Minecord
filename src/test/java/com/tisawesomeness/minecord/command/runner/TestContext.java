package com.tisawesomeness.minecord.command.runner;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Lang;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.CommandExecutor;
import com.tisawesomeness.minecord.command.Result;
import com.tisawesomeness.minecord.config.serial.Config;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
public class TestContext implements CommandContext {

    private static final String UNSUPPORTED = "This operation is not supported in a testing context!";

    /**
     * The result of the command according to the {@link CommandExecutor}.
     */
    @Getter protected Result result;

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

    public @NonNull MessageReceivedEvent getE() {
        throw new UnsupportedOperationException(UNSUPPORTED);
    }
    public @NonNull Bot getBot() {
        throw new UnsupportedOperationException(UNSUPPORTED);
    }

    public Result reply(@NonNull CharSequence text) {
        replies.add(text);
        return Result.SUCCESS;
    }
    public Result replyRaw(@NonNull EmbedBuilder eb) {
        embedReplies.add(eb.build());
        return Result.SUCCESS;
    }
    public Result showHelp() {
        requestedHelp = true;
        return Result.HELP;
    }

    public Result sendResult(Result result, @NonNull CharSequence text) {
        replies.add(text);
        return result;
    }

    public void triggerCooldown() {
        triggeredCooldown = true;
    }

    public @NonNull EmbedBuilder addFooter(@NonNull EmbedBuilder eb) {
        return eb;
    }
    public @NonNull EmbedBuilder brand(@NonNull EmbedBuilder eb) {
        return eb;
    }

    public boolean userHasPermission(Permission... permissions) {
        return true;
    }
    public boolean userHasPermission(Collection<Permission> permissions) {
        return true;
    }
    public boolean botHasPermission(Permission... permissions) {
        return true;
    }
    public boolean botHasPermission(Collection<Permission> permissions) {
        return true;
    }

    public boolean isFromGuild() {
        return true;
    }

    public boolean shouldUseMenus() {
        return false;
    }

    public void log(@NonNull String m) {
        System.out.println(m);
    }
    public void log(@NonNull Message m) {
        System.out.println(m.getContentRaw());
    }
    public void log(@NonNull MessageEmbed m) {
        System.out.println(m.toData());
    }

}
