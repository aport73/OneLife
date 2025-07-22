package xyz.acyber.oneLife.Managers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.papermc.paper.advancement.AdvancementDisplay;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.minecraft.advancements.*;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.util.datafix.fixes.AdvancementsFix;
import org.bukkit.*;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.advancement.AdvancementRequirement;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.acyber.oneLife.DataObjects.Settings;
import xyz.acyber.oneLife.DataObjects.SubScoreData.MaterialInteractions;
import xyz.acyber.oneLife.DataObjects.SubSettings.Team;
import xyz.acyber.oneLife.Main;
import xyz.acyber.oneLife.DataObjects.ScoreData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static java.lang.Math.round;

public class ScoreManager {

    static Main main;
    private double adventureMultiplier;

    public ScoreManager(Main plugin) {
        main = plugin;
    }

    public ScoreData initializePlayerScore(OfflinePlayer player) {
        Team team = main.settings.getPlayerConfigs().get(player.getUniqueId()).getTeam();
        ScoreData scoreData = new ScoreData(main, player, team);
        main.scoreData.put(player.getUniqueId(),scoreData);
        return scoreData;
    }

    public void playerFish(@NotNull PlayerFishEvent event) {
        //Update Players Fished Placed
        Player player = event.getPlayer();
        ScoreData scoreData;
        String gameMode = player.getGameMode().name();
        if (event.getCaught() != null) {

            if (main.scoreData.containsKey(player.getUniqueId())) scoreData = main.scoreData.get(player.getUniqueId());
            else scoreData = initializePlayerScore(player);

            HashMap<String, Integer> caught = scoreData.getCaught();

            if (playerAFK(player)) {
                if (caught.containsKey("AFK")) caught.replace("AFK", (caught.get("AFK") + 1));
                else caught.put("AFK", 1);
            } else {
                if (caught.containsKey(gameMode)) caught.replace(gameMode, (caught.get(gameMode) + 1));
                else caught.put(gameMode, 1);
            }

            scoreData.setCaught(caught);
            main.scoreData.replace(player.getUniqueId(), scoreData);
        }
    }

    public void playerHarvest(@NotNull PlayerHarvestBlockEvent event) {
        //Update Players Fished Placed
        Player player = event.getPlayer();
        ScoreData scoreData;
        String gameMode = player.getGameMode().name();
        List<ItemStack> harvest = event.getItemsHarvested();

        if (main.scoreData.containsKey(player.getUniqueId()))
            scoreData = main.scoreData.get(player.getUniqueId());
        else
            scoreData = initializePlayerScore(player);

        HashMap<String, Integer> harvested = scoreData.getHarvested();
        HashMap<Material,MaterialInteractions> typeItemsHarvested = scoreData.getTypeItemsHarvested();

        if (playerAFK(player)) {
            for (ItemStack item : harvest) {
                Material material = item.getType();
                if (typeItemsHarvested.containsKey(material)) {
                    MaterialInteractions materialInteractions = typeItemsHarvested.get(material);
                    if (materialInteractions == null)
                        materialInteractions = new MaterialInteractions(main, material, new HashMap<>(), "ItemsHarvested");
                    if (materialInteractions.getCount().containsKey("AFK"))
                        materialInteractions.getCount().replace("AFK", (materialInteractions.getCount().get("AFK") + 1));
                    else
                        materialInteractions.getCount().put("AFK", 1);
                }
                if (harvested.containsKey("AFK"))
                    harvested.replace("AFK", (harvested.get("AFK") + 1));
                else harvested.put("AFK", 1);
            }
        } else {
            for (ItemStack item : harvest) {
                Material material = item.getType();
                if (typeItemsHarvested.containsKey(material)) {
                    MaterialInteractions materialInteractions = typeItemsHarvested.get(material);
                    if (materialInteractions == null)
                        materialInteractions = new MaterialInteractions(main, material, new HashMap<>(), "ItemsHarvested");
                    if (materialInteractions.getCount().containsKey(gameMode))
                        materialInteractions.getCount().replace(gameMode, (materialInteractions.getCount().get(gameMode) + 1));
                    else
                        materialInteractions.getCount().put(gameMode, 1);
                }
                if (harvested.containsKey(gameMode))
                    harvested.replace(gameMode, (harvested.get(gameMode) + 1));
                else
                    harvested.put(gameMode, 1);
            }
        }

        scoreData.setHarvested(harvested);
        scoreData.setTypeItemsHarvested(typeItemsHarvested);
        main.scoreData.replace(player.getUniqueId(), scoreData);
    }

