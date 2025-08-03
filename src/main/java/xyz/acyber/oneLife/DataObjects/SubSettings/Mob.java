package xyz.acyber.oneLife.DataObjects.SubSettings;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;

public class Mob {

    private EntityType mobType;
    private boolean noBabies = false;
    private HashMap<InventoryType.SlotType, ItemStack> equipment;
    private HashMap<Material, HashMap<String, Integer>> drops;
    private HashMap<Attribute, HashMap<String, Double>> buffedAttributes;

    public Mob () {
        super();
    }

    public Mob(EntityType mobType) {
        this.mobType = mobType;
        equipment = new HashMap<>();
        drops = new HashMap<>();
        buffedAttributes = new HashMap<>();
    }

    public EntityType getMobType() { return mobType; }
    public void setMobType(EntityType mobType) { this.mobType = mobType; }

    public boolean getNoBabies() { return noBabies; };
    public void setNoBabies(boolean noBabies) { this.noBabies = noBabies; }

    public HashMap<InventoryType.SlotType, ItemStack> getEquipment() { return equipment; }
    public void setEquipment(HashMap<InventoryType.SlotType, ItemStack> equipment) {  this.equipment = equipment; }

    public HashMap<Material, HashMap<String, Integer>> getDrops() { return drops; }
    public void setDrops(HashMap<Material, HashMap<String, Integer>> drops) { this.drops = drops; }

    public HashMap<Attribute, HashMap<String, Double>> getBuffedAttributes() { return buffedAttributes; }
    public void setBuffedAttributes(HashMap<Attribute, HashMap<String, Double>> buffedAttributes) { this.buffedAttributes = buffedAttributes; }

}
