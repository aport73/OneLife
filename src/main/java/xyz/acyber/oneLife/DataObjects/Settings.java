package xyz.acyber.oneLife.DataObjects;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import xyz.acyber.oneLife.DataObjects.SubSettings.*;
import xyz.acyber.oneLife.OneLifePlugin;

import java.util.*;

public class Settings {

    private transient OneLifePlugin OLP;

    private int backupsToKeep = 10;
    private int autoSaveIntervalSeconds = 300;
    private boolean luckPermsEnabled = true;
    private EnabledFeatures enabledFeatures = new EnabledFeatures();
    private AFKCheckerConfig afkCheckerConfig = new AFKCheckerConfig();
    private Lives lives = new Lives();
    private Scoring scoring = new Scoring();

    private HashMap<String, MobConfig> mobs = new HashMap<>(); // key is mobType, value is MobConfig object
    private HashMap<UUID,PlayerConfig> playerConfigs = new HashMap<>(); //Key is player UUID, value is PlayerConfig object
    private HashMap<UUID,Race> races = new HashMap<>(); // key is race UUID, value is Race Object
    private HashMap<UUID,Team> teams = new HashMap<>(); //key is team UUID, value is Team Object

    public Settings() {
        super();
    }

    public void setPlugin(OneLifePlugin plugin) { this.OLP = plugin; }
    private void markDirty() { if (OLP != null) OLP.markSettingsDirty(); }

    public int getBackupsToKeep() { return backupsToKeep; }
    public void setBackupsToKeep(int backupsToKeep) { markDirty(); this.backupsToKeep = backupsToKeep; }

    public int getAutoSaveIntervalSeconds() { return autoSaveIntervalSeconds; }
    public void setAutoSaveIntervalSeconds(int autoSaveIntervalSeconds) { markDirty(); this.autoSaveIntervalSeconds = autoSaveIntervalSeconds; }

    public boolean isLuckPermsEnabled() { return luckPermsEnabled; }
    public void setLuckPermsEnabled(boolean luckPermsEnabled) { markDirty(); this.luckPermsEnabled = luckPermsEnabled; }

    public AFKCheckerConfig getAfkCheckerConfig() { return afkCheckerConfig; }
    public void setAfkCheckerConfig(AFKCheckerConfig afkChecker) { markDirty(); this.afkCheckerConfig = afkChecker; }

    public Lives getLives() { return lives; }
    public void setLives(Lives lives) { markDirty(); this.lives = lives; }

    public Scoring getScoring() { return scoring; }
    public void setScoring(Scoring scoring) { markDirty(); this.scoring = scoring; }

    public HashMap<String, MobConfig> getMobs() { if (mobs == null) mobs = new HashMap<>(); return mobs; }
    public void setMobs(HashMap<String, MobConfig> mobs) { markDirty(); if (mobs == null) mobs = new HashMap<>(); this.mobs = mobs; }
    public boolean addMob(MobConfig mobConfig) {
        if (mobs.containsKey(mobConfig.getMobType()))
            return false;
        mobs.put(mobConfig.getMobType(), mobConfig);
        markDirty();
        return true;
    }
    public boolean replaceMob(MobConfig mobConfig) {
        if (mobs.containsKey(mobConfig.getMobType())) {
            mobs.replace(mobConfig.getMobType(), mobConfig);
            markDirty();
            return true;
        }
         return false;
    }
    public void removeMob(MobConfig mobConfig) { mobs.remove(mobConfig.getMobType()); markDirty(); }

    public EnabledFeatures getEnabledFeatures() { return enabledFeatures; }
    public void setEnabledFeatures(EnabledFeatures enabledFeatures) { markDirty(); this.enabledFeatures = enabledFeatures; }

    public HashMap<UUID,PlayerConfig> getPlayerConfigs() { if (playerConfigs == null) playerConfigs = new HashMap<>(); return playerConfigs; }
    public void setPlayerConfigs(HashMap<UUID,PlayerConfig> playerConfigs) {
        markDirty();
        if (playerConfigs == null)
            playerConfigs = new HashMap<>();
        this.playerConfigs = playerConfigs;
    }
    public void initialisePlayer(OfflinePlayer player) {
        if (!playerConfigs.containsKey(player.getUniqueId()))
             addPlayerConfigs(new PlayerConfig(player.getUniqueId(), player.getName()));
    }

