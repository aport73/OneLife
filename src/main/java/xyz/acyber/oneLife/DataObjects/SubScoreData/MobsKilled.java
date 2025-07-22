package xyz.acyber.oneLife.DataObjects.SubScoreData;

import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import xyz.acyber.oneLife.Main;

import java.util.HashMap;

public class MobsKilled {

    private Main plugin;
    private final EntityType entityType;
    private HashMap<String, Integer> count;

    public MobsKilled(Main plugin, EntityType entityType, HashMap<String, Integer> count) {
        this.plugin = plugin;
        this.entityType = entityType;
        this.count = count;
    }

    public EntityType getEntityType() { return entityType; }

    public HashMap<String, Integer> getCount() { return count; }
    public void setCount(HashMap<String, Integer> count) { this.count = count; }

    public HashMap<String, Double> getPoints() {
        HashMap<String, Double> points = new HashMap<>();
        for (String key: count.keySet()) {
            double multiplier = 0;
            if (key.equals("AFK"))
                multiplier = plugin.settings.getScoring().get(GameMode.SURVIVAL).getMobKillMultipliers().get(entityType) * plugin.settings.getAFKMultiplier();
            else
                multiplier = plugin.settings.getScoring().get(GameMode.valueOf(key)).getMobKillMultipliers().get(entityType);
            points.put(key,(count.get(key)*multiplier));
        }
        return points;
    }

    public int getTotalCount() {
        int total = 0;
        for (String key: count.keySet()) {
            total += count.get(key);
        }
        return total;
    }

}
