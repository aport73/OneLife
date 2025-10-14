package xyz.acyber.oneLife.DataObjects.SubSettings;

import com.fasterxml.jackson.annotation.*;
import org.bukkit.Material;

public class BuffedFood {

    @JsonProperty("material")
    private Material material = null;
    @JsonProperty("hungerBuff")
    private double hungerBuff = 0;
    @JsonProperty("saturationBuff")
    private double saturationBuff = 0;

    @JsonCreator
    public BuffedFood() { super(); } // Default constructor

    @JsonIgnore
    public BuffedFood(Material material, double hungerBuff, double saturationBuff) {
        this.material = material;
        this.hungerBuff = hungerBuff;
        this.saturationBuff = saturationBuff;
    }

    @JsonGetter
    public  Material getMaterial() { return material; }
    @JsonSetter
    public void setMaterial( Material material) { this.material = material; }

    @JsonGetter
    public double getHungerBuff() { return hungerBuff; }
    @JsonSetter
    public void setHungerBuff(double hungerBuff) {  this.hungerBuff = hungerBuff; }

    @JsonGetter
    public double getSaturationBuff() { return saturationBuff; }
    @JsonSetter
    public void setSaturationBuff(double saturationBuff) {  this.saturationBuff = saturationBuff; }
}
