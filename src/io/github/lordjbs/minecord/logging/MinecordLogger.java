package io.github.lordjbs.minecord.logging;

import io.github.lordjbs.minecord.exception.LoggerException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author lordjbs
 * Copyright (C) 2017 lordjbs.
 */

public class MinecordLogger
{

    /**
     * this is the set LoggerName. Its only for the file, so its not public and cannot conflict.
     */
    private String loggerName = "";
    /**
     * Standard Exception.
     */

    /**
     * Creates a Logger Instance
     * @param SUPER logger name
     */
    public MinecordLogger(String SUPER)
    {
        try {
            a();
            if (SUPER.length() < 1) {
                throw new LoggerException("SUPER is smaller than 1");
            }
            loggerName = SUPER;
        }catch(LoggerException le)
        {
            le.printStackTrace();
        }
    }

    /**
     * class.info - LOGTYPE: information
     * @param message message to print out
     */
    public void info(String message)
    {
        try{
            a();
            System.out.println("[" + getTime() + "] [" + loggerName  + "] [INFO] " + message);
        }catch(LoggerException le)
        {
            le.printStackTrace();
        }
    }

    /**
     * class.info - LOGTYPE: warning
     * @param message message to print out
     */
    public void warn(String message)
    {
        try{
            a();
            System.out.println("[" + getTime() + "] [" + loggerName  + "] [WARN] " + message);
        }catch(LoggerException le)
        {
            le.printStackTrace();
        }
    }

    /**
     * class.info - LOGTYPE: error
     * @param message message to print out
     */
    public void error(String message)
    {
        try{
            a();
            System.err.println("[" + getTime() + "] [" + loggerName  + "] [ERROR] " + message);
            ExceptionListSystem.addException(message);
        }catch(LoggerException le)
        {
            le.printStackTrace();
        }
    }

    public String getTime()
    {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     * This is a function that throws a LoggerException, so the Try/Catch wont cry
     */
    private void a() throws LoggerException {}

}
