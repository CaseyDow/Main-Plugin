package me.solarlego.solarmain.commands;

import me.solarlego.solarmain.Stats;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandMessage implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length == 0) {
                return false;
            } else {
                Player target = Bukkit.getPlayerExact(args[0]);
                if (target == null) {
                    sender.sendMessage("\u00A7cThat player is not online!");
                } else {
                    Stats sInfo = Stats.get(((Player) sender).getUniqueId());
                    Stats tInfo = Stats.get(target.getUniqueId());
                    if (args.length > 2) {
                        String msg = "";
                        for (String arg : args) {
                            msg = msg.concat(arg + " ");
                        }
                        target.sendMessage("\u00A7dFrom " + sInfo.getPrefix() + sInfo.getName() + "\u00A7f: \u00A77" + msg);
                        sender.sendMessage("\u00A7dTo " + tInfo.getPrefix() + tInfo.getName() + "\u00A7f: \u00A77" + msg);
                        target.playSound(target.getLocation(), Sound.SUCCESSFUL_HIT, 1, 1);
                    } else {
                        CommandChat.setChat((Player) sender, "msg-" + target.getName());
                        sender.sendMessage("\u00A7aOpened a conversation with " + tInfo.getPrefix() + tInfo.getName() + "\u00A7a. Use \u00A7b/chat a \u00A7ato leave.");
                    }
                }
            }
        }
        return true;
    }

}
