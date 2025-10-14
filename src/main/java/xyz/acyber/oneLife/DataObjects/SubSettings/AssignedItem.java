package xyz.acyber.oneLife.DataObjects.SubSettings;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.bukkit.Material;

import java.util.List;

public class AssignedItem {

    @JsonProperty("itemMaterial")
    private Material itemMaterial = null;
    @JsonProperty("itemAmount")
    private int itemAmount = 1;
    @JsonProperty("itemEnchantments")
    private List<Enchant> itemEnchantments = null;

    public AssignedItem() { super(); }

    @JsonGetter
    public Material getItemMaterial() { return itemMaterial; }
    @JsonSetter
    public void setItemMaterial(Material itemMaterial) { this.itemMaterial = itemMaterial; }

    @JsonGetter
    public int getItemAmount() { return itemAmount; }
    @JsonSetter
    public void setItemAmount(int itemAmount) { this.itemAmount = itemAmount; }

    @JsonGetter
    public List<Enchant> getItemEnchantments() { return itemEnchantments; }
    @JsonSetter
    public void setItemEnchantments(List<Enchant> itemEnchantments) { this.itemEnchantments = itemEnchantments; }

}
