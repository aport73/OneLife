package xyz.acyber.oneLife.DataObjects.SubSettings;

import com.fasterxml.jackson.annotation.*;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.HashMap;

public class GamemodeScoreMultipliers {

    @JsonProperty("deathMultiplier")
    private double deathMultiplier = -10;
    @JsonProperty("xpMultiplier")
    private double xpMultiplier = 0.0001;
    @JsonProperty("onlineHrMultiplier")
    private double onlineHrMultiplier = 2.5;
    @JsonProperty("defaultGamemodeMultiplier")
    private double defaultGamemodeMultiplier = 1;
    @JsonProperty("mobKillMultipliers")
    private HashMap<EntityType, Double> mobKillMultipliers = new HashMap<>(); // Key is entityType, value is multiplier
    @JsonProperty("blockPlaceMultipliers")
    private HashMap<Material, Double> blockPlaceMultipliers = new HashMap<>(); // Key is blockType, value is multiplier
    @JsonProperty("blockMineMultipliers")
    private HashMap<Material, Double> blockMineMultipliers = new HashMap<>(); // Key is blockType, value is multiplier
    @JsonProperty("harvestMultipliers")
    private HashMap<Material, Double> harvestMultipliers = new HashMap<>(); // Key is itemType, value is multiplier

    @JsonCreator
    public GamemodeScoreMultipliers() { super(); } // Default constructor

    @JsonGetter
    public double getDeathMultiplier() { return deathMultiplier; }
    @JsonSetter
    public void setDeathMultiplier(double deathMultiplier) {  this.deathMultiplier = deathMultiplier; }

    @JsonGetter
    public double getXpMultiplier() { return xpMultiplier; }
    @JsonSetter
    public void setXpMultiplier(double xpMultiplier) {  this.xpMultiplier = xpMultiplier; }

    @JsonGetter
    public double getOnlineHrMultiplier() { return onlineHrMultiplier; }
    @JsonSetter
    public void setOnlineHrMultiplier(double onlineHrMultiplier) {  this.onlineHrMultiplier = onlineHrMultiplier; }


    @JsonGetter
    public HashMap<EntityType, Double> getMobKillMultipliers() { if (mobKillMultipliers == null) mobKillMultipliers = new HashMap<>(); return mobKillMultipliers; }
    @JsonSetter
    public void setMobKillMultipliers(HashMap<EntityType, Double> mobKillMultipliers) { if (mobKillMultipliers == null) mobKillMultipliers = new HashMap<>(); this.mobKillMultipliers = mobKillMultipliers; }
    @JsonIgnore
    public boolean addMobKillMultiplier(EntityType entityType, double multiplier) {
        if (mobKillMultipliers.containsKey(entityType))
            return false;
        mobKillMultipliers.put(entityType, multiplier);
        return true;
    }
    @JsonIgnore
    public boolean modifyMobKillMultiplier(EntityType entityType, double multiplier) {
        if (mobKillMultipliers.containsKey(entityType)) {
            mobKillMultipliers.replace(entityType, multiplier);
            return true;
        }
        return false;
    }
    @JsonIgnore
    public void removeMobKillMultiplier(EntityType entityType) {
        mobKillMultipliers.remove(entityType);
    }

    @JsonGetter
    public HashMap<Material, Double> getBlockPlaceMultipliers() { if (blockPlaceMultipliers == null) blockPlaceMultipliers = new HashMap<>(); return blockPlaceMultipliers; }
    @JsonSetter
    public void setBlockPlaceMultipliers(HashMap<Material, Double>  blockPlaceMultipliers) { if (blockPlaceMultipliers == null) blockPlaceMultipliers = new HashMap<>();  this.blockPlaceMultipliers = blockPlaceMultipliers; }
    @JsonIgnore
    public boolean addBlockPlaceMultiplier(Material type, double multiplier) {
        if (blockPlaceMultipliers.containsKey(type))
            return false;
        blockPlaceMultipliers.put(type, multiplier);
        return true;
    }
    @JsonIgnore
    public boolean modifyBlockPlaceMultiplier(Material type, double multiplier) {
        if (blockPlaceMultipliers.containsKey(type)) {
            blockPlaceMultipliers.replace(type,multiplier);
            return true;
        }
        return false;
    }
    @JsonIgnore
    public void removeBlockPlaceMultiplier(Material type) {
        blockPlaceMultipliers.remove(type);
    }

    @JsonGetter
    public HashMap<Material, Double> getBlockMineMultipliers() { if (blockMineMultipliers == null) blockMineMultipliers = new HashMap<>(); return blockMineMultipliers; }
    @JsonSetter
    public void setBlockMineMultipliers(HashMap<Material, Double> blockMineMultipliers) { if (blockMineMultipliers == null) blockMineMultipliers = new HashMap<>(); this.blockMineMultipliers = blockMineMultipliers; }
    @JsonIgnore
    public boolean addBlockMineMultiplier(Material type, double multiplier) {
        if (blockMineMultipliers.containsKey(type)) {
            return false;
        }
        blockMineMultipliers.put(type, multiplier);
        return true;
    }
    @JsonIgnore
    public boolean modifyBlockMineMultiplier(Material type, double multiplier) {
        if (blockMineMultipliers.containsKey(type)) {
            blockMineMultipliers.replace(type,multiplier);
            return true;
        }
        return false;
    }
    @JsonIgnore
    public void removeBlockMineMultiplier(Material type) {
        blockMineMultipliers.remove(type);
    }

    @JsonGetter
    public HashMap<Material, Double> getHarvestMultipliers() { if (harvestMultipliers == null) harvestMultipliers = new HashMap<>(); return harvestMultipliers; }
    @JsonSetter
    public void setHarvestMultipliers(HashMap<Material, Double> harvestMultipliers) { if (harvestMultipliers == null) harvestMultipliers = new HashMap<>();  this.harvestMultipliers = harvestMultipliers; }
    @JsonIgnore
    public boolean addHarvestMultiplier(Material type, double multiplier) {
        if (harvestMultipliers.containsKey(type)) {
            return false;
        }
        harvestMultipliers.put(type, multiplier);
        return true;
    }
    @JsonIgnore
    public boolean modifyHarvestMultiplier(Material type, double multiplier) {
        if (harvestMultipliers.containsKey(type)) {
            harvestMultipliers.replace(type, multiplier);
            return true;
        }
        return false;
    }
    @JsonIgnore
    public void removeHarvestMultiplier(Material type) {harvestMultipliers.remove(type);
    }
}
