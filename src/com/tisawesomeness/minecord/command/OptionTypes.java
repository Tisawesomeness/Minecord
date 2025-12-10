package com.tisawesomeness.minecord.command;

import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import javax.annotation.Nullable;
import java.util.function.Function;

public class OptionTypes {
    private OptionTypes() {}

    public static final Type<Boolean> BOOLEAN = new Type<>(OptionTypes::getAsBoolean);
    public static final Type<Integer> INTEGER = new Type<>(OptionTypes::getAsInt);
    public static final Type<Long> LONG = new Type<>(OptionTypes::getAsLong);
    public static final Type<Double> DOUBLE = new Type<>(OptionTypes::getAsDouble);
    public static final Type<String> STRING = new Type<>(OptionTypes::getAsString);
    public static final Type<User> USER = new Type<>(OptionTypes::getAsUser);
    public static final Type<Member> MEMBER = new Type<>(OptionTypes::getAsMember);
    public static final Type<Role> ROLE = new Type<>(OptionTypes::getAsRole);

    private static @Nullable Boolean getAsBoolean(OptionMapping option) {
        try {
            return option.getAsBoolean();
        } catch (IllegalStateException ignored) {
            return null;
        }
    }

    private static @Nullable Integer getAsInt(OptionMapping option) {
        try {
            return option.getAsInt();
        } catch (IllegalStateException ignored) {
            return null;
        }
    }

    private static @Nullable Long getAsLong(OptionMapping option) {
        try {
            return option.getAsLong();
        } catch (IllegalStateException ignored) {
            return null;
        }
    }

    private static @Nullable Double getAsDouble(OptionMapping option) {
        try {
            return option.getAsDouble();
        } catch (IllegalStateException ignored) {
            return null;
        }
    }

    private static @Nullable String getAsString(OptionMapping option) {
        try {
            return option.getAsString();
        } catch (IllegalStateException ignored) {
            return null;
        }
    }

    private static @Nullable User getAsUser(OptionMapping option) {
        try {
            return option.getAsUser();
        } catch (IllegalStateException ignored) {
            return null;
        }
    }

    private static @Nullable Member getAsMember(OptionMapping option) {
        try {
            return option.getAsMember();
        } catch (IllegalStateException ignored) {
            return null;
        }
    }

    private static @Nullable Role getAsRole(OptionMapping option) {
        try {
            return option.getAsRole();
        } catch (IllegalStateException ignored) {
            return null;
        }
    }

    @AllArgsConstructor
    public static class Type<T> {

        private final Function<OptionMapping, T> func;

        public T resolve(OptionMapping optionMapping) {
            return func.apply(optionMapping);
        }

    }

}
