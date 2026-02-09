package xyz.acyber.oneLife.DataObjects;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import xyz.acyber.oneLife.DataObjects.SubSettings.AFKCheckerConfig;
import xyz.acyber.oneLife.DataObjects.SubSettings.BuffedAttribute;
import xyz.acyber.oneLife.DataObjects.SubSettings.EnabledFeatures;
import xyz.acyber.oneLife.DataObjects.SubSettings.Lives;
import xyz.acyber.oneLife.DataObjects.SubSettings.MobConfig;
import xyz.acyber.oneLife.DataObjects.SubSettings.MobDrop;
import xyz.acyber.oneLife.DataObjects.SubSettings.PlayerConfig;
import xyz.acyber.oneLife.DataObjects.SubSettings.Race;
import xyz.acyber.oneLife.DataObjects.SubSettings.Scoring;
import xyz.acyber.oneLife.DataObjects.SubSettings.Team;
import xyz.acyber.oneLife.OneLifePlugin;

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

    public boolean importDefaultMobConfigsFromResource() {
        if (OLP == null) return false;
        if (mobs != null && !mobs.isEmpty()) return false;

        try (InputStream in = OLP.getResource("mobs.json")) {
            if (in == null) return false;
            String json = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            HashMap<String, MobConfig> defaults = new HashMap<>();

            for (String rootKey : root.keySet()) {
                JsonObject mobJson = root.getAsJsonObject(rootKey);
                String mobType = mobJson.has("mobType") ? mobJson.get("mobType").getAsString() : rootKey;
                MobConfig config = new MobConfig(mobType);

                JsonElement noBabies = mobJson.has("noBabies") ? mobJson.get("noBabies") : mobJson.get("NOBABYS");
                if (noBabies != null && noBabies.isJsonPrimitive()) {
                    config.setNoBabies(noBabies.getAsBoolean());
                }

                JsonElement handMaterial = mobJson.get("handMaterial");
                if (handMaterial != null && handMaterial.isJsonPrimitive()) {
                    try {
                        config.setHandMaterial(Material.valueOf(handMaterial.getAsString()));
                    } catch (IllegalArgumentException ignored) {
                        OLP.getLogger().warning(() -> "Invalid handMaterial for " + mobType + ": " + handMaterial.getAsString());
                    }
                }

                List<MobDrop> drops = new ArrayList<>();
                if (mobJson.has("drops") && mobJson.get("drops").isJsonArray()) {
                    mobJson.getAsJsonArray("drops").forEach(dropEl -> {
                        if (!dropEl.isJsonObject()) return;
                        JsonObject dropObj = dropEl.getAsJsonObject();
                        String materialName = dropObj.has("materialName") ? dropObj.get("materialName").getAsString() : null;
                        int min = dropObj.has("min") ? dropObj.get("min").getAsInt() : 0;
                        int max = dropObj.has("max") ? dropObj.get("max").getAsInt() : 0;
                        if (materialName != null) drops.add(new MobDrop(materialName, min, max));
                    });
                } else if (mobJson.has("DROPS") && mobJson.get("DROPS").isJsonObject()) {
                    JsonObject dropsJson = mobJson.getAsJsonObject("DROPS");
                    for (String dropKey : dropsJson.keySet()) {
                        JsonObject dropObj = dropsJson.getAsJsonObject(dropKey);
                        int min = dropObj.has("MIN") ? dropObj.get("MIN").getAsInt() : 0;
                        int max = dropObj.has("MAX") ? dropObj.get("MAX").getAsInt() : 0;
                        drops.add(new MobDrop(dropKey, min, max));
                    }
                }
                if (!drops.isEmpty()) config.setDrops(drops);

                List<BuffedAttribute> buffs = new ArrayList<>();
                if (mobJson.has("buffedAttributes") && mobJson.get("buffedAttributes").isJsonArray()) {
                    mobJson.getAsJsonArray("buffedAttributes").forEach(buffEl -> {
                        if (!buffEl.isJsonObject()) return;
                        JsonObject buffObj = buffEl.getAsJsonObject();
                        String attrName = buffObj.has("attribute") ? buffObj.get("attribute").getAsString() : null;
                        double base = buffObj.has("base") ? buffObj.get("base").getAsDouble() : 0.0;
                        double variance = buffObj.has("variance") ? buffObj.get("variance").getAsDouble() : 0.0;
                        if (attrName != null) {
                            Attribute attr = null;
                            try {
                                attr = org.bukkit.Registry.ATTRIBUTE.get(new org.bukkit.NamespacedKey("minecraft", attrName.toLowerCase()));
                            } catch (IllegalArgumentException ignored) {
                                OLP.getLogger().warning(() -> "Invalid attribute for " + mobType + ": " + attrName);
                            }
                            if (attr != null) {
                                buffs.add(new BuffedAttribute(attr, base, variance));
                            }
                        }
                    });
                } else if (mobJson.has("BUFFS") && mobJson.get("BUFFS").isJsonObject()) {
                    JsonObject buffsJson = mobJson.getAsJsonObject("BUFFS");
                    if (buffsJson.has("SIZE")) {
                        JsonObject size = buffsJson.getAsJsonObject("SIZE");
                        double base = size.has("BASE") ? size.get("BASE").getAsDouble() : 0.0;
                        double variance = size.has("VARIANCE") ? size.get("VARIANCE").getAsDouble() : 0.0;
                        buffs.add(new BuffedAttribute(Attribute.SCALE, base, variance));
                    }
                    if (buffsJson.has("SPEED")) {
                        JsonObject speed = buffsJson.getAsJsonObject("SPEED");
                        double base = speed.has("BASE") ? speed.get("BASE").getAsDouble() : 0.0;
                        double variance = speed.has("VARIANCE") ? speed.get("VARIANCE").getAsDouble() : 0.0;
                        buffs.add(new BuffedAttribute(Attribute.MOVEMENT_SPEED, base, variance));
                    }
                }
                if (!buffs.isEmpty()) config.setBuffedAttributes(buffs);

                if (mobJson.has("ITEMS")) {
                    OLP.getLogger().warning(() -> "mobs.json ITEMS entries are not supported by MobConfig and were ignored for: " + mobType);
                }

                defaults.put(mobType, config);
            }

            setMobs(defaults);
            return true;
        } catch (IOException | IllegalStateException e) {
            if (OLP != null) {
                OLP.getLogger().warning(() -> "Failed to import mobs.json: " + e.getMessage());
            }
            return false;
        }
    }

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
        PlayerConfig config = getPlayerConfig(player);
        if (config == null || config.getRaceUUID() == null) return null;
        return races.get(config.getRaceUUID());
    }
    public Race getPlayerRace(OfflinePlayer player) {
        PlayerConfig config = getPlayerConfig(player);
        if (config == null || config.getRaceUUID() == null) return null;
        return races.get(config.getRaceUUID());
    }
    public void setPlayerRace(Player player, Race race) {
        if (race == null) return;
        PlayerConfig config = getPlayerConfig(player);
        if (config == null) return;
        config.setRaceUUID(race.getRaceUUID());
        markDirty();
    }
    public void setPlayerRace(OfflinePlayer player, Race race) {
        if (race == null) return;
        PlayerConfig config = getPlayerConfig(player);
        if (config == null) return;
        config.setRaceUUID(race.getRaceUUID());
        markDirty();
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
