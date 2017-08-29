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

    /**
     * Just set the normal Stringholder. Mainly for using .set/.add afterwards.
     */
    public StringHolder() {
        this.val = STANDARD_VALUE;
    }

    /**
     * Make new StringHolder with already an existing String[].
     * @param values A base string[]. You can also use the Stringholder without args.
     */
    public StringHolder(String[] values) {
        this.val = values;
    }

    /**
     * Set the old to a complete new String[].
     * @param newValue The new String[] value.
     */
    public void setComplete(String[] newValue) {
        try {
            this.val = newValue;
        }catch(NotStringException ex) {
            ex.printStackTrace();
            ExceptionListSystem.addException(ex.getMessage());
        }
    }

    /**
     * Set the String[index] to a new value.
     * @param index The index of the String[] that should be set.
     * @param valueToAdd The value to add.
     */
    public void set(int index, String valueToAdd) {
        this.val[index] = valueToAdd;
    }

    /**
     * Adds a String at the next not used position.
     * @param str The String to add.
     */
    public void add(String str) {
        this.val[(val.length) + 1] = str;
    }

    /**
     * Gets the full StringHolder as String[].
     * @return String[]
     */
    public String[] get() {
        return this.val;
    }

    /**
     * Get a value from the choosen index.
     * @param index The index.
     * @return String
     */
    public String get(int index) {
        return this.val[index];
    }

    /**
     * Get all strings in one String.
     * @return String
     */
    public String getValueParsed() {
        return Arrays.stream(this.val).collect(Collectors.joining(" "));
    }


    /**
     * Removes the number from index given, and sorts.
     * @param index index to remove-
     */
    public void remove(int index)  {
        String[] oldval = val;
        String[] newval = {};
        int currentindex = 0;
        for (String s : oldval) {
            if(currentindex != index) {
                newval[currentindex] = s;
            }
            currentindex++;
        }
    }

    /**
     * Search value and remove it. It sorts after that.
     * @param name the value to remove
     */
    public void remove(String name)  {
        String[] oldval = val;
        String[] newval = {};
        int currentindex = 0;
        for (String s : oldval) {
            if(!oldval[currentindex].equals(name)) {
                newval[currentindex] = s;
            }
            currentindex++;
        }
    }

    public void clear() {
        this.val = new String[]{};
    }


    public void removeAll() {
        this.val = new String[]{};
    }
}
