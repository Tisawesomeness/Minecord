package com.tisawesomeness.minecord.testutil.runner;

import net.dv8tion.jda.api.entities.MessageEmbed;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

import javax.annotation.Nullable;
import java.util.Objects;

public class MessageEmbedAssert extends AbstractAssert<MessageEmbedAssert, MessageEmbed> {

    public MessageEmbedAssert(MessageEmbed actual) {
        super(actual, MessageEmbedAssert.class);
    }

    public static MessageEmbedAssert assertThat(MessageEmbed actual) {
        return new MessageEmbedAssert(actual);
    }


    public MessageEmbedAssert titleIsEqualTo(String title) {
        isNotNull();
        String actualTitle = actual.getTitle();
        if (!Objects.deepEquals(actualTitle, title)) {
            String assertjErrorMessage = "\nExpecting title of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";
            failWithMessage(assertjErrorMessage, actual, title, actualTitle);
        }
        return this;
    }

    public MessageEmbedAssert titleContains(CharSequence... values) {
        isNotNull();
        Assertions.assertThat(actual.getTitle()).isNotNull().contains(values);
        return this;
    }

    public MessageEmbedAssert titleContains(Iterable<? extends CharSequence> values) {
        isNotNull();
        Assertions.assertThat(actual.getTitle()).isNotNull().contains(values);
        return this;
    }


    /**
     * Verifies that the header (title or author name, whichever is present) is equal to the given header.
     * @param header the given header
     * @return this assertion object
     * @throws AssertionError If the actual MessageEmbed's header is not equal to the given header
     */
    public MessageEmbedAssert headerIsEqualTo(String header) {
        isNotNull();
        String actualHeader = getHeader();
        if (!Objects.deepEquals(actualHeader, header)) {
            String assertjErrorMessage = "\nExpecting header of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";
            failWithMessage(assertjErrorMessage, actual, header, actualHeader);
        }
        return this;
    }

    /**
     * Verifies that the header (title or author name, whichever is present) contains all of the given values.
     * @param values the given values
     * @return this assertion object
     * @throws AssertionError If the actual MessageEmbed's header does not contain the given values
     */
    public MessageEmbedAssert headerContains(CharSequence... values) {
        isNotNull();
        Assertions.assertThat(getHeader()).contains(values);
        return this;
    }

    /**
     * Verifies that the header (title or author name, whichever is present) contains all of the given values.
     * @param values the given values
     * @return this assertion object
     * @throws AssertionError If the actual MessageEmbed's header does not contain the given values
     */
    public MessageEmbedAssert headerContains(Iterable<? extends CharSequence> values) {
        isNotNull();
        Assertions.assertThat(getHeader()).contains(values);
        return this;
    }

    private @Nullable String getHeader() {
        String title = actual.getTitle();
        if (title != null) {
            return title;
        }
        MessageEmbed.AuthorInfo author = actual.getAuthor();
        if (author != null) {
            return author.getName();
        }
        return null;
    }


    public MessageEmbedAssert descriptionIsEqualTo(String description) {
        isNotNull();
        String actualDescription = actual.getDescription();
        if (!Objects.deepEquals(actualDescription, description)) {
            String assertjErrorMessage = "\nExpecting description of:\n  <%s>\nto be:\n  <%s>\nbut was:\n  <%s>";
            failWithMessage(assertjErrorMessage, actual, description, actualDescription);
        }
        return this;
    }

    public MessageEmbedAssert descriptionContains(CharSequence... values) {
        isNotNull();
        Assertions.assertThat(actual.getDescription()).isNotNull().contains(values);
        return this;
    }

    public MessageEmbedAssert descriptionContains(Iterable<? extends CharSequence> values) {
        isNotNull();
        Assertions.assertThat(actual.getDescription()).isNotNull().contains(values);
        return this;
    }


}
