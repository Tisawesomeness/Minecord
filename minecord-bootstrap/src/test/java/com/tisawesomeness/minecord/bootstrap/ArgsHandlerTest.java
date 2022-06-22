package com.tisawesomeness.minecord.bootstrap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import picocli.CommandLine;

import static org.assertj.core.api.Assertions.assertThat;

public class ArgsHandlerTest {

    @Test
    @DisplayName("Running with no args is accepted and creates path")
    public void testEmptyPath() {
        ArgsHandler handler = new ArgsHandler();
        int exitCode = new CommandLine(handler).execute();
        assertThat(exitCode).isEqualTo(0);
        assertThat(handler.getPath()).isDirectory();
        assertThat(handler.requestedHelp()).isFalse();
    }

    @ParameterizedTest(name = "{index} ==> Rejecting path with flag ''{0}''")
    @ValueSource(strings = {"-p", "--path"})
    @DisplayName("Paths to files supplied through command-line args are rejected")
    public void testFilePath(String candidate) {
        String path = "./path/to/file.txt";
        String[] args = getArgs(candidate, path);
        ArgsHandler handler = new ArgsHandler();
        int exitCode = new CommandLine(handler).execute(args);
        assertThat(exitCode).isNotEqualTo(0);
        assertThat(handler.requestedHelp()).isFalse();
    }

    @ParameterizedTest(name = "{index} ==> Rejecting path with flag ''{0}''")
    @ValueSource(strings = {"-p", "--path"})
    @DisplayName("Paths to nonsense supplied through command-line args are rejected")
    public void testNonsensePath(String candidate) {
        String path = "@/*912349087asdfkl;jsdg;!";
        String[] args = getArgs(candidate, path);
        ArgsHandler handler = new ArgsHandler();
        int exitCode = new CommandLine(handler).execute(args);
        assertThat(exitCode).isNotEqualTo(0);
        assertThat(handler.requestedHelp()).isFalse();
    }

    @ParameterizedTest(name = "{index} ==> Accepting help with flag ''{0}''")
    @ValueSource(strings = {"-v", "--version", "-h", "--help"})
    @DisplayName("Running with help or version arg is accepted")
    public void testVersion(String candidate) {
        ArgsHandler handler = new ArgsHandler();
        int exitCode = new CommandLine(handler).execute(candidate);
        assertThat(exitCode).isEqualTo(0);
        assertThat(handler.requestedHelp()).isTrue();
    }

    private static String[] getArgs(String candidate, String value) {
        return (candidate + " " + value).split(" ");
    }

}
