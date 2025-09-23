package me.yiliya.takeYourTime;

import me.yiliya.takeYourTime.commands.ClockCommand;
import me.yiliya.takeYourTime.commands.TakeYourTimeCommand;
import me.yiliya.takeYourTime.commands.TytTabCompleter;
import me.yiliya.takeYourTime.manager.ClockManager;
import me.yiliya.takeYourTime.manager.PlayerDataHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class TakeYourTime extends JavaPlugin {

    private PlayerDataHandler playerDataHandler;

    @Override
    public void onEnable() {
        TakeYourTime instance = this;

        saveDefaultConfig();
        updateConfigIfNeeded();

        // Load player data
        playerDataHandler = new PlayerDataHandler(instance);
        playerDataHandler.load();

        // Register commands
        Objects.requireNonNull(getCommand("clock")).setExecutor(new ClockCommand(instance));
        Objects.requireNonNull(getCommand("clock")).setTabCompleter(new TytTabCompleter());

        Objects.requireNonNull(getCommand("tyt")).setExecutor(new TakeYourTimeCommand(instance));
        Objects.requireNonNull(getCommand("tyt")).setTabCompleter(new TytTabCompleter());

        // Start clock manager
        ClockManager clockManager = new ClockManager(instance);
        clockManager.start();

        logStartup();
    }

    @Override
    public void onDisable() {
        playerDataHandler.save();
        getLogger().info("TakeYourTime is stopping...");
    }

    private void updateConfigIfNeeded() {
        int currentVersion = getConfig().getInt("config-version", 0);
        int latestVersion = 1;

        if (currentVersion < latestVersion) {
            getLogger().info("Updating config.yml to latest version...");

            // Example: add missing keys with defaults
            if (!getConfig().contains("update-interval"))
                getConfig().set("update-interval", 20L);

            if (!getConfig().contains("counter-mode"))
                getConfig().set("counter-mode", "gametime");

            if (!getConfig().contains("bossbar.defaultColor"))
                getConfig().set("bossbar.defaultColor", "WHITE");
            if (!getConfig().contains("bossbar.dawnColor"))
                getConfig().set("bossbar.dawnColor", "PINK");
            if (!getConfig().contains("bossbar.dayColor"))
                getConfig().set("bossbar.dayColor", "BLUE");
            if (!getConfig().contains("bossbar.duskColor"))
                getConfig().set("bossbar.duskColor", "RED");
            if (!getConfig().contains("bossbar.nightColor"))
                getConfig().set("bossbar.nightColor", "PURPLE");

            getConfig().set("config-version", latestVersion);

            saveConfig();
            getLogger().info("Config update complete.");
        }
    }

    private void logStartup() {
        String name = getDescription().getName();
        String version = getDescription().getVersion();

        getLogger().info(name + " is running.");
        getLogger().info("Installed version: " + version);
    }

    public PlayerDataHandler getPlayerDataHandler() {
        return playerDataHandler;
    }
}
