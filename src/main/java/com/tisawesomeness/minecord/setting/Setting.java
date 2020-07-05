package com.tisawesomeness.minecord.setting;

import com.tisawesomeness.minecord.database.DatabaseCache;
import com.tisawesomeness.minecord.database.SettingContainer;
import com.tisawesomeness.minecord.util.type.Validation;

import lombok.NonNull;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.SQLException;
import java.util.Optional;

public abstract class Setting<T> implements ISetting<T> {

    public abstract Optional<T> get(@NonNull SettingContainer obj);
    public T getEffective(@NonNull SettingContainer obj) {
        return get(obj).orElse(getDefault());
    }
    public abstract void set(@NonNull SettingContainer obj, @NonNull T setting) throws SQLException;
    public abstract void reset(@NonNull SettingContainer obj) throws SQLException;

    public Optional<T> get(@NonNull MessageReceivedEvent e, DatabaseCache cache) {
        long gid = e.getGuild().getIdLong();
        if (e.isFromGuild()) {
            Optional<T> setting = get(cache.getChannel(e.getChannel().getIdLong(), gid));
            if (setting.isPresent()) {
                return setting;
            }
            return get(cache.getGuild(gid));
        }
        return Optional.empty();
    }

    public @NonNull Validation<String> tryToSet(SettingContainer obj, @NonNull String input) throws SQLException {
        Optional<T> from = get(obj);
        Validation<T> toValidation = resolve(input);
        if (!toValidation.isValid()) {
            return Validation.propogateError(toValidation);
        }
        T to = toValidation.getValue();

        String name = getDisplayName();
        String fromStr = from.orElse(getDefault()).toString();
        String toStr = to.toString();
        return tryToSetInternal(obj, from, to).toValidation(name, fromStr, toStr);
    }
    private @NonNull SetStatus tryToSetInternal(SettingContainer obj, Optional<T> from, T to) throws SQLException {
        if (from.isPresent()) {
            if (to.equals(getDefault())) {
                set(obj, to);
                return SetStatus.SET_FROM_TO_DEFAULT;
            } else if (to.equals(from.get())) {
                return SetStatus.SET_NO_CHANGE;
            }
        } else if (to.equals(getDefault())) {
            set(obj, to);
            return SetStatus.SET_TO_DEFAULT;
        }
        set(obj, to);
        return SetStatus.SET;
    }

}
