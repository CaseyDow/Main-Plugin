package me.solarlego.solarmain.hub;

import me.solarlego.solarmain.Scoreboard;
import me.solarlego.solarmain.Stats;
import me.solarlego.solarmain.gui.PlayGUI;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.util.Vector;

import java.util.Timer;
import java.util.TimerTask;

public class HubEvents implements Listener {

    private final Hub hub;
    private final Scoreboard sb;

    public HubEvents(Hub h) {
        hub = h;
        sb = new Scoreboard(hub);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (hub.checkWorld(event.getPlayer().getWorld())) {
            setUpPlayer(event.getPlayer());
            event.getPlayer().teleport(hub.worldHub.getSpawnLocation().add(0.5, 0, 0.5));
            (new Timer()).scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    if (hub.checkWorld(event.getPlayer().getWorld()) && event.getPlayer().isOnline()) {
                        sb.updateScoreboard(event.getPlayer());
                    } else {
                        this.cancel();
                    }
                }
            }, 0, 1000);
        }
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        if (hub.checkWorld(event.getPlayer().getWorld())) {
            setUpPlayer(event.getPlayer());
            (new Timer()).scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    if (hub.checkWorld(event.getPlayer().getWorld()) && event.getPlayer().isOnline()) {
                        sb.updateScoreboard(event.getPlayer());
                    } else {
                        this.cancel();
                    }
                }
            }, 0, 1000);
        } else if (hub.checkWorld(event.getFrom()) && event.getPlayer().getGameMode() != GameMode.CREATIVE && event.getPlayer().getGameMode() != GameMode.SPECTATOR) {
            event.getPlayer().setAllowFlight(false);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (hub.checkWorld(event.getPlayer().getWorld())) {
            if (event.getTo().getBlockY() <= 20) {
                event.getPlayer().teleport(new Location(event.getPlayer().getWorld(), 0.5, 115, 0.5));
            } else if (Math.abs(event.getTo().getBlockX()) > 80 || Math.abs(event.getTo().getBlockZ()) > 80) {
                event.setTo(event.getFrom());
            } else if (inPvP(event.getFrom()) && !inPvP(event.getTo())) {
                setUpPlayer(event.getPlayer());
            } else if (!inPvP(event.getFrom()) && inPvP(event.getTo())) {
                event.getPlayer().setAllowFlight(false);
                event.getPlayer().getInventory().clear();
            }

        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (hub.checkWorld(event.getPlayer().getWorld()) && event.getAction() != Action.PHYSICAL) {
            if (event.getPlayer().getItemInHand().getType() == Material.COMPASS) {
                new PlayGUI(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (hub.checkWorld(event.getPlayer().getWorld()) && event.getPlayer().getGameMode() == GameMode.ADVENTURE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (hub.checkWorld(event.getWhoClicked().getWorld()) && event.getWhoClicked().getGameMode() == GameMode.ADVENTURE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (hub.checkWorld(event.getWhoClicked().getWorld()) && event.getWhoClicked().getGameMode() == GameMode.ADVENTURE) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (hub.checkWorld(event.getEntity().getWorld())) {
            if (inPvP(event.getDamager().getLocation()) && inPvP(event.getEntity().getLocation())) {
                event.setCancelled(false);
                event.setDamage(event.getDamage() * 2);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (hub.checkWorld(event.getEntity().getWorld())) {
            Player killer = event.getEntity().getKiller();
            if (killer != null) {
                killer.setHealth(Math.min(killer.getHealth() + 8, 20));
            }
            event.getEntity().setVelocity(new Vector());
            event.getEntity().teleport(hub.worldHub.getSpawnLocation().add(0.5, 0, 0.5));
            event.getEntity().spigot().respawn();
            setUpPlayer(event.getEntity());
        }
    }

    private boolean inPvP(Location loc) {
        return Math.abs(loc.getBlockX()) < 7 && Math.abs(loc.getBlockY() - 112.5) < 2 && Math.abs(loc.getBlockZ() + 21) < 6;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (hub.checkWorld(event.getEntity().getWorld())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (hub.checkWorld(event.getEntity().getWorld())) {
            event.setCancelled(true);
        }
    }

    private void setUpPlayer(Player player) {
        Hub.resetPlayer(player);
        player.setAllowFlight(true);
        player.setGameMode(GameMode.ADVENTURE);

        player.setPlayerListName(Stats.get(player.getUniqueId()).getPrefix() + Stats.get(player.getUniqueId()).getName());
        player.getInventory().setItem(0, Hub.createItemStack(Material.COMPASS, "\u00A7fGame Selector", 0, "\u00A7eRight Click to Open!"));
    }

}
