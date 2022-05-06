package com.tisawesomeness.minecord.testutil.annotation;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import java.lang.annotation.*;

/**
 * Marks a test that connects to the real Mojang API. This will be disabled if the {@code MINECORD_MOJANG_API}
 * environment variable is set to {@code false} or the {@code MojangAPI} tag is excluded.
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@DisabledIfEnvironmentVariable(named = "MINECORD_MOJANG_API", matches = "false")
@Tag("MojangAPI")
public @interface MojangAPITest {
}
