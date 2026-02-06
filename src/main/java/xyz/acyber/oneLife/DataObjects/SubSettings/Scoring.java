package xyz.acyber.oneLife.DataObjects.SubSettings;

import org.bukkit.GameMode;

import java.util.HashMap;

public class Scoring {


    private String pathToScoreData = "/PlayerScore/";
    private double livesBuyBackMultiplier = -20.0;
    private double defaultAFKMultiplier = -1.0;
    private double defaultMobKillMulitplier = 0.001;
    private double defaultBlocksPlacedMultiplier = 0.001;
    private double defaultBlocksMinedMultiplier = 0.001;
    private double defaultHarvestedMultiplier = 0.001;
    private double defaultCaughtMultiplier = 0.001;
    private double defaultAchievementMultiplier = 0.03;
    private HashMap<String, GamemodeScoreMultipliers> gamemodeMultipliers = new HashMap<>(); // key is gamemode, value is GamemodeScoreMultipliers Object

    public Scoring() {
        GamemodeScoreMultipliers setup = new GamemodeScoreMultipliers();
        for (GameMode mode : GameMode.values()) {
            this.gamemodeMultipliers.put(mode.name(), setup);
        }
    }

    public String getPathToScoreData() { return pathToScoreData; }
    public void setPathToScoreData(String pathToScoreData) { this.pathToScoreData = pathToScoreData; }

    public double getDefaultAFKMultiplier() { return defaultAFKMultiplier; }
    public void setDefaultAFKMultiplier(double defaultAFKMultiplier) { this.defaultAFKMultiplier = defaultAFKMultiplier; }

    public double getDefaultMobKillMultiplier() { return defaultMobKillMulitplier; }
    public void setDefaultMobKillMultiplier(double defaultMobKillMultiplier) { this.defaultMobKillMulitplier = defaultMobKillMultiplier; }

    public double getLivesBuyBackMultiplier() { return livesBuyBackMultiplier; }
    public void setLivesBuyBackMultiplier(double livesBuyBackMultiplier) { this.livesBuyBackMultiplier = livesBuyBackMultiplier; }

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

    public HashMap<String, GamemodeScoreMultipliers> getGamemodeMultipliers() { if (gamemodeMultipliers == null) gamemodeMultipliers = new HashMap<>(); return gamemodeMultipliers; }
    public void setGamemodeMultipliers(HashMap<String, GamemodeScoreMultipliers> gamemodeMultipliers) { if (gamemodeMultipliers == null) gamemodeMultipliers = new HashMap<>(); this.gamemodeMultipliers = gamemodeMultipliers; }
    public boolean addGamemodeMultipliers(String gameMode, GamemodeScoreMultipliers scoringConfig) {
        if (gamemodeMultipliers.containsKey(gameMode))
            return false;
        gamemodeMultipliers.put(gameMode, scoringConfig);
        return true;
    }
    public boolean replaceGamemodeMultipliers(String gameMode, GamemodeScoreMultipliers scoringConfig) {
        if (gamemodeMultipliers.containsKey(gameMode)) {
            gamemodeMultipliers.replace(gameMode, scoringConfig);
            return true;
        }
        return false;
    }
    public void removeGamemodeMultipliers(String gameMode) {  gamemodeMultipliers.remove(gameMode); }
}
