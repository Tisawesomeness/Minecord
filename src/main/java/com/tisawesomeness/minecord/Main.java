package com.tisawesomeness.minecord;

import java.util.Locale;
import java.util.ResourceBundle;

public class Main {
	
	private static ClassLoader cl;
	
	public static void main(String[] args) {

		if (!new Bot().setup(args, false)) {
			cl = Thread.currentThread().getContextClassLoader();
            new ReloadHandler().load(args);
		}
		
	}

	// i18n handlers
    public static ResourceBundle getDefaultLang(String ignore) {
	    return ResourceBundle.getBundle("lang");
    }
    public static ResourceBundle getLang(Locale l) {
        return ResourceBundle.getBundle("lang", l);
    }

}
