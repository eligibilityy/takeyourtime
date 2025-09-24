package me.yiliya.takeYourTime;

import me.yiliya.takeYourTime.commands.ClockCommand;
import me.yiliya.takeYourTime.commands.TakeYourTimeCommand;
import me.yiliya.takeYourTime.commands.TytTabCompleter;
import me.yiliya.takeYourTime.manager.ClockManager;
import me.yiliya.takeYourTime.manager.PlayerDataHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class TakeYourTime extends JavaPlugin {

    private PlayerDataHandler playerDataHandler;
    private ClockManager clockManager;

    @Override
    public void onEnable() {

        // Version check
        String version = Bukkit.getBukkitVersion(); // e.g., "1.20.1-R0.1-SNAPSHOT"
        if (!version.startsWith("1.20") && !version.startsWith("1.21")) {
            getLogger().severe("TakeYourTime requires at least Minecraft 1.20.1!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        saveDefaultConfig();
        updateConfigIfNeeded();

        // Initialize PlayerDataHandler (per-player async files)
        playerDataHandler = new PlayerDataHandler(this);
        playerDataHandler.loadAll(); // load all players asynchronously

        // Register commands
        Objects.requireNonNull(getCommand("clock")).setExecutor(new ClockCommand(this));
        Objects.requireNonNull(getCommand("clock")).setTabCompleter(new TytTabCompleter());

        Objects.requireNonNull(getCommand("tyt")).setExecutor(new TakeYourTimeCommand(this));
        Objects.requireNonNull(getCommand("tyt")).setTabCompleter(new TytTabCompleter());

        // Start clock manager
        clockManager = new ClockManager(this);
        clockManager.start();

        logStartup();
    }

    @Override
    public void onDisable() {
        if (playerDataHandler != null) {
            playerDataHandler.saveAll(); // save all player files asynchronously
        }
        getLogger().info("TakeYourTime is stopping...");
    }

    private void updateConfigIfNeeded() {
        int currentVersion = getConfig().getInt("config-version", 0);
        int latestVersion = 1;

        if (currentVersion < latestVersion) {
            getLogger().info("Updating config.yml to latest version...");

            // Add missing keys with defaults
            getConfig().addDefault("update-interval", 20L);
            getConfig().addDefault("counter-mode", "gametime");
            getConfig().addDefault("bossbar.defaultColor", "WHITE");
            getConfig().addDefault("bossbar.dawnColor", "PINK");
            getConfig().addDefault("bossbar.dayColor", "BLUE");
            getConfig().addDefault("bossbar.duskColor", "RED");
            getConfig().addDefault("bossbar.nightColor", "PURPLE");

            getConfig().set("config-version", latestVersion);
            saveConfig();
            getLogger().info("Config update complete.");
        }
    }

    private void logStartup() {
        String name = getDescription().getName();
        String version = getDescription().getVersion();

        getLogger().info(name + " v" + version + " is running.");
    }

    public PlayerDataHandler getPlayerDataHandler() {
        return playerDataHandler;
    }

    public ClockManager getClockManager() {
        return clockManager;
    }
}
