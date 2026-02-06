package xyz.acyber.oneLife.Managers;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.luckperms.api.model.user.User;
import org.bukkit.*;
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
import xyz.acyber.oneLife.DataObjects.PlayerScore;
import xyz.acyber.oneLife.OneLifePlugin;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class ScoreManager {

    private final OneLifePlugin OLP;

    public ScoreManager(OneLifePlugin plugin) {
        OLP = plugin;
    }

    public void playerFish(@NotNull PlayerFishEvent event) {
        //Update Players Caught Items
        //TODO Look into ability to record the number of specific items caught not just total number of items caught
        Player player = event.getPlayer();
        String gameMode = player.getGameMode().name();
        if (playerAFK(player)) gameMode = "AFK";
        if (event.getCaught() != null) OLP.scoreData.get(player.getUniqueId()).incrementCaughtItems(gameMode);
    }

    public void playerHarvest(@NotNull PlayerHarvestBlockEvent event) {
        //Update Players Items Harvested
        Player player = event.getPlayer();
        String gameMode = player.getGameMode().name();
        List<ItemStack> harvest = event.getItemsHarvested();

        if (playerAFK(player)) gameMode = "AFK";

        for (ItemStack item : harvest) {
            Material material = item.getType();
            OLP.scoreData.get(player.getUniqueId()).incrementTypeItemsHarvested(material,gameMode);
            OLP.scoreData.get(player.getUniqueId()).incrementHarvestedItems(gameMode);
        }
    }

    public void blocksPlaced(@NotNull BlockPlaceEvent event) {
        //Update Players Blocks Placed
        Player player = event.getPlayer();
        String gameMode = player.getGameMode().name();
        if (playerAFK(player)) gameMode = "AFK";
        //Update Total Blocks Placed Count
        OLP.scoreData.get(player.getUniqueId()).incrementBlocksPlaced(gameMode);
        OLP.scoreData.get(player.getUniqueId()).incrementTypeBlocksPlaced(event.getBlock().getType(),gameMode);
    }

    public void blocksMined(@NotNull BlockBreakEvent event) {
        //Update Players Blocks Mined
        Player player = event.getPlayer();
        String gameMode = player.getGameMode().name();
        if (playerAFK(player)) gameMode = "AFK";
        OLP.scoreData.get(player.getUniqueId()).incrementBlocksMined(gameMode);
        OLP.scoreData.get(player.getUniqueId()).incrementTypeBlocksMined(event.getBlock().getType(),gameMode);
    }

    public void playerAdvanced(@NotNull PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();
        event.getAdvancement().getDisplay();
        String gameMode = player.getGameMode().name();
        if (playerAFK(player)) gameMode = "AFK";
        OLP.scoreData.get(player.getUniqueId()).incrementAchievements(gameMode);
    }

    public void playerDeath(@NotNull PlayerDeathEvent event) {
        Player player = event.getEntity();
        String gameMode = player.getGameMode().name();
        if (playerAFK(player)) gameMode = "AFK";
        OLP.scoreData.get(player.getUniqueId()).incrementDeaths(gameMode);
    }

    public void entityKilled(@NotNull EntityDeathEvent event) {
        if (event.getDamageSource().getCausingEntity() != null && event.getDamageSource().getCausingEntity().getType() == EntityType.PLAYER) {
            Player player = (Player) event.getDamageSource().getCausingEntity();
            String gameMode = player.getGameMode().name();
            if (playerAFK(player)) gameMode = "AFK";
            OLP.scoreData.get(player.getUniqueId()).incrementTypeMobsKilled(event.getEntityType(),gameMode);
        }
    }

    public void timeOnline(@NotNull Player player) {
        String gameMode = player.getGameMode().name();
        if (playerAFK(player)) gameMode = "AFK";
        OLP.scoreData.get(player.getUniqueId()).incrementOnlineHr(gameMode,"Sec");
    }

    public void allPlayerScores(CommandSourceStack stack, Boolean showInChat) {
        List<PlayerScore> scores = new ArrayList<>();
        Map<String, Double> teamScore = new HashMap<>();
        for (PlayerScore score : OLP.scoreData.values()) {
            scores.add(score);
            if (score.getTeam() != null) {
                // Adds team score if needed; updates existing score
                if (!teamScore.containsKey(score.getTeam().getTeamName()))
                    teamScore.put(score.getTeam().getTeamName(), score.totalPoints());
                else
                    teamScore.replace(score.getTeam().getTeamName(), teamScore.getOrDefault(score.getTeam().getTeamName(), 0.00) + score.totalPoints());
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
            for(PlayerScore playerScore : scores) {
                showPlayerScore(stack, playerScore);
            }
        }
        csvPlayerScore(scores);
        logPlayerScore(scores, sortedTeamScore);
    }

    public void singlePlayerScores(CommandSourceStack stack, @NotNull Player player) {
        PlayerScore playerScore = OLP.scoreData.get(player.getUniqueId());
        showPlayerScore(stack, playerScore);
    }

    public void csvPlayerScore(@NotNull List<PlayerScore> scores) {
        String[] headers = new String[]{"Player", "Team", "Total", "Deaths", "DeathPoints", "Xp", "XpPoints", "OnlineHr", "SurvivalHr", "AdventureHr", "AfkHr",
                "OnlineHrPoints", "Total MobConfig Kill Points", "Blocks Mined", "Blocks Mined Points", "Blocks Placed", "Blocks Placed Points", "Items Harvested",
                "Items Harvested Points", "Items Caught", "Items Caught Points", "Achievements", "Achievement Points"};
        List<String[]> data = new ArrayList<>();
        for (PlayerScore playerScore : scores) {
            data.add(new String[]{playerScore.getPlayerName(), playerScore.getTeam().getTeamName(), String.valueOf(playerScore.totalPoints()), String.valueOf(playerScore.getDeaths()), String.valueOf(playerScore.getDeathPoints()),
                    String.valueOf(playerScore.getXp()), String.valueOf(playerScore.getXpPoints()), String.valueOf(playerScore.onlineHr()), String.valueOf(playerScore.getOnlineHr().get("SURVIVAL")), String.valueOf(playerScore.getOnlineHr().get("ADVENTURE")),
                    String.valueOf(playerScore.getOnlineHr().get("AFK")), String.valueOf(playerScore.getOnlineHrPoints()), String.valueOf(playerScore.getTypeMobTotalPoints()), String.valueOf(playerScore.getBlocksMined()),
                    String.valueOf(playerScore.getDefaultBlocksMinedPoints()), String.valueOf(playerScore.getBlocksPlaced()), String.valueOf(playerScore.getDefaultBlocksPlacedPoints()), String.valueOf(playerScore.getHarvested()),
                    String.valueOf(playerScore.getDefaultHarvestedPoints()), String.valueOf(playerScore.getCaught()), String.valueOf(playerScore.getDefaultCaughtPoints()), String.valueOf(playerScore.getAchievements()), String.valueOf(playerScore.getDefaultAchievementPoints())});
        }
        OLP.exportToCsv("Score", data, headers);
    }

    public void logPlayerScore(@NotNull List<PlayerScore> scores, @NotNull Map<String, Double> teamScores) {
        String path = "Score";
        OLP.logToFile("--- Team Scores ---", path);
        OLP.logToFile("", path);
        teamScores.forEach((key, value) -> {
            OLP.logToFile(key + "'s Score: " + value, path);
            OLP.logToFile("--- Member Ranking ---", path);
            scores.stream().filter(score -> Objects.equals(score.getTeam().getTeamName(), key)).forEach(score -> OLP.logToFile(score.getPlayerName() +"'s Score: " + score.totalPoints(), path));
            OLP.logToFile("--- End Members ---", path);
            OLP.logToFile("",path);
        });
        OLP.logToFile("",path);
        OLP.logToFile("--- Individualised Ranking ---", path);
        for(PlayerScore playerScore : scores) {
            OLP.logToFile(playerScore.getPlayerName() + " - Team: " + playerScore.getTeam().getTeamName() + ":", path);
            OLP.logToFile("Total Points: " + playerScore.totalPoints(), path);
            OLP.logToFile("--- Category Totals ---", path);
            OLP.logToFile("Deaths: " + playerScore.getDeaths().get(GameMode.SURVIVAL.name()) + ", Adventure Deaths: " + playerScore.getDeaths().get(GameMode.ADVENTURE.name()), path);
            OLP.logToFile("Death Points: " + playerScore.getDeathPoints(), path);
            OLP.logToFile("AFK Penalty: " + playerScore.getAFKPointsOffset(), path);
            OLP.logToFile("Xp: " + playerScore.getXp() + ", Points: " + playerScore.getXpPoints(), path);
            OLP.logToFile("OnlineHr: " + playerScore.onlineHr() + ", Points: " + playerScore.getOnlineHrPoints(), path);
            OLP.logToFile("SurvivalHr: " + playerScore.getOnlineHr().get("SURVIVIAL") + ", AdventureHr: " + playerScore.getOnlineHr().get("ADVENTURE") + ", AfkHr: " + playerScore.getOnlineHr().get("AFK"), path);
            OLP.logToFile("Total MobConfig Kill Points: " + playerScore.getTypeMobTotalPoints(), path);
            OLP.logToFile("Blocks Mined: " + playerScore.getBlocksMined() + ", Points: " + playerScore.getDefaultBlocksMinedPoints(), path);
            OLP.logToFile("Blocks Placed: " + playerScore.getBlocksPlaced() + ", Points: " + playerScore.getDefaultBlocksPlacedPoints(), path);
            OLP.logToFile("Items Harvested: " + playerScore.getHarvested() + ", Points: " + playerScore.getDefaultHarvestedPoints(), path);
            OLP.logToFile("Items Caught: " + playerScore.getCaught() + ", Points: " + playerScore.getDefaultCaughtPoints(), path);
            OLP.logToFile("Achievements: " + playerScore.getAchievements() + ", Points: " + playerScore.getDefaultAchievementPoints(), path);
            OLP.logToFile("", path);
            OLP.logToFile("MobConfig Killed Breakdown", path);
            if (!playerScore.getTypeMobsKilled().isEmpty()) {
                playerScore.getTypeMobsKilled().keySet().forEach(key -> OLP.logToFile(key + "s Killed: " + playerScore.getTypeMobsKilled().get(key).getTotalCount() + " , Points: " + playerScore.getIndividualMobKilledPoints(key), path));
            }
            OLP.logToFile("", path);
            OLP.logToFile("Blocks Mined Breakdown", path);
            if (!playerScore.getTypeBlocksMined().isEmpty()) {
                playerScore.getTypeBlocksMined().keySet().forEach(key -> OLP.logToFile(key.name() + " Mined: " + playerScore.getTypeBlocksMined().get(key).getTotalCount() + " , Points: " + playerScore.getIndividualBlocksMinedPoints(key), path));
            }
            OLP.logToFile("", path);
            OLP.logToFile("Blocks Placed Breakdown", path);
            if (!playerScore.getTypeBlocksPlaced().isEmpty()) {
                playerScore.getTypeBlocksPlaced().keySet().forEach(key -> OLP.logToFile(key.name() + " Placed: " + playerScore.getTypeBlocksPlaced().get(key).getTotalCount() + " , Points: " + playerScore.getIndividualBlocksPlacedPoints(key), path));
            }
            OLP.logToFile("", path);
            OLP.logToFile("Items Harvested Breakdown", path);
            if (!playerScore.getTypeItemsHarvested().isEmpty()) {
                playerScore.getTypeItemsHarvested().keySet().forEach(key -> OLP.logToFile(key + " Havested: " + playerScore.getTypeItemsHarvested().get(key).getTotalCount() + " , Points: " + playerScore.getIndividualItemsHarvestedPoints(key), path));
            }
            OLP.logToFile("", path);
            OLP.logToFile("------- END -----", path);
            OLP.logToFile("", path);
        }
    }

    public void showPlayerScore(@NotNull CommandSourceStack stack, @NotNull PlayerScore playerScore) {
        String Team = "N/A";
        if (playerScore.getTeam() != null)
            Team = playerScore.getTeam().getTeamName();
        stack.getSender().sendMessage(playerScore.getPlayerName() + " - Team: " + Team + ":");
        stack.getSender().sendMessage("Total Points: " + playerScore.totalPoints());
        stack.getSender().sendMessage("--- Category Totals ---");
        stack.getSender().sendMessage("Deaths: " + playerScore.getDeaths().get(GameMode.SURVIVAL.name()) + ", Adventure Deaths: " + playerScore.getDeaths().get(GameMode.ADVENTURE.name()));
        stack.getSender().sendMessage("Death Points: " + playerScore.getDeathPoints());
        stack.getSender().sendMessage("AFK Penalty: " + playerScore.getAFKPointsOffset());
        stack.getSender().sendMessage("Xp: " + playerScore.getXp() + ", Points: " + playerScore.getXpPoints());
        stack.getSender().sendMessage("OnlineHr: " + playerScore.onlineHr() + ", Points: " + playerScore.getOnlineHrPoints());
        stack.getSender().sendMessage("SurvivalHr: " + playerScore.getOnlineHr().get("SURVIVIAL") + ", AdventureHr: " + playerScore.getOnlineHr().get("ADVENTURE") + ", AfkHr: " + playerScore.getOnlineHr().get("AFK"));
        stack.getSender().sendMessage("Total MobConfig Kill Points: " + playerScore.getTypeMobTotalPoints());
        stack.getSender().sendMessage("Blocks Mined: " + playerScore.getBlocksMined() + ", Points: " + playerScore.getDefaultBlocksMinedPoints());
        stack.getSender().sendMessage("Blocks Placed: " + playerScore.getBlocksPlaced() + ", Points: " + playerScore.getDefaultBlocksPlacedPoints());
        stack.getSender().sendMessage("Items Harvested: " + playerScore.getHarvested() + ", Points: " + playerScore.getDefaultHarvestedPoints());
        stack.getSender().sendMessage("Items Caught: " + playerScore.getCaught() + ", Points: " + playerScore.getDefaultCaughtPoints());
        stack.getSender().sendMessage("Achievements: " + playerScore.getAchievements() + ", Points: " + playerScore.getDefaultAchievementPoints());
        stack.getSender().sendMessage("Use /OneLife Scores All for a Detailed Breakdown");
        stack.getSender().sendMessage("------- END -----");
    }

    public boolean playerAFK(@NotNull OfflinePlayer player) {
        User user = OLP.lpAPI.getUserManager().getUser(player.getUniqueId());
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
