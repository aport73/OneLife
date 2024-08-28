package xyz.acyber.oneLifeRaces;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import io.papermc.paper.event.player.PlayerPickItemEvent;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;


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

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent ev) {
        //Code for Wall Climbing
        Boolean climbingEnabled = getConfig().getBoolean("races." + getPlayerRace(ev.getPlayer()) + ".climbingEnabled");
        if(climbingEnabled) {
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
                if (y % 1 > .40 && b2.getType() == Material.AIR) {
                    ev.getPlayer().sendBlockChange(b2.getLocation(), vine);
                }
                if(ev.getFrom().getBlockX() != ev.getTo().getBlockX()
                        || ev.getFrom().getBlockZ() != ev.getTo().getBlockZ()
                        || ev.getFrom().getBlockY() != ev.getTo().getBlockY()) {
                    ev.getPlayer().sendBlockChange(ev.getFrom().getBlock().getLocation(), ev.getFrom().getBlock().getBlockData());                    ev.getPlayer().sendBlockChange(ev.getFrom().getBlock().getRelative(BlockFace.UP).getLocation(), ev.getFrom().getBlock().getRelative(BlockFace.UP).getBlockData());
                }

            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        setPlayerTasks(player, 0);
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
        applyRaceEffects(player,null);
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
        applyRaceEffects(event.getPlayer(),null);
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
                for (var key : mobConfig.getKeys(false)) {
                        getLogger().config(mobConfig.getString(key));
                        ItemStack item = ItemStack.of(Material.valueOf(mobConfig.getString(key)));
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
            applyRaceEffects(event.getPlayer(), null);
        }
    }

    @EventHandler
    public void playerArmorChange(PlayerArmorChangeEvent event) {
        //Run code to check for armor enchants for race.
        ItemStack item = event.getNewItem();
        String race = getPlayerRace(event.getPlayer());
        ConfigurationSection equipConfig = getConfig().getConfigurationSection("races." + race + ".equipment");
        if (equipConfig != null && !item.isEmpty()) {
            applyRaceEffects(event.getPlayer(), null);
        }
    }

    @EventHandler
    public void entityPickupEvent(EntityPickupItemEvent event) {
        if (event.getEntity().getType() == EntityType.PLAYER) {
            Player player = (Player) event.getEntity();
            ItemStack item = event.getItem().getItemStack();
            applyRaceEffects(player, item);
        }
    }

    @EventHandler
    public void inventoryClickEvent(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getAction() == InventoryAction.PLACE_ALL ||
                event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY ||
                event.getAction() == InventoryAction.PLACE_ONE ||
                event.getAction() == InventoryAction.PLACE_SOME ||
                event.getAction() == InventoryAction.SWAP_WITH_CURSOR) {
            if (event.getClickedInventory() != null) {
                ItemStack item = event.getClickedInventory().getItem(event.getSlot());
                if (event.getAction() == InventoryAction.SWAP_WITH_CURSOR || item == null) item = event.getCursor();
                event.setCancelled(isRaceItem(item) &&
                        (!event.getClickedInventory().getType().equals(InventoryType.PLAYER) ||
                                event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY)
                );
                raceItemChecks(item);
                if ((event.getClickedInventory().getType().equals(InventoryType.PLAYER) ||
                        event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) && event.getAction() != InventoryAction.SWAP_WITH_CURSOR) {
                    applyRaceEffects(player, item);
                }
            }
        }
    }

    @EventHandler
    public void playerDropItem(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        event.setCancelled(isRaceItem(item));
        raceItemChecks(item);
    }

    public void raceItemChecks(ItemStack item) {
        String RaceEnchants = getRaceEnchants(item);
        if (RaceEnchants != null && !RaceEnchants.equals("")) {
            for (String enchant: RaceEnchants.split(",")) {
                item.removeEnchantment(RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(NamespacedKey.minecraft(enchant.toLowerCase())));
            }
            setRaceEnchants(item, "");
        }
    }

    @EventHandler
    public void playerDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        setPlayerTasks(player, 0);
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

    public void setPlayerStartItem(Player player, Boolean start) {
        NamespacedKey key = new NamespacedKey(this, "oneLifeRaces-playerStartItem");
        PersistentDataContainer playerContainer = player.getPersistentDataContainer();
        playerContainer.set(key, PersistentDataType.BOOLEAN, start);
    }

    public Boolean getPlayerStartItem(Player player) {
        NamespacedKey key = new NamespacedKey(this, "oneLifeRaces-playerStartItem");
        PersistentDataContainer playerContainer = player.getPersistentDataContainer();
        if (playerContainer.has(key)) {
            return playerContainer.get(key, PersistentDataType.BOOLEAN);
        } else {
            return false;
        }
    }

    public void setRaceEnchants(ItemStack item, String enchants) {
        NamespacedKey key = new NamespacedKey(this, "oneLifeRaces-enchants");
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer itemContainer = meta.getPersistentDataContainer();
        itemContainer.set(key, PersistentDataType.STRING, enchants);
        item.setItemMeta(meta);
    }

    public String getRaceEnchants(ItemStack item) {
        try {
            NamespacedKey key = new NamespacedKey(this, "oneLifeRaces-enchants");
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
        NamespacedKey key = new NamespacedKey(this, "oneLifeRaces-raceItem");
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer itemContainer = meta.getPersistentDataContainer();
        itemContainer.set(key, PersistentDataType.BOOLEAN, value);
        item.setItemMeta(meta);
    }

    public Boolean isRaceItem(ItemStack item) {
        try {
            NamespacedKey key = new NamespacedKey(this, "oneLifeRaces-raceItem");
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

    public Double getPlayerDamage(Player player) {
        NamespacedKey key = new NamespacedKey(this, "oneLifeRaces-totalDamageTaken");
        PersistentDataContainer playerContainer = player.getPersistentDataContainer();
        if (playerContainer.has(key)) {
            return playerContainer.get(key, PersistentDataType.DOUBLE);
        } else {
            return null;
        }
    }

    public void setPlayerTasks(Player player, int task) {
        NamespacedKey key = new NamespacedKey(this, "oneLifeRaces-tasks");
        PersistentDataContainer playerContainer = player.getPersistentDataContainer();
        playerContainer.set(key, PersistentDataType.INTEGER, task);
    }

    public int getPlayerTasks(Player player) {
        NamespacedKey key = new NamespacedKey(this, "oneLifeRaces-tasks");
        PersistentDataContainer playerContainer = player.getPersistentDataContainer();
        if (playerContainer.has(key)) {
            return playerContainer.get(key, PersistentDataType.INTEGER);
        } else {
            return 0;
        }
    }

    public void applyRaceEffects(Player player, ItemStack item) {
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
            ConfigurationSection raceEquipment = raceConfig.getConfigurationSection("equipment");
            if (raceEquipment != null) {
                for (String key : raceEquipment.getKeys(false)) {
                    Boolean exists = false;
                    if (item == null || !item.getType().toString().endsWith(key.toUpperCase())) {
                        switch (key) {
                            case "HELMET":
                                if (player.getInventory().getHelmet() != null) item = player.getInventory().getHelmet();
                                break;
                            case "CHESTPLATE":
                                if (player.getInventory().getChestplate() != null) item = player.getInventory().getChestplate();
                                break;
                            case "LEGGINGS":
                                if (player.getInventory().getLeggings() != null) item = player.getInventory().getLeggings();
                                break;
                            case "BOOTS":
                                if (player.getInventory().getBoots() != null) item = player.getInventory().getBoots();
                                break;
                            default:
                                for (ItemStack i : player.getInventory().getStorageContents()) {
                                    if (i != null && i.getType().toString().endsWith(key.toUpperCase())) {
                                        item = i;
                                    }
                                }
                                break;
                        }
                    }
                    if (item == null) {
                        item = new ItemStack(Material.valueOf(raceEquipment.getString(key + ".Default")));
                    } else {
                        exists = true;
                    }
                    String upgrades = "";
                    for (String upgrade: raceEquipment.getConfigurationSection(key + ".Enchants").getKeys(false)) {
                        int level = raceEquipment.getInt(key + ".Enchants." + upgrade);
                        upgrades += upgrade + ",";
                        Enchantment enchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(NamespacedKey.minecraft(upgrade.toLowerCase()));
                        if (getRaceEnchants(item) != null || getRaceEnchants(item) != "") {
                            item.addEnchantment(enchantment, level);
                        }
                    }
                    setRaceEnchants(item, upgrades);
                    if (!exists) {
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
                                break;
                        }
                    }
                }
            }
            ConfigurationSection startItems = raceConfig.getConfigurationSection("startItems");
            if (startItems != null && !getPlayerStartItem(player)) {
                for (String key : startItems.getKeys(false)) {
                    ItemStack startItem = new ItemStack(Material.getMaterial(key));
                    startItem.setAmount(startItems.getInt(key));
                    player.getInventory().addItem(startItem);
                }
                setPlayerStartItem(player, true);
            }
            ConfigurationSection repeatItems = raceConfig.getConfigurationSection("repeatItems");
            if (repeatItems != null) {
                for (String key : repeatItems.getKeys(false)) {
                    ItemStack repeatItem = new ItemStack(Material.getMaterial(key));
                    setIsRaceItem(repeatItem, true);
                    int Max = repeatItems.getInt(key + ".Max");
                    int QtyPer = repeatItems.getInt(key + ".QtyPer");
                    int TimeSec = repeatItems.getInt(key + ".TimeSec") * 20;
                    if (getPlayerTasks(player) <= 0) {
                        setPlayerTasks(player,1);
                        BukkitRunnable runnable = new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (getPlayerTasks(player) <= 0) {
                                    this.cancel();
                                }
                                int offHandCount = 0;
                                int inventoryCount = 0;
                                ItemStack inv = null;
                                ItemStack oHand = null;
                                Boolean exist = false;
                                for (ItemStack i : player.getInventory().getStorageContents()) {
                                    if (i != null && i.getType() == repeatItem.getType()) {
                                        inventoryCount = i.getAmount();
                                        setIsRaceItem(i, true);
                                        inv = i;
                                        exist = true;
                                        break;
                                    }
                                }
                                ItemStack offHand = player.getInventory().getItemInOffHand();
                                if (offHand != null && offHand.getType() == repeatItem.getType()) {
                                    offHandCount = offHand.getAmount();
                                    setIsRaceItem(offHand, true);
                                    oHand = offHand;
                                    exist = true;
                                }
                                if (!exist) {
                                    repeatItem.setAmount(QtyPer);
                                    player.getInventory().addItem(repeatItem);
                                } else {
                                    if (offHandCount + inventoryCount < Max) {
                                        if (oHand != null) {
                                            oHand.setAmount(oHand.getAmount() + QtyPer);
                                        } else {
                                            inv.setAmount(inv.getAmount() + QtyPer);
                                        }
                                    }
                                }
                            }
                        };
                        runnable.runTaskTimerAsynchronously(this, 0, TimeSec);
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
            if (stack.getSender().isOp()) {
                reloadConfig();
                stack.getSender().sendRichMessage("One Life Plugin Reloaded");
            } else {
                stack.getSender().sendRichMessage("You don't have the need permission to reload the plugin");
            }
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("races")) {
            stack.getSender().sendRichMessage(args[1] + "'s race: further work to be done");
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("races") && args[2].equalsIgnoreCase("resetStartItems")) {
            if (stack.getSender().isOp()) {
                Player player = Bukkit.getPlayer(args[1]);
                setPlayerStartItem(player, false);
                stack.getSender().sendRichMessage(player.getName() + "'s start items have been reset");
            } else {
                stack.getSender().sendRichMessage("You don't have the need permission to reset the start items");
            }
        }
        if (args.length == 4 && args[0].equalsIgnoreCase("races") && args[2].equalsIgnoreCase("setRace")) {
            Player player = Bukkit.getPlayer(args[1]);
            if (stack.getSender().isOp()) {
                setPlayerTasks(player,0);
                setPlayerRace(player, args[3]);
                applyRaceEffects(player, null);
                stack.getSender().sendMessage(player.getName() + " has been set to " + args[3]);
            } else if (getPlayerRace(player).equalsIgnoreCase("Human")) {
                setPlayerTasks(player,0);
                setPlayerRace(player, args[3]);
                applyRaceEffects(player, null);
                stack.getSender().sendMessage( "Your race has been set to " + args[3]);
            } else {
                stack.getSender().sendMessage( "You are unable to change your race more than once, please ask an op for more assistance.");
                sendMsgOps(stack.getSender().getName() + " tried to change their race.");
            }
        }
    }

    @Override
    public @NotNull Collection<String> suggest(final @NotNull CommandSourceStack stack, final @NotNull String[] args) {
        Collection<String> sug = new ArrayList<>();
        if (args.length <= 1) {
            if (stack.getSender().isOp()) {
                sug.add("reload");
            }
            sug.add("races");
            sug.add("help");
            return sug;
        }
        if (args.length <= 2 && args[0].equalsIgnoreCase("races")) {
            if (stack.getSender().isOp()) {
                if (args[1].length() == 0) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        sug.add(player.getName());
                    }
                } else {
                    for (String name : getConfig().getConfigurationSection("races").getKeys(false)) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                                sug.add(player.getName());
                            }
                        }
                    }
                }

            } else {
                sug.add(stack.getSender().getName());
            }
            return sug;
        }
        if (args.length <= 3 && args[0].equalsIgnoreCase("races")) {
            sug.add("setRace");
            if (stack.getSender().isOp()) {
                sug.add("resetStartItems");
            }
            return sug;
        }
        if (args.length <= 4 && args[0].equalsIgnoreCase("races") && args[2].equalsIgnoreCase("setRace")) {
            if (args[3].length() == 0) {
                sug.addAll(getConfig().getConfigurationSection("races").getKeys(false));
            } else {
                for (String key : getConfig().getConfigurationSection("races").getKeys(false)) {
                    if (key.toLowerCase().startsWith(args[3].toLowerCase())) {
                        sug.add(key);
                    }
                }
            }
            return sug;
        }
        return Collections.emptyList();
    }
    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return this.permission() == null || sender.hasPermission(this.permission());
    }

    @Override
    public @Nullable String permission() {
        return null;
    }
}
