package io.github.lordjbs.ConCore;

import io.github.lordjbs.ConCore.Console.Logger;

/*
* ConCore version C.1
* Copyright (C) 2017 by lordjbs.
* https://lordjbs.github.io
*/
public class ConCore extends PrivateCon
{
    public static String logName;

    public static void initConCore(String logname)
    {
        //set name
        log("ConCore > (line 15; io.github.lordjbs.ConCore.ConCore) setting name!");
        logName = logname;

        log("ConCore > (line 18; io.github.lordjbs.ConCore.ConCore) Success! ConCore successfully started");
    }

}
