package me.solarlego.solarmain.commands;

import me.solarlego.solarmain.SolarMain;
import me.solarlego.solarmain.Stats;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;


public class CommandRank implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 2 || Bukkit.getPlayerExact(args[0]) == null) {
            return false;
        } else {
            FileConfiguration configFile = SolarMain.getPlugin().getConfigFile();
            if (!configFile.getConfigurationSection("ranks").getKeys(false).contains(args[1])) {
                StringBuilder validRanks = new StringBuilder();
                for (String key : configFile.getConfigurationSection("ranks").getKeys(false)) {
                    validRanks.append(", ").append(key);
                }
                sender.sendMessage("\u00A7cInvalid rank! Valid ranks: " + validRanks.substring(2));
            } else {
                Player player = Bukkit.getPlayerExact(args[0]);
                String oldPrefix = Stats.get(player.getUniqueId()).getPrefix();
                Stats.updatePlayerFile(player.getUniqueId() + ".rank", args[1]);
                sender.sendMessage("\u00A7aSuccessfully changed " + args[0] + "'s rank to " + args[1] + "!");
                String newPrefix = Stats.get(player.getUniqueId()).getPrefix();
                while (!player.getPlayerListName().contains(oldPrefix)) {
                    oldPrefix = oldPrefix.substring(0, oldPrefix.length() - 1 - (String.valueOf(oldPrefix.charAt(oldPrefix.length() - 2)).equals("\u00A7") ? 1 : 0));
                    if (oldPrefix.length() == 0) {
                        break;
                    }
                }
                if (oldPrefix.length() == 0) {
                    player.setPlayerListName(newPrefix.concat(player.getPlayerListName()));
                } else {
                    player.setPlayerListName(player.getPlayerListName().replace(oldPrefix, newPrefix));
                }
            }
            return true;
        }
    }
}

