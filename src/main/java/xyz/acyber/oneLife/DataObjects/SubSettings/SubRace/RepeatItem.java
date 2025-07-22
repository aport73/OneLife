package xyz.acyber.oneLife.DataObjects.SubSettings.SubRace;

import org.bukkit.Material;

public class RepeatItem {

    private Material material;
    private int max;
    private int qtyPerRepeat;
    private int secondsTillRepeat;

    public RepeatItem(Material material, int max, int qtyPerRepeat, int secondsTillRepeat) {
        this.material = material;
        this.max = max;
        this.qtyPerRepeat = qtyPerRepeat;
        this.secondsTillRepeat = secondsTillRepeat;
    }

    public Material getMaterial() { return material; }
    public void setMaterial(Material material) { this.material = material; }

    public int getMax() { return max; }
    public void setMax(int max) { this.max = max; }

    public int getQtyPerRepeat() { return qtyPerRepeat; }
    public void setQtyPerRepeat(int qty) { this.qtyPerRepeat = qty; }

    public int getSecondsTillRepeat() { return secondsTillRepeat; }
    public void setSecondsTillRepeat(int seconds) { this.secondsTillRepeat = seconds; }

}
