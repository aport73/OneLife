package xyz.acyber.oneLife.DataObjects.SubSettings;

import org.bukkit.Material;

public class BuffedFood {
    private Material material = null;
    private double hungerBuff = 0;
    private double saturationBuff = 0;

    public BuffedFood() { super(); } // Default constructor

    public BuffedFood(Material material, double hungerBuff, double saturationBuff) {
        this.material = material;
        this.hungerBuff = hungerBuff;
        this.saturationBuff = saturationBuff;
    }

    public  Material getMaterial() { return material; }
    public void setMaterial( Material material) { this.material = material; }
    public double getHungerBuff() { return hungerBuff; }
    public void setHungerBuff(double hungerBuff) {  this.hungerBuff = hungerBuff; }
    public double getSaturationBuff() { return saturationBuff; }
    public void setSaturationBuff(double saturationBuff) { this.saturationBuff = saturationBuff; }
}
