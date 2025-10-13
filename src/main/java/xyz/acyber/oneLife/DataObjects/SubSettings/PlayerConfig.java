package xyz.acyber.oneLife.DataObjects.SubSettings;

import com.fasterxml.jackson.annotation.*;
import java.util.UUID;

public class PlayerConfig {

    @JsonProperty("uuid")
    private UUID uuid = null;
    @JsonProperty("name")
    private String name = "";
    @JsonProperty("team")
    private UUID teamUUID = null;
    @JsonProperty("race")
    private UUID raceUUID = null;
    @JsonProperty("givenStartItems")
    private boolean givenStartItems = false;
    @JsonProperty("repeatItemsTask")
    private int repeatItemsTaskId = 0;
    @JsonProperty("climbingEnabled")
    private boolean climbingEnabled = false;
    @JsonProperty("playerClimbVines")
    private String playerClimbVines = null;

    @JsonCreator
    public PlayerConfig() { super(); } // Default constructor

    @JsonIgnore
    public PlayerConfig(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;

    }

    @JsonGetter
    public UUID getTeamUUID() { return teamUUID; }
    @JsonSetter
    public void setTeamUUID(UUID teamUUID) { this.teamUUID = teamUUID; }

    @JsonGetter
    public UUID getUUID() { return uuid; }
    @JsonSetter
    public void setUUID(UUID uuid) { this.uuid = uuid; }

    @JsonGetter
    public String getName() { return name; }
    @JsonSetter
    public void setName(String name) { this.name = name; }

    @JsonGetter
    public UUID getRaceUUID() { return raceUUID; }
    @JsonSetter
    public void setRaceUUID(UUID raceUUID) { this.raceUUID = raceUUID; }

    @JsonGetter
    public boolean isGivenStartItems() { return givenStartItems; }
    @JsonSetter
    public void setGivenStartItems(boolean givenStartItems) { this.givenStartItems = givenStartItems; }

    @JsonGetter
    public int getRepeatItemsTask() { return repeatItemsTaskId; }
    @JsonSetter
    public void setRepeatItemsTask(int bukkitTask) { this.repeatItemsTaskId = bukkitTask; }

    @JsonGetter
    public boolean isClimbingEnabled() { return climbingEnabled; }
    @JsonSetter
    public void setClimbingEnabled(boolean enabled) { this.climbingEnabled = enabled; }

    @JsonGetter
    public String getPlayerClimbVines() { return playerClimbVines; }
    @JsonSetter
    public void setPlayerClimbVines(String playerClimbVines) {  this.playerClimbVines = playerClimbVines; }

}
