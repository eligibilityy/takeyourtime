package me.yiliya.takeYourTime.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TytTabCompleter implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (command.getName().equalsIgnoreCase("clock")) {
            if (args.length == 1) {
                suggestions.add("on");
                suggestions.add("off");
                suggestions.add("mode");
            } else if (args.length == 2 && args[0].equalsIgnoreCase("mode")) {
                suggestions.add("actionbar");
                suggestions.add("bossbar");
            }
        }

        if (command.getName().equalsIgnoreCase("tyt")) {
            if (args.length == 1) {
                if ("reload".startsWith(args[0].toLowerCase())) {
                    suggestions.add("reload");
                }
            }
        }

        return suggestions.isEmpty() ? Collections.emptyList() : suggestions;
    }
}
