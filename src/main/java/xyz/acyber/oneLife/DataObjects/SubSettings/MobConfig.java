package xyz.acyber.oneLife.DataObjects.SubSettings;

import com.fasterxml.jackson.annotation.*;

import java.util.ArrayList;
import java.util.List;

public class MobConfig {

    @JsonProperty("mobType")
    private String mobType = null;
    @JsonProperty("noBabies")
    private boolean noBabies = false;
    @JsonProperty("equipment")
    private AssignedArmor equipment = null; // Key is inventory slotType, value is the ItemStack
    @JsonProperty("drops")
    private List<MobDrop> drops = null;
    @JsonProperty("buffedAttributes")
    private List<BuffedAttribute> buffedAttributes = null;
    @JsonProperty("MobHostility")
    private MobHostility mobHostility = null;

    @JsonCreator
    public MobConfig() { super(); } // Default constructor

    @JsonIgnore
    public MobConfig(String mobType) {
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
    public AssignedArmor getEquipment() { if (equipment == null) equipment = new AssignedArmor(); return equipment; }
    @JsonSetter
    public void setEquipment(AssignedArmor equipment) {  this.equipment = equipment; }

    @JsonGetter
    public List<MobDrop> getDrops() { if (drops == null ) drops = new ArrayList<>(); return drops; }
    @JsonSetter
    public void setDrops(List<MobDrop> drops) { this.drops = drops; }

    @JsonGetter
    public List<BuffedAttribute> getBuffedAttributes() { return buffedAttributes; }
    @JsonSetter
    public void setBuffedAttributes(List<BuffedAttribute> buffedAttributes) { this.buffedAttributes = buffedAttributes; }

    @JsonGetter
    public MobHostility getMobHostility() { return mobHostility; }
    @JsonSetter
    public void setMobHostility(MobHostility mobHostility) { this.mobHostility = mobHostility; }

}
