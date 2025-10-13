package xyz.acyber.oneLife.DataObjects.SubSettings.SubRace;

import com.fasterxml.jackson.annotation.*;
import org.bukkit.Material;

import javax.annotation.Nullable;

public class RepeatItem {

    @JsonProperty("material")
    private Material material = null;
    @JsonProperty("max")
    private int max = 0;
    @JsonProperty("qtyPerRepeat")
    private int qtyPerRepeat = 0;
    @JsonProperty("secondsTillRepeat")
    private int secondsTillRepeat = 0;

    @JsonCreator
    public RepeatItem() { super(); } // Default constructor

    @JsonIgnore
    public RepeatItem( Material material, int max, int qtyPerRepeat, int secondsTillRepeat) {
        this.material = material;
        this.max = max;
        this.qtyPerRepeat = qtyPerRepeat;
        this.secondsTillRepeat = secondsTillRepeat;
    }

    @JsonGetter
    public Material getMaterial() { return material; }
    @JsonSetter
    public void setMaterial(Material material) { this.material = material; }

    @JsonGetter
    public int getMax() { return max; }
    @JsonSetter
    public void setMax(int max) { this.max = max; }

    @JsonGetter
    public int getQtyPerRepeat() { return qtyPerRepeat; }
    @JsonSetter
    public void setQtyPerRepeat(int qty) { this.qtyPerRepeat = qty; }

    @JsonGetter
    public int getSecondsTillRepeat() { return secondsTillRepeat; }
    @JsonSetter
    public void setSecondsTillRepeat(int seconds) { this.secondsTillRepeat = seconds; }

}
