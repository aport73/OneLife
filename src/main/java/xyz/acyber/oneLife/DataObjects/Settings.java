package xyz.acyber.oneLife.DataObjects;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import xyz.acyber.oneLife.DataObjects.SubSettings.*;
import xyz.acyber.oneLife.OneLifePlugin;

import java.util.HashMap;
import java.util.UUID;

public class Settings {

    private int backupsToKeep;

    private double livesBuyBackMulitplier;
    private double AFKMultiplier;
    private String pathToScoreData;

    private AFKCheckerConfig afkCheckerConfig;
    private Lives lives;
    private HashMap<EntityType,Mob> mobs;
    private EnabledFeatures enabledFeatures;
    private HashMap<UUID,PlayerConfig> playerConfigs;
    private HashMap<String, Race> races;
    private HashMap<GameMode, Scoring> scoring;
    private HashMap<String,Team> teams;

    public Settings() {
        super();
    }

    public Settings(OneLifePlugin plugin) {
        this.afkCheckerConfig = new AFKCheckerConfig();
        this.lives = new Lives();
        this.mobs = new HashMap<>();
        this.enabledFeatures = new EnabledFeatures();
        this.playerConfigs = new HashMap<>();
        this.races = new HashMap<>();
        setupScoring(plugin);
        this.teams = new HashMap<>();

        this.AFKMultiplier = -1.0;
        this.livesBuyBackMulitplier = -20.0;
        this.pathToScoreData = "/ScoreData/";
        this.backupsToKeep = 10;
    }
    @JsonIgnore
    private void setupScoring(OneLifePlugin plugin) {
        this.scoring = new HashMap<>();
        Scoring setup = new Scoring(plugin);
        for (GameMode mode : GameMode.values()) {
            this.scoring.put(mode, setup);
        }
    }

    @JsonGetter
    public String getPathToScoreData() { return pathToScoreData; }
    @JsonSetter
    public void setPathToScoreData(String pathToScoreData) { this.pathToScoreData = pathToScoreData; }

    @JsonGetter
    public double getLivesBuyBackMulitplier() { return livesBuyBackMulitplier; }
    @JsonSetter
    public void setLivesBuyBackMulitplier(double livesBuyBackMulitplier) {  this.livesBuyBackMulitplier = livesBuyBackMulitplier; }

    @JsonGetter
    public double getAFKMultiplier() { return AFKMultiplier; }
    @JsonSetter
    public void setAFKMultiplier(double AFKMultiplier) {  this.AFKMultiplier = AFKMultiplier; }

    @JsonGetter
    public int getBackupsToKeep() { return backupsToKeep; }
    @JsonSetter
    public void setBackupsToKeep(int backupsToKeep) { this.backupsToKeep = backupsToKeep; }

    @JsonGetter
    public AFKCheckerConfig getAfkCheckerConfig() { return afkCheckerConfig; }
    @JsonSetter
    public void setAfkCheckerConfig(AFKCheckerConfig afkChecker) { this.afkCheckerConfig = afkChecker; }

    @JsonGetter
    public Lives getLives() { return lives; }
    @JsonSetter
    public void setLives(Lives lives) { this.lives = lives; }

    @JsonGetter
    public HashMap<EntityType,Mob> getMobs() { return mobs; }
    @JsonSetter
    public void setMobs(HashMap<EntityType,Mob> mobs) { this.mobs = mobs; }
    @JsonIgnore
    public boolean addMob(Mob mob) {
        if (mobs.containsKey(mob.getMobType()))
            return false;
        mobs.put(mob.getMobType(), mob);
        return true;
    }
    @JsonIgnore
    public boolean replaceMob(Mob mob) {
        if (mobs.containsKey(mob.getMobType())) {
            mobs.replace(mob.getMobType(), mob);
            return true;
        }
         return false;
    }
    @JsonIgnore
    public void removeMob(Mob mob) { mobs.remove(mob.getMobType()); }

    @JsonGetter
    public EnabledFeatures getEnabledFeatures() { return enabledFeatures; }
    @JsonSetter
    public void setEnabledFeatures(EnabledFeatures enabledFeatures) { this.enabledFeatures = enabledFeatures; }

    @JsonGetter
    public HashMap<UUID,PlayerConfig> getPlayerConfigs() { return playerConfigs; }
    @JsonSetter
    public void setPlayerConfigs(HashMap<UUID,PlayerConfig> playerConfigs) { this.playerConfigs = playerConfigs; }
    @JsonIgnore
    public void addPlayerConfigs(PlayerConfig playerConfig) {
        if (!playerConfigs.containsKey(playerConfig.getUUID()))
            playerConfigs.put(playerConfig.getUUID(), playerConfig);
    }
    @JsonIgnore
    public boolean replacePlayerConfigs(PlayerConfig playerConfig) {
        if (playerConfigs.containsKey(playerConfig.getUUID())) {
            playerConfigs.replace(playerConfig.getUUID(), playerConfig);
            return true;
        }
        return false;
    }
    @JsonIgnore
    public void removePlayerConfigs(PlayerConfig playerConfig) { playerConfigs.remove(playerConfig.getUUID()); }

    @JsonGetter
    public HashMap<String, Race> getRaces() { return races; }
    @JsonSetter
    public void setRaces(HashMap<String, Race> races) { this.races = races; }
    @JsonIgnore
    public boolean addRace(Race race) {
        if (races.containsKey(race.getRaceName()))
            return false;
        races.put(race.getRaceName(), race);
        return true;
    }
    @JsonIgnore
    public boolean replaceRace(Race race) {
        if (races.containsKey(race.getRaceName())) {
            races.replace(race.getRaceName(), race);
            return true;
        }
        return false;
    }
    @JsonIgnore
    public void removeRace(Race race) { races.remove(race.getRaceName()); }

    @JsonGetter
    public HashMap<GameMode, Scoring> getScoring() { return scoring; }
    @JsonSetter
    public void setScoring(HashMap<GameMode, Scoring> scoring) { this.scoring = scoring; }
    @JsonIgnore
    public boolean addScoring(GameMode gameMode, Scoring scoringConfig) {
        if (scoring.containsKey(gameMode))
            return false;
        scoring.put(gameMode, scoringConfig);
        return true;
    }
    @JsonIgnore
    public boolean replaceScoring(GameMode gameMode, Scoring scoringConfig) {
        if (scoring.containsKey(gameMode)) {
            scoring.replace(gameMode, scoringConfig);
            return true;
        }
        return false;
    }
    @JsonIgnore
    public void removeScoring(GameMode gameMode) {  scoring.remove(gameMode); }

    @JsonGetter
    public HashMap<String,Team> getTeams() { return teams; }
    @JsonSetter
    public void setTeams(HashMap<String, Team> teams) { this.teams = teams; }
    @JsonIgnore
    public boolean addTeam(Team team) {
        if (teams.containsKey(team.getTeamName()))
            return false;
        teams.put(team.getTeamName(), team);
        return true;
    }
    @JsonIgnore
    public boolean replaceTeam(Team team) {
        if (teams.containsKey(team.getTeamName())) {
            teams.put(team.getTeamName(), team);
            return true;
        }
        return false;
    }
    @JsonIgnore
    public void removeTeam(Team team) { teams.remove(team.getTeamName()); }
}

