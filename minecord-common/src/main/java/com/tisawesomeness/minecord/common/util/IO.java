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
     * Loads a file from the resources folder.
     * @param name The filename with extension
     * @param clazz The class whose class loader will load the file (this decides which resources folder to load from)
     * @return A string with the contents of the file
     */
    public static @NonNull String loadResource(@NonNull String name, @NonNull Class<?> clazz) {
        InputStream is = openResource(name, clazz);
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
     * @param clazz The class whose class loader will load the file, generally use the calling class
     * @return A properties object with the contents of the file
     */
    public static @NonNull Properties loadPropertiesResource(@NonNull String name, @NonNull Class<?> clazz) {
        InputStream is = openResource(name, clazz);
        Properties prop = new Properties();
        try {
            prop.load(is);
        } catch (IOException ex) {
            throw new IllegalArgumentException("The resource was not loaded properly!", ex);
        }
        return prop;
    }

    /**
     * Copies a file from the resources folder to the file system.
     * @param name The filename with extension
     * @param path The path to the file
     * @throws IOException When an I/O error occurs
     */
    public static void copyResource(@NonNull String name, @NonNull Path path, @NonNull Class<?> clazz) throws IOException {
        Files.copy(openResource(name, clazz), path);
    }

    private static InputStream openResource(String name, Class<?> clazz) {
        InputStream is = clazz.getClassLoader().getResourceAsStream(name);
        if (is == null) {
            throw new IllegalArgumentException("The resource was not found!");
        }
        return is;
    }

}
