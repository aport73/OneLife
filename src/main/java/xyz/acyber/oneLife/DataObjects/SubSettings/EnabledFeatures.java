package xyz.acyber.oneLife.DataObjects.SubSettings;

public class EnabledFeatures {
    private boolean RaceManager;
    private boolean MobManager;
    private boolean ScoreManager;
    private boolean LivesManager;
    private boolean LifeGifting;
    private boolean AFKChecker;
    private boolean NightHostiles;

    public EnabledFeatures() {
        this.RaceManager = true;
        this.MobManager = true;
        this.ScoreManager = true;
        this.LivesManager = true;
        this.LifeGifting = true;
        this.AFKChecker = true;
        this.NightHostiles = true;
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
