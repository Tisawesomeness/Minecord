package io.github.lordjbs.minecord.java;

import io.github.lordjbs.minecord.java.exceptions.NotStringException;
import io.github.lordjbs.minecord.logging.ExceptionListSystem;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author lordjbs
 * Copyright (C) 2017 lordjbs.
 */
public class StringHolder {

    private String[] STANDARD_VALUE = null;
    private String[] val = STANDARD_VALUE;
    public String version = "ALPHA_1";

    public StringHolder() {
        this.val = STANDARD_VALUE;
    }

    public StringHolder(String[] values) {
        this.val = values;
    }

    public void set(String[] newval) {
        try {
            this.val = newval;
        }catch(NotStringException ex) {
            ex.printStackTrace();
            ExceptionListSystem.addException(ex.getMessage());
        }
    }

    public void set(int index, String valtoadd) {
        this.val[index] = valtoadd;
    }

    public void clear() {
        this.val = new String[]{};
    }

    public String[] get() {
        return this.val;
    }

    public String get(int index) {
        return this.val[index];
    }

    public String getValueParsed() {
        return Arrays.stream(this.val).collect(Collectors.joining(" "));
    }
}
