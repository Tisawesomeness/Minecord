package com.tisawesomeness.minecord;

import lombok.NoArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class, {@link Main}, and {@link DynamicLoader} are loaded by the default system classloader.
 *
 * Used to reload the bot and keep persistent information.
 */
public class ReloadHandler {
    private static final boolean propagate = false;
    private final static String botClass = "com.tisawesomeness.minecord.Bot";
    private final static String packClass = "com.tisawesomeness.minecord.PersistPackage";
    private final static ClassLoader cl = ClassLoader.getSystemClassLoader();

    /**
     * Starts the dynamic loader in a new thread for use in hot code reloading
     * @param args The program's args
     */
    public void load(String[] args) {
        new Thread(() -> startDynamicLoad(args)).start();
    }

    /**
     * Hot reloads the bot using the dynamic loader
     * @param args The program's args
     * @param pack The information to keep when reloading
     */
    public void reload(String[] args, PersistPackage pack) {
        new Thread(() -> startDynamicLoad(args, pack)).start();
    }

    private void startDynamicLoad(String[] args) {
        startDynamicLoad(args, null);
    }
    private void startDynamicLoad(String[] args, PersistPackage pack) {
        // Dynamically start a new bot
        DynamicLoader dl = new DynamicLoader(cl);
        if (propagate) Thread.currentThread().setContextClassLoader(dl);
        Class<?> clazz = dl.loadClass(botClass);
        Class<?> packClazz = dl.loadClass(packClass);
        try {
            Object bot = clazz.newInstance(); // bot = new Bot()
            if (pack != null) {
                Object loadedPack = call(packClazz, null, "of", pack.toArray()); // loadedPack = new PersistPackage(pack)
                call(clazz, bot, "setPack", loadedPack); // bot.setPack(pack)
            }
            call(clazz, bot, "setup", args, true); // bot.setup(args, true)
        } catch (ClassCastException ex) {
            // Do nothing
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Calls a method using reflection.
     * @param clazz The class the method is from. Can be loaded by a different classloader.
     * @param obj The object used to call the method. Often an instance of {@code clazz}. May be null for static methods.
     * @param methodName The name of the method, without parentheses.
     * @param args The arguments of the method.
     * @return The return object of the call. <b>If {@code clazz} is loaded with a different classloader than the current thread's, casting will throw a {@link ClassCastException}!</b>
     * @throws InvocationTargetException If the method throws an exception.
     * @throws IllegalAccessException If the method is not accessible.
     * @throws IllegalArgumentException If the incorrect number or type of arguments are provided, or {@code methodName} is not a valid method.
     */
    private Object call(Class<?> clazz, Object obj, String methodName, Object... args) throws InvocationTargetException, IllegalAccessException {
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getName().equals(methodName)) {
                return m.invoke(obj, args);
            }
        }
        throw new IllegalArgumentException("Method name not valid.");
    }

}
