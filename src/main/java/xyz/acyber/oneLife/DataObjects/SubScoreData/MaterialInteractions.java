package xyz.acyber.oneLife.DataObjects.SubScoreData;

import org.bukkit.Material;

import java.util.HashMap;

public class MaterialInteractions {

    private Material material = null;
    private HashMap<String, Integer> count = null; // Key is gamemode, value is number of interactions

    public MaterialInteractions() { super(); } // Default constructor

    public MaterialInteractions(Material material, HashMap<String, Integer> count) {
        this.material = material;
        this.count = count;
    }

    public Material getMaterial() { return material; }
    public void setMaterial(Material material) { this.material = material; }

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
