package me.solarlego.solarmain.gui;

import me.solarlego.solarmain.SolarMain;
import me.solarlego.solarmain.hub.Hub;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PlayGUI implements Listener {

    private final Inventory inv;

    public PlayGUI(Player player) {
        inv = Bukkit.createInventory(null, 27, "Teams");
        initializeItems();
        Bukkit.getServer().getPluginManager().registerEvents(this, SolarMain.getPlugin());
        player.openInventory(inv);
    }

    public void initializeItems() {
        inv.setItem(10, Hub.createItemStack(Material.SIGN, "\u00A7fCommands", 0, "\u00A77Display the list of commands", "", "\u00A7eClick for Help"));
        inv.setItem(12, Hub.createItemStack(Material.SHEARS, "\u00A7fSpleef", 0, "\u00A77Free for all", "", "\u00A7eClick to join Spleef!"));
        inv.setItem(13, Hub.createItemStack(Material.GOLDEN_APPLE, "\u00A7fUHC", 0, "\u00A77Hardcore Survival", "", "\u00A7eClick to join UHC!"));
        inv.setItem(14, Hub.createItemStack(Material.WOOL, "\u00A7fBridgeWars", 14, "\u00A77Team Survival", "", "\u00A7eClick to join BridgeWars!"));
        inv.setItem(16, Hub.createItemStack(Material.REDSTONE_TORCH_ON, "\u00A7fMore Info", 0, "\u00A7e\u00A7lJoin a Game!", "\u00A76\u00A7m--------------------------", "\u00A7fServer Owner: \u00A72solarlego", "\u00A7fDiscord: \u00A7asolarlego#5862", "\u00A77  Contact with any questions", "\u00A73Ranks are purely cosmetic!", "\u00A76\u00A7m--------------------------"));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(inv)) {
            return;
        }
        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        Player p = (Player) event.getWhoClicked();
        if (event.getRawSlot() == 10) {
            p.closeInventory();
            final String msg = "\u00A76\u00A7m-----------------------------------\n"
                    .concat("\u00A7e/play: \u00A7fJoin a game.\n")
                    .concat("\u00A7e/hub: \u00A7fWarp back to the hub.\n")
                    .concat("\u00A7e/party: \u00A7fStarts a party.\n")
                    .concat("\u00A7e/chat: \u00A7fSwitch your chat channel.\n")
                    .concat("\u00A7e/message: \u00A7fMessage another player privately.\n")
                    .concat("\u00A7e/team: \u00A7fJoin a team. \u00A77Game Specific\n")
                    .concat("\u00A7e/tools: \u00A7fSet up your hotbar. \u00A77Game Specific\n")
                    .concat("\u00A76\u00A7m-----------------------------------");
            p.sendMessage(msg);
        } else if (event.getRawSlot() == 12) {
            p.chat("/play spleef");
        } else if (event.getRawSlot() == 13) {
            p.chat("/play uhc");
        } else if (event.getRawSlot() == 14) {
            p.chat("/play bridgewars");
        }

    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().equals(inv)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory() == inv) {
            HandlerList.unregisterAll(this);
        }
    }

}
