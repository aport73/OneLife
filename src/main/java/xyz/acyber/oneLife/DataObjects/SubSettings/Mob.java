package xyz.acyber.oneLife.DataObjects.SubSettings;

import com.fasterxml.jackson.annotation.*;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import java.util.HashMap;

public class Mob {

    @JsonProperty("mobType")
    private String mobType = null;
    @JsonProperty("noBabies")
    private boolean noBabies = false;
    @JsonProperty("equipment")
    private HashMap<String, String> equipment = null; // SlotType -> Item
    @JsonProperty("drops")
    private HashMap<Material, HashMap<String, Integer>> drops = null;
    @JsonProperty("buffedAttributes")
    private HashMap<Attribute, HashMap<String, Double>> buffedAttributes = null;

    @JsonCreator
    public Mob() { super(); } // Default constructor

    @JsonIgnore
    public Mob(String mobType) {
        this.mobType = mobType;
        equipment = null;
        drops = null;
        buffedAttributes = null;
    }


    @JsonGetter
    public String getMobType() { return mobType; }
    @JsonSetter
    public void setMobType(String mobType) { this.mobType = mobType; }

    @JsonGetter
    public boolean getNoBabies() { return noBabies; }
    @JsonSetter
    public void setNoBabies(boolean noBabies) { this.noBabies = noBabies; }

    @JsonGetter
    public HashMap<String, String> getEquipment() { if (equipment == null) equipment = new HashMap<>(); return equipment; }
    @JsonSetter
    public void setEquipment(HashMap<String, String> equipment) {  this.equipment = equipment; }

    @JsonGetter
    public HashMap<Material, HashMap<String, Integer>> getDrops() { if (drops == null ) drops = new HashMap<>(); return drops; }
    @JsonSetter
    public void setDrops(HashMap<Material, HashMap<String, Integer>> drops) { this.drops = drops; }

    @JsonGetter
    public HashMap<Attribute, HashMap<String, Double>> getBuffedAttributes() { return buffedAttributes; }
    @JsonSetter
    public void setBuffedAttributes(HashMap<Attribute, HashMap<String, Double>> buffedAttributes) { this.buffedAttributes = buffedAttributes; }

}
