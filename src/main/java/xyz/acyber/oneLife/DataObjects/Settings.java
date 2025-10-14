package xyz.acyber.oneLife.DataObjects;

import com.fasterxml.jackson.annotation.*;
import org.bukkit.OfflinePlayer;
import xyz.acyber.oneLife.DataObjects.SubSettings.*;

import java.util.*;

public class Settings {

    @JsonProperty("backupsToKeep")
    private int backupsToKeep = 10;
    @JsonProperty("autoSaveIntervalSeconds")
    private int autoSaveIntervalSeconds = 300;
    @JsonProperty("luckPermsEnabled")
    private boolean luckPermsEnabled = true;
    @JsonProperty("enabledFeatures")
    private EnabledFeatures enabledFeatures = new EnabledFeatures();
    @JsonProperty("afkCheckerConfig")
    private AFKCheckerConfig afkCheckerConfig = new AFKCheckerConfig();
    @JsonProperty("lives")
    private Lives lives = new Lives();
    @JsonProperty("scoring")
    private Scoring scoring = new Scoring();

    @JsonProperty("mobs")
    private HashMap<String, MobConfig> mobs = new HashMap<>(); // key is mobType, value is MobConfig object
    @JsonProperty("playerConfigs")
    private HashMap<UUID,PlayerConfig> playerConfigs = new HashMap<>(); //Key is player UUID, value is PlayerConfig object
    @JsonProperty("races")
    private HashMap<UUID,Race> races = new HashMap<>(); // key is race UUID, value is Race Object
    @JsonProperty("teams")
    private HashMap<UUID,Team> teams = new HashMap<>(); //key is team UUID, value is Team Object

    @JsonCreator
    public Settings() {
        super();
    }

    @JsonGetter
    public int getBackupsToKeep() { return backupsToKeep; }
    @JsonSetter
    public void setBackupsToKeep(int backupsToKeep) { this.backupsToKeep = backupsToKeep; }

    @JsonGetter
    public int getAutoSaveIntervalSeconds() { return autoSaveIntervalSeconds; }
    @JsonSetter
    public void setAutoSaveIntervalSeconds(int autoSaveIntervalSeconds) { this.autoSaveIntervalSeconds = autoSaveIntervalSeconds; }

    @JsonGetter
    public boolean isLuckPermsEnabled() { return luckPermsEnabled; }
    @JsonSetter
    public void setLuckPermsEnabled(boolean luckPermsEnabled) { this.luckPermsEnabled = luckPermsEnabled; }

    @JsonGetter
    public AFKCheckerConfig getAfkCheckerConfig() { return afkCheckerConfig; }
    @JsonSetter
    public void setAfkCheckerConfig(AFKCheckerConfig afkChecker) { this.afkCheckerConfig = afkChecker; }

    @JsonGetter
    public Lives getLives() { return lives; }
    @JsonSetter
    public void setLives(Lives lives) { this.lives = lives; }

    @JsonGetter
    public Scoring getScoring() { return scoring; }
    @JsonSetter
    public void setScoring(Scoring scoring) { this.scoring = scoring; }

    @JsonGetter
    public HashMap<String, MobConfig> getMobs() { if (mobs == null) mobs = new HashMap<>(); return mobs; }
    @JsonSetter
    public void setMobs(HashMap<String, MobConfig> mobs) { if (mobs == null) mobs = new HashMap<>(); this.mobs = mobs; }
    @JsonIgnore
    public boolean addMob(MobConfig mobConfig) {
        if (mobs.containsKey(mobConfig.getMobType()))
            return false;
        mobs.put(mobConfig.getMobType(), mobConfig);
        return true;
    }
    @JsonIgnore
    public boolean replaceMob(MobConfig mobConfig) {
        if (mobs.containsKey(mobConfig.getMobType())) {
            mobs.replace(mobConfig.getMobType(), mobConfig);
            return true;
        }
         return false;
    }
    @JsonIgnore
    public void removeMob(MobConfig mobConfig) { mobs.remove(mobConfig.getMobType()); }

    @JsonGetter
    public EnabledFeatures getEnabledFeatures() { return enabledFeatures; }
    @JsonSetter
    public void setEnabledFeatures(EnabledFeatures enabledFeatures) { this.enabledFeatures = enabledFeatures; }

    @JsonGetter
    public HashMap<UUID,PlayerConfig> getPlayerConfigs() { if (playerConfigs == null) playerConfigs = new HashMap<>(); return playerConfigs; }
    @JsonSetter
    public void setPlayerConfigs(HashMap<UUID,PlayerConfig> playerConfigs) {
        if (playerConfigs == null)
            playerConfigs = new HashMap<>();
        this.playerConfigs = playerConfigs;
    }
    @JsonIgnore
    public void initialisePlayer(OfflinePlayer player) {
        if (!playerConfigs.containsKey(player.getUniqueId()))
             addPlayerConfigs(new PlayerConfig(player.getUniqueId(), player.getName()));
    }

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
    public HashMap<UUID, Race> getRaces() { if (races == null) races = new HashMap<>(); return races; }
    @JsonSetter
    public void setRaces(HashMap<UUID, Race> races) { if (races == null) races = new HashMap<>(); this.races = races; }
    @JsonIgnore
    public Race getRace(UUID raceUUID) { return races.get(raceUUID); }
    @JsonIgnore
    public boolean addRace(Race race) {
        if (races == null) races = new HashMap<>();
        if (races.containsKey(race.getRaceUUID()))
            return false;
        races.put(race.getRaceUUID(), race);
        return true;
    }
    @JsonIgnore
    public boolean replaceRace(Race race) {
        if (races == null) return false;
        if (races.containsKey(race.getRaceUUID())) {
            races.replace(race.getRaceUUID(), race);
            return true;
        }
        return false;
    }
    @JsonIgnore
    public void removeRace(Race race) {
        if (races == null) return;
        races.remove(race.getRaceUUID());
    }

    @JsonGetter
    public HashMap<UUID,Team> getTeams() { if (teams == null) teams = new HashMap<>(); return teams; }
    @JsonSetter
    public void setTeams(HashMap<UUID, Team> teams) { if (teams == null) teams = new HashMap<>(); this.teams = teams; }

    @JsonGetter
    public Team getTeam(UUID teamUUID) { return teams.get(teamUUID); }

    @JsonIgnore
    public boolean addTeam(Team team) {
        if (teams != null && teams.containsKey(team.getUUID()))
            return false;
        if (teams == null) teams = new HashMap<>();
        teams.put(team.getUUID(), team);
        return true;
    }

    @JsonIgnore
    public boolean replaceTeam(Team team) {
        if (teams != null && teams.containsKey(team.getUUID())) {
            teams.put(team.getUUID(), team);
            return true;
        }
        return false;
    }

    @JsonIgnore
    public void removeTeam(Team team) {
        if (teams == null) return;
        teams.remove(team.getUUID());
    }
}

