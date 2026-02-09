package xyz.acyber.oneLife.DataObjects.SubSettings;

public class EnabledFeatures {
    private boolean RaceManager;
    private boolean MobManager;
    private boolean ScoreManager;
    private boolean LivesManager;
    private boolean LifeGifting;
    private boolean AFKChecker;
    private boolean NightHostiles;

    public EnabledFeatures() { super(); } // Default constructor

    public EnabledFeatures(boolean RaceManager, boolean MobManager, boolean ScoreManager, boolean LivesManager, boolean LifeGifting, boolean AFKChecker, boolean NightHostiles) {
        this.RaceManager = RaceManager;
        this.MobManager = MobManager;
        this.ScoreManager = ScoreManager;
        this.LivesManager = LivesManager;
        this.LifeGifting = LifeGifting;
        this.AFKChecker = AFKChecker;
        this.NightHostiles = NightHostiles;
    }

    public boolean getEnabledRaceManager() { return RaceManager; }
    public boolean getEnabledMobManager() { return MobManager; }
    public boolean getEnabledScoreManager() { return ScoreManager; }
    public boolean getEnabledLivesManager() { return LivesManager; }
    public boolean getEnabledLifeGifting() { return LifeGifting; }
    public boolean getEnabledAFKChecker() { return AFKChecker; }
    public boolean getEnabledNightHostiles() { return NightHostiles; }

    public void setEnabledRaceManager(boolean enabled) { RaceManager = enabled; }
    public void setEnabledMobManager(boolean enabled) { MobManager = enabled; }
    public void setEnabledScoreManager(boolean enabled) { ScoreManager = enabled; }
    public void setEnabledLivesManager(boolean enabled) { LivesManager = enabled; }
    public void setEnabledLifeGifting(boolean enabled) { LifeGifting = enabled; }
    public void setEnabledAFKChecker(boolean enabled) { AFKChecker = enabled; }
    public void setEnabledNightHostiles(boolean enabled) { NightHostiles = enabled; }
}
