package xyz.acyber.oneLife.DataObjects.SubSettings;

import com.fasterxml.jackson.annotation.*;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Race {

    @JsonProperty("raceName")
    private String raceName = "";
    @JsonProperty("raceUUID")
    private UUID raceUUID = UUID.randomUUID();
    @JsonProperty("enabled")
    private boolean enabled = false;

    @JsonProperty("scale")
    private double scale = 0;
    @JsonProperty("reach")
    private double reach = 0;
    @JsonProperty("fallDamageMultiplier")
    private double fallDamageMultiplier = 1.0;
    @JsonProperty("flyingAttackDamageMultiplier")
    private double flyingAttackDamageMultiplier = 1.0;
    @JsonProperty("fireDamageCap")
    private double fireDamageCap = 0.0;
    @JsonProperty("fastSneak")
    private boolean fastSneak = false;
    @JsonProperty("isRawFoodSafe")
    private boolean isRawFoodSafe = false;
    @JsonProperty("hasFreezeTicks")
    private boolean hasFreezeTicks = true;
    @JsonProperty("sinksInWater")
    private boolean sinksInWater = true;
    @JsonProperty("isSlowOnLand")
    private boolean isSlowOnLand = false;
    @JsonProperty("canClimbWalls")
    private boolean canClimbWalls = false;
    @JsonProperty("isWeakUnderGround")
    private boolean isWeakUnderGround = false;

    @JsonProperty("assignedArmor")
    private AssignedArmor assignedArmor = new AssignedArmor();
    @JsonProperty("assignedItems")
    private List<AssignedItem> assignedItem = new ArrayList<>();
    @JsonProperty("allowedFoods")
    private List<Material> allowedFoods = new ArrayList<>();
    @JsonProperty("effects")
    private List<PotionEffect> effects = new ArrayList<>();
    @JsonProperty("buffedFoods")
    private List<BuffedFood> buffedFoods = new ArrayList<>();
    @JsonProperty("repeatItems")
    private List<RepeatItem> repeatItems = new ArrayList<>();
    @JsonProperty("startItems")
    private HashMap<String, Integer> startItems = new HashMap<>(); // Key is material, value is the number of items to give

    @JsonCreator
    public Race() { super(); } // Default constructor

    @JsonIgnore
    public Race(String raceName, boolean enabled, double scale, double reach) {
        this.raceName = raceName;
        this.enabled = enabled;
        this.scale = scale;
        this.reach = reach;
    }

    @JsonGetter
    public String getRaceName() { return raceName; }
    @JsonSetter
    public void setRaceName(String raceName) { this.raceName = raceName; }

    @JsonGetter
    public UUID getRaceUUID() { return raceUUID; }
    @JsonSetter
    public void setRaceUUID(UUID raceUUID) { this.raceUUID = raceUUID; }

    @JsonGetter
    public boolean isEnabled() { return enabled; }
    @JsonSetter
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    @JsonGetter
    public double getScale() { return scale; }
    @JsonSetter
    public void setScale(double scale) { this.scale = scale; }

    @JsonGetter
    public double getReach() { return reach; }
    @JsonSetter
    public void setReach(double reach) { this.reach = reach; }

    @JsonGetter
    public double getFallDamageMultiplier() { return fallDamageMultiplier; }
    @JsonSetter
    public void setFallDamageMultiplier(double fallDamageMultiplier) {  this.fallDamageMultiplier = fallDamageMultiplier; }

    @JsonGetter
    public double getFlyingAttackDamageMultiplier() { return flyingAttackDamageMultiplier; }
    @JsonSetter
    public void setFlyingAttackDamageMultiplier(double flyingAttackDamageMultiplier) { this.flyingAttackDamageMultiplier = flyingAttackDamageMultiplier; }

    @JsonGetter
    public double getFireDamageCap() { return fireDamageCap; }
    @JsonSetter
    public void setFireDamageCap(double fireDamageCap) { this.fireDamageCap = fireDamageCap; }

    @JsonGetter
    public boolean getFastSneak() { return fastSneak; }
    @JsonSetter
    public void setFastSneak(boolean fastSneak) { this.fastSneak = fastSneak; }

    @JsonGetter
    public boolean getIsRawFoodSafe() { return isRawFoodSafe; }
    @JsonSetter
    public void setIsRawFoodSafe(boolean isRawFoodSafe) { this.isRawFoodSafe = isRawFoodSafe; }

    @JsonGetter
    public boolean getHasFreezeTicks() { return hasFreezeTicks; }
    @JsonSetter
    public void setHasFreezeTicks(boolean hasFreezeTicks) {  this.hasFreezeTicks = hasFreezeTicks; }

    @JsonGetter
    public boolean getSinksInWater() { return sinksInWater; }
    @JsonSetter
    public void setSinksInWater(boolean sinksInWater) { this.sinksInWater = sinksInWater; }

    @JsonGetter
    public boolean getIsSlowOnLand() { return isSlowOnLand; }
    @JsonSetter
    public void setIsSlowOnLand(boolean isSlowOnLand) { this.isSlowOnLand = isSlowOnLand; }

    @JsonGetter
    public boolean getCanClimbWalls() { return canClimbWalls; }
    @JsonSetter
    public void setCanClimbWalls(boolean canClimbWalls) { this.canClimbWalls = canClimbWalls; }

    @JsonGetter
    public boolean getIsWeakUnderGround() { return isWeakUnderGround; }
    @JsonSetter
    public void setIsWeakUnderGround(boolean isWeakUnderGround) {  this.isWeakUnderGround = isWeakUnderGround; }

    @JsonGetter
    public List<Material> getAllowedFoods() { return allowedFoods; }
    @JsonSetter
    public void setAllowedFoods(List<Material> allowedFoods) { this.allowedFoods = allowedFoods; }

    @JsonGetter
    public List<PotionEffect> getEffects() { return effects; }
    @JsonSetter
    public void setEffects(List<PotionEffect> appliedEffects) {  this.effects = appliedEffects; }

    @JsonGetter
    public List<BuffedFood> getBuffedFoods() { return buffedFoods; }
    @JsonSetter
    public void setBuffedFoods(List<BuffedFood> buffedFoods) {  this.buffedFoods = buffedFoods; }

    @JsonGetter
    public AssignedArmor getArmor() { return assignedArmor; }
    @JsonSetter
    public void setArmor(AssignedArmor armor) { this.assignedArmor = armor; }

    @JsonGetter
    public List<AssignedItem> getAssignedItems() { return assignedItem; }
    @JsonSetter
    public void setAssignedItems(List<AssignedItem> items) { this.assignedItem = items; }

    @JsonGetter
    public List<RepeatItem> getRepeatItems() { return repeatItems; }
    @JsonSetter
    public void setRepeatItems(List<RepeatItem> repeatItems) { this.repeatItems = repeatItems; }

    @JsonGetter
    public HashMap<String, Integer> getStartItems() { return startItems; }
    @JsonSetter
    public void setStartItems(HashMap<String, Integer> startItems) { this.startItems = startItems; }

}
