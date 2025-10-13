package xyz.acyber.oneLife.DataObjects;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.acyber.oneLife.DataObjects.SubScoreData.MaterialInteractions;
import xyz.acyber.oneLife.DataObjects.SubScoreData.MobsKilled;
import xyz.acyber.oneLife.DataObjects.SubSettings.Team;
import xyz.acyber.oneLife.OneLifePlugin;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.UUID;

public class ScoreData {

    @JsonIgnore
    private OneLifePlugin plugin = JavaPlugin.getPlugin(OneLifePlugin.class);

    @JsonProperty("uuid")
    private UUID uuid = null;
    @JsonProperty("playerName")
    private String playerName = null;
    @JsonProperty("team")
    private Team team = null;
    @JsonProperty("livesBuyBack")
    private int livesBuyBack = 0;
    @JsonProperty("deaths")
    private HashMap<String, Double> deaths = new HashMap<>();
    @JsonProperty("xp")
    private HashMap<String, Double> xp = new HashMap<>();
    @JsonProperty("onlineHr")
    private HashMap<String, Double> onlineHr = new HashMap<>();
    @JsonProperty("blocksPlaced")
    private HashMap<String, Integer> blocksPlaced = new HashMap<>();
    @JsonProperty("blocksMined")
    private HashMap<String, Integer> blocksMined = new HashMap<>();
    @JsonProperty("harvested")
    private HashMap<String, Integer> harvested = new HashMap<>();
    @JsonProperty("caught")
    private HashMap<String, Integer> caught = new HashMap<>();
    @JsonProperty("achievements")
    private HashMap<String, Integer> achievements = new HashMap<>();
    @JsonProperty("typeMobsKilled")
    private HashMap<String, MobsKilled> typeMobsKilled = new HashMap<>(); // key is entity type, value is MobsKilled object
    @JsonProperty("typeBlocksMined")
    private HashMap<Material, MaterialInteractions> typeBlocksMined = new HashMap<>();
    @JsonProperty("typeBlocksPlaced")
    private HashMap<Material, MaterialInteractions> typeBlocksPlaced = new HashMap<>();
    @JsonProperty("typeBlocksHarvested")
    private HashMap<Material, MaterialInteractions> typeItemsHarvested = new HashMap<>();

    private double afkPointsOffset = 0;


    public ScoreData() {
        super();
    }
    @JsonIgnore
    public ScoreData(OneLifePlugin plugin) {
        this.plugin = plugin;
        this.uuid = null;
        this.playerName = null;
        this.team = null;
        this.deaths = new HashMap<>();
        this.xp = new HashMap<>();
        this.onlineHr = new HashMap<>();
        this.blocksPlaced = new HashMap<>();
        this.blocksMined = new HashMap<>();
        this.harvested = new HashMap<>();
        this.caught = new HashMap<>();
        this.achievements = new HashMap<>();
        this.typeMobsKilled = new HashMap<>();
        this.typeBlocksMined = new HashMap<>();
        this.typeBlocksPlaced = new HashMap<>();
        this.typeItemsHarvested = new HashMap<>();
    }

    @JsonIgnore
    public ScoreData(OneLifePlugin plugin, OfflinePlayer player, Team team) {
        this.plugin = plugin;
        this.uuid = player.getUniqueId();
        this.playerName = player.getName();
        this.team = team;
        this.deaths = new HashMap<>();
        this.xp = new HashMap<>();
        this.onlineHr = new HashMap<>();
        this.blocksPlaced = new HashMap<>();
        this.blocksMined = new HashMap<>();
        this.harvested = new HashMap<>();
        this.caught = new HashMap<>();
        this.achievements = new HashMap<>();
        this.typeMobsKilled = new HashMap<>();
        this.typeBlocksMined = new HashMap<>();
        this.typeBlocksPlaced = new HashMap<>();
        this.typeItemsHarvested = new HashMap<>();
    }

    @JsonIgnore
    public void setPlayer(OfflinePlayer offlinePlayer) {
        this.uuid = offlinePlayer.getUniqueId();
        this.playerName = offlinePlayer.getName();
    }

    @JsonGetter
    public UUID getUUID() { return uuid; }
    @JsonSetter
    public void setUUID(UUID uuid) { this.uuid = uuid; }

    @JsonGetter
    public String getPlayerName() { return playerName; }
    @JsonSetter
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    @JsonGetter
    public Team getTeam() { return team; }
    @JsonSetter
    public void setTeam(Team team) { this.team = team; }

