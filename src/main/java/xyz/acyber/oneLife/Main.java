package xyz.acyber.oneLife;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.NotNull;
import xyz.acyber.oneLife.managers.CommandManager;
import xyz.acyber.oneLife.managers.MobManager;
import xyz.acyber.oneLife.managers.RaceManager;
import xyz.acyber.oneLife.managers.ScoreManager;
import xyz.acyber.oneLife.managers.LivesManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;

public class Main extends JavaPlugin implements Listener {
    //TODO Redo Mob Sounds
    //TODO Idea - Disable Traveling Merchants being able to use Invisibility?
    public final MobManager mm = new MobManager(this);
    public final RaceManager rm = new RaceManager(this);
    public final ScoreManager sm = new ScoreManager(this);
    public final LivesManager lm = new LivesManager(this);
    public final CommandManager cm = new CommandManager(this);

    public boolean mobMEnabled = false;
    public boolean raceMEnabled = false;
    public boolean scoreMEnabled = false;
    public boolean livesMEnabled = false;
    public boolean lifeGEnabled = false;

    public LuckPerms lpAPI;

    @Override
    public void onEnable() {

        saveDefaultConfig();
        saveDefaultPlayerConfig();

        FileConfiguration config = getConfig();
        ConfigurationSection modes = config.getConfigurationSection("Modes");
        assert modes != null;
        mobMEnabled = modes.getBoolean("MobManager");
        raceMEnabled = modes.getBoolean("RaceManager");
        scoreMEnabled = modes.getBoolean("ScoreManager");
        lifeGEnabled = modes.getBoolean("LifeGifting");
        livesMEnabled = modes.getBoolean("LivesManager");

        Bukkit.getPluginManager().registerEvents(this, this);

        LifecycleEventManager<@NotNull Plugin> manager = this.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, commands -> commands.registrar().register(cm.loadCmds()));

        if (livesMEnabled)
            lm.enableDeathsScoreboard();

