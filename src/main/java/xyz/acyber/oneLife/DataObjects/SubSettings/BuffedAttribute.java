package xyz.acyber.oneLife.DataObjects.SubSettings;

import org.bukkit.attribute.Attribute;

public class BuffedAttribute {

    private Attribute attribute = null;
    private double base;
    private double variance;

    public BuffedAttribute() { super(); }

    public BuffedAttribute(Attribute attribute, double base, double variance) {
        this.attribute = attribute;
        this.base = base;
        this.variance = variance;
    }

    public Attribute getAttribute() { return attribute; }
    public void setAttribute(Attribute attribute) { this.attribute = attribute; }

    public double getBase() { return base; }
    public void setBase(double base) { this.base = base; }

    public double getVariance() { return variance; }
    public void setVariance(double variance) { this.variance = variance; }

}
