package xyz.acyber.oneLife.DataObjects;

import com.fasterxml.jackson.annotation.*;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import xyz.acyber.oneLife.DataObjects.SubSettings.*;
import xyz.acyber.oneLife.OneLifePlugin;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class Settings {

    @JsonProperty("backupsToKeep")
    private int backupsToKeep = 1;

    @JsonProperty("livesBuyBackMultiplier")
    private double livesBuyBackMultiplier = 0;
    @JsonProperty("AFKMultiplier")
    private double AFKMultiplier = 0;
    @JsonProperty("pathToScoreData")
    private String pathToScoreData = "/scoreData/";

    @JsonProperty("afkCheckerConfig")
    private AFKCheckerConfig afkCheckerConfig = null;
    @JsonProperty("lives")
    private Lives lives = null;
    @JsonProperty("mobs")
    private HashMap<String,Mob> mobs = new HashMap<>();
    @JsonProperty("enabledFeatures")
    private EnabledFeatures enabledFeatures = null;
    @JsonProperty("playerConfigs")
    private HashMap<UUID,PlayerConfig> playerConfigs = new HashMap<>();
    @JsonProperty("races")
    private HashMap<String, Race> races = new HashMap<>();
    @JsonProperty("scoring")
    private HashMap<String, Scoring> scoring = new HashMap<>();
    @JsonProperty("teams")
    private HashMap<String,Team> teams = new HashMap<>();

    @JsonCreator
    public Settings() { super(); } // Default constructor

    @JsonCreator
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
                Mob mob = new Mob(type.name());
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
    public HashMap<String,Mob> getMobs() { if (mobs == null) mobs = new HashMap<>(); return mobs; }
    @JsonSetter
    public void setMobs(HashMap<String,Mob> mobs) { if (mobs == null) mobs = new HashMap<>(); this.mobs = mobs; }
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
    public HashMap<UUID,PlayerConfig> getPlayerConfigs() { if (playerConfigs == null) playerConfigs = new HashMap<>(); return playerConfigs; }
    @JsonSetter
    public void setPlayerConfigs(HashMap<UUID,PlayerConfig> playerConfigs) { if (playerConfigs == null) playerConfigs = new HashMap<>(); this.playerConfigs = playerConfigs; }
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
    public HashMap<String, Race> getRaces() { if (races == null) races = new HashMap<>(); return races; }
    @JsonSetter
    public void setRaces(HashMap<String, Race> races) { if (races == null) races = new HashMap<>(); this.races = races; }
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
    public HashMap<String, Scoring> getScoring() { if (scoring == null) scoring = new HashMap<>(); return scoring; }
    @JsonSetter
    public void setScoring(HashMap<String, Scoring> scoring) { if (scoring == null) scoring = new HashMap<>(); this.scoring = scoring; }
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
    public HashMap<String,Team> getTeams() { if (teams == null) teams = new HashMap<>(); return teams; }
    @JsonSetter
    public void setTeams(HashMap<String, Team> teams) { if (teams == null) teams = new HashMap<>(); this.teams = teams; }
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