        lpAPI = LuckPermsProvider.get();
    }

    public FileConfiguration getPlayerConfig() {
        File file = new File(getDataFolder(), "players.yml");
        return YamlConfiguration.loadConfiguration(file);
    }

    public void savePlayerConfig(@NotNull FileConfiguration config) {
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
                //noinspection ResultOfMethodCallIgnored
                dataFolder.mkdirs();
            }

            File saveTo = new File(dataFolder, "players.yml");
            if (!saveTo.exists()) {
                //noinspection ResultOfMethodCallIgnored
                saveTo.createNewFile();
            }

            for (OfflinePlayer offlinePlayer : Bukkit.getWhitelistedPlayers()) {
                checkPlayerInConfig(offlinePlayer.getUniqueId().toString());
            }
        } catch (IOException e) {
            this.getLogger().log(Level.SEVERE, "Failed to save Default players.yml", e.getCause());
            this.getLogger().log(Level.INFO, "Stacktrace", e.fillInStackTrace());
        }
    }

    public void logToFile(String message, String path) {
        try {
            File dataFolder = createFolder(path);

            File saveTo = new File(dataFolder, "scores-" + LocalDate.now() + ".txt");
            if (!saveTo.exists()) {
                //noinspection ResultOfMethodCallIgnored
                saveTo.createNewFile();
            }

            FileWriter fw = new FileWriter(saveTo, true);
            PrintWriter pw = new PrintWriter(fw);
            pw.println(message);
            pw.flush();
            pw.close();
        } catch (IOException e) {
            this.getLogger().log(Level.SEVERE, "Failed to logToFile with Path: " + path + " & Message: " + message, e.getCause());
            this.getLogger().log(Level.INFO, "Stacktrace", e.fillInStackTrace());
        }

    }

    public void exportToCsv(String filePath, List<String[]> data, String[] headers) {

        //Create Folder
        File dataFolder = createFolder(filePath);

        try (PrintWriter writer = new PrintWriter(new FileWriter(dataFolder.getPath() + "/scores-" + LocalDate.now() + ".csv"), true)) {

            // Write headers
            if (headers != null && headers.length > 0) {
                writer.println(String.join(",", headers));
            }

            // Write data rows
            for (String[] row : data) {
                writer.println(String.join(",", row));
            }
            System.out.println("CSV file exported successfully to: " + filePath);
        } catch (IOException e) {
            System.err.println("Error exporting CSV: " + e.getMessage());
        }
    }

    private File createFolder(String filePath) {
        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) {
            //noinspection ResultOfMethodCallIgnored
            dataFolder.mkdirs();
        }
        if (!filePath.equalsIgnoreCase("")) {
            File root = dataFolder;
            dataFolder = new File(root, filePath);
            if (!dataFolder.exists()) {
                //noinspection ResultOfMethodCallIgnored
                dataFolder.mkdirs();
            }
        }
        return dataFolder;
    }

    public void checkPlayerInConfig(String playerUUID) {
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

    public void sendMsgOps(String components) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("ServerOperator")) {
                player.sendMessage(components);
            }
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void playerHarvest(PlayerHarvestBlockEvent ev) {
        if (scoreMEnabled)
            sm.playerHarvest(ev);
    }

    @EventHandler
    public void playerFish(PlayerFishEvent ev) {
        if (scoreMEnabled)
            sm.playerFish(ev);
    }

    @EventHandler
    public void blockPlaced(BlockPlaceEvent event) {
        //Check if Scoring Enabled and Run Function if So
        if (scoreMEnabled)
            sm.blocksPlaced(event);
    }

    @EventHandler
    public void blockMined(BlockBreakEvent event) {
        //Check if Scoring Enabled and Run Function if So
        if (scoreMEnabled)
            sm.blocksMined(event);
    }

    @EventHandler
    public void playerAdvanced(PlayerAdvancementDoneEvent event) {
        if (scoreMEnabled)
            sm.playerAdvanced(event);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent ev) {
        if (raceMEnabled)
            rm.onPlayerMove(ev);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        checkPlayerInConfig(player.getUniqueId().toString());
        setPlayerTasks(player, 0);
        player.sendMessage(Component.text("Hello, " + player.getName() + "!"));
        if (raceMEnabled)
            rm.onPlayerJoin(event);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (mobMEnabled)
            mm.onEntityDeath(event);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (raceMEnabled)
            rm.onDeath(event);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (raceMEnabled)
            rm.applyRace(event.getPlayer(), null);
        if (livesMEnabled)
            lm.setPlayerGameMode(event.getPlayer());
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (mobMEnabled)
            mm.onEntitySpawn(event);
    }

    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        if (livesMEnabled)
            if (!event.getPlayer().isOp())
                lm.setPlayerGameMode(event.getPlayer());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (raceMEnabled)
            rm.onDamage(event);
    }

    @EventHandler
    public void playerItemConsume(PlayerItemConsumeEvent event) {
        if (raceMEnabled)
            rm.playerItemConsume(event);
    }

    @EventHandler
    public void playerTamePet(EntityTameEvent event) {
        if (raceMEnabled)
            rm.playerTamePet(event);
    }

    @EventHandler
    public void playerArmorChange(PlayerArmorChangeEvent event) {
        if (raceMEnabled)
            rm.playerArmorChange(event);
    }

    @EventHandler
    public void entityPickupEvent(EntityPickupItemEvent event) {
        if (raceMEnabled)
            rm.entityPickupEvent(event);
    }

    @EventHandler
    public void inventoryClickEvent(InventoryClickEvent event) {
        if (raceMEnabled)
            rm.inventoryClickEvent(event);
    }

    @EventHandler
    public void playerDropItem(PlayerDropItemEvent event) {
        if (raceMEnabled)
            rm.playerDropItem(event);
    }

    @EventHandler
    public void playerDisconnect(@NotNull PlayerQuitEvent event) {
        FileConfiguration config = getPlayerConfig();
        Player player = event.getPlayer();
        config.set(player.getUniqueId() + ".playerTasks", 0);
        if (scoreMEnabled)
            config.set(player.getUniqueId() + ".playerTotalXp", config.getInt(player.getUniqueId() + ".playerTotalXp") + 1);
        savePlayerConfig(config);
    }

    public void setPlayerTasks(@NotNull Player player, int task) {
        FileConfiguration config = getPlayerConfig();
        config.set(player.getUniqueId() + ".playerTasks", task);
        savePlayerConfig(config);
    }

    public void setFeatures() {
        FileConfiguration config = getConfig();
        ConfigurationSection modes = config.getConfigurationSection("Modes");
        assert modes != null;
        modes.set("MobManager", mobMEnabled);
        modes.set("RaceManager", raceMEnabled);
        modes.set("ScoreManager", scoreMEnabled);
        modes.set("LifeGifting", lifeGEnabled);
        saveConfig();
    }

    public int getPlayerTasks(@NotNull Player player) {
        FileConfiguration config = getPlayerConfig();
        return config.getInt(player.getUniqueId() + ".playerTasks");
    }

    public void giveLife(Player giver, Player receiver) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getMainScoreboard();
        Objective deaths = board.getObjective("deaths");
        assert deaths != null;
        int recscore = deaths.getScoreFor(receiver).getScore();
        int givscore = deaths.getScoreFor(giver).getScore();
        if (givscore < 4) {
            if (recscore > 4) {
                deaths.getScoreFor(receiver).setScore(4);
                receiver.setGameMode(GameMode.SURVIVAL);
            } else {
                deaths.getScoreFor(receiver).setScore(recscore - 1);
            }
            deaths.getScoreFor(giver).setScore(givscore + 1);
        }
    }
}
