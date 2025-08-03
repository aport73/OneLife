package xyz.acyber.oneLife.DataObjects.SubScoreData;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import xyz.acyber.oneLife.OneLifePlugin;

import java.util.HashMap;

public class MobsKilled {

    private OneLifePlugin plugin;
    private EntityType entityType;
    private HashMap<String, Integer> count;

    @JsonIgnore
    public MobsKilled(OneLifePlugin plugin, EntityType entityType, HashMap<String, Integer> count) {
        this.plugin = plugin;
        this.entityType = entityType;
        this.count = count;
    }

    @JsonGetter
    public EntityType getEntityType() { return entityType; }
    @JsonSetter
    public void setEntityType(EntityType entityType) { this.entityType = entityType; }

    @JsonGetter
    public HashMap<String, Integer> getCount() { return count; }
    @JsonSetter
    public void setCount(HashMap<String, Integer> count) { this.count = count; }

    @JsonIgnore
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

    @JsonIgnore
    public int getTotalCount() {
        int total = 0;
        for (String key: count.keySet()) {
            total += count.get(key);
        }
        return total;
    }

}
