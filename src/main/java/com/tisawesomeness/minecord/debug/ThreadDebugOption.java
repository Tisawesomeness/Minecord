package com.tisawesomeness.minecord.debug;

import lombok.NonNull;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Debugs all current threads in the JVM
 */
public class ThreadDebugOption implements DebugOption {
    public @NonNull String getName() {
        return "threads";
    }
    public @NonNull String debug() {
        // Although getting all stack traces is expensive
        // this is called from an admin-only command
        // so performance is not necessary
        Set<Thread> threads = Thread.getAllStackTraces().keySet();
        return threads.stream()
                .map(ThreadDebugOption::getThreadInfo)
                .collect(Collectors.joining("\n"));
    }

    private static String getThreadInfo(Thread t) {
        String threadInfo = String.format("%s %s: ID `%s` | Priority `%s`",
                t.getState(), MarkdownUtil.bold(t.getName()), t.getId(), t.getPriority());
        if (t.isDaemon()) {
            threadInfo += " | DAEMON";
        }
        if (Thread.currentThread().equals(t)) {
            return MarkdownUtil.italics(threadInfo);
        }
        return threadInfo;
    }
}
