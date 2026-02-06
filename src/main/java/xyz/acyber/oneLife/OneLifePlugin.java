package xyz.acyber.oneLife;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.node.types.SuffixNode;
import net.luckperms.api.node.types.WeightNode;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.NotNull;
import xyz.acyber.oneLife.DataObjects.PlayerScore;
import xyz.acyber.oneLife.DataObjects.Settings;
import xyz.acyber.oneLife.Runables.AFKChecker;
import xyz.acyber.oneLife.Runables.AutoSaver;
import xyz.acyber.oneLife.Runables.DayNightChecker;
import xyz.acyber.oneLife.Runables.PassiveMobsModifier;
import xyz.acyber.oneLife.Managers.*;
import xyz.acyber.oneLife.Serialization.EntityTypeAdapter;
import xyz.acyber.oneLife.Serialization.LocationTypeAdapter;
import xyz.acyber.oneLife.Serialization.MaterialTypeAdapter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class OneLifePlugin extends JavaPlugin {

    public static OneLifePlugin OLP;
    public LuckPerms lpAPI;
    public Settings settings = new Settings();
    public HashMap<UUID, PlayerScore> scoreData = new HashMap<>();

    // Track persistence state to reduce disk writes
    private final AtomicBoolean settingsDirty = new AtomicBoolean(true);
    private final AtomicBoolean scoresDirty = new AtomicBoolean(true);

    public final EventManager em = new EventManager(this);
    public final MobManager mm = new MobManager(this);
    public final RaceManager rm = new RaceManager(this);
    public final ScoreManager sm = new ScoreManager(this);
    public final LivesManager lm = new LivesManager(this);
    public final CommandManager cm = new CommandManager(this);
    public final DayNightChecker dnc = new DayNightChecker(this);
    public final PassiveMobsModifier pmm = new PassiveMobsModifier(this);
    private boolean night = false;

    public HashMap<UUID,Long> afkLastInput;
    public BukkitTask afkChecker;
    public BukkitTask dnChecker;
    public BukkitTask pmModifier;
    public BukkitTask autoSave;
    public long afkCheck;

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(org.bukkit.Material.class, new MaterialTypeAdapter())
            .registerTypeAdapter(org.bukkit.entity.EntityType.class, new EntityTypeAdapter())
            .registerTypeAdapter(org.bukkit.Location.class, new LocationTypeAdapter())
            .create();

    /**
     * Enables plugin; registers events, commands, settings, and features
     */
    @Override
    public void onEnable() {
        OLP = this;
        try {
            settings = loadSettings();
            if (Objects.equals(settings, new Settings())) {
                saveSettings();
            }
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to load settings, using defaults", e);
            settings = new Settings(); // safe fallback
        }

        getServer().getPluginManager().registerEvents(em, this);

        //Register Commands
        LifecycleEventManager<@NotNull Plugin> manager = this.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, commands -> commands.registrar().register(cm.loadCmds()));

        //Load into memory settings and scoring objects from saved files
        // Ensure settings is initialized to avoid NPE

        scoreData = loadScoring();

        if (settings.isLuckPermsEnabled())
            lpAPI = LuckPermsProvider.get();

        loadFeatures();

        ensureWhitelistedPlayersSetup();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        saveSettingsSync();
        savePlayerScoresSync();
    }

    public void markSettingsDirty() { settingsDirty.set(true); }
    public void markScoresDirty() { scoresDirty.set(true); }

    public void saveSettings() {
        if (!settingsDirty.get()) return;
        Runnable task = this::saveSettingsSync;
        if (Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTaskAsynchronously(this, task);
        } else {
            task.run();
        }
    }

    private void saveSettingsSync() {
        if (!settingsDirty.getAndSet(false)) return;
        Path path = Paths.get(getDataFolder() + "/settings.json");
        createDirectoryOrBackups(path);

        try {
            String jsonString = gson.toJson(settings);
            Files.write(path, jsonString.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            settingsDirty.set(true); // retry later
            getLogger().log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
            getLogger().log(Level.SEVERE, e.getMessage());
            getLogger().log(Level.SEVERE, "Failed to save settings.json", e.getCause());
        }
    }

    public void savePlayerScores() {
        if (!scoresDirty.get()) return;
        Runnable task = this::savePlayerScoresSync;
        if (Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTaskAsynchronously(this, task);
        } else {
            task.run();
        }
    }

    private void savePlayerScoresSync() {
        if (!scoresDirty.getAndSet(false)) return;
        if (scoreData == null || scoreData.isEmpty()) return;
        Map<UUID, PlayerScore> snapshot = new HashMap<>(scoreData);
        for (UUID uuid : snapshot.keySet()) {
            PlayerScore playerScore = snapshot.get(uuid);
            Path path = Path.of(getDataFolder() + settings.getScoring().getPathToScoreData() + playerScore.getPlayerName() + " - " + playerScore.getUUID() + ".json");
            createDirectoryOrBackups(path);
            try {
                String jsonString = gson.toJson(playerScore);
                Files.write(path, jsonString.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                scoresDirty.set(true); // retry later
                getLogger().log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
                getLogger().log(Level.SEVERE, e.getMessage());
                getLogger().log(Level.SEVERE, "Failed to save PlayerScore", e.getCause());
            }
        }
    }

    public void reload() {
        savePlayerScores();
        loadSettings();
        loadScoring();
        loadFeatures();
    }

    public void createDirectoryOrBackups(@NotNull Path path) {
        try {
            Path parent = path.getParent();
            if (parent == null) return;
            Path backupDir = parent.resolve("Backup");
            String timestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss").format(LocalDateTime.now());
            Path backupPath = backupDir.resolve(path.getFileName() + " - " + timestamp);
            if (Files.exists(path)) {
                Files.createDirectories(backupDir);
                Files.move(path, backupPath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.createDirectories(parent);
            }
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
            getLogger().log(Level.SEVERE, e.getMessage());
            getLogger().log(Level.SEVERE, "Failed to create backup of settings.json", e.getCause());
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

    public File createFolder(String filePath) {
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

    public void initialisePlayer(@NotNull OfflinePlayer player) {
        settings.initialisePlayer(player);
        if (!scoreData.containsKey(player.getUniqueId())) {
            PlayerScore playerScore = new PlayerScore(this, player);
            scoreData.put(player.getUniqueId(), playerScore);
            markScoresDirty();
        }
    }

    public void sendMsgOps(String components) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("ServerOperator")) {
                player.sendMessage(components);
            }
        }
    }

    public void sendMsgDevs(String components) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("OneLife.Developer")) {
                player.sendMessage(components);
            }
        }
    }

    public void giveLife(Player giver, Player receiver) {
        //TODO refactor life gifting feature
        // current version not modular and doesn't corretly apply what has been gifted to scores
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

    public void afkUpdater(@NotNull Player player){

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

    public boolean isNight() { return night; }
    public void setNight(boolean night) { this.night = night; }

    private void ensureWhitelistedPlayersSetup() {
        for (OfflinePlayer player : Bukkit.getWhitelistedPlayers()) {
            initialisePlayer(player);
        }
        saveSettings();
        savePlayerScores();
    }

    private void loadFeatures() {
        //Start Day Night Checking
        dnChecker = dnc.runTaskTimer(this,0,20L);

        loadNightHostiles();
        loadAFKChecking();

        if (settings.getEnabledFeatures().getEnabledLivesManager())
            lm.enableDeathsScoreboard();
        autoSave = new AutoSaver(this).runTaskTimerAsynchronously(this, 0, settings.getAutoSaveIntervalSeconds() * 20L);

    }

    private Settings loadSettings() {
        Path path = Paths.get(getDataFolder() + "/settings.json");
        Settings load;
        if (!Files.exists(path)) {
            getLogger().log(Level.INFO, "settings.json does not exist. Creating new one.");
            load = new Settings();
            return load;
        }
        try {
            String json = Files.readString(path, StandardCharsets.UTF_8);
            load = gson.fromJson(json, Settings.class);
            return load;
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, e.getMessage());
            getLogger().log(Level.SEVERE, "Failed to load settings.json", e.getCause());
            return new Settings();
        }
    }

    private @NotNull HashMap<UUID, PlayerScore> loadScoring() {
        HashMap<UUID, PlayerScore> loadData = new HashMap<>();
        Path path = Paths.get(getDataFolder() + settings.getScoring().getPathToScoreData());
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Failed to create score data directory", e);
            return loadData;
        }
        File[] files = path.toFile().listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".json")) {
                    PlayerScore data = null;
                    try {
                        String json = Files.readString(file.toPath(), StandardCharsets.UTF_8);
                        data = gson.fromJson(json, PlayerScore.class);
                    } catch (IOException e) {
                        getLogger().log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
                        getLogger().log(Level.SEVERE, e.getMessage());
                        getLogger().log(Level.SEVERE, "Failed to load PlayerScore", e.getCause());
                    }
                    data = Objects.requireNonNullElseGet(data, PlayerScore::new);
                    if (loadData.containsKey(data.getUUID()))
                        loadData.replace(data.getUUID(), data);
                    else
                        loadData.put(data.getUUID(), data);
                }
            }
        }
        return loadData;
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
        if (!settings.getEnabledFeatures().getEnabledAFKChecker()) {
            if (afkChecker != null)
                afkChecker.cancel();
            if (afkLastInput != null)
                afkLastInput.clear();
            return;
        }
        if (lpAPI == null) {
            getLogger().log(Level.WARNING, "LuckPerms not available; AFK checker disabled.");
            return;
        }
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

        afkCheck = settings.getAfkCheckerConfig().getSecondsInterval()*20L;
        afkLastInput = new HashMap<>();
        afkChecker = new AFKChecker(this, afkLastInput, afk, lpAPI, sm).runTaskTimer(this,0,afkCheck);
    }
}
