package com.tisawesomeness.minecord.setting;

import com.tisawesomeness.minecord.database.dao.SettingContainer;
import com.tisawesomeness.minecord.share.util.Validation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * A helper enum that provides messages describing the result of {@link Setting#tryToSet(SettingContainer, String)}.
 * <br>
 * <br>
 * <table border="1">
 *   <caption>Vertical = From, Horizontal = To</caption>
 *   <thead>
 *     <tr>
 *       <th></th>
 *       <th><strong>Unset</strong></th>
 *       <th><strong>Default</strong></th>
 *       <th><strong>Set</strong></th>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td><strong>Unset</strong></td>
 *       <td>{@link SetStatus#RESET_NO_CHANGE}</td>
 *       <td>{@link SetStatus#SET_TO_DEFAULT}</td>
 *       <td>{@link SetStatus#SET}</td>
 *     </tr>
 *     <tr>
 *       <td><strong>Default</strong></td>
 *       <td>{@link SetStatus#RESET_FROM_DEFAULT}</td>
 *       <td>{@link SetStatus#SET_NO_CHANGE}</td>
 *       <td>{@link SetStatus#SET}</td>
 *     </tr>
 *     <tr>
 *       <td><strong>Set</strong></td>
 *       <td>{@link SetStatus#RESET}</td>
 *       <td>{@link SetStatus#SET_FROM_TO_DEFAULT}</td>
 *       <td>{@link SetStatus#SET_NO_CHANGE}</td>
 *     </tr>
 *   </tbody>
 * </table>
 */
@RequiredArgsConstructor
public enum SetStatus {
    SET(":white_check_mark: {name} was changed from `{from}` to `{to}`."),
    SET_NO_CHANGE(":warning: {name} is already `{from}`."),
    SET_TO_DEFAULT(":white_check_mark: {name} was changed to the default, `{to}`."),
    SET_FROM_TO_DEFAULT(":white_check_mark: {name} was changed from `{from}` to the default, `{to}`."),
    RESET(":white_check_mark: {name} was reset to `{from}`"),
    RESET_NO_CHANGE(":warning: {name} has not been set."),
    RESET_FROM_DEFAULT(":white_check_mark: {name} was already the default, `{from}`, and is now unset.");

    private final @NonNull String msg;

    /**
     * Generates a validation from the enum's message.
     * @param name The setting name
     * @param from The old setting value
     * @param to The new setting value
     * @return A successful validation
     */
    public @NonNull Validation<String> toValidation(String name, String from, String to) {
        return Validation.valid(msg
                .replace("{name}", name)
                .replace("{from}", from)
                .replace("{to}", to));
    }
}