    @JsonGetter
    public int getLivesBuyBack() { return livesBuyBack; }
    @JsonSetter
    public void setLivesBuyBack(int livesBuyBack) { this.livesBuyBack = livesBuyBack; }

    @JsonGetter
    public HashMap<String, Double> getDeaths() { return deaths; }
    @JsonSetter
    public void setDeaths(HashMap<String, Double> deaths) {  this.deaths = deaths; }

    @JsonGetter
    public HashMap<String, Double> getXp() { return xp; }
    @JsonSetter
    public void setXp(HashMap<String, Double> xp) { this.xp = xp; }

    @JsonGetter
    public HashMap<String, Double> getOnlineHr() { return onlineHr; }
    @JsonSetter
    public void setOnlineHr(HashMap<String, Double> onlineHr) { this.onlineHr = onlineHr; }

    @JsonGetter
    public HashMap<String, Integer> getBlocksPlaced() { return blocksPlaced; }
    @JsonSetter
    public void setBlocksPlaced(HashMap<String, Integer> blocksPlaced) {  this.blocksPlaced = blocksPlaced; }

    @JsonGetter
    public HashMap<String, Integer> getBlocksMined() { return blocksMined; }
    @JsonSetter
    public void setBlocksMined(HashMap<String, Integer> blocksMined) {  this.blocksMined = blocksMined; }

    @JsonGetter
    public HashMap<String, Integer> getHarvested() { return harvested; }
    @JsonSetter
    public void setHarvested(HashMap<String, Integer> harvested) {  this.harvested = harvested; }

    @JsonGetter
    public HashMap<String, Integer> getCaught() { return caught; }
    @JsonSetter
    public void setCaught(HashMap<String, Integer> caught) {  this.caught = caught; }

    @JsonGetter
    public HashMap<String, Integer> getAchievements() { return achievements; }
    @JsonSetter
    public void setAchievements(HashMap<String, Integer> achievements) { this.achievements = achievements; }

    @JsonGetter
    public HashMap<String, MobsKilled> getTypeMobs() { return typeMobsKilled; }
    @JsonSetter
    public void setTypeMobs(HashMap<String, MobsKilled> typeMobs) { this.typeMobsKilled = typeMobs; }

    @JsonGetter
    public HashMap<Material, MaterialInteractions> getTypeBlocksMined() { return typeBlocksMined; }
    @JsonSetter
    public void setTypeBlocksMined(HashMap<Material, MaterialInteractions> typeBlocksMined) { this.typeBlocksMined = typeBlocksMined; }

    @JsonGetter
    public HashMap<Material, MaterialInteractions> getTypeBlocksPlaced() { return typeBlocksPlaced; }
    @JsonSetter
    public void setTypeBlocksPlaced(HashMap<Material, MaterialInteractions> typeBlocksPlaced) { this.typeBlocksPlaced = typeBlocksPlaced; }

    @JsonGetter
    public HashMap<Material, MaterialInteractions> getTypeItemsHarvested() { return typeItemsHarvested; }
    @JsonSetter
    public void setTypeItemsHarvested(HashMap<Material, MaterialInteractions> typeItemsHarvested) { this.typeItemsHarvested = typeItemsHarvested; }

