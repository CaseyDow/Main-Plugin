package me.solarlego.solarmain;

import me.solarlego.solarmain.hub.Hub;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.Map;

public class Scoreboard {

    private final Hub hub;

    public Scoreboard(Hub h) {
        hub = h;
    }

    public void updateScoreboard(Player player) {
        if (!hub.checkWorld(player.getWorld())) {
            return;
        }
        FileConfiguration configFile = SolarMain.getPlugin().getConfigFile();
        org.bukkit.scoreboard.Scoreboard board = player.getScoreboard();

        if (board.getObjective("healthName") == null) {
            Objective hpName = board.registerNewObjective("healthName", "health");
            hpName.setDisplayName("❤");
            hpName.setDisplaySlot(DisplaySlot.BELOW_NAME);
        }
        if (board.getObjective("healthList") == null) {
            Objective hpList = board.registerNewObjective("healthList", "dummy");
            hpList.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        }


        Map<String, String> replacements = new HashMap<>();

        Stats playerInfo = Stats.get(player.getUniqueId());
        replacements.put("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()));
        replacements.put("%rank%", playerInfo.getRank());
        replacements.put("%rank_color%", playerInfo.getColor());

        String title = configFile.getString("scoreboards.name");
        if (title != null) {
            Objective obj = board.getObjective(title.replace("&", "\u00A7"));
            if (obj == null) {
                obj = board.registerNewObjective(title.replace("&", "\u00A7"), "dummy");
                obj.setDisplaySlot(DisplaySlot.SIDEBAR);
            }
            int i = 0;
            while (configFile.contains("scoreboards." + i)) {

                String name = configFile.getString("scoreboards." + i);
                for (String key : replacements.keySet()) {
                    name = name.replace(key, replacements.get(key));
                }

                String prefix = "";
                String suffix = "";
                for (int j = 0; j < name.length(); j++) {
                    if (prefix.length() < 15 || !Character.toString(name.charAt(j)).equals("&") && prefix.length() == 15) {
                        String curChar = Character.toString(name.charAt(j));
                        prefix += curChar;
                        if (curChar.equals("&")) {
                            String nextChar = Character.toString(name.charAt(j + 1));
                            if (nextChar.equals("k") || nextChar.equals("l") || nextChar.equals("m") || nextChar.equals("n") || nextChar.equals("o") || nextChar.equals("r")) {
                                suffix += "&" + nextChar;
                            } else {
                                suffix = "&" + nextChar;
                            }
                        }
                    } else {
                        int end = j + 15 - suffix.length();
                        if (end > name.length()) {
                            end = name.length();
                        }
                        suffix += name.substring(j, end);
                        break;
                    }
                }

                String teamName = "\u00A7" + i + "\u00A7" + i % 10;
                Team team = board.getTeam("line" + i);
                if (team == null) {
                    team = board.registerNewTeam("line" + i);
                    team.addEntry(teamName);
                    obj.getScore(teamName).setScore(i);
                }
                team.setPrefix(prefix.replace("&", "\u00A7"));
                team.setSuffix(suffix.replace("&", "\u00A7"));
                i++;
            }
        }

        player.setScoreboard(board);
    }

}
