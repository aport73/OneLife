package xyz.acyber.oneLife.DataObjects.SubSettings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.Nullable;
import java.util.HashMap;

public class Mob {

    private EntityType mobType;
    private boolean noBabies = false;
    private @Nullable HashMap<InventoryType.SlotType, String> equipment;
    private @Nullable HashMap<Material, HashMap<String, Integer>> drops;
    private @Nullable HashMap<Attribute, HashMap<String, Double>> buffedAttributes;

    @JsonCreator
    public Mob() { super(); } // Default constructor

    @JsonIgnore
    public Mob(EntityType mobType) {
        this.mobType = mobType;
        equipment = null;
        drops = null;
        buffedAttributes = null;
    }

    @JsonGetter
    public EntityType getMobType() { return mobType; }
    @JsonSetter
    public void setMobType(EntityType mobType) { this.mobType = mobType; }

    @JsonGetter
    public boolean getNoBabies() { return noBabies; }
    @JsonSetter
    public void setNoBabies(boolean noBabies) { this.noBabies = noBabies; }

    @JsonGetter
    public @org.jetbrains.annotations.Nullable HashMap<InventoryType.SlotType, String> getEquipment() { return equipment; }
    @JsonSetter
    public void setEquipment(@org.jetbrains.annotations.Nullable HashMap<InventoryType.SlotType, String> equipment) {  this.equipment = equipment; }

    @JsonGetter
    public @org.jetbrains.annotations.Nullable HashMap<Material, HashMap<String, Integer>> getDrops() { return drops; }
    @JsonSetter
    public void setDrops(@org.jetbrains.annotations.Nullable HashMap<Material, HashMap<String, Integer>> drops) { this.drops = drops; }

    @JsonGetter
    public @org.jetbrains.annotations.Nullable HashMap<Attribute, HashMap<String, Double>> getBuffedAttributes() { return buffedAttributes; }
    @JsonSetter
    public void setBuffedAttributes(@org.jetbrains.annotations.Nullable HashMap<Attribute, HashMap<String, Double>> buffedAttributes) { this.buffedAttributes = buffedAttributes; }

}
