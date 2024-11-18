package xyz.acyber.oneLifeRaces;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.google.common.collect.Lists;
import io.netty.util.internal.StringUtil;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.minecraft.world.food.FoodConstants;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.bukkit.scheduler.BukkitRunnable;
import org.w3c.dom.DOMStringList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.time.LocalDate;
import java.util.logging.Level;

public final class OneLifeRaces extends JavaPlugin implements Listener, BasicCommand {
    //TODO Redo Mob Sounds
    //TODO Idea - Disable Traveling Merchants being able to use Invisibility?
    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        saveDefaultPlayerConfig();
        getConfig();
        Bukkit.getPluginManager().registerEvents(this, this);

        LifecycleEventManager<Plugin> manager = this.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register("onelife", this);
        });
    }

    public FileConfiguration getPlayerConfig() {
        File file = new File(getDataFolder(), "players.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        return config;
    }

    public void savePlayerConfig(FileConfiguration config) {
        try {
            File file = new File(getDataFolder(), "players.yml");
            config.save(file);
        } catch (IOException e) {
            this.getLogger().log(Level.SEVERE, "Failed to save players.yml", e.getCause());
            this.getLogger().log(Level.INFO, "Stacktrace", e.fillInStackTrace());
        }
    }

    public void saveDefaultPlayerConfig() {
        try {
            File dataFolder = getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }

            File saveTo = new File(dataFolder, "players.yml");
            if (!saveTo.exists()) {
                saveTo.createNewFile();
            }

            for (OfflinePlayer offlinePlayer : Bukkit.getWhitelistedPlayers()) {
                checkPlayerinConfig(offlinePlayer.getUniqueId().toString());
            }
        } catch (IOException e)
        {
            this.getLogger().log(Level.SEVERE, "Failed to save Default players.yml", e.getCause());
            this.getLogger().log(Level.INFO, "Stacktrace", e.fillInStackTrace());
        }
    }

    public void checkPlayerinConfig(String playerUUID){
        FileConfiguration config = getPlayerConfig();
        ConfigurationSection p = config.getConfigurationSection(playerUUID);
        if (p == null) {
            config.set(playerUUID + ".playerRace", "Human");
            config.set(playerUUID + ".playerStartItem", "");
            config.set(playerUUID + ".playerTasks", 0);
            config.set(playerUUID + ".playerTotalXp", 0);
            config.set(playerUUID + ".playerBlocksPlaced", 0);
            config.set(playerUUID + ".playerBlocksMined", 0);
            config.set(playerUUID + ".playerAdvancements", 0);
            config.set(playerUUID + ".playerClimbs", true);
            config.set(playerUUID + ".playerClimbVines", "");
            savePlayerConfig(config);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void blockPlaced(BlockPlaceEvent event) {
        FileConfiguration config = getPlayerConfig();
        Player player = event.getPlayer();
        config.set(player.getUniqueId() + ".playerBlocksPlaced", config.getInt(player.getUniqueId() + ".playerBlocksPlaced") + 1);
        savePlayerConfig(config);
    }

    @EventHandler
    public void blockMined(BlockBreakEvent event) {
        FileConfiguration config = getPlayerConfig();
        Player player = event.getPlayer();
        config.set(player.getUniqueId() + ".playerBlocksMined", config.getInt(player.getUniqueId() + ".playerBlocksMined") + 1);
        savePlayerConfig(config);
    }

    @EventHandler
    public void playerAdvanced(PlayerAdvancementDoneEvent event) {
        FileConfiguration config = getPlayerConfig();
        Player player = event.getPlayer();
        config.set(player.getUniqueId() + ".playerAdvancements", config.getInt(player.getUniqueId() + ".playerAdvancements") + 1);
        savePlayerConfig(config);
    }

    public void playerScore(CommandSourceStack stack, OfflinePlayer player, Boolean showInChat) {
        ConfigurationSection scoring = getConfig().getConfigurationSection("Scoring");
        ConfigurationSection playerScore = getPlayerConfig().getConfigurationSection(player.getUniqueId().toString());

        assert scoring != null;

        assert playerScore != null;
        double Death = player.getStatistic(Statistic.DEATHS) * scoring.getDouble("Death");
        double Xp = playerScore.getInt("playerTotalXp") * scoring.getDouble("Xp");
        double OnlineHr = (double) ((player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20) / 60) / 60 * scoring.getDouble("OnlineHr");
        double BlocksPlaced = playerScore.getInt("playerBlocksPlaced") * scoring.getDouble("BlocksPlaced");
        double BlocksMined = playerScore.getInt("playerBlocksMined") * scoring.getDouble("BlocksMined");
        double Achevements = playerScore.getInt("playerAdvancements") * scoring.getDouble("Achevements");
        double TotalPoints = Death + Xp + OnlineHr + BlocksPlaced + BlocksMined + Achevements;
        List<String> MobMessages = new ArrayList<>();
        for (String key: scoring.getConfigurationSection("MobKills").getKeys(false)) {
            double MobPoints = player.getStatistic(Statistic.KILL_ENTITY, EntityType.valueOf(key)) * scoring.getConfigurationSection("MobKills").getDouble(key);
            TotalPoints = TotalPoints + MobPoints;
            MobMessages.add(StringUtils.capitalize(key.toLowerCase()) + " Killed: " + player.getStatistic(Statistic.KILL_ENTITY, EntityType.valueOf(key)) + ", Points: " + MobPoints);
        }
        if (showInChat) {
            stack.getSender().sendMessage(player.getName() + "'s Scorecard:");
            stack.getSender().sendMessage("Total Points: " + TotalPoints);
            stack.getSender().sendMessage("Deaths: " + player.getStatistic(Statistic.DEATHS) + ", Points: " + Death);
            stack.getSender().sendMessage("Xp: " + playerScore.getInt("playerTotalXp") + ", Points: " + Xp);
            stack.getSender().sendMessage("OnlineHr: " + (double) ((player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20) / 60) / 60 + ", Points: " + OnlineHr);
            if (MobMessages.size() > 0) {
                for (String msg : MobMessages) {
                    stack.getSender().sendMessage(msg);
                }
            }
            stack.getSender().sendMessage("Blocks Placed: " + playerScore.getInt("playerBlocksPlaced") + ", Points: " + BlocksPlaced);
            stack.getSender().sendMessage("Blocks Mined: " + playerScore.getInt("playerBlocksMined") + ", Points: " + BlocksMined);
            stack.getSender().sendMessage("Achevements: " + playerScore.getInt("playerAdvancements") + ", Points: " + Achevements);
            stack.getSender().sendMessage("------- END -----");
        } else {
            String path = "Score";
            logToFile(player.getName() + "'s Scorecard:", path);
            logToFile("Total Points: " + TotalPoints, path);
            logToFile("Deaths: " + player.getStatistic(Statistic.DEATHS) + ", Points: " + Death, path);
            logToFile("Xp: " + playerScore.getInt("playerTotalXp") + ", Points: " + Xp, path);
            logToFile("OnlineHr: " + (double) ((player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20) / 60) / 60 + ", Points: " + OnlineHr, path);
            if (MobMessages.size() > 0) {
                for (String msg : MobMessages) {
                    logToFile(msg, path);
                }
            }
            logToFile("Blocks Placed: " + playerScore.getInt("playerBlocksPlaced") + ", Points: " + BlocksPlaced, path);
            logToFile("Blocks Mined: " + playerScore.getInt("playerBlocksMined") + ", Points: " + BlocksMined, path);
            logToFile("Achevements: " + playerScore.getInt("playerAdvancements") + ", Points: " + Achevements, path);
            logToFile("------- END -----", path);
        }
    }

    public void logToFile(String message, String path)
    {
        try {
            File dataFolder = getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }
            if (!path.equalsIgnoreCase("")) {
                File root = dataFolder;
                dataFolder = new File(root, path);
                if (!dataFolder.exists()) {
                    dataFolder.mkdirs();
                }
            }

            File saveTo = new File(dataFolder, "scores-" + LocalDate.now() + ".txt");
            if (!saveTo.exists()) {
                saveTo.createNewFile();
            }

            FileWriter fw = new FileWriter(saveTo, true);
            PrintWriter pw = new PrintWriter(fw);
            pw.println(message);
            pw.flush();
            pw.close();
        } catch (IOException e)
        {
            this.getLogger().log(Level.SEVERE, "Failed to logToFile with Path: " + path + " & Message: " + message, e.getCause());
            this.getLogger().log(Level.INFO, "Stacktrace", e.fillInStackTrace());
        }

    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent ev) {
        //Code for Wall Climbing
        Boolean climbingEnabled = getConfig().getBoolean("races." + getPlayerRace(ev.getPlayer()) + ".climbingEnabled");
        if(climbingEnabled && getPlayerClimbs(ev.getPlayer())) {
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
                setPlayerClimbVines(ev.getPlayer(), getPlayerClimbVines(ev.getPlayer()) + b1.getLocation().blockX() + ","+b1.getLocation().blockY() + "," + b1.getLocation().blockZ() + "/");
                if (y % 1 > .40 && b2.getType() == Material.AIR) {
                    ev.getPlayer().sendBlockChange(b2.getLocation(), vine);
                    setPlayerClimbVines(ev.getPlayer(), getPlayerClimbVines(ev.getPlayer()) + b2.getLocation().blockX() + ","+b2.getLocation().blockY() + "," + b2.getLocation().blockZ() + "/");
                }
                if(ev.getFrom().getBlockX() != ev.getTo().getBlockX()
                        || ev.getFrom().getBlockZ() != ev.getTo().getBlockZ()
                        || ev.getFrom().getBlockY() != ev.getTo().getBlockY()) {
                    for (String xyz: getPlayerClimbVines(ev.getPlayer()).split("/")) {
                        if (xyz != "" && xyz != null) {
                            int xVine = Integer.parseInt(xyz.split(",")[0]);
                            int yVine = Integer.parseInt(xyz.split(",")[1]);
                            int zVine = Integer.parseInt(xyz.split(",")[2]);
                            Location loc = new Location(ev.getFrom().getWorld(), xVine, yVine, zVine);
                            if (loc != b1.getLocation() && loc != b2.getLocation()) {
                                ev.getPlayer().sendBlockChange(loc, loc.getBlock().getBlockData());
                                setPlayerClimbVines(ev.getPlayer(), getPlayerClimbVines(ev.getPlayer()).replaceAll(xyz +"/",""));
                            }
                        }
                    }
                }

            }
        }

        //Zora Slowness on Land
        if(getConfig().getBoolean("races." + getPlayerRace(ev.getPlayer()) + ".slowOnLand")) {
            if(ev.getTo().getBlock().isLiquid()) {
                ev.getPlayer().removePotionEffect(PotionEffectType.SLOWNESS);
            } else {
                ev.getPlayer().addPotionEffect(PotionEffectType.SLOWNESS.createEffect(-1,1));
            }
        }

        //Zora Dont Sink in Water
        if(getConfig().getBoolean("races." + getPlayerRace(ev.getPlayer()) + ".stopSinkInWater")) {
            ev.getPlayer().setGravity(!ev.getPlayer().isInWater());
        } else {
            ev.getPlayer().setGravity(true);
        }

        //Katari swiftsneak
        if(getConfig().getBoolean("races." + getPlayerRace(ev.getPlayer()) + ".fastSneak")) {
            if (ev.getPlayer().isSneaking()) {
                ev.getPlayer().setWalkSpeed(0.4f);
            } else {
                ev.getPlayer().setWalkSpeed(0.2f);
            }
        }

        //Aven Weakness & Slowness underground
        if (getConfig().getBoolean("races." + getPlayerRace(ev.getPlayer()) + ".weakUnderGround")) {
            Player player = ev.getPlayer();
            if (ev.getTo().getBlock().getLightFromSky() < 8) {
                player.addPotionEffect(PotionEffectType.WEAKNESS.createEffect(-1,1));
                player.addPotionEffect(PotionEffectType.SLOWNESS.createEffect(-1,1));
            } else {
                if (player.hasPotionEffect(PotionEffectType.WEAKNESS))
                    player.removePotionEffect(PotionEffectType.WEAKNESS);
                if (player.hasPotionEffect(PotionEffectType.SLOWNESS))
                    player.removePotionEffect(PotionEffectType.SLOWNESS);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        checkPlayerinConfig(player.getUniqueId().toString());
        setPlayerTasks(player, 0);
        player.sendMessage(Component.text("Hello, " + player.getName() + "!"));
        String race = getPlayerRace(player);
        if (race != null) {
            player.sendMessage(Component.text("Race: " + race));
        } else {
            setPlayerRace(player, "Human");
            player.sendMessage(Component.text("Race: " + "Human"));
        }
        applyRace(player,null);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        //Change Rotten Flesh Drops
        int numbFlesh = 1;
        for (ItemStack item : event.getDrops()) {
            if (item.getType() == Material.ROTTEN_FLESH) {
                numbFlesh = item.getAmount();
            }
        }
        event.getDrops().remove(new ItemStack(Material.ROTTEN_FLESH, numbFlesh));
        ConfigurationSection mobConfig = getConfig().getConfigurationSection("MOBS." + event.getEntityType().name());
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

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        List<ItemStack> drops = event.getDrops();
        for (ItemStack item : drops) {
            if (isRaceItem(item)) {
                event.getDrops().remove(item);
                break;
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        applyRace(event.getPlayer(),null);
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
                ConfigurationSection mobItems = mobConfig.getConfigurationSection("ITEMS");
                if (mobItems != null) {
                    for (var key : mobItems.getKeys(false)) {
                        getLogger().config(mobItems.getString(key));
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
                        spawnedEntity.getEquipment().setItem(EquipmentSlot.valueOf(key),item);
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
                        double result = r.nextDouble(high-low) + low;
                        Objects.requireNonNull(spawnedEntity.getAttribute(Attribute.GENERIC_SCALE)).setBaseValue(result);
                    }
                    ConfigurationSection mobSpeed = mobBuffs.getConfigurationSection("Speed");
                    if (mobSpeed != null) {
                        Random r = new Random();
                        double multiple = mobSpeed.getDouble("BASE");
                        double base = spawnedEntity.getAttribute(Attribute.GENERIC_SCALE).getBaseValue() * multiple;
                        double variance = mobSpeed.getDouble("VARIANCE");
                        double low = base - variance;
                        double high = base + variance;
                        double result = r.nextDouble(high-low) + low;
                        Objects.requireNonNull(spawnedEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(result);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            double damage = event.getDamage();
            String race = getPlayerRace(player);

            //Adjust Fall Damage Aven
            if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL) && race.equals("Aven")) {
                event.setDamage(damage * getConfig().getDouble("races." + race + ".fallDamage"));
            }

            //Slow Burn Damage Dwarven
            if (event.getCause().equals(EntityDamageEvent.DamageCause.FIRE) || event.getCause().equals(EntityDamageEvent.DamageCause.LAVA)) {
                double multiple = getConfig().getDouble("races." + getPlayerRace(player) + ".firedamage");
                if (multiple != 0.0) {
                    if (damage > multiple)
                        event.setDamage(getConfig().getDouble("races." + getPlayerRace(player) + ".firedamage"));
                }
            }
        }

        if (event.getDamageSource().getCausingEntity() instanceof Player player) {
            Entity item = event.getDamageSource().getDirectEntity();
            if (player.isGliding() && !item.getType().name().contains("ARROW")) {
                Double multiplier = getConfig().getDouble("races." + getPlayerRace(player) + ".flyingAttackDamage");
                if (multiplier == null)
                    multiplier = 1.0;
                event.setDamage(event.getDamage() * multiplier);
            }
        }
    }

    @EventHandler
    public void playerItemConsume(PlayerItemConsumeEvent event) {
        //
        List<String> allowedFoods = getConfig().getStringList("races." + getPlayerRace(event.getPlayer()) + ".allowedFoods");
        ConfigurationSection buffedFoods = getConfig().getConfigurationSection("races." + getPlayerRace(event.getPlayer()) + ".buffedFoods");
        boolean rawFoodSafe = getConfig().getBoolean("races." + getPlayerRace(event.getPlayer()) + ".rawFoodSafe");

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

        if(rawFoodSafe) {
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
            runnable.runTaskLater(this,5);
        }
    }

    @EventHandler
    public void playerTamePet(EntityTameEvent event) {
        if(getPlayerRace(Bukkit.getPlayer(event.getOwner().getUniqueId())).equalsIgnoreCase("Katari")) {
            if (event.getEntity() instanceof Wolf) {
                Bukkit.getPlayer(event.getOwner().getUniqueId()).sendMessage("Your Race Cannot Tame Wolfs");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void playerArmorChange(PlayerArmorChangeEvent event) {
        //Run code to check for armor enchants for race.
        ItemStack item = event.getNewItem();
        String race = getPlayerRace(event.getPlayer());
        ConfigurationSection equipConfig = getConfig().getConfigurationSection("races." + race + ".equipment");
        if (equipConfig != null && !item.isEmpty()) {
            applyRace(event.getPlayer(), null);
        }
    }

    @EventHandler
    public void entityPickupEvent(EntityPickupItemEvent event) {
        if (event.getEntity().getType() == EntityType.PLAYER) {
            Player player = (Player) event.getEntity();
            ItemStack item = event.getItem().getItemStack();
            applyRace(player, item);
        }
    }

    @EventHandler
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

    @EventHandler
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

            for(ItemStack invItem : playerInventory.getContents()) {
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
            for(String enchant : RaceEnchants.split(",")) {
                item.removeEnchantment((Enchantment)Objects.requireNonNull((Enchantment)RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(NamespacedKey.minecraft(enchant.toLowerCase()))));
            }

            this.setRaceEnchants(item, "");
        }

    }

    @EventHandler
    public void playerDisconnect(PlayerQuitEvent event) {
        FileConfiguration config = getPlayerConfig();
        Player player = event.getPlayer();
        config.set(player.getUniqueId() + ".playerTasks", 0);
        config.set(player.getUniqueId() + ".playerTotalXp", config.getInt(player.getUniqueId() + ".playerTotalXp") + 1);
        savePlayerConfig(config);
    }

    public String getPlayerRace(Player player) {
        FileConfiguration config = getPlayerConfig();
        return config.getString(player.getUniqueId() + ".playerRace");
//        NamespacedKey key = new NamespacedKey(this, "oneLifeRaces-playerRace");
//        PersistentDataContainer playerContainer = player.getPersistentDataContainer();
//        if (playerContainer.has(key)) {
//            return playerContainer.get(key, PersistentDataType.STRING);
//        } else {
//            return null;
//        }

    }

    public void setPlayerRace(Player player, String playerRace) {
        FileConfiguration config = getPlayerConfig();
        config.set(player.getUniqueId() + ".playerRace", playerRace);
        savePlayerConfig(config);
//        NamespacedKey key = new NamespacedKey(this, "oneLifeRaces-playerRace");
//        PersistentDataContainer playerContainer = player.getPersistentDataContainer();
//        playerContainer.set(key, PersistentDataType.STRING, playerRace);
    }

    public void setPlayerStartItem(Player player, Boolean start) {
        FileConfiguration config = getPlayerConfig();
        config.set(player.getUniqueId() + ".playerStartItem", start);
        savePlayerConfig(config);
//        NamespacedKey key = new NamespacedKey(this, "oneLifeRaces-playerStartItem");
//        PersistentDataContainer playerContainer = player.getPersistentDataContainer();
//        playerContainer.set(key, PersistentDataType.BOOLEAN, start);
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

    public void setPlayerTasks(Player player, int task) {
            FileConfiguration config = getPlayerConfig();
            config.set(player.getUniqueId() + ".playerTasks", task);
            savePlayerConfig(config);
//        NamespacedKey key = new NamespacedKey(this, "oneLifeRaces-tasks");
//        PersistentDataContainer playerContainer = player.getPersistentDataContainer();
//        playerContainer.set(key, PersistentDataType.INTEGER, task);
    }

    public int getPlayerTasks(Player player) {
        FileConfiguration config = getPlayerConfig();
        return config.getInt(player.getUniqueId() + ".playerTasks");
//        NamespacedKey key = new NamespacedKey(this, "oneLifeRaces-tasks");
//        PersistentDataContainer playerContainer = player.getPersistentDataContainer();
//        if (playerContainer.has(key)) {
//            return playerContainer.get(key, PersistentDataType.INTEGER);
//        } else {
//            return 0;
//        }
    }

    public void setPlayerClimbs(Player player, Boolean climbOn) {
        FileConfiguration config = getPlayerConfig();
        config.set(player.getUniqueId() + ".playerClimbs", climbOn);
        savePlayerConfig(config);
//        NamespacedKey key = new NamespacedKey(this, "oneLifeRaces-climbOn");
//        PersistentDataContainer playerContainer = player.getPersistentDataContainer();
//        playerContainer.set(key, PersistentDataType.BOOLEAN, climbOn);
    }

    public Boolean getPlayerClimbs(Player player) {
        FileConfiguration config = getPlayerConfig();
        return config.getBoolean(player.getUniqueId() + ".playerClimbs");
//        NamespacedKey key = new NamespacedKey(this, "oneLifeRaces-climbOn");
//        PersistentDataContainer playerContainer = player.getPersistentDataContainer();
//        if (playerContainer.has(key)) {
//            return playerContainer.get(key, PersistentDataType.BOOLEAN);
//        } else {
//            return true;
//        }
    }

    public void setPlayerClimbVines(Player player, String climbed) {
            FileConfiguration config = getPlayerConfig();
            config.set(player.getUniqueId() + ".playerClimbVines", climbed);
            savePlayerConfig(config);
//        NamespacedKey key = new NamespacedKey(this, "oneLifeRaces-climbed");
//        PersistentDataContainer playerContainer = player.getPersistentDataContainer();
//        playerContainer.set(key, PersistentDataType.STRING, climbed);
    }

    public String getPlayerClimbVines(Player player) {
        FileConfiguration config = getPlayerConfig();
        String vines = config.getString(player.getUniqueId() + ".playerClimbVines");
        if (vines == null) return "";
        return vines;
//        NamespacedKey key = new NamespacedKey(this, "oneLifeRaces-climbed");
//        PersistentDataContainer playerContainer = player.getPersistentDataContainer();
//        if (playerContainer.has(key)) {
//            return playerContainer.get(key, PersistentDataType.STRING);
//        } else {
//            return "";
//        }
    }

    public void giveRaceEquipment(Player player, ItemStack item, ConfigurationSection Equipment) {
        for(String key : Equipment.getKeys(false)) {
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
                        for(ItemStack i : player.getInventory().getContents()) {
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

            for(String upgrade : ((ConfigurationSection)Objects.requireNonNull(Equipment.getConfigurationSection(key + ".Enchants"))).getKeys(false)) {
                int level = Equipment.getInt(key + ".Enchants." + upgrade);
                Enchantment enchantment = (Enchantment)RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(NamespacedKey.minecraft(upgrade.toLowerCase()));

                assert enchantment != null;

                if ((getRaceEnchants(item) == null || ((String)Objects.requireNonNull(getRaceEnchants(item))).isEmpty()) && !item.containsEnchantment(enchantment)) {
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
        for(String effect : Effects) {
            PotionEffectType potion = (PotionEffectType)Registry.EFFECT.get(NamespacedKey.minecraft(effect.toLowerCase()));

            assert potion != null;

            player.addPotionEffect((PotionEffect)Objects.requireNonNull(potion.createEffect(-1, 0)));
        }

    }

    public void giveStartItems(Player player, ConfigurationSection startItems) {
        FileConfiguration config = this.getPlayerConfig();
        if (startItems != null && !config.getBoolean(String.valueOf(player.getUniqueId()) + ".playerStartItem")) {
            for(String key : startItems.getKeys(false)) {
                ItemStack startItem = new ItemStack((Material)Objects.requireNonNull(Material.getMaterial(key)));
                startItem.setAmount(startItems.getInt(key));
                player.getInventory().addItem(new ItemStack[]{startItem});
            }

            this.setPlayerStartItem(player, true);
        }

    }

    public void setRepeatItems(final Player player, ConfigurationSection repeatItems) {
        if (repeatItems != null) {
            for(String key : repeatItems.getKeys(false)) {
                final ItemStack repeatItem = new ItemStack((Material)Objects.requireNonNull(Material.getMaterial(key)));
                this.setIsRaceItem(repeatItem, true);
                final int Max = repeatItems.getInt(key + ".Max");
                final int QtyPer = repeatItems.getInt(key + ".QtyPer");
                int TimeSec = repeatItems.getInt(key + ".TimeSec") * 20;
                if (this.getPlayerTasks(player) <= 0) {
                    this.setPlayerTasks(player, 1);
                    BukkitRunnable runnable = new BukkitRunnable() {
                        public void run() {
                            if (OneLifeRaces.this.getPlayerTasks(player) <= 0) {
                                this.cancel();
                            }

                            int inventoryCount = 0;
                            ItemStack inv = null;

                            for(ItemStack i : player.getInventory().getContents()) {
                                if (i != null && i.getType() == repeatItem.getType()) {
                                    inventoryCount += i.getAmount();
                                    OneLifeRaces.this.setIsRaceItem(i, true);
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
                    runnable.runTaskTimerAsynchronously(this, 0L, (long)TimeSec);
                }
            }
        }

    }

    public void applyRace(Player player, ItemStack item) {
        player.clearActivePotionEffects();
        String race = this.getPlayerRace(player);
        if (race != null) {
            ConfigurationSection raceConfig = this.getConfig().getConfigurationSection("races." + race);

            assert raceConfig != null;

            ((AttributeInstance)Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_SCALE))).setBaseValue(raceConfig.getDouble("scale"));
            ((AttributeInstance)Objects.requireNonNull(player.getAttribute(Attribute.PLAYER_BLOCK_INTERACTION_RANGE))).setBaseValue(raceConfig.getDouble("reach"));
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
        }

    }

   /* Old Race Effects Code for Refferance
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
                                for (ItemStack i : player.getInventory().getContents()) {
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
                                setIsRaceItem(item, true);
                                player.getInventory().setHelmet(item);
                                break;
                            case "CHESTPLATE":
                                setIsRaceItem(item, true);
                                player.getInventory().setChestplate(item);
                                break;
                            case "LEGGINGS":
                                setIsRaceItem(item, true);
                                player.getInventory().setLeggings(item);
                                break;
                            case "BOOTS":
                                setIsRaceItem(item, true);
                                player.getInventory().setBoots(item);
                                break;
                            default:
                                setIsRaceItem(item, true);
                                player.getInventory().addItem(item);
                                break;
                        }
                    }
                }
            }
            ConfigurationSection startItems = raceConfig.getConfigurationSection("startItems");
            FileConfiguration config = getPlayerConfig();
            if (startItems != null && !config.getBoolean(player.getUniqueId() + ".playerStartItem")) {
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
                                int inventoryCount = 0;
                                ItemStack inv = null;
                                Boolean exist = false;
                                for (ItemStack i : player.getInventory().getContents()) {
                                    if (i != null && i.getType() == repeatItem.getType()) {
                                        inventoryCount += i.getAmount();
                                        setIsRaceItem(i, true);
                                        if (!exist)
                                            inv = i;
                                        exist = true;
                                        break;
                                    }
                                }
                                if (!exist) {
                                    repeatItem.setAmount(QtyPer);
                                    player.getInventory().addItem(repeatItem);
                                } else {
                                    if (inventoryCount < Max) {
                                        inv.setAmount(inv.getAmount() + QtyPer);
                                    }
                                }
                            }
                        };
                        runnable.runTaskTimerAsynchronously(this, 0, TimeSec);
                    }
                }
            }
            if (raceConfig.getBoolean("lockFreezeTicks")) {
                player.setFreezeTicks(0);
            }
            player.lockFreezeTicks(raceConfig.getBoolean("lockFreezeTicks"));
        }
    }*/

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
        if (args.length == 1 && args[0].equalsIgnoreCase("climb")) {
            if (getPlayerRace((Player) stack.getSender()).equalsIgnoreCase("Arathim")) {
                if (getPlayerClimbs((Player) stack.getSender())) {
                    setPlayerClimbs((Player) stack.getSender(), false);
                    stack.getSender().sendRichMessage("Climbing Turned Off");
                } else {
                    setPlayerClimbs((Player) stack.getSender(), true);
                    stack.getSender().sendRichMessage("Climbing Turned On");
                }
            }
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("allScores")) {
            stack.getSender().sendMessage("Generating Scores");
            for (OfflinePlayer offlinePlayer : Bukkit.getWhitelistedPlayers()) {
                if (offlinePlayer.getStatistic(Statistic.PLAY_ONE_MINUTE) != 0)
                    playerScore(stack, offlinePlayer,false);
            }
            stack.getSender().sendMessage("Scores have been logged to a File");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("races")) {
            stack.getSender().sendRichMessage(args[1] + "'s race: further work to be done");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("playerScore")) {
            playerScore(stack, Bukkit.getPlayer(args[1]),true);
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
                for (ItemStack i : player.getInventory().getContents()) {
                    clearRaceItemEnchants(i);
                    if (isRaceItem(i)) {
                        player.getInventory().removeItemAnySlot(i);
                    }
                }
                setPlayerTasks(player,0);
                setPlayerRace(player, args[3]);
                applyRace(player, null);
                stack.getSender().sendMessage(player.getName() + " has been set to " + args[3]);
            } else if (getPlayerRace(player).equalsIgnoreCase("Human")) {
                setPlayerTasks(player,0);
                setPlayerRace(player, args[3]);
                applyRace(player, null);
                stack.getSender().sendMessage( "Your race has been set to " + args[3]);
            } else {
                stack.getSender().sendMessage( "You are unable to change your race more than once, please ask an op for more assistance.");
                sendMsgOps(stack.getSender().getName() + " tried to change their race.");
            }
        }
        if (args.length == 4 && args[0].equalsIgnoreCase("races") && args[2].equalsIgnoreCase("abilities")) {
            Player player = Bukkit.getPlayer(args[1]);
            if (stack.getSender().isOp()) {
                if (args[3].equalsIgnoreCase("ClimbOn")); {
                    setPlayerClimbs(player, true);
                }
                if (args[3].equalsIgnoreCase("ClimbOff")) {
                    setPlayerClimbs(player, false);
                }
            }
        }
    }

    @Override
    public @NotNull Collection<String> suggest(final @NotNull CommandSourceStack stack, final @NotNull String[] args) {
        Collection<String> sug = new ArrayList<>();
        if (args.length <= 1) {
            if (stack.getSender().isOp()) {
                sug.add("reload");
                sug.add("allScores");
                sug.add("playerScore");
            }
            if (getPlayerRace((Player) stack.getSender()).equalsIgnoreCase("Arathim")) {
                sug.add("climb");
            }
            sug.add("races");
            sug.add("help");
            return sug;
        }
        if (args.length <= 2 && (args[0].equalsIgnoreCase("races") || args[0].equalsIgnoreCase("playerScore"))) {
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
                sug.add("abilities");
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
        if (args.length <= 4 && args[0].equalsIgnoreCase("races") && args[2].equalsIgnoreCase("abilities")) {
            sug.add("ClimbOn");
            sug.add("ClimbOff");
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
