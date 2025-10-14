package xyz.acyber.oneLife.DataObjects.SubSettings;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.bukkit.Material;
import java.util.List;

public class AssignedArmor {

    @JsonProperty("helmetMaterial")
    private Material helmetMaterial;
    @JsonProperty("helmetEnchants")
    private List<Enchant> helmetEnchants;
    @JsonProperty("chestplateMaterial")
    private Material chestplateMaterial;
    @JsonProperty("chestplateEnchants")
    private List<Enchant> chestplateEnchants;
    @JsonProperty("leggingsMaterial")
    private Material leggingsMaterial;
    @JsonProperty("leggingsEnchants")
    private List<Enchant> leggingsEnchants;
    @JsonProperty("bootsMaterial")
    private Material bootsMaterial = null;
    @JsonProperty("bootsEnchants")
    private List<Enchant> bootsEnchants = null;

    public AssignedArmor() { super(); }

    @JsonGetter
    public Material getHelmetMaterial() { return helmetMaterial; }
    @JsonSetter
    public void setHelmetMaterial(Material helmetMaterial) { this.helmetMaterial = helmetMaterial; }

    @JsonGetter
    public List<Enchant> getHelmetEnchants() { return helmetEnchants; }
    @JsonSetter
    public void setHelmetEnchants(List<Enchant> helmetEnchants) { this.helmetEnchants = helmetEnchants; }

    @JsonGetter
    public Material getChestplateMaterial() { return chestplateMaterial; }
    @JsonSetter
    public void setChestplateMaterial(Material chestplateMaterial) { this.chestplateMaterial = chestplateMaterial; }

    @JsonGetter
    public List<Enchant> getChestplateEnchants() { return chestplateEnchants; }
    @JsonSetter
    public void setChestplateEnchants(List<Enchant> chestplateEnchants) { this.chestplateEnchants = chestplateEnchants; }

    @JsonGetter
    public Material getLeggingsMaterial() { return leggingsMaterial; }
    @JsonSetter
    public void setLeggingsMaterial(Material leggingsMaterial) { this.leggingsMaterial = leggingsMaterial; }

    @JsonGetter
    public List<Enchant> getLeggingsEnchants() { return leggingsEnchants; }
    @JsonSetter
    public void setLeggingsEnchants(List<Enchant> leggingsEnchants) { this.leggingsEnchants = leggingsEnchants; }

    @JsonGetter
    public Material getBootsMaterial() { return bootsMaterial; }
    @JsonSetter
    public void setBootsMaterial(Material bootsMaterial) { this.bootsMaterial = bootsMaterial; }

    @JsonGetter
    public List<Enchant> getBootsEnchants() { return bootsEnchants; }
    @JsonSetter
    public void setBootsEnchants(List<Enchant> bootsEnchants) { this.bootsEnchants = bootsEnchants; }


}
