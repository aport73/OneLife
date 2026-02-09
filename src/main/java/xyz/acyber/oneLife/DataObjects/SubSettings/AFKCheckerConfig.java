package xyz.acyber.oneLife.DataObjects.SubSettings;

public class AFKCheckerConfig {

    private int minutesAFK = 5;
    private int secondsInterval = 1;
    private String afkWarning = "<bold><red>You will be marked afk ing <blue>%seconds% <red>seconds";
    private String kickedReason = "<red>You have been kicked for being AFK";

    public AFKCheckerConfig() { super(); } // Default constructor

    public AFKCheckerConfig(int minutesAFK, int secondsInterval, String afkWarning, String kickedReason) {
        this.minutesAFK = minutesAFK;
        this.secondsInterval = secondsInterval;
        this.afkWarning = afkWarning;
        this.kickedReason = kickedReason;
    }

    /**
     * Gets the Minutes you can be AFK before marked as AFK.
     */
    public int getMinutesAFK() { return minutesAFK; }

    /**
     * Sets the Minutes you can be AFK before marked as AFK.
     */
    public void setMinutesAFK(int minutesAFK) { this.minutesAFK = minutesAFK; }

    /**
     * Gets the Seconds between each check of if players should be AFK.
     */
    public int getSecondsInterval() { return secondsInterval; }

    /**
     * Sets the Seconds between each check of if players should be AFK.
     */
    public void setSecondsInterval(int secondsInterval) { this.secondsInterval = secondsInterval; }

    /**
     * Gets the Formated Template String used to send player a warning message.
     * Example: <bold><red>You will be marked afk ing <blue>%seconds% <red>seconds
     */
    public String getAfkWarning() { return afkWarning; }

    /**
     * Sets the Formated Template String used to send player a warning message.
     * Example: <bold><red>You will be marked afk ing <blue>%seconds% <red>seconds
     */
    public void setAfkWarning(String afkWarning) { this.afkWarning = afkWarning; }

    /**
     * Gets the Formated Template String used to declare the reason the Player was Kicked.
     * Example: <red>You have been kicked for being AFK
     */
    public String getKickedReason() { return kickedReason; }

    /**
     * Sets the Formated Template String used to declare the reason the Player was Kicked.
     * Example: <red>You have been kicked for being AFK
     */
    public void setKickedReason(String kickedReason) { this.kickedReason = kickedReason; }

}
