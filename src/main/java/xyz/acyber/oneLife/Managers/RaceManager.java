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
import org.bukkit.inventory.CreativeCategory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import xyz.acyber.oneLife.DataObjects.Settings;
import xyz.acyber.oneLife.DataObjects.SubSettings.*;
import xyz.acyber.oneLife.OneLifePlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class RaceManager {

    static OneLifePlugin OLP;

    public RaceManager(OneLifePlugin plugin) {
        OLP = plugin;
    }

    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Race race = OLP.settings.getPlayerRace(player);
        if (race != null) {
            player.sendMessage(Component.text("Race: " + race.getRaceName()));
        } else {
            OLP.settings.setPlayerRace(player, OLP.settings.getRaceByName("Human"));
            player.sendMessage(Component.text("Race: " + "Human"));
        }
        applyRace(player, null);
    }

    public void onPlayerMove(PlayerMoveEvent ev) {
        Player player = ev.getPlayer();
        PlayerConfig playerConfig = OLP.settings.getPlayerConfig(player);
        Race race = OLP.settings.getPlayerRace(player);

        // Manages climbable vine blocks for climbing‑enabled races
        if (race.getCanClimbWalls() && playerConfig.isClimbingEnabled()) {
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
                // Persists vine location to player‑specific climb data
                playerConfig.appendClimbingData(b1.getLocation());
                // Conditionally places vine and persists its location
                if (y % 1 > .40 && b2.getType() == Material.AIR) {
                    // Persists vine location to player climb data
                    ev.getPlayer().sendBlockChange(b2.getLocation(), vine);
                // Reconciles vine state when a player moves between blocks
                    playerConfig.appendClimbingData(b2.getLocation()); ;
                }
                // Reconciles vine state when player moves between blocks
                if (ev.getFrom().getBlockX() != ev.getTo().getBlockX()
                        || ev.getFrom().getBlockZ() != ev.getTo().getBlockZ()
                        || ev.getFrom().getBlockY() != ev.getTo().getBlockY()) {
                    for (Location xyz : playerConfig.getClimbingData()) {
                        // Iterates persisted climb data; skips null locations
                        if (xyz != null) {
                            Location loc = new Location(ev.getFrom().getWorld(), xyz.getBlockX(), xyz.getBlockY(), xyz.getBlockZ());
                            // Resets vine block and removes persisted climb data
                            if (!loc.equals(b1.getLocation()) && loc != b2.getLocation()) {
                                ev.getPlayer().sendBlockChange(loc, loc.getBlock().getBlockData());
                                playerConfig.removeClimbingData(xyz) ;
                            }
                        }
                    }
                }

            }
        }

        //Zora Slowness on Land
        if (race.getIsSlowOnLand()) {
            // Applies slowness to player when not in liquid
            if (ev.getTo().getBlock().isLiquid()) {
                ev.getPlayer().removePotionEffect(PotionEffectType.SLOWNESS);
            } else {
                ev.getPlayer().addPotionEffect(PotionEffectType.SLOWNESS.createEffect(-1, 1));
            }
        }

        //Zora Dont Sink in Water
        if (!race.getSinksInWater()) {
            ev.getPlayer().setGravity(!ev.getPlayer().isInWater());
        } else {
            ev.getPlayer().setGravity(true);
        }

        //Katari swiftsneak
        if (race.getFastSneak()) {
            if (player.isSneaking()) {
                player.setWalkSpeed(0.4f);
            } else {
                player.setWalkSpeed(0.2f);
            }
        }

        //Aven Weakness & Slowness underground
        if (race.getIsWeakUnderGround()) {
            if (ev.getTo().getBlock().getLightFromSky() < 8) {
            // Applies/removes weakness and slowness underground based on config
                player.addPotionEffect(PotionEffectType.WEAKNESS.createEffect(-1, 1));
                player.addPotionEffect(PotionEffectType.SLOWNESS.createEffect(-1, 1));
            } else {
                if (player.hasPotionEffect(PotionEffectType.WEAKNESS))
                    player.removePotionEffect(PotionEffectType.WEAKNESS);
                if (player.hasPotionEffect(PotionEffectType.SLOWNESS))
                    player.removePotionEffect(PotionEffectType.SLOWNESS);
            }
        }

        OLP.settings.replacePlayerConfigs(playerConfig);
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

    /**
     * Adjusts damage based on race and circumstances
     */
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            Race race = OLP.settings.getPlayerRace(player);
            double damage = event.getDamage();

            //Adjust Fall Damage Aven
            if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                event.setDamage(damage * race.getFallDamageMultiplier());
            }

            //Per Tick fire damage Cap
            if (event.getCause().equals(EntityDamageEvent.DamageCause.FIRE) || event.getCause().equals(EntityDamageEvent.DamageCause.LAVA)) {
                double multiple = race.getFireDamageCap();
                if (multiple != 0.0) {
                    if (damage > multiple)
                        event.setDamage(race.getFireDamageCap());
                }
            }
        }

        // Modifies damage based on race and gliding status
        if (event.getDamageSource().getCausingEntity() instanceof Player player) {
            Race race = OLP.settings.getPlayerRace(player);
            Entity item = event.getDamageSource().getDirectEntity();
            if (player.isGliding()) {
                assert item != null;
                // Multiplies damage when gliding and attacking with non‑arrow
                if (!item.getType().name().contains("ARROW")) {
                    double multiplier = race.getFlyingAttackDamageMultiplier();
                    event.setDamage(event.getDamage() * multiplier);
                }
            }
        }
    }

    public void playerItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        Race race = OLP.settings.getPlayerRace(player);

        List<Material> allowedFoods = race.getAllowedFoods();
        List<BuffedFood> buffedFoods = race.getBuffedFoods();

        // Cancels consumption of disallowed food items
        if (!allowedFoods.isEmpty()) {
            Material item = event.getItem().getType();
            boolean inList = false;
            for (Material mat: allowedFoods) {
                if (item == mat) {
                    inList = true;
                    break;
                }
            }
            if (!inList) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("Your race cannot consume this item");
            }
        }

        // Removes poison/hunger effects if raw food is safe
        if (race.getIsRawFoodSafe()) {
            if (event.getPlayer().hasPotionEffect(PotionEffectType.POISON))
                event.getPlayer().removePotionEffect(PotionEffectType.POISON);
            if (event.getPlayer().hasPotionEffect(PotionEffectType.HUNGER))
                event.getPlayer().removePotionEffect(PotionEffectType.HUNGER);
        }

        if (buffedFoods != null) {
            Material item = event.getItem().getType();
            for (BuffedFood buffedFood: buffedFoods) {
                // Cancels consumption; applies saturation and food level
                if (item == buffedFood.getMaterial()) {
                    event.setCancelled(true);
                    float sat = (float) buffedFood.getSaturationBuff();
                    int food = (int) buffedFood.getHungerBuff();
                    player.setSaturation(player.getSaturation() + sat);
                    player.setFoodLevel(player.getFoodLevel() + food);
                    player.getInventory().removeItemAnySlot(event.getItem().asQuantity(1));
                }
            }
        }

        // Schedules delayed race application after milk consumption
        if (event.getItem().getType() == Material.MILK_BUCKET) {
            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    applyRace(event.getPlayer(), null);
                }
            };
            runnable.runTaskLater(OLP, 5);
        }
    }

    public void playerTamePet(EntityTameEvent event) {
        Player player = (Player) event.getOwner();
        Race race = OLP.settings.getPlayerRace(player);
        if (race.getRaceName().equalsIgnoreCase("Katari")) {
            // Cancels Katari player taming wolfs; informs player
            if (event.getEntity() instanceof Wolf) {
                Objects.requireNonNull(Bukkit.getPlayer(event.getOwner().getUniqueId())).sendMessage("Your Race Cannot Tame Wolfs");
                event.setCancelled(true);
            }
        }
    }

    public void playerArmorChange(PlayerArmorChangeEvent event) {
        //Run code to check for armor enchants for race.
        ItemStack item = event.getNewItem();
        Player player = event.getPlayer();
        Race race = OLP.settings.getPlayerRace(player);
        AssignedArmor assignedArmor = race.getArmor();
        if (assignedArmor != null && !item.isEmpty()) {
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
        // Cancels or applies race based on inventory action
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
        // Cancels drop; removes race item; clears enchants; applies race
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
                    String[] invType = Objects.requireNonNull(invItem).getType().toString().split("_");
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
                item.removeEnchantment(Objects.requireNonNull(RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(NamespacedKey.minecraft(enchant.toLowerCase()))));
            }

            this.setRaceEnchants(item, "");
        }

    }

    public void setRaceEnchants(ItemStack item, String enchants) {
        NamespacedKey key = new NamespacedKey(OLP, "oneLifeRaces-enchants");
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer itemContainer = meta.getPersistentDataContainer();
        itemContainer.set(key, PersistentDataType.STRING, enchants);
        item.setItemMeta(meta);
    }

    public String getRaceEnchants(ItemStack item) {
        try {
            NamespacedKey key = new NamespacedKey(OLP, "oneLifeRaces-enchants");
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
        NamespacedKey key = new NamespacedKey(OLP, "oneLifeRaces-raceItem");
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer itemContainer = meta.getPersistentDataContainer();
        itemContainer.set(key, PersistentDataType.BOOLEAN, value);
        item.setItemMeta(meta);
    }

    public Boolean isRaceItem(ItemStack item) {
        try {
            NamespacedKey key = new NamespacedKey(OLP, "oneLifeRaces-raceItem");
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

    private void recordItemEnchants(List<Enchant> enchantsList, ItemStack item, NamespacedKey key) {
        StringBuilder enchants = new StringBuilder();
        for (Enchant enchant: enchantsList) {
            enchants.append(enchant.getEnchantment().getKey().getKey()).append(",").append(enchant.getLevel()).append(";");
        }
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, enchants.toString());
        item.setItemMeta(itemMeta);
    }

    private ItemStack getEnchantedItem(Player player, ItemStack item, Material assignedMaterial, List<Enchant> assignedEnchants) {

        List<Enchant> raceAssignedEnchants = new ArrayList<>();
        List<Enchant> overriddenEnchants = new ArrayList<>();
        //Need to see if the player has an existing item otherwise create one with race assigned default
        if (item == null) item = new ItemStack(assignedMaterial);
        //We need to keep track of any enchanting we overwrite so we can restore the item to original state when not with player

        for (Enchant enchant : assignedEnchants) {
            if (item.getEnchantments().containsKey(enchant.getEnchantment())) {
                if (item.getEnchantmentLevel(enchant.getEnchantment()) <= enchant.getLevel()) {
                    overriddenEnchants.add(new Enchant(enchant.getEnchantment(), item.getEnchantmentLevel(enchant.getEnchantment())));
                    raceAssignedEnchants.add(new Enchant(enchant.getEnchantment(), enchant.getLevel()));
                    item.getEnchantments().remove(enchant.getEnchantment());
                }
            } else {
                raceAssignedEnchants.add(new Enchant(enchant.getEnchantment(), enchant.getLevel()));
            }
        }
        if (!overriddenEnchants.isEmpty()) {
            recordItemEnchants(overriddenEnchants, item, new NamespacedKey(OLP, "oneLifeRaces-overriddenEnchants"));
        }
        if (!raceAssignedEnchants.isEmpty()) {
            recordItemEnchants(raceAssignedEnchants, item, new NamespacedKey(OLP, "oneLifeRaces-assignedEnchants"));
        }

        //Apply race enchantments required to Item
        for (Enchant enchant: raceAssignedEnchants) {
            item.addEnchantment(enchant.getEnchantment(), enchant.getLevel());
        }

        return item;
    }

    public void giveRaceArmor(Player player, ItemStack item, AssignedArmor assignedArmor) {

        if (assignedArmor.getHelmetMaterial() != null)
        {
            if (player.getInventory().getHelmet() != null) item = player.getInventory().getHelmet();
            item = getEnchantedItem(player, item, assignedArmor.getHelmetMaterial(), assignedArmor.getHelmetEnchants());
            player.getInventory().setHelmet(item);
        }
        if (assignedArmor.getChestplateMaterial() != null) {
            if(player.getInventory().getChestplate() != null) item = player.getInventory().getChestplate();
            item = getEnchantedItem(player, item, assignedArmor.getChestplateMaterial(), assignedArmor.getChestplateEnchants());
            player.getInventory().setChestplate(item);
        }
        if (assignedArmor.getLeggingsMaterial() != null) {
            if(player.getInventory().getLeggings() != null) item = player.getInventory().getLeggings();
            item = getEnchantedItem(player, item, assignedArmor.getLeggingsMaterial(), assignedArmor.getLeggingsEnchants());
            player.getInventory().setLeggings(item);
        }
        if (assignedArmor.getBootsMaterial() != null) {
            if (player.getInventory().getBoots() != null) item = player.getInventory().getBoots();
            item = getEnchantedItem(player, item, assignedArmor.getBootsMaterial(), assignedArmor.getBootsEnchants());
            player.getInventory().setBoots(item);
        }
    }

    public void giveRaceEffects(Player player, List<PotionEffect> Effects) {
        for (PotionEffect effect : Effects) {
            PotionEffectType potion = effect.getType();

            player.addPotionEffect(Objects.requireNonNull(potion.createEffect(-1, 0)));
        }

    }

    public void giveStartItems(Player player, HashMap<String, Integer> startItems) {
        PlayerConfig playerConfig = OLP.settings.getPlayerConfig(player);
        if (startItems != null && !playerConfig.getGivenStartItems()) {
            // Adds configured start items to player inventory
            for (String key : startItems.keySet()) {
                ItemStack startItem = new ItemStack(Objects.requireNonNull(Material.getMaterial(key)));
                startItem.setAmount(startItems.get(key));
                player.getInventory().addItem(startItem);
            }
            playerConfig.setGivenStartItems(true);
            OLP.settings.replacePlayerConfigs(playerConfig);
        }

    }

    public void setRepeatItems(final Player player, List<RepeatItem> repeatItems) {
        if (repeatItems != null) {
            PlayerConfig playerConfig = OLP.settings.getPlayerConfig(player);
            for (RepeatItem repeatItem : repeatItems) {
                final ItemStack itemStack = new ItemStack(repeatItem.getMaterial(), repeatItem.getQtyPerRepeat());
                this.setIsRaceItem(itemStack, true);
                final int Max = repeatItem.getMax();
                final int QtyPer = repeatItem.getQtyPerRepeat();
                int TimeSec = repeatItem.getSecondsTillRepeat() * 20;
                BukkitRunnable runnable = new BukkitRunnable() {
                    public void run() {
                        Race race = OLP.settings.getPlayerRace(player);

                        if (!race.getRepeatItems().contains(repeatItem)) {
                            this.cancel();
                            playerConfig.removeRunningTask(this.getTaskId());
                            OLP.settings.replacePlayerConfigs(playerConfig);
                        }

                        if (!playerConfig.getRunningTasks().containsKey(this.getTaskId()) && playerConfig.getRunningTasks().containsValue(repeatItem.getUUID()))
                            this.cancel();
                        else if (!playerConfig.getRunningTasks().containsKey(this.getTaskId()))
                            playerConfig.addRunningTask(this.getTaskId(), repeatItem.getUUID());

                        int inventoryCount = 0;
                        ItemStack inv = null;

                        for (ItemStack i : player.getInventory().getContents()) {
                            if (i != null && i.getType() == itemStack.getType()) {
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
                            itemStack.setAmount(QtyPer);
                            player.getInventory().addItem(itemStack);
                        }

                    }
                };
                runnable.runTaskTimerAsynchronously(OLP, 0L, TimeSec);
            }
            OLP.settings.replacePlayerConfigs(playerConfig);
        }
    }

    public void applyRace(Player player, ItemStack item) {
        player.clearActivePotionEffects();
        Race race = OLP.settings.getPlayerRace(player);
        if (race != null) {
            if (race.isEnabled()) {
                Objects.requireNonNull(player.getAttribute(Attribute.SCALE)).setBaseValue(race.getScale());
                Objects.requireNonNull(player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE)).setBaseValue(race.getReach());

                if (race.getHasFreezeTicks()) {
                    player.setFreezeTicks(0);
                    player.lockFreezeTicks(true);
                } else {
                    player.lockFreezeTicks(false);
                }

                giveRaceEffects(player, race.getEffects());
                giveRaceArmor(player, item, race.getArmor());

                giveStartItems(player, race.getStartItems());
                setRepeatItems(player, race.getRepeatItems());
            } else {
                for (PotionEffect effect: race.getEffects()) {
                    player.removePotionEffect(effect.getType());
                }
                for (ItemStack items : player.getInventory().getContents())
                    clearRaceItemEnchants(items);
                Objects.requireNonNull(player.getAttribute(Attribute.SCALE)).setBaseValue(Objects.requireNonNull(player.getAttribute(Attribute.SCALE)).getDefaultValue());
                Objects.requireNonNull(player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE)).setBaseValue(Objects.requireNonNull(player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE)).getDefaultValue());
            }
        }

    }
}
