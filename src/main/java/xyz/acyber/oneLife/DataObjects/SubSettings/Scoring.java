package xyz.acyber.oneLife.DataObjects.SubSettings;

import com.fasterxml.jackson.annotation.*;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.event.DataComponentValue;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.legacy.MaterialRerouting;
import org.bukkit.inventory.ItemStack;
import xyz.acyber.oneLife.OneLifePlugin;

import java.util.Arrays;
import java.util.HashMap;

public class Scoring {

    @JsonProperty("deathMultiplier")
    private double deathMultiplier;
    @JsonProperty("xpMultiplier")
    private double xpMultiplier;
    @JsonProperty("onlineHrMultiplier")
    private double onlineHrMultiplier;
    @JsonProperty("defaultBlocksPlacedMultiplier")
    private double defaultBlocksPlacedMultiplier;
    @JsonProperty("defaultBlocksMinedMultiplier")
    private double defaultBlocksMinedMultiplier;
    @JsonProperty("defaultHarvestedMultiplier")
    private double defaultHarvestedMultiplier;
    @JsonProperty("defaultCaughtMultiplier")
    private double defaultCaughtMultiplier;
    @JsonProperty("defaultAchievementMultiplier")
    private double defaultAchievementMultiplier;

    @JsonProperty("mobKillMultipliers")
    private HashMap<String, Double> mobKillMultipliers = new HashMap<>(); // Key is entity type, value is multiplier
    @JsonProperty("blockPlaceMultipliers")
    private HashMap<String, Double> blockPlaceMultipliers = new HashMap<>(); // Key is block type, value is multiplier
    @JsonProperty("blockMineMultipliers")
    private HashMap<String, Double> blockMineMultipliers = new HashMap<>(); // Key is block type, value is multiplier
    @JsonProperty("harvestMultipliers")
    private HashMap<String, Double> harvestMultipliers = new HashMap<>(); // Key is item type, value is multiplier

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
            mobKillMultipliers.put(key, section.getDouble(key));
        }
        this.blockPlaceMultipliers = new HashMap<>();
        this.blockMineMultipliers = new HashMap<>();
        this.harvestMultipliers = new HashMap<>();
        double multiplier = 0.00;
        for (Material key : Material.values()) {
            if (key.isBlock()) {
                blockPlaceMultipliers.put(key.name(),multiplier);
                blockMineMultipliers.put(key.name(),multiplier);
            } else if (key.isItem()) {
                harvestMultipliers.put(key.name(),multiplier);
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
    public HashMap<String, Double> getMobKillMultipliers() { if (mobKillMultipliers == null) mobKillMultipliers = new HashMap<>(); return mobKillMultipliers; }
    @JsonSetter
    public void setMobKillMultipliers(HashMap<String, Double> mobKillMultipliers) { if (mobKillMultipliers == null) mobKillMultipliers = new HashMap<>(); this.mobKillMultipliers = mobKillMultipliers; }
    @JsonIgnore
    public boolean addMobKillMultiplier(String entityType, double multiplier) {
        if (mobKillMultipliers.containsKey(entityType))
            return false;
        mobKillMultipliers.put(entityType, multiplier);
        return true;
    }
    @JsonIgnore
    public boolean modifyMobKillMultiplier(String entityType, double multiplier) {
        if (mobKillMultipliers.containsKey(entityType)) {
            mobKillMultipliers.replace(entityType, multiplier);
            return true;
        }
        return false;
    }
    @JsonIgnore
    public void removeMobKillMultiplier(String entityType) {
        mobKillMultipliers.remove(entityType);
    }

    @JsonGetter
    public HashMap<String, Double> getBlockPlaceMultipliers() { if (blockPlaceMultipliers == null) blockPlaceMultipliers = new HashMap<>(); return blockPlaceMultipliers; }
    @JsonSetter
    public void setBlockPlaceMultipliers(HashMap<String, Double>  blockPlaceMultipliers) { if (blockPlaceMultipliers == null) blockPlaceMultipliers = new HashMap<>();  this.blockPlaceMultipliers = blockPlaceMultipliers; }
    @JsonIgnore
    public boolean addBlockPlaceMultiplier(String type, double multiplier) {
        if (blockPlaceMultipliers.containsKey(type))
            return false;
        blockPlaceMultipliers.put(type, multiplier);
        return true;
    }
    @JsonIgnore
    public boolean modifyBlockPlaceMultiplier(String type, double multiplier) {
        if (blockPlaceMultipliers.containsKey(type)) {
            blockPlaceMultipliers.replace(type,multiplier);
            return true;
        }
        return false;
    }
    @JsonIgnore
    public void removeBlockPlaceMultiplier(String type) {
        blockPlaceMultipliers.remove(type);
    }

    @JsonGetter
    public HashMap<String, Double> getBlockMineMultipliers() { if (blockMineMultipliers == null) blockMineMultipliers = new HashMap<>(); return blockMineMultipliers; }
    @JsonSetter
    public void setBlockMineMultipliers(HashMap<String, Double> blockMineMultipliers) { if (blockMineMultipliers == null) blockMineMultipliers = new HashMap<>(); this.blockMineMultipliers = blockMineMultipliers; }
    @JsonIgnore
    public boolean addBlockMineMultiplier(String type, double multiplier) {
        if (blockMineMultipliers.containsKey(type)) {
            return false;
        }
        blockMineMultipliers.put(type, multiplier);
        return true;
    }
    @JsonIgnore
    public boolean modifyBlockMineMultiplier(String type, double multiplier) {
        if (blockMineMultipliers.containsKey(type)) {
            blockMineMultipliers.replace(type,multiplier);
            return true;
        }
        return false;
    }
    @JsonIgnore
    public void removeBlockMineMultiplier(String type) {
        blockMineMultipliers.remove(type);
    }

    @JsonGetter
    public HashMap<String, Double> getHarvestMultipliers() { if (harvestMultipliers == null) harvestMultipliers = new HashMap<>(); return harvestMultipliers; }
    @JsonSetter
    public void setHarvestMultipliers(HashMap<String, Double> harvestMultipliers) { if (harvestMultipliers == null) harvestMultipliers = new HashMap<>();  this.harvestMultipliers = harvestMultipliers; }
    @JsonIgnore
    public boolean addHarvestMultiplier(String type, double multiplier) {
        if (harvestMultipliers.containsKey(type)) {
            return false;
        }
        harvestMultipliers.put(type, multiplier);
        return true;
    }
    @JsonIgnore
    public boolean modifyHarvestMultiplier(String type, double multiplier) {
        if (harvestMultipliers.containsKey(type)) {
            harvestMultipliers.replace(type, multiplier);
            return true;
        }
        return false;
    }
    @JsonIgnore
    public void removeHarvestMultiplier(String type) {harvestMultipliers.remove(type);
    }
}
