package xyz.acyber.oneLife.DataObjects.SubSettings;

import org.bukkit.GameMode;

import java.util.UUID;

public class Lives {

    private int cap;
    private GameMode gameModeAfterLastDeath;

    public Lives() {
        this.cap = 2;
        this.gameModeAfterLastDeath = GameMode.ADVENTURE;
    }

    public int getCap() { return cap; }
    public void setCap(int cap) { this.cap = cap; }

    public GameMode getGameModeAfterLastDeath() { return gameModeAfterLastDeath; }
    public void SetGameModeAfterLastDeath(GameMode gameModeAfterLastDeath) {  this.gameModeAfterLastDeath = gameModeAfterLastDeath; }

}
