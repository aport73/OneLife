package xyz.acyber.oneLife.DataObjects.SubSettings;

import java.util.ArrayList;
import java.util.List;

public class MobConfig {

    private String mobType = null;
    private boolean noBabies = false;
    private AssignedArmor equipment = null; // Key is inventory slotType, value is the ItemStack
    private List<MobDrop> drops = null;
    private List<BuffedAttribute> buffedAttributes = null;
    private MobHostility mobHostility = null;

    public MobConfig() { super(); } // Default constructor

    public MobConfig(String mobType) {
        this.mobType = mobType;
        equipment = null;
        drops = null;
        buffedAttributes = null;
    }

    public String getMobType() { return mobType; }
    public void setMobType(String mobType) { this.mobType = mobType; }

    public boolean getNoBabies() { return noBabies; }
    public void setNoBabies(boolean noBabies) { this.noBabies = noBabies; }

    public AssignedArmor getEquipment() { if (equipment == null) equipment = new AssignedArmor(); return equipment; }
    public void setEquipment(AssignedArmor equipment) {  this.equipment = equipment; }

    public List<MobDrop> getDrops() { if (drops == null ) drops = new ArrayList<>(); return drops; }
    public void setDrops(List<MobDrop> drops) { this.drops = drops; }

    public List<BuffedAttribute> getBuffedAttributes() { return buffedAttributes; }
    public void setBuffedAttributes(List<BuffedAttribute> buffedAttributes) { this.buffedAttributes = buffedAttributes; }

    public MobHostility getMobHostility() { return mobHostility; }
    public void setMobHostility(MobHostility mobHostility) { this.mobHostility = mobHostility; }

}
