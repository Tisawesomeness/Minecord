package com.tisawesomeness.minecord.share.config;

import com.tisawesomeness.minecord.share.util.Verification;

/**
 * A config that can be automatically verified.
 */
public interface VerifiableConfig {
    /**
     * @return valid if the config is valid, or invalid with a list of error messages
     */
    Verification verify();
}
