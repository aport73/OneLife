package xyz.acyber.oneLife.DataObjects.SubSettings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.bukkit.attribute.Attribute;

public class BuffedAttribute {

    @JsonProperty("attribute")
    private Attribute attribute = null;
    @JsonProperty("base")
    private double base;
    @JsonProperty("variance")
    private double variance;

    @JsonCreator
    public BuffedAttribute() { super(); }

    public BuffedAttribute(Attribute attribute, double base, double variance) {
        this.attribute = attribute;
        this.base = base;
        this.variance = variance;
    }

    @JsonGetter
    public Attribute getAttribute() { return attribute; }
    @JsonSetter
    public void setAttribute(Attribute attribute) { this.attribute = attribute; }

    @JsonGetter
    public double getBase() { return base; }
    @JsonSetter
    public void setBase(double base) { this.base = base; }

    @JsonGetter
    public double getVariance() { return variance; }
    @JsonSetter
    public void setVariance(double variance) { this.variance = variance; }

}