    public void blocksPlaced(@NotNull BlockPlaceEvent event) {
        //Update Players Blocks Placed
        FileConfiguration config = main.getPlayerConfig();
        Player player = event.getPlayer();
        ConfigurationSection scoring = main.getConfig().getConfigurationSection("Scoring");

        if (playerAFK(player)) {
            assert scoring != null;
            config.set(player.getUniqueId() + ".afkPointsOffset", config.getDouble(player.getUniqueId() + ".afkPointsOffset") - (1 * scoring.getDouble("BlocksPlaced")));
            config.set(player.getUniqueId() + ".afkPointsOffset", config.getDouble(player.getUniqueId() + ".afkPointsOffset") - (1 * scoring.getDouble("playerPlaced." + event.getBlock().getType().name())));
        }
        else {
            //Update Total Blocks Placed Count
            config.set(player.getUniqueId() + ".playerBlocksPlaced", config.getInt(player.getUniqueId() + ".playerBlocksPlaced") + 1);
            //Update Specific Blocks Placed Count
            config.set(player.getUniqueId() + ".playerPlaced." + event.getBlock().getType().name(), config.getInt(player.getUniqueId() + ".playerPlaced." + event.getBlock().getType().name()) + 1);
        }
        main.savePlayerConfig(config);
    }

    public void blocksMined(@NotNull BlockBreakEvent event) {
        //Update Players Blocks Placed
        FileConfiguration config = main.getPlayerConfig();
        Player player = event.getPlayer();
        ConfigurationSection scoring = main.getConfig().getConfigurationSection("Scoring");

        if (playerAFK(player)) {
            assert scoring != null;
            config.set(player.getUniqueId() + ".afkPointsOffset", config.getDouble(player.getUniqueId() + ".afkPointsOffset") - (1 * scoring.getDouble("BlocksMined")));
            config.set(player.getUniqueId() + ".afkPointsOffset", config.getDouble(player.getUniqueId() + ".afkPointsOffset") - (1 * scoring.getDouble("playerMined." + event.getBlock().getType().name())));
        }
        else {
            config.set(player.getUniqueId() + ".playerMined." + event.getBlock().getType().name(), config.getDouble(player.getUniqueId() + ".playerMined." + event.getBlock().getType().name()) + 1);
            config.set(player.getUniqueId() + ".playerBlocksMined", config.getDouble(player.getUniqueId() + ".playerBlocksMined") + 1);
        }

        main.savePlayerConfig(config);
    }

    public void playerAdvanced(@NotNull PlayerAdvancementDoneEvent event) {
        FileConfiguration config = main.getPlayerConfig();
        Player player = event.getPlayer();
        event.getAdvancement().getDisplay();
        ConfigurationSection scoring = main.getConfig().getConfigurationSection("Scoring");
        assert scoring != null;

        if (playerAFK(player)) {

            config.set(player.getUniqueId() + ".afkPointsOffset", config.getDouble(player.getUniqueId() + ".afkPointsOffset") - (1 * scoring.getDouble("Achevements")));
        }
        else
            config.set(player.getUniqueId() + ".playerAdvancements", config.getInt(player.getUniqueId() + ".playerAdvancements") - 1);
        main.savePlayerConfig(config);
    }

    public void playerDeath(@NotNull PlayerDeathEvent event) {
        Player player = event.getEntity();
        ScoreData scoreData;
        GameMode gameMode = player.getGameMode();

        if (main.scoreData.containsKey(player.getUniqueId()))
            scoreData = main.scoreData.get(player.getUniqueId());
        else
            scoreData = initializePlayerScore(player);

        HashMap<GameMode, Double> deaths = scoreData.getDeaths();
        if (deaths.containsKey(gameMode)) deaths.replace(gameMode, (deaths.get(gameMode) + 1));
        else deaths.put(gameMode, 1.0);

        scoreData.setDeaths(deaths);
        main.scoreData.replace(player.getUniqueId(), scoreData);
    }

    public void entityKilled(@NotNull EntityDeathEvent event) {
        if (event.getDamageSource().getCausingEntity() != null && event.getDamageSource().getCausingEntity().getType() == EntityType.PLAYER) {
            FileConfiguration config = main.getPlayerConfig();
            Player player = (Player) event.getDamageSource().getCausingEntity();
            ConfigurationSection scoring = main.getConfig().getConfigurationSection("Scoring");

            if (playerAFK(player)) {
                assert scoring != null;
                config.set(player.getUniqueId() + ".afkPointsOffset", config.getDouble(player.getUniqueId() + ".afkPointsOffset") - (1 * scoring.getDouble("MobKills." + event.getEntity().getType().name().toUpperCase())));
            }
            else
                config.set(player.getUniqueId() + ".MobKills." + event.getEntity().getType().name().toUpperCase(), config.getDouble(player.getUniqueId() + ".MobKills." + event.getEntity().getType().name().toUpperCase()) + 1);

            main.savePlayerConfig(config);
        }
    }

