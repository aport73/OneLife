package xyz.acyber.oneLife.DataObjects.SubSettings;

import org.bukkit.Material;

public class MobDrop {

    private String materialName = "";
    private int min = 0;
    private int max = 0;

    public MobDrop() { super(); }

    public MobDrop(String materialName, int min, int max) {
        this.materialName = materialName;
        this.min = min;
        this.max = max;
    }

    public MobDrop(Material material, int min, int max) {
        this.materialName = material.name();
        this.min = min;
        this.max = max;
    }

    public String getMaterialName() { return materialName; }
    public void setMaterial(String materialName) { this.materialName = materialName; }
    public Material getMaterial() { return Material.valueOf(materialName); }
    public int getMin() { return min; }
    public void setMin(int min) { this.min = min; }
    public int getMax() { return max; }
    public void setMax(int max) { this.max = max; }
}
