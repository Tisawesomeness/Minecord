package com.tisawesomeness.minecord.common;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.nio.file.Path;

/**
 * <p>
 *     A Discord bot that accepts a path to config files, an instance config,
 *     and a connection to Discord through the {@link ShardManager}.
 *     The bootstrapper is responsible for creating the bot and calling each setup method,
 *     while the bot is responsible for accepting commands.
 * </p>
 * <p>
 *     The boot process is split into five stages:
 *     <ul>
 *         <li>args stage: parse command line arguments</li>
 *         <li>config stage: create, read, and verify config files,
 *         {@link #createConfigs(Path)}</li>
 *         <li>preInit stage: initialize anything that can be started before logging into Discord,
 *         {@link #preInit(BootContext)}</li>
 *         <li>init stage: log into discord and start accepting commands,
 *         {@link #init(ShardManager)}</li>
 *         <li>postInit stage: any tasks that can be initialized after accepting commands,
 *         {@link #postInit()}</li>
 *     </ul>
 *     Only the bootstrapper processes the args stage.
 * </p>
 * <p>
 *     Note that <b>the bootstrapper only runs the next stage once the previous setup method returns</b>.
 *     Consider making long processes asynchronous if possible.
 * </p>
 */
@RequiredArgsConstructor
public abstract class Bot {

    private final @NonNull BootstrapHook hook;

    /**
     * Reloads the bot by creating a new bot, moving over the listeners, and shutting down the current one.
     * This bot should not be used after calling reload().
     */
    public void reload() {
        hook.reload();
    }
    /**
     * Shuts down the bot.
     * This bot should not be used after calling reload().
     */
    public void shutdown() {
        hook.shutdown();
        onShutdown();
    }

    /**
     * Create and read the bot-specific config files.
     * Called after the instance config has been read.
     * @param path the path to the folder containing the config files
     * @return an exit code, will continue to the next stage if 0, otherwise exit
     */
    public abstract int createConfigs(@NonNull Path path);

    /**
     * Initialize anything that can be started before logging into Discord.
     * Called right before logging into Discord.
     * The login process can take a while, so it is best to start some long processes (such as connecting to a database)
     * here and wait for them to finish in the init stage.
     * @param context contains data used to start the bot such as the shard count and http client configuration
     * @return an exit code, will continue to the next stage if 0, otherwise exit
     */
    public abstract int preInit(@NonNull BootContext context);

    /**
     * Initialize the bot and start accepting commands.
     * Called after logging into Discord.
     * @param shardManager an entry point for the Discord API library
     * @return an exit code, will exit if not 0
     */
    public abstract int init(@NonNull ShardManager shardManager);

    /**
     * Start any tasks that can be initialized after accepting commands.
     * Called after the init stage finishes.
     */
    public abstract void postInit();

    /**
     * Closes any resources that need to be closed, such as databases or threads.
     * Called when the bot is shutting down, either because {@link #shutdown()} was called or the old bot is
     * being shut down after calling {@link #reload()}.
     */
    public abstract void onShutdown();

}
