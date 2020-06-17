package com.tisawesomeness.minecord;

import java.util.Locale;
import java.util.ResourceBundle;

public class Main {
	
	public static void main(String[] args) {
		new Bot().setup(args, false);
	}

	// i18n handlers
    public static ResourceBundle getDefaultLang(String ignore) {
	    return ResourceBundle.getBundle("lang");
    }
    public static ResourceBundle getLang(Locale l) {
        return ResourceBundle.getBundle("lang", l);
    }

}
