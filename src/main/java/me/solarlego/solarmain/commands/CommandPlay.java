package me.solarlego.solarmain.commands;

import me.solarlego.solarmain.gui.PlayGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandPlay implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length == 0) {
                new PlayGUI(((Player) sender));
            } else {
                return false;
            }
        } else {
            sender.sendMessage("\u00A7cYou must be a player to use this command!");
        }
        return true;
    }

}
