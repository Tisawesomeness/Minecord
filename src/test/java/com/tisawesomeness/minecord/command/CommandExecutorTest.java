package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.command.admin.AbstractAdminCommand;
import com.tisawesomeness.minecord.command.meta.CommandContext;
import com.tisawesomeness.minecord.command.meta.Result;
import com.tisawesomeness.minecord.testutil.Resources;
import com.tisawesomeness.minecord.testutil.runner.TestCommandRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tisawesomeness.minecord.testutil.assertion.CustomAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class CommandExecutorTest {

    private static final String WARNING = "\n" +
            "!!!!!!!!!!!!!!!!!!!!!! WARNING !!!!!!!!!!!!!!!!!!!!!!\n" +
            "!!! DO NOT DEPLOY TO PROD UNDER ANY CIRCUMSTANCES !!!\n" +
            "!!!  A NON-ELEVATED USER HAS ACCESS TO ELEVATION  !!!\n" +
            "!!!!!!!!!!!!!!!!!!!!!! WARNING !!!!!!!!!!!!!!!!!!!!!!\n";

    private static TestCommandRunner runner;

    @BeforeAll
    private static void initRunner() throws JsonProcessingException {
        runner = new TestCommandRunner(Resources.config(), new FailOnRunAdminCommand());
    }

    @Test
    @DisplayName("Make sure an un-elevated user does not have access to elevation or admin commands")
    public void testElevationCheck() {
        assertThat(runner.isElevated).isFalse();
        // Uncomment to trip the elevation warning
//        runner.isElevated = true;
        assertThat(runner.run()).resultIs(Result.NOT_ELEVATED);
    }

    private static class FailOnRunAdminCommand extends AbstractAdminCommand {
        public @NonNull String getId() {
            return "dummy";
        }
        public void run(String[] args, CommandContext ctx) {
            fail(WARNING);
            throw new AssertionError("Unreachable");
        }
    }

}
