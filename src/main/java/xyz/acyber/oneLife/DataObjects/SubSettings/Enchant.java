package xyz.acyber.oneLife.DataObjects.SubSettings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.bukkit.enchantments.Enchantment;

public class Enchant {
    @JsonProperty("enchantment")
    private Enchantment enchantment;
    @JsonProperty("level")
    private int level;

    public Enchant() { super(); }

    public Enchant(Enchantment enchantment, int level) {
        this.enchantment = enchantment;
        this.level = level;
    }

    @JsonGetter
    public Enchantment getEnchantment() { return enchantment; }
    @JsonSetter
    public void setEnchantment(Enchantment enchantment) { this.enchantment = enchantment; }

    @JsonGetter
    public int getLevel() { return level; }
    @JsonSetter
    public void setLevel(int level) { this.level = level; }
}
