package xyz.acyber.oneLife.DataObjects.SubSettings;

import org.bukkit.GameMode;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class PlayerConfig {

    private UUID uuid;
    private String name;
    Team team;
    private Race race;
    private boolean givenStartItems;
    private BukkitTask repeatItemsTask;
    private boolean climbingEnabled;
    private String playerClimbVines;

    public PlayerConfig() {
        super();
    }

    public PlayerConfig(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }

    public UUID getUUID() { return uuid; }
    public void setUUID(UUID uuid) { this.uuid = uuid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Race getRace() { return race; }
    public void setRace(Race race) { this.race = race; }

    public boolean isGivenStartItems() { return givenStartItems; }
    public void setGivenStartItems(boolean givenStartItems) { this.givenStartItems = givenStartItems; }

    public BukkitTask getRepeatItemsTask() { return repeatItemsTask; }
    public void setRepeatItemsTask(BukkitTask bukkitTask) { this.repeatItemsTask = bukkitTask; }

    public boolean isClimbingEnabled() { return climbingEnabled; }
    public void setclimbingEnabled(boolean enabled) { this.climbingEnabled = enabled; }

    public String getPlayerClimbVines() { return playerClimbVines; }
    public void setPlayerClimbVines(String playerClimbVines) {  this.playerClimbVines = playerClimbVines; }

}
