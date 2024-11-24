package me.solarlego.solarmain.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CommandChat implements CommandExecutor {

    private static final Map<String, String> chat = new HashMap<>();

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            if (!chat.containsKey(sender.getName())) {
                chat.put(sender.getName(), "all");
            }
            if (args.length == 0) {
                sender.sendMessage("\u00A7cInvalid Usage! Valid chats: all, world, party, team.");
            } else if (args[0].equals("all") || args[0].equals("a")) {
                chat.replace(sender.getName(), "all");
                sender.sendMessage("\u00A7aYou are now in the \u00A76ALL \u00A7achannel.");
            } else if (args[0].equals("world") || args[0].equals("w")) {
                chat.replace(sender.getName(), "world");
                sender.sendMessage("\u00A7aYou are now in the \u00A76WORLD \u00A7achannel.");
            } else if (args[0].equals("party") || args[0].equals("p")) {
                chat.replace(sender.getName(), "party");
                sender.sendMessage("\u00A7aYou are now in the \u00A76PARTY \u00A7achannel.");
            } else if (args[0].equals("team") || args[0].equals("t")) {
                chat.replace(sender.getName(), "team");
                sender.sendMessage("\u00A7aYou are now in the \u00A76TEAM \u00A7achannel.");
            } else {
                sender.sendMessage("\u00A7cInvalid Usage! Valid chats: all, world, party, team.");
            }
        } else {
            sender.sendMessage("\u00A7cYou must be a player to use this command!");
        }
        return true;
    }

    public static String getChat(Player player) {
        return chat.getOrDefault(player.getName(), "all");
    }

    public static void setChat(Player player, String channel) {
        if (chat.containsKey(player.getName())) {
            chat.replace(player.getName(), channel);
        } else {
            chat.put(player.getName(), channel);
        }
    }

}
