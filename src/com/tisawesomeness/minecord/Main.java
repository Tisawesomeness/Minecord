package com.tisawesomeness.minecord;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.ShardManager;

@SuppressWarnings("unused")
public class Main {

    protected static ClassLoader cl;

    //Store data between reloads
    private static ShardManager shards;
    private static User user;
    private static Message message;
    private static long birth;

    public static void main(String[] args) {

        if (!Bot.setup(args, false)) {
            cl = Thread.currentThread().getContextClassLoader();
            load(args);
        }

    }

    //Start loader
    public static void load(String[] args) {
        new Thread(new Loader(args)).start();
    }

    //Getters and setters
    public static Message getMessage(String ignore) {
        return message;
    }
    public static void setMessage(Message m) {
        message = m;
    }
    public static User getUser(String ignore) {
        return user;
    }
    public static void setUser(User u) {
        user = u;
    }
    public static ShardManager getShards(String ignore) {
        return shards;
    }
    public static void setShards(ShardManager s) {
        shards = s;
    }
    public static long getBirth(String ignore) {
        return birth;
    }
    public static void setBirth(long b) {
        birth = b;
    }

}
