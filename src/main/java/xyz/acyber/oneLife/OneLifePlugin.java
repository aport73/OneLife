package xyz.acyber.oneLife;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.node.types.SuffixNode;
import net.luckperms.api.node.types.WeightNode;
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
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.NotNull;
import xyz.acyber.oneLife.DataObjects.ScoreData;
import xyz.acyber.oneLife.DataObjects.Settings;
import xyz.acyber.oneLife.DataObjects.SubSettings.PlayerConfig;
import xyz.acyber.oneLife.Runables.AFKChecker;
import xyz.acyber.oneLife.Runables.DayNightChecker;
import xyz.acyber.oneLife.Runables.PassiveMobsModifier;
import xyz.acyber.oneLife.Events.HasBecomeDayEvent;
import xyz.acyber.oneLife.Events.HasBecomeNightEvent;
import xyz.acyber.oneLife.Managers.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class OneLifePlugin extends JavaPlugin implements Listener {
    //TODO Redo Mob Sounds
    //TODO Idea - Disable Traveling Merchants being able to use Invisibility?

    public LuckPerms lpAPI;
    public Settings settings;
    public HashMap<UUID,ScoreData> scoreDataMap;

    public final MobManager mm = new MobManager(this);
    public final RaceManager rm = new RaceManager(this);
    public final ScoreManager sm = new ScoreManager(this);
    public final LivesManager lm = new LivesManager(this);
    public final CommandManager cm = new CommandManager(this);
    public final DayNightChecker dnc = new DayNightChecker(this);
    public final PassiveMobsModifier pmm = new PassiveMobsModifier(this);

    public boolean isNight = false;

    public HashMap<UUID,Long> afkLastInput;
    public BukkitTask afkChecker;
    public BukkitTask dnChecker;
    public BukkitTask pmModifier;
    public long afkCheck;
    public List<UUID> afkPlayers;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onEnable() {

        saveDefaultConfig();
        saveDefaultPlayerConfig();

        Bukkit.getPluginManager().registerEvents(this, this);

        //Register Commands
        LifecycleEventManager<@NotNull Plugin> manager = this.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, commands -> commands.registrar().register(cm.loadCmds()));

        loadSettings();
        lpAPI = LuckPermsProvider.get();

        if (settings.getEnabledFeatures().getEnabledLivesManager())
            lm.enableDeathsScoreboard();

        loadAFKChecking();
        //Start Day Night Checking
        dnChecker = dnc.runTaskTimer(this,0,20L);
        loadNightHostiles();

        addPlayerConfigForWhitelist();
        try {
            loadScoring();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        addWhitelistToScoring();

        saveSettings();
        savePlayerScores();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        // Save Scoring data
        saveSettings();
        savePlayerScores();
    }

    private void saveSettings() {
        Path path = Paths.get(getDataFolder() + "/settings.json");
        directoryCheck(path);
        try {
            objectMapper.writer().withDefaultPrettyPrinter().writeValue(new File(path.toUri()), settings);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
            getLogger().log(Level.SEVERE, e.getMessage());
            getLogger().log(Level.SEVERE, "Failed to save settings.json", e.getCause());
        }
    }

    private void loadSettings() {
        settings = new Settings(this);
        Path path = Paths.get(getDataFolder() + "/settings.json");
        if (!Files.exists(path)) {
            getLogger().log(Level.INFO, "settings.json does not exist. Creating new one.");
            settings = new Settings(this);
            saveSettings();
            return;
        }
        try {
            settings = objectMapper.readerFor(Settings.class).readValue(new File(path.toUri()));
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, e.getMessage());
            getLogger().log(Level.SEVERE, "Failed to load settings.json", e.getCause());
            settings = new Settings(this);
            saveSettings();
        }
    }

    private void loadScoring() throws JsonProcessingException {
        scoreDataMap = new HashMap<>();
        Path path = Paths.get(getDataFolder() + settings.getPathToScoreData());
        File[] files = path.toFile().listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".json")) {
                    ScoreData data = null;
                    try {
                        data = objectMapper.readerFor(ScoreData.class).readValue(file);
                    } catch (IOException e) {
                        getLogger().log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
                        getLogger().log(Level.SEVERE, e.getMessage());
                        getLogger().log(Level.SEVERE, "Failed to load ScoreData", e.getCause());
                        data = new ScoreData(this);
                    }
                    data = Objects.requireNonNullElseGet(data, ScoreData::new);
                    if (scoreDataMap.containsKey(data.getUUID()))
                        scoreDataMap.replace(data.getUUID(), data);
                    else
                        scoreDataMap.put(data.getUUID(), data);
                }
            }
        }
    }

    public void savePlayerScores() {
        if (scoreDataMap != null) {
            for (UUID uuid : scoreDataMap.keySet()) {
                ScoreData sd = scoreDataMap.get(uuid);
                Path path = Path.of(getDataFolder() + settings.getPathToScoreData() + sd.getPlayerName() + " - " + sd.getUUID() + ".json");
                directoryCheck(path);
                try {
                    objectMapper.writer().withDefaultPrettyPrinter().writeValue(new File(path.toUri()), sd);
                } catch (IOException e) {
                    getLogger().log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
                    getLogger().log(Level.SEVERE, e.getMessage());
                    getLogger().log(Level.SEVERE, "Failed to save ScoreData", e.getCause());
                }
            }
        }
    }

    private void addPlayerConfigForWhitelist() {
        for (OfflinePlayer player : Bukkit.getWhitelistedPlayers()) {
            if (settings != null) {
                assert settings.getPlayerConfigs() != null;
                if (!settings.getPlayerConfigs().containsKey(player.getUniqueId())) {
                    PlayerConfig playerConfig = new PlayerConfig(player.getUniqueId(), player.getName());
                    settings.addPlayerConfigs(playerConfig);
                }
            }
        }
    }

    private void addWhitelistToScoring() {
        for (OfflinePlayer p : Bukkit.getWhitelistedPlayers()) {
            if (!scoreDataMap.containsKey(p.getUniqueId())) {
                sm.initializePlayerScore(p);
            }
        }
    }

    public void reload() {
        reloadConfig();
        loadNightHostiles();
    }

    private void loadNightHostiles() {
        if (settings.getEnabledFeatures().getEnabledNightHostiles()) {
            if (pmModifier != null)
                pmModifier = pmm.runTaskTimer(this, 0, 20L);
        }
        else if (pmModifier != null)
            pmModifier.cancel();
    }

    private void loadAFKChecking() {
        if (settings.getEnabledFeatures().getEnabledAFKChecker()) {
            Group afk = lpAPI.getGroupManager().getGroup("AFK");
            if (afk == null) {
                try {
                    afk = lpAPI.getGroupManager().createAndLoadGroup("AFK").get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }

            afk.data().clear();
            PrefixNode prefixNode = PrefixNode.builder("[AFK] ", 200).build();
            SuffixNode suffixNode = SuffixNode.builder("§7", 200).build();
            WeightNode weightNode = WeightNode.builder(200).build();
            afk.data().add(prefixNode);
            afk.data().add(suffixNode);
            afk.data().add(weightNode);
            lpAPI.getGroupManager().saveGroup(afk);

            afkCheck = getConfig().getLong("AFK.secondsInterval");
            afkLastInput = new HashMap<>();
            afkChecker = new AFKChecker(this, afkLastInput, afk, lpAPI, sm).runTaskTimer(this,0,afkCheck*20L);
        } else {
            if (afkChecker != null)
                afkChecker.cancel();
            if (afkLastInput != null)
                afkLastInput.clear();
        }
    }

    public void directoryCheck(Path path) {
        try {
            Path backupPath = Paths.get(path.toString().replace(".json","") + "-Backup-" + LocalDate.now() + ".json");
            if (Files.exists(path)) {
                if (!Files.exists(backupPath)) {
                    Files.createDirectories(backupPath);
                }
                Files.move(path, backupPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.REPLACE_EXISTING);
            } else
                Files.createDirectories(path.getParent());
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
            getLogger().log(Level.SEVERE, e.getMessage());
            getLogger().log(Level.SEVERE, "Failed to create backup of settings.json", e.getCause());
        }
    }

    public FileConfiguration getPassiveMobsModifierConfig() {
        File file = new File(getDataFolder(), "PassiveMobsModifier.yml");
        return YamlConfiguration.loadConfiguration(file);
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

    @EventHandler
    public void nightFall(HasBecomeNightEvent event) {
        isNight = true;
    }

    @EventHandler
    public void dayBreak(HasBecomeDayEvent event) {
        isNight = false;
    }

    @EventHandler
    public void playerHarvest(PlayerHarvestBlockEvent ev) {
        if (settings.getEnabledFeatures().getEnabledScoreManager())
            sm.playerHarvest(ev);
    }

    @EventHandler
    public void playerFish(PlayerFishEvent ev) {
        if (settings.getEnabledFeatures().getEnabledScoreManager())
            sm.playerFish(ev);
    }

    @EventHandler
    public void blockPlaced(BlockPlaceEvent event) {
        //Check if Scoring Enabled and Run Function if So
        if (settings.getEnabledFeatures().getEnabledScoreManager())
            sm.blocksPlaced(event);
    }

    @EventHandler
    public void blockMined(BlockBreakEvent event) {
        //Check if Scoring Enabled and Run Function if So
        if (settings.getEnabledFeatures().getEnabledScoreManager())
            sm.blocksMined(event);
    }

    @EventHandler
    public void playerAdvanced(PlayerAdvancementDoneEvent event) {
        if (settings.getEnabledFeatures().getEnabledScoreManager())
            sm.playerAdvanced(event);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent ev) {
        if (settings.getEnabledFeatures().getEnabledRaceManager())
            rm.onPlayerMove(ev);
        if (settings.getEnabledFeatures().getEnabledAFKChecker())
            afkUpdater(ev.getPlayer());
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        if (settings.getEnabledFeatures().getEnabledAFKChecker())
            afkUpdater(event.getPlayer());
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        checkPlayerInConfig(player.getUniqueId().toString());
        setPlayerTasks(player, 0);
        player.sendMessage(Component.text("Hello, " + player.getName() + "!"));
        if (settings.getEnabledFeatures().getEnabledRaceManager())
            rm.onPlayerJoin(event);
        if (settings.getEnabledFeatures().getEnabledLivesManager())
            lm.setPlayerGameMode(event.getPlayer());
        if (settings.getEnabledFeatures().getEnabledAFKChecker()) {
            if(!player.hasPermission("OneLife.AFK.Bypass"))
                afkLastInput.put(player.getUniqueId(),System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (settings.getEnabledFeatures().getEnabledMobManager())
            mm.onEntityDeath(event);
        if (settings.getEnabledFeatures().getEnabledScoreManager())
            sm.entityKilled(event);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (settings.getEnabledFeatures().getEnabledRaceManager())
            rm.onDeath(event);
        if (settings.getEnabledFeatures().getEnabledScoreManager())
            sm.playerDeath(event);
        if (settings.getEnabledFeatures().getEnabledAFKChecker())
            afkUpdater(event.getEntity());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (settings.getEnabledFeatures().getEnabledRaceManager())
            rm.applyRace(event.getPlayer(), null);
        if (settings.getEnabledFeatures().getEnabledLivesManager())
            lm.setPlayerGameMode(event.getPlayer());
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (settings.getEnabledFeatures().getEnabledMobManager())
            mm.onEntitySpawn(event);
    }

    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        /*
        if (settings.getEnabledFeatures().getEnabledLivesManager())
            if (!event.getPlayer().isOp())
                lm.setPlayerGameMode(event.getPlayer());
        */
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (settings.getEnabledFeatures().getEnabledRaceManager())
            rm.onDamage(event);
    }

    @EventHandler
    public void playerItemConsume(PlayerItemConsumeEvent event) {
        if (settings.getEnabledFeatures().getEnabledRaceManager())
            rm.playerItemConsume(event);
    }

    @EventHandler
    public void playerTamePet(EntityTameEvent event) {
        if (settings.getEnabledFeatures().getEnabledRaceManager())
            rm.playerTamePet(event);
    }

    @EventHandler
    public void playerArmorChange(PlayerArmorChangeEvent event) {
        if (settings.getEnabledFeatures().getEnabledRaceManager())
            rm.playerArmorChange(event);
    }

    @EventHandler
    public void entityPickupEvent(EntityPickupItemEvent event) {
        if (settings.getEnabledFeatures().getEnabledRaceManager())
            rm.entityPickupEvent(event);
    }

    @EventHandler
    public void inventoryClickEvent(InventoryClickEvent event) {
        if (settings.getEnabledFeatures().getEnabledRaceManager())
            rm.inventoryClickEvent(event);
    }

    @EventHandler
    public void playerDropItem(PlayerDropItemEvent event) {
        if (settings.getEnabledFeatures().getEnabledRaceManager())
            rm.playerDropItem(event);
    }

    @EventHandler
    public void playerDisconnect(@NotNull PlayerQuitEvent event) {
        FileConfiguration config = getPlayerConfig();
        Player player = event.getPlayer();
        config.set(player.getUniqueId() + ".playerTasks", 0);
        if (settings.getEnabledFeatures().getEnabledScoreManager())
            config.set(player.getUniqueId() + ".playerTotalXp", config.getInt(player.getUniqueId() + ".playerTotalXp") + 1);
        savePlayerConfig(config);
        if (settings.getEnabledFeatures().getEnabledAFKChecker()) {
            User user = lpAPI.getUserManager().getUser(player.getUniqueId());
            assert user != null;
            Group afk = lpAPI.getGroupManager().getGroup("afk");
            assert afk != null;
            user.data().remove(InheritanceNode.builder(afk).build());
            lpAPI.getUserManager().saveUser(user);
            afkLastInput.remove(player.getUniqueId());
        }
    }

    public void setPlayerTasks(@NotNull Player player, int task) {
        FileConfiguration config = getPlayerConfig();
        config.set(player.getUniqueId() + ".playerTasks", task);
        savePlayerConfig(config);
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

    private void afkUpdater(@NotNull Player player){

        if (player.hasPermission("OneLife.AFK.Bypass")){
            afkLastInput.remove(player.getUniqueId());
        } else {
            long eventTime = System.currentTimeMillis();

            if(!afkLastInput.containsKey(player.getUniqueId())){
                afkLastInput.put(player.getUniqueId(),eventTime);
                return;
            }

            long lastIn = afkLastInput.get(player.getUniqueId());
            long interval = eventTime - lastIn;

            if(interval < afkCheck){
                return;
            }

            afkLastInput.replace(player.getUniqueId(),eventTime);
        }
    }
}
