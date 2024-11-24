package me.solarlego.solarmain;

import me.solarlego.solarmain.commands.CommandChat;
import me.solarlego.solarmain.hub.Hub;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.WorldLoadEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MainEvents implements Listener {

    private final SolarMain main;
    private final Map<Player, Player> lastMsg;

    public MainEvents(SolarMain m) {
        main = m;
        lastMsg = new HashMap<>();
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        if (Hub.hubs.size() == 0) {
            Properties pr = new Properties();
            try {
                pr.load(new FileInputStream("./server.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            new Hub(pr.getProperty("level-name"));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        FileConfiguration playersFile = YamlConfiguration.loadConfiguration(new File(main.getDataFolder(), "players.yml"));

        playersFile.addDefault(event.getPlayer().getUniqueId() + ".rank", "Default");
        playersFile.options().copyDefaults(true);
        try {
            playersFile.save(new File(main.getDataFolder(), "players.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        event.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        Hub.sendHub(event.getPlayer());
        playerVisibility(event.getPlayer());
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        event.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        playerVisibility(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerVisibility(event.getPlayer());
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (!event.getMessage().contains(" ")) {
            return;
        }
        String cmd = event.getMessage().split(" ")[0].toLowerCase();
        switch (cmd) {
            case "/ac":
            case "/achat":
                handleChat(event.getPlayer(), "all", event.getMessage().substring(event.getMessage().indexOf(" ") + 1));
                break;
            case "/wc":
            case "/wchat":
                handleChat(event.getPlayer(), "world", event.getMessage().substring(event.getMessage().indexOf(" ") + 1));
                break;
            case "/pc":
            case "/pchat":
                handleChat(event.getPlayer(), "party", event.getMessage().substring(event.getMessage().indexOf(" ") + 1));
                break;
            case "/reply":
            case "/r":
                handleChat(event.getPlayer(), "msg-" + lastMsg.get(event.getPlayer()).getName(), event.getMessage().substring(event.getMessage().indexOf(" ") + 1));
                break;
            default:
                return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        String chat = CommandChat.getChat(event.getPlayer());
        handleChat(event.getPlayer(), chat, event.getMessage());
    }

    private void handleChat(Player player, String chat, String msg) {
        Stats playerInfo = Stats.get(player.getUniqueId());
        if ("all".equals(chat)) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(playerInfo.getPrefix() + playerInfo.getName() + ": " + msg);
            }
        } else if ("world".equals(chat)) {
            for (Player p : player.getWorld().getPlayers()) {
                p.sendMessage(playerInfo.getPrefix() + playerInfo.getName() + ": " + msg);
            }
        } else if ("party".equals(chat)) {
            Party party = Party.getParty(player);
            if (party == null) {
                player.sendMessage("\u00A7cYou are not in a party.");
                CommandChat.setChat(player, "all");
            } else {
                for (Player p : party.getPlayers()) {
                    p.sendMessage("\u00A79Party > " + playerInfo.getPrefix() + playerInfo.getName() + ": " + msg);
                    if (player != p) {
                        p.playSound(p.getLocation(), Sound.SUCCESSFUL_HIT, 1, 1);
                    }
                }
            }
        } else if (chat.startsWith("msg-")) {
            Player target = Bukkit.getPlayerExact(chat.substring(4));
            Stats sInfo = Stats.get(player.getUniqueId());
            Stats tInfo = Stats.get(target.getUniqueId());
            target.sendMessage("\u00A7dFrom " + sInfo.getPrefix() + sInfo.getName() + "\u00A7f: \u00A77" + msg);
            player.sendMessage("\u00A7dTo " + tInfo.getPrefix() + tInfo.getName() + "\u00A7f: \u00A77" + msg);
            target.playSound(target.getLocation(), Sound.SUCCESSFUL_HIT, 1, 1);
            if (lastMsg.containsKey(player)) {
                lastMsg.replace(player, target);
            } else {
                lastMsg.put(player, target);
            }
        } else {
            player.sendMessage("\u00A7cInvalid channel! Use \"/chat\" to select a channel!");
            CommandChat.setChat(player, "all");
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(event.toWeatherState());
    }

    private void playerVisibility(Player player) {
        String playerWorld = player.getWorld().getName();
        List<String> playerSection = main.getConfigFile().getStringList("sameTab." + playerWorld);
        for (Player p : Bukkit.getOnlinePlayers()) {
            String pWorld = p.getWorld().getName();
            List<String> pSection = main.getConfigFile().getStringList("sameTab." + pWorld);
            if (playerSection != null && playerSection.contains(pWorld)) {
                pWorld = playerWorld;
            } else if (pSection != null && pSection.contains(playerWorld)) {
                playerWorld = pWorld;
            }
            if (playerWorld.equals(pWorld)) {
                if (!p.canSee(player)) {
                    p.showPlayer(player);
                }
                if (!player.canSee(p)) {
                    player.showPlayer(p);
                }
            } else {
                if (player.canSee(p)) {
                    player.hidePlayer(p);
                }
                if (p.canSee(player)) {
                    p.hidePlayer(player);
                }
            }
        }
    }

}
