package xyz.acyber.oneLife.DataObjects.SubSettings;

import org.bukkit.GameMode;

public class Lives {
    private int cap = 2;
    private String gameModeAfterLastDeath = GameMode.ADVENTURE.name();

    public Lives() { super(); } // Default constructor
    public Lives(int cap, GameMode gameModeAfterLastDeath) {
    }
    public int getCap() { return cap; }
    public void setCap(int cap) { this.cap = cap; }
    public  String getGameModeAfterLastDeath() { return gameModeAfterLastDeath; }
    public void SetGameModeAfterLastDeath( String gameModeAfterLastDeath) {  this.gameModeAfterLastDeath = gameModeAfterLastDeath; }
}
