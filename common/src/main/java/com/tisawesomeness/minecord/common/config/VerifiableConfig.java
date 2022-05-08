package com.tisawesomeness.minecord.common.config;

import com.tisawesomeness.minecord.common.util.Verification;

/**
 * A config that can be automatically verified.
 */
public interface VerifiableConfig {
    /**
     * @return valid if the config is valid, or invalid with a list of error messages
     */
    Verification verify();
}
