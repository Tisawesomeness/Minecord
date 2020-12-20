package com.tisawesomeness.minecord.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Utility class to for eval help
 */
public final class EvalUtils {

    /**
     * Generates the help message for an object
     * @param o The input object
     * @return A help message with the class name, list of fields, and list of methods
     */
    public String elp(Object o) {
        return help(o);
    }
    private String help(Object o) {
        Class<?> clazz = o.getClass();
        String fields = "NONE";
        if (clazz.getFields().length > 0) {
            fields = Arrays.stream(clazz.getFields())
                    .sorted(Comparator.comparing(Field::getName))
                    .map(EvalUtils::getDeclaration)
                    .collect(Collectors.joining("\n"));
        }
        String methods = "NONE";
        if (clazz.getMethods().length > 0) {
            methods = Arrays.stream(clazz.getMethods())
                    .filter(m -> o.getClass().equals(Object.class) || !m.getDeclaringClass().equals(Object.class))
                    .sorted(Comparator.comparing(Method::getName))
                    .map(EvalUtils::getSignature)
                    .collect(Collectors.joining("\n"));
        }
        return String.format("%s\n\nFields:\n%s\n\nMethods:\n%s", clazz.getName(), fields, methods);
    }

    private static String getDeclaration(Field f) {
        String finall = Modifier.isFinal(f.getModifiers()) ? "final " : "";
        return finall + cleanType(f.getGenericType()) + " " + f.getName();
    }
    /**
     * Generates a shortened signature for a method.
     * Exceptions, non-static modifiers, generic parameters, and annotations are excluded for brevity.
     * @param m The method object to generate a signature for
     * @return The signature as a string
     */
    private static String getSignature(Method m) {
        String params = Arrays.stream(m.getParameters())
                .map(p -> cleanType(p.getType()))
                .collect(Collectors.joining(", ")); // Comma-separated args like in "add(int x, int y)"
        String staticc = Modifier.isStatic(m.getModifiers()) ? "static " : ""; // Only static is included for brevity
        return String.format("%s%s %s(%s)", staticc, cleanType(m.getGenericReturnType()), m.getName(), params);
    }
    /**
     * Generates a clean string for a type, transforming "java.lang.String" into "String".
     * Takes generics into account and parses them into the diamond operator format.
     * @param t The type reflection object
     * @return The type name as a string
     */
    private static String cleanType(Type t) {
        String typeName = t.getTypeName();
        if (typeName.contains("<")) {
            String[] split = typeName.split("<");
            String type = split[0].substring(split[0].lastIndexOf('.') + 1); // Thanks -1 on failure for making this super clean
            String generic = split[1].substring(split[1].lastIndexOf('.') + 1, split[1].length() - 1);
            return type + "<" + generic + ">";
        }
        return typeName.substring(typeName.lastIndexOf('.') + 1);
    }

}
