package com.tisawesomeness.minecord.common.util;

import lombok.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Utility class for working with files.
 */
public final class IO {
    private IO() {}

    /**
     * Reads a string from a file.
     * @param path The path to the file
     * @return A string with the contents of the file
     * @throws IOException When an I/O error occurs
     */
    public static @NonNull String read(@NonNull Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    /**
     * Writes a string to a file. This will create the file if it does not exist and replace any existing contents.
     * @param path The path to the file
     * @param data The string to write
     * @throws IOException When an I/O error occurs
     */
    public static void write(@NonNull Path path, @NonNull String data) throws IOException {
        Files.write(path, data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Loads a file from the resources folder.
     * @param name The filename with extension
     * @return A string with the contents of the file
     */
    public static @NonNull String loadResource(@NonNull String name) {
        InputStream is = openResource(name);
        try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr)) {
            return br.lines().collect(Collectors.joining("\n"));
        } catch (IOException ex) {
            throw new AssertionError("An IOException when closing a resource stream should never happen.");
        }
    }

    /**
     * Loads a properties file from the resources folder.
     * @param name The filename with extension
     * @return A properties object with the contents of the file
     */
    public static @NonNull Properties loadPropertiesResource(@NonNull String name) {
        InputStream is = openResource(name);
        Properties prop = new Properties();
        try {
            prop.load(is);
        } catch (IOException ex) {
            throw new IllegalArgumentException("The resource was not loaded properly!", ex);
        }
        return prop;
    }

    public static InputStream openResource(String name) {
        InputStream is = IO.class.getClassLoader().getResourceAsStream(name);
        if (is == null) {
            throw new IllegalArgumentException("The resource was not found!");
        }
        return is;
    }
}
