package xyz.acyber.oneLife.DataObjects.SubSettings;

import org.bukkit.Material;

import java.util.List;

public class AssignedItem {

    private Material itemMaterial = null;
    private int itemAmount = 1;
    private List<Enchant> itemEnchantments = null;

    public AssignedItem() { super(); }

    public Material getItemMaterial() { return itemMaterial; }
    public void setItemMaterial(Material itemMaterial) { this.itemMaterial = itemMaterial; }

    public int getItemAmount() { return itemAmount; }
    public void setItemAmount(int itemAmount) { this.itemAmount = itemAmount; }

    public List<Enchant> getItemEnchantments() { return itemEnchantments; }
    public void setItemEnchantments(List<Enchant> itemEnchantments) { this.itemEnchantments = itemEnchantments; }

}
