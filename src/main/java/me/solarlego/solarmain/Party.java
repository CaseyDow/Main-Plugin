package me.solarlego.solarmain;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.Player;
import org.spigotmc.SpigotConfig;

import java.util.ArrayList;
import java.util.UUID;

public class Party {

    private static final ArrayList<Party> parties = new ArrayList<>();
    private final ArrayList<UUID> players;
    private final ArrayList<UUID> invited;
    private UUID leader;

    public Party(Player player) {
        parties.add(this);
        players = new ArrayList<>();
        invited = new ArrayList<>();
        leader = player.getUniqueId();
        players.add(leader);
    }

    public static Party getParty(OfflinePlayer player) {
        for (Party party : parties) {
            for (UUID p : party.players) {
                if (p.equals(player.getUniqueId())) {
                    return party;
                }
            }
        }
        return null;
    }

    public void invite(Player player, Player target) {
        if (invited.contains(target.getUniqueId())) {
            player.sendMessage("\u00A7cThis player has already been invited to the party!");
            return;
        }
        Stats tInfo = Stats.get(leader);
        ComponentBuilder tMsg = new ComponentBuilder("");
        tMsg = tMsg.append("\u00A76\u00A7m-----------------------------------\n");
        tMsg = tMsg.append(tInfo.getPrefix() + tInfo.getName() + " \u00A7einvited you to join their party!\n");
        tMsg = tMsg.append("  \u00A76Click Here to join.\n");
        BaseComponent[] msg = tMsg.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept " + Bukkit.getPlayer(leader).getName())).append("\u00A76\u00A7m-----------------------------------").create();
        target.spigot().sendMessage(msg);
        Stats sInfo = Stats.get(target.getUniqueId());
        String sMsg = "\u00A76\u00A7m-----------------------------------\n";
        sMsg = sMsg.concat(sInfo.getPrefix() + sInfo.getName() + " \u00A7ewas invited to join the party!\n");
        sMsg = sMsg.concat("\u00A76\u00A7m-----------------------------------");
        for (UUID p : players) {
            if (Bukkit.getPlayer(p) != null) {
                Bukkit.getPlayer(p).sendMessage(sMsg);
            }
        }
        invited.add(target.getUniqueId());
    }

    public void accept(Player sender) {
        if (!invited.contains(sender.getUniqueId())) {
            sender.sendMessage("\u00A7cYou do not have an invite to this party!");
            return;
        }
        players.add(sender.getUniqueId());
        invited.remove(sender.getUniqueId());
        Stats pInfo = Stats.get(sender.getUniqueId());
        String msg = "\u00A76\u00A7m-----------------------------------\n";
        msg = msg.concat(pInfo.getPrefix() + pInfo.getName() + " \u00A7ejoined the party!\n");
        msg = msg.concat("\u00A76\u00A7m-----------------------------------");
        for (UUID p : players) {
            if (Bukkit.getPlayer(p) != null) {
                Bukkit.getPlayer(p).sendMessage(msg);
            }
        }
    }

    public String list() {
        String msg = "\u00A76\u00A7m-----------------------------------\n";
        Stats lInfo = Stats.get(leader);
        msg = msg.concat("\u00A78Leader: " + lInfo.getPrefix() + lInfo.getName() + "\n");
        int i = 0;
        for (UUID player : players) {
            if (player.equals(leader)) {
                continue;
            }
            if (i == 0) {
                msg = msg.concat("\u00A78Players: ");
            } else if (i % 3 == 0) {
                msg = msg.concat("\n  ");
            }
            Stats pInfo = Stats.get(player);
            msg = msg.concat(pInfo.getPrefix() + pInfo.getName() + ", ");
            i++;
        }
        if (i > 0) {
            msg = msg.substring(0, msg.length() - 2);
        }
        return msg.concat("\n\u00A76\u00A7m-----------------------------------");
    }

    public void warp() {
        for (UUID player : players) {
            if (!player.equals(leader)) {
                Player p = Bukkit.getPlayer(player);
                if (p == null) {
                    continue;
                }
                if (p.getWorld() != Bukkit.getPlayer(leader).getWorld()) {
                    p.teleport(Bukkit.getPlayer(leader).getWorld().getSpawnLocation());
                }
                String msg = "\u00A76\u00A7m-----------------------------------\n";
                msg = msg.concat("\u00A7eThe party leader warped you to their lobby.\n");
                msg = msg.concat("\u00A76\u00A7m-----------------------------------");
                p.sendMessage(msg);
            } else {
                String msg = "\u00A76\u00A7m-----------------------------------\n";
                msg = msg.concat("\u00A7eYou warped the party to your lobby.\n");
                msg = msg.concat("\u00A76\u00A7m-----------------------------------");
                Bukkit.getPlayer(leader).sendMessage(msg);
            }
        }
    }

