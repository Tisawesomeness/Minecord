package com.tisawesomeness.minecord;

import picocli.CommandLine;

import java.util.Locale;
import java.util.ResourceBundle;

public class Main {
	
	public static void main(String[] args) {
        ArgsHandler handle = new ArgsHandler();
        int exitCode = new CommandLine(handle).execute(args);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
        new Bot().setup(handle);
	}

	// i18n handlers
    public static ResourceBundle getDefaultLang(String ignore) {
	    return ResourceBundle.getBundle("lang");
    }
    public static ResourceBundle getLang(Locale l) {
        return ResourceBundle.getBundle("lang", l);
    }

}
