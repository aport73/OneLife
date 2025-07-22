package xyz.acyber.oneLife.DataObjects;

import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import xyz.acyber.oneLife.DataObjects.SubSettings.*;
import xyz.acyber.oneLife.Main;

import java.util.HashMap;
import java.util.UUID;

public class Settings {

    private int backupsToKeep;

    private double livesBuyBackMulitplier;
    private double AFKMultiplier;

    private AFKCheckerConfig afkCheckerConfig;
    private Lives lives;
    private HashMap<EntityType,Mob> mobs;
    private EnabledFeatures enabledFeatures;
    private HashMap<UUID,PlayerConfig> playerConfigs;
    private HashMap<String, Race> races;
    private HashMap<GameMode, Scoring> scoring;
    private HashMap<String,Team> teams;

    public Settings(Main plugin) {
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

        this.backupsToKeep = 10;
    }

    private void setupScoring(Main plugin) {
        this.scoring = new HashMap<>();
        Scoring setup = new Scoring(plugin);
        for (GameMode mode : GameMode.values()) {
            this.scoring.put(mode, setup);
        }
    }

    public double getLivesBuyBackMulitplier() { return livesBuyBackMulitplier; }
    public void setLivesBuyBackMulitplier(double livesBuyBackMulitplier) {  this.livesBuyBackMulitplier = livesBuyBackMulitplier; }

    public double getAFKMultiplier() { return AFKMultiplier; }
    public void setAFKMultiplier(double AFKMultiplier) {  this.AFKMultiplier = AFKMultiplier; }

    public int getBackupsToKeep() { return backupsToKeep; }
    public void setBackupsToKeep(int backupsToKeep) { this.backupsToKeep = backupsToKeep; }

    public AFKCheckerConfig getAfkCheckerConfig() { return afkCheckerConfig; }
    public void setAfkCheckerConfig(AFKCheckerConfig afkChecker) { this.afkCheckerConfig = afkChecker; }

    public Lives getLives() { return lives; }
    public void setLives(Lives lives) { this.lives = lives; }

    public HashMap<EntityType,Mob> getMobs() { return mobs; }
    public void setMobs(HashMap<EntityType,Mob> mobs) { this.mobs = mobs; }
    public boolean addMob(Mob mob) {
        if (mobs.containsKey(mob.getMobType()))
            return false;
        mobs.put(mob.getMobType(), mob);
        return true;
    }
    public boolean replaceMob(Mob mob) {
        if (mobs.containsKey(mob.getMobType())) {
            mobs.replace(mob.getMobType(), mob);
            return true;
        }
         return false;
    }
    public void removeMob(Mob mob) { mobs.remove(mob.getMobType()); }

    public EnabledFeatures getEnabledFeatures() { return enabledFeatures; }
    public void setEnabledFeatures(EnabledFeatures enabledFeatures) { this.enabledFeatures = enabledFeatures; }

    public HashMap<UUID,PlayerConfig> getPlayerConfigs() { return playerConfigs; }
    public void setPlayerConfigs(HashMap<UUID,PlayerConfig> playerConfigs) { this.playerConfigs = playerConfigs; }
    public boolean addPlayerConfigs(PlayerConfig playerConfig) {
        if (playerConfigs.containsKey(playerConfig.getUUID()))
            return false;
        playerConfigs.put(playerConfig.getUUID(), playerConfig);
        return true;
    }
    public boolean replacePlayerConfigs(PlayerConfig playerConfig) {
        if (playerConfigs.containsKey(playerConfig.getUUID())) {
            playerConfigs.replace(playerConfig.getUUID(), playerConfig);
            return true;
        }
        return false;
    }
    public void removePlayerConfigs(PlayerConfig playerConfig) { playerConfigs.remove(playerConfig.getUUID()); }

    public HashMap<String, Race> getRaces() { return races; }
    public void setRaces(HashMap<String, Race> races) { this.races = races; }
    public boolean addRace(Race race) {
        if (races.containsKey(race.getRaceName()))
            return false;
        races.put(race.getRaceName(), race);
        return true;
    }
    public boolean replaceRace(Race race) {
        if (races.containsKey(race.getRaceName())) {
            races.replace(race.getRaceName(), race);
            return true;
        }
        return false;
    }
    public void removeRace(Race race) { races.remove(race.getRaceName()); }

    public HashMap<GameMode, Scoring> getScoring() { return scoring; }
    public void setScoring(HashMap<GameMode, Scoring> scoring) { this.scoring = scoring; }
    public boolean addScoring(GameMode gameMode, Scoring scoringConfig) {
        if (scoring.containsKey(gameMode))
            return false;
        scoring.put(gameMode, scoringConfig);
        return true;
    }
    public boolean replaceScoring(GameMode gameMode, Scoring scoringConfig) {
        if (scoring.containsKey(gameMode)) {
            scoring.replace(gameMode, scoringConfig);
            return true;
        }
        return false;
    }
    public void removeScoring(GameMode gameMode) {  scoring.remove(gameMode); }

    public HashMap<String,Team> getTeams() { return teams; }
    public void setTeams(HashMap<String, Team> teams) { this.teams = teams; }
    public boolean addTeam(Team team) {
        if (teams.containsKey(team.getTeamName()))
            return false;
        teams.put(team.getTeamName(), team);
        return true;
    }
    public boolean replaceTeam(Team team) {
        if (teams.containsKey(team.getTeamName())) {
            teams.put(team.getTeamName(), team);
            return true;
        }
        return false;
    }
    public void removeTeam(Team team) { teams.remove(team.getTeamName()); }
}

