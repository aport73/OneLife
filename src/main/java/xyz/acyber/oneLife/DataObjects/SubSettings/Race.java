package xyz.acyber.oneLife.DataObjects.SubSettings;

import com.fasterxml.jackson.annotation.*;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import xyz.acyber.oneLife.DataObjects.SubSettings.SubRace.BuffedFood;
import xyz.acyber.oneLife.DataObjects.SubSettings.SubRace.RepeatItem;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Race {

    @JsonProperty("raceName")
    private String raceName = null;
    @JsonProperty("raceUUID")
    private UUID raceUUID = null;
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

    @JsonProperty("allowedFoods")
    private List<Material> allowedFoods = new ArrayList<>();
    @JsonProperty("effects")
    private List<Effect> effects = new ArrayList<>();

    @JsonProperty("buffedFoods")
    private HashMap<Material, BuffedFood> buffedFoods = new HashMap<>();
    @JsonProperty("equipment")
    private HashMap<String, String> equipment = new HashMap<>(); // Key is inventory slotType, value is item string
    @JsonProperty("repeatItems")
    private HashMap<Material, RepeatItem> repeatItems = new HashMap<>();
    @JsonProperty("startItems")
    private HashMap<Material, Integer> startItems = new HashMap<>();

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
    public List<Effect> getEffects() { return effects; }
    @JsonSetter
    public void setEffects(List<Effect> appliedEffects) {  this.effects = appliedEffects; }

    @JsonGetter
    public HashMap<Material, BuffedFood> getBuffedFoods() { return buffedFoods; }
    @JsonSetter
    public void setBuffedFoods(HashMap<Material, BuffedFood> buffedFoods) {  this.buffedFoods = buffedFoods; }
    @JsonIgnore
    public boolean addBuffedFood(Material mat, BuffedFood food) {
        if (buffedFoods == null) buffedFoods = new HashMap<>();
        if (buffedFoods.containsKey(mat))
            return false;
        buffedFoods.put(mat, food);
        return true;
    }
    @JsonIgnore
    public boolean modifyBuffedFood(Material mat, BuffedFood food) {
        if (buffedFoods == null) buffedFoods = new HashMap<>();
        if (buffedFoods.containsKey(mat)) {
            buffedFoods.replace(mat, food);
            return true;
        }
        return false;
    }
    @JsonIgnore
    public void removeBuffedFood(Material mat) { if (buffedFoods == null) buffedFoods = new HashMap<>(); buffedFoods.remove(mat); }

    @JsonGetter
    public @Nullable HashMap<String, String> getEquipment() { return equipment; }
    @JsonSetter
    public void setEquipment(HashMap<String, String> equipment) { this.equipment = equipment; }
    @JsonIgnore
    public boolean addEquipment(String slot, ItemStack item) {
        if (equipment == null) equipment = new HashMap<>();
        if (equipment.containsKey(slot))
            return false;
        equipment.put(slot, item.toString());
        return true;
    }
    @JsonIgnore
    public boolean modifyEquipment(String slot, ItemStack item) {
        if (equipment == null) equipment = new HashMap<>();
        if (equipment.containsKey(slot)) {
            equipment.replace(slot, item.toString());
            return true;
        }
        return false;
    }
    @JsonIgnore
    public void removeEquipment(String slot) { if (equipment == null) return; equipment.remove(slot); }

    @JsonGetter
    public HashMap<Material, RepeatItem> getRepeatItems() { return repeatItems; }
    @JsonSetter
    public void setRepeatItems(HashMap<Material, RepeatItem> repeatItems) { this.repeatItems = repeatItems; }
    @JsonIgnore
    public boolean addRepeatItem(Material mat, RepeatItem item) {
        if (repeatItems == null) repeatItems = new HashMap<>();
        if (repeatItems.containsKey(mat))
            return false;
        repeatItems.put(mat, item);
        return true;
    }
    @JsonIgnore
    public boolean modifyRepeatItem(Material mat, RepeatItem item) {
        if (repeatItems == null) repeatItems = new HashMap<>();
        if (repeatItems.containsKey(mat)) {
            repeatItems.put(mat, item);
            return true;
        }
        return false;
    }
    @JsonIgnore
    public void removeRepeatItem(Material mat) { if (repeatItems == null) return; repeatItems.remove(mat); }

    @JsonGetter
    public HashMap<Material, Integer> getStartItems() { return startItems; }
    @JsonSetter
    public void setStartItems(HashMap<Material, Integer> startItems) { this.startItems = startItems; }
    @JsonIgnore
    public boolean addStartItem(Material mat, Integer startItem) {
        if (startItems == null) startItems = new HashMap<>();
        if (startItems.containsKey(mat))
            return false;
        startItems.put(mat, startItem);
        return true;
    }
    @JsonIgnore
    public boolean modifyStartItem(Material mat, Integer startItem) {
        if (startItems == null) startItems = new HashMap<>();
        if (startItems.containsKey(mat)) {
            startItems.put(mat, startItem);
            return true;
        }
        return false;
    }
    @JsonIgnore
    public void removeStartItem(Material mat) { if (startItems == null) return; startItems.remove(mat); }
}
