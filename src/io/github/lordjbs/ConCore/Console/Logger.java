package io.github.lordjbs.ConCore.Console;

/*
* ConCore version C.1
* Copyright (C) 2017 by lordjbs.
* https://lordjbs.github.io
*/
public class Logger
{
    public static void Log(String log)
    {
        LogMore.Log(log, 1);
    }

    public static void Warn(String log)
    {
        LogMore.Log(log, 2);
    }

    public static void Error(String log)
    {
        LogMore.Log(log, 3);
    }
}
