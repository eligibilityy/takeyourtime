package me.yiliya.takeYourTime.utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class VersionSupport {

    public static void sendActionBar(Player player, String message) {
        try {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
        } catch (NoSuchMethodError e) {
            player.sendMessage(message); // fallback for old versions
        }
    }
}
