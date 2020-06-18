package com.tisawesomeness.minecord;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("ArgsHandler")
public class ArgsHandlerTest {

    @ParameterizedTest
    @ValueSource(strings = {"-t", "--token"})
    @DisplayName("Custom tokens supplied through command-line args are accepted")
    public void testToken(String candidate) {
        String token = "dummyToken";
        String[] args = getArgs(candidate, token);
        ArgsHandler handler = new ArgsHandler();
        int exitCode = new CommandLine(handler).execute(args);
        assertEquals(0, exitCode);
        // Not using assertEquals to make sure tokens NEVER get printed
        assertTrue(handler.getTokenOverride().equals(token));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-p", "--path"})
    @DisplayName("Paths to files supplied through command-line args are rejected")
    public void testFilePath(String candidate) {
        String path = "./path/to/file.txt";
        String[] args = getArgs(candidate, path);
        ArgsHandler handler = new ArgsHandler();
        int exitCode = new CommandLine(handler).execute(args);
        assertNotEquals(0, exitCode);
    }

    @ParameterizedTest
    @ValueSource(strings = {"-p", "--path"})
    @DisplayName("Paths to nonsense supplied through command-line args are rejected")
    public void testNonsensePath(String candidate) {
        String path = "@/*912349087asdfkl;jsdg;!";
        String[] args = getArgs(candidate, path);
        ArgsHandler handler = new ArgsHandler();
        int exitCode = new CommandLine(handler).execute(args);
        assertNotEquals(0, exitCode);
    }

    private String[] getArgs(String candidate, String value) {
        return (candidate + " " + value).split(" ");
    }

}
