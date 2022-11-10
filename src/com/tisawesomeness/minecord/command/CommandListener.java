package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.util.ArrayUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.type.Either;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        Optional<Either<String, Command<?>>> mappingOpt = Registry.getCommandMapping(e.getName());
        if (!mappingOpt.isPresent()) {
            return;
        }
        Either<String, Command<?>> mapping = mappingOpt.get();
        if (mapping.isLeft()) {
            return;
        }
        Command<?> c = mapping.getRight();
        if (!(c instanceof SlashCommand)) {
            return;
        }
        SlashCommand cmd = (SlashCommand) c;

        //Check for elevation
        if (cmd.getInfo().elevated && !Database.isElevated(e.getUser().getIdLong())) {
            e.reply(":warning: Insufficient permissions!").setEphemeral(true).queue();
            return;
        }

        //Check for cooldowns, skipping if user is elevated
        if (!(Config.getElevatedSkipCooldown() && Database.isElevated(e.getUser().getIdLong())) && cmd.getInfo().cooldown > 0) {
            long cooldownLeft = Registry.getCooldownLeft(cmd, e.getUser());
            if (cooldownLeft > 0) {
                //Format warning message
                StringBuilder seconds = new StringBuilder(String.valueOf(cooldownLeft));
                while (seconds.length() < 4) {
                    seconds.insert(0, "0");
                }
                seconds.insert(seconds.length() - 3, ".");
                e.reply(":warning: Wait " + seconds + " more seconds.").setEphemeral(true).queue();
                return;
            }
        }

        Command.Result result = null;
        Exception exception = null;
        Registry.useCommand(cmd, e.getUser());
        try {
            result = cmd.run(e);
        } catch (Exception ex) {
            exception = ex;
        }
        handleResult(cmd, result, exception, e);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        Message m = e.getMessage();
        if (m.getContentRaw().isEmpty()) {
            return; // MESSAGE_CONTENT got yoinked
        }

        // Get all values that change based on channel type
        String prefix = MessageUtils.getPrefix(e);
        boolean deleteCommands = false;
        boolean canEmbed = true;
        if (e.isFromType(ChannelType.TEXT) || e.isFromType(ChannelType.VOICE) || e.isFromType(ChannelType.NEWS) || e.isFromType(ChannelType.FORUM) || e.isFromThread()) {
            Member sm = e.getGuild().getSelfMember();
            GuildChannel tc = e.getGuildChannel();
            if (!sm.hasPermission(tc, Permission.MESSAGE_SEND) || Database.isBanned(e.getGuild().getIdLong())) {
                return;
            }
            deleteCommands = sm.hasPermission(tc, Permission.MESSAGE_MANAGE) &&
                    Database.getDeleteCommands(e.getGuild().getIdLong());
            canEmbed = sm.hasPermission(tc, Permission.MESSAGE_EMBED_LINKS);
        } else if (e.isFromType(ChannelType.PRIVATE)) {
            prefix = Config.getPrefix();
        } else {
            return;
        }

        //Check if message can be acted upon
        User a = m.getAuthor();
        if (a.isBot() || Database.isBanned(a.getIdLong())) return;

        //If the message is a valid command
        String[] content = MessageUtils.getContent(m, prefix, e.getJDA().getSelfUser());
        if (content == null || content.length == 0) {
            return;
        }

        //Extract name and argument list
        String name = content[0];
        if (name.isEmpty()) {
            return; //If there is a space after prefix, don't process any more
        }
        String[] args = ArrayUtils.remove(content, 0);

        // Embed links is required for 90% of commands, so send a message if the bot does not have it.
        if (!canEmbed) {
            e.getChannel().sendMessage(":warning: I need Embed Links permissions to use commands!").queue();
            return;
        }

        // Get command info if the command has been registered
        String delMessage = deletedMigrateMessage(name, args);
        if (delMessage != null) {
            e.getChannel().sendMessage(delMessage).queue();
            return;
        }
        Optional<Either<String, Command<?>>> mappingOpt = Registry.getCommandMapping(name);
        if (!mappingOpt.isPresent()) {
            return;
        }
        Either<String, Command<?>> mapping = mappingOpt.get();
        if (mapping.isLeft()) {
            String migrateTo = mapping.getLeft();
            e.getChannel().sendMessage(migrateMessage(migrateTo)).queue();
            return;
        }
        Command<?> cmd = mapping.getRight();
        if (!(cmd instanceof LegacyCommand)) {
            String migrateTo = cmd.getInfo().name;
            e.getChannel().sendMessage(migrateMessage(migrateTo)).queue();
            return;
        }
        Command.CommandInfo ci = cmd.getInfo();

        //Delete message if enabled in the config and the bot has permissions
        if (deleteCommands) {
            m.delete().queue();
        }

        MessageChannel c = e.getChannel();

        //Check for elevation
        if (ci.elevated && !Database.isElevated(a.getIdLong())) {
            c.sendMessage(":warning: Insufficient permissions!").queue();
            return;
        }

        //Check for cooldowns, skipping if user is elevated
        if (!(Config.getElevatedSkipCooldown() && Database.isElevated(a.getIdLong())) && ci.cooldown > 0) {
            long cooldownLeft = Registry.getCooldownLeft(cmd, a);
            if (cooldownLeft > 0) {
                //Format warning message
                StringBuilder seconds = new StringBuilder(String.valueOf(cooldownLeft));
                while (seconds.length() < 4) {
                    seconds.insert(0, "0");
                }
                seconds.insert(seconds.length() - 3, ".");
                c.sendMessage(":warning: Wait " + seconds + " more seconds.").queue();
                return;
            }
        }

        //Run command
        Command.Result result = null;
        Exception exception = null;
        Registry.useCommand(cmd, a);
        try {
            result = ((LegacyCommand) cmd).run(args, e);
        } catch (Exception ex) {
            exception = ex;
        }

        //Catch exceptions
        handleResult((LegacyCommand) cmd, result, exception, e);
    }

    private static <T extends Event> void handleResult(Command<T> cmd, Command.Result result, Exception exception, T e) {
        if (result == null) {
            String err;
            if (exception == null) {
                err = ":boom: There was a null exception";
            } else {
                if (Config.getDebugMode()) {
                    exception.printStackTrace();

                    StringBuilder errSb = new StringBuilder();
                    boolean seenMinecordCode = false;
                    for (StackTraceElement ste : exception.getStackTrace()) {
                        String className = ste.getClassName();
                        if (className.startsWith("com.google.gson") || className.startsWith("net.kyori")) {
                            continue;
                        }
                        errSb.append("\n").append(ste);
                        if (className.startsWith("net.dv8tion") || className.startsWith("com.neovisionaries")) {
                            if (seenMinecordCode) {
                                errSb.append("...");
                                break;
                            }
                        } else if (className.startsWith("com.tisawesomeness") || className.startsWith("br.com.azalim")) {
                            seenMinecordCode = true;
                        }
                    }

                    err = ":boom: There was an unexpected exception: " + MarkdownUtil.monospace(exception.toString()) +
                            "\n" + MarkdownUtil.codeblock("java", errSb.toString());
                } else {
                    err = ":boom: There was an unexpected exception: " + MarkdownUtil.monospace(exception.toString());
                }
            }
            String logMsg = "EXCEPTION: " + MarkdownUtil.monospace(cmd.debugRunCommand(e)) + "\n" + err;
            if (logMsg.length() > Message.MAX_CONTENT_LENGTH) {
                logMsg = logMsg.substring(0, Message.MAX_CONTENT_LENGTH - 6) + "...```";
            }
            MessageUtils.log(logMsg);
            if (err.length() > Message.MAX_CONTENT_LENGTH) {
                err = err.substring(0, Message.MAX_CONTENT_LENGTH - 6) + "...```";
            }
            cmd.sendFailure(e, MessageCreateData.fromContent(err));
            //If message is empty
        } else if (result.message == null) {
            if (result.outcome != null && result.outcome != Command.Outcome.SUCCESS) {
                System.out.println("Command \"" + cmd.getInfo().name + "\" returned an empty " +
                        result.outcome.toString().toLowerCase());
            }
        } else {
            switch (result.outcome) {
                case SUCCESS:
                    cmd.sendSuccess(e, result.message);
                    break;
                case ERROR:
                    System.out.println("Command \"" + cmd.getInfo().name + "\" returned an error: " +
                            result.message.getContent());
                    // fallthrough
                case WARNING:
                    cmd.sendFailure(e, result.message);
                    break;
            }
        }
    }

    private static String migrateMessage(String migrateTo) {
        return String.format("Minecord has moved to slash commands. Use `/%s` instead.", migrateTo);
    }
    private static final List<String> colorShortcutNames = Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f");
    private static String deletedMigrateMessage(String name, String[] args) {
        if (name.equalsIgnoreCase("render")) {
            if (args.length == 0) {
                return "Minecord has moved to slash commands. Use `/avatar`, `/body`, or `/head` instead.";
            }
            if (args[0].equalsIgnoreCase("avatar")) {
                return "Minecord has moved to slash commands. Use `/avatar` instead.";
            }
            if (args[0].equalsIgnoreCase("body")) {
                return "Minecord has moved to slash commands. Use `/body` instead.";
            }
            if (args[0].equalsIgnoreCase("head")) {
                return "Minecord has moved to slash commands. Use `/head` instead.";
            }
        } else if (colorShortcutNames.contains(name.toLowerCase())) {
            return String.format("Minecord has moved to slash commands. Use `/color %s` instead.", name.toLowerCase());
        }
        return null;
    }

}
