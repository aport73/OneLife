package xyz.acyber.oneLife.Runables;

import com.destroystokyo.paper.entity.Pathfinder;
import com.google.common.base.Function;
import net.minecraft.server.commands.EffectCommands;
import net.minecraft.world.item.enchantment.Enchantment;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import xyz.acyber.oneLife.Main;

import java.util.*;

import static java.lang.Math.max;

public class PassiveMobsModifier extends BukkitRunnable {

    private final Main plugin;
    private final FileConfiguration config;
    private final HashMap<Player,List<UUID>> modifiedMobs;
    private final List<String> enabledMobs;
    private int maxRadius = 0;

    public PassiveMobsModifier(@NotNull Main plugin) {
        this.plugin = plugin;
        this.config = plugin.getPassiveMobsModifierConfig();
        this.enabledMobs = getEnabledMobs();
        modifiedMobs = new HashMap<>();
        updateMaxRadius();
    }

    @Override
    public void run() {
        if (plugin.isNight) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                List<Entity> nearbyEntities = new ArrayList<>(p.getNearbyEntities(maxRadius, maxRadius, maxRadius));
                List<UUID> previousMobs = new ArrayList<>();
                List<UUID> currentMobs = new ArrayList<>();
                if (!modifiedMobs.isEmpty() && modifiedMobs.containsKey(p))
                    previousMobs.addAll(modifiedMobs.get(p));

                nearbyEntities.forEach(entity -> {
                    if (entity instanceof Mob mob) {
                        String mobType = mob.getType().name().toUpperCase();
                        if (enabledMobs.contains(mobType)) {
                            if (config.getKeys(false).contains(mobType)) {
                                double dist = mob.getLocation().distanceSquared(p.getLocation());
                                int mobRadius = config.getInt(mobType + ".Radius") * config.getInt(mobType + ".Radius");
                                if (dist <= mobRadius) {
                                    Pathfinder pfinder = mob.getPathfinder();
                                    Pathfinder.PathResult pathResult = pfinder.findPath(p);
                                    mob.setJumping(false);
                                    if (pathResult != null)
                                        pfinder.moveTo(pathResult);
                                    if (dist <= 6 && mob.isUnderWater() && mob.canBreatheUnderwater()) {
                                        mob.setJumping(true);
                                        mob.addPotionEffect(PotionEffectType.JUMP_BOOST.createEffect(1,3));
                                    }
                                    if (dist <= 1 && (mob.getLocation().getY() - p.getLocation().getY()) >= -0.5) {
                                        mob.setJumping(true);
                                        AttributeInstance armor = p.getAttribute(Attribute.ARMOR);
                                        double defensePoints = 0;
                                        if (armor != null) defensePoints = armor.getValue();
                                        double damage = config.getInt(mobType + ".Damage");
                                        double finalDamage =  damage * ( 1 - max( defensePoints / 5, defensePoints - damage / 2 ) / 25 );
                                        p.damage(finalDamage);
                                    }
                                    previousMobs.remove(mob.getUniqueId());
                                    currentMobs.add(mob.getUniqueId());
                                }
                            }
                        }
                    }
                });
                previousMobs.forEach(uuid -> {
                    Mob mob = (Mob) Bukkit.getEntity(uuid);
                    if (mob != null) {
                        mob.getPathfinder().stopPathfinding();
                        mob.setJumping(false);
                    }
                });
                if (!modifiedMobs.isEmpty())
                    modifiedMobs.remove(p);
                modifiedMobs.put(p, currentMobs);
            }
        }  else {
            if (!modifiedMobs.isEmpty()) {
                modifiedMobs.forEach( (player, uuids) -> {
                    uuids.forEach(uuid -> {
                        Mob mob = (Mob) Bukkit.getEntity(uuid);
                        if (mob != null) {
                            mob.setTarget(null);
                            mob.setAggressive(false);
                        }
                    });
                });
            }
        }
    }

    private void updateMaxRadius() {
        for (String key: enabledMobs) {
            if (config.getInt(key + ".Radius") > maxRadius)
                maxRadius = config.getInt(key + ".Radius");
        }
    }

    private List<String> getEnabledMobs() {
        List<String> mobs = new ArrayList<>();
        for (String key: config.getKeys(false)) {
            if (config.getBoolean(key + ".Enabled")) {
                mobs.add(key);
            }
        }
        return mobs;
    }

}
