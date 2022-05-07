package com.tisawesomeness.minecord.bootstrap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import picocli.CommandLine;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("ArgsHandler")
public class ArgsHandlerTest {

    @ParameterizedTest(name = "{index} ==> Accepting path with flag ''{0}''")
    @ValueSource(strings = {"-p", "--path"})
    @DisplayName("Paths to files supplied through command-line args are rejected")
    public void testFilePath(String candidate) {
        String path = "./path/to/file.txt";
        String[] args = getArgs(candidate, path);
        ArgsHandler handler = new ArgsHandler();
        int exitCode = new CommandLine(handler).execute(args);
        assertThat(exitCode).isNotEqualTo(0);
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
    }

    private static String[] getArgs(String candidate, String value) {
        return (candidate + " " + value).split(" ");
    }

}
