package xyz.acyber.oneLife.DataObjects.SubSettings;

import com.fasterxml.jackson.annotation.*;
import org.bukkit.GameMode;

import javax.annotation.Nullable;
import java.util.UUID;

public class Lives {

    @JsonProperty("cap")
    private int cap = 2;
    @JsonProperty("gameModeAfterLastDeath")
    private String gameModeAfterLastDeath = GameMode.ADVENTURE.name();

    @JsonCreator
    public Lives() { super(); } // Default constructor

    @JsonIgnore
    public Lives(int cap, GameMode gameModeAfterLastDeath) {
    }

    @JsonGetter
    public int getCap() { return cap; }
    @JsonSetter
    public void setCap(int cap) { this.cap = cap; }

    @JsonGetter
    public  String getGameModeAfterLastDeath() { return gameModeAfterLastDeath; }
    @JsonSetter
    public void SetGameModeAfterLastDeath( String gameModeAfterLastDeath) {  this.gameModeAfterLastDeath = gameModeAfterLastDeath; }

}
