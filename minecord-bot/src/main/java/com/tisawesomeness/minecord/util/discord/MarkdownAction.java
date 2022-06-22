package com.tisawesomeness.minecord.util.discord;

import lombok.NonNull;

import java.util.Comparator;
import java.util.function.UnaryOperator;

/**
 * A Discord markdown element that can be applied to text, such as "bold" or "masked link".
 * To apply markdown, use {@link #apply(String)}. Unlike a normal unary operator, nulls are not allowed.
 * @see SimpleMarkdownAction
 * @see MaskedLink
 * @see Codeblock
 */
public interface MarkdownAction extends UnaryOperator<String> {

    /**
     * Applies this markdown action to some text
     * @param text Any text
     * @return The text with markdown applied
     */
    @Override
    @NonNull String apply(@NonNull String text);
    /**
     * The priority of this markdown, must be constant, lower is applied first
     * @return The markdown priority
     */
    int getPriority();

    /**
     * A hash code is required to ensure a consistent ordering when priority ties
     * @return The hash code of the markdown action
     */
    @Override
    int hashCode();

    /**
     * @return A comparator that consistently orders markdown application by priority
     */
    static Comparator<MarkdownAction> comparingByPriority() {
        return Comparator.comparing(MarkdownAction::getPriority)
                .thenComparing(Object::hashCode);
    }

}
