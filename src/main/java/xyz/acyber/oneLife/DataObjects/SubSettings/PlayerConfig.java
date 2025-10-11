package xyz.acyber.oneLife.DataObjects.SubSettings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nullable;
import java.util.UUID;

public class PlayerConfig {

    private @Nullable UUID uuid;
    private @Nullable String name;
    private @Nullable Team team;
    private @Nullable Race race;
    private boolean givenStartItems;
    private @Nullable BukkitTask repeatItemsTask;
    private boolean climbingEnabled;
    private @Nullable String playerClimbVines;

    @JsonCreator
    public PlayerConfig() { super(); } // Default constructor

    @JsonIgnore
    public PlayerConfig(@org.jetbrains.annotations.Nullable UUID uuid, @org.jetbrains.annotations.Nullable String name) {
        this.uuid = uuid;
        this.name = name;
    }

    @JsonGetter
    public @Nullable Team getTeam() { return team; }
    @JsonSetter
    public void setTeam(@Nullable Team team) { this.team = team; }

    @JsonGetter
    public UUID getUUID() { return uuid; }
    @JsonSetter
    public void setUUID(UUID uuid) { this.uuid = uuid; }

    @JsonGetter
    public @org.jetbrains.annotations.Nullable String getName() { return name; }
    @JsonSetter
    public void setName(@org.jetbrains.annotations.Nullable String name) { this.name = name; }

    @JsonGetter
    public @Nullable Race getRace() { return race; }
    @JsonSetter
    public void setRace(@Nullable Race race) { this.race = race; }

    @JsonGetter
    public boolean isGivenStartItems() { return givenStartItems; }
    @JsonSetter
    public void setGivenStartItems(boolean givenStartItems) { this.givenStartItems = givenStartItems; }

    @JsonGetter
    public @Nullable BukkitTask getRepeatItemsTask() { return repeatItemsTask; }
    @JsonSetter
    public void setRepeatItemsTask(@Nullable BukkitTask bukkitTask) { this.repeatItemsTask = bukkitTask; }

    @JsonGetter
    public boolean isClimbingEnabled() { return climbingEnabled; }
    @JsonSetter
    public void setClimbingEnabled(boolean enabled) { this.climbingEnabled = enabled; }

    @JsonGetter
    public @org.jetbrains.annotations.Nullable String getPlayerClimbVines() { return playerClimbVines; }
    @JsonSetter
    public void setPlayerClimbVines(@org.jetbrains.annotations.Nullable String playerClimbVines) {  this.playerClimbVines = playerClimbVines; }

}
