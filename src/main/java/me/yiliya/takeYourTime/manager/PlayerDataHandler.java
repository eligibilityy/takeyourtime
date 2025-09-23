package me.yiliya.takeYourTime.manager;

import me.yiliya.takeYourTime.commands.ClockSettings;
import me.yiliya.takeYourTime.TakeYourTime;
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
            File parent = file.getParentFile();
            if (!parent.exists() && !parent.mkdirs()) {
                plugin.getLogger().severe("Failed to create plugin data folder: " + parent.getPath());
            }

            try {
                if (!file.createNewFile()) {
                    plugin.getLogger().severe("Failed to create playerdata.yml file!");
                }
            } catch (IOException e) {
                plugin.getLogger().severe("Error creating playerdata.yml: " + e.getMessage());
            }
        }

        config = YamlConfiguration.loadConfiguration(file);

        for (String key : config.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                boolean enabled = config.getBoolean(key + ".enabled", false);
                String mode = config.getString(key + ".mode", "actionbar");
                playerSettings.put(uuid, new ClockSettings(enabled, mode));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid UUID found in playerdata.yml: " + key);
            }
        }

        plugin.getLogger().info("[TakeYourTime] Player data loaded.");
    }

    public void save() {
        for (Map.Entry<UUID, ClockSettings> entry : playerSettings.entrySet()) {
            UUID uuid = entry.getKey();
            ClockSettings settings = entry.getValue();
            config.set(uuid.toString() + ".enabled", settings.enabled());
            config.set(uuid + ".mode", settings.mode());
        }

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save playerdata.yml: " + e.getMessage());
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
