package xyz.acyber.oneLife.DataObjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import xyz.acyber.oneLife.DataObjects.SubSettings.*;
import xyz.acyber.oneLife.OneLifePlugin;
import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.UUID;

public class Settings {

    private int backupsToKeep;

    private double livesBuyBackMultiplier;
    private double AFKMultiplier;
    private String pathToScoreData;

    private AFKCheckerConfig afkCheckerConfig;
    private Lives lives;
    private HashMap<String,Mob> mobs;
    private EnabledFeatures enabledFeatures;
    private @Nullable HashMap<UUID,PlayerConfig> playerConfigs;
    private @Nullable HashMap<String, Race> races;
    private HashMap<String, Scoring> scoring;
    private @Nullable HashMap<String,Team> teams;

    @JsonCreator
    public Settings() {} // Default constructor

    @JsonIgnore
    public Settings(OneLifePlugin plugin) {
        this.afkCheckerConfig = new AFKCheckerConfig();
        this.lives = new Lives();
        setupMobs();
        this.enabledFeatures = new EnabledFeatures();
        this.playerConfigs = null;
        this.races = null;
        setupScoring(plugin);
        this.teams = null;
        this.AFKMultiplier = -1.0;
        this.livesBuyBackMultiplier = -20.0;
        this.pathToScoreData = "/ScoreData/";
        this.backupsToKeep = 10;
    }
    @JsonIgnore
    private void setupScoring(OneLifePlugin plugin) {
        this.scoring = new HashMap<>();
        Scoring setup = new Scoring(plugin);
        for (GameMode mode : GameMode.values()) {
            this.scoring.put(mode.name(), setup);
        }
    }
    @JsonIgnore
    private void setupMobs() {
        this.mobs = new HashMap<>();
        for (EntityType type : EntityType.values()) {
            if (type.isAlive() && type != EntityType.PLAYER) {
                Mob mob = new Mob(type);
                this.mobs.put(type.name(), mob);
            }
        }
    }

    @JsonGetter
    public String getPathToScoreData() { return pathToScoreData; }
    @JsonSetter
    public void setPathToScoreData(String pathToScoreData) { this.pathToScoreData = pathToScoreData; }

    @JsonGetter
    public double getLivesBuyBackMultiplier() { return livesBuyBackMultiplier; }
    @JsonSetter
    public void setLivesBuyBackMultiplier(double livesBuyBackMultiplier) {  this.livesBuyBackMultiplier = livesBuyBackMultiplier; }

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
    public HashMap<String,Mob> getMobs() { return mobs; }
    @JsonSetter
    public void setMobs(HashMap<String,Mob> mobs) { this.mobs = mobs; }
    @JsonIgnore
    public boolean addMob(Mob mob) {
        if (mobs.containsKey(mob.getMobType().name()))
            return false;
        mobs.put(mob.getMobType().name(), mob);
        return true;
    }
    @JsonIgnore
    public boolean replaceMob(Mob mob) {
        if (mobs.containsKey(mob.getMobType().name())) {
            mobs.replace(mob.getMobType().name(), mob);
            return true;
        }
         return false;
    }
    @JsonIgnore
    public void removeMob(Mob mob) { mobs.remove(mob.getMobType().name()); }

    @JsonGetter
    public EnabledFeatures getEnabledFeatures() { return enabledFeatures; }
    @JsonSetter
    public void setEnabledFeatures(EnabledFeatures enabledFeatures) { this.enabledFeatures = enabledFeatures; }

    @JsonGetter
    public @Nullable HashMap<UUID,PlayerConfig> getPlayerConfigs() { return playerConfigs; }
    @JsonSetter
    public void setPlayerConfigs(@Nullable HashMap<UUID,PlayerConfig> playerConfigs) { if (playerConfigs == null) this.playerConfigs = null; this.playerConfigs = playerConfigs; }
    @JsonIgnore
    public void addPlayerConfigs(PlayerConfig playerConfig) {
        if (playerConfigs == null) playerConfigs = new HashMap<>();
        if (!playerConfigs.containsKey(playerConfig.getUUID()))
            playerConfigs.put(playerConfig.getUUID(), playerConfig);
    }
    @JsonIgnore
    public boolean replacePlayerConfigs(PlayerConfig playerConfig) {
        if (playerConfigs == null) return false;
        if (playerConfigs.containsKey(playerConfig.getUUID())) {
            playerConfigs.replace(playerConfig.getUUID(), playerConfig);
            return true;
        }
        return false;
    }
    @JsonIgnore
    public void removePlayerConfigs(PlayerConfig playerConfig) {
        if (playerConfigs == null) return;
        playerConfigs.remove(playerConfig.getUUID());
    }

    @JsonGetter
    public @Nullable HashMap<String, Race> getRaces() { return races; }
    @JsonSetter
    public void setRaces(@Nullable HashMap<String, Race> races) { this.races = races; }
    @JsonIgnore
    public boolean addRace(Race race) {
        if (races == null) races = new HashMap<>();
        if (races.containsKey(race.getRaceName()))
            return false;
        races.put(race.getRaceName(), race);
        return true;
    }
    @JsonIgnore
    public boolean replaceRace(Race race) {
        if (races == null) return false;
        if (races.containsKey(race.getRaceName())) {
            races.replace(race.getRaceName(), race);
            return true;
        }
        return false;
    }
    @JsonIgnore
    public void removeRace(Race race) {
        if (races == null) return;
        races.remove(race.getRaceName()); }


    @JsonGetter
    public HashMap<String, Scoring> getScoring() { return scoring; }
    @JsonSetter
    public void setScoring(HashMap<String, Scoring> scoring) { this.scoring = scoring; }
    @JsonIgnore
    public boolean addScoring(String gameMode, Scoring scoringConfig) {
        if (scoring.containsKey(gameMode))
            return false;
        scoring.put(gameMode, scoringConfig);
        return true;
    }
    @JsonIgnore
    public boolean replaceScoring(String gameMode, Scoring scoringConfig) {
        if (scoring.containsKey(gameMode)) {
            scoring.replace(gameMode, scoringConfig);
            return true;
        }
        return false;
    }
    @JsonIgnore
    public void removeScoring(String gameMode) {  scoring.remove(gameMode); }

    @JsonGetter
    public @Nullable HashMap<String,Team> getTeams() { return teams; }
    @JsonSetter
    public void setTeams(@Nullable HashMap<String, Team> teams) { this.teams = teams; }
    @JsonIgnore
    public boolean addTeam(Team team) {
        if (teams != null && teams.containsKey(team.getTeamName()))
            return false;
        if (teams == null) teams = new HashMap<>();
        teams.put(team.getTeamName(), team);
        return true;
    }
    @JsonIgnore
    public boolean replaceTeam(Team team) {
        if (teams != null && teams.containsKey(team.getTeamName())) {
            teams.put(team.getTeamName(), team);
            return true;
        }
        return false;
    }
    @JsonIgnore
    public void removeTeam(Team team) {
        if (teams == null) return;
        teams.remove(team.getTeamName());
    }
}