    @JsonIgnore
    public double getDeathPoints() {
        double deathPoints = 0;
        for (String key : deaths.keySet()) {
            double multiplier = plugin.settings.getScoring().get(key).getDeathMultiplier();
            deathPoints += deaths.get(key) * multiplier;
        }
        return deathPoints;
    }
    @JsonIgnore
    public double getXpPoints() {
        double xpPoints = 0;
        for (String key : xp.keySet()) {
            if (key.equals("AFK")) {
                double multiplier = plugin.settings.getScoring().get(GameMode.SURVIVAL.name()).getXpMultiplier() * plugin.settings.getAFKMultiplier();
                afkPointsOffset += xp.get(key) * multiplier;
            } else {
                double multiplier = plugin.settings.getScoring().get(GameMode.valueOf(key).name()).getXpMultiplier();
                xpPoints += xp.get(key) * multiplier;
            }
        }
        return  xpPoints;
    }
    @JsonIgnore
    public double getOnlineHrPoints() {
        double onlineHrPoints = 0;
        for (String key : onlineHr.keySet()) {
            if (key.equals("AFK")) {
                double multiplier = plugin.settings.getScoring().get(GameMode.SURVIVAL.name()).getXpMultiplier() * plugin.settings.getAFKMultiplier();
                afkPointsOffset += onlineHr.get(key) * multiplier;
            } else {
                double multiplier = plugin.settings.getScoring().get(GameMode.valueOf(key).name()).getXpMultiplier();
                onlineHrPoints += onlineHr.get(key) * multiplier;
            }
        }
        return  onlineHrPoints;
    }
    @JsonIgnore
    public double getLivesBuyBackPoints() {
        return livesBuyBack * plugin.settings.getLivesBuyBackMultiplier();
    }
    @JsonIgnore
    public double getDefaultBlocksPlacedPoints() {
        double defaultBlocksPlacedPoints = 0;
        for (String key : blocksPlaced.keySet()) {
            if (key.equals("AFK")) {
                double multiplier = plugin.settings.getScoring().get(GameMode.SURVIVAL.name()).getXpMultiplier() * plugin.settings.getAFKMultiplier();
                afkPointsOffset += blocksPlaced.get(key) * multiplier;
            } else {
                double multiplier = plugin.settings.getScoring().get(GameMode.valueOf(key).name()).getXpMultiplier();
                defaultBlocksPlacedPoints += blocksPlaced.get(key) * multiplier;
            }
        }
        return defaultBlocksPlacedPoints;
    }
    @JsonIgnore
    public double getDefaultBlocksMinedPoints() {
        double defaultBlocksMinedPoints = 0;
        for (String key : blocksMined.keySet()) {
            if (key.equals("AFK")) {
                double multiplier = plugin.settings.getScoring().get(GameMode.SURVIVAL.name()).getXpMultiplier() * plugin.settings.getAFKMultiplier();
                afkPointsOffset += blocksMined.get(key) * multiplier;
            } else {
                double mulitpler = plugin.settings.getScoring().get(GameMode.valueOf(key).name()).getXpMultiplier();
                defaultBlocksMinedPoints += blocksMined.get(key) * mulitpler;
            }
        }
        return defaultBlocksMinedPoints;
    }
    @JsonIgnore
    public double getDefaultHarvestedPoints() {
        double defaultHarvestedPoints = 0;
        for (String key : harvested.keySet()) {
            if (key.equals("AFK")) {
                double multiplier = plugin.settings.getScoring().get(GameMode.SURVIVAL.name()).getXpMultiplier()  * plugin.settings.getAFKMultiplier();
                afkPointsOffset += harvested.get(key) * multiplier;
            } else {
                double mulitpler = plugin.settings.getScoring().get(GameMode.valueOf(key).name()).getXpMultiplier();
                defaultHarvestedPoints += harvested.get(key) * mulitpler;
            }
        }
        return defaultHarvestedPoints;
    }
    @JsonIgnore
    public double getDefaultCaughtPoints() {
        double defaultCaughtPoints = 0;
        for (String key : caught.keySet()) {
            if (key.equals("AFK")) {
                double multiplier = plugin.settings.getScoring().get(GameMode.SURVIVAL.name()).getXpMultiplier() * plugin.settings.getAFKMultiplier();
                afkPointsOffset += caught.get(key) * multiplier;
            } else {
                double multiplier = plugin.settings.getScoring().get(GameMode.valueOf(key).name()).getXpMultiplier();
                defaultCaughtPoints += caught.get(key) * multiplier;
            }
        }
        return defaultCaughtPoints;
    }
    @JsonIgnore
    public double getDefaultAchievementPoints() {
        double defaultAchievementPoints = 0;
        for (String key : achievements.keySet()) {
            if (key.equals("AFK")) {
                double multiplier = plugin.settings.getScoring().get(GameMode.SURVIVAL.name()).getXpMultiplier() * plugin.settings.getAFKMultiplier();
                afkPointsOffset += achievements.get(key) * multiplier;
            } else {
                double multiplier = plugin.settings.getScoring().get(GameMode.valueOf(key).name()).getXpMultiplier();
                defaultAchievementPoints += achievements.get(key) * multiplier;
            }
        }
        return defaultAchievementPoints;
    }
    @JsonIgnore
    public double getTypeMobTotalPoints() {
        double typeMobTotalPoints = 0;
        for (String mobEntityType : typeMobsKilled.keySet()) {
            for (String key : typeMobsKilled.get(mobEntityType).getCount().keySet()) {
                if (key.equals("AFK")) {
                    double multiplier = plugin.settings.getScoring().get(GameMode.SURVIVAL.name()).getMobKillMultipliers().get(mobEntityType) * plugin.settings.getAFKMultiplier();
                    afkPointsOffset += typeMobsKilled.get(mobEntityType).getCount().get(key) * multiplier;
                } else {
                    double multiplier = plugin.settings.getScoring().get(GameMode.valueOf(key).name()).getMobKillMultipliers().get(mobEntityType);
                    typeMobTotalPoints += typeMobsKilled.get(mobEntityType).getCount().get(key) * multiplier;
                }
            }
        }
        return typeMobTotalPoints;
    }
    @JsonIgnore
    public double getTypeBlocksMinedTotalPoints() {
        double typeBlocksMinedTotalPoints = 0;
        for (Material material : typeBlocksMined.keySet()) {
            for (String key : typeBlocksMined.get(material).getCount().keySet()) {
                if (key.equals("AFK")) {
                    double multiplier = plugin.settings.getScoring().get(GameMode.SURVIVAL.name()).getBlockMineMultipliers().get(material) * plugin.settings.getAFKMultiplier();
                    afkPointsOffset += typeBlocksMined.get(material).getCount().get(key) * multiplier;
                } else {
                    double multiplier = plugin.settings.getScoring().get(GameMode.valueOf(key).name()).getBlockMineMultipliers().get(material);
                    typeBlocksMinedTotalPoints += typeBlocksMined.get(material).getCount().get(key) * multiplier;
                }
            }
        }
        return typeBlocksMinedTotalPoints;
    }
    @JsonIgnore
    public double getTypeBlocksPlacedTotalPoints() {
        double typeBlocksPlacedTotalPoints = 0;
        for (Material material : typeBlocksPlaced.keySet()) {
            for (String key : typeBlocksPlaced.get(material).getCount().keySet()) {
                if (key.equals("AFK")) {
                    double multiplier = plugin.settings.getScoring().get(GameMode.SURVIVAL.name()).getBlockPlaceMultipliers().get(material) * plugin.settings.getAFKMultiplier();
                    afkPointsOffset += typeBlocksPlaced.get(material).getCount().get(key) * multiplier;
                } else {
                    double multiplier = plugin.settings.getScoring().get(GameMode.valueOf(key).name()).getBlockPlaceMultipliers().get(material);
                    typeBlocksPlacedTotalPoints += typeBlocksPlaced.get(material).getCount().get(key) * multiplier;
                }
            }
        }
        return typeBlocksPlacedTotalPoints;
    }
    @JsonIgnore
    public double getTypeItemsHarvestedTotalPoints() {
        double typeItemsHarvestedTotalPoints = 0;
        for (Material material : typeItemsHarvested.keySet()) {
            for (String key : typeItemsHarvested.get(material).getCount().keySet()) {
                if (key.equals("AFK")) {
                    double multiplier = plugin.settings.getScoring().get(GameMode.SURVIVAL.name()).getHarvestMultipliers().get(material) * plugin.settings.getAFKMultiplier();
                    afkPointsOffset += typeItemsHarvested.get(material).getCount().get(key) * multiplier;
                } else {
                    double multiplier = plugin.settings.getScoring().get(GameMode.valueOf(key).name()).getHarvestMultipliers().get(material);
                    typeItemsHarvestedTotalPoints += typeItemsHarvested.get(material).getCount().get(key) * multiplier;
                }
            }
        }
        return typeItemsHarvestedTotalPoints;

    }

    @JsonIgnore
    public double getAFKPointsOffset() { return afkPointsOffset; }

    @JsonIgnore
    public double totalPoints() {
        return round(getDeathPoints() + getAFKPointsOffset() + getLivesBuyBackPoints() + getXpPoints() + getOnlineHrPoints() + getDefaultBlocksPlacedPoints() +
                getDefaultBlocksMinedPoints() + getDefaultHarvestedPoints() + getDefaultCaughtPoints() + getDefaultAchievementPoints() +
                getTypeMobTotalPoints() + getTypeBlocksMinedTotalPoints() + getTypeBlocksPlacedTotalPoints() + getTypeItemsHarvestedTotalPoints(), 4);
    }

    @JsonIgnore
    public double onlineHr() {
        double hours = 0;
        for (String key : onlineHr.keySet()) {
            hours += onlineHr.get(key);
        }
        return round(hours,2);
    }

    @JsonIgnore
    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}

