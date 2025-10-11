package xyz.acyber.oneLife.DataObjects.SubSettings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

public class AFKCheckerConfig {

    private int minutesAFK = 5;
    private int secondsInterval = 1;
    private String afkWarning = "<bold><red>You will be marked afk ing <blue>%seconds% <red>seconds";
    private String kickedReason = "<red>You have been kicked for being AFK";

    @JsonCreator
    public AFKCheckerConfig() { super(); } // Default constructor

    @JsonIgnore
    public AFKCheckerConfig(int minutesAFK, int secondsInterval, String afkWarning, String kickedReason) {
        this.minutesAFK = minutesAFK;
        this.secondsInterval = secondsInterval;
        this.afkWarning = afkWarning;
        this.kickedReason = kickedReason;
    }

    /**
     * Gets the Minutes you can be AFK before marked as AFK.
     */
    @JsonGetter
    public int getMinutesAFK() { return minutesAFK; }

    /**
     * Sets the Minutes you can be AFK before marked as AFK.
     */
    @JsonSetter
    public void setMinutesAFK(int minutesAFK) { this.minutesAFK = minutesAFK; }

    /**
     * Gets the Seconds between each check of if players should be AFK.
     */
    @JsonGetter
    public int getSecondsInterval() { return secondsInterval; }

    /**
     * Sets the Seconds between each check of if players should be AFK.
     */
    @JsonSetter
    public void setSecondsInterval(int secondsInterval) { this.secondsInterval = secondsInterval; }

    /**
     * Gets the Formated Template String used to send player a warning message.
     * Example: <bold><red>You will be marked afk ing <blue>%seconds% <red>seconds
     */
    @JsonGetter
    public String getAfkWarning() { return afkWarning; }

    /**
     * Sets the Formated Template String used to send player a warning message.
     * Example: <bold><red>You will be marked afk ing <blue>%seconds% <red>seconds
     */
    @JsonSetter
    public void setAfkWarning(String afkWarning) { this.afkWarning = afkWarning; }

    /**
     * Gets the Formated Template String used to declare the reason the Player was Kicked.
     * Example: <red>You have been kicked for being AFK
     */
    @JsonGetter
    public String getKickedReason() { return kickedReason; }

    /**
     * Sets the Formated Template String used to declare the reason the Player was Kicked.
     * Example: <red>You have been kicked for being AFK
     */
    @JsonSetter
    public void setKickedReason(String kickedReason) { this.kickedReason = kickedReason; }

}
