package me.yiliya.takeYourTime.commands;

import me.yiliya.takeYourTime.TakeYourTime;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class TakeYourTimeCommand implements CommandExecutor {

    private final TakeYourTime plugin;

    public TakeYourTimeCommand(TakeYourTime plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§4TakeYourTime §f- A day and clock display plugin.");
            sender.sendMessage("§7Usage: /tyt reload");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            sender.sendMessage("§eTakeYourTime config reloaded.");
            return true;
        }

        sender.sendMessage("§cUnknown subcommand. Available subcommands: §freload");
        return true;
    }
}
