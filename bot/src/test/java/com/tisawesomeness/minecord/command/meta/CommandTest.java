package com.tisawesomeness.minecord.command.meta;

import com.tisawesomeness.minecord.testutil.Resources;
import com.tisawesomeness.minecord.testutil.runner.TestCommandRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tisawesomeness.minecord.testutil.assertion.CustomAssertions.assertThat;

public class CommandTest {

    private static final String REPLY = "test";
    private static TestCommandRunner runner;

    @BeforeAll
    private static void initRunner() throws JsonProcessingException {
        runner = new TestCommandRunner(Resources.config(), new SimpleReplyCommand());
    }

    @Test
    @DisplayName("Command replies send text unmodified and return SUCCESS")
    public void testCommandReply() {
        assertThat(runner.run())
                .isSuccess()
                .repliesAre(REPLY);
    }

    private static class SimpleReplyCommand extends Command {
        public @NonNull String getId() {
            return "dummy";
        }
        public void run(String[] args, CommandContext ctx) {
            ctx.reply(REPLY);
        }
    }
}
