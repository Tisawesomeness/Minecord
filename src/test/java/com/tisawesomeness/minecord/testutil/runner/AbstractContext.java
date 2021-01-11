package com.tisawesomeness.minecord.testutil.runner;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Lang;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.CommandExecutor;
import com.tisawesomeness.minecord.config.serial.Config;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Collection;

/**
 * Implements CommandContext by throwing {@link #unsupported()} in every non-default method
 */
public abstract class AbstractContext extends CommandContext {

    public String[] getArgs() {
        unsupported();
        return null;
    }
    public @NonNull MessageReceivedEvent getE() {
        unsupported();
        return null;
    }
    public @NonNull Config getConfig() {
        unsupported();
        return null;
    }
    public @NonNull Bot getBot() {
        unsupported();
        return null;
    }
    public @NonNull Command getCmd() {
        unsupported();
        return null;
    }
    public @NonNull CommandExecutor getExecutor() {
        unsupported();
        return null;
    }
    public boolean isElevated() {
        unsupported();
        return false;
    }
    public @NonNull String getPrefix() {
        unsupported();
        return null;
    }
    public Lang getLang() {
        unsupported();
        return null;
    }
    protected void sendMessage(@NonNull CharSequence text) {
        unsupported();
    }
    protected void sendMessage(@NonNull MessageEmbed emb) {
        unsupported();
    }
    public void requestHelp() {
        unsupported();
    }
    public void triggerCooldown() {
        unsupported();
    }
    public @NonNull EmbedBuilder addFooter(@NonNull EmbedBuilder eb) {
        unsupported();
        return null;
    }
    public @NonNull EmbedBuilder brand(@NonNull EmbedBuilder eb) {
        unsupported();
        return null;
    }
    public boolean isFromGuild() {
        unsupported();
        return false;
    }
    public boolean userHasPermission(Permission... permissions) {
        unsupported();
        return false;
    }
    public boolean userHasPermission(Collection<Permission> permissions) {
        unsupported();
        return false;
    }
    public boolean botHasPermission(Permission... permissions) {
        unsupported();
        return false;
    }
    public boolean botHasPermission(Collection<Permission> permissions) {
        unsupported();
        return false;
    }
    public boolean shouldUseMenus() {
        unsupported();
        return false;
    }
    public void log(@NonNull String m) {
        unsupported();
    }
    public void log(@NonNull Message m) {
        unsupported();
    }
    public void log(@NonNull MessageEmbed m) {
        unsupported();
    }

    /**
     * @throws UnsupportedOperationException immediately
     */
    protected void unsupported() {
        throw new UnsupportedOperationException("This operation is not supported!");
    }

}
