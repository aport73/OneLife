package xyz.acyber.oneLife.Managers;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import xyz.acyber.oneLife.Main;

import java.util.Objects;
import java.util.Random;

public class MobManager {

    static CommandManager cm;
    static ScoreManager sm;
    static RaceManager rm;
    static Main main;

    public MobManager(Main plugin) {
        main = plugin;
        cm = main.cm;
        sm = main.sm;
        rm = main.rm;
    }

    public void onEntityDeath(EntityDeathEvent event) {
        //Change Rotten Flesh Drops
        int numbFlesh = 1;
        for (ItemStack item : event.getDrops()) {
            if (item.getType() == Material.ROTTEN_FLESH) {
                numbFlesh = item.getAmount();
            }
        }
        event.getDrops().remove(new ItemStack(Material.ROTTEN_FLESH, numbFlesh));
        ConfigurationSection mobConfig = main.getConfig().getConfigurationSection("MOBS." + event.getEntityType().name());
        if (mobConfig != null) {
            ConfigurationSection mobDrops = mobConfig.getConfigurationSection("DROPS");
            if (mobDrops != null) {
                for (String key : mobDrops.getKeys(false)) {
                    int min = mobDrops.getInt(key + ".MIN");
                    int max = mobDrops.getInt(key + ".MAX");
                    Random random = new Random();
                    int numbItem = random.nextInt(max - min) + min;
                    if (numbItem > 0) {
                        event.getDrops().add(new ItemStack(Material.valueOf(key), numbItem));
                    }
                }
            }
        }
    }

    public void onEntitySpawn(EntitySpawnEvent event) {
        //Handle Mob Adjustments from Config
        if (event.getEntity() instanceof LivingEntity spawnedEntity) {
            ConfigurationSection mobConfig = main.getConfig().getConfigurationSection("MOBS." + spawnedEntity.getType().name());
            if (mobConfig != null) {
                ConfigurationSection mobItems = mobConfig.getConfigurationSection("ITEMS");
                if (mobItems != null) {
                    for (var key : mobItems.getKeys(false)) {
                        main.getLogger().config(mobItems.getString(key));
                        ItemStack item = ItemStack.of(Material.valueOf(mobItems.getString(key)));
                        /* Color of Leather Armor Code. Still needs work.
                        if (item.getType().toString().contains("LEATHER")) {
                            String colorString = mobConfig.getString("Color");
                            String rgb[] = colorString.split(",");
                            int r = Integer.parseInt(rgb[0]);
                            int g = Integer.parseInt(rgb[1]);
                            int b = Integer.parseInt(rgb[2]);
                            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
                            meta.setColor(Color.fromRGB(r,g,b));
                            item.setItemMeta(meta);
                        } */
                        spawnedEntity.getEquipment().setItem(EquipmentSlot.valueOf(key), item);
                    }
                }
                ConfigurationSection mobBuffs = mobConfig.getConfigurationSection("BUFFS");
                if (mobBuffs != null) {
                    ConfigurationSection mobSize = mobBuffs.getConfigurationSection("SIZE");
                    if (mobSize != null) {
                        Random r = new Random();
                        double base = mobSize.getDouble("BASE");
                        double variance = mobSize.getDouble("VARIANCE");
                        double low = base - variance;
                        double high = base + variance;
                        double result = r.nextDouble(high - low) + low;
                        Objects.requireNonNull(spawnedEntity.getAttribute(Attribute.SCALE)).setBaseValue(result);
                    }
                    ConfigurationSection mobSpeed = mobBuffs.getConfigurationSection("Speed");
                    if (mobSpeed != null) {
                        Random r = new Random();
                        double multiple = mobSpeed.getDouble("BASE");
                        double base = spawnedEntity.getAttribute(Attribute.SCALE).getBaseValue() * multiple;
                        double variance = mobSpeed.getDouble("VARIANCE");
                        double low = base - variance;
                        double high = base + variance;
                        double result = r.nextDouble(high - low) + low;
                        Objects.requireNonNull(spawnedEntity.getAttribute(Attribute.MOVEMENT_SPEED)).setBaseValue(result);
                    }
                }
            }
        }
    }
}
