package com.tisawesomeness.minecord;

import net.dv8tion.jda.api.events.session.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class StatusListener extends ListenerAdapter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss:SSS")
            .withZone(ZoneId.of("UTC"));

    @Override
    public void onReady(ReadyEvent e) {
        processStatus(e);
    }
    @Override
    public void onSessionInvalidate(SessionInvalidateEvent e) {
        processStatus(e);
    }
    @Override
    public void onSessionDisconnect(SessionDisconnectEvent e) {
        processStatus(e);
    }
    @Override
    public void onSessionResume(SessionResumeEvent e) {
        processStatus(e);
    }
    @Override
    public void onSessionRecreate(SessionRecreateEvent e) {
        processStatus(e);
    }
    @Override
    public void onShutdown(ShutdownEvent e) {
        processStatus(e);
    }
    @Override
    public void onGenericSessionEvent(GenericSessionEvent e) {
        processStatus(e);
    }

    private static void processStatus(GenericSessionEvent e) {
        String emote;
        String status;
        switch (e.getState()) {
            case READY:
                Bot.readyShard();
                emote = ":ballot_box_with_check:";
                status = "Ready";
                break;
            case INVALIDATED:
                emote = ":no_entry_sign:";
                status = "Invalidated";
                break;
            case DISCONNECTED:
                emote = ":no_mobile_phones:";
                status = "Disconnected";
                break;
            case RESUMED:
                emote = ":arrow_forward:";
                status = "Resumed";
                break;
            case RECREATED:
                emote = ":recycle:";
                status = "Recreated";
                break;
            case SHUTDOWN:
                emote = ":octagonal_sign:";
                status = "Shutdown";
                break;
            default:
                emote = ":interrobang:";
                status = "Unknown";
        }

        String time = FORMATTER.format(Instant.now());
        int shardId = e.getJDA().getShardInfo().getShardId();
        int shardCount = e.getJDA().getShardInfo().getShardTotal();
        System.out.printf("%s Shard %03d/%03d %s\n", time, shardId, shardCount, status);
        String logMsg = String.format("`%s` %s Shard `%03d/%03d` %s", time, emote, shardId, shardCount, status);
        Bot.logger.statusLog(logMsg);
    }

}
