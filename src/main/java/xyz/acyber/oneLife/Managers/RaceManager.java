package xyz.acyber.oneLife.Managers;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Objects;

import xyz.acyber.oneLife.Main;


public class RaceManager {

    static MobManager mm;
    static ScoreManager sm;
    static CommandManager cm;
    static Main main;

    public RaceManager(Main plugin) {
        main = plugin;
        mm = main.mm;
        sm = main.sm;
        cm = main.cm;
    }

    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String race = getPlayerRace(player);
        if (race != null) {
            player.sendMessage(Component.text("Race: " + race));
        } else {
            setPlayerRace(player, "Human");
            player.sendMessage(Component.text("Race: " + "Human"));
        }
        applyRace(player, null);
    }

    public void onPlayerMove(PlayerMoveEvent ev) {
        //Code for Wall Climbing
        Boolean climbingEnabled = main.getConfig().getBoolean("races." + getPlayerRace(ev.getPlayer()) + ".climbingEnabled");
        FileConfiguration config = main.getConfig();

        if (climbingEnabled && getPlayerClimbs(ev.getPlayer())) {
            Block b1 = ev.getPlayer().getLocation().getBlock();

            if (b1.getType() != Material.AIR) {
                return;
            }

            Block b2 = b1.getRelative(BlockFace.UP);
            Location l = ev.getPlayer().getLocation();

            Vector vec = ev.getTo().clone().subtract(ev.getFrom().clone()).toVector();
            double x = vec.getX();
            double z = vec.getZ();
            double Vy = vec.getY();
            String direction;
            if (Math.abs(x) > Math.abs(z)) {
                direction = x > 0.0 ? "EAST" : "WEST";
            } else {
                direction = z > 0.0 ? "SOUTH" : "NORTH";
            }
            if (b1.getRelative(BlockFace.valueOf(direction)).getType().isCollidable() || (Vy != 0
                    && (b1.getRelative(BlockFace.NORTH).getType().isCollidable()
                    || b1.getRelative(BlockFace.SOUTH).getType().isCollidable()
                    || b1.getRelative(BlockFace.WEST).getType().isCollidable()
                    || b1.getRelative(BlockFace.EAST).getType().isCollidable()))) {
                double y = l.getY();
                BlockData vine = Material.VINE.createBlockData("[up=true]");
                ev.getPlayer().sendBlockChange(b1.getLocation(), vine);
                setPlayerClimbVines(ev.getPlayer(), getPlayerClimbVines(ev.getPlayer()) + b1.getLocation().blockX() + "," + b1.getLocation().blockY() + "," + b1.getLocation().blockZ() + "/");
                if (y % 1 > .40 && b2.getType() == Material.AIR) {
                    ev.getPlayer().sendBlockChange(b2.getLocation(), vine);
                    setPlayerClimbVines(ev.getPlayer(), getPlayerClimbVines(ev.getPlayer()) + b2.getLocation().blockX() + "," + b2.getLocation().blockY() + "," + b2.getLocation().blockZ() + "/");
                }
                if (ev.getFrom().getBlockX() != ev.getTo().getBlockX()
                        || ev.getFrom().getBlockZ() != ev.getTo().getBlockZ()
                        || ev.getFrom().getBlockY() != ev.getTo().getBlockY()) {
                    for (String xyz : getPlayerClimbVines(ev.getPlayer()).split("/")) {
                        if (xyz != "" && xyz != null) {
                            int xVine = Integer.parseInt(xyz.split(",")[0]);
                            int yVine = Integer.parseInt(xyz.split(",")[1]);
                            int zVine = Integer.parseInt(xyz.split(",")[2]);
                            Location loc = new Location(ev.getFrom().getWorld(), xVine, yVine, zVine);
                            if (loc != b1.getLocation() && loc != b2.getLocation()) {
                                ev.getPlayer().sendBlockChange(loc, loc.getBlock().getBlockData());
                                setPlayerClimbVines(ev.getPlayer(), getPlayerClimbVines(ev.getPlayer()).replaceAll(xyz + "/", ""));
                            }
                        }
                    }
                }

            }
        }

        //Zora Slowness on Land
        if (config.getBoolean("races." + getPlayerRace(ev.getPlayer()) + ".slowOnLand")) {
            if (ev.getTo().getBlock().isLiquid()) {
                ev.getPlayer().removePotionEffect(PotionEffectType.SLOWNESS);
            } else {
                ev.getPlayer().addPotionEffect(PotionEffectType.SLOWNESS.createEffect(-1, 1));
            }
        }

        //Zora Dont Sink in Water
        if (config.getBoolean("races." + getPlayerRace(ev.getPlayer()) + ".stopSinkInWater")) {
            ev.getPlayer().setGravity(!ev.getPlayer().isInWater());
        } else {
            ev.getPlayer().setGravity(true);
        }

        //Katari swiftsneak
        if (config.getBoolean("races." + getPlayerRace(ev.getPlayer()) + ".fastSneak")) {
            if (ev.getPlayer().isSneaking()) {
                ev.getPlayer().setWalkSpeed(0.4f);
            } else {
                ev.getPlayer().setWalkSpeed(0.2f);
            }
        }

        //Aven Weakness & Slowness underground
        if (config.getBoolean("races." + getPlayerRace(ev.getPlayer()) + ".weakUnderGround")) {
            Player player = ev.getPlayer();
            if (ev.getTo().getBlock().getLightFromSky() < 8) {
                player.addPotionEffect(PotionEffectType.WEAKNESS.createEffect(-1, 1));
                player.addPotionEffect(PotionEffectType.SLOWNESS.createEffect(-1, 1));
            } else {
                if (player.hasPotionEffect(PotionEffectType.WEAKNESS))
                    player.removePotionEffect(PotionEffectType.WEAKNESS);
                if (player.hasPotionEffect(PotionEffectType.SLOWNESS))
                    player.removePotionEffect(PotionEffectType.SLOWNESS);
            }
        }
    }

    public void onDeath(PlayerDeathEvent event) {
        List<ItemStack> drops = event.getDrops();
        for (ItemStack item : drops) {
            if (isRaceItem(item)) {
                event.getDrops().remove(item);
                break;
            }
        }
    }

    public void onDamage(EntityDamageEvent event) {
        FileConfiguration config = main.getConfig();

        if (event.getEntity() instanceof Player player) {
            double damage = event.getDamage();
            String race = getPlayerRace(player);

            //Adjust Fall Damage Aven
            if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL) && race.equals("Aven")) {
                event.setDamage(damage * config.getDouble("races." + race + ".fallDamage"));
            }

            //Slow Burn Damage Dwarven
            if (event.getCause().equals(EntityDamageEvent.DamageCause.FIRE) || event.getCause().equals(EntityDamageEvent.DamageCause.LAVA)) {
                double multiple = config.getDouble("races." + getPlayerRace(player) + ".firedamage");
                if (multiple != 0.0) {
                    if (damage > multiple)
                        event.setDamage(config.getDouble("races." + getPlayerRace(player) + ".firedamage"));
                }
            }
        }

        if (event.getDamageSource().getCausingEntity() instanceof Player player) {
            Entity item = event.getDamageSource().getDirectEntity();
            if (player.isGliding()) {
                assert item != null;
                if (!item.getType().name().contains("ARROW")) {
                    double multiplier = config.getDouble("races." + getPlayerRace(player) + ".flyingAttackDamage");
                    event.setDamage(event.getDamage() * multiplier);
                }
            }
        }
    }

    public void playerItemConsume(PlayerItemConsumeEvent event) {
        FileConfiguration config = main.getConfig();

        List<String> allowedFoods = config.getStringList("races." + getPlayerRace(event.getPlayer()) + ".allowedFoods");
        ConfigurationSection buffedFoods = config.getConfigurationSection("races." + getPlayerRace(event.getPlayer()) + ".buffedFoods");
        boolean rawFoodSafe = config.getBoolean("races." + getPlayerRace(event.getPlayer()) + ".rawFoodSafe");

        if (!allowedFoods.isEmpty()) {
            Material item = event.getItem().getType();
            boolean inList = false;
            for (String str : allowedFoods) {
                if (item == Material.getMaterial(str.toUpperCase())) {
                    inList = true;
                }
            }
            if (!inList) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("Your race cannot consume this item");
            }
        }

        if (rawFoodSafe) {
            if (event.getPlayer().hasPotionEffect(PotionEffectType.POISON))
                event.getPlayer().removePotionEffect(PotionEffectType.POISON);
            if (event.getPlayer().hasPotionEffect(PotionEffectType.HUNGER))
                event.getPlayer().removePotionEffect(PotionEffectType.HUNGER);
        }

        if (buffedFoods != null) {
            Material item = event.getItem().getType();
            for (String str : buffedFoods.getKeys(false)) {
                if (item == Material.getMaterial(str.toUpperCase())) {
                    event.setCancelled(true);
                    Player player = event.getPlayer();
                    float sat = (float) buffedFoods.getDouble(str + ".SATURATION");
                    int food = buffedFoods.getInt(str + ".FOOD");
                    player.setSaturation(player.getSaturation() + sat);
                    player.setFoodLevel(player.getFoodLevel() + food);
                    player.getInventory().removeItemAnySlot(event.getItem().asQuantity(1));
                }
            }
        }

        if (event.getItem().getType() == Material.MILK_BUCKET) {
            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    applyRace(event.getPlayer(), null);
                }
            };
            runnable.runTaskLater(main, 5);
        }
    }

    public void playerTamePet(EntityTameEvent event) {
        if (getPlayerRace(Objects.requireNonNull(Bukkit.getPlayer(event.getOwner().getUniqueId()))).equalsIgnoreCase("Katari")) {
            if (event.getEntity() instanceof Wolf) {
                Objects.requireNonNull(Bukkit.getPlayer(event.getOwner().getUniqueId())).sendMessage("Your Race Cannot Tame Wolfs");
                event.setCancelled(true);
            }
        }
    }

    public void playerArmorChange(PlayerArmorChangeEvent event) {
        //Run code to check for armor enchants for race.
        ItemStack item = event.getNewItem();
        String race = getPlayerRace(event.getPlayer());
        ConfigurationSection equipConfig = main.getConfig().getConfigurationSection("races." + race + ".equipment");
        if (equipConfig != null && !item.isEmpty()) {
            applyRace(event.getPlayer(), null);
        }
    }

    public void entityPickupEvent(EntityPickupItemEvent event) {
        if (event.getEntity().getType() == EntityType.PLAYER) {
            Player player = (Player) event.getEntity();
            ItemStack item = event.getItem().getItemStack();
            applyRace(player, item);
        }
    }

    public void inventoryClickEvent(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if ((event.getAction() == InventoryAction.PLACE_ALL ||
                event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY ||
                event.getAction() == InventoryAction.PLACE_ONE ||
                event.getAction() == InventoryAction.PLACE_SOME ||
                event.getAction() == InventoryAction.SWAP_WITH_CURSOR) &&
                event.getClickedInventory() != null) {
            ItemStack item = event.getClickedInventory().getItem(event.getSlot());
            if (event.getAction() == InventoryAction.SWAP_WITH_CURSOR || item == null) {
                item = event.getCursor();
            }

            if (!stopItemDrop(item, player) ||
                    event.getClickedInventory().getType().equals(InventoryType.PLAYER) &&
                            event.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                if (!isRaceItem(item) ||
                        event.getClickedInventory().getType().equals(InventoryType.PLAYER) &&
                                event.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                    clearRaceItemEnchants(item);
                } else {
                    event.setCancelled(true);
                    player.getInventory().removeItem(item);
                }
            } else {
                event.setCancelled(true);
            }

            if (event.getClickedInventory().getType().equals(InventoryType.PLAYER) && event.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                this.applyRace(player, item);
            }
        }
    }

    public void playerDropItem(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        Player player = event.getPlayer();
        if (stopItemDrop(item, player)) {
            event.setCancelled(true);
        } else if (isRaceItem(item)) {
            event.getItemDrop().remove();
        } else {
            this.clearRaceItemEnchants(item);
            this.applyRace(player, null);
        }
    }

    private boolean stopItemDrop(ItemStack item, Player player) {
        if (isRaceItem(item)) {
            int count = 1;
            String[] itemType = item.getType().toString().split("_");
            PlayerInventory playerInventory = player.getInventory();
            playerInventory.removeItem(item);

            for (ItemStack invItem : playerInventory.getContents()) {
                if (invItem != null) {
                    String[] invType = ((ItemStack) Objects.requireNonNull(invItem)).getType().toString().split("_");
                    if (itemType[itemType.length - 1].equalsIgnoreCase(invType[invType.length - 1])) {
                        ++count;
                    }
                }
            }

            return count <= 1;
        } else {
            return false;
        }
    }

    public void clearRaceItemEnchants(ItemStack item) {
        String RaceEnchants = getRaceEnchants(item);
        if (RaceEnchants != null && !RaceEnchants.isEmpty()) {
            for (String enchant : RaceEnchants.split(",")) {
                item.removeEnchantment((Enchantment) Objects.requireNonNull((Enchantment) RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(NamespacedKey.minecraft(enchant.toLowerCase()))));
            }

            this.setRaceEnchants(item, "");
        }

    }

    public String getPlayerRace(Player player) {
        FileConfiguration config = main.getPlayerConfig();
        return config.getString(player.getUniqueId() + ".playerRace");
    }

    public void setPlayerRace(Player player, String playerRace) {
        FileConfiguration config = main.getPlayerConfig();
        config.set(player.getUniqueId() + ".playerRace", playerRace);
        main.savePlayerConfig(config);
    }

    public void setPlayerStartItem(Player player, Boolean start) {
        FileConfiguration config = main.getPlayerConfig();
        config.set(player.getUniqueId() + ".playerStartItem", start);
        main.savePlayerConfig(config);
    }

    public void setRaceEnchants(ItemStack item, String enchants) {
        NamespacedKey key = new NamespacedKey(main, "oneLifeRaces-enchants");
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer itemContainer = meta.getPersistentDataContainer();
        itemContainer.set(key, PersistentDataType.STRING, enchants);
        item.setItemMeta(meta);
    }

    public String getRaceEnchants(ItemStack item) {
        try {
            NamespacedKey key = new NamespacedKey(main, "oneLifeRaces-enchants");
            PersistentDataContainer itemContainer = item.getItemMeta().getPersistentDataContainer();
            if (itemContainer.has(key)) {
                return itemContainer.get(key, PersistentDataType.STRING);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public void setIsRaceItem(ItemStack item, Boolean value) {
        NamespacedKey key = new NamespacedKey(main, "oneLifeRaces-raceItem");
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer itemContainer = meta.getPersistentDataContainer();
        itemContainer.set(key, PersistentDataType.BOOLEAN, value);
        item.setItemMeta(meta);
    }

    public Boolean isRaceItem(ItemStack item) {
        try {
            NamespacedKey key = new NamespacedKey(main, "oneLifeRaces-raceItem");
            PersistentDataContainer itemContainer = item.getItemMeta().getPersistentDataContainer();
            if (itemContainer.has(key)) {
                return itemContainer.get(key, PersistentDataType.BOOLEAN);
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

    }

    public void setPlayerClimbs(Player player, Boolean climbOn) {
        FileConfiguration config = main.getPlayerConfig();
        config.set(player.getUniqueId() + ".playerClimbs", climbOn);
        main.savePlayerConfig(config);
    }

    public Boolean getPlayerClimbs(Player player) {
        FileConfiguration config = main.getPlayerConfig();
        return config.getBoolean(player.getUniqueId() + ".playerClimbs");
    }

    public void setPlayerClimbVines(Player player, String climbed) {
        FileConfiguration config = main.getPlayerConfig();
        config.set(player.getUniqueId() + ".playerClimbVines", climbed);
        main.savePlayerConfig(config);
    }

    public String getPlayerClimbVines(Player player) {
        FileConfiguration config = main.getPlayerConfig();
        String vines = config.getString(player.getUniqueId() + ".playerClimbVines");
        if (vines == null) return "";
        return vines;
    }

    public void giveRaceEquipment(Player player, ItemStack item, ConfigurationSection Equipment) {
        for (String key : Equipment.getKeys(false)) {
            boolean createdItem = false;
            if (item == null || !item.getType().toString().endsWith(key.toUpperCase()) && !key.contains(item.getType().getEquipmentSlot().toString().toUpperCase()) || item.getType().toString().equalsIgnoreCase("CrossBow") && key.equalsIgnoreCase("Bow")) {
                switch (key) {
                    case "HELMET":
                        if (player.getInventory().getHelmet() != null) {
                            item = player.getInventory().getHelmet();
                        }
                        break;
                    case "CHESTPLATE":
                        if (player.getInventory().getChestplate() != null) {
                            item = player.getInventory().getChestplate();
                        }
                        break;
                    case "LEGGINGS":
                        if (player.getInventory().getLeggings() != null) {
                            item = player.getInventory().getLeggings();
                        }
                        break;
                    case "BOOTS":
                        if (player.getInventory().getBoots() != null) {
                            item = player.getInventory().getBoots();
                        }
                        break;
                    default:
                        for (ItemStack i : player.getInventory().getContents()) {
                            if (i != null && i.getType().toString().endsWith(key.toUpperCase()) && (!i.getType().toString().equalsIgnoreCase("CrossBow") || !key.equalsIgnoreCase("Bow"))) {
                                item = i;
                            }
                        }
                }

                if (item == null || !item.getType().toString().endsWith(key.toUpperCase()) && !key.contains(item.getType().getEquipmentSlot().toString().toUpperCase()) || item.getType().toString().equalsIgnoreCase("CrossBow") && key.equalsIgnoreCase("Bow")) {
                    item = new ItemStack(Material.valueOf(Equipment.getString(key + ".Default")));
                    this.setIsRaceItem(item, true);
                    createdItem = true;
                }
            }

            StringBuilder upgrades = new StringBuilder();

            for (String upgrade : ((ConfigurationSection) Objects.requireNonNull(Equipment.getConfigurationSection(key + ".Enchants"))).getKeys(false)) {
                int level = Equipment.getInt(key + ".Enchants." + upgrade);
                Enchantment enchantment = (Enchantment) RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(NamespacedKey.minecraft(upgrade.toLowerCase()));

                assert enchantment != null;

                if ((getRaceEnchants(item) == null || ((String) Objects.requireNonNull(getRaceEnchants(item))).isEmpty()) && !item.containsEnchantment(enchantment)) {
                    upgrades.append(upgrade).append(",");
                    item.addEnchantment(enchantment, level);
                }
            }

            this.setRaceEnchants(item, upgrades.toString());
            if (createdItem) {
                switch (key) {
                    case "HELMET":
                        player.getInventory().setHelmet(item);
                        break;
                    case "CHESTPLATE":
                        player.getInventory().setChestplate(item);
                        break;
                    case "LEGGINGS":
                        player.getInventory().setLeggings(item);
                        break;
                    case "BOOTS":
                        player.getInventory().setBoots(item);
                        break;
                    default:
                        player.getInventory().addItem(item);
                }
            }
        }

    }

    public void giveRaceEffects(Player player, List<String> Effects) {
        for (String effect : Effects) {
            PotionEffectType potion = (PotionEffectType) Registry.EFFECT.get(NamespacedKey.minecraft(effect.toLowerCase()));

            assert potion != null;

            player.addPotionEffect((PotionEffect) Objects.requireNonNull(potion.createEffect(-1, 0)));
        }

    }

    public void giveStartItems(Player player, ConfigurationSection startItems) {
        FileConfiguration config = main.getPlayerConfig();
        if (startItems != null && !config.getBoolean(String.valueOf(player.getUniqueId()) + ".playerStartItem")) {
            for (String key : startItems.getKeys(false)) {
                ItemStack startItem = new ItemStack((Material) Objects.requireNonNull(Material.getMaterial(key)));
                startItem.setAmount(startItems.getInt(key));
                player.getInventory().addItem(new ItemStack[]{startItem});
            }

            this.setPlayerStartItem(player, true);
        }

    }

    public void setRepeatItems(final Player player, ConfigurationSection repeatItems) {
        if (repeatItems != null) {
            for (String key : repeatItems.getKeys(false)) {
                final ItemStack repeatItem = new ItemStack((Material) Objects.requireNonNull(Material.getMaterial(key)));
                this.setIsRaceItem(repeatItem, true);
                final int Max = repeatItems.getInt(key + ".Max");
                final int QtyPer = repeatItems.getInt(key + ".QtyPer");
                int TimeSec = repeatItems.getInt(key + ".TimeSec") * 20;
                if (main.getPlayerTasks(player) <= 0) {
                    main.setPlayerTasks(player, 1);
                    BukkitRunnable runnable = new BukkitRunnable() {
                        public void run() {
                            if (main.getPlayerTasks(player) <= 0) {
                                this.cancel();
                            }

                            int inventoryCount = 0;
                            ItemStack inv = null;

                            for (ItemStack i : player.getInventory().getContents()) {
                                if (i != null && i.getType() == repeatItem.getType()) {
                                    inventoryCount += i.getAmount();
                                    setIsRaceItem(i, true);
                                    inv = i;
                                    break;
                                }
                            }

                            if (inv != null && !inv.isEmpty()) {
                                if (inventoryCount < Max) {
                                    inv.setAmount(Math.min(inv.getAmount() + QtyPer, Max));
                                }
                            } else {
                                repeatItem.setAmount(QtyPer);
                                player.getInventory().addItem(new ItemStack[]{repeatItem});
                            }

                        }
                    };
                    runnable.runTaskTimerAsynchronously(main, 0L, (long) TimeSec);
                }
            }
        }

    }

    public void applyRace(Player player, ItemStack item) {
        player.clearActivePotionEffects();
        String race = this.getPlayerRace(player);
        if (race != null) {
            ConfigurationSection raceConfig = main.getConfig().getConfigurationSection("races." + race);

            assert raceConfig != null;
            if (raceConfig.getBoolean("enabled")) {
                Objects.requireNonNull(player.getAttribute(Attribute.SCALE)).setBaseValue(raceConfig.getDouble("scale"));
                Objects.requireNonNull(player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE)).setBaseValue(raceConfig.getDouble("reach"));
                if (raceConfig.getBoolean("lockFreezeTicks")) {
                    player.setFreezeTicks(0);
                }

                player.lockFreezeTicks(raceConfig.getBoolean("lockFreezeTicks"));
                this.giveRaceEffects(player, raceConfig.getStringList("effects"));
                ConfigurationSection raceEquipment = raceConfig.getConfigurationSection("equipment");
                if (raceEquipment != null) {
                    this.giveRaceEquipment(player, item, raceEquipment);
                }

                this.giveStartItems(player, raceConfig.getConfigurationSection("startItems"));
                this.setRepeatItems(player, raceConfig.getConfigurationSection("repeatItems"));
            } else {
                player.clearActivePotionEffects();
                for (ItemStack items : player.getInventory().getContents())
                    clearRaceItemEnchants(items);
                Objects.requireNonNull(player.getAttribute(Attribute.SCALE)).setBaseValue(Objects.requireNonNull(player.getAttribute(Attribute.SCALE)).getDefaultValue());
                Objects.requireNonNull(player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE)).setBaseValue(Objects.requireNonNull(player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE)).getDefaultValue());
            }

        }

    }
}
