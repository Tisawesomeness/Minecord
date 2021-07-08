package com.tisawesomeness.minecord.testutil.runner;

import com.tisawesomeness.minecord.command.CommandExecutor;
import com.tisawesomeness.minecord.command.meta.Command;
import com.tisawesomeness.minecord.command.meta.CommandContext;
import com.tisawesomeness.minecord.command.meta.Result;
import com.tisawesomeness.minecord.config.config.Config;
import com.tisawesomeness.minecord.lang.Lang;
import com.tisawesomeness.minecord.mc.MCLibrary;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.junit.jupiter.api.Assertions;

import javax.annotation.Nullable;
import java.awt.Color;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Provides the test environment to commands and keeps track of replies.
 * <br>The methods in the "See Also" section track the output of the command.
 * <br>
 * <br>Note that the @link #getE()}, {@link #getBot()}, and all database cache methods are unsupported.
 * <br>See the docs in {@link TestCommandRunner} for changes from normal execution.
 * @implSpec This class is thread-safe outside of the runner package.
 * @see #getResult()
 * @see #getReplies()
 * @see #getEmbedReplies()
 * @see #hasRequestedHelp()
 * @see #hasTriggeredCooldown()
 */
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TestContext extends AbstractContext {

    // Overrides getters in AbstractContext
    @Getter private final String[] args;
    @Getter private final @NonNull Config config;
    @Getter private final @NonNull Command cmd;
    @Getter private final @NonNull CommandExecutor executor;
    @Getter private final boolean isElevated;
    @Getter private final @NonNull String rawPrefix;
    @Getter private final Lang lang;

    private final @Nullable MCLibrary library;

    /**
     * The result of the command according to the latest reply, null if not ran yet
     */
    @Getter private @Nullable Result result;
    private final List<CharSequence> replies = new ArrayList<>();
    private final List<MessageEmbed> embedReplies = new ArrayList<>();
    private boolean requestedHelp;
    private boolean triggeredCooldown;

    private final BlockingQueue<Object> gate = new LinkedBlockingQueue<>(1);
    private @Nullable Predicate<View> condition;
    private final Object lock = new Object();


    @Override
    protected void sendMessage(@NonNull CharSequence text) {
        if (text.length() > Message.MAX_CONTENT_LENGTH) {
            throw new IllegalArgumentException(String.format("Message length %d is too long!", text.length()));
        }
        replies.add(text);
    }
    @Override
    protected void sendMessage(@NonNull MessageEmbed emb) {
        if (!emb.isSendable()) {
            throw new IllegalArgumentException(String.format("Embed length %d is too long!", emb.getLength()));
        }
        embedReplies.add(emb);
    }
    @Override
    public void requestHelp() {
        requestedHelp = true;
    }

    @Override
    public void reply(@NonNull CharSequence text) {
        sendMessage(text);
        result = Result.SUCCESS;
        onUpdate();
    }
    @Override
    public void reply(@NonNull MessageEmbed emb) {
        sendMessage(emb);
        result = Result.SUCCESS;
        onUpdate();
    }
    @Override
    public void showHelp() {
        requestHelp();
        result = Result.HELP;
        onUpdate();
    }
    @Override
    public void sendResult(Result result, @NonNull CharSequence text) {
        sendMessage(text);
        this.result = result;
        onUpdate();
    }
    @Override
    public void triggerCooldown() {
        triggeredCooldown = true;
        onUpdate();
    }

    @Override
    public @NonNull EmbedBuilder addFooter(@NonNull EmbedBuilder eb) {
        return eb;
    }
    @Override
    public @NonNull Color getColor() {
        return Color.GREEN;
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
    public void handleException(Throwable ex) {
        Assertions.fail(ex);
    }

    @Override
    public long getUserId() {
        return -1;
    }

    @Override
    public @NonNull MCLibrary getMCLibrary() {
        if (library != null) {
            return library;
        }
        unsupported();
        throw new AssertionError("Unreachable");
    }


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


    /**
     * Blocks until the given condition is met. Only one thread may wait for a condition at a time, if another thread
     * is currently waiting, that thread must finish before this thread can start.
     * @param condition A Predicate that tests for a specific condition, see {@link View} for the available methods
     * @param timeout How long to wait before timing out
     * @param unit The time unit of the timeout duration
     * @return True if the condition became true before the timeout expired, false otherwise
     * @throws InterruptedException If the timeout is negative
     */
    protected synchronized boolean awaitCondition(@NonNull Predicate<View> condition, long timeout, @NonNull TimeUnit unit)
            throws InterruptedException {
        if (timeout < 0) {
            throw new IllegalArgumentException("Timeout must be positive but was " + timeout);
        }
        synchronized (lock) {
            if (condition.test(new View(this))) {
                return true;
            }
            this.condition = condition;
        }
        return gate.poll(timeout, unit) != null;
    }

    // Should be called after every state change
    private void onUpdate() {
        synchronized (lock) {
            if (gate.isEmpty() && condition != null && condition.test(new View(this))) {
                gate.add(new Object());
            }
        }
    }


    @Override
    protected void unsupported() {
        throw new UnsupportedOperationException("This operation is not supported in a testing context!");
    }

    @Override
    public String toString() {
        String argsStr = args.length == 0 ? "" : " " + joinArgs();
        String cmdStr = String.format("'%s%s'", cmd, argsStr);
        return new StringJoiner("\n  ", TestContext.class.getSimpleName() + "{", "\n}")
                .add(cmdStr)
                .add("elevated=" + isElevated)
                .add("config=" + config.hashCode()) // Listing all fields of config would take too long
                .add("prefix=`" + rawPrefix + "`")
                .add("lang=" + lang)
                .add("library=" + library)
                .add("result=" + result)
                .add("replies=" + replies)
                .add("embedReplies=" + generateEmbedRepliesString(embedReplies))
                .add("help=" + requestedHelp)
                .add("cooldown=" + triggeredCooldown)
                .toString();
    }

    /**
     * An immutable view of TestContext mutable data (such as replies).
     */
    public static class View {

        private View(TestContext tc) {
            result = tc.result;
            replies = Collections.unmodifiableList(tc.replies);
            embedReplies = Collections.unmodifiableList(tc.embedReplies);
            requestedHelp = tc.requestedHelp;
            triggeredCooldown = tc.triggeredCooldown;
        }

        /**
         * The result of the command according to the latest reply, null if not ran yet
         */
        @Getter private final @Nullable Result result;
        /**
         * An immutable list of <b>text</b> replies, in order of appearance
         */
        @Getter private final List<CharSequence> replies;
        /**
         * An immutable list of <b>embed</b> replies, in order of appearance
         */
        @Getter private final List<MessageEmbed> embedReplies;
        private final boolean requestedHelp;
        private final boolean triggeredCooldown;

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
        public String toString() {
            return new StringJoiner("\n  ", "TestContextView{", "\n}")
                    .add("result=" + result)
                    .add("replies=" + replies)
                    .add("embedReplies=" + generateEmbedRepliesString(embedReplies))
                    .add("help=" + requestedHelp)
                    .add("cooldown=" + triggeredCooldown)
                    .toString();
        }

    }

    private static String generateEmbedRepliesString(Collection<? extends MessageEmbed> embedReplies) {
        return embedReplies.stream()
                .map(MessageEmbed::toData)
                .map(Object::toString)
                .collect(Collectors.joining(" ", "[", "]"));
    }

}
