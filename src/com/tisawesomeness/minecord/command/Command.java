package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.UUID;

public interface Command<T extends Event> {

    /**
     * @return The command info.
     */
    CommandInfo getInfo();

    /**
     * Defines the help text shown by &help <command>.
     * Use {&} to substitute the current prefix, or {@literal @} to substitute the bot mention.
     * @return Never-null help string
     */
    default String getHelp() {
        return getInfo().description + "\n";
    }

    void sendSuccess(T e, MessageCreateData message);
    void sendFailure(T e, MessageCreateData message);

    String debugRunCommand(T e);

    default void handleException(Throwable ex, T e) {
        UUID uuid = UUID.randomUUID();

        String logMsg = String.format("EXCEPTION: `%s`\nID: `%s`\n%s", debugRunCommand(e), uuid, buildErrorMessage(ex));
        String logMsgTrimmed = MessageUtils.trimCodeblock(logMsg);
        System.err.println(uuid);
        ex.printStackTrace();
        MessageUtils.log(logMsgTrimmed);

        if (Config.getDebugMode()) {
            sendFailure(e, MessageCreateData.fromContent(logMsgTrimmed));
        } else {
            String userMsg = ":boom: An unexpected error occurred!\nID: " + MarkdownUtil.monospace(uuid.toString());
            String userMsgTrimmed = MessageUtils.trimCodeblock(userMsg);
            sendFailure(e, MessageCreateData.fromContent(userMsgTrimmed));
        }
    }

    static String buildErrorMessage(Throwable ex) {
        if (ex == null) {
            return ":boom: There was a null exception";
        } else {
            String cleanedStackTrace = buildStackTrace(ex);
            return ":boom: There was an unexpected exception: " + MarkdownUtil.monospace(ex.toString()) +
                    "\n" + MarkdownUtil.codeblock("java", cleanedStackTrace);
        }
    }

    static String buildStackTrace(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        boolean seenMinecordCode = false;
        for (StackTraceElement ste : ex.getStackTrace()) {
            String className = ste.getClassName();
            if (className.startsWith("com.google.gson") || className.startsWith("net.kyori")) {
                continue;
            }
            sb.append("\n").append(ste);
            if (className.startsWith("net.dv8tion") || className.startsWith("com.neovisionaries")) {
                if (seenMinecordCode) {
                    sb.append("...");
                    break;
                }
            } else if (className.startsWith("com.tisawesomeness") || className.startsWith("br.com.azalim")) {
                seenMinecordCode = true;
            }
        }
        return sb.toString();
    }

    /**
     * Represents all the data needed to register a command.
     */
    class CommandInfo {

        /**
         * The name needed to call the command, shown on the help menu.
         */
        public final String name;
        /**
         * The description that appears in the help menu.
         */
        public final String description;
        /**
         * The command usage, such as "\<player\> [time]"
         */
        public final String usage;
        /**
         * The cooldown of the command in miliseconds. Enter anything less than 1 to disable the cooldown.
         */
        public final long cooldown;
        /**
         * Whether the command is hidden from the help menu.
         */
        public final boolean hidden;
        /**
         * Whether the user must be an elevated user to execute this command.
         */
        public final boolean elevated;

        /**
         * Represents all the data needed to register a command.
         * @param name The name needed to call the command, shown on the help menu.
         * @param description The description shown on the help menu.
         * @param cooldown The cooldown of the command in milliseconds. Enter anything less than 1 to disable the cooldown.
         * @param hidden Whether to hide the command from the help menu.
         * @param elevated Whether the user must be an elevated user to execute this command.
         */
        public CommandInfo(String name, String description, String usage, long cooldown, boolean hidden, boolean elevated) {
            if (name == null) {
                throw new IllegalArgumentException("Name cannot be null.");
            }
            this.name = name;
            this.description = description == null ? "A command." : description;
            this.usage = usage;
            if (cooldown < 0) {
                throw new IllegalArgumentException("Cooldown cannot be less than 0.");
            }
            this.cooldown = cooldown;
            this.hidden = hidden;
            this.elevated = elevated;
        }

    }

    /**
     * Represents the result of a command.
     */
    class Result {
        public final Outcome outcome;
        public final MessageCreateData message;

        /**
         * Represents the result of a command.
         * @param outcome Represents the outcome of the command, either SUCCESS, WARNING, or ERROR.
         */
        public Result(Outcome outcome) {
            this.outcome = outcome;
            this.message = null;
        }

        /**
         * Represents the result of a command.
         * @param outcome Represents the outcome of the command, either SUCCESS, WARNING, or ERROR.
         * @param message The message to send.
         */
        public Result(Outcome outcome, String message) {
            this.outcome = outcome;
            this.message = new MessageCreateBuilder().setContent(message).build();
        }

        /**
         * Represents the result of a command.
         * @param outcome Represents the outcome of the command, either SUCCESS, WARNING, or ERROR.
         * @param message The message to send.
         */
        public Result(Outcome outcome, MessageEmbed message) {
            this.outcome = outcome;
            this.message = new MessageCreateBuilder().setEmbeds(message).build();
        }
    }

    /**
     * Represents the end result of a command.
     * SUCCESS - Message is sent permanently.
     * WARNING - Message is sent temporarily.
     * ERROR - Message is sent temporarily and logged to console.
     */
    enum Outcome {
        SUCCESS("Success"), WARNING("Warning"), ERROR("Error");

        private final String s;
        Outcome(String s) {
            this.s = s;
        }
        public String toString() {
            return s;
        }
    }

}
