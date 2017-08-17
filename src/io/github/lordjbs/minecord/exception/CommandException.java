package io.github.lordjbs.minecord.exception;

/**
 * @author lordjbs
 * Copyright (C) 2017 lordjbs.
 */
public class CommandException extends Exception
{
    public CommandException()
    {
        super();
    }

    public CommandException(String message)
    {
        super(message);
    }

    public CommandException(Throwable cause)
    {
        super(cause);
    }

    public CommandException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
