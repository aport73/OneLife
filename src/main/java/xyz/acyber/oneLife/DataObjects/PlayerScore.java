package xyz.acyber.oneLife.DataObjects;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.acyber.oneLife.DataObjects.SubScoreData.MaterialInteractions;
import xyz.acyber.oneLife.DataObjects.SubScoreData.MobsKilled;
import xyz.acyber.oneLife.DataObjects.SubSettings.Team;
import xyz.acyber.oneLife.OneLifePlugin;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class PlayerScore {

    private transient OneLifePlugin OLP = JavaPlugin.getPlugin(OneLifePlugin.class);

    private UUID uuid = null;
    private String playerName = null;
    private Team team = null;
    private int livesBoughtBack = 0;
    private HashMap<String, Double> deaths = new HashMap<>(); // key is gamemode, value is the number of deaths in that gamemode
    private HashMap<String, Double> xp = new HashMap<>(); // key is gamemode, value is the xp in that gamemode
    private HashMap<String, Double> onlineHr = new HashMap<>(); // key is gamemode, value is the online hours in that gamemode
    private HashMap<String, Integer> blocksPlaced = new HashMap<>(); // key is gamemode, value is the number of blocks placed in that gamemode
    private HashMap<String, Integer> blocksMined = new HashMap<>(); // key is gamemode, value is the number of blocks mined in that gamemode
    private HashMap<String, Integer> harvested = new HashMap<>(); // key is gamemode, value is the number of items harvested in that gamemode
    private HashMap<String, Integer> caught = new HashMap<>(); // key is gamemode, value is the number of items caught in that gamemode
    private HashMap<String, Integer> achievements = new HashMap<>(); // key is gamemode, value is the number of achievements unlocked in that gamemode
    private HashMap<EntityType, MobsKilled> typeMobsKilled = new HashMap<>(); // key is entity type, value is MobsKilled object
    private HashMap<Material, MaterialInteractions> typeBlocksMined = new HashMap<>();
    private HashMap<Material, MaterialInteractions> typeBlocksPlaced = new HashMap<>();
    private HashMap<Material, MaterialInteractions> typeItemsHarvested = new HashMap<>();

    private double afkPointsOffset = 0;


    public PlayerScore() {
        super();
    }

    public PlayerScore(OneLifePlugin OLP, OfflinePlayer player) {
        this.OLP = OLP;
        this.uuid = player.getUniqueId();
        this.playerName = player.getName();
    }
    private void markDirty() {
        if (OLP != null) OLP.markScoresDirty();
    }
    public void setPlayer(OfflinePlayer offlinePlayer) {
        markDirty();
        this.uuid = offlinePlayer.getUniqueId();
        this.playerName = offlinePlayer.getName();
    }

    public UUID getUUID() { return uuid; }
    public void setUUID(UUID uuid) { markDirty(); this.uuid = uuid; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { markDirty(); this.playerName = playerName; }

    public Team getTeam() { return team; }
    public void setTeam(Team team) { markDirty(); this.team = team; }

    public int getLivesBoughtBack() { return livesBoughtBack; }
    public void setLivesBoughtBack(int livesBoughtBack) { markDirty(); this.livesBoughtBack = livesBoughtBack; }

    public HashMap<String, Double> getDeaths() { return deaths; }
    public void setDeaths(HashMap<String, Double> deaths) { markDirty(); this.deaths = deaths; }
    public void incrementDeaths(String gameMode) {
        markDirty();
        if (deaths.containsKey(gameMode))
            deaths.replace(gameMode, deaths.get(gameMode) + 1);
        else
            deaths.put(gameMode, 1.0);
    }

    public HashMap<String, Double> getXp() { return xp; }
    public void setXp(HashMap<String, Double> xp) { markDirty(); this.xp = xp; }

    public HashMap<String, Double> getOnlineHr() { return onlineHr; }
    public void setOnlineHr(HashMap<String, Double> onlineHr) { markDirty(); this.onlineHr = onlineHr; }
    /**
     * Increments the online hours for the given gamemode by 1 of the given unit.
     * Unit accepts any of, [Hr, Min, Sec]
     * Defaults to Sec
     */
    public void incrementOnlineHr(String gameMode, String unit) {
        markDirty();
        double additionalTime;

        if (unit.equals("Hr"))
            additionalTime = 1.0;
        else if (unit.equals("Min"))
            additionalTime = 1.0/60;
        else
            additionalTime = 1.0/60/60;

        if (onlineHr.containsKey(gameMode))
            onlineHr.replace(gameMode, onlineHr.get(gameMode) + additionalTime);
        else
            onlineHr.put(gameMode, additionalTime);
    }

    public HashMap<String, Integer> getBlocksPlaced() { return blocksPlaced; }
    public void setBlocksPlaced(HashMap<String, Integer> blocksPlaced) { markDirty(); this.blocksPlaced = blocksPlaced; }
    public void incrementBlocksPlaced(String gameMode) {
        markDirty();
        if (blocksPlaced.containsKey(gameMode))
            blocksPlaced.replace(gameMode, blocksPlaced.get(gameMode) + 1);
        else
            blocksPlaced.put(gameMode, 1);
    }

    public HashMap<String, Integer> getBlocksMined() { return blocksMined; }
    public void setBlocksMined(HashMap<String, Integer> blocksMined) { markDirty(); this.blocksMined = blocksMined; }
    public void incrementBlocksMined(String gameMode) {
        markDirty();
        if (blocksMined.containsKey(gameMode))
            blocksMined.replace(gameMode, blocksMined.get(gameMode) + 1);
        else
            blocksMined.put(gameMode, 1);
    }

    public HashMap<String, Integer> getHarvested() { return harvested; }
    public void setHarvested(HashMap<String, Integer> harvested) { markDirty(); this.harvested = harvested; }
    public void incrementHarvestedItems(String gameMode) {
        markDirty();
        if (harvested.containsKey(gameMode))
            harvested.replace(gameMode, harvested.get(gameMode) + 1);
        else
            harvested.put(gameMode, 1);
    }

    public HashMap<String, Integer> getCaught() { return caught; }
    public void setCaught(HashMap<String, Integer> caught) { markDirty(); this.caught = caught; }
    public void incrementCaughtItems(String gameMode) {
        markDirty();
        if (caught.containsKey(gameMode))
            caught.replace(gameMode, caught.get(gameMode) + 1);
        else
            caught.put(gameMode, 1);
    }

    public HashMap<String, Integer> getAchievements() { return achievements; }
    public void setAchievements(HashMap<String, Integer> achievements) { markDirty(); this.achievements = achievements; }
    public void incrementAchievements(String gameMode) {
        markDirty();
        if (achievements.containsKey(gameMode))
            achievements.replace(gameMode, achievements.get(gameMode) + 1);
        else
            achievements.put(gameMode, 1);
    }

    public HashMap<EntityType, MobsKilled> getTypeMobsKilled() { return typeMobsKilled; }
    public void setTypeMobsKilled(HashMap<EntityType, MobsKilled> typeMobs) { markDirty(); this.typeMobsKilled = typeMobs; }
    public void incrementTypeMobsKilled(EntityType mobType, String gameMode) {
        markDirty();
        if (typeMobsKilled.containsKey(mobType))
            typeMobsKilled.get(mobType).incrementCount(gameMode);
        else {
            HashMap<String, Integer> count = new HashMap<>();
            count.put(gameMode, 1);
            typeMobsKilled.put(mobType, new MobsKilled(mobType, count));
        }
    }
    public double getIndividualMobKilledPoints(EntityType mobType) {
        double individualMobKilledPoints = 0;
        if (typeMobsKilled.containsKey(mobType)) {
            for (String key : typeMobsKilled.get(mobType).getCount().keySet()) {
                double multiplier = Objects.requireNonNullElse(OLP.settings.getScoring().getGamemodeMultipliers().get(key).getMobKillMultipliers().get(mobType), OLP.settings.getScoring().getDefaultMobKillMultiplier());
                individualMobKilledPoints += typeMobsKilled.get(mobType).getCount().get(key) * multiplier;
            }
        }
        return individualMobKilledPoints;
    }

    public HashMap<Material, MaterialInteractions> getTypeBlocksMined() { return typeBlocksMined; }
    public void setTypeBlocksMined(HashMap<Material, MaterialInteractions> typeBlocksMined) { markDirty(); this.typeBlocksMined = typeBlocksMined; }
    public void incrementTypeBlocksMined(Material material, String gameMode) {
        markDirty();
        if (typeBlocksMined.containsKey(material))
            typeBlocksMined.get(material).incrementCount(gameMode);
        else {
            HashMap<String, Integer> count = new HashMap<>();
            count.put(gameMode, 1);
            typeBlocksMined.put(material, new MaterialInteractions(material, count));
        }
    }
    public double getIndividualBlocksMinedPoints(Material material) {
        double individualBlocksMinedPoints = 0;
        if (typeBlocksMined.containsKey(material)) {
            for (String gameMode: typeBlocksMined.get(material).getCount().keySet()) {
                double multiplier = Objects.requireNonNullElse(OLP.settings.getScoring().getGamemodeMultipliers().get(gameMode).getBlockMineMultipliers().get(material), OLP.settings.getScoring().getDefaultBlocksMinedMultiplier());
                individualBlocksMinedPoints += typeBlocksMined.get(material).getCount().get(gameMode) * multiplier;
            }
        }
        return individualBlocksMinedPoints;
    }

    public HashMap<Material, MaterialInteractions> getTypeBlocksPlaced() { return typeBlocksPlaced; }
    public void setTypeBlocksPlaced(HashMap<Material, MaterialInteractions> typeBlocksPlaced) { markDirty(); this.typeBlocksPlaced = typeBlocksPlaced; }
    public void incrementTypeBlocksPlaced(Material material, String gameMode) {
        markDirty();
        if (typeBlocksPlaced.containsKey(material))
            typeBlocksPlaced.get(material).incrementCount(gameMode);
        else {
            HashMap<String, Integer> count = new HashMap<>();
            count.put(gameMode, 1);
            typeBlocksPlaced.put(material, new MaterialInteractions(material, count));
        }
    }

    public HashMap<Material, MaterialInteractions> getTypeItemsHarvested() { return typeItemsHarvested; }
    public void setTypeItemsHarvested(HashMap<Material, MaterialInteractions> typeItemsHarvested) { markDirty(); this.typeItemsHarvested = typeItemsHarvested; }
    public void incrementTypeItemsHarvested(Material material, String gameMode) {
        markDirty();
        if (typeItemsHarvested.containsKey(material))
            typeItemsHarvested.get(material).incrementCount(gameMode);
        else {
            HashMap<String, Integer> count = new HashMap<>();
            count.put(gameMode, 1);
            typeItemsHarvested.put(material, new MaterialInteractions(material, count));
        }
    }

    public double getDeathPoints() {
        double deathPoints = 0;
        for (String key : deaths.keySet()) {
            double multiplier = OLP.settings.getScoring().getGamemodeMultipliers().get(key).getDeathMultiplier();
            deathPoints += deaths.get(key) * multiplier;
        }
        return deathPoints;
    }

    public double getXpPoints() {
        double xpPoints = 0;
        for (String key : xp.keySet()) {
            if (key.equals("AFK")) {
                double multiplier = OLP.settings.getScoring().getGamemodeMultipliers().get(GameMode.SURVIVAL.name()).getXpMultiplier() * OLP.settings.getScoring().getDefaultAFKMultiplier();
                afkPointsOffset += xp.get(key) * multiplier;
            } else {
                double multiplier = OLP.settings.getScoring().getGamemodeMultipliers().get(GameMode.valueOf(key).name()).getXpMultiplier();
                xpPoints += xp.get(key) * multiplier;
            }
        }
        return  xpPoints;
    }

    public double getOnlineHrPoints() {
        double onlineHrPoints = 0;
        for (String key : onlineHr.keySet()) {
            if (key.equals("AFK")) {
                double multiplier = OLP.settings.getScoring().getGamemodeMultipliers().get(GameMode.SURVIVAL.name()).getXpMultiplier() * OLP.settings.getScoring().getDefaultAFKMultiplier();
                afkPointsOffset += onlineHr.get(key) * multiplier;
            } else {
                double multiplier = OLP.settings.getScoring().getGamemodeMultipliers().get(GameMode.valueOf(key).name()).getXpMultiplier();
                onlineHrPoints += onlineHr.get(key) * multiplier;
            }
        }
        return  onlineHrPoints;
    }

    public double getLivesBoughtBackPoints() {
        return livesBoughtBack * OLP.settings.getScoring().getLivesBuyBackMultiplier();
    }

    public double getDefaultBlocksPlacedPoints() {
        double defaultBlocksPlacedPoints = 0;
        for (String key : blocksPlaced.keySet()) {
            if (key.equals("AFK")) {
                double multiplier = OLP.settings.getScoring().getGamemodeMultipliers().get(GameMode.SURVIVAL.name()).getXpMultiplier() * OLP.settings.getScoring().getDefaultAFKMultiplier();
                afkPointsOffset += blocksPlaced.get(key) * multiplier;
            } else {
                double multiplier = OLP.settings.getScoring().getGamemodeMultipliers().get(GameMode.valueOf(key).name()).getXpMultiplier();
                defaultBlocksPlacedPoints += blocksPlaced.get(key) * multiplier;
            }
        }
        return defaultBlocksPlacedPoints;
    }

    public double getDefaultBlocksMinedPoints() {
        double defaultBlocksMinedPoints = 0;
        for (String key : blocksMined.keySet()) {
            if (key.equals("AFK")) {
                double multiplier = OLP.settings.getScoring().getGamemodeMultipliers().get(GameMode.SURVIVAL.name()).getXpMultiplier() * OLP.settings.getScoring().getDefaultAFKMultiplier();
                afkPointsOffset += blocksMined.get(key) * multiplier;
            } else {
                double mulitpler = OLP.settings.getScoring().getGamemodeMultipliers().get(GameMode.valueOf(key).name()).getXpMultiplier();
                defaultBlocksMinedPoints += blocksMined.get(key) * mulitpler;
            }
        }
        return defaultBlocksMinedPoints;
    }

    public double getDefaultHarvestedPoints() {
        double defaultHarvestedPoints = 0;
        for (String key : harvested.keySet()) {
            if (key.equals("AFK")) {
                double multiplier = OLP.settings.getScoring().getGamemodeMultipliers().get(GameMode.SURVIVAL.name()).getXpMultiplier()  * OLP.settings.getScoring().getDefaultAFKMultiplier();
                afkPointsOffset += harvested.get(key) * multiplier;
            } else {
                double mulitpler = OLP.settings.getScoring().getGamemodeMultipliers().get(GameMode.valueOf(key).name()).getXpMultiplier();
                defaultHarvestedPoints += harvested.get(key) * mulitpler;
            }
        }
        return defaultHarvestedPoints;
    }

    public double getDefaultCaughtPoints() {
        double defaultCaughtPoints = 0;
        for (String key : caught.keySet()) {
            if (key.equals("AFK")) {
                double multiplier = OLP.settings.getScoring().getGamemodeMultipliers().get(GameMode.SURVIVAL.name()).getXpMultiplier() * OLP.settings.getScoring().getDefaultAFKMultiplier();
                afkPointsOffset += caught.get(key) * multiplier;
            } else {
                double multiplier = OLP.settings.getScoring().getGamemodeMultipliers().get(GameMode.valueOf(key).name()).getXpMultiplier();
                defaultCaughtPoints += caught.get(key) * multiplier;
            }
        }
        return defaultCaughtPoints;
    }

    public double getDefaultAchievementPoints() {
        double defaultAchievementPoints = 0;
        for (String key : achievements.keySet()) {
            if (key.equals("AFK")) {
                double multiplier = OLP.settings.getScoring().getGamemodeMultipliers().get(GameMode.SURVIVAL.name()).getXpMultiplier() * OLP.settings.getScoring().getDefaultAFKMultiplier();
                afkPointsOffset += achievements.get(key) * multiplier;
            } else {
                double multiplier = OLP.settings.getScoring().getGamemodeMultipliers().get(GameMode.valueOf(key).name()).getXpMultiplier();
                defaultAchievementPoints += achievements.get(key) * multiplier;
            }
        }
        return defaultAchievementPoints;
    }

    public double getTypeMobTotalPoints() {
        double typeMobTotalPoints = 0;
        for (EntityType mobEntityType : typeMobsKilled.keySet()) {
            for (String key : typeMobsKilled.get(mobEntityType).getCount().keySet()) {
                if (key.equals("AFK")) {
                    double multiplier = OLP.settings.getScoring().getGamemodeMultipliers().get(GameMode.SURVIVAL.name()).getMobKillMultipliers().get(mobEntityType) * OLP.settings.getScoring().getDefaultAFKMultiplier();
                    afkPointsOffset += typeMobsKilled.get(mobEntityType).getCount().get(key) * multiplier;
                } else {
                    double multiplier = OLP.settings.getScoring().getGamemodeMultipliers().get(GameMode.valueOf(key).name()).getMobKillMultipliers().get(mobEntityType);
                    typeMobTotalPoints += typeMobsKilled.get(mobEntityType).getCount().get(key) * multiplier;
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
                    double multiplier = OLP.settings.getScoring().getGamemodeMultipliers().get(GameMode.SURVIVAL.name()).getBlockMineMultipliers().get(material) * OLP.settings.getScoring().getDefaultAFKMultiplier();
                    afkPointsOffset += typeBlocksMined.get(material).getCount().get(key) * multiplier;
                } else {
                    double multiplier = OLP.settings.getScoring().getGamemodeMultipliers().get(GameMode.valueOf(key).name()).getBlockMineMultipliers().get(material);
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
                    double multiplier = OLP.settings.getScoring().getGamemodeMultipliers().get(GameMode.SURVIVAL.name()).getBlockPlaceMultipliers().get(material) * OLP.settings.getScoring().getDefaultAFKMultiplier();
                    afkPointsOffset += typeBlocksPlaced.get(material).getCount().get(key) * multiplier;
                } else {
                    double multiplier = OLP.settings.getScoring().getGamemodeMultipliers().get(GameMode.valueOf(key).name()).getBlockPlaceMultipliers().get(material);
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
                    double multiplier = OLP.settings.getScoring().getGamemodeMultipliers().get(GameMode.SURVIVAL.name()).getHarvestMultipliers().get(material) * OLP.settings.getScoring().getDefaultAFKMultiplier();
                    afkPointsOffset += typeItemsHarvested.get(material).getCount().get(key) * multiplier;
                } else {
                    double multiplier = OLP.settings.getScoring().getGamemodeMultipliers().get(GameMode.valueOf(key).name()).getHarvestMultipliers().get(material);
                    typeItemsHarvestedTotalPoints += typeItemsHarvested.get(material).getCount().get(key) * multiplier;
                }
            }
        }
        return typeItemsHarvestedTotalPoints;

    }

    public double getAFKPointsOffset() { return afkPointsOffset; }

    public double totalPoints() {
        return round(getDeathPoints() + getAFKPointsOffset() + getLivesBoughtBackPoints() + getXpPoints() + getOnlineHrPoints() + getDefaultBlocksPlacedPoints() +
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
