package xyz.acyber.oneLife.DataObjects.SubScoreData;

import com.fasterxml.jackson.annotation.*;
import org.bukkit.GameMode;
import org.bukkit.Material;
import xyz.acyber.oneLife.OneLifePlugin;

import java.util.HashMap;

public class MaterialInteractions {

    @JsonIgnore
    private OneLifePlugin plugin = null;
    @JsonProperty("material")
    private Material material = null;
    @JsonProperty("count")
    private HashMap<String, Integer> count = null; // Key is gamemode, value is number of interactions

    /**
     * can be any of BlocksPlaced, BlocksMined & ItemsHarvested
     */
    @JsonProperty("typeInteraction")
    private String typeInteraction = null;

    @JsonCreator
    public MaterialInteractions() { super(); } // Default constructor

    /**
     * @param typeInteraction can be any of BlocksPlaced, BlocksMined & ItemsHarvested
     */
    @JsonIgnore
    public MaterialInteractions(OneLifePlugin plugin, Material material, HashMap<String, Integer> count, String typeInteraction) {
        this.plugin = plugin;
        this.material = material;
        this.count = count;
        this.typeInteraction = typeInteraction;
    }
    @JsonGetter
    public Material getMaterial() { return material; }
    @JsonSetter
    public void setMaterial(Material material) { this.material = material; }

    @JsonGetter
    public HashMap<String, Integer> getCount() { if (count == null) count = new HashMap<>(); return count; }
    @JsonSetter
    public void setCount(HashMap<String, Integer> count) { this.count = count; }

    @JsonGetter
    public String getTypeInteraction() { return typeInteraction; }
    @JsonSetter
    public void setTypeInteraction(String typeInteraction) { this.typeInteraction = typeInteraction; }

    @JsonIgnore
    public HashMap<String, Double> getPoints() {
        HashMap<String, Double> points = new HashMap<>();
        for (String key: count.keySet()) {
            double multiplier = 0;
            if (typeInteraction.equals("BlocksPlaced")) {
                if (key.equals("AFK"))
                    multiplier = plugin.settings.getScoring().get(GameMode.SURVIVAL.name()).getBlockPlaceMultipliers().get(material) * plugin.settings.getAFKMultiplier();
                else
                    multiplier = plugin.settings.getScoring().get(GameMode.valueOf(key).name()).getBlockPlaceMultipliers().get(material);
            } else if (typeInteraction.equals("BlocksMined")) {
                if (key.equals("AFK"))
                    multiplier = plugin.settings.getScoring().get(GameMode.SURVIVAL.name()).getBlockMineMultipliers().get(material) * plugin.settings.getAFKMultiplier();
                else
                    multiplier = plugin.settings.getScoring().get(GameMode.valueOf(key).name()).getBlockMineMultipliers().get(material);
            } else if (typeInteraction.equals("ItemsHarvested")) {
                if (key.equals("AFK"))
                    multiplier = plugin.settings.getScoring().get(GameMode.SURVIVAL.name()).getHarvestMultipliers().get(material) * plugin.settings.getAFKMultiplier();
                else
                    multiplier = plugin.settings.getScoring().get(GameMode.valueOf(key).name()).getHarvestMultipliers().get(material);
            }
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
