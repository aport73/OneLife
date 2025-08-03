package xyz.acyber.oneLife.DataObjects.SubSettings.SubRace;

import org.bukkit.Material;

public class BuffedFood {

    private Material material;
    private double hungerBuff;
    private double saturationBuff;

    public BuffedFood() {
        super();
    }
    public BuffedFood(Material material, double hungerBuff, double saturationBuff) {
        this.material = material;
        this.hungerBuff = hungerBuff;
        this.saturationBuff = saturationBuff;
    }

    public Material getMaterial() { return material; }
    public void setMaterial(Material material) { this.material = material; }

    public double getHungerBuff() { return hungerBuff; }
    public void setHungerBuff(double hungerBuff) {  this.hungerBuff = hungerBuff; }

    public double getSaturationBuff() { return saturationBuff; }
    public void setSaturationBuff(double saturationBuff) {  this.saturationBuff = saturationBuff; }
}
