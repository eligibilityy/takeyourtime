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
        new BukkitRunnable() {
            @Override
            public void run() {
                World world = Bukkit.getWorlds().getFirst();

                // Correct sources:
                long dayTime = world.getTime(); // 0–23999 (current day ticks)
                long fullTime = world.getFullTime(); // total ticks since world creation
                long days = fullTime / 24000L; // 1 MC day = 24000 ticks

                String timeString = parseTime(dayTime) + " | Day " + days;

                for (Player player : Bukkit.getOnlinePlayers()) {
                    ClockSettings settings = plugin.getPlayerDataHandler().getSettings(player.getUniqueId());

                    if (!settings.enabled()) {
                        removeBossBar(player);
                        continue;
                    }

                    if (settings.mode().equalsIgnoreCase("bossbar")) {
                        BossBar bar = bossBarMap.computeIfAbsent(player.getUniqueId(),
                                k -> Bukkit.createBossBar("", BarColor.WHITE, BarStyle.SOLID));
                        bar.setTitle(timeString);
                        bar.addPlayer(player);
                    } else {
                        player.spigot().sendMessage(
                                ChatMessageType.ACTION_BAR,
                                new TextComponent(timeString)
                        );
                        removeBossBar(player);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void removeBossBar(Player player) {
        BossBar bar = bossBarMap.remove(player.getUniqueId());
        if (bar != null) {
            bar.removeAll();
        }
    }

    /**
     * Converts Minecraft time (0–23999 ticks) into a 12-hour clock format.
     */
    public static String parseTime(long time) {
        long hours = time / 1000 + 6; // MC day starts at 6:00
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
