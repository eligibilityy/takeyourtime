package me.yiliya.takeYourTime;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class TytTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (command.getName().equalsIgnoreCase("clock")) {
            if (args.length == 1) {
                suggestions.add("on");
                suggestions.add("off");
                suggestions.add("info");
            } else if (args.length == 2 && args[0].equalsIgnoreCase("mode")) {
                suggestions.add("actionbar");
                suggestions.add("bossbar");
            }
        }

        return suggestions;
    }
}
