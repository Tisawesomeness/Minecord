package io.github.lordjbs.ConCore.Console;

/*
* ConCore version C.1
* Copyright (C) 2017 by lordjbs.
* https://lordjbs.github.io
*/

public class LogMore {
	static void Log(String log, int mode) {
		if (mode == 1) {
			System.out.println("MineCordBot > CodeName: Log -> " + log);
		} else if (mode == 2) {
			System.out.println("MineCordBot > CodeName: Warn -> " + log);
		} else if (mode == 3) {
			System.out.println("MineCordBot > CodeName: Error -> " + log);
		} else {
			System.out.println("Error: Unknown mode.");
		}
	}
}
