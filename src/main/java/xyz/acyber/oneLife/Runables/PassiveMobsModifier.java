package xyz.acyber.oneLife.Runables;

import com.destroystokyo.paper.entity.Pathfinder;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acyber.oneLife.DataObjects.SubSettings.MobConfig;
import xyz.acyber.oneLife.DataObjects.SubSettings.MobHostility;
import xyz.acyber.oneLife.OneLifePlugin;

import java.util.*;

import static java.lang.Math.max;

public class PassiveMobsModifier extends BukkitRunnable {

    private final OneLifePlugin OLP;
    private HashMap<String, MobConfig> mobconfigs;
    private final HashMap<Player,List<UUID>> modifiedMobs;
    private HashMap<UUID,Player> mobsAssignedPlayers;
    private final List<String> enabledMobs;
    private Double maxRadius = 0.0;

    public PassiveMobsModifier(@NotNull OneLifePlugin OLP) {
        this.OLP = OLP;
        this.enabledMobs = getEnabledMobs();
        modifiedMobs = new HashMap<>();
        mobsAssignedPlayers = new HashMap<>();
        mobconfigs = OLP.settings.getMobs();
        updateMaxRadius();
    }

    @Override
    public void run() {
        if (OLP.isNight()) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                List<Entity> nearbyEntities = new ArrayList<>(p.getNearbyEntities(maxRadius, maxRadius, maxRadius));
                List<UUID> previousMobs = new ArrayList<>();
                List<UUID> currentMobs = new ArrayList<>();
                if (!modifiedMobs.isEmpty() && modifiedMobs.containsKey(p))
                    previousMobs.addAll(modifiedMobs.get(p));

                nearbyEntities.forEach(entity -> {
                    if (entity instanceof Mob mob) {
                        String mobType = mob.getType().name().toUpperCase();
                        MobHostility mobHostility = mobconfigs.get(mobType).getMobHostility();
                        if (enabledMobs.contains(mobType)) {
                            double dist = mob.getLocation().distanceSquared(p.getLocation());
                            double mobRadius = mobconfigs.get(mobType).getMobHostility().getAggroRange();
                            Player target = p;


                            //MobConfig point view
                            List<Player> nearbyPlayers = new ArrayList<>();
                            mob.getNearbyEntities(maxRadius, maxRadius, maxRadius).forEach((entity1) -> {
                                if (entity1 instanceof Player) {
                                    nearbyPlayers.add((Player) entity1);
                                }
                            });

                            if (mobsAssignedPlayers.containsKey(mob.getUniqueId())) {
                                if (!nearbyPlayers.contains(mobsAssignedPlayers.get(mob.getUniqueId())))
                                    mobsAssignedPlayers.remove(mob.getUniqueId());
                                else target = mobsAssignedPlayers.get(mob.getUniqueId());
                            } else {
                                if ((long) nearbyPlayers.size() > 1) {
                                    for (Player p1 : nearbyPlayers) {
                                        double dist1 = mob.getLocation().distanceSquared(p1.getLocation());
                                        if (dist > dist1) {
                                            dist = dist1;
                                            target = p1;
                                        }

                                    }
                                }
                            }

                            if (dist <= mobRadius) {
                                mob.setTarget(target);
                                Pathfinder pfinder = mob.getPathfinder();
                                Pathfinder.PathResult pathResult = pfinder.findPath(target);
                                mob.setJumping(false);

                                List<Block> sightBlocks = mob.getLineOfSight(null, 1);
                                sightBlocks.forEach(block -> {
                                    if (block.getType().toString().contains("FENCE"))
                                        block.breakNaturally();
                                    Block b1 = block.getRelative(0, 1, 0);
                                    if (b1.getType().toString().contains("FENCE"))
                                        b1.breakNaturally();
                                    Block b2 = block.getRelative(0, -1, 0);
                                    if (b2.getType().toString().contains("FENCE"))
                                        b2.breakNaturally();
                                });

                                if (pathResult != null)
                                    pfinder.moveTo(pathResult);
                                if (dist <= 6 && mob.isUnderWater() && mob.canBreatheUnderwater()) {
                                    mob.setJumping(true);
                                    mob.addPotionEffect(PotionEffectType.JUMP_BOOST.createEffect(1, 3));
                                    if (mob.getAttribute(Attribute.ATTACK_DAMAGE) != null)
                                        mob.attack(target);
                                }
                                if (dist <= 1 && (mob.getLocation().getY() - target.getLocation().getY()) >= -0.5) {
                                    if (mob.getAttribute(Attribute.ATTACK_DAMAGE) != null)
                                        mob.attack(target);
                                    else {
                                        mob.setJumping(true);
                                        AttributeInstance armor = target.getAttribute(Attribute.ARMOR);
                                        double damage = mobHostility.getDamageAmount();
                                        mob.getAttribute(Attribute.ATTACK_DAMAGE);
                                        double defensePoints = 0;
                                        if (armor != null) defensePoints = armor.getValue();
                                        double finalDamage = damage * (1 - max(defensePoints / 5, defensePoints - damage / 2) / 25);
                                        //TODO look into correcting below damage type to show mob name on death
                                        DamageSource damageSource = DamageSource.builder(DamageType.GENERIC).build();
                                        target.damage(finalDamage, damageSource);
                                    }
                                }
                                previousMobs.remove(mob.getUniqueId());
                                currentMobs.add(mob.getUniqueId());
                            }
                        }
                    }
                });
                previousMobs.forEach(uuid -> {
                    Mob mob = (Mob) Bukkit.getEntity(uuid);
                    if (mob != null) {
                        mob.getPathfinder().stopPathfinding();
                        mob.setTarget(null);
                        mob.setJumping(false);
                    }
                });
                if (!modifiedMobs.isEmpty())
                    modifiedMobs.remove(p);
                modifiedMobs.put(p, currentMobs);
            }
        }  else {
            if (!modifiedMobs.isEmpty()) {
                modifiedMobs.forEach( (player, uuids) -> uuids.forEach(uuid -> {
                    Mob mob = (Mob) Bukkit.getEntity(uuid);
                    if (mob != null) {
                        mob.setTarget(null);
                        mob.setAggressive(false);
                    }
                }));
            }
        }
    }

    private void updateMaxRadius() {
        for (MobConfig config: mobconfigs.values()) {
            if (config.getMobHostility().getAggroRange() > maxRadius)
                maxRadius = config.getMobHostility().getAggroRange();
        }
    }

    private @Nullable List<String> getEnabledMobs() {
        if (mobconfigs == null)
            return null;
        List<String> mobs = new ArrayList<>();
        for (MobConfig config: mobconfigs.values()) {
           if (config.getMobHostility().getEnabledDay() || config.getMobHostility().getEnabledNight())
               mobs.add(config.getMobType());
        }
        return mobs;
    }

}
