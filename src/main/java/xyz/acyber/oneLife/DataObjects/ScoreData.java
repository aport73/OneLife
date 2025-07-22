package xyz.acyber.oneLife.DataObjects;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import xyz.acyber.oneLife.DataObjects.SubScoreData.MaterialInteractions;
import xyz.acyber.oneLife.DataObjects.SubScoreData.MobsKilled;
import xyz.acyber.oneLife.DataObjects.SubSettings.Team;
import xyz.acyber.oneLife.Main;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

public class ScoreData {

    private Main plugin;

    private OfflinePlayer player;
    private Team team;
    private int livesBuyBack;
    private HashMap<GameMode, Double> deaths;
    private HashMap<String, Double> xp;
    private HashMap<String, Double> onlineHr;
    private HashMap<String, Integer> blocksPlaced;
    private HashMap<String, Integer> blocksMined;
    private HashMap<String, Integer> harvested;
    private HashMap<String, Integer> caught;
    private HashMap<String, Integer> achievements;

    private HashMap<EntityType, MobsKilled> typeMobs;
    private HashMap<Material, MaterialInteractions> typeBlocksMined;
    private HashMap<Material, MaterialInteractions> typeBlocksPlaced;
    private HashMap<Material, MaterialInteractions> typeItemsHarvested;

    public ScoreData(Main plugin, OfflinePlayer player, Team team) {
        this.plugin = plugin;
        this.player = player;
        this.team = team;
        this.deaths = new HashMap<>();
        this.xp = new HashMap<>();
        this.onlineHr = new HashMap<>();
        this.blocksPlaced = new HashMap<>();
        this.blocksMined = new HashMap<>();
        this.harvested = new HashMap<>();
        this.caught = new HashMap<>();
        this.achievements = new HashMap<>();
        this.typeMobs = new HashMap<>();
        this.typeBlocksMined = new HashMap<>();
        this.typeBlocksPlaced = new HashMap<>();
        this.typeItemsHarvested = new HashMap<>();
    }

