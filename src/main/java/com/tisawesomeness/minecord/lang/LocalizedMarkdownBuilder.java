package com.tisawesomeness.minecord.lang;

import com.tisawesomeness.minecord.util.discord.Codeblock;
import com.tisawesomeness.minecord.util.discord.MarkdownAction;
import com.tisawesomeness.minecord.util.discord.MaskedLink;
import com.tisawesomeness.minecord.util.discord.SimpleMarkdownAction;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.annotation.Nullable;
import java.net.URL;
import java.text.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *     Formats a message according to a {@link MessageFormat} and applies markdown to the localized string.
 *     Localized markdown can be created simply by applying markdown to a localized string, but this does not work for
 *     more advanced use cases such as bolding the numeric portion of a quantity like "5 messages", or underlining the
 *     year portion of a date. This class is intended for those use cases, or when putting markdown in internationalized
 *     base files is undesirable.
 * </p>
 * <p>
 *     Trying to nest markdown in incompatible ways, such as bold text inside monospace text, will not and can not
 *     display properly. The behavior of such nesting is undefined. However, if multiple markdown actions start on the
 *     same character, {@link MarkdownAction#comparingByPriority()} is used to resolve conflicts. For example,
 *     bold will always be applied outside monospace text when they both start on the same character.
 * </p>
 * @implNote This class is NOT thread-safe
 * @see MarkdownAction
 */
public class LocalizedMarkdownBuilder {
    private final @NonNull MessageFormat mf;
    private final Object[] args;
    private final List<List<PotentialAction>> table;
    private int size;

    /**
     * Creates a new builder.
     * @param mf The MessageFormat that will be used for formatting
     * @param args A possibly-empty list of possibly-null arguments
     * @see MessageFormat
     * @see Locale
     */
    public LocalizedMarkdownBuilder(@NonNull MessageFormat mf, Object... args) {
        this.mf = mf;
        this.args = Arrays.copyOf(args, args.length); // prevent leaking args
        table = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            table.add(new ArrayList<>());
        }
    }

    /**
     * Bolds a specific part of the localized message.
     * <br>If no format fields are provided, the markdown will always be applied to the argument given by the index.
     * <br>If format fields are provided, markdown is applied to the part of the argument where any field matches.
     * <br>If the index is too high, nothing happens.
     * @param index The index of the argument provided to this builder in
     *              {@link LocalizedMarkdownBuilder#LocalizedMarkdownBuilder(MessageFormat, Object...)}
     * @param fields A possibly-empty list of fields to match, only NumberFormat and DateFormat fields are considered
     * @return This builder, useful for chaining
     * @throws IllegalArgumentException If the index is negative
     * @see NumberFormat.Field
     * @see DateFormat.Field
     */
    public LocalizedMarkdownBuilder bold(int index, Format.Field... fields) {
        return apply(index, SimpleMarkdownAction.BOLD, fields);
    }

    /**
     * Italicizes a specific part of the localized message.
     * <br>If no format fields are provided, the markdown will always be applied to the argument given by the index.
     * <br>If format fields are provided, markdown is applied to the part of the argument where any field matches.
     * <br>If the index is too high, nothing happens.
     * @param index The index of the argument provided to this builder in
     *              {@link LocalizedMarkdownBuilder#LocalizedMarkdownBuilder(MessageFormat, Object...)}
     * @param fields A possibly-empty list of fields to match, only NumberFormat and DateFormat fields are considered
     * @return This builder, useful for chaining
     * @throws IllegalArgumentException If the index is negative
     * @see NumberFormat.Field
     * @see DateFormat.Field
     */
    public LocalizedMarkdownBuilder italics(int index, Format.Field... fields) {
        return apply(index, SimpleMarkdownAction.ITALICS, fields);
    }

    /**
     * Underlines a specific part of the localized message.
     * <br>If no format fields are provided, the markdown will always be applied to the argument given by the index.
     * <br>If format fields are provided, markdown is applied to the part of the argument where any field matches.
     * <br>If the index is too high, nothing happens.
     * @param index The index of the argument provided to this builder in
     *              {@link LocalizedMarkdownBuilder#LocalizedMarkdownBuilder(MessageFormat, Object...)}
     * @param fields A possibly-empty list of fields to match, only NumberFormat and DateFormat fields are considered
     * @return This builder, useful for chaining
     * @throws IllegalArgumentException If the index is negative
     * @see NumberFormat.Field
     * @see DateFormat.Field
     */
    public LocalizedMarkdownBuilder underline(int index, Format.Field... fields) {
        return apply(index, SimpleMarkdownAction.UNDERLINE, fields);
    }

    /**
     * Strikes through a specific part of the localized message.
     * <br>If no format fields are provided, the markdown will always be applied to the argument given by the index.
     * <br>If format fields are provided, markdown is applied to the part of the argument where any field matches.
     * <br>If the index is too high, nothing happens.
     * @param index The index of the argument provided to this builder in
     *              {@link LocalizedMarkdownBuilder#LocalizedMarkdownBuilder(MessageFormat, Object...)}
     * @param fields A possibly-empty list of fields to match, only NumberFormat and DateFormat fields are considered
     * @return This builder, useful for chaining
     * @throws IllegalArgumentException If the index is negative
     * @see NumberFormat.Field
     * @see DateFormat.Field
     */
    public LocalizedMarkdownBuilder strike(int index, Format.Field... fields) {
        return apply(index, SimpleMarkdownAction.STRIKE, fields);
    }

    /**
     * Spoilers a specific part of the localized message.
     * <br>If no format fields are provided, the markdown will always be applied to the argument given by the index.
     * <br>If format fields are provided, markdown is applied to the part of the argument where any field matches.
     * <br>If the index is too high, nothing happens.
     * @param index The index of the argument provided to this builder in
     *              {@link LocalizedMarkdownBuilder#LocalizedMarkdownBuilder(MessageFormat, Object...)}
     * @param fields A possibly-empty list of fields to match, only NumberFormat and DateFormat fields are considered
     * @return This builder, useful for chaining
     * @throws IllegalArgumentException If the index is negative
     * @see NumberFormat.Field
     * @see DateFormat.Field
     */
    public LocalizedMarkdownBuilder spoiler(int index, Format.Field... fields) {
        return apply(index, SimpleMarkdownAction.SPOILER, fields);
    }

    /**
     * Adds monospace to a specific part of the localized message.
     * <br>If no format fields are provided, the markdown will always be applied to the argument given by the index.
     * <br>If format fields are provided, markdown is applied to the part of the argument where any field matches.
     * <br>If the index is too high, nothing happens.
     * @param index The index of the argument provided to this builder in
     *              {@link LocalizedMarkdownBuilder#LocalizedMarkdownBuilder(MessageFormat, Object...)}
     * @param fields A possibly-empty list of fields to match, only NumberFormat and DateFormat fields are considered
     * @return This builder, useful for chaining
     * @throws IllegalArgumentException If the index is negative
     * @see NumberFormat.Field
     * @see DateFormat.Field
     */
    public LocalizedMarkdownBuilder monospace(int index, Format.Field... fields) {
        return apply(index, SimpleMarkdownAction.MONOSPACE, fields);
    }

    /**
     * Adds a code block to a specific part of the localized message.
     * <br>If no format fields are provided, the markdown will always be applied to the argument given by the index.
     * <br>If format fields are provided, markdown is applied to the part of the argument where any field matches.
     * <br>If the index is too high, nothing happens.
     * @param index The index of the argument provided to this builder in
     *              {@link LocalizedMarkdownBuilder#LocalizedMarkdownBuilder(MessageFormat, Object...)}
     * @param fields A possibly-empty list of fields to match, only NumberFormat and DateFormat fields are considered
     * @return This builder, useful for chaining
     * @throws IllegalArgumentException If the index is negative
     * @see NumberFormat.Field
     * @see DateFormat.Field
     */
    public LocalizedMarkdownBuilder codeblock(int index, Format.Field... fields) {
        return apply(index, new Codeblock(), fields);
    }

    /**
     * Adds a code block to a specific part of the localized message.
     * <br>If no format fields are provided, the markdown will always be applied to the argument given by the index.
     * <br>If format fields are provided, markdown is applied to the part of the argument where any field matches.
     * <br>If the index is too high, nothing happens.
     * @param language The programming language of the code block, pass null for no language
     * @param index The index of the argument provided to this builder in
     *              {@link LocalizedMarkdownBuilder#LocalizedMarkdownBuilder(MessageFormat, Object...)}
     * @param fields A possibly-empty list of fields to match, only NumberFormat and DateFormat fields are considered
     * @return This builder, useful for chaining
     * @throws IllegalArgumentException If the index is negative
     * @see NumberFormat.Field
     * @see DateFormat.Field
     */
    public LocalizedMarkdownBuilder codeblock(@Nullable String language, int index, Format.Field... fields) {
        return apply(index, new Codeblock(language), fields);
    }

    /**
     * Quotes a specific part of the localized message.
     * <br>If no format fields are provided, the markdown will always be applied to the argument given by the index.
     * <br>If format fields are provided, markdown is applied to the part of the argument where any field matches.
     * <br>If the index is too high, nothing happens.
     * @param index The index of the argument provided to this builder in
     *              {@link LocalizedMarkdownBuilder#LocalizedMarkdownBuilder(MessageFormat, Object...)}
     * @param fields A possibly-empty list of fields to match, only NumberFormat and DateFormat fields are considered
     * @return This builder, useful for chaining
     * @throws IllegalArgumentException If the index is negative
     * @see NumberFormat.Field
     * @see DateFormat.Field
     */
    public LocalizedMarkdownBuilder quote(int index, Format.Field... fields) {
        return apply(index, SimpleMarkdownAction.QUOTE, fields);
    }

    /**
     * Adds a quote block to a specific part of the localized message.
     * <br>If no format fields are provided, the markdown will always be applied to the argument given by the index.
     * <br>If format fields are provided, markdown is applied to the part of the argument where any field matches.
     * <br>If the index is too high, nothing happens.
     * @param index The index of the argument provided to this builder in
     *              {@link LocalizedMarkdownBuilder#LocalizedMarkdownBuilder(MessageFormat, Object...)}
     * @param fields A possibly-empty list of fields to match, only NumberFormat and DateFormat fields are considered
     * @return This builder, useful for chaining
     * @throws IllegalArgumentException If the index is negative
     * @see NumberFormat.Field
     * @see DateFormat.Field
     */
    public LocalizedMarkdownBuilder quoteBlock(int index, Format.Field... fields) {
        return apply(index, SimpleMarkdownAction.QUOTE_BLOCK, fields);
    }

    /**
     * Adds a masked link to a specific part of the localized message.
     * <br>If no format fields are provided, the markdown will always be applied to the argument given by the index.
     * <br>If format fields are provided, markdown is applied to the part of the argument where any field matches.
     * <br>If the index is too high, nothing happens.
     * @param url The URL of the link
     * @param index The index of the argument provided to this builder in
     *              {@link LocalizedMarkdownBuilder#LocalizedMarkdownBuilder(MessageFormat, Object...)}
     * @param fields A possibly-empty list of fields to match, only NumberFormat and DateFormat fields are considered
     * @return This builder, useful for chaining
     * @throws IllegalArgumentException If the index is negative
     * @see NumberFormat.Field
     * @see DateFormat.Field
     */
    public LocalizedMarkdownBuilder maskedLink(@NonNull String url, int index, Format.Field... fields) {
        return apply(index, new MaskedLink(url), fields);
    }

    /**
     * Adds a masked link to a specific part of the localized message.
     * <br>If no format fields are provided, the markdown will always be applied to the argument given by the index.
     * <br>If format fields are provided, markdown is applied to the part of the argument where any field matches.
     * <br>If the index is too high, nothing happens.
     * @param url The URL of the link
     * @param index The index of the argument provided to this builder in
     *              {@link LocalizedMarkdownBuilder#LocalizedMarkdownBuilder(MessageFormat, Object...)}
     * @param fields A possibly-empty list of fields to match, only NumberFormat and DateFormat fields are considered
     * @return This builder, useful for chaining
     * @throws IllegalArgumentException If the index is negative
     * @see NumberFormat.Field
     * @see DateFormat.Field
     */
    public LocalizedMarkdownBuilder maskedLink(@NonNull URL url, int index, Format.Field... fields) {
        return apply(index, new MaskedLink(url), fields);
    }

    /**
     * Bolds a specific part of the localized message.
     * <br>If no format fields are provided, the markdown will always be applied to the argument given by the index.
     * <br>If format fields are provided, markdown is applied to the part of the argument where any field matches.
     * <br>If the index is too high, nothing happens.
     * @param index The index of the argument provided to this builder in
     *              {@link LocalizedMarkdownBuilder#LocalizedMarkdownBuilder(MessageFormat, Object...)}
     * @param action The markdown to apply
     * @param fields A possibly-empty list of fields to match, only NumberFormat and DateFormat fields are considered
     * @return This builder, useful for chaining
     * @throws IllegalArgumentException If the index is negative
     * @see NumberFormat.Field
     * @see DateFormat.Field
     */
    public LocalizedMarkdownBuilder apply(int index, @NonNull MarkdownAction action, Format.Field... fields) {
        Preconditions.checkArgument(index >= 0, "Index must be positive, was %d", index);
        if (index >= args.length) {
            return this;
        }
        if (fields.length == 0) {
            table.get(index).add(new PotentialAction(null, action));
            size++;
        }
        for (Format.Field field : fields) {
            if (field instanceof NumberFormat.Field || field instanceof DateFormat.Field) {
                table.get(index).add(new PotentialAction(field, action));
                size++;
            }
        }
        return this;
    }

    /**
     * @return The number of arguments used for this builder
     */
    public int getArgsLength() {
        return args.length;
    }
    /**
     * @return The number of arg + field combinations for this builder
     */
    public int size() {
        return size;
    }

    /**
     * Builds a localized string from this builder's MessageFormat and the provided markdown.
     * See {@link LocalizedMarkdownBuilder} for the docs on how markdown behaves.
     * @return A localized string with all markdown applied
     */
    public @NonNull String build() {
        if (size == 0) {
            return mf.format(args);
        }
        if (size == 1) {
            return buildNoConflicts();
        }
        return buildWithConflicts();
    }

    // A simpler and more efficient way to apply markdown when we don't need to worry about conflicts
    // Assumes size == 1
    private String buildNoConflicts() {
        AttributedCharacterIterator iter = mf.formatToCharacterIterator(args);
        IndexedPotentialAction potentialAction = getPotentialAction();
        StringBuilder mainString = new StringBuilder();
        StringBuilder markdownString = new StringBuilder();

        boolean currentlyProcessingMarkdown = false;
        char ch = iter.first();
        StringBuilder currentRun = mainString;
        while (ch != AttributedCharacterIterator.DONE) {

            // If we just finished processing markdown
            if (currentlyProcessingMarkdown) {
                if (!potentialAction.satisfies(iter)) {
                    // Add it to the main string
                    currentlyProcessingMarkdown = false;
                    String formatted = potentialAction.apply(markdownString.toString());
                    mainString.append(formatted);
                    currentRun = mainString;
                }
            // swap to processing markdown if needed
            } else if (potentialAction.satisfies(iter)) {
                currentlyProcessingMarkdown = true;
                currentRun = markdownString;
            }

            // appending the text
            int runLength = iter.getRunLimit() - iter.getRunStart();
            for (int i = 0; i < runLength; i++) {
                currentRun.append(ch);
                ch = iter.next();
            }
        }

        // if the MessageFormat ends with a parameter, add the unprocessed markdown to the main string
        if (currentlyProcessingMarkdown) {
            String formatted = potentialAction.apply(markdownString.toString());
            mainString.append(formatted);
        }
        return mainString.toString();
    }
    // Assumes size == 1
    private IndexedPotentialAction getPotentialAction() {
        for (int i = 0; i < args.length; i++) {
            List<PotentialAction> actions = table.get(i);
            if (!actions.isEmpty()) {
                return new IndexedPotentialAction(actions.get(0), i);
            }
        }
        throw new AssertionError("No potential actions found but size == 1!");
    }

    public String buildWithConflicts() {
        AttributedCharacterIterator iter = mf.formatToCharacterIterator(args);

        // root StringBuilder used when stack is empty
        StringBuilder sb = new StringBuilder();
        // top of stack is the markdown action currently being processed
        // when that action stops being processed, it gets popped and the one below starts being processed
        Deque<MarkdownAction> markdownStack = new ArrayDeque<>();
        // the value keeps track of the text that will be formatted with its action key
        Map<MarkdownAction, StringBuilder> currentRuns = new HashMap<>();
        // all actions currently in a run, even if they are not being processed
        Set<MarkdownAction> activeActions = markdownOrderedSet();
        char ch = iter.first();

        while (ch != AttributedCharacterIterator.DONE) {
            Set<MarkdownAction> currentActions = getCurrentActions(iter);
            // runs that were active but now are not must be removed in LIFO order
            int numFinishedActions = activeActions.size() - currentActions.size();
            for (int i = 0; i < numFinishedActions; i++) {
                collapseOneLevel(sb, markdownStack, currentRuns);
            }
            // push activated runs to the stack
            // thanks to enum ordering, monospace/codeblocks will always apply before (inside) other markdown such
            // as bold or underline, so the codeblocks should stay literals
            // not guaranteed if the caller tries to do something that does not work in normal Discord
            for (MarkdownAction activatedElement : Sets.difference(currentActions, activeActions)) {
                markdownStack.push(activatedElement);
                currentRuns.put(activatedElement, new StringBuilder());
            }
            // updating active runs
            activeActions = currentActions;

            // actually appending the text
            StringBuilder currentRun = markdownStack.isEmpty() ? sb : currentRuns.get(markdownStack.peek());
            int runLength = iter.getRunLimit() - iter.getRunStart();
            for (int i = 0; i < runLength; i++) {
                currentRun.append(ch);
                ch = iter.next();
            }
        }

        // if the MessageFormat ends with a parameter, the stack may still have unfinished markdown
        // in that case, keep on collapsing until everything is added to sb
        while (!markdownStack.isEmpty()) {
            collapseOneLevel(sb, markdownStack, currentRuns);
        }
        return sb.toString();
    }
    private static void collapseOneLevel(StringBuilder sb, Deque<? extends MarkdownAction> markdownStack,
                                         Map<MarkdownAction, StringBuilder> currentRuns) {
        MarkdownAction finishedElement = markdownStack.pop();
        // we are done adding text, apply the markdown
        String formatted = finishedElement.apply(currentRuns.get(finishedElement).toString());
        // add the formatted text to the next level up
        StringBuilder destination = markdownStack.isEmpty() ? sb : currentRuns.get(markdownStack.peek());
        destination.append(formatted);
    }
    private Set<MarkdownAction> getCurrentActions(AttributedCharacterIterator iter) {
        Set<MarkdownAction> currentElements = markdownOrderedSet();
        Integer arg = (Integer) iter.getAttribute(MessageFormat.Field.ARGUMENT);
        if (arg == null) {
            return currentElements;
        }
        return table.get(arg).stream()
                .filter(potentialAction -> potentialAction.satisfies(iter))
                .map(PotentialAction::getAction)
                .collect(Collectors.toCollection(() -> currentElements));
    }

    private static Set<MarkdownAction> markdownOrderedSet() {
        return new TreeSet<>(MarkdownAction.comparingByPriority());
    }

    @ToString
    @RequiredArgsConstructor
    private static class PotentialAction {
        private final @Nullable Format.Field field;
        @Getter private final MarkdownAction action;

        public boolean satisfies(AttributedCharacterIterator iter) {
            return field == null || iter.getAttribute(field) != null;
        }
        public @NonNull String apply(@NonNull String text) {
            return action.apply(text);
        }
    }

    @ToString
    @RequiredArgsConstructor
    private static class IndexedPotentialAction {
        private final @NonNull PotentialAction potentialAction;
        private final int arg;

        public boolean satisfies(AttributedCharacterIterator iter) {
            Integer arg = (Integer) iter.getAttribute(MessageFormat.Field.ARGUMENT);
            if (arg == null) {
                return false;
            }
            return this.arg == arg && potentialAction.satisfies(iter);
        }
        public @NonNull String apply(@NonNull String text) {
            return potentialAction.apply(text);
        }
    }

}
