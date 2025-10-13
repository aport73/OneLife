package xyz.acyber.oneLife.DataObjects.SubSettings;

import com.fasterxml.jackson.annotation.*;
import xyz.acyber.oneLife.Managers.ScoreManager;

public class EnabledFeatures {
    @JsonProperty("enabledRaceManager")
    private boolean RaceManager;
    @JsonProperty("enabledMobManager")
    private boolean MobManager;
    @JsonProperty("enabledScoreManager")
    private boolean ScoreManager;
    @JsonProperty("enabledLivesManager")
    private boolean LivesManager;
    @JsonProperty("enabledLifeGifting")
    private boolean LifeGifting;
    @JsonProperty("enabledAFKChecker")
    private boolean AFKChecker;
    @JsonProperty("enabledNightHostiles")
    private boolean NightHostiles;

    @JsonCreator
    public EnabledFeatures() { super(); } // Default constructor

    @JsonIgnore
    public EnabledFeatures(boolean RaceManager, boolean MobManager, boolean ScoreManager, boolean LivesManager, boolean LifeGifting, boolean AFKChecker, boolean NightHostiles) {
        this.RaceManager = RaceManager;
        this.MobManager = MobManager;
        this.ScoreManager = ScoreManager;
        this.LivesManager = LivesManager;
        this.LifeGifting = LifeGifting;
        this.AFKChecker = AFKChecker;
        this.NightHostiles = NightHostiles;
    }

    @JsonGetter
    public boolean getEnabledRaceManager() { return RaceManager; }
    @JsonGetter
    public boolean getEnabledMobManager() { return MobManager; }
    @JsonGetter
    public boolean getEnabledScoreManager() { return ScoreManager; }
    @JsonGetter
    public boolean getEnabledLivesManager() { return LivesManager; }
    @JsonGetter
    public boolean getEnabledLifeGifting() { return LifeGifting; }
    @JsonGetter
    public boolean getEnabledAFKChecker() { return AFKChecker; }
    @JsonGetter
    public boolean getEnabledNightHostiles() { return NightHostiles; }

    @JsonSetter
    public void setEnabledRaceManager(boolean enabled) { RaceManager = enabled; }
    @JsonSetter
    public void setEnabledMobManager(boolean enabled) { MobManager = enabled; }
    @JsonSetter
    public void setEnabledScoreManager(boolean enabled) { ScoreManager = enabled; }
    @JsonSetter
    public void setEnabledLivesManager(boolean enabled) { LivesManager = enabled; }
    @JsonSetter
    public void setEnabledLifeGifting(boolean enabled) { LifeGifting = enabled; }
    @JsonSetter
    public void setEnabledAFKChecker(boolean enabled) { AFKChecker = enabled; }
    @JsonSetter
    public void setEnabledNightHostiles(boolean enabled) { NightHostiles = enabled; }

}
