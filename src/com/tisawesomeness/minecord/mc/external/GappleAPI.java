package com.tisawesomeness.minecord.mc.external;

import com.tisawesomeness.minecord.mc.player.AccountStatus;
import com.tisawesomeness.minecord.util.UuidUtils;
import lombok.NonNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

/**
 * A wrapper for the Gapple API. See <a href="https://api.gapple.pw/">the docs</a>
 */
public abstract class GappleAPI {

    /**
     * Requests the account status of a UUID.
     * @param uuid a valid UUID
     * @return the raw JSON response, or empty if the uuid doesn't <b>currently</b> exist
     * @throws IOException if an I/O error occurs
     */
    protected abstract Optional<String> requestAccountStatus(@NonNull UUID uuid) throws IOException;
    /**
     * Gets the status of the account associated with the given UUID.
     * The API will do its best, but there are no accuracy guarantees.
     * @param uuid a valid UUID
     * @return the account status, or empty if the uuid doesn't <b>currently</b> exist
     * @throws IOException if an I/O error occurs
     * @throws IllegalArgumentException if the uuid is invalid
     */
    public Optional<AccountStatus> getAccountStatus(@NonNull UUID uuid) throws IOException {
        if (!UuidUtils.isValid(uuid)) {
            throw new IllegalArgumentException(String.format("UUID %s is not a valid UUID", uuid));
        }
        Optional<String> responseOpt = requestAccountStatus(uuid);
        if (!responseOpt.isPresent()) {
            return Optional.empty();
        }
        JSONObject json = new JSONObject(responseOpt.get());
        String status = json.getString("status");
        // NOTE: new microsoft accounts show up as "msa" instead of "new_msa" as of mar 12 2022
        return AccountStatus.from(status);
    }

}
