package xyz.acyber.oneLife.DataObjects.SubSettings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.bukkit.GameMode;

import javax.annotation.Nullable;
import java.util.UUID;

public class Lives {

    private int cap = 2;
    private @Nullable GameMode gameModeAfterLastDeath = GameMode.ADVENTURE;

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
    public @Nullable GameMode getGameModeAfterLastDeath() { return gameModeAfterLastDeath; }
    @JsonSetter
    public void SetGameModeAfterLastDeath(@Nullable GameMode gameModeAfterLastDeath) {  this.gameModeAfterLastDeath = gameModeAfterLastDeath; }

}
