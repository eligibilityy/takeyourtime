package me.yiliya.takeYourTime.commands;

import me.yiliya.takeYourTime.TakeYourTime;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClockCommand implements CommandExecutor {
    private final TakeYourTime plugin;

    public ClockCommand(TakeYourTime plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§eUsage: /clock <on|off|mode>");
            return true;
        }

        var data = plugin.getPlayerDataHandler();
        var current = data.getSettings(player.getUniqueId());

        switch (args[0].toLowerCase()) {
            case "on" -> {
                data.setSettings(player.getUniqueId(), new ClockSettings(true, current.mode()));
                player.sendMessage("§aClock enabled!");
            }
            case "off" -> {
                data.setSettings(player.getUniqueId(), new ClockSettings(false, current.mode()));
                player.sendMessage("§cClock disabled!");
            }
            case "mode" -> {
                if (args.length < 2) {
                    player.sendMessage("§eUsage: /clock mode <actionbar|bossbar>");
                    return true;
                }
                String mode = args[1].toLowerCase();
                if (!mode.equals("actionbar") && !mode.equals("bossbar")) {
                    player.sendMessage("§cInvalid mode! Use actionbar or bossbar.");
                    return true;
                }
                data.setSettings(player.getUniqueId(), new ClockSettings(current.enabled(), mode));
                player.sendMessage("§aClock mode set to " + mode + "!");
            }
            default -> player.sendMessage("§eUsage: /clock <on|off|mode>");
        }

        plugin.getPlayerDataHandler().saveAll(); // save immediately
        return true;
    }
}
