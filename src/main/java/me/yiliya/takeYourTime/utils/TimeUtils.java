package me.yiliya.takeYourTime.utils;

import me.yiliya.takeYourTime.TakeYourTime;
import org.bukkit.boss.BarColor;

public class TimeUtils {

    public static String parseTime(long ticks) {
        long hours = (ticks / 1000 + 6) % 24; // 0–23 hours
        long minutes = (ticks % 1000) * 60 / 1000;
        String ampm = hours >= 12 ? "PM" : "AM";
        if (hours == 0) hours = 12;
        else if (hours > 12) hours -= 12;

        return String.format("%d:%02d %s", hours, minutes, ampm);
    }

    public static double getDayProgress(long ticks) {
        return (ticks % 24000L) / 24000.0;
    }

    public static BarColor getColorByPhase(TakeYourTime plugin, long time) {
        BarColor defaultColor = BarColor.WHITE;
        try {
            String defaultStr = plugin.getConfig().getString("bossbar.defaultColor", "WHITE").toUpperCase();
            if (!defaultStr.isEmpty()) defaultColor = BarColor.valueOf(defaultStr);

            String dawn = plugin.getConfig().getString("bossbar.dawnColor", "").toUpperCase();
            String day = plugin.getConfig().getString("bossbar.dayColor", "").toUpperCase();
            String dusk = plugin.getConfig().getString("bossbar.duskColor", "").toUpperCase();
            String night = plugin.getConfig().getString("bossbar.nightColor", "").toUpperCase();

            if (time < 1000 && !dawn.isEmpty()) return BarColor.valueOf(dawn);
            if (time >= 1000 && time < 12000 && !day.isEmpty()) return BarColor.valueOf(day);
            if (time >= 12000 && time < 13000 && !dusk.isEmpty()) return BarColor.valueOf(dusk);
            if (time >= 13000 && time < 24000 && !night.isEmpty()) return BarColor.valueOf(night);

        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid bossbar color in config.yml! Using default WHITE.");
        }

        return defaultColor;
    }
}
