package xyz.acyber.oneLife.DataObjects.SubSettings;

import com.fasterxml.jackson.annotation.*;
import org.bukkit.Material;

public class RepeatItem {

    @JsonProperty("materialType")
    private String materialType = null;
    @JsonProperty("max")
    private int max = 0;
    @JsonProperty("qtyPerRepeat")
    private int qtyPerRepeat = 0;
    @JsonProperty("secondsTillRepeat")
    private int secondsTillRepeat = 0;

    @JsonCreator
    public RepeatItem() { super(); } // Default constructor

    @JsonIgnore
    public RepeatItem(String materialType, int max, int qtyPerRepeat, int secondsTillRepeat) {
        this.materialType = materialType;
        this.max = max;
        this.qtyPerRepeat = qtyPerRepeat;
        this.secondsTillRepeat = secondsTillRepeat;
    }

    @JsonGetter
    public String getMaterialType() { return materialType; }
    @JsonSetter
    public void setMaterialType(String materialType) { this.materialType = materialType; }
    @JsonIgnore
    public Material getMaterial() { return Material.valueOf(materialType); }

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
