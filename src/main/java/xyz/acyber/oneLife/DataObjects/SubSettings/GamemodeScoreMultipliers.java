package xyz.acyber.oneLife.DataObjects.SubSettings;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.HashMap;

public class GamemodeScoreMultipliers {
    private double deathMultiplier = -10;
    private double xpMultiplier = 0.0001;
    private double onlineHrMultiplier = 2.5;
    private double defaultGamemodeMultiplier = 1;
    private HashMap<EntityType, Double> mobKillMultipliers = new HashMap<>(); // Key is entityType, value is multiplier
    private HashMap<Material, Double> blockPlaceMultipliers = new HashMap<>(); // Key is blockType, value is multiplier
    private HashMap<Material, Double> blockMineMultipliers = new HashMap<>(); // Key is blockType, value is multiplier
    private HashMap<Material, Double> harvestMultipliers = new HashMap<>(); // Key is itemType, value is multiplier

    public GamemodeScoreMultipliers() { super(); } // Default constructor

    public double getDeathMultiplier() { return deathMultiplier; }
    public void setDeathMultiplier(double deathMultiplier) {  this.deathMultiplier = deathMultiplier; }

    public double getXpMultiplier() { return xpMultiplier; }
    public void setXpMultiplier(double xpMultiplier) {  this.xpMultiplier = xpMultiplier; }

    public double getOnlineHrMultiplier() { return onlineHrMultiplier; }
    public void setOnlineHrMultiplier(double onlineHrMultiplier) {  this.onlineHrMultiplier = onlineHrMultiplier; }


    public HashMap<EntityType, Double> getMobKillMultipliers() { if (mobKillMultipliers == null) mobKillMultipliers = new HashMap<>(); return mobKillMultipliers; }
    public void setMobKillMultipliers(HashMap<EntityType, Double> mobKillMultipliers) { if (mobKillMultipliers == null) mobKillMultipliers = new HashMap<>(); this.mobKillMultipliers = mobKillMultipliers; }

    public boolean addMobKillMultiplier(EntityType entityType, double multiplier) {
        if (mobKillMultipliers.containsKey(entityType))
            return false;
        mobKillMultipliers.put(entityType, multiplier);
        return true;
    }
    public boolean modifyMobKillMultiplier(EntityType entityType, double multiplier) {
        if (mobKillMultipliers.containsKey(entityType)) {
            mobKillMultipliers.replace(entityType, multiplier);
            return true;
        }
        return false;
    }
    public void removeMobKillMultiplier(EntityType entityType) {
        mobKillMultipliers.remove(entityType);
    }

    public HashMap<Material, Double> getBlockPlaceMultipliers() { if (blockPlaceMultipliers == null) blockPlaceMultipliers = new HashMap<>(); return blockPlaceMultipliers; }
    public void setBlockPlaceMultipliers(HashMap<Material, Double>  blockPlaceMultipliers) { if (blockPlaceMultipliers == null) blockPlaceMultipliers = new HashMap<>();  this.blockPlaceMultipliers = blockPlaceMultipliers; }
    public boolean addBlockPlaceMultiplier(Material type, double multiplier) {
        if (blockPlaceMultipliers.containsKey(type))
            return false;
        blockPlaceMultipliers.put(type, multiplier);
        return true;
    }
    public boolean modifyBlockPlaceMultiplier(Material type, double multiplier) {
        if (blockPlaceMultipliers.containsKey(type)) {
            blockPlaceMultipliers.replace(type,multiplier);
            return true;
        }
        return false;
    }
    public void removeBlockPlaceMultiplier(Material type) {
        blockPlaceMultipliers.remove(type);
    }

    public HashMap<Material, Double> getBlockMineMultipliers() { if (blockMineMultipliers == null) blockMineMultipliers = new HashMap<>(); return blockMineMultipliers; }
    public void setBlockMineMultipliers(HashMap<Material, Double> blockMineMultipliers) { if (blockMineMultipliers == null) blockMineMultipliers = new HashMap<>(); this.blockMineMultipliers = blockMineMultipliers; }
    public HashMap<Material, Double> getHarvestMultipliers() { if (harvestMultipliers == null) harvestMultipliers = new HashMap<>(); return harvestMultipliers; }
    public void setHarvestMultipliers(HashMap<Material, Double> harvestMultipliers) { if (harvestMultipliers == null) harvestMultipliers = new HashMap<>(); this.harvestMultipliers = harvestMultipliers; }
}
