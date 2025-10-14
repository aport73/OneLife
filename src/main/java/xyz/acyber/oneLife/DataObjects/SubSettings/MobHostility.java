package xyz.acyber.oneLife.DataObjects.SubSettings;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class MobHostility {
    @JsonProperty("enabledDay")
    private Boolean enabledDay = false;
    @JsonProperty("enabledNight")
    private Boolean enabledNight = false;
    @JsonProperty("aggroRange")
    private double aggroRange = 35;
    @JsonProperty("damageAmount")
    private double damageAmount = 6.0;
    @JsonProperty("damageCooldownSeconds")
    private double damageCooldownSeconds = 1;
    @JsonProperty("breakableBlocks")
    private List<Material> breakableBlocks = new ArrayList<>();

    public MobHostility() { super(); }

    @JsonGetter
    public Boolean getEnabledDay() { return enabledDay; }
    @JsonSetter
    public void setEnabledDay(Boolean enabledDay) { this.enabledDay = enabledDay; }

    @JsonGetter
    public Boolean getEnabledNight() { return enabledNight; }
    @JsonSetter
    public void setEnabledNight(Boolean enabledNight) { this.enabledNight = enabledNight; }

    @JsonGetter
    public Double getAggroRange() { return aggroRange; }
    @JsonSetter
    public void setAggroRange(Double aggroRange) { this.aggroRange = aggroRange; }

    @JsonGetter
    public double getDamageAmount() { return damageAmount; }
    @JsonSetter
    public void setDamageAmount(double damageAmount) { this.damageAmount = damageAmount; }

    @JsonGetter
    public double getDamageCooldownSeconds() { return damageCooldownSeconds; }
    @JsonSetter
    public void setDamageCooldownSeconds(double damageCooldownSeconds) { this.damageCooldownSeconds = damageCooldownSeconds; }

    @JsonGetter
    public List<Material> getBreakableBlocks() { return breakableBlocks; }
    @JsonSetter
    public void setBreakableBlocks(List<Material> breakableBlocks) { this.breakableBlocks = breakableBlocks; }
    @JsonIgnore
    public void addBreakableBlock(Material material) { this.breakableBlocks.add(material); }
    @JsonIgnore
    public void removeBreakableBlock(Material material) { this.breakableBlocks.remove(material); }
}
