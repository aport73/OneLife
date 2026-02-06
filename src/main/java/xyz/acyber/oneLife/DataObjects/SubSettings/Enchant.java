package xyz.acyber.oneLife.DataObjects.SubSettings;

import org.bukkit.enchantments.Enchantment;

public class Enchant {
    private Enchantment enchantment;
    private int level;

    public Enchant() { super(); }

    public Enchant(Enchantment enchantment, int level) {
        this.enchantment = enchantment;
        this.level = level;
    }

    public Enchantment getEnchantment() { return enchantment; }
    public void setEnchantment(Enchantment enchantment) { this.enchantment = enchantment; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
}