    public PlayerConfig getPlayerConfig(OfflinePlayer player) {
        if (!playerConfigs.containsKey(player.getUniqueId()))
            initialisePlayer(player);
        return playerConfigs.get(player.getUniqueId());

    }

    public PlayerConfig getPlayerConfig(Player player) {
        if (!playerConfigs.containsKey(player.getUniqueId()))
            initialisePlayer(player);
        return playerConfigs.get(player.getUniqueId());
    }

    public void addPlayerConfigs(PlayerConfig playerConfig) {
        if (playerConfigs == null) playerConfigs = new HashMap<>();
        if (!playerConfigs.containsKey(playerConfig.getUUID())) {
            playerConfigs.put(playerConfig.getUUID(), playerConfig);
            markDirty();
        }
    }

    public void replacePlayerConfigs(PlayerConfig playerConfig) {
        if (playerConfigs == null) return;
        if (playerConfigs.containsKey(playerConfig.getUUID())) {
            playerConfigs.replace(playerConfig.getUUID(), playerConfig);
            markDirty();
        }
    }

    public void removePlayerConfigs(PlayerConfig playerConfig) {
        if (playerConfigs == null) return;
        playerConfigs.remove(playerConfig.getUUID());
        markDirty();
    }

    public Race getPlayerRace(Player player) {
        PlayerConfig config = playerConfigs.get(player.getUniqueId());
        return races.get(config.getRaceUUID());
    }
    public Race getPlayerRace(OfflinePlayer player) {
        PlayerConfig config = playerConfigs.get(player.getUniqueId());
        return races.get(config.getRaceUUID());
    }
    public void setPlayerRace(Player player, Race race) {
        PlayerConfig config = playerConfigs.get(player.getUniqueId());
        config.setRaceUUID(race.getRaceUUID());
    }
    public void setPlayerRace(OfflinePlayer player, Race race) {
        PlayerConfig config = playerConfigs.get(player.getUniqueId());
        config.setRaceUUID(race.getRaceUUID());
    }

    public HashMap<UUID, Race> getRaces() { if (races == null) races = new HashMap<>(); return races; }
    public void setRaces(HashMap<UUID, Race> races) { markDirty(); if (races == null) races = new HashMap<>(); this.races = races; }
    public Race getRace(UUID raceUUID) { return races.get(raceUUID); }
    public List<String> getRaceNames() {
        List<String> raceNames = new ArrayList<>();
        for (Race race : races.values()) {
            raceNames.add(race.getRaceName());
        }
        return raceNames;
    }
    public List<Race> getRacesList() { return new ArrayList<>(races.values()); }
    public Race getRaceByName(String raceName) {
        for (Race race : races.values()) {
            if (race.getRaceName().equalsIgnoreCase(raceName)) return race;
        }
        return null;
    }
    public boolean addRace(Race race) {
        if (races == null) races = new HashMap<>();
        if (races.containsKey(race.getRaceUUID()))
            return false;
        races.put(race.getRaceUUID(), race);
        markDirty();
        return true;
    }
    public boolean replaceRace(Race race) {
        if (races == null) return false;
        if (races.containsKey(race.getRaceUUID())) {
            races.replace(race.getRaceUUID(), race);
            markDirty();
            return true;
        }
        return false;
    }
    public void removeRace(Race race) {
        if (races == null) return;
        races.remove(race.getRaceUUID());
        markDirty();
    }

    public HashMap<UUID,Team> getTeams() { if (teams == null) teams = new HashMap<>(); return teams; }
    public void setTeams(HashMap<UUID, Team> teams) { markDirty(); if (teams == null) teams = new HashMap<>(); this.teams = teams; }

    public boolean addTeam(Team team) {
        if (teams != null && teams.containsKey(team.getUUID()))
            return false;
        if (teams == null) teams = new HashMap<>();
        teams.put(team.getUUID(), team);
        markDirty();
        return true;
    }

    public boolean replaceTeam(Team team) {
        if (teams != null && teams.containsKey(team.getUUID())) {
            teams.put(team.getUUID(), team);
            markDirty();
            return true;
        }
        return false;
    }

    public void removeTeam(Team team) {
        if (teams == null) return;
        teams.remove(team.getUUID());
        markDirty();
    }
}
