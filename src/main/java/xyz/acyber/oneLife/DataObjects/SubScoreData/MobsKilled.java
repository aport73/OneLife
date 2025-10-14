package xyz.acyber.oneLife.DataObjects.SubScoreData;

import com.fasterxml.jackson.annotation.*;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import xyz.acyber.oneLife.OneLifePlugin;

import java.util.HashMap;

public class MobsKilled {

    @JsonProperty("entityType")
    private EntityType entityType = null;
    @JsonProperty("count")
    private HashMap<String, Integer> count = null; // Key is gamemode, value is number of mobs killed

    @JsonCreator
    public MobsKilled() { super(); } // Default constructor

    @JsonIgnore
    public MobsKilled(EntityType entityType, HashMap<String, Integer> count) {
        this.entityType = entityType;
        this.count = count;
    }

    @JsonGetter
    public EntityType getEntityType() { return entityType; }
    @JsonSetter
    public void setEntityType(EntityType entityType) { this.entityType = entityType; }

    @JsonGetter
    public HashMap<String, Integer> getCount() { if (count == null) count = new HashMap<>(); return count; }
    @JsonSetter
    public void setCount(HashMap<String, Integer> count) { this.count = count; }
    @JsonIgnore
    public void incrementCount(String gameMode) {
        if (this.count.containsKey(gameMode))
            count.replace(gameMode, count.get(gameMode) + 1);
        else
            count.put(gameMode, 1);
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
