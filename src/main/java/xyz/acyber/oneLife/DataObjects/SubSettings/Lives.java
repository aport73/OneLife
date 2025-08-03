package xyz.acyber.oneLife.DataObjects.SubSettings;

import org.bukkit.GameMode;

import java.util.UUID;

public class Lives {

    private int cap = 2;
    private GameMode gameModeAfterLastDeath = GameMode.ADVENTURE;

    public Lives() {
        super();
    }
    public Lives(int cap, GameMode gameModeAfterLastDeath) {
    }

    public int getCap() { return cap; }
    public void setCap(int cap) { this.cap = cap; }

    public GameMode getGameModeAfterLastDeath() { return gameModeAfterLastDeath; }
    public void SetGameModeAfterLastDeath(GameMode gameModeAfterLastDeath) {  this.gameModeAfterLastDeath = gameModeAfterLastDeath; }

}