    public OfflinePlayer getPlayer() { return player; }
    public void setPlayer(OfflinePlayer offlinePlayer) { this.player = offlinePlayer; }

    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }

    public int getLivesBuyBack() { return livesBuyBack; }
    public void setLivesBuyBack(int livesBuyBack) { this.livesBuyBack = livesBuyBack; }

    public HashMap<GameMode, Double> getDeaths() { return deaths; }
    public void setDeaths(HashMap<GameMode, Double> deaths) {  this.deaths = deaths; }

    public HashMap<String, Double> getXp() { return xp; }
    public void setXp(HashMap<String, Double> xp) { this.xp = xp; }

    public HashMap<String, Double> getOnlineHr() { return onlineHr; }
    public void setOnlineHr(HashMap<String, Double> onlineHr) { this.onlineHr = onlineHr; }

    public HashMap<String, Integer> getBlocksPlaced() { return blocksPlaced; }
    public void setBlocksPlaced(HashMap<String, Integer> blocksPlaced) {  this.blocksPlaced = blocksPlaced; }

    public HashMap<String, Integer> getBlocksMined() { return blocksMined; }
    public void setBlocksMined(HashMap<String, Integer> blocksMined) {  this.blocksMined = blocksMined; }

    public HashMap<String, Integer> getHarvested() { return harvested; }
    public void setHarvested(HashMap<String, Integer> harvested) {  this.harvested = harvested; }

    public HashMap<String, Integer> getCaught() { return caught; }
    public void setCaught(HashMap<String, Integer> caught) {  this.caught = caught; }

    public HashMap<String, Integer> getAchievements() { return achievements; }
    public void setAchievements(HashMap<String, Integer> achievements) { this.achievements = achievements; }

    public HashMap<EntityType, MobsKilled> getTypeMobs() { return typeMobs; }
    public void setTypeMobs(HashMap<EntityType, MobsKilled> typeMobs) { this.typeMobs = typeMobs; }

    public HashMap<Material, MaterialInteractions> getTypeBlocksMined() { return typeBlocksMined; }
    public void setTypeBlocksMined(HashMap<Material, MaterialInteractions> typeBlocksMined) { this.typeBlocksMined = typeBlocksMined; }

    public HashMap<Material, MaterialInteractions> getTypeBlocksPlaced() { return typeBlocksPlaced; }
    public void setTypeBlocksPlaced(HashMap<Material, MaterialInteractions> typeBlocksPlaced) { this.typeBlocksPlaced = typeBlocksPlaced; }

    public HashMap<Material, MaterialInteractions> getTypeItemsHarvested() { return typeItemsHarvested; }
    public void setTypeItemsHarvested(HashMap<Material, MaterialInteractions> typeItemsHarvested) { this.typeItemsHarvested = typeItemsHarvested; }

    private double afkPointsOffset;

    public double getDeathPoints() {
        double deathPoints = 0;
        for (GameMode key : deaths.keySet()) {
            double multiplier = plugin.settings.getScoring().get(key).getDeathMultiplier();
            deathPoints += deaths.get(key) * multiplier;
        }
        return deathPoints;
    };
    public double getXpPoints() {
        double xpPoints = 0;
        for (String key : xp.keySet()) {
            if (key.equals("AFK")) {
                double multiplier = plugin.settings.getScoring().get(GameMode.SURVIVAL).getXpMultiplier() * plugin.settings.getAFKMultiplier();
                afkPointsOffset += xp.get(key) * multiplier;
            } else {
                double multiplier = plugin.settings.getScoring().get(GameMode.valueOf(key)).getXpMultiplier();
                xpPoints += xp.get(key) * multiplier;
            }
        }
        return  xpPoints;
    }
    public double getOnlineHrPoints() {
        double onlineHrPoints = 0;
        for (String key : onlineHr.keySet()) {
            if (key.equals("AFK")) {
                double multiplier = plugin.settings.getScoring().get(GameMode.SURVIVAL).getXpMultiplier() * plugin.settings.getAFKMultiplier();
                afkPointsOffset += onlineHr.get(key) * multiplier;
            } else {
                double multiplier = plugin.settings.getScoring().get(GameMode.valueOf(key)).getXpMultiplier();
                onlineHrPoints += onlineHr.get(key) * multiplier;
            }
        }
        return  onlineHrPoints;
    }
    public double getLivesBuyBackPoints() {
        return livesBuyBack * plugin.settings.getLivesBuyBackMulitplier();
    }
    public double getDefaultBlocksPlacedPoints() {
        double defaultBlocksPlacedPoints = 0;
        for (String key : blocksPlaced.keySet()) {
            if (key.equals("AFK")) {
                double multiplier = plugin.settings.getScoring().get(GameMode.SURVIVAL).getXpMultiplier() * plugin.settings.getAFKMultiplier();
                afkPointsOffset += blocksPlaced.get(key) * multiplier;
            } else {
                double multiplier = plugin.settings.getScoring().get(GameMode.valueOf(key)).getXpMultiplier();
                defaultBlocksPlacedPoints += blocksPlaced.get(key) * multiplier;
            }
        }
        return defaultBlocksPlacedPoints;
    }
    public double getDefaultBlocksMinedPoints() {
        double defaultBlocksMinedPoints = 0;
        for (String key : blocksMined.keySet()) {
            if (key.equals("AFK")) {
                double multiplier = plugin.settings.getScoring().get(GameMode.SURVIVAL).getXpMultiplier() * plugin.settings.getAFKMultiplier();
                afkPointsOffset += blocksMined.get(key) * multiplier;
            } else {
                double mulitpler = plugin.settings.getScoring().get(GameMode.valueOf(key)).getXpMultiplier();
                defaultBlocksMinedPoints += blocksMined.get(key) * mulitpler;
            }
        }
        return defaultBlocksMinedPoints;
    }
    public double getDefaultHarvestedPoints() {
        double defaultHarvestedPoints = 0;
        for (String key : harvested.keySet()) {
            if (key.equals("AFK")) {
                double multiplier = plugin.settings.getScoring().get(GameMode.SURVIVAL).getXpMultiplier()  * plugin.settings.getAFKMultiplier();
                afkPointsOffset += harvested.get(key) * multiplier;
            } else {
                double mulitpler = plugin.settings.getScoring().get(GameMode.valueOf(key)).getXpMultiplier();
                defaultHarvestedPoints += harvested.get(key) * mulitpler;
            }
        }
        return defaultHarvestedPoints;
    }
    public double getDefaultCaughtPoints() {
        double defaultCaughtPoints = 0;
        for (String key : caught.keySet()) {
            if (key.equals("AFK")) {
                double multiplier = plugin.settings.getScoring().get(GameMode.SURVIVAL).getXpMultiplier() * plugin.settings.getAFKMultiplier();
                afkPointsOffset += caught.get(key) * multiplier;
            } else {
                double multiplier = plugin.settings.getScoring().get(GameMode.valueOf(key)).getXpMultiplier();
                defaultCaughtPoints += caught.get(key) * multiplier;
            }
        }
        return defaultCaughtPoints;
    }
    public double getDefaultAchievementPoints() {
        double defaultAchievementPoints = 0;
        for (String key : achievements.keySet()) {
            if (key.equals("AFK")) {
                double multiplier = plugin.settings.getScoring().get(GameMode.SURVIVAL).getXpMultiplier() * plugin.settings.getAFKMultiplier();
                afkPointsOffset += achievements.get(key) * multiplier;
            } else {
                double multiplier = plugin.settings.getScoring().get(GameMode.valueOf(key)).getXpMultiplier();
                defaultAchievementPoints += achievements.get(key) * multiplier;
            }
        }
        return defaultAchievementPoints;
    }
    public double getTypeMobTotalPoints() {
        double typeMobTotalPoints = 0;
        for (EntityType mob : typeMobs.keySet()) {
            for (String key : typeMobs.get(mob).getCount().keySet()) {
                if (key.equals("AFK")) {
                    double multiplier = plugin.settings.getScoring().get(GameMode.SURVIVAL).getMobKillMultipliers().get(mob) * plugin.settings.getAFKMultiplier();
                    afkPointsOffset += typeMobs.get(mob).getCount().get(key) * multiplier;
                } else {
                    double multiplier = plugin.settings.getScoring().get(GameMode.valueOf(key)).getMobKillMultipliers().get(mob);
                    typeMobTotalPoints += typeMobs.get(mob).getCount().get(key) * multiplier;
                }
            }
        }
        return typeMobTotalPoints;
    }
    public double getTypeBlocksMinedTotalPoints() {
        double typeBlocksMinedTotalPoints = 0;
        for (Material material : typeBlocksMined.keySet()) {
            for (String key : typeBlocksMined.get(material).getCount().keySet()) {
                if (key.equals("AFK")) {
                    double multiplier = plugin.settings.getScoring().get(GameMode.SURVIVAL).getBlockMineMultipliers().get(material) * plugin.settings.getAFKMultiplier();
                    afkPointsOffset += typeBlocksMined.get(material).getCount().get(key) * multiplier;
                } else {
                    double multiplier = plugin.settings.getScoring().get(GameMode.valueOf(key)).getBlockMineMultipliers().get(material);
                    typeBlocksMinedTotalPoints += typeBlocksMined.get(material).getCount().get(key) * multiplier;
                }
            }
        }
        return typeBlocksMinedTotalPoints;
    }
    public double getTypeBlocksPlacedTotalPoints() {
        double typeBlocksPlacedTotalPoints = 0;
        for (Material material : typeBlocksPlaced.keySet()) {
            for (String key : typeBlocksPlaced.get(material).getCount().keySet()) {
                if (key.equals("AFK")) {
                    double multiplier = plugin.settings.getScoring().get(GameMode.SURVIVAL).getBlockPlaceMultipliers().get(material) * plugin.settings.getAFKMultiplier();
                    afkPointsOffset += typeBlocksPlaced.get(material).getCount().get(key) * multiplier;
                } else {
                    double multiplier = plugin.settings.getScoring().get(GameMode.valueOf(key)).getBlockPlaceMultipliers().get(material);
                    typeBlocksPlacedTotalPoints += typeBlocksPlaced.get(material).getCount().get(key) * multiplier;
                }
            }
        }
        return typeBlocksPlacedTotalPoints;
    }
    public double getTypeItemsHarvestedTotalPoints() {
        double typeItemsHarvestedTotalPoints = 0;
        for (Material material : typeItemsHarvested.keySet()) {
            for (String key : typeItemsHarvested.get(material).getCount().keySet()) {
                if (key.equals("AFK")) {
                    double multiplier = plugin.settings.getScoring().get(GameMode.SURVIVAL).getHarvestMultipliers().get(material) * plugin.settings.getAFKMultiplier();
                    afkPointsOffset += typeItemsHarvested.get(material).getCount().get(key) * multiplier;
                } else {
                    double multiplier = plugin.settings.getScoring().get(GameMode.valueOf(key)).getHarvestMultipliers().get(material);
                    typeItemsHarvestedTotalPoints += typeItemsHarvested.get(material).getCount().get(key) * multiplier;
                }
            }
        }
        return typeItemsHarvestedTotalPoints;

    }
    public double getAFKPointsOffset() { return afkPointsOffset; }

    public double totalPoints() {
        return round(getDeathPoints() + getAFKPointsOffset() + getLivesBuyBackPoints() + getXpPoints() + getOnlineHrPoints() + getDefaultBlocksPlacedPoints() +
                getDefaultBlocksMinedPoints() + getDefaultHarvestedPoints() + getDefaultCaughtPoints() + getDefaultAchievementPoints() +
                getTypeMobTotalPoints() + getTypeBlocksMinedTotalPoints() + getTypeBlocksPlacedTotalPoints() + getTypeItemsHarvestedTotalPoints(), 4);
    }

    public double onlineHr() {
        double hours = 0;
        for (String key : onlineHr.keySet()) {
            hours += onlineHr.get(key);
        }
        return round(hours,2);
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}

