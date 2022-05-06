package com.tisawesomeness.minecord.config.config;

import com.tisawesomeness.minecord.util.type.Verification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class CircuitBreakerConfig {
    /** Percentage rate 1-100 of failures to open the circuit */
    @JsonProperty("failureRateThreshold")
    int failureRateThreshold;
    /** Minimum number of executions that must occur within the threshold period for the circuit to open */
    @JsonProperty("failureExecutionThreshold")
    int failureExecutionThreshold;
    /** Time in seconds to track failures */
    @JsonProperty("thresholdPeriod")
    int thresholdPeriod;
    /** Time in seconds the circuit is disabled */
    @JsonProperty("disablePeriod")
    int disablePeriod;
    /** Number of consecutive executions needed to reset the circuit back to closed */
    @JsonProperty("resetCount")
    int resetCount;

    public Verification verify() {
        return Verification.combineAll(
                verifyFailureRateThreshold(),
                Verification.verify(failureExecutionThreshold > 0, "Failure execution threshold must be positive"),
                Verification.verify(thresholdPeriod > 0, "Threshold period must be positive"),
                Verification.verify(disablePeriod >= 0, "Disable period cannot be negative"),
                Verification.verify(resetCount > 0, "Reset count must be positive")
        );
    }
    private Verification verifyFailureRateThreshold() {
        if (failureRateThreshold < 1 || 100 < failureRateThreshold) {
            return Verification.invalid("Failure rate threshold must be between 1 and 100");
        }
        return Verification.valid();
    }

}
