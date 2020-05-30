package com.tisawesomeness.minecord.command;

/**
 * A category of commands grouped together.
 */
public class Module {
    private String name;
    private Command[] commands;
    private String moduleHelp;
    private boolean isAdmin;

    public Module(String name, Command... commands) {
        this(name, false, null, commands);
    }
    public Module(String name, boolean isAdmin, String moduleHelp, Command... commands) {
        this.name = name;
        this.commands = commands;
        this.isAdmin = isAdmin;
    }

    public String getName() {
        return name;
    }
    public Command[] getCommands() {
        return commands;
    }
    public String getHelp() {
        return moduleHelp;
    }
    public boolean isAdmin() {
        return isAdmin;
    }
}