    public void timeOnline(@NotNull Player player) {
        FileConfiguration config = main.getPlayerConfig();
        ConfigurationSection scoring = main.getConfig().getConfigurationSection("Scoring");
        assert scoring != null;
        if (player.getGameMode().equals(GameMode.ADVENTURE)) {
            config.set(player.getUniqueId() + ".adventureSec", config.getDouble(player.getUniqueId() + ".adventureSec") + 1);
            config.set(player.getUniqueId() + ".adventureHr", (config.getDouble(player.getUniqueId() + ".adventureSec") + 1)/60/60);
        } else if (playerAFK(player)) {
            config.set(player.getUniqueId() + ".afkSec", config.getDouble(player.getUniqueId() + ".afkSec") + 1);
            config.set(player.getUniqueId() + ".afkHr", (config.getDouble(player.getUniqueId() + ".afkSec") + 1)/60/60);
        } else {
            config.set(player.getUniqueId() + ".survivalSec", config.getDouble(player.getUniqueId() + ".survivalSec") + 1);
            config.set(player.getUniqueId() + ".survivalHr", (config.getDouble(player.getUniqueId() + ".survivalSec") + 1)/60/60);
        }
        main.savePlayerConfig(config);
    }

    public void allPlayerScores(CommandSourceStack stack, Boolean showInChat) {
        List<ScoreData> scores = new ArrayList<>();
        Map<String, Double> teamScore = new HashMap<>();
        for (OfflinePlayer offlinePlayer : Bukkit.getWhitelistedPlayers()) {
            if (offlinePlayer.getStatistic(Statistic.PLAY_ONE_MINUTE) != 0) {
                ScoreData score = main.scoreData.get(offlinePlayer.getUniqueId());;
                scores.add(score);
                if (score.getTeam() != null) {
                    if (!teamScore.containsKey(score.getTeam().getTeamName()))
                        teamScore.put(score.getTeam().getTeamName(), score.totalPoints());
                    else
                        teamScore.replace(score.getTeam().getTeamName(), teamScore.getOrDefault(score.getTeam().getTeamName(), 0.00) + score.totalPoints());
                }
            }
        }
        scores.sort((o1, o2) -> {
            var a = o1.totalPoints() * 100;
            var b = o2.totalPoints() * 100;
            return (int) b - (int) a;
        });

        Map<String, Double> sortedTeamScore = teamScore.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        if (showInChat) {
            for(ScoreData scoreData : scores) {
                showPlayerScore(stack,scoreData);
            }
        }
        csvPlayerScore(scores);
        logPlayerScore(scores, sortedTeamScore);
    }

