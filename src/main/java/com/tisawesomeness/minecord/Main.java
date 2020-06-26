package com.tisawesomeness.minecord;

import picocli.CommandLine;

public class Main {
	
	public static void main(String[] args) {
        ArgsHandler handle = new ArgsHandler();
        int exitCode = new CommandLine(handle).execute(args);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
        new Bot().setup(handle);
	}

}
