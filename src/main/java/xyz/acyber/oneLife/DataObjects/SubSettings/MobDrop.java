package xyz.acyber.oneLife.DataObjects.SubSettings;

import com.fasterxml.jackson.annotation.*;
import org.bukkit.Material;

public class MobDrop {

    @JsonProperty("materialName")
    private String materialName = "";
    @JsonProperty("min")
    private int min = 0;
    @JsonProperty("max")
    private int max = 0;

    @JsonCreator
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

    @JsonGetter
    public String getMaterialName() { return materialName; }
    @JsonSetter
    public void setMaterial(String materialName) { this.materialName = materialName; }
    @JsonIgnore
    public Material getMaterial() { return Material.valueOf(materialName); }

    @JsonGetter
    public int getMin() { return min; }
    @JsonSetter
    public void setMin(int min) { this.min = min; }

    @JsonGetter
    public int getMax() { return max; }
    @JsonSetter
    public void setMax(int max) { this.max = max; }
}
