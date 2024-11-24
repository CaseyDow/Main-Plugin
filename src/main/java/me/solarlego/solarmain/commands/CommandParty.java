package me.solarlego.solarmain.commands;

import me.solarlego.solarmain.Party;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandParty implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        for (int i = 0; i < args.length; i++) {
            args[i] = args[i].toLowerCase();
        }
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 0 || args[0].equals("help")) {
                help(p);
            } else if (args[0].equals("list")) {
                list(p);
            } else if (args[0].equals("warp")) {
                warp(p);
            } else if (args[0].equals("disband")) {
                disband(p);
            } else if (args.length > 1 && args[0].equals("accept")) {
                accept(p, args[1]);
            } else if (args.length > 1 && (args[0].equals("remove") || args[0].equals("kick"))) {
                remove(p, args[1]);
            } else if (args[0].equals("leave")) {
                leave(p);
            } else if (args.length > 1 && args[0].equals("transfer")) {
                transfer(p, args[1]);
            } else {
                if (args.length > 1 && args[0].equals("invite")) {
                    invite(p, args[1]);
                } else {
                    invite(p, args[0]);
                }
            }
        } else {
            sender.sendMessage("\u00A7cYou must be a player to use this command!");
        }
        return true;
    }

    private void help(Player player) {
        String msg = "\u00A76\u00A7m-----------------------------------\n";
        msg = msg.concat("\u00A7e/party help: \u00A7fDisplays this help menu.\n");
        msg = msg.concat("\u00A7e/party invite <player>: \u00A7fInvites another player to your party.\n");
        msg = msg.concat("\u00A7e/party accept <player>: \u00A7fAccepts an invite to another player's party.\n");
        msg = msg.concat("\u00A7e/party transfer <player>: \u00A7fTransfers leadership of the party to another player.\n");
        msg = msg.concat("\u00A7e/party remove <player>: \u00A7fRemoves a player from your party.\n");
        msg = msg.concat("\u00A7e/party list: \u00A7fLists the players currently in your party.\n");
        msg = msg.concat("\u00A7e/party disband: \u00A7fDisbands the party.\n");
        msg = msg.concat("\u00A76\u00A7m-----------------------------------");
        player.sendMessage(msg);
    }

    private void list(Player player) {
        Party party = Party.getParty(player);
        if (party == null) {
            player.sendMessage("\u00A7cYou are not currently in a party.");
        } else {
            player.sendMessage(party.list());
        }
    }

    private void warp(Player player) {
        Party party = Party.getParty(player);
        if (party == null) {
            player.sendMessage("\u00A7cYou are not currently in a party.");
        } else {
            if (party.getLeader() == player) {
                party.warp();
            } else {
                player.sendMessage("\u00A7cYou are not the leader of this party.");
            }
        }
    }

    private void disband(Player player) {
        Party party = Party.getParty(player);
        if (party == null) {
            player.sendMessage("\u00A7cYou are not currently in a party.");
        } else {
            if (party.getLeader() == player) {
                party.disband();
            } else {
                player.sendMessage("\u00A7cYou are not the leader of this party.");
            }
        }
    }

    private void accept(Player player, String target) {
        Party party = Party.getParty(player);
        if (party == null) {
            Party newParty = Party.getParty(Bukkit.getPlayerExact(target));
            if (newParty == null) {
                player.sendMessage("\u00A7cThis party has been disbanded.");
            } else {
                newParty.accept(player);
            }
        } else {
            player.sendMessage("\u00A7cYou are already in a party! Leave it to join a new one.");
        }
    }

    private void remove(Player player, String target) {
        Party party = Party.getParty(player);
        if (party == null) {
            player.sendMessage("\u00A7cYou are not currently in a party.");
        } else {
            OfflinePlayer p = Party.getOfflinePlayer(target);
            if (party.getLeader() != player) {
                player.sendMessage("\u00A7cYou are not the leader of this party.");
            } else if (!party.hasPlayer(p)) {
                player.sendMessage("\u00A7cThis player is not in your party!");
            } else if (player.getUniqueId() == Party.getOfflinePlayer(target).getUniqueId()) {
                player.sendMessage("\u00A7cYou can not remove yourself from the party!");
            } else {
                party.remove(p);
            }
        }
    }

    private void invite(Player player, String target) {
        Player p = Bukkit.getPlayerExact(target);
        if (p == null) {
            player.sendMessage("\u00A7cThis player is not online!");
        } else if (p == player) {
            player.sendMessage("\u00A7cYou can not invite yourself to a party!");
        } else {
            Party party = Party.getParty(player);
            if (party == null) {
                party = new Party(player);
            } else if (party.getLeader() != player) {
                player.sendMessage("\u00A7cYou can not invite players to this party!");
                return;
            } else if (party.hasPlayer(p)) {
                player.sendMessage("\u00A7cThis player is already in your party!");
                return;
            }
            party.invite(player, p);
        }
    }

    private void leave(Player player) {
        Party party = Party.getParty(player);
        if (party == null) {
            player.sendMessage("\u00A7cYou are not currently in a party.");
        } else {
            party.leave(player);
        }
    }

    private void transfer(Player player, String target) {
        OfflinePlayer p = Party.getOfflinePlayer(target);
        if (p == player) {
            player.sendMessage("\u00A7cYou can not transfer the party to yourself!");
        } else {
            Party party = Party.getParty(player);
            if (party == null) {
                player.sendMessage("\u00A7cYou are not in a party!");
                return;
            } else if (party.getLeader() != player) {
                player.sendMessage("\u00A7cYou are not leader of this party!");
                return;
            } else if (!party.hasPlayer(p)) {
                player.sendMessage("\u00A7cThis player is not in your party!");
                return;
            }
            party.transfer(p);
        }
    }

}
