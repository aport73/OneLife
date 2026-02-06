package xyz.acyber.oneLife.DataObjects.SubScoreData;

import org.bukkit.entity.EntityType;

import java.util.HashMap;

public class MobsKilled {
    private EntityType entityType = null;
    private HashMap<String, Integer> count = null; // Key is gamemode, value is number of mobs killed

    public MobsKilled() { super(); } // Default constructor

    public MobsKilled(EntityType entityType, HashMap<String, Integer> count) {
        this.entityType = entityType;
        this.count = count;
    }

    public EntityType getEntityType() { return entityType; }
    public void setEntityType(EntityType entityType) { this.entityType = entityType; }

    public HashMap<String, Integer> getCount() { if (count == null) count = new HashMap<>(); return count; }
    public void setCount(HashMap<String, Integer> count) { this.count = count; }

    public void incrementCount(String gameMode) {
        if (this.count == null) this.count = new HashMap<>();
        this.count.merge(gameMode, 1, Integer::sum);
    }

    public int getTotalCount() {
        if (count == null) return 0;
        int total = 0;
        for (Integer v : count.values()) {
            total += (v == null ? 0 : v);
        }
        return total;
    }
}
