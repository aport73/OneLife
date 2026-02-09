package xyz.acyber.oneLife.DataObjects.SubSettings;

import org.bukkit.Material;

import java.util.UUID;

public class RepeatItem {

    private final UUID UUID = java.util.UUID.randomUUID();
    private String materialType = null;
    private int max = 0;
    private int qtyPerRepeat = 0;
    private int secondsTillRepeat = 0;

    public RepeatItem() { super(); } // Default constructor

    public RepeatItem(String materialType, int max, int qtyPerRepeat, int secondsTillRepeat) {
        this.materialType = materialType;
        this.max = max;
        this.qtyPerRepeat = qtyPerRepeat;
        this.secondsTillRepeat = secondsTillRepeat;
    }

    public UUID getUUID() { return UUID; }

    public String getMaterialType() { return materialType; }
    public void setMaterialType(String materialType) { this.materialType = materialType; }
    public Material getMaterial() { return Material.valueOf(materialType); }

    public int getMax() { return max; }
    public void setMax(int max) { this.max = max; }

    public int getQtyPerRepeat() { return qtyPerRepeat; }
    public void setQtyPerRepeat(int qty) { this.qtyPerRepeat = qty; }

    public int getSecondsTillRepeat() { return secondsTillRepeat; }
    public void setSecondsTillRepeat(int seconds) { this.secondsTillRepeat = seconds; }

}
