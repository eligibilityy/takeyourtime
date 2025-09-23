package me.yiliya.takeYourTime;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

public class TakeYourTimeCommand implements CommandExecutor {

    private final TakeYourTime plugin;

    public TakeYourTimeCommand(TakeYourTime plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String name = plugin.getDescription().getName();
        String version = plugin.getDescription().getVersion();
        String author = String.join(", ", plugin.getDescription().getAuthors());

        sender.sendMessage(ChatColor.GOLD + "==== " + name + " ====");
        sender.sendMessage(ChatColor.YELLOW + "Version: " + ChatColor.WHITE + version);
        sender.sendMessage(ChatColor.YELLOW + "Author(s): " + ChatColor.WHITE + author);
        sender.sendMessage(ChatColor.YELLOW + "Description: " + ChatColor.WHITE + plugin.getDescription().getDescription());
        sender.sendMessage(ChatColor.GOLD + "====================");

        return true;
    }
}
