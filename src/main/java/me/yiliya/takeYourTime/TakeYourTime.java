package me.yiliya.takeYourTime;

import org.bukkit.plugin.java.JavaPlugin;

public final class TakeYourTime extends JavaPlugin {

    private static TakeYourTime instance;
    private PlayerDataHandler playerDataHandler;
    private ClockManager clockManager;

    @Override
    public void onEnable() {
        instance = this;

        // Load player data
        playerDataHandler = new PlayerDataHandler(this);
        playerDataHandler.load();

        // Register commands
        if (getCommand("clock") != null) {
            getCommand("clock").setExecutor(new ClockCommand(this));
        } else {
            getLogger().severe("Command 'clock' is missing in plugin.yml!");
        }

        if (getCommand("takeyourtime") != null) {
            getCommand("takeyourtime").setExecutor(new TakeYourTimeCommand(this));
        } else {
            getLogger().severe("Command 'takeyourtime' is missing in plugin.yml!");
        }

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
