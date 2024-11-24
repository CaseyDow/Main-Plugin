package me.solarlego.solarmain;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class Stats {

    private static final FileConfiguration playersFile = YamlConfiguration.loadConfiguration(new File(SolarMain.getPlugin().getDataFolder(), "players.yml"));

    private final UUID player;

    public Stats(UUID p) {
        player = p;
    }

    public org.bukkit.entity.Player getPlayer() {
        return Bukkit.getPlayer(player);
    }

    public String getName() {
        return Bukkit.getPlayer(player).getName();
    }

    public String getRank() {
        return playersFile.getString(player + ".rank");
    }

    public String getPrefix() {
        String rank = playersFile.getString(player + ".rank");
        return SolarMain.getPlugin().getConfigFile().getString("ranks." + rank + ".prefix").replace("&", "\u00A7");
    }

    public String getColor() {
        String rank = playersFile.getString(player + ".rank");
        return SolarMain.getPlugin().getConfigFile().getString("ranks." + rank + ".color").replace("&", "\u00A7");
    }

    public static void updatePlayerFile(String path, String val) {
        playersFile.set(path, val);
        playersFile.options().copyDefaults(true);
        try {
            playersFile.save(new File(SolarMain.getPlugin().getDataFolder(), "players.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Stats get(UUID p) {
        return new Stats(p);
    }

}
