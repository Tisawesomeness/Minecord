package com.tisawesomeness.minecord.testutil.runner;

import net.dv8tion.jda.api.entities.MessageEmbed;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

import javax.annotation.Nullable;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

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

    /**
     * Verifies that the header (title or author url, whichever is present) has a url.
     * @return this assertion object
     * @throws AssertionError If the actual MessageEmbed's header does not have a url
     */
    public MessageEmbedAssert headerHasUrl() {
        isNotNull();
        Assertions.assertThat(getHeaderUrl()).isNotNull();
        return this;
    }

    /**
     * Verifies that the header (title or author url, whichever is present) does not have a url.
     * @return this assertion object
     * @throws AssertionError If the actual MessageEmbed's header has a url
     */
    public MessageEmbedAssert headerDoesNotHaveUrl() {
        isNotNull();
        Assertions.assertThat(getHeaderUrl()).isNull();
        return this;
    }

    /**
     * Verifies that the header (title or author url, whichever is present) has the given url.
     * @param url the given url
     * @return this assertion object
     * @throws AssertionError If the actual MessageEmbed's header does not have the given url
     */
    public MessageEmbedAssert headerLinksTo(String url) {
        isNotNull();
        Assertions.assertThat(getHeaderUrl()).isEqualTo(url);
        return this;
    }

    /**
     * Verifies that the header (title or author url, whichever is present) has the given url.
     * @param url the given url
     * @return this assertion object
     * @throws AssertionError If the actual MessageEmbed's header does not have the given url
     */
    public MessageEmbedAssert headerLinksTo(URL url) {
        isNotNull();
        Assertions.assertThat(getHeaderUrl()).isEqualTo(url.toString());
        return this;
    }

    /**
     * Verifies that the header (title or author url, whichever is present) has one of the given urls.
     * @param urls the given urls
     * @return this assertion object
     * @throws AssertionError If the actual MessageEmbed's header does not have one of the given urls
     */
    public MessageEmbedAssert headerLinksToAnyOf(String... urls) {
        isNotNull();
        Assertions.assertThat(getHeaderUrl()).isIn((Object[]) urls);
        return this;
    }

    /**
     * Verifies that the header (title or author url, whichever is present) has one of the given urls.
     * @param urls the given urls
     * @return this assertion object
     * @throws AssertionError If the actual MessageEmbed's header does not have one of the given urls
     */
    public MessageEmbedAssert headerLinksToAnyOf(Iterable<? extends String> urls) {
        isNotNull();
        Assertions.assertThat(getHeaderUrl()).isIn(urls);
        return this;
    }

    /**
     * Verifies that the header (title or author url, whichever is present) has one of the given urls.
     * @param urls the given urls
     * @return this assertion object
     * @throws AssertionError If the actual MessageEmbed's header does not have one of the given urls
     */
    public MessageEmbedAssert headerLinksToAnyOf(URL... urls) {
        isNotNull();
        List<String> urlStrings = Arrays.stream(urls)
                .map(Object::toString)
                .collect(Collectors.toList());
        Assertions.assertThat(getHeaderUrl()).isIn(urlStrings);
        return this;
    }

    /**
     * Verifies that the header (title or author url, whichever is present) has one of the given urls.
     * @param urls the given urls
     * @return this assertion object
     * @throws AssertionError If the actual MessageEmbed's header does not have one of the given urls
     */
    public MessageEmbedAssert headerLinksToAnyOfUrls(Iterable<? extends URL> urls) {
        isNotNull();
        Collection<String> urlStrings = new ArrayList<>();
        for (URL url : urls) {
            urlStrings.add(url.toString());
        }
        Assertions.assertThat(getHeaderUrl()).isIn(urlStrings);
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

    private @Nullable String getHeaderUrl() {
        MessageEmbed.AuthorInfo author = actual.getAuthor();
        if (author != null) {
            return author.getUrl();
        }
        return actual.getUrl();
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
        Assertions.assertThat(actual.getDescription()).contains(values);
        return this;
    }

    public MessageEmbedAssert descriptionContains(Iterable<? extends CharSequence> values) {
        isNotNull();
        Assertions.assertThat(actual.getDescription()).contains(values);
        return this;
    }

    public MessageEmbedAssert descriptionContainsSequence(CharSequence... values) {
        isNotNull();
        Assertions.assertThat(actual.getDescription()).containsSequence(values);
        return this;
    }

    public MessageEmbedAssert descriptionContainsSequence(Iterable<? extends CharSequence> values) {
        isNotNull();
        Assertions.assertThat(actual.getDescription()).containsSequence(values);
        return this;
    }

    public MessageEmbedAssert descriptionContainsSubsequence(CharSequence... values) {
        isNotNull();
        Assertions.assertThat(actual.getDescription()).containsSubsequence(values);
        return this;
    }

    public MessageEmbedAssert descriptionContainsSubsequence(Iterable<? extends CharSequence> values) {
        isNotNull();
        Assertions.assertThat(actual.getDescription()).containsSubsequence(values);
        return this;
    }


    public MessageEmbedAssert fieldsContains(CharSequence... values) {
        noNullChars(values);
        isNotNull();
        Assertions.assertThat(getJoinedFieldString()).contains(values);
        return this;
    }

    public MessageEmbedAssert fieldsContains(Iterable<? extends CharSequence> values) {
        noNullChars(values);
        isNotNull();
        Assertions.assertThat(getJoinedFieldString()).contains(values);
        return this;
    }

    public MessageEmbedAssert fieldsContainsSequence(CharSequence... values) {
        noNullChars(values);
        isNotNull();
        Assertions.assertThat(getJoinedFieldString()).containsSequence(values);
        return this;
    }

    public MessageEmbedAssert fieldsContainsSequence(Iterable<? extends CharSequence> values) {
        noNullChars(values);
        isNotNull();
        Assertions.assertThat(getJoinedFieldString()).containsSequence(values);
        return this;
    }

    public MessageEmbedAssert fieldsContainsSubsequence(CharSequence... values) {
        noNullChars(values);
        isNotNull();
        Assertions.assertThat(getJoinedFieldString()).containsSubsequence(values);
        return this;
    }

    public MessageEmbedAssert fieldsContainsSubsequence(Iterable<? extends CharSequence> values) {
        noNullChars(values);
        isNotNull();
        Assertions.assertThat(getJoinedFieldString()).containsSubsequence(values);
        return this;
    }

    private String getJoinedFieldString() {
        return actual.getFields().stream()
                .map(MessageEmbed.Field::getValue)
                .collect(Collectors.joining("\0"));
    }
    private void noNullChars(CharSequence... values) {
        for (CharSequence value : values) {
            if (value.toString().contains("\0")) {
                failWithMessage("Input values should not contain null chars.");
            }
        }
    }
    private void noNullChars(Iterable<? extends CharSequence> values) {
        for (CharSequence value : values) {
            if (value.toString().contains("\0")) {
                failWithMessage("Input values should not contain null chars.");
            }
        }
    }



}
