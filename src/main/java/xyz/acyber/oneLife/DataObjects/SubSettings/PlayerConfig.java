package xyz.acyber.oneLife.DataObjects.SubSettings;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jline.utils.ShutdownHooks;
import xyz.acyber.oneLife.OneLifePlugin;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerConfig {

    private UUID uuid = null;
    private String name = "";
    private UUID teamUUID = null;
    private UUID raceUUID = null;
    private boolean givenStartItems = false;
    private boolean climbingEnabled = false;
    private List<Location> climbingData = null;

    private HashMap<Integer, UUID> runningTasks = null;

    public PlayerConfig() { super(); } // Default constructor

    public PlayerConfig(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public PlayerConfig(@NotNull OfflinePlayer player) {
        this.uuid = player.getUniqueId();
        this.name = player.getName();
    }

    public UUID getTeamUUID() { return teamUUID; }
    public void setTeamUUID(UUID teamUUID) { this.teamUUID = teamUUID; }

    public UUID getUUID() { return uuid; }
    public void setUUID(UUID uuid) { this.uuid = uuid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public UUID getRaceUUID() { return raceUUID; }
    public void setRaceUUID(UUID raceUUID) { this.raceUUID = raceUUID; }

    public boolean getGivenStartItems() { return givenStartItems; }
    public void setGivenStartItems(boolean givenStartItems) { this.givenStartItems = givenStartItems; }

    public boolean isClimbingEnabled() { return climbingEnabled; }
    public void setClimbingEnabled(boolean enabled) { this.climbingEnabled = enabled; }

    public List<Location> getClimbingData() { return climbingData; }
    public void setClimbingData(List<Location> climbingData) { this.climbingData = climbingData; }
    public void appendClimbingData(Location vine) { if (climbingData == null) climbingData = new java.util.ArrayList<>(); climbingData.add(vine); }
    public void removeClimbingData(Location vine) { if (climbingData != null) climbingData.remove(vine); }
    public void resetClimbingData() { climbingData = null; }

    public HashMap<Integer, UUID> getRunningTasks() { if (runningTasks == null) runningTasks = new HashMap<>(); return runningTasks; }
    public void setRunningTasks(HashMap<Integer, UUID> runningTasks) { this.runningTasks = runningTasks; }
    public void addRunningTask(Integer taskId, UUID taskUUID) { if (runningTasks == null) runningTasks = new HashMap<>(); runningTasks.put(taskId, taskUUID); }
    public void removeRunningTask(Integer taskId) { if (runningTasks != null) runningTasks.remove(taskId); }

}
