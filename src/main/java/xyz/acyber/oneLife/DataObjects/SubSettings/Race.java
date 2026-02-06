package xyz.acyber.oneLife.DataObjects.SubSettings;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Race {

    private String raceName = "";
    private UUID raceUUID = UUID.randomUUID();
    private boolean enabled = false;

    private double scale = 0;
    private double reach = 0;
    private double fallDamageMultiplier = 1.0;
    private double flyingAttackDamageMultiplier = 1.0;
    private double fireDamageCap = 0.0;
    private boolean fastSneak = false;
    private boolean isRawFoodSafe = false;
    private boolean hasFreezeTicks = true;
    private boolean sinksInWater = true;
    private boolean isSlowOnLand = false;
    private boolean canClimbWalls = false;
    private boolean isWeakUnderGround = false;

    private AssignedArmor assignedArmor = new AssignedArmor();
    private List<AssignedItem> assignedItem = new ArrayList<>();
    private List<Material> allowedFoods = new ArrayList<>();
    private List<PotionEffect> effects = new ArrayList<>();
    private List<BuffedFood> buffedFoods = new ArrayList<>();
    private List<RepeatItem> repeatItems = new ArrayList<>();
    private HashMap<String, Integer> startItems = new HashMap<>(); // Key is material, value is the number of items to give

    public Race() { super(); } // Default constructor

    public Race(String raceName, boolean enabled, double scale, double reach) {
        this.raceName = raceName;
        this.enabled = enabled;
        this.scale = scale;
        this.reach = reach;
    }

    public String getRaceName() { return raceName; }
    public void setRaceName(String raceName) { this.raceName = raceName; }

    public UUID getRaceUUID() { return raceUUID; }
    public void setRaceUUID(UUID raceUUID) { this.raceUUID = raceUUID; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public double getScale() { return scale; }
    public void setScale(double scale) { this.scale = scale; }

    public double getReach() { return reach; }
    public void setReach(double reach) { this.reach = reach; }

    public double getFallDamageMultiplier() { return fallDamageMultiplier; }
    public void setFallDamageMultiplier(double fallDamageMultiplier) {  this.fallDamageMultiplier = fallDamageMultiplier; }

    public double getFlyingAttackDamageMultiplier() { return flyingAttackDamageMultiplier; }
    public void setFlyingAttackDamageMultiplier(double flyingAttackDamageMultiplier) { this.flyingAttackDamageMultiplier = flyingAttackDamageMultiplier; }

    public double getFireDamageCap() { return fireDamageCap; }
    public void setFireDamageCap(double fireDamageCap) { this.fireDamageCap = fireDamageCap; }

    public boolean getFastSneak() { return fastSneak; }
    public void setFastSneak(boolean fastSneak) { this.fastSneak = fastSneak; }

    public boolean getIsRawFoodSafe() { return isRawFoodSafe; }
    public void setIsRawFoodSafe(boolean isRawFoodSafe) { this.isRawFoodSafe = isRawFoodSafe; }

    public boolean getHasFreezeTicks() { return hasFreezeTicks; }
    public void setHasFreezeTicks(boolean hasFreezeTicks) {  this.hasFreezeTicks = hasFreezeTicks; }

    public boolean getSinksInWater() { return sinksInWater; }
    public void setSinksInWater(boolean sinksInWater) { this.sinksInWater = sinksInWater; }

    public boolean getIsSlowOnLand() { return isSlowOnLand; }
    public void setIsSlowOnLand(boolean isSlowOnLand) { this.isSlowOnLand = isSlowOnLand; }

    public boolean getCanClimbWalls() { return canClimbWalls; }
    public void setCanClimbWalls(boolean canClimbWalls) { this.canClimbWalls = canClimbWalls; }

    public boolean getIsWeakUnderGround() { return isWeakUnderGround; }
    public void setIsWeakUnderGround(boolean isWeakUnderGround) {  this.isWeakUnderGround = isWeakUnderGround; }

    public List<Material> getAllowedFoods() { return allowedFoods; }
    public void setAllowedFoods(List<Material> allowedFoods) { this.allowedFoods = allowedFoods; }

    public List<PotionEffect> getEffects() { return effects; }
    public void setEffects(List<PotionEffect> appliedEffects) {  this.effects = appliedEffects; }

    public List<BuffedFood> getBuffedFoods() { return buffedFoods; }
    public void setBuffedFoods(List<BuffedFood> buffedFoods) {  this.buffedFoods = buffedFoods; }

    public AssignedArmor getArmor() { return assignedArmor; }
    public void setArmor(AssignedArmor armor) { this.assignedArmor = armor; }

    public List<AssignedItem> getAssignedItems() { return assignedItem; }
    public void setAssignedItems(List<AssignedItem> items) { this.assignedItem = items; }

    public List<RepeatItem> getRepeatItems() { return repeatItems; }
    public void setRepeatItems(List<RepeatItem> repeatItems) { this.repeatItems = repeatItems; }

    public HashMap<String, Integer> getStartItems() { return startItems; }
    public void setStartItems(HashMap<String, Integer> startItems) { this.startItems = startItems; }

}
