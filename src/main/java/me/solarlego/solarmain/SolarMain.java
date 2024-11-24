package me.solarlego.solarmain;

import me.solarlego.solarmain.commands.*;
import me.solarlego.solarmain.hub.Hub;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

public class SolarMain extends JavaPlugin {

    private static SolarMain instance;
    private FileConfiguration configFile;

    @Override
    public void onEnable() {
        Properties pr = new Properties();
        try {
            pr.load(new FileInputStream("./server.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileUtils.deleteDirectory(new File("./" + pr.getProperty("level-name")));
        FileUtils.copyResourcesRecursively(this,"/hub", new File("./" + pr.getProperty("level-name")));
        instance = this;
        Bukkit.getServer().getPluginManager().registerEvents(new MainEvents(this), this);
        getCommand("hub").setExecutor(new CommandHub());
        getCommand("hub").setAliases(Arrays.asList("l", "lobby", "zoo"));
        getCommand("rank").setExecutor(new CommandRank());
        getCommand("warp").setExecutor(new CommandWarp());
        getCommand("party").setExecutor(new CommandParty());
        getCommand("party").setAliases(Arrays.asList("p", "fiesta"));
        getCommand("chat").setExecutor(new CommandChat());
        getCommand("message").setExecutor(new CommandMessage());
        getCommand("message").setAliases(Arrays.asList("msg", "whisper", "w", "tell", "t"));
        getCommand("play").setExecutor(new CommandPlay());
        getCommand("play").setAliases(Arrays.asList("game", "games"));

        saveDefaultConfig();
        configFile = getConfig();
        File file = new File(getDataFolder(), "players.yml");
        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this::updateHealth, 0, 4);

    }

    @Override
    public void onDisable() {
        for (Hub hub : Hub.hubs.keySet()) {
            hub.shutdown();
        }
    }

    public static SolarMain getPlugin() {
        return instance;
    }

    public FileConfiguration getConfigFile() {
        return configFile;
    }

    private void updateHealth() {
        for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
            if (player.getScoreboard().getObjective("healthList") == null) {
                continue;
            }
            for (org.bukkit.entity.Player p : Bukkit.getOnlinePlayers()) {
                if (player.canSee(p)) {
                    double health = ((CraftPlayer) p).getHandle().getAbsorptionHearts() + p.getHealth();
                    player.getScoreboard().getObjective("healthList").getScore(p.getName()).setScore((int) Math.round(health));
                }
            }
        }
    }

}
