package xyz.acyber.oneLife.DataObjects.SubSettings;

import com.fasterxml.jackson.annotation.*;
import org.bukkit.GameMode;
import xyz.acyber.oneLife.OneLifePlugin;

import java.util.HashMap;

public class Scoring {


    @JsonProperty("pathToScoreData")
    private String pathToScoreData = "/PlayerScore/";
    @JsonProperty("livesBuyBackMultiplier")
    private double livesBuyBackMultiplier = -20.0;
    @JsonProperty("defaultAFKMultiplier")
    private double defaultAFKMultiplier = -1.0;
    @JsonProperty("defaultMobKillMultiplier")
    private double defaultMobKillMulitplier = 0.001;
    @JsonProperty("defaultBlocksPlacedMultiplier")
    private double defaultBlocksPlacedMultiplier = 0.001;
    @JsonProperty("defaultBlocksMinedMultiplier")
    private double defaultBlocksMinedMultiplier = 0.001;
    @JsonProperty("defaultHarvestedMultiplier")
    private double defaultHarvestedMultiplier = 0.001;
    @JsonProperty("defaultCaughtMultiplier")
    private double defaultCaughtMultiplier = 0.001;
    @JsonProperty("defaultAchievementMultiplier")
    private double defaultAchievementMultiplier = 0.03;
    @JsonProperty("gamemodeMultipliers")
    private HashMap<String, GamemodeScoreMultipliers> gamemodeMultipliers = new HashMap<>(); // key is gamemode, value is GamemodeScoreMultipliers Object

    @JsonCreator
    public Scoring() {
        GamemodeScoreMultipliers setup = new GamemodeScoreMultipliers();
        for (GameMode mode : GameMode.values()) {
            this.gamemodeMultipliers.put(mode.name(), setup);
        }
    }

    @JsonGetter
    public String getPathToScoreData() { return pathToScoreData; }
    @JsonSetter
    public void setPathToScoreData(String pathToScoreData) { this.pathToScoreData = pathToScoreData; }

    @JsonGetter
    public double getDefaultAFKMultiplier() { return defaultAFKMultiplier; }
    @JsonSetter
    public void setDefaultAFKMultiplier(double defaultAFKMultiplier) { this.defaultAFKMultiplier = defaultAFKMultiplier; }

    @JsonGetter
    public double getDefaultMobKillMultiplier() { return defaultMobKillMulitplier; }
    @JsonSetter
    public void setDefaultMobKillMultiplier(double defaultMobKillMultiplier) { this.defaultMobKillMulitplier = defaultMobKillMultiplier; }

    @JsonGetter
    public double getLivesBuyBackMultiplier() { return livesBuyBackMultiplier; }
    @JsonSetter
    public void setLivesBuyBackMultiplier(double livesBuyBackMultiplier) { this.livesBuyBackMultiplier = livesBuyBackMultiplier; }

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
    public HashMap<String, GamemodeScoreMultipliers> getGamemodeMultipliers() { if (gamemodeMultipliers == null) gamemodeMultipliers = new HashMap<>(); return gamemodeMultipliers; }
    @JsonSetter
    public void setGamemodeMultipliers(HashMap<String, GamemodeScoreMultipliers> gamemodeMultipliers) { if (gamemodeMultipliers == null) gamemodeMultipliers = new HashMap<>(); this.gamemodeMultipliers = gamemodeMultipliers; }
    @JsonIgnore
    public boolean addGamemodeMultipliers(String gameMode, GamemodeScoreMultipliers scoringConfig) {
        if (gamemodeMultipliers.containsKey(gameMode))
            return false;
        gamemodeMultipliers.put(gameMode, scoringConfig);
        return true;
    }
    @JsonIgnore
    public boolean replaceGamemodeMultipliers(String gameMode, GamemodeScoreMultipliers scoringConfig) {
        if (gamemodeMultipliers.containsKey(gameMode)) {
            gamemodeMultipliers.replace(gameMode, scoringConfig);
            return true;
        }
        return false;
    }
    @JsonIgnore
    public void removeGamemodeMultipliers(String gameMode) {  gamemodeMultipliers.remove(gameMode); }
}
