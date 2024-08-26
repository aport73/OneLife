package xyz.acyber.oneLifeRaces;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.LogRecord;


public final class OneLifeRaces extends JavaPlugin implements Listener, BasicCommand {

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        getConfig();
        Bukkit.getPluginManager().registerEvents(this, this);

        LifecycleEventManager<Plugin> manager = this.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register("onelife", this);
        });
    }

    public void reload() {
        getConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.sendMessage(Component.text("Hello, " + player.getName() + "!"));
        String race = getPlayerRace(player);
        Double damage = getPlayerDamage(player);
        if (race != null) {
            player.sendMessage(Component.text("Race: " + race));
        } else {
            setPlayerRace(player, "Human");
            player.sendMessage(Component.text("Race: " + "Human"));
        }
        if (damage != null) {
            player.sendMessage(Component.text("Damage: " + damage));
        }
        applyRaceEffects(player);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        NamespacedKey key = new NamespacedKey(this, "oneLifeRaces-totalDeathsTaken");
        Player player = event.getEntity();
        PersistentDataContainer playerContainer = player.getPersistentDataContainer();
        double deaths = 1.00;
        if (playerContainer.has(key)) deaths += playerContainer.get(key, PersistentDataType.DOUBLE);
        playerContainer.set(key, PersistentDataType.DOUBLE, deaths);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        applyRaceEffects(event.getPlayer());
    }

    public void sendMsgOps(String components) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("ServerOperator")) {
                player.sendMessage(components);
            }
        }
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        //Handle Mob Adjustments from Config
        if (event.getEntity() instanceof LivingEntity spawnedEntity) {
            ConfigurationSection mobConfig = getConfig().getConfigurationSection("MOBS." + spawnedEntity.getType().name());
            if (mobConfig != null) {
                for (var key : mobConfig.getConfigurationSection("Armor").getKeys(false)) {
                        getLogger().config(mobConfig.getString(key));
                        ItemStack item = ItemStack.of(Material.valueOf(mobConfig.getString(key)));
                        if (item.getType().toString().contains("LEATHER")) {
                            String colorString = mobConfig.getString("Color");
                            String rgb[] = colorString.split(",");
                            int r = Integer.parseInt(rgb[0]);
                            int g = Integer.parseInt(rgb[1]);
                            int b = Integer.parseInt(rgb[2]);
                            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
                            meta.setColor(Color.fromRGB(r,g,b));
                            item.setItemMeta(meta);
                        }
                        spawnedEntity.getEquipment().setItem(EquipmentSlot.valueOf(key),item);
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        NamespacedKey key = new NamespacedKey(this, "oneLifeRaces-totalDamageTaken");
        if (event.getEntity() instanceof Player player) {
            double damage = event.getDamage();
            PersistentDataContainer playerContainer = player.getPersistentDataContainer();
            if (playerContainer.has(key)) {
                damage += playerContainer.get(key, PersistentDataType.DOUBLE);
            }
            playerContainer.set(key, PersistentDataType.DOUBLE, damage);
        }
    }


    @EventHandler
    public void playerItemConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() == Material.MILK_BUCKET) {
            applyRaceEffects(event.getPlayer());
        }
    }

    @EventHandler
    public void playerEquipItem(PlayerInventorySlotChangeEvent event) {

    }

    public String getPlayerRace(Player player) {
        NamespacedKey key = new NamespacedKey(this, "oneLifeRaces-playerRace");
        PersistentDataContainer playerContainer = player.getPersistentDataContainer();
        if (playerContainer.has(key)) {
            return playerContainer.get(key, PersistentDataType.STRING);
        } else {
            return null;
        }

    }

    public void setPlayerRace(Player player, String playerRace) {
        NamespacedKey key = new NamespacedKey(this, "oneLifeRaces-playerRace");
        PersistentDataContainer playerContainer = player.getPersistentDataContainer();
        playerContainer.set(key, PersistentDataType.STRING, playerRace);
    }

    public Double getPlayerDamage(Player player) {
        NamespacedKey key = new NamespacedKey(this, "oneLifeRaces-totalDamageTaken");
        PersistentDataContainer playerContainer = player.getPersistentDataContainer();
        if (playerContainer.has(key)) {
            return playerContainer.get(key, PersistentDataType.DOUBLE);
        } else {
            return null;
        }
    }

    public void applyRaceEffects(Player player) {
        player.clearActivePotionEffects();
        String race = getPlayerRace(player);
        if (race != null) {
            ConfigurationSection raceConfig = getConfig().getConfigurationSection("races." + race);
            Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_SCALE)).setBaseValue(raceConfig.getDouble("scale"));
            Objects.requireNonNull(player.getAttribute(Attribute.PLAYER_BLOCK_INTERACTION_RANGE)).setBaseValue(raceConfig.getDouble("reach"));
            List<String> raceEffects = raceConfig.getStringList("effects");
            for (String effect : raceEffects) {
                PotionEffectType potion = Registry.EFFECT.get(NamespacedKey.minecraft(effect.toLowerCase()));
                assert potion != null;
                player.addPotionEffect(Objects.requireNonNull(potion.createEffect(-1,0)));
            }
            ConfigurationSection raceEquipment = raceConfig.getConfigurationSection("equipmentEnchants");
            if (raceEquipment != null) {
                for (String key : raceEquipment.getKeys(false)) {
                    for (String ditem : raceEquipment.getConfigurationSection(key).getKeys(false)) {
                        for (String upgrade : raceEquipment.getConfigurationSection(key).getConfigurationSection(ditem).getKeys(false)) {
                            Enchantment enchant = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(NamespacedKey.minecraft(upgrade.toLowerCase()));
                            ItemStack playerItem = player.getEquipment().getItem(EquipmentSlot.valueOf(key));
                            if (!playerItem.isEmpty()) {
                                playerItem.addEnchantment(enchant, raceEquipment.getInt(key + "." + ditem + "." + upgrade));
                                player.getEquipment().setItem(EquipmentSlot.valueOf(key), playerItem);
                            } else {
                                sendMsgOps("applying to default item");
                                ItemStack defaultItem = new ItemStack(Material.getMaterial(ditem));
                                defaultItem.addEnchantment(enchant, raceEquipment.getInt(key + "." + ditem + "." + upgrade));
                                player.getEquipment().setItem(EquipmentSlot.valueOf(key), defaultItem);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            stack.getSender().sendRichMessage("One Life Help!");
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            reload();
            stack.getSender().sendRichMessage("One Life Plugin Reloaded");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("races")) {
            stack.getSender().sendRichMessage(args[1] + "'s race: further work to be done");
        }
        if (args.length == 4 && args[0].equalsIgnoreCase("races") && args[2].equalsIgnoreCase("setRace")) {
            Player player = Bukkit.getPlayer(args[1]);
            setPlayerRace(player, args[3]);
            applyRaceEffects(player);
        }
    }

    @Override
    public @NotNull Collection<String> suggest(final @NotNull CommandSourceStack stack, final @NotNull String[] args) {
        Collection<String> sug = new ArrayList<>();
        if (args.length == 0) {
            sug.add("races");
            sug.add("reload");
            sug.add("help");
            return sug;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("races")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                sug.add(player.getName());
            }
            return sug;
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("races")) {
            sug.add("setRace");
            return sug;
        }
        if (args.length == 4 && args[0].equalsIgnoreCase("races") && args[2].equalsIgnoreCase("setRace")) {
            sug.addAll(getConfig().getConfigurationSection("races").getKeys(false));
            return sug;
        }
        return Collections.emptyList();
    }
}
