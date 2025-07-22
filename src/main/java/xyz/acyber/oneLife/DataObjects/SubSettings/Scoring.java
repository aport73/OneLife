package xyz.acyber.oneLife.DataObjects.SubSettings;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import xyz.acyber.oneLife.Main;

import java.util.Arrays;
import java.util.HashMap;

public class Scoring {

    private double deathMultiplier;
    private double xpMultiplier;
    private double onlineHrMulitplier;
    private double defaultBlocksPlacedMultiplier;
    private double defaultBlocksMinedMultiplier;
    private double defaultHarvestedMultiplier;
    private double defaultCaughtMultiplier;
    private double defaultAchievementMultiplier;

    private HashMap<EntityType, Double> mobKillMultipliers;
    private HashMap<Material, Double> blockPlaceMultipliers;
    private HashMap<Material, Double> blockMineMultipliers;
    private HashMap<Material, Double> HarvestMultipliers;

    public Scoring(Main plugin) {
        this.deathMultiplier = -10;
        this.xpMultiplier = 0.0001;
        this.onlineHrMulitplier = 2.5;
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
        for (Material key : Material.values()) {
            if (key.isBlock()) {
                blockPlaceMultipliers.put(key,0.0);
                blockMineMultipliers.put(key,0.0);
            }
        }
        this.HarvestMultipliers = new HashMap<>();
        for (Material key : Material.values()) {
            if (key.isItem()) {
                HarvestMultipliers.put(key,0.0);
            }
        }
    }

    public Scoring(double deathMultiplier, double xpMultiplier, double onlineHrMulitplier,
                   double defaultBlocksPlacedMultiplier, double defaultBlocksMinedMultiplier,
                   double defaultHarvestedMultiplier, double defaultCaughtMultiplier,
                   double defaultAchievementMultiplier) {
        this.deathMultiplier = deathMultiplier;
        this.xpMultiplier = xpMultiplier;
        this.onlineHrMulitplier = onlineHrMulitplier;
        this.defaultBlocksPlacedMultiplier = defaultBlocksPlacedMultiplier;
        this.defaultBlocksMinedMultiplier = defaultBlocksMinedMultiplier;
        this.defaultHarvestedMultiplier = defaultHarvestedMultiplier;
        this.defaultCaughtMultiplier = defaultCaughtMultiplier;
        this.defaultAchievementMultiplier = defaultAchievementMultiplier;

        this.mobKillMultipliers = new HashMap<>();
        this.blockPlaceMultipliers = new HashMap<>();
        this.blockMineMultipliers = new HashMap<>();
        this.HarvestMultipliers = new HashMap<>();
    }

    public double getDeathMultiplier() { return deathMultiplier; }
    public void setDeathMultiplier(double deathMultiplier) {  this.deathMultiplier = deathMultiplier; }

    public double getXpMultiplier() { return xpMultiplier; }
    public void setXpMultiplier(double xpMultiplier) {  this.xpMultiplier = xpMultiplier; }

    public double getOnlineHrMultiplier() { return onlineHrMulitplier; }
    public void setOnlineHrMulitplier(double onlineHrMulitplier) {  this.onlineHrMulitplier = onlineHrMulitplier; }

    public double getDefaultBlocksPlacedMultiplier() { return defaultBlocksPlacedMultiplier; }
    public void setDefaultBlocksPlacedMultiplier(double defaultBlocksPlacedMultiplier) { this.defaultBlocksPlacedMultiplier = defaultBlocksPlacedMultiplier; }

    public double getDefaultBlocksMinedMultiplier() { return defaultBlocksMinedMultiplier; }
    public void setDefaultBlocksMinedMultiplier(double defaultBlocksMinedMultiplier) { this.defaultBlocksMinedMultiplier = defaultBlocksMinedMultiplier; }

    public double getDefaultHarvestedMultiplier() { return defaultHarvestedMultiplier; }
    public void setDefaultHarvestedMultiplier(double defaultHarvestedMultiplier) { this.defaultHarvestedMultiplier = defaultHarvestedMultiplier; }

    public double getDefaultCaughtMultiplier() { return defaultCaughtMultiplier; }
    public void setDefaultCaughtMultiplier(double defaultCaughtMultiplier) {  this.defaultCaughtMultiplier = defaultCaughtMultiplier; }

    public double getDefaultAchievementMultiplier() { return defaultAchievementMultiplier; }
    public void setDefaultAchievementMultiplier(double defaultAchievementMultiplier) { this.defaultAchievementMultiplier = defaultAchievementMultiplier; }

    public HashMap<EntityType, Double> getMobKillMultipliers() { return mobKillMultipliers; }
    public void setMobKillMultipliers(HashMap<EntityType, Double> mobKillMultipliers) {  this.mobKillMultipliers = mobKillMultipliers; }
    public boolean addMobKillMultiplier(EntityType type, double multiplier) {
        if (mobKillMultipliers.containsKey(type))
            return false;
        mobKillMultipliers.put(type, multiplier);
        return true;
    }
    public boolean modifyMobKillMultiplier(EntityType type, double multiplier) {
        if (mobKillMultipliers.containsKey(type)) {
            mobKillMultipliers.replace(type, multiplier);
            return true;
        }
        return false;
    }
    public void removeMobKillMultiplier(EntityType type) {
        mobKillMultipliers.remove(type);
    }


    public HashMap<Material, Double> getBlockPlaceMultipliers() { return blockPlaceMultipliers; }
    public void setBlockPlaceMultipliers(HashMap<Material, Double>  blockPlaceMultipliers) {  this.blockPlaceMultipliers = blockPlaceMultipliers; }
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

    public HashMap<Material, Double> getBlockMineMultipliers() { return blockMineMultipliers; }
    public void setBlockMineMultipliers(HashMap<Material, Double> blockMineMultipliers) {  this.blockMineMultipliers = blockMineMultipliers; }
    public boolean addBlockMineMultiplier(Material type, double multiplier) {
        if (blockMineMultipliers.containsKey(type)) {
            return false;
        }
        blockMineMultipliers.put(type, multiplier);
        return true;
    }
    public boolean modifyBlockMineMultiplier(Material type, double multiplier) {
        if (blockMineMultipliers.containsKey(type)) {
            blockMineMultipliers.replace(type,multiplier);
            return true;
        }
        return false;
    }
    public void removeBlockMineMultiplier(Material type) {
        blockMineMultipliers.remove(type);
    }

    public HashMap<Material, Double> getHarvestMultipliers() { return HarvestMultipliers; }
    public void setHarvestMultipliers(HashMap<Material, Double> harvestMultipliers) {  this.HarvestMultipliers = harvestMultipliers; }
    public boolean addHarvestMultiplier(Material type, double multiplier) {
        if (HarvestMultipliers.containsKey(type)) {
            return false;
        }
        HarvestMultipliers.put(type, multiplier);
        return true;
    }
    public boolean modifyHarvestMultiplier(Material type, double multiplier) {
        if (HarvestMultipliers.containsKey(type)) {
            HarvestMultipliers.replace(type, multiplier);
            return true;
        }
        return false;
    }
    public void removeHarvestMultiplier(Material type) {
        HarvestMultipliers.remove(type);
    }
}
