package xyz.acyber.oneLife.DataObjects.SubSettings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import xyz.acyber.oneLife.OneLifePlugin;

import java.util.HashMap;

public class Scoring {

    private double deathMultiplier;
    private double xpMultiplier;
    private double onlineHrMultiplier;
    private double defaultBlocksPlacedMultiplier;
    private double defaultBlocksMinedMultiplier;
    private double defaultHarvestedMultiplier;
    private double defaultCaughtMultiplier;
    private double defaultAchievementMultiplier;

    private HashMap<EntityType, Double> mobKillMultipliers;
    private HashMap<Material, Double> blockPlaceMultipliers;
    private HashMap<Material, Double> blockMineMultipliers;
    private HashMap<Material, Double> HarvestMultipliers;

    @JsonCreator
    public Scoring() { super(); } // Default constructor

    @JsonIgnore
    public Scoring(OneLifePlugin plugin) {
        this.deathMultiplier = -10;
        this.xpMultiplier = 0.0001;
        this.onlineHrMultiplier = 2.5;
        this.defaultBlocksPlacedMultiplier = 0.001;
        this.defaultBlocksMinedMultiplier = 0.001;
        this.defaultHarvestedMultiplier = 0.001;
        this.defaultCaughtMultiplier = 0.001;
        this.defaultAchievementMultiplier = 0.03;

        this.mobKillMultipliers = new HashMap<>();
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("Scoring.MobKills");
        assert section != null;
        for (String key : section.getKeys(false)) {
            mobKillMultipliers.put(EntityType.valueOf(key), section.getDouble(key));
        }
        this.blockPlaceMultipliers = new HashMap<>();
        this.blockMineMultipliers = new HashMap<>();
        double multiplier = 0.00;
        for (Material key : Material.values()) {
            if (key.isBlock()) {
                blockPlaceMultipliers.put(key,multiplier);
                blockMineMultipliers.put(key,multiplier);
            }
        }
        this.HarvestMultipliers = new HashMap<>();
        for (Material key : Material.values()) {
            if (key.isItem()) {
                HarvestMultipliers.put(key,multiplier);
            }
        }
    }

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
    public double getDefaultBlocksPlacedMultiplier() { return defaultBlocksPlacedMultiplier; }
    @JsonSetter
    public void setDefaultBlocksPlacedMultiplier(double defaultBlocksPlacedMultiplier) { this.defaultBlocksPlacedMultiplier = defaultBlocksPlacedMultiplier; }

    @JsonGetter
    public double getDefaultBlocksMinedMultiplier() { return defaultBlocksMinedMultiplier; }
    @JsonSetter
    public void setDefaultBlocksMinedMultiplier(double defaultBlocksMinedMultiplier) { this.defaultBlocksMinedMultiplier = defaultBlocksMinedMultiplier; }

    @JsonGetter
    public double getDefaultHarvestedMultiplier() { return defaultHarvestedMultiplier; }
    @JsonSetter
    public void setDefaultHarvestedMultiplier(double defaultHarvestedMultiplier) { this.defaultHarvestedMultiplier = defaultHarvestedMultiplier; }

    @JsonGetter
    public double getDefaultCaughtMultiplier() { return defaultCaughtMultiplier; }
    @JsonSetter
    public void setDefaultCaughtMultiplier(double defaultCaughtMultiplier) {  this.defaultCaughtMultiplier = defaultCaughtMultiplier; }

    @JsonGetter
    public double getDefaultAchievementMultiplier() { return defaultAchievementMultiplier; }
    @JsonSetter
    public void setDefaultAchievementMultiplier(double defaultAchievementMultiplier) { this.defaultAchievementMultiplier = defaultAchievementMultiplier; }

    @JsonGetter
    public HashMap<EntityType, Double> getMobKillMultipliers() { return mobKillMultipliers; }
    @JsonSetter
    public void setMobKillMultipliers(HashMap<EntityType, Double> mobKillMultipliers) {  this.mobKillMultipliers = mobKillMultipliers; }
    @JsonIgnore
    public boolean addMobKillMultiplier(EntityType type, double multiplier) {
        if (mobKillMultipliers.containsKey(type))
            return false;
        mobKillMultipliers.put(type, multiplier);
        return true;
    }
    @JsonIgnore
    public boolean modifyMobKillMultiplier(EntityType type, double multiplier) {
        if (mobKillMultipliers.containsKey(type)) {
            mobKillMultipliers.replace(type, multiplier);
            return true;
        }
        return false;
    }
    @JsonIgnore
    public void removeMobKillMultiplier(EntityType type) {
        mobKillMultipliers.remove(type);
    }

    @JsonGetter
    public HashMap<Material, Double> getBlockPlaceMultipliers() { return blockPlaceMultipliers; }
    @JsonSetter
    public void setBlockPlaceMultipliers(HashMap<Material, Double>  blockPlaceMultipliers) {  this.blockPlaceMultipliers = blockPlaceMultipliers; }
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
    public HashMap<Material, Double> getBlockMineMultipliers() { return blockMineMultipliers; }
    @JsonSetter
    public void setBlockMineMultipliers(HashMap<Material, Double> blockMineMultipliers) {  this.blockMineMultipliers = blockMineMultipliers; }
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
    public HashMap<Material, Double> getHarvestMultipliers() { return HarvestMultipliers; }
    @JsonSetter
    public void setHarvestMultipliers(HashMap<Material, Double> harvestMultipliers) {  this.HarvestMultipliers = harvestMultipliers; }
    @JsonIgnore
    public boolean addHarvestMultiplier(Material type, double multiplier) {
        if (HarvestMultipliers.containsKey(type)) {
            return false;
        }
        HarvestMultipliers.put(type, multiplier);
        return true;
    }
    @JsonIgnore
    public boolean modifyHarvestMultiplier(Material type, double multiplier) {
        if (HarvestMultipliers.containsKey(type)) {
            HarvestMultipliers.replace(type, multiplier);
            return true;
        }
        return false;
    }
    @JsonIgnore
    public void removeHarvestMultiplier(Material type) {
        HarvestMultipliers.remove(type);
    }
}