    public void singlePlayerScores(CommandSourceStack stack, @NotNull Player player) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
        ScoreData scoreData = main.scoreData.get(player.getUniqueId());
        if (scoreData == null)
            scoreData = initializePlayerScore(player);
        showPlayerScore(stack,scoreData);
    }

    public void csvPlayerScore(@NotNull List<ScoreData> scores) {
        String[] headers = new String[]{"Player", "Team", "Total", "Deaths", "DeathPoints", "Xp", "XpPoints", "OnlineHr", "SurvivalHr", "AdventureHr", "AfkHr",
                "OnlineHrPoints", "Total Mob Kill Points", "Blocks Mined", "Blocks Mined Points", "Blocks Placed", "Blocks Placed Points", "Items Harvested",
                "Items Harvested Points", "Items Caught", "Items Caught Points", "Achievements", "Achievement Points"};
        List<String[]> data = new ArrayList<>();
        for (ScoreData scoreData : scores) {
            data.add(new String[]{scoreData.getPlayer().getName(), scoreData.getTeam().getTeamName(), String.valueOf(scoreData.totalPoints()), String.valueOf(scoreData.getDeaths()), String.valueOf(scoreData.getDeathPoints()),
                    String.valueOf(scoreData.getXp()), String.valueOf(scoreData.getXpPoints()), String.valueOf(scoreData.onlineHr()), String.valueOf(scoreData.getOnlineHr().get("SURVIVAL")), String.valueOf(scoreData.getOnlineHr().get("ADVENTURE")),
                    String.valueOf(scoreData.getOnlineHr().get("AFK")), String.valueOf(scoreData.getOnlineHrPoints()), String.valueOf(scoreData.getTypeMobTotalPoints()), String.valueOf(scoreData.getBlocksMined()),
                    String.valueOf(scoreData.getDefaultBlocksMinedPoints()), String.valueOf(scoreData.getBlocksPlaced()), String.valueOf(scoreData.getDefaultBlocksPlacedPoints()), String.valueOf(scoreData.getHarvested()),
                    String.valueOf(scoreData.getDefaultHarvestedPoints()), String.valueOf(scoreData.getCaught()), String.valueOf(scoreData.getDefaultCaughtPoints()), String.valueOf(scoreData.getAchievements()), String.valueOf(scoreData.getDefaultAchievementPoints())});
        }
        main.exportToCsv("Score", data, headers);
    }

    public void logPlayerScore(@NotNull List<ScoreData> scores, @NotNull Map<String, Double> teamScores) {
        String path = "Score";
        main.logToFile("--- Team Scores ---", path);
        main.logToFile("", path);
        teamScores.forEach((key, value) -> {
            main.logToFile(key + "'s Score: " + value, path);
            main.logToFile("--- Member Ranking ---", path);
            scores.stream().filter(score -> Objects.equals(score.getTeam().getTeamName(), key)).forEach(score -> main.logToFile(score.getPlayer().getName() +"'s Score: " + score.totalPoints(), path));
            main.logToFile("--- End Members ---", path);
            main.logToFile("",path);
        });
        main.logToFile("",path);
        main.logToFile("--- Individualised Ranking ---", path);
        for(ScoreData scoreData : scores) {
            main.logToFile(scoreData.getPlayer().getName() + " - Team: " + scoreData.getTeam().getTeamName() + ":", path);
            main.logToFile("Total Points: " + scoreData.totalPoints(), path);
            main.logToFile("--- Category Totals ---", path);
            main.logToFile("Deaths: " + scoreData.getDeaths().get(GameMode.SURVIVAL) + ", Adventure Deaths: " + scoreData.getDeaths().get(GameMode.ADVENTURE), path);
            main.logToFile("Death Points: " + scoreData.getDeathPoints(), path);
            main.logToFile("AFK Penalty: " + scoreData.getAFKPointsOffset(), path);
            main.logToFile("Xp: " + scoreData.getXp() + ", Points: " + scoreData.getXpPoints(), path);
            main.logToFile("OnlineHr: " + scoreData.onlineHr() + ", Points: " + scoreData.getOnlineHrPoints(), path);
            main.logToFile("SurvivalHr: " + scoreData.getOnlineHr().get("SURVIVIAL") + ", AdventureHr: " + scoreData.getOnlineHr().get("ADVENTURE") + ", AfkHr: " + scoreData.getOnlineHr().get("AFK"), path);
            main.logToFile("Total Mob Kill Points: " + scoreData.getTypeMobTotalPoints(), path);
            main.logToFile("Blocks Mined: " + scoreData.getBlocksMined() + ", Points: " + scoreData.getDefaultBlocksMinedPoints(), path);
            main.logToFile("Blocks Placed: " + scoreData.getBlocksPlaced() + ", Points: " + scoreData.getDefaultBlocksPlacedPoints(), path);
            main.logToFile("Items Harvested: " + scoreData.getHarvested() + ", Points: " + scoreData.getDefaultHarvestedPoints(), path);
            main.logToFile("Items Caught: " + scoreData.getCaught() + ", Points: " + scoreData.getDefaultCaughtPoints(), path);
            main.logToFile("Achievements: " + scoreData.getAchievements() + ", Points: " + scoreData.getDefaultAchievementPoints(), path);
            main.logToFile("", path);
            main.logToFile("Mob Killed Breakdown", path);
            if (!scoreData.getTypeMobs().isEmpty()) {
                scoreData.getTypeMobs().keySet().forEach(key -> main.logToFile(key.name() + "s Killed: " + scoreData.getTypeMobs().get(key).getTotalCount() + " , Points: " + scoreData.getTypeMobs().get(key).getPoints(), path));
            }
            main.logToFile("", path);
            main.logToFile("Blocks Mined Breakdown", path);
            if (!scoreData.getTypeBlocksMined().isEmpty()) {
                scoreData.getTypeBlocksMined().keySet().forEach(key -> main.logToFile(key.name() + " Mined: " + scoreData.getTypeBlocksMined().get(key).getTotalCount() + " , Points: " + scoreData.getTypeBlocksMined().get(key).getPoints(), path));
            }
            main.logToFile("", path);
            main.logToFile("Blocks Placed Breakdown", path);
            if (!scoreData.getTypeBlocksPlaced().isEmpty()) {
                scoreData.getTypeBlocksPlaced().keySet().forEach(key -> main.logToFile(key.name() + " Placed: " + scoreData.getTypeBlocksPlaced().get(key).getTotalCount() + " , Points: " + scoreData.getTypeBlocksPlaced().get(key).getPoints(), path));
            }
            main.logToFile("", path);
            main.logToFile("Items Harvested Breakdown", path);
            if (!scoreData.getTypeItemsHarvested().isEmpty()) {
                scoreData.getTypeItemsHarvested().keySet().forEach(key -> main.logToFile(key + " Havested: " + scoreData.getTypeItemsHarvested().get(key).getTotalCount() + " , Points: " + scoreData.getTypeItemsHarvested().get(key).getPoints(), path));
            }
            main.logToFile("", path);
            main.logToFile("------- END -----", path);
            main.logToFile("", path);
        }
    }

    public void showPlayerScore(@NotNull CommandSourceStack stack, @NotNull ScoreData scoreData) {
        String Team = "N/A";
        if (scoreData.getTeam() != null)
            Team = scoreData.getTeam().getTeamName();
        stack.getSender().sendMessage(scoreData.getPlayer().getName() + " - Team: " + Team + ":");
        stack.getSender().sendMessage("Total Points: " + scoreData.totalPoints());
        stack.getSender().sendMessage("--- Category Totals ---");
        stack.getSender().sendMessage("Deaths: " + scoreData.getDeaths().get(GameMode.SURVIVAL) + ", Adventure Deaths: " + scoreData.getDeaths().get(GameMode.ADVENTURE));
        stack.getSender().sendMessage("Death Points: " + scoreData.getDeathPoints());
        stack.getSender().sendMessage("AFK Penalty: " + scoreData.getAFKPointsOffset());
        stack.getSender().sendMessage("Xp: " + scoreData.getXp() + ", Points: " + scoreData.getXpPoints());
        stack.getSender().sendMessage("OnlineHr: " + scoreData.onlineHr() + ", Points: " + scoreData.getOnlineHrPoints());
        stack.getSender().sendMessage("SurvivalHr: " + scoreData.getOnlineHr().get("SURVIVIAL") + ", AdventureHr: " + scoreData.getOnlineHr().get("ADVENTURE") + ", AfkHr: " + scoreData.getOnlineHr().get("AFK"));
        stack.getSender().sendMessage("Total Mob Kill Points: " + scoreData.getTypeMobTotalPoints());
        stack.getSender().sendMessage("Blocks Mined: " + scoreData.getBlocksMined() + ", Points: " + scoreData.getDefaultBlocksMinedPoints());
        stack.getSender().sendMessage("Blocks Placed: " + scoreData.getBlocksPlaced() + ", Points: " + scoreData.getDefaultBlocksPlacedPoints());
        stack.getSender().sendMessage("Items Harvested: " + scoreData.getHarvested() + ", Points: " + scoreData.getDefaultHarvestedPoints());
        stack.getSender().sendMessage("Items Caught: " + scoreData.getCaught() + ", Points: " + scoreData.getDefaultCaughtPoints());
        stack.getSender().sendMessage("Achievements: " + scoreData.getAchievements() + ", Points: " + scoreData.getDefaultAchievementPoints());
        stack.getSender().sendMessage("Use /OneLife Scores All for a Detailed Breakdown");
        stack.getSender().sendMessage("------- END -----");
    }

    public Team getTeam(@NotNull OfflinePlayer player) {
        List<String> teams = main.getConfig().getStringList("Team");
        User user;
        try {
            user = main.lpAPI.getUserManager().loadUser(player.getUniqueId()).get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        String pg;
        if (user == null)
            pg = "default";
        else
            pg = user.getPrimaryGroup();
        if (pg.isEmpty())
            pg = "default";
        Group group = main.lpAPI.getGroupManager().getGroup(pg);
        assert group != null;
        for (String team : teams) {
            if (Objects.requireNonNull(group.getName()).equalsIgnoreCase(team)) {
                return main.settings.getTeams().get(Objects.requireNonNullElse(group.getDisplayName(), group.getName()));
            }
        }
        return null;
    }

    public boolean playerAFK(@NotNull OfflinePlayer player) {
        User user = main.lpAPI.getUserManager().getUser(player.getUniqueId());
        assert user != null;
        return user.getPrimaryGroup().equalsIgnoreCase("AFK");
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
