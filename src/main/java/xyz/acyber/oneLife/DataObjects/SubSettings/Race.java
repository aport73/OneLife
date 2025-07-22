package xyz.acyber.oneLife.DataObjects.SubSettings;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import xyz.acyber.oneLife.DataObjects.SubSettings.SubRace.BuffedFood;
import xyz.acyber.oneLife.DataObjects.SubSettings.SubRace.RepeatItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Race {

    private String raceName;
    private boolean enabled;

    private double scale;
    private double reach;
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

    private List<Material> allowedFoods = new ArrayList<>();
    private List<Effect> effects = new ArrayList<>();

    private HashMap<Material, BuffedFood> buffedFoods = new HashMap<>();
    private HashMap<InventoryType.SlotType, ItemStack> equipment = new HashMap<>();
    private HashMap<Material, RepeatItem> repeatItems = new HashMap<>();
    private HashMap<Material, Integer> startItems = new HashMap<>();

    public Race(String raceName, boolean enabled, double scale, double reach) {
        this.raceName = raceName;
        this.enabled = enabled;
        this.scale = scale;
        this.reach = reach;
    }

    public String getRaceName() { return raceName; }
    public void setRaceName(String raceName) { this.raceName = raceName; }

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

    public List<Effect> getEffects() { return effects; }
    public void setEffects(List<Effect> appliedEffects) {  this.effects = appliedEffects; }

    public HashMap<Material, BuffedFood> getBuffedFoods() { return buffedFoods; }
    public void setBuffedFoods(HashMap<Material, BuffedFood> buffedFoods) {  this.buffedFoods = buffedFoods; }
    public boolean addBuffedFood(Material mat, BuffedFood food) {
        if (buffedFoods.containsKey(mat))
            return false;
        buffedFoods.put(mat, food);
        return true;
    }
    public boolean modifyBuffedFood(Material mat, BuffedFood food) {
        if (buffedFoods.containsKey(mat)) {
            buffedFoods.replace(mat, food);
            return true;
        }
        return false;
    }
    public void removeBuffedFood(Material mat) { buffedFoods.remove(mat); }

    public HashMap<InventoryType.SlotType, ItemStack> getEquipment() { return equipment; }
    public void setEquipment(HashMap<InventoryType.SlotType, ItemStack> equipment) { this.equipment = equipment; }
    public boolean addEquipment(InventoryType.SlotType slot, ItemStack item) {
        if (equipment.containsKey(slot))
            return false;
        equipment.put(slot, item);
        return true;
    }
    public boolean modifyEquipment(InventoryType.SlotType slot, ItemStack item) {
        if (equipment.containsKey(slot)) {
            equipment.replace(slot, item);
            return true;
        }
        return false;
    }
    public void removeEquipment(InventoryType.SlotType slot) { equipment.remove(slot); }

    public HashMap<Material, RepeatItem> getRepeatItems() { return repeatItems; }
    public void setRepeatItems(HashMap<Material, RepeatItem> repeatItems) { this.repeatItems = repeatItems; }
    public boolean addRepeatItem(Material mat, RepeatItem item) {
        if (repeatItems.containsKey(mat))
            return false;
        repeatItems.put(mat, item);
        return true;
    }
    public boolean modifyRepeatItem(Material mat, RepeatItem item) {
        if (repeatItems.containsKey(mat)) {
            repeatItems.put(mat, item);
            return true;
        }
        return false;
    }
    public void removeRepeatItem(Material mat) { repeatItems.remove(mat); }

    public HashMap<Material, Integer> getStartItems() { return startItems; }
    public void setStartItems(HashMap<Material, Integer> startItems) { this.startItems = startItems; }
    public boolean addStartItem(Material mat, Integer startItem) {
        if (startItems.containsKey(mat))
            return false;
        startItems.put(mat, startItem);
        return true;
    }
    public boolean modifyStartItem(Material mat, Integer startItem) {
        if (startItems.containsKey(mat)) {
            startItems.put(mat, startItem);
            return true;
        }
        return false;
    }
    public void removeStartItem(Material mat) { startItems.remove(mat); }
}
