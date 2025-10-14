package xyz.acyber.oneLife.DataObjects.SubScoreData;

import com.fasterxml.jackson.annotation.*;
import org.bukkit.GameMode;
import org.bukkit.Material;
import xyz.acyber.oneLife.OneLifePlugin;

import java.util.HashMap;

public class MaterialInteractions {

    @JsonProperty("material")
    private Material material = null;
    @JsonProperty("count")
    private HashMap<String, Integer> count = null; // Key is gamemode, value is number of interactions

    @JsonCreator
    public MaterialInteractions() { super(); } // Default constructor

    public MaterialInteractions(Material material, HashMap<String, Integer> count) {
        this.material = material;
        this.count = count;
    }

    @JsonGetter
    public Material getMaterial() { return material; }
    @JsonSetter
    public void setMaterial(Material material) { this.material = material; }

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
