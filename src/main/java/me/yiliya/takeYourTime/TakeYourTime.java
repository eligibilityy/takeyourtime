package me.yiliya.takeYourTime;

import org.bukkit.plugin.java.JavaPlugin;

public final class TakeYourTime extends JavaPlugin {

    private static TakeYourTime instance;
    private PlayerDataHandler playerDataHandler;
    private ClockManager clockManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        updateConfigIfNeeded();

        // Load player data
        playerDataHandler = new PlayerDataHandler(this);
        playerDataHandler.load();

        // Register commands
        getCommand("clock").setExecutor(new ClockCommand(this));
        getCommand("clock").setTabCompleter(new TytTabCompleter());

        getCommand("tyt").setExecutor(new TakeYourTimeCommand(this));
        getCommand("tyt").setTabCompleter(new TytTabCompleter());

        // Start clock manager
        clockManager = new ClockManager(this);
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
