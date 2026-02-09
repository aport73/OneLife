package xyz.acyber.oneLife.Runables;

import static java.lang.Math.max;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.destroystokyo.paper.entity.Pathfinder;

import xyz.acyber.oneLife.DataObjects.SubSettings.MobConfig;
import xyz.acyber.oneLife.DataObjects.SubSettings.MobHostility;
import xyz.acyber.oneLife.OneLifePlugin;

public class PassiveMobsModifier extends BukkitRunnable {

    private final OneLifePlugin OLP;
    private final HashMap<String, MobConfig> mobconfigs;
    private final HashMap<Player,List<UUID>> modifiedMobs;
    private final HashMap<UUID,Player> mobsAssignedPlayers;
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
    
// This code is to turn passive mobs to "hostile" *(Starts the loop)*

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
                        MobConfig mobConfig = mobconfigs.get(mobType);
                        MobHostility mobHostility = mobConfig != null ? mobConfig.getMobHostility() : null;
                        if (mobHostility != null && enabledMobs.contains(mobType)) {
                            double dist = mob.getLocation().distanceSquared(p.getLocation());
                            double mobRadius = mobHostility.getAggroRange();
                            Player target = p;


                            //MobConfig point view
                            List<Player> nearbyPlayers = new ArrayList<>();
                            mob.getNearbyEntities(maxRadius, maxRadius, maxRadius).forEach((entity1) -> {
                                if (entity1 instanceof Player player) {
                                    nearbyPlayers.add(player);
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

// Sets hostile mob pathfinding to check block above and below sight block *(to break)*

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
// Sets fish in server to jump *(start attack code for passive (underwater))*

                                if (pathResult != null)
                                    pfinder.moveTo(pathResult);
                                if (dist <= 6 && mob.isUnderWater() && mob.canBreatheUnderwater()) {
                                    mob.setJumping(true);
                                    mob.addPotionEffect(PotionEffectType.JUMP_BOOST.createEffect(1, 3));
                                    if (mob.getAttribute(Attribute.ATTACK_DAMAGE) != null)
                                        mob.attack(target);

                            // Sets the *(attack damage)* for *(hostile mobs)* and runs damage math to calculate the *(amount)*

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
            MobHostility hostility = config.getMobHostility();
            if (hostility == null) continue;
            if (hostility.getAggroRange() > maxRadius)
                maxRadius = hostility.getAggroRange();
        }
    }

    private @Nullable List<String> getEnabledMobs() {
        if (mobconfigs == null)
            return new ArrayList<>();
        List<String> mobs = new ArrayList<>();
        for (MobConfig config: mobconfigs.values()) {
            MobHostility hostility = config.getMobHostility();
            if (hostility != null && (hostility.getEnabledDay() || hostility.getEnabledNight()))
                mobs.add(config.getMobType());
        }
        return mobs;
    }

}
