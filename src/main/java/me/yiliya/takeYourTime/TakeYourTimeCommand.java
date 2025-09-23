package me.yiliya.takeYourTime;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TakeYourTimeCommand implements CommandExecutor {

    private final TakeYourTime plugin;

    public TakeYourTimeCommand(TakeYourTime plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§aTakeYourTime §f- A day and clock display plugin.");
            sender.sendMessage("§7Usage: /tyt reload");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            sender.sendMessage("§aTakeYourTime config reloaded.");
            return true;
        }

        sender.sendMessage("§cUnknown subcommand. Use /tyt reload");
        return true;
    }
}
