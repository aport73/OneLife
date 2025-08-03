package xyz.acyber.oneLife.DataObjects.SubScoreData;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.bukkit.GameMode;
import org.bukkit.Material;
import xyz.acyber.oneLife.OneLifePlugin;

import java.util.HashMap;

public class MaterialInteractions {

    private OneLifePlugin plugin;
    private Material material;
    private HashMap<String, Integer> count;

    /**
     * can be any of BlocksPlaced, BlocksMined & ItemsHarvested
     */
    private String type;


    /**
     * @param type can be any of BlocksPlaced, BlocksMined & ItemsHarvested
     */
    @JsonIgnore
    public MaterialInteractions(OneLifePlugin plugin, Material material, HashMap<String, Integer> count, String type) {
        this.plugin = plugin;
        this.material = material;
        this.count = count;
        this.type = type;
    }
    @JsonGetter
    public Material getMaterial() { return material; }
    @JsonSetter
    public void setMaterial(Material material) { this.material = material; }

    @JsonGetter
    public HashMap<String, Integer> getCount() { return count; }
    @JsonSetter
    public void setCount(HashMap<String, Integer> count) { this.count = count; }

    @JsonGetter
    public String getType() { return type; }
    @JsonSetter
    public void setType(String type) { this.type = type; }

    @JsonIgnore
    public HashMap<String, Double> getPoints() {
        HashMap<String, Double> points = new HashMap<>();
        for (String key: count.keySet()) {
            double multiplier = 0;
            if (type.equals("BlocksPlaced")) {
                if (key.equals("AFK"))
                    multiplier = plugin.settings.getScoring().get(GameMode.SURVIVAL).getBlockPlaceMultipliers().get(material) * plugin.settings.getAFKMultiplier();
                else
                    multiplier = plugin.settings.getScoring().get(GameMode.valueOf(key)).getBlockPlaceMultipliers().get(material);
            } else if (type.equals("BlocksMined")) {
                if (key.equals("AFK"))
                    multiplier = plugin.settings.getScoring().get(GameMode.SURVIVAL).getBlockMineMultipliers().get(material) * plugin.settings.getAFKMultiplier();
                else
                    multiplier = plugin.settings.getScoring().get(GameMode.valueOf(key)).getBlockMineMultipliers().get(material);
            } else if (type.equals("ItemsHarvested")) {
                if (key.equals("AFK"))
                    multiplier = plugin.settings.getScoring().get(GameMode.SURVIVAL).getHarvestMultipliers().get(material) * plugin.settings.getAFKMultiplier();
                else
                    multiplier = plugin.settings.getScoring().get(GameMode.valueOf(key)).getHarvestMultipliers().get(material);
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
