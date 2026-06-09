package io.benwong01f611.villagerenchantblacklist;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class VEBCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;

    public VEBCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // 1. Check for basic admin permission
        if (!sender.hasPermission("veb.admin")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
            return true;
        }

        // 2. Check if the sub-command is "reload"
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            // Reload the config.yml file from disk
            plugin.reloadConfig();

            // Re-map the values into your VTBConfig memory pool
            VEBConfig.load(plugin.getConfig());

            sender.sendMessage(ChatColor.GREEN + "[TradeBlacklist] Configuration successfully reloaded!");
            return true;
        }

        // Usage help if they type just /veb or incorrect arguments
        sender.sendMessage(ChatColor.YELLOW + "Usage: /veb reload");
        return true;
    }

    // Adds auto-complete suggestions when typing the command in-game
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1 && sender.hasPermission("veb.admin")) {
            suggestions.add("reload");
        }
        return suggestions;
    }
}