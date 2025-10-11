package xyz.acyber.oneLife.DataObjects.SubSettings.SubRace;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.bukkit.Material;

import javax.annotation.Nullable;

public class BuffedFood {

    private @Nullable Material material;
    private double hungerBuff;
    private double saturationBuff;

    @JsonCreator
    public BuffedFood() { super(); } // Default constructor

    @JsonIgnore
    public BuffedFood(@Nullable Material material, double hungerBuff, double saturationBuff) {
        this.material = material;
        this.hungerBuff = hungerBuff;
        this.saturationBuff = saturationBuff;
    }

    @JsonGetter
    public @Nullable Material getMaterial() { return material; }
    @JsonSetter
    public void setMaterial(@Nullable Material material) { this.material = material; }

    @JsonGetter
    public double getHungerBuff() { return hungerBuff; }
    @JsonSetter
    public void setHungerBuff(double hungerBuff) {  this.hungerBuff = hungerBuff; }

    @JsonGetter
    public double getSaturationBuff() { return saturationBuff; }
    @JsonSetter
    public void setSaturationBuff(double saturationBuff) {  this.saturationBuff = saturationBuff; }
}
