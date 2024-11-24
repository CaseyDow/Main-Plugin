package me.solarlego.solarmain.hub;

import me.solarlego.solarmain.FileUtils;
import me.solarlego.solarmain.SolarMain;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class Hub {

    public static final Map<Hub, String> hubs = new HashMap<>();
    public final World worldHub;

    public Hub(String hub) {
        hubs.put(this, hub);
        Bukkit.getServer().getPluginManager().registerEvents(new HubEvents(this), SolarMain.getPlugin());

        FileUtils.copyResourcesRecursively(SolarMain.getPlugin(),"/hub", new File("./" + hub));
        worldHub = new WorldCreator(hub).createWorld();
    }

    public boolean checkWorld(World world) {
        return worldHub == world;
    }

    public static void sendHub(Player player) {
        if (hubs.size() < 1) {
            Properties pr = new Properties();
            try {
                pr.load(new FileInputStream("./server.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            new Hub(pr.getProperty("level-name"));
        }
        Hub hub = (Hub) hubs.keySet().toArray()[new Random().nextInt(hubs.size())];
        player.teleport(hub.worldHub.getSpawnLocation().add(0.5, 0, 0.5));
    }

    public static ItemStack createItemStack(Material material, String name, Integer damage, String... lore) {
        ItemStack item = new ItemStack(material, 1, damage.shortValue());

        ItemMeta meta = item.getItemMeta();
        if (meta instanceof Damageable) {
            ((Damageable) meta).damage(damage);
        }
        if (!name.equals("")) {
            meta.setDisplayName(name);
        }
        if (lore.length > 0) {
            meta.setLore(Arrays.asList(lore));
        }
        item.setItemMeta(meta);

        return item;
    }

    public static void resetPlayer(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setExp(0);
        player.setLevel(0);
        player.setMaxHealth(20);
        player.setHealth(20);
        player.setFoodLevel(20);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        player.setBedSpawnLocation(player.getWorld().getSpawnLocation(), true);
    }

    public void shutdown() {
        Properties pr = new Properties();
        try {
            pr.load(new FileInputStream("./server.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!pr.getProperty("level-name").equals(worldHub.getName())) {
            Bukkit.getServer().unloadWorld(worldHub, false);
            FileUtils.deleteDirectory(new File("./" + worldHub.getName()));
        }
    }

}