    public void leave(Player player) {
        players.remove(player.getUniqueId());
        String lMsg = "\u00A76\u00A7m-----------------------------------\n";
        lMsg = lMsg.concat("\u00A7eYou left the party.\n");
        lMsg = lMsg.concat("\u00A76\u00A7m-----------------------------------");
        player.sendMessage(lMsg);
        Stats pInfo = Stats.get(player.getUniqueId());
        String pMsg = "\u00A76\u00A7m-----------------------------------\n";
        pMsg = pMsg.concat(pInfo.getPrefix() + pInfo.getName() + " \u00A7eleft the party.\n");
        pMsg = pMsg.concat("\u00A76\u00A7m-----------------------------------");
        for (UUID p : players) {
            if (Bukkit.getPlayer(p) != null) {
                Bukkit.getPlayer(p).sendMessage(pMsg);
            }
        }
        if (player.getUniqueId().equals(leader)) {
            if (players.size() > 0) {
                for (UUID uuid : players) {
                    leader = uuid;
                    if (Bukkit.getPlayer(leader) != null) {
                        break;
                    }
                }
                Stats info = Stats.get(leader);
                String msg = "\u00A76\u00A7m-----------------------------------\n";
                msg = msg.concat("\u00A7eThe party was transfered to " + info.getPrefix() + info.getName() + " \u00A7ebecause the leader left.\n");
                msg = msg.concat("\u00A76\u00A7m-----------------------------------");
                for (UUID p : players) {
                    if (Bukkit.getPlayer(p) != null) {
                        Bukkit.getPlayer(p).sendMessage(msg);
                    }
                }
            } else {
                parties.remove(this);
            }
        }
    }

    public void remove(OfflinePlayer player) {
        players.remove(player.getUniqueId());
        if (player.isOnline()) {
            String lMsg = "\u00A76\u00A7m-----------------------------------\n";
            lMsg = lMsg.concat("\u00A7eYou were removed from the party.\n");
            lMsg = lMsg.concat("\u00A76\u00A7m-----------------------------------");
            ((Player) player).sendMessage(lMsg);
        }
        Stats pInfo = Stats.get(player.getUniqueId());
        String pMsg = "\u00A76\u00A7m-----------------------------------\n";
        pMsg = pMsg.concat(pInfo.getPrefix() + pInfo.getName() + " \u00A7ewas removed from the party.\n");
        pMsg = pMsg.concat("\u00A76\u00A7m-----------------------------------");
        for (UUID p : players) {
            if (Bukkit.getPlayer(p) != null) {
                Bukkit.getPlayer(p).sendMessage(pMsg);
            }
        }
    }

    public void disband() {
        parties.remove(this);
        String msg = "\u00A76\u00A7m-----------------------------------\n";
        msg = msg.concat("\u00A7eThe party leader disbanded the party!\n");
        msg = msg.concat("\u00A76\u00A7m-----------------------------------");
        for (UUID player : players) {
            if (Bukkit.getPlayer(player) != null) {
                Bukkit.getPlayer(player).sendMessage(msg);
            }
        }
    }

    public void transfer(OfflinePlayer player) {
         if (!hasPlayer(player)) {
            return;
        }
        Stats oInfo = Stats.get(leader);
        leader = player.getUniqueId();
        Stats info = Stats.get(player.getUniqueId());
        String msg = "\u00A76\u00A7m-----------------------------------\n";
        msg = msg.concat("\u00A7eThe party was transfered to " + info.getPrefix() + info.getName() + " \u00A7eby " + oInfo.getPrefix() + oInfo.getName());
        msg = msg.concat(".\n\u00A76\u00A7m-----------------------------------");
        for (UUID p : players) {
            if (Bukkit.getPlayer(p) != null) {
                Bukkit.getPlayer(p).sendMessage(msg);
            }
        }
    }

    public Player getLeader() {
        return Bukkit.getPlayer(leader);
    }

    public boolean hasPlayer(OfflinePlayer player) {
        return players.contains(player.getUniqueId());
    }

    public ArrayList<Player> getPlayers() {
        ArrayList<Player> realPlayers = new ArrayList<>();
        for (UUID player : players) {
            if (Bukkit.getPlayer(player) != null) {
                realPlayers.add(Bukkit.getPlayer(player));
            }
        }
        return realPlayers;
    }

    public static OfflinePlayer getOfflinePlayer(String name) {
        OfflinePlayer result = Bukkit.getPlayerExact(name);
        if (result == null) {
            GameProfile profile = null;
            if (MinecraftServer.getServer().getOnlineMode() || SpigotConfig.bungee) {
                profile = MinecraftServer.getServer().getUserCache().getProfile(name);
            }

            if (profile == null) {
                result = ((CraftServer) Bukkit.getServer()).getOfflinePlayer(new GameProfile(UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8)), name));
            } else {
                result = ((CraftServer) Bukkit.getServer()).getOfflinePlayer(profile);
            }
        }
        return result;
    }

}
