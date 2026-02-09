package xyz.acyber.oneLife.DataObjects.SubSettings;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class MobHostility {
    private Boolean enabledDay = false;
    private Boolean enabledNight = false;
    private double aggroRange = 35;
    private double damageAmount = 6.0;
    private double damageCooldownSeconds = 1;
    private List<Material> breakableBlocks = new ArrayList<>();

    public MobHostility() { super(); }

    public Boolean getEnabledDay() { return enabledDay; }
    public void setEnabledDay(Boolean enabledDay) { this.enabledDay = enabledDay; }

    public Boolean getEnabledNight() { return enabledNight; }
    public void setEnabledNight(Boolean enabledNight) { this.enabledNight = enabledNight; }

    public Double getAggroRange() { return aggroRange; }
    public void setAggroRange(Double aggroRange) { this.aggroRange = aggroRange; }

    public double getDamageAmount() { return damageAmount; }
    public void setDamageAmount(double damageAmount) { this.damageAmount = damageAmount; }

    public double getDamageCooldownSeconds() { return damageCooldownSeconds; }
    public void setDamageCooldownSeconds(double damageCooldownSeconds) { this.damageCooldownSeconds = damageCooldownSeconds; }

    public List<Material> getBreakableBlocks() { return breakableBlocks; }
    public void setBreakableBlocks(List<Material> breakableBlocks) { this.breakableBlocks = breakableBlocks; }
    public void addBreakableBlock(Material material) { this.breakableBlocks.add(material); }
    public void removeBreakableBlock(Material material) { this.breakableBlocks.remove(material); }
}
