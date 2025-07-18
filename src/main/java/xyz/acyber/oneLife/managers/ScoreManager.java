package xyz.acyber.oneLife.managers;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
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
import xyz.acyber.oneLife.Main;
import xyz.acyber.oneLife.ScoreData;

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

    public void playerFish(@NotNull PlayerFishEvent event) {
        //Update Players Fished Placed
        FileConfiguration config = main.getPlayerConfig();
        Player player = event.getPlayer();
        ConfigurationSection scoring = main.getConfig().getConfigurationSection("Scoring");
        if (event.getCaught() != null) {
            if (playerAFK(player)) {
                assert scoring != null;
                config.set(player.getUniqueId() + ".afkPointsOffset", config.getDouble(player.getUniqueId() + ".afkPointsOffset") - (1 * scoring.getDouble("CaughtTotal")));
                config.set(player.getUniqueId() + ".afkPointsOffset", config.getDouble(player.getUniqueId() + ".afkPointsOffset") - (1 * scoring.getDouble("playerCaught." + event.getCaught().getType().name())));
            }
            else {
                //Update Total Fished Count
                config.set(player.getUniqueId() + ".playerCaughtTotal", config.getInt(player.getUniqueId() + ".playerCaughtTotal") + 1);
                //Update Specific Fish Caught
                config.set(player.getUniqueId() + ".playerCaught." + event.getCaught().getType().name(), config.getInt(player.getUniqueId() + ".playerCaught." + event.getCaught().getType().name()) + 1);
            }
            main.savePlayerConfig(config);
        }
    }

    public void playerHarvest(@NotNull PlayerHarvestBlockEvent event) {
        //Update Players Harvest
        FileConfiguration config = main.getPlayerConfig();
        Player player = event.getPlayer();
        ConfigurationSection scoring = main.getConfig().getConfigurationSection("Scoring");
        List<ItemStack> harvested = event.getItemsHarvested();

        if (playerAFK(player)) {
            assert scoring != null;
            config.set(player.getUniqueId() + ".afkPointsOffset", config.getDouble(player.getUniqueId() + ".afkPointsOffset") - (1 * scoring.getDouble("HarvestedTotal")));
            for (ItemStack item : harvested) {
                config.set(player.getUniqueId() + ".afkPointsOffset", config.getDouble(player.getUniqueId() + ".afkPointsOffset") - (1 * scoring.getDouble("playerHarvested." + item.getType().name())));
            }

        }
        else {
            //Update Total Harvested Count
            config.set(player.getUniqueId() + ".playerHarvestedTotal", config.getInt(player.getUniqueId() + ".playerHarvestedTotal") + 1);
            //Update Specific Harvested Count
            for (ItemStack item : harvested) {
                config.set(player.getUniqueId() + ".playerHarvested." + item.getType().name(), config.getInt(player.getUniqueId() + ".playerHarvested." + item.getType().name()) + 1);
            }
        }

        main.savePlayerConfig(config);
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
        FileConfiguration config = main.getPlayerConfig();
        Player player = event.getEntity();
        if (player.getGameMode().equals(GameMode.ADVENTURE)) {
            config.set(player.getUniqueId() + ".adventureDeaths", config.getDouble(player.getUniqueId() + ".adventureDeaths") + 1);
        } else {
            config.set(player.getUniqueId() + ".deaths", config.getDouble(player.getUniqueId() + ".deaths") + 1);
        }
        main.savePlayerConfig(config);
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
                ScoreData score = playerScore(offlinePlayer);
                scores.add(score);
                if (score.team != null) {
                    if (!teamScore.containsKey(score.team))
                        teamScore.put(score.team, score.totalPoints());
                    else
                        teamScore.replace(score.team, teamScore.getOrDefault(score.team, 0.00) + score.totalPoints());
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
        ScoreData scoreData = playerScore(offlinePlayer);
        showPlayerScore(stack,scoreData);
    }

    public ScoreData playerScore(@NotNull OfflinePlayer player) {
        ConfigurationSection scoring = main.getConfig().getConfigurationSection("Scoring");
        ConfigurationSection playerScore = main.getPlayerConfig().getConfigurationSection(player.getUniqueId().toString());
        assert scoring != null;
        assert playerScore != null;

        ScoreData scoreData = new ScoreData();
        scoreData.player = player;
        scoreData.team = getTeam(player);
        scoreData.deaths = playerScore.getDouble("deaths");
        scoreData.adventureDeaths = playerScore.getDouble("adventureDeaths");
        scoreData.xp = round(playerScore.getDouble("playerTotalXp"),2);
        scoreData.survivalHr = round(playerScore.getDouble("survivalHr"),2);
        scoreData.adventureHr = round(playerScore.getDouble("adventureHr"),2);
        scoreData.afkHr = round(playerScore.getDouble("afkHr"),2);
        scoreData.blocksPlaced = playerScore.getDouble("playerBlocksPlaced");
        scoreData.blocksMined = playerScore.getDouble("playerBlocksMined");
        scoreData.harvested = playerScore.getDouble("playerHarvestedTotal");
        scoreData.caught = playerScore.getDouble("playerCaughtTotal");
        scoreData.achievements = playerScore.getDouble("playerAdvancements");

        scoreData.afkPointsOffset = playerScore.getDouble("afkPointsOffset") - (scoreData.afkHr * scoring.getDouble("OnlineHr"));

        scoreData.deathPoints = scoreData.deaths * scoring.getDouble("Death");
        scoreData.adventureDeathPoints = scoreData.adventureDeaths * scoring.getDouble("AdventureDeath");
        scoreData.xpPoints = round(scoreData.xp * scoring.getDouble("Xp"),5);
        scoreData.onlineHrPoints = round((scoreData.survivalHr * scoring.getDouble("OnlineHr")) + (scoreData.adventureHr * scoring.getDouble("OnlineHr") * scoring.getDouble("adventureMultiplier")),2);
        scoreData.blocksPlacedPoints = scoreData.blocksPlaced * scoring.getDouble("BlocksPlaced");
        scoreData.blocksMinedPoints = scoreData.blocksMined * scoring.getDouble("BlocksMined");
        scoreData.harvestedPoints = scoreData.harvested * scoring.getDouble("HarvestedTotal");
        scoreData.caughtPoints = scoreData.caught * scoring.getDouble("CaughtTotal");
        scoreData.achievementPoints = round(scoreData.achievements * scoring.getDouble("Achevements"),2);
        scoreData.typeMobs = new Hashtable<>();
        scoreData.typeItemsHarvested = new Hashtable<>();
        scoreData.typeItemsCaught = new Hashtable<>();
        scoreData.typeBlocksPlaced = new Hashtable<>();
        scoreData.typeBlocksMined = new Hashtable<>();
        if (playerScore.isConfigurationSection("MobKills")) {
            for (String key : Objects.requireNonNull(playerScore.getConfigurationSection("MobKills")).getKeys(false)) {
                List<Double> data = new ArrayList<>();

                double multiplier = scoring.getDouble("MobKills." + key);
                double mined = playerScore.getDouble("MobKills." + key);
                double minedPoints = mined * multiplier;

                data.add(mined);
                data.add(minedPoints);

                scoreData.typeMobTotalPoints = scoreData.typeMobTotalPoints + minedPoints;
                scoreData.typeMobs.put(key, data);
            }
        }
        if (playerScore.isConfigurationSection("playerMined")) {
            for (String key : Objects.requireNonNull(playerScore.getConfigurationSection("playerMined")).getKeys(false)) {
                List<Double> data = new ArrayList<>();

                double multiplier = scoring.getDouble("playerMined." + key);
                double mined = playerScore.getDouble("playerMined." + key);
                double minedPoints = mined * multiplier;

                data.add(mined);
                data.add(minedPoints);

                scoreData.typeBlocksMinedTotalPoints = scoreData.typeBlocksMinedTotalPoints + minedPoints;
                scoreData.typeBlocksMined.put(key, data);
            }
        }
        if (playerScore.isConfigurationSection("playerPlaced")) {
            for (String key : Objects.requireNonNull(playerScore.getConfigurationSection("playerPlaced")).getKeys(false)) {
                List<Double> data = new ArrayList<>();
                double multiplier = scoring.getDouble("playerPlaced." + key);
                double placed = playerScore.getDouble("playerPlaced." + key);
                double placedPoints = placed * multiplier;
                data.add(placed);
                data.add(placedPoints);
                scoreData.typeBlocksPlacedTotalPoints = scoreData.typeBlocksPlacedTotalPoints + placedPoints;
                scoreData.typeBlocksPlaced.put(key, data);
            }
        }
        if (playerScore.isConfigurationSection("playerHarvested")) {
            for (String key : Objects.requireNonNull(playerScore.getConfigurationSection("playerHarvested")).getKeys(false)) {
                List<Double> data = new ArrayList<>();

                double multiplier = scoring.getDouble("playerHarvested." + key);
                double harvested = playerScore.getDouble("playerHarvested." + key);
                double harvestedPoints = harvested * multiplier;

                data.add(harvested);
                data.add(harvestedPoints);

                scoreData.typeItemsHarvestedTotalPoints = scoreData.typeItemsHarvestedTotalPoints + harvestedPoints;
                scoreData.typeItemsHarvested.put(key, data);
            }

        }
        if (playerScore.isConfigurationSection("playerCaught")) {
            for (String key : Objects.requireNonNull(playerScore.getConfigurationSection("playerCaught")).getKeys(false)) {
                List<Double> data = new ArrayList<>();

                double multiplier = scoring.getDouble("playerCaught." + key);
                double caught = playerScore.getDouble("playerCaught." + key);
                double caughtPoints = caught * multiplier;

                data.add(caught);
                data.add(caughtPoints);

                scoreData.typeItemsCaughtTotalPoints = scoreData.typeItemsCaughtTotalPoints + caughtPoints;
                scoreData.typeItemsCaught.put(key, data);
            }
        }
        return scoreData;
    }

    public void csvPlayerScore(@NotNull List<ScoreData> scores) {
        String[] headers = new String[]{"Player", "Team", "Total", "Deaths", "DeathPoints", "Xp", "XpPoints", "OnlineHr", "SurvivalHr", "AdventureHr", "AfkHr",
                "OnlineHrPoints", "Total Mob Kill Points", "Blocks Mined", "Blocks Mined Points", "Blocks Placed", "Blocks Placed Points", "Items Harvested",
                "Items Harvested Points", "Items Caught", "Items Caught Points", "Achievements", "Achievement Points"};
        List<String[]> data = new ArrayList<>();
        for (ScoreData scoreData : scores) {
            data.add(new String[]{scoreData.player.getName(), scoreData.team, String.valueOf(scoreData.totalPoints()), String.valueOf(scoreData.deaths), String.valueOf(scoreData.deathPoints),
                    String.valueOf(scoreData.xp), String.valueOf(scoreData.xpPoints), String.valueOf(scoreData.onlineHr()), String.valueOf(scoreData.survivalHr), String.valueOf(scoreData.adventureHr),
                    String.valueOf(scoreData.afkHr), String.valueOf(scoreData.onlineHrPoints), String.valueOf(scoreData.typeMobTotalPoints), String.valueOf(scoreData.blocksMined),
                    String.valueOf(scoreData.blocksMinedPoints), String.valueOf(scoreData.blocksPlaced), String.valueOf(scoreData.blocksPlacedPoints), String.valueOf(scoreData.harvested),
                    String.valueOf(scoreData.harvestedPoints), String.valueOf(scoreData.caught), String.valueOf(scoreData.caughtPoints), String.valueOf(scoreData.achievements), String.valueOf(scoreData.achievementPoints)});
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
            scores.stream().filter(score -> Objects.equals(score.team, key)).forEach(score -> main.logToFile(score.player.getName() +"'s Score: " + score.totalPoints(), path));
            main.logToFile("--- End Members ---", path);
            main.logToFile("",path);
        });
        main.logToFile("",path);
        main.logToFile("--- Individualised Ranking ---", path);
        for(ScoreData scoreData : scores) {
            main.logToFile(scoreData.player.getName() + " - Team: " + scoreData.team + ":", path);
            main.logToFile("Total Points: " + scoreData.totalPoints(), path);
            main.logToFile("--- Category Totals ---", path);
            main.logToFile("Deaths: " + scoreData.deaths + ", Points: " + scoreData.deathPoints, path);
            main.logToFile("Adventure Deaths: " + scoreData.adventureDeaths + ", Points: " + scoreData.adventureDeathPoints, path);
            main.logToFile("AFK Penalty: " + scoreData.afkPointsOffset, path);
            main.logToFile("Xp: " + scoreData.xp + ", Points: " + scoreData.xpPoints, path);
            main.logToFile("OnlineHr: " + scoreData.onlineHr() + ", Points: " + scoreData.onlineHrPoints, path);
            main.logToFile("SurvivalHr: " + scoreData.survivalHr + ", AdventureHr: " + scoreData.adventureHr + ", AfkHr: " + scoreData.afkHr, path);
            main.logToFile("Total Mob Kill Points: " + scoreData.typeMobTotalPoints, path);
            main.logToFile("Blocks Mined: " + scoreData.blocksMined + ", Points: " + scoreData.blocksMinedPoints, path);
            main.logToFile("Blocks Placed: " + scoreData.blocksPlaced + ", Points: " + scoreData.blocksPlacedPoints, path);
            main.logToFile("Items Harvested: " + scoreData.harvested + ", Points: " + scoreData.harvestedPoints, path);
            main.logToFile("Items Caught: " + scoreData.caught + ", Points: " + scoreData.caughtPoints, path);
            main.logToFile("Achievements: " + scoreData.achievements + ", Points: " + scoreData.achievementPoints, path);
            main.logToFile("", path);
            main.logToFile("Mobs Killed Breakdown", path);
            if (!scoreData.typeMobs.isEmpty()) {
                scoreData.typeMobs.keys().asIterator().forEachRemaining(key -> main.logToFile(key + "s Killed: " + scoreData.typeMobs.get(key).getFirst() + " , Points: " + scoreData.typeMobs.get(key).getLast(), path));
            }
            main.logToFile("", path);
            main.logToFile("Blocks Mined Breakdown", path);
            if (!scoreData.typeBlocksMined.isEmpty()) {
                scoreData.typeBlocksMined.keys().asIterator().forEachRemaining(key -> main.logToFile(key + " Mined: " + scoreData.typeBlocksMined.get(key).getFirst() + " , Points: " + scoreData.typeBlocksMined.get(key).getLast(), path));
            }
            main.logToFile("", path);
            main.logToFile("Blocks Placed Breakdown", path);
            if (!scoreData.typeBlocksPlaced.isEmpty()) {
                scoreData.typeBlocksPlaced.keys().asIterator().forEachRemaining(key -> main.logToFile(key + " Placed: " + scoreData.typeBlocksPlaced.get(key).getFirst() + " , Points: " + scoreData.typeBlocksPlaced.get(key).getLast(), path));
            }
            main.logToFile("", path);
            main.logToFile("Items Harvested Breakdown", path);
            if (!scoreData.typeItemsHarvested.isEmpty()) {
                scoreData.typeItemsHarvested.keys().asIterator().forEachRemaining(key -> main.logToFile(key + " Havested: " + scoreData.typeItemsHarvested.get(key).getFirst() + " , Points: " + scoreData.typeItemsHarvested.get(key).getLast(), path));
            }
            main.logToFile("", path);
            main.logToFile("Items Caught Breakdown", path);
            if (!scoreData.typeItemsCaught.isEmpty()) {
                scoreData.typeItemsCaught.keys().asIterator().forEachRemaining(key -> main.logToFile(key + " Caught: " + scoreData.typeItemsCaught.get(key).getFirst() + " , Points: " + scoreData.typeItemsCaught.get(key).getLast(), path));
            }
            main.logToFile("------- END -----", path);
            main.logToFile("", path);
        }
    }

    public void showPlayerScore(@NotNull CommandSourceStack stack, @NotNull ScoreData scoreData) {
        stack.getSender().sendMessage(scoreData.player.getName() + " - Team: " + scoreData.team + ":");
        stack.getSender().sendMessage("Total Points: " + scoreData.totalPoints());
        stack.getSender().sendMessage("------- Category Totals -------");
        stack.getSender().sendMessage("Deaths: " + scoreData.deaths + ", Points: " + scoreData.deathPoints);
        stack.getSender().sendMessage("Adventure Deaths: " + scoreData.adventureDeaths + ", Points: " + scoreData.adventureDeathPoints);
        stack.getSender().sendMessage("AFK Penalty: " + scoreData.afkPointsOffset);
        stack.getSender().sendMessage("Xp: " + scoreData.xp + ", Points: " + scoreData.xpPoints);
        stack.getSender().sendMessage("OnlineHr: " + scoreData.onlineHr() + ", Points: " + scoreData.onlineHrPoints);
        stack.getSender().sendMessage("SurvivalHr: " + scoreData.survivalHr + ", AdventureHr: " + scoreData.adventureHr + ", AfkHr: " + scoreData.afkHr);
        stack.getSender().sendMessage("Total Mob Kill Points: " + scoreData.typeMobTotalPoints);
        stack.getSender().sendMessage("Blocks Mined: " + scoreData.blocksMined + ", Points: " + scoreData.blocksMinedPoints);
        stack.getSender().sendMessage("Blocks Placed: " + scoreData.blocksPlaced + ", Points: " + scoreData.blocksPlacedPoints);
        stack.getSender().sendMessage("Items Harvested: " + scoreData.harvested + ", Points: " + scoreData.harvestedPoints);
        stack.getSender().sendMessage("Items Caught: " + scoreData.caught + ", Points: " + scoreData.caughtPoints);
        stack.getSender().sendMessage("Achievements: " + scoreData.achievements + ", Points: " + scoreData.achievementPoints);
        stack.getSender().sendMessage("Use /OneLife Scores All for a Detailed Breakdown");
        stack.getSender().sendMessage("------- END -----");
    }

    public String getTeam(@NotNull OfflinePlayer player) {
        List<String> teams = main.getConfig().getStringList("Teams");
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
            if (Objects.requireNonNull(group.getName()).equalsIgnoreCase(team))
                return Objects.requireNonNullElse(group.getDisplayName(), group.getName());
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
