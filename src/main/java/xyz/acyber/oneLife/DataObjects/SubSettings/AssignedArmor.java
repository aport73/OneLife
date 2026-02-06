package xyz.acyber.oneLife.DataObjects.SubSettings;

import org.bukkit.Material;
import java.util.List;

public class AssignedArmor {

    private Material helmetMaterial;
    private List<Enchant> helmetEnchants;
    private Material chestplateMaterial;
    private List<Enchant> chestplateEnchants;
    private Material leggingsMaterial;
    private List<Enchant> leggingsEnchants;
    private Material bootsMaterial = null;
    private List<Enchant> bootsEnchants = null;

    public AssignedArmor() { super(); }

    public Material getHelmetMaterial() { return helmetMaterial; }
    public void setHelmetMaterial(Material helmetMaterial) { this.helmetMaterial = helmetMaterial; }

    public List<Enchant> getHelmetEnchants() { return helmetEnchants; }
    public void setHelmetEnchants(List<Enchant> helmetEnchants) { this.helmetEnchants = helmetEnchants; }

    public Material getChestplateMaterial() { return chestplateMaterial; }
    public void setChestplateMaterial(Material chestplateMaterial) { this.chestplateMaterial = chestplateMaterial; }

    public List<Enchant> getChestplateEnchants() { return chestplateEnchants; }
    public void setChestplateEnchants(List<Enchant> chestplateEnchants) { this.chestplateEnchants = chestplateEnchants; }

    public Material getLeggingsMaterial() { return leggingsMaterial; }
    public void setLeggingsMaterial(Material leggingsMaterial) { this.leggingsMaterial = leggingsMaterial; }

    public List<Enchant> getLeggingsEnchants() { return leggingsEnchants; }
    public void setLeggingsEnchants(List<Enchant> leggingsEnchants) { this.leggingsEnchants = leggingsEnchants; }

    public Material getBootsMaterial() { return bootsMaterial; }
    public void setBootsMaterial(Material bootsMaterial) { this.bootsMaterial = bootsMaterial; }

    public List<Enchant> getBootsEnchants() { return bootsEnchants; }
    public void setBootsEnchants(List<Enchant> bootsEnchants) { this.bootsEnchants = bootsEnchants; }

}
