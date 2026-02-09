package xyz.acyber.oneLife.DataObjects.SubSettings;

import java.util.UUID;

public class Team {

    private UUID uuid = UUID.randomUUID();
    private String teamName = "";
    private String prefix = "";
    private String suffix = "";
    private String color = "";

    public Team() { super(); } // Default constructor

    public Team(UUID uuid, String teamName, String prefix, String suffix, String color) {
        this.uuid = uuid;
        this.teamName = teamName;
        this.prefix = prefix;
        this.suffix = suffix;
        this.color = color;
    }

    public UUID getUUID() { return uuid; }
    public void setUUID(UUID uuid) { this.uuid = uuid; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public String getPrefix() { return prefix; }
    public void setPrefix(String prefix) { this.prefix = prefix; }

    public String getSuffix() { return suffix; }
    public void setSuffix(String suffix) { this.suffix = suffix; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

}
