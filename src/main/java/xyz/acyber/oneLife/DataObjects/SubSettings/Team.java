package xyz.acyber.oneLife.DataObjects.SubSettings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import javax.annotation.Nullable;
import java.util.UUID;

public class Team {

    private UUID uuid;
    private String teamName;
    private String prefix;
    private String suffix;
    private String color;

    @JsonCreator
    public Team() { super(); } // Default constructor

    @JsonIgnore
    public Team(UUID uuid, String teamName, String prefix, String suffix, String color) {
        this.uuid = uuid;
        this.teamName = teamName;
        this.prefix = prefix;
        this.suffix = suffix;
        this.color = color;
    }

    @JsonGetter
    public UUID getUUID() { return uuid; }
    @JsonSetter
    public void setUUID(UUID uuid) { this.uuid = uuid; }

    @JsonGetter
    public String getTeamName() { return teamName; }
    @JsonSetter
    public void setTeamName(String teamName) { this.teamName = teamName; }

    @JsonGetter
    public String getPrefix() { return prefix; }
    @JsonSetter
    public void setPrefix(String prefix) { this.prefix = prefix; }

    @JsonGetter
    public String getSuffix() { return suffix; }
    @JsonSetter
    public void setSuffix(String suffix) { this.suffix = suffix; }

    @JsonGetter
    public String getColor() { return color; }
    @JsonSetter
    public void setColor(String color) { this.color = color; }

}
