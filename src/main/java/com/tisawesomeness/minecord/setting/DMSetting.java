package com.tisawesomeness.minecord.setting;

import com.tisawesomeness.minecord.database.DMSettingContainer;
import com.tisawesomeness.minecord.database.DatabaseCache;
import com.tisawesomeness.minecord.database.SettingContainer;

import lombok.NonNull;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.SQLException;
import java.util.Optional;

public abstract class DMSetting<T> extends Setting<T> {

    // Since DM settings require a *less* specific container, casting is both necessary and safe
    public Optional<T> get(@NonNull SettingContainer obj) {
        return get((DMSettingContainer) obj);
    }
    public T getEffective(@NonNull SettingContainer obj) {
        return getEffective((DMSettingContainer) obj);
    }
    public void set(@NonNull SettingContainer obj, @NonNull T setting) throws SQLException {
        set((DMSettingContainer) obj, setting);
    }
    public void reset(@NonNull SettingContainer obj) throws SQLException {
        reset((DMSettingContainer) obj);
    }

    public abstract Optional<T> get(@NonNull DMSettingContainer obj);
    public T getEffective(@NonNull DMSettingContainer obj) {
        return get(obj).orElse(getDefault());
    }
    public abstract void set(@NonNull DMSettingContainer obj, @NonNull T setting) throws SQLException;
    public abstract void reset(@NonNull DMSettingContainer obj) throws SQLException;

    public Optional<T> get(@NonNull MessageReceivedEvent e, DatabaseCache cache) {
        long gid = e.getGuild().getIdLong();
        if (e.isFromGuild()) {
            Optional<T> setting = get(cache.getChannel(e.getChannel().getIdLong(), gid));
            if (setting.isPresent()) {
                return setting;
            }
            return get(cache.getGuild(gid));
        }
        return get(cache.getUser(e.getAuthor().getIdLong()));
    }

}
