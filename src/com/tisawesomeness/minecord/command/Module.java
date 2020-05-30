package com.tisawesomeness.minecord.command;

/**
 * A category of commands grouped together.
 */
public class Module {
    private String name;
    private boolean hidden;
    private String moduleHelp;
    private Command[] commands;

    public Module(String name, Command... commands) {
        this(name, false, null, commands);
    }
    public Module(String name, boolean hidden, String moduleHelp, Command... commands) {
        this.name = name;
        this.hidden = hidden;
        this.moduleHelp = moduleHelp;
        this.commands = commands;
    }

    public String getName() {
        return name;
    }
    public boolean isHidden() {
        return hidden;
    }
    public String getHelp() {
        return moduleHelp;
    }
    public Command[] getCommands() {
        return commands;
    }
}