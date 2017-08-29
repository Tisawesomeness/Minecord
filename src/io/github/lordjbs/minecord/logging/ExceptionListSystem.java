package io.github.lordjbs.minecord.logging;

import java.util.ArrayList;

/**
 * @author lordjbs
 * Copyright (C) 2017 lordjbs.
 */
public class ExceptionListSystem {
    public static ArrayList<String> exceptions = new ArrayList<>();
    public static String readExceptions() {
        int i =0;
        StringBuilder str = new StringBuilder();
        for(String x : exceptions) {
            str.append(i).append(": ").append(x).append("\n");
            i++;
        }
        return str.toString();
    }

    public static void addException(String msg) {
        if (exceptions.size() > 10) {
            exceptions.remove(exceptions.get(0));
        }
        exceptions.add(msg);
    }
}
