package me.yiliya.takeYourTime;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClockManager {
    private final TakeYourTime plugin;
    private final Map<UUID, BossBar> bossBarMap = new HashMap<>();

    public ClockManager(TakeYourTime plugin) {
        this.plugin = plugin;
    }

    public void start() {
        long updateInterval = plugin.getConfig().getLong("update-interval", 20L);

        new BukkitRunnable() {
            @Override
            public void run() {
                World world = Bukkit.getWorlds().getFirst();
                long time = world.getTime();

                long days;
                String mode = plugin.getConfig().getString("counter-mode", "gametime");

                if (mode.equalsIgnoreCase("gametime")) {
                    days = world.getGameTime() / 24000L;
                } else {
                    days = world.getFullTime() / 24000L;
                }

                String timeString = parseTime(time) + " | Day " + days;

                double progress = (time % 24000L) / 24000.0;

                // Get color using gradual phase steps
                BarColor barColor = getBossBarColor(time);

                for (Player player : Bukkit.getOnlinePlayers()) {
                    ClockSettings settings = plugin.getPlayerDataHandler().getSettings(player.getUniqueId());

                    if (!settings.enabled()) {
                        removeBossBar(player);
                        continue;
                    }

                    if (settings.mode().equalsIgnoreCase("bossbar")) {
                        BossBar bar = bossBarMap.computeIfAbsent(player.getUniqueId(),
                                k -> Bukkit.createBossBar("", barColor, BarStyle.SOLID));
                        bar.setTitle(timeString);
                        bar.setProgress(progress);

                        // Update color dynamically if it changed
                        if (bar.getColor() != barColor) {
                            bar.setColor(barColor);
                        }

                        if (!bar.getPlayers().contains(player)) {
                            bar.addPlayer(player);
                        }
                    } else {
                        player.spigot().sendMessage(
                                ChatMessageType.ACTION_BAR,
                                new TextComponent(timeString)
                        );
                        removeBossBar(player);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, updateInterval);
    }

    private void removeBossBar(Player player) {
        BossBar bar = bossBarMap.remove(player.getUniqueId());
        if (bar != null) bar.removeAll();
    }

    /**
     * Stepwise gradual bossbar color based on Minecraft ticks
     */
    private BarColor getBossBarColor(long time) {
        String defaultColorStr = plugin.getConfig().getString("bossbar.defaultColor", "WHITE").toUpperCase();
        String dawnColor = plugin.getConfig().getString("bossbar.dawnColor", "PINK").toUpperCase();
        String dayColor = plugin.getConfig().getString("bossbar.dayColor", "YELLOW").toUpperCase();
        String duskColor = plugin.getConfig().getString("bossbar.duskColor", "RED").toUpperCase();
        String nightColor = plugin.getConfig().getString("bossbar.nightColor", "PURPLE").toUpperCase();

        try {
            if (time < 1000) {
                return time < 500 ? BarColor.valueOf(dawnColor) : BarColor.YELLOW;
            } else if (time < 12000) {
                // Day: early = BLUE, mid = GREEN, late = YELLOW
                if (time < 4000) return BarColor.valueOf(dayColor);
                else if (time < 8000) return BarColor.YELLOW;
                else return BarColor.YELLOW;
            } else if (time < 13000) {
                // Dusk: early = RED, late = PURPLE
                return time < 12500 ? BarColor.valueOf(duskColor) : BarColor.PURPLE;
            } else {
                return time < 20000 ? BarColor.valueOf(nightColor) : BarColor.BLUE;
            }
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid bossbar color in config.yml, defaulting to WHITE");
            return BarColor.WHITE;
        }
    }

    /**
     * Converts Minecraft time (0–23999 ticks) into 12-hour format
     */
    public static String parseTime(long time) {
        long hours = time / 1000 + 6;
        long minutes = (time % 1000) * 60 / 1000;
        String ampm = "AM";

        if (hours >= 12) {
            ampm = "PM";
            hours -= 12;
        }
        if (hours >= 12) {
            ampm = "AM";
            hours -= 12;
        }
        if (hours == 0) hours = 12;

        String mm = "0" + minutes;
        mm = mm.substring(mm.length() - 2);

        return hours + ":" + mm + " " + ampm;
    }
}
