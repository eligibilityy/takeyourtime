package me.yiliya.takeYourTime.manager;

import me.yiliya.takeYourTime.TakeYourTime;
import me.yiliya.takeYourTime.commands.ClockSettings;
import me.yiliya.takeYourTime.utils.TimeUtils;
import me.yiliya.takeYourTime.utils.VersionSupport;
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
    private final Map<UUID, BossBar> bossBars = new HashMap<>();

    public ClockManager(TakeYourTime plugin) {
        this.plugin = plugin;
    }

    public void start() {
        long interval = plugin.getConfig().getLong("update-interval", 1200L);

        new BukkitRunnable() {
            @Override
            public void run() {
                World world = Bukkit.getWorlds().getFirst();
                long ticks = world.getTime();
                long totalTime = world.getFullTime();

                String counterMode = plugin.getConfig().getString("counter-mode", "gametime");
                long days = counterMode.equalsIgnoreCase("gametime")
                        ? world.getGameTime() / 24000L
                        : totalTime / 24000L;

                String timeString = TimeUtils.parseTime(ticks) + " | Day " + days;
                double progress = TimeUtils.getDayProgress(ticks);

                for (Player player : Bukkit.getOnlinePlayers()) {
                    ClockSettings settings = plugin.getPlayerDataHandler().getSettings(player.getUniqueId());

                    if (!settings.enabled()) {
                        removeBossBar(player);
                        continue;
                    }

                    if (settings.mode().equalsIgnoreCase("bossbar")) {
                        BarColor barColor = TimeUtils.getColorByPhase(plugin, ticks);
                        BossBar bar = bossBars.computeIfAbsent(player.getUniqueId(),
                                k -> Bukkit.createBossBar(timeString, barColor, BarStyle.SOLID));
                        bar.setTitle(timeString);
                        bar.setProgress(progress);

                        if (bar.getColor() != barColor) bar.setColor(barColor);
                        if (!bar.getPlayers().contains(player)) bar.addPlayer(player);

                    } else {
                        VersionSupport.sendActionBar(player, timeString);
                        removeBossBar(player);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, interval);
    }

    private void removeBossBar(Player player) {
        BossBar bar = bossBars.remove(player.getUniqueId());
        if (bar != null) bar.removeAll();
    }
}
