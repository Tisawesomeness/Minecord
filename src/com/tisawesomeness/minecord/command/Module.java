package com.tisawesomeness.minecord.command;

/**
 * A category of commands grouped together.
 */
public class Module {
    private final String name;
    private final boolean hidden;
    private final String moduleHelp;
    private final Command[] commands;

    /**
     * Creates a new user-facing module.
     * @param name The display name of the module
     * @param commands The list of commands it contains
     */
    public Module(String name, Command... commands) {
        this(name, false, null, commands);
    }
    /**
     * Creates a new module.
     * @param name The display name of the module
     * @param hidden Whether the module is hidden to users
     * @param moduleHelp Extra info displayed when using &help <module>. May be null. Use {&} to substitute in the current prefix.
     * @param commands The list of commands it contains
     */
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
    public String getHelp(String prefix) {
        return moduleHelp == null ? null : moduleHelp.replace("{&}", prefix);
    }
    public Command[] getCommands() {
        return commands;
    }
}