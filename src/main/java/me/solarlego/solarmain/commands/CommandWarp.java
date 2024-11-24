package me.solarlego.solarmain.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandWarp implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0 || Bukkit.getWorld(args[0]) == null) {
            return false;
        } else {
            if (args.length == 1 && sender instanceof Player) {
                ((Player) sender).teleport(Bukkit.getWorld(args[0]).getSpawnLocation());
                sender.sendMessage("\u00A77Sending you to " + args[0].toLowerCase() + "...");
                sender.sendMessage("\n");
            } else if (args.length > 1) {
                if (Bukkit.getPlayerExact(args[1]).isOnline()) {
                    Bukkit.getPlayer(args[1]).teleport(Bukkit.getWorld(args[0]).getSpawnLocation());
                    sender.sendMessage("\u00A77Sending " + Bukkit.getPlayer(args[1]).getName() + " to " + args[0].toLowerCase() + "...");
                } else {
                    sender.sendMessage("\u00A7cCould not find the player ' " + args[1] + "'");
                }
            } else {
                sender.sendMessage("\u00A7cYou must be a player to use this command!");
            }
            return true;
        }
    }
}
