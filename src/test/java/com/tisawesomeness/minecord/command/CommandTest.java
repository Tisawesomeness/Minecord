package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.command.runner.TestCommandRunner;
import com.tisawesomeness.minecord.config.ConfigReader;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tisawesomeness.minecord.command.runner.TestContextAssert.assertThat;

public class CommandTest {

    private static final String REPLY = "test";
    private static TestCommandRunner runner;

    @BeforeAll
    private static void initRunner() throws JsonProcessingException {
        runner = new TestCommandRunner(ConfigReader.readFromResources(), new SimpleReplyCommand());
    }

    @Test
    @DisplayName("Command replies send text unmodified and return SUCCESS")
    public void testCommandReply() {
        System.out.println(runner);
        assertThat(runner.run())
                .repliesAre(REPLY)
                .resultIs(Result.SUCCESS);
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
