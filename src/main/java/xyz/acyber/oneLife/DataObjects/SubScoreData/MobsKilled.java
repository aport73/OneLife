package xyz.acyber.oneLife.DataObjects.SubScoreData;

import com.fasterxml.jackson.annotation.*;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import xyz.acyber.oneLife.OneLifePlugin;

import java.util.HashMap;

public class MobsKilled {

    @JsonIgnore
    private OneLifePlugin plugin = null;
    @JsonProperty("entityType")
    private String entityType = null;
    @JsonProperty("count")
    private HashMap<String, Integer> count = null; // Key is gamemode, value is number of mobs killed

    @JsonCreator
    public MobsKilled() { super(); } // Default constructor

    @JsonIgnore
    public MobsKilled(OneLifePlugin plugin, String entityType, HashMap<String, Integer> count) {
        this.plugin = plugin;
        this.entityType = entityType;
        this.count = count;
    }

    @JsonGetter
    public String getEntityType() { return entityType; }
    @JsonSetter
    public void setEntityType(String entityType) { this.entityType = entityType; }

    @JsonGetter
    public HashMap<String, Integer> getCount() { if (count == null) count = new HashMap<>(); return count; }
    @JsonSetter
    public void setCount(HashMap<String, Integer> count) { this.count = count; }

    @JsonIgnore
    public HashMap<String, Double> getPoints() {
        HashMap<String, Double> points = new HashMap<>();
        for (String key: count.keySet()) {
            double multiplier = 0;
            if (key.equals("AFK"))
                multiplier = plugin.settings.getScoring().get(GameMode.SURVIVAL.name()).getMobKillMultipliers().get(entityType) * plugin.settings.getAFKMultiplier();
            else
                multiplier = plugin.settings.getScoring().get(GameMode.valueOf(key).name()).getMobKillMultipliers().get(entityType);
            points.put(key,(count.get(key)*multiplier));
        }
        return points;
    }

    @JsonIgnore
    public int getTotalCount() {
        int total = 0;
        for (String key: count.keySet()) {
            total += count.get(key);
        }
        return total;
    }

}
