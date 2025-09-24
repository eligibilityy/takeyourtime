package me.yiliya.takeYourTime.manager;

import me.yiliya.takeYourTime.TakeYourTime;
import me.yiliya.takeYourTime.commands.ClockSettings;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataHandler {
    private final TakeYourTime plugin;
    private final File dataFolder;
    private final Map<UUID, ClockSettings> playerSettings = new ConcurrentHashMap<>();

    public PlayerDataHandler(TakeYourTime plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            plugin.getLogger().severe("Could not create playerdata folder!");
        }
    }

    /** Load all player data at startup (optional; can skip to load on login) */
    public void loadAll() {
        File[] files = dataFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            try {
                UUID uuid = UUID.fromString(file.getName().replace(".yml", ""));
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                boolean enabled = config.getBoolean("enabled", false);
                String mode = config.getString("mode", "actionbar");
                playerSettings.put(uuid, new ClockSettings(enabled, mode));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid player file: " + file.getName());
            }
        }

        plugin.getLogger().info("[TakeYourTime] Loaded " + playerSettings.size() + " player files.");
    }

    /** Load a single player's data */
    public void loadPlayer(UUID uuid) {
        File file = getPlayerFile(uuid);
        if (!file.exists()) return;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                boolean enabled = config.getBoolean("enabled", false);
                String mode = config.getString("mode", "actionbar");
                playerSettings.put(uuid, new ClockSettings(enabled, mode));
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load player data for " + uuid);
            }
        });
    }

    /** Save all player data asynchronously */
    public void saveAll() {
        for (UUID uuid : playerSettings.keySet()) {
            savePlayer(uuid);
        }
    }

    /** Save a single player's data asynchronously */
    public void savePlayer(UUID uuid) {
        ClockSettings settings = playerSettings.get(uuid);
        if (settings == null) return;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            File file = getPlayerFile(uuid);
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            config.set("enabled", settings.enabled());
            config.set("mode", settings.mode());

            try {
                config.save(file);
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to save player data for " + uuid);
            }
        });
    }

    /** Get settings, defaulting to disabled + actionbar */
    public ClockSettings getSettings(UUID uuid) {
        return playerSettings.getOrDefault(uuid, new ClockSettings(false, "actionbar"));
    }

    public void setSettings(UUID uuid, ClockSettings settings) {
        playerSettings.put(uuid, settings);
        savePlayer(uuid); // save automatically
    }

    /** Helper to get the file path for a player */
    private File getPlayerFile(UUID uuid) {
        return new File(dataFolder, uuid.toString() + ".yml");
    }
}
