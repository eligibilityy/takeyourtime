package me.yiliya.takeYourTime;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PlayerDataHandler {
    private final TakeYourTime plugin;
    private final File file;
    private FileConfiguration config;

    private final Map<UUID, ClockSettings> playerSettings = new HashMap<>();

    public PlayerDataHandler(TakeYourTime plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "playerdata.yml");
    }

    public void load() {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);

        for (String key : config.getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            boolean enabled = config.getBoolean(key + ".enabled", false);
            String mode = config.getString(key + ".mode", "actionbar");
            playerSettings.put(uuid, new ClockSettings(enabled, mode));
        }
        plugin.getLogger().info("[TakeYourTime] Player data loaded.");
    }

    public void save() {
        for (UUID uuid : playerSettings.keySet()) {
            ClockSettings settings = playerSettings.get(uuid);
            config.set(uuid.toString() + ".enabled", settings.enabled());
            config.set(uuid + ".mode", settings.mode());
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        plugin.getLogger().info("[TakeYourTime] Player data saved.");
    }

    public ClockSettings getSettings(UUID uuid) {
        return playerSettings.getOrDefault(uuid, new ClockSettings(false, "actionbar"));
    }

    public void setSettings(UUID uuid, ClockSettings settings) {
        playerSettings.put(uuid, settings);
    }
}
