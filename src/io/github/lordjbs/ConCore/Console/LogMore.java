package io.github.lordjbs.ConCore.Console;

/*
* ConCore version C.1
* Copyright (C) 2017 by lordjbs.
* https://lordjbs.github.io
*/

import io.github.lordjbs.ConCore.ConCore;

public class LogMore
{
    static void Log(String log, int mode)
    {
        if(mode == 1)
        {
            System.out.println("MineCordBot > CodeName: Log -> ");
        }else if(mode == 2)
        {
            System.out.println("MineCordBot > CodeName: Warn -> ");
        }else if(mode == 3)
        {
            System.out.println("MineCordBot > CodeName: Error -> ");

        }else{
            System.out.println("Error: Unknown mode.");
        }
    }
}
