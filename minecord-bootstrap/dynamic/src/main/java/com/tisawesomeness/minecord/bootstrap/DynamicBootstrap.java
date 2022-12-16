package com.tisawesomeness.minecord.bootstrap;

import com.tisawesomeness.minecord.common.Bot;
import com.tisawesomeness.minecord.common.BuildInfo;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Starts the bootstrapper, loading the bot dynamically from an external jar.
 */
@Slf4j
public final class DynamicBootstrap {

    public static void main(String[] args) {
        new Bootstrap(DynamicBootstrap::dynamicLoad).start(args);
    }

    private static Bot dynamicLoad(Bootstrap bootstrap) {
        log.debug("Dynamic loading bot");
        File f = new File(String.format("./MinecordBot-%s.jar", BuildInfo.getInstance().version));
        try {
            ClassLoader cl = URLClassLoader.newInstance(new URL[]{f.toURI().toURL()});
            Class<?> clazz = Class.forName("com.tisawesomeness.minecord.Minecord", true, cl);
            Bot bot = (Bot) clazz.getConstructors()[0].newInstance(bootstrap);
            log.info("Bot successfully loaded from file");
            return bot;
        } catch (MalformedURLException | ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

